package ivyio


internal object SegmentPool {

    const val MAX_SIZE = 1024L

    private var head: Segment? = null

    fun take(): Segment {
        synchronized(this) {
            if (head != null) {
                val res = head
                head = res!!.next
                res.next = null
                return res
            }
        }
        return Segment()
    }

    fun recycle(segment: Segment) {
        require(segment.next == null && segment.prev == null)
        //todo if the segment is shared, do not recycle
        synchronized(this) {
            segment.next = head
            segment.limit = 0
            segment.pos = 0
            head = segment
        }
    }
}

