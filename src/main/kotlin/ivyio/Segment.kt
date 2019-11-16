package ivyio


class Segment {

    companion object {
        const val SIZE: Int = 8192
    }

    var next: Segment? = null

    var prev: Segment? = null

    var limit: Int = 0

    var pos: Int = 0

    val data: ByteArray


    constructor() {
        this.data = ByteArray(SIZE)
    }


    fun pop(): Segment? {
        val result = if (next !== this) {
            next
        } else null
        prev!!.next = next
        next!!.prev = prev
        next = null
        prev = null
        return result
    }

    fun push(seg: Segment): Segment {
        seg.next = next
        seg.prev = this
        next!!.prev = this
        next = seg
        return seg
    }

}
