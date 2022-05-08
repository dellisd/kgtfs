package io.github.dellisd.raptor.db

import app.cash.sqldelight.ExecutableQuery

internal fun <T : Any> ExecutableQuery<T>.executeAsSet(): Set<T> = execute { cursor ->
    val result = mutableSetOf<T>()
    while (cursor.next()) result.add(mapper(cursor))
    result
}
