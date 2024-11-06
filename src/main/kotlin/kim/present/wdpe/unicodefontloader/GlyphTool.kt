package kim.present.wdpe.unicodefontloader

import com.sksamuel.scrimage.ImmutableImage
import com.sksamuel.scrimage.nio.ImmutableImageLoader
import com.sksamuel.scrimage.nio.PngWriter
import java.awt.image.BufferedImage
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import java.security.MessageDigest
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream
import kotlin.math.max

object GlyphTool {
    @JvmStatic
    val loader: ImmutableImageLoader = ImmutableImage.loader().withJavaxImageReaders()

    @JvmStatic
    fun readGlyphFile(file: File): ImmutableImage {
        require(file.isFile) { "The file must be a file, given $file" }

        return loader.fromFile(file)
    }

    @JvmStatic
    fun writeGlyphFile(file: File, glyphGroup: ImmutableImage) {
        require(!file.exists() || file.isFile) { "The file must be a file, given $file" }

        glyphGroup.output(PngWriter.MaxCompression, file)
    }

    @JvmStatic
    fun writeGlyphFile(stream: OutputStream, glyphGroup: ImmutableImage) {
        glyphGroup.forWriter(PngWriter.MaxCompression).write(stream)
    }

    @JvmStatic
    fun readGlyphPieces(dir: File): Map<PieceKey, ImmutableImage> {
        require(dir.isDirectory) { "The directory must be a directory, given $dir" }

        return dir.listFiles()!!
            .filter { it.isFile && it.name.matches(Regex("^[0-9A-F]{1,2}\\.png$", RegexOption.IGNORE_CASE)) }
            .associate { file ->
                val x = file.name[0].toString().toInt(16)
                val y = file.name[1].toString().toInt(16)

                PieceKey(x, y) to loader.fromFile(file)
            }
    }

    @JvmStatic
    fun writeGlyphPieces(dir: File, glyphPieces: Map<PieceKey, ImmutableImage>) {
        require(dir.isDirectory) { "The directory must be a directory, given $dir" }

        dir.mkdirs()
        glyphPieces.forEach { (key, piece) -> writeGlyphFile(dir.resolve("${key.hex}.png"), piece) }
    }

    @JvmStatic
    fun separate(glyphFile: File): Map<PieceKey, ImmutableImage> {
        return separate(readGlyphFile(glyphFile))
    }

    @JvmStatic
    fun separate(glyphGroup: ImmutableImage): Map<PieceKey, ImmutableImage> {
        require(glyphGroup.width == glyphGroup.height) {
            "Glyph group image size must be square, given ${glyphGroup.width}x${glyphGroup.height}"
        }

        require(glyphGroup.width % 16 == 0) {
            "Glyph image size must be a multiple of 16, given ${glyphGroup.width}px"
        }

        val pieceSize = glyphGroup.width / 16
        val glyphPieces = mutableMapOf<PieceKey, ImmutableImage>()
        for (x in 0 until 16) {
            for (y in 0 until 16) {
                val piece = glyphGroup.subimage(y * pieceSize, x * pieceSize, pieceSize, pieceSize)

                // Skip empty pieces (alpha < 0x0f)
                if (!piece.pixels().any { it.alpha() > 0x0f }) {
                    continue
                }

                glyphPieces[PieceKey(x, y)] = piece
            }
        }

        return glyphPieces
    }

    @JvmStatic
    fun separateAll(workDir: File): List<File> {
        return workDir.listFiles()!!
            .filter { it.isFile && it.name.matches(Regex("^glyph_[0-9A-F]{2}\\.png$", RegexOption.IGNORE_CASE)) }
            .filter { file ->
                val prefixHex = file.name.substring(6, 8).uppercase()
                try {
                    writeGlyphPieces(workDir.resolve("glyph_$prefixHex"), separate(file))

                    return@filter true
                } catch (_: Exception) {
                }
                return@filter false
            }
    }

