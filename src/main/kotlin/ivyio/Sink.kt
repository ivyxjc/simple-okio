package ivyio

import java.io.Closeable
import java.io.Flushable
import java.io.IOException

interface Sink : Closeable, Flushable {
    @Throws(IOException::class)
    fun write(source: Buffer, bytesCount: Long)

    @Throws(IOException::class)
    override fun flush()

    fun timeout(): Timeout

    @Throws(IOException::class)
    override fun close()
}

fun Sink.buffer() = RealBufferedSink(this)