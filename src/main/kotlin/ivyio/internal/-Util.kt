@file:JvmName("-Util")

package ivyio

@Suppress("NOTHING_TO_INLINE") // Syntactic sugar.
internal inline fun minOf(a: Long, b: Int): Long = minOf(a, b.toLong())