    @JvmStatic
    fun merge(glyphPieces: Map<PieceKey, ImmutableImage>, cacheFile: File?): ImmutableImage {
        if (cacheFile == null) {
            return merge(glyphPieces)
        }

        return if (cacheFile.exists()) {
            readGlyphFile(cacheFile)
        } else {
            merge(glyphPieces).apply { writeGlyphFile(cacheFile, this) }
        }
    }

    @JvmStatic
    fun merge(glyphPieces: Map<PieceKey, ImmutableImage>): ImmutableImage {
        val maxLength = maxOf(2, glyphPieces.maxOf { (_, value) -> value.width })

        var glyphGroup = ImmutableImage.create(maxLength * 16, maxLength * 16, BufferedImage.TYPE_INT_ARGB)

        glyphPieces.forEach { (key, piece) ->
            glyphGroup = glyphGroup.overlay(
                piece,
                key.x * maxLength,
                key.y * maxLength + max(0.0, (maxLength.toDouble() - piece.height) / 2).toInt()
            )
        }

        return glyphGroup
    }

    @JvmStatic
    fun mergeAll(workDir: File, cacheDir: File? = null): Map<String, ImmutableImage> {
        return workDir.listFiles()!!
            .filter { it.isDirectory && it.name.matches(Regex("^glyph_[0-9A-F]{2}$", RegexOption.IGNORE_CASE)) }
            .associate { dir ->
                val glyphPieces = readGlyphPieces(dir)
                val md5hash = md5(glyphPieces)

                val prefixHex = dir.name.substring(6, 8).uppercase()
                prefixHex to merge(glyphPieces, cacheDir?.resolve("$md5hash.png"))
            }
    }

    @JvmStatic
    fun buildAddonWith(cacheDir: File, glyphGroups: Map<String, ImmutableImage>): File {
        val md5hash = md5(glyphGroups)
        val addonFile = cacheDir.resolve("$md5hash.zip")
        buildAddon(addonFile, md5hash, glyphGroups)

        return addonFile
    }

    @JvmStatic
    fun buildAddon(file: File, uuid: String, glyphGroups: Map<String, ImmutableImage>) {
        if (file.exists()) return

        ZipOutputStream(FileOutputStream(file)).use { zos ->
            glyphGroups.forEach { (prefixHex, glyphGroup) ->
                zos.putNextEntry(ZipEntry("font/glyph_$prefixHex.png"))
                writeGlyphFile(zos, glyphGroup)
            }

            zos.putNextEntry(ZipEntry("manifest.json"))
            zos.write(
                """
                    {
                        "format_version": 2,
                        "header": {
                            "name": "UnicodeFont",
                            "description": "Unicode font addon built automatically by Unicode Font Loader plug-in",
                            "uuid": "${md5ToUuid(uuid)}",
                            "version": [1, 0, 0],
                            "min_engine_version": [1, 20, 0]
                        },
                        "modules": [
                            {
                                "description": "Unicode font addon built automatically by Unicode Font Loader plug-in",
                                "type": "resources",
                                "uuid": "${md5ToUuid("$uuid-resources".md5())}",
                                "version": [1, 0, 0]
                            }
                        ]
                    }
                    """.trimIndent().toByteArray()
            )
        }
    }

    @JvmStatic
    fun md5ToUuid(md5: String): String {
        require(md5.length == 32) { "Invalid MD5 hash : $md5" }
        return md5.replace(Regex("(.{8})(.{4})(.{4})(.{4})(.{12})"), "$1-$2-$3-$4-$5")
    }

    @JvmStatic
    fun md5(glyph: ImmutableImage): String {
        return glyph.pixels().joinToString { it.argb.toString(16) }.md5()
    }

    @JvmStatic
    fun md5(glyphPieces: Map<*, ImmutableImage>): String {
        return md5(glyphPieces.values)
    }

    @JvmStatic
    fun md5(glyphPieces: Collection<ImmutableImage>): String {
        return glyphPieces.joinToString { md5(it) }.md5()
    }

    data class PieceKey(val x: Int, val y: Int) {
        val hex: String = String.format("%02X", y * 16 + x)
    }
}

fun String.md5(): String {
    return MessageDigest.getInstance("MD5").digest(this.toByteArray()).joinToString("") { "%02x".format(it) }
}