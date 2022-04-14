package io.github.dellisd.kgtfs.db

import app.cash.sqldelight.ColumnAdapter
import java.time.Instant

internal object InstantColumnAdapter : ColumnAdapter<Instant, Long> {
    override fun decode(databaseValue: Long): Instant = Instant.ofEpochSecond(databaseValue)

    override fun encode(value: Instant): Long = value.epochSecond
}
