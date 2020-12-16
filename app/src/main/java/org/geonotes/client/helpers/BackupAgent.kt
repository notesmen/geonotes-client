package org.geonotes.client.helpers

import android.app.backup.BackupAgent
import android.app.backup.BackupDataInput
import android.app.backup.BackupDataOutput
import android.os.ParcelFileDescriptor
import android.util.Log

class BackupAgent : BackupAgent() {
    /**
     * Send log message when backup started
     * @see BackupAgent.onBackup
     */
    override fun onBackup(
            oldState: ParcelFileDescriptor?,
            data: BackupDataOutput?,
            newState: ParcelFileDescriptor?
    ) {
        Log.d(javaClass.simpleName, "Backing up!")
    }

    /**
     * Send log message when restore finished
     * @see BackupAgent.onRestore
     */
    override fun onRestore(
            data: BackupDataInput?,
            appVersionCode: Int,
            newState: ParcelFileDescriptor?
    ) {
        Log.d(javaClass.simpleName, "Restoring from backup!")
    }

    /**
     * Updates notes when restore finished.
     * @see BackupAgent.onRestoreFinished
     */
    override fun onRestoreFinished() {
        super.onRestoreFinished()
        NoteManager.getInstance(null).getNotes()
    }
}