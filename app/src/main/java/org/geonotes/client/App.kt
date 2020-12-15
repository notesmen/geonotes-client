package org.geonotes.client

import android.app.Application
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class App : Application() {
    val applicationScope = CoroutineScope(SupervisorJob())
}
