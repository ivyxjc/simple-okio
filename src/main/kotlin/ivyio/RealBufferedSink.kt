package ivyio

import java.nio.charset.Charset

class RealBufferedSink(
    private val sink: Sink
) : BufferedSink {

    val bufferField = Buffer()
    var closed: Boolean = false

    @Suppress("OVERRIDE_BY_INLINE") // Prevent internal code from calling the getter.
    val buffer: Buffer
        inline get() = bufferField

    fun buffer() = bufferField


    override fun writeString(data: String, charset: Charset): BufferedSink {
        return writeString(data, 0, data.length, charset)
    }

    override fun writeString(data: String, beginIndex: Int, endIndex: Int, charset: Charset): BufferedSink {
        buffer.writeString(data, charset)
        return emitCompleteSegments()
    }

    override fun write(data: ByteArray, offset: Int, bytesCount: Int): BufferedSink {
        buffer.write(data, offset, bytesCount)
        return emitCompleteSegments()
    }

    override fun emitCompleteSegments(): BufferedSink {
        check(!closed) { "closed" }
        val bytesCount = buffer.completeSegmentByteCount()
        if (bytesCount >= 0) {
            sink.write(buffer, bytesCount)
        }
        return this
    }

    override fun write(source: Buffer, bytesCount: Long) {
        TODO("not implemented")
    }

    override fun flush() {
        check(!closed) { "closed" }
        if (buffer.size > 0L) {
            sink.write(buffer, buffer.size)
        }
        sink.flush()
    }

    override fun timeout(): Timeout {
        TODO("not implemented")
    }

    override fun close() {
        if (closed) return

        // Emit buffered data to the underlying sink. If this fails, we still need
        // to close the sink; otherwise we risk leaking resources.
        var thrown: Throwable? = null
        try {
            if (buffer.size > 0) {
                sink.write(buffer, buffer.size)
            }
        } catch (e: Throwable) {
            thrown = e
        }

        try {
            sink.close()
        } catch (e: Throwable) {
            if (thrown == null) thrown = e
        }

        closed = true

        if (thrown != null) throw thrown
    }
}