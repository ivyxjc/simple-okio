package ivyio

import java.nio.charset.Charset

interface BufferedSink : Sink {
    fun emitCompleteSegments(): BufferedSink

    fun writeString(data: String, charset: Charset): BufferedSink
    fun writeString(data: String, beginIndex: Int, endIndex: Int, charset: Charset): BufferedSink

    fun write(data: ByteArray, offset: Int, bytesCount: Int): BufferedSink
}