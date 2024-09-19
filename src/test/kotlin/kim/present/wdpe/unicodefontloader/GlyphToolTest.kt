package kim.present.wdpe.unicodefontloader

import org.junit.jupiter.api.Test
import java.io.File

class GlyphToolTest {

    private val fontsDir = File("C:/home/test")
    private val cacheDir = File("C:/home/test/.cache")

    @Test
    fun `Test GlyphTool separate()`() {
        fontsDir.mkdirs()
        cacheDir.mkdirs()
    }

    @Test
    fun `Test GlyphTool merge()`() {

    }

    @Test
    fun `Test GlyphTool readGlyphGroup()`() {
        val group = GlyphTool.readGlyphFile(fontsDir.resolve("glyph_E0.png"))
        println(group)
    }
}