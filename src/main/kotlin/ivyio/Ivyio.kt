@file:JvmName("Ivyio")

package ivyio

import java.io.*


fun InputStream.source(): Source = InputStreamSource(this, Timeout())
fun File.source(): Source = InputStreamSource(FileInputStream(this), Timeout())

private class InputStreamSource(
    private val inputStream: InputStream,
    private val timeout: Timeout
) : Source {

    override fun read(sink: Buffer, byteCount: Long): Long {
        if (byteCount == 0L) {
            return 0L
        }
        val tail = sink.writeableSegment(1)
        val maxToCopy = minOf(byteCount, Segment.SIZE - tail.limit).toInt()
        val bytesRead = inputStream.read(tail.data, tail.limit, maxToCopy)
        if (bytesRead == -1) {
            return -1
        }
        tail.limit += bytesRead
        sink.size += bytesRead
        return bytesRead.toLong()
    }

    override fun timeout() = timeout

    override fun close() {
        inputStream.close()
    }

    override fun toString() = "Source($inputStream)"
}

fun OutputStream.sink(): Sink = OutputStreamSink(this, Timeout())
fun File.sink(): Sink = OutputStreamSink(FileOutputStream(this), Timeout())


private class OutputStreamSink(
    private val output: OutputStream,
    private val timeout: Timeout
) : Sink {

    override fun write(source: Buffer, bytesCount: Long) {
        if (bytesCount == 0L) {
            return
        }
        var remaining = bytesCount
        while (remaining > 0) {
            val head = source.head!!
            val toCopy = minOf(bytesCount, head.limit - head.pos).toInt()
            output.write(head.data, head.pos, toCopy)

            head.pos += toCopy
            remaining -= toCopy
            source.size -= toCopy

            if (head.pos == head.limit) {
                source.head = head.pop()
                SegmentPool.recycle(head)
            }
        }
    }

    override fun flush() = output.flush()

    override fun timeout(): Timeout {
        TODO("not implemented")
    }

    override fun close() = output.close()
}
