package kim.present.wdpe.unicodefontloader

import dev.waterdog.waterdogpe.packs.types.ZipResourcePack
import dev.waterdog.waterdogpe.plugin.Plugin
import java.io.File

class UnicodeFontLoader : Plugin() {

    private lateinit var cacheDir: File

    override fun onEnable() {
        val fontsDir = proxy.dataPath.toFile().resolve("packs/fonts")
        cacheDir = dataFolder.resolve(".cache")

        // Save default font glyphs
        if (!fontsDir.exists()) {
            fontsDir.mkdirs()

            listOf("glyph_E0.png", "glyph_E1.png").forEach { resourcePath ->
                getResourceFile(resourcePath).copyTo(fontsDir.resolve(resourcePath).outputStream())
            }
            logger.info("Saved default font glyphs to : $fontsDir")
        }

        // Separate the all glyph group images into 256 pieces (16x16)
        GlyphTool.separateAll(fontsDir).forEach { file ->
            file.delete()
            logger.info("Separated glyph group image : $file")
        }

        // Merge the all glyph pieces into a glyph group image
        val glyphGroups = GlyphTool.mergeAll(fontsDir, cacheDir)
        logger.info("Load unicode font glyphs from : $fontsDir")

        // Build the addon file with the all glyph group images
        val addonFile = GlyphTool.buildAddonWith(cacheDir, glyphGroups)
        logger.info("Built unicode font addon : $addonFile")

        // Register the addon
        val pack = ZipResourcePack(addonFile.toPath())
        pack.loadManifest()
        pack.contentKey = ""

        proxy.packManager.registerPack(pack)
        logger.info("Registered unicode font addon : ${pack.packName} (UUID: ${pack.packId}")
    }
}