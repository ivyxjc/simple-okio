package ivyio

import org.junit.Assert
import org.junit.Test
import java.nio.charset.Charset

class IvyioTest {

    @Test
    fun testOne() {
        val input = IvyioTest::class.java.classLoader.getResourceAsStream("abc.txt")
        Assert.assertNotNull(input)
        val source = input.source()
        val bufferSource = source.buffer()
        val res = bufferSource.readString(Charset.defaultCharset())
    }
}