package ca.derekellis.kgtfs

import ca.derekellis.kgtfs.dsl.gtfs
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

class LoadingTest {
    @Test
    fun `read 2013 successfully`() = runTest {
        gtfs("src/test/resources/2013.zip") {}
    }

    @Test
    fun `read 2021 successfully`() = runTest {
        gtfs("src/test/resources/2021.zip") {}
    }
}