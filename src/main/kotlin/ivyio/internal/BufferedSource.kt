package ivyio.internal

import ivyio.Source
import java.nio.charset.Charset


interface BufferedSource : Source {
    fun readString(charset: Charset): String
    fun readString(bytesCount: Long, charset: Charset): String
    fun readByteArray(bytesCount: Long): ByteArray
    fun read(sink: ByteArray, offset: Int, bytesCount: Long): Int
}
