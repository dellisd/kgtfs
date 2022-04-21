package io.github.dellisd.raptor.db

import app.cash.sqldelight.ExecutableQuery

fun <T : Any> ExecutableQuery<T>.executeAsSet() = execute { cursor ->
    val result = mutableSetOf<T>()
    while (cursor.next()) result.add(mapper(cursor))
    result
}
