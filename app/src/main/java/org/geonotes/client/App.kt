package org.geonotes.client

import android.app.Application
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob


@Suppress("Unused")
class App : Application() {
    val applicationScope = CoroutineScope(SupervisorJob())
}
