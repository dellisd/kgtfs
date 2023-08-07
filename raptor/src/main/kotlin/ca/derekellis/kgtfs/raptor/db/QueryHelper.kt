package ca.derekellis.kgtfs.raptor.db

import app.cash.sqldelight.ExecutableQuery
import app.cash.sqldelight.db.QueryResult

internal fun <T : Any> ExecutableQuery<T>.executeAsSet(): Set<T> = execute { cursor ->
  val result = mutableSetOf<T>()
  while (cursor.next().value) result.add(mapper(cursor))
  QueryResult.Value(result)
}.value
