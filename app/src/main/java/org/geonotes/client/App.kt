package org.geonotes.client

import android.app.Application
import org.geonotes.client.helpers.NoteManager

/**
 * Creates NoteManager instance
 */
@Suppress("Unused")
class App : Application() {
    override fun onCreate() {
        super.onCreate()
        NoteManager.getInstance(this)
    }
}