package org.geonotes.client

import android.app.Application
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.Job

import dagger.hilt.android.HiltAndroidApp


@HiltAndroidApp
class App : Application() {
    private val job: Job = SupervisorJob()
    val applicationScope = CoroutineScope(job)

    override fun onTerminate() {
        super.onTerminate()
        job.cancel()
    }
}
