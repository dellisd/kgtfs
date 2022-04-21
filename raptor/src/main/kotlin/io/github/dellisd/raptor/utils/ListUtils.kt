package io.github.dellisd.raptor.utils

inline fun <T> List<T>.takeLastWhileInclusive(predicate: (T) -> Boolean): List<T> {
    val index = indexOfFirst { !predicate(it) }
    return subList(index, size)
}
