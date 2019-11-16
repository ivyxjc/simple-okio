@file:JvmName("Ivyio")

package ivyio

import java.io.File
import java.io.FileInputStream
import java.io.InputStream


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
