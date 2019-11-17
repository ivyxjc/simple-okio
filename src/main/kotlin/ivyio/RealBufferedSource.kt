package ivyio

import java.nio.charset.Charset

class RealBufferedSource(val source: Source) : BufferedSource {

    val bufferField = Buffer()

    val buffer: Buffer
        inline get() = bufferField

    override fun readString(charset: Charset): String {
        buffer.writeAll(source)
        return buffer.readString(charset)
    }

    override fun readString(bytesCount: Long, charset: Charset): String {
        TODO("not implemented")
    }

    override fun readByteArray(bytesCount: Long): ByteArray {
        TODO("not implemented")
    }

    override fun read(sink: ByteArray, offset: Int, bytesCount: Long): Int {
        TODO("not implemented")
    }

    override fun read(sink: Buffer, byteCount: Long): Long {
        TODO("not implemented")
    }

    override fun timeout(): Timeout {
        TODO("not implemented")
    }

    override fun close() {
        TODO("not implemented")
    }
}