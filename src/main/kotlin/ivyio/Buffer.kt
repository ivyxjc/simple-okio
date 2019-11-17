package ivyio

import java.io.EOFException
import java.nio.charset.Charset


class Buffer : BufferedSink, BufferedSource {

    internal var head: Segment? = null
    internal var size: Long = 0

    override fun write(source: Buffer, byteCount: Long) {
        TODO("not implemented")
    }

    override fun flush() {
        TODO("not implemented")
    }

    override fun timeout(): Timeout {
        TODO("not implemented")
    }

    override fun read(sink: Buffer, byteCount: Long): Long {
        TODO("not implemented")
    }

    override fun close() {
        TODO("not implemented")
    }

    override fun readString(charset: Charset): String {
        return readString(size, charset)
    }

    override fun readString(bytesCount: Long, charset: Charset): String {
        if (size < bytesCount) {
            throw EOFException()
        }
        if (bytesCount == 0L) {
            return ""
        }
        val s = head!!

        if (s.pos + bytesCount > s.limit) {
            return String(readByteArray(bytesCount), charset)
        }

        val result = String(s.data, s.pos, bytesCount.toInt(), charset)
        s.pos += bytesCount.toInt()
        size -= bytesCount
        if (s.pos == s.limit) {
            head = s.pop()
            SegmentPool.recycle(s)
        }
        return result
    }

    override fun readByteArray(bytesCount: Long): ByteArray {
        val result = ByteArray(bytesCount.toInt())
        var offset = 0
        while (offset < bytesCount) {
            val read = read(result, offset, bytesCount - offset)
            if (read == -1) {
                throw EOFException()
            }
            offset += read
        }
        return result
    }

    override fun read(sink: ByteArray, offset: Int, bytesCount: Long): Int {
        val s = head!!
        val toCopy = minOf(bytesCount, s.limit - s.pos).toInt()
        s.data.copyInto(sink, offset, s.pos, s.pos + toCopy)
        s.pos += toCopy
        size -= toCopy.toLong()

        if (s.pos == s.limit) {
            head = s.pop()
            SegmentPool.recycle(s)
        }
        return toCopy
    }

    override fun writeString(data: String, charset: Charset): BufferedSink = writeString(data, 0, data.length, charset)

    override fun writeString(string: String, beginIndex: Int, endIndex: Int, charset: Charset): Buffer {
        val data = string.substring(beginIndex, endIndex).toByteArray(charset)
        return write(data, 0, data.size)
    }

    override fun write(data: ByteArray, offset: Int, bytesCount: Int): Buffer {
        var tmpOffset = offset
        val limit = offset + bytesCount
        while (tmpOffset < bytesCount) {
            val tail = writeableSegment(1)
            val toCopy = minOf(limit - tmpOffset, Segment.SIZE - tail.limit)
            data.copyInto(
                destination = tail.data,
                destinationOffset = tail.limit,
                startIndex = tmpOffset,
                endIndex = tmpOffset + toCopy
            )
            tmpOffset += toCopy
            tail.limit += toCopy
        }
        size += bytesCount.toLong()
        return this
    }

    override fun emitCompleteSegments(): BufferedSink = this

    fun completeSegmentByteCount(): Long {
        var res = size
        if (res == 0L) {
            return res
        }

        val tail = head!!.prev!!
        if (tail.limit < Segment.SIZE) {
            res -= (tail.limit - tail.pos).toLong()
        }
        return res
    }

    fun writeAll(source: Source): Long {
        var totalBytesRead = 0L
        while (true) {
            val readCount = source.read(this, Segment.SIZE.toLong())
            if (readCount != -1L) {
                totalBytesRead += readCount
            } else {
                break
            }
        }
        return totalBytesRead
    }

    internal fun writeableSegment(minimumCapacity: Int): Segment {
        require(minimumCapacity >= 1 && minimumCapacity <= Segment.SIZE) { "unexpected capacity" }
        if (head == null) {
            val res = SegmentPool.take()
            head = res
            res.next = head
            res.prev = head
            return res
        }
        var tail = head!!.prev
        if (tail!!.limit + minimumCapacity > Segment.SIZE) {
            tail = tail.push(SegmentPool.take())
        }
        return tail
    }
}