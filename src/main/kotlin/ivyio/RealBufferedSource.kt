package ivyio

import java.nio.charset.Charset

class RealBufferedSource(val source: Source) {

    val bufferField = Buffer()

    val buffer: Buffer
        inline get() = bufferField

    fun readString(charset: Charset): String {
        buffer.writeAll(source)
        return buffer.readString(charset)
    }
}