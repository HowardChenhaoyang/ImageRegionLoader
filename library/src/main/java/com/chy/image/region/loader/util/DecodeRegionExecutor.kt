package com.chy.image.region.loader.util

import java.util.concurrent.Executors
import java.util.concurrent.Future

internal object DecodeRegionExecutor {
    private val executor = Executors.newFixedThreadPool(5)
    fun execute(runnable: Runnable): Future<*> {
        executor.execute {

        }
        return executor.submit {
            runnable.run()
        }
    }
}