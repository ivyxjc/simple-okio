package ivyio

import org.apache.commons.io.IOUtils
import org.junit.Assert
import org.junit.Test
import java.io.File
import java.nio.charset.Charset

class IvyioTest {

    @Test
    fun testReadFromInputStream() {
        val input = IvyioTest::class.java.classLoader.getResourceAsStream("abc.txt")
        Assert.assertNotNull(input)
        val expected =
            IOUtils.toString(IvyioTest::class.java.classLoader.getResourceAsStream("abc.txt"), Charsets.UTF_8)
        Assert.assertNotNull(expected)
        val source = input.source()
        val bufferSource = source.buffer()
        val res = bufferSource.readString(Charset.defaultCharset())
        Assert.assertEquals(expected, res)
    }

    @Test
    fun testReadFromFile() {
        val file = File("src\\test\\resources\\abc.txt")
        Assert.assertNotNull(file)
        val expected =
            IOUtils.toString(IvyioTest::class.java.classLoader.getResourceAsStream("abc.txt"), Charsets.UTF_8)
        Assert.assertNotNull(expected)
        val source = file.source()
        val bufferSource = source.buffer()
        val res = bufferSource.readString(Charsets.UTF_8)
        println(res)
        Assert.assertEquals(expected, res)
    }

    @Test
    fun testWriteToFile() {
        val file = File("src\\test\\resources\\test-outputs\\output.txt")
        val sink = file.sink()
        val bufferedSink = sink.buffer()
        val res = bufferedSink.writeString("Hello world", Charsets.UTF_8)
        bufferedSink.flush()
    }

    @Test
    fun testWriteToFile2() {
        val file = File("src\\test\\resources\\test-outputs\\output.txt")
        Assert.assertNotNull(file)
        val sink = file.sink()
        val bufferedSink = sink.buffer()
        val res = bufferedSink.writeString("Hello world,你好世界\n".repeat(5000), Charsets.UTF_8)
        bufferedSink.flush()
    }
}