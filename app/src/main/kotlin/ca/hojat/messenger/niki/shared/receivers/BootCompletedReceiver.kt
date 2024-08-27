package ca.hojat.messenger.niki.shared.receivers

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import ca.hojat.messenger.niki.shared.helpers.ensureBackgroundThread
import ca.hojat.messenger.niki.shared.extensions.checkAndBackupNotesOnBoot

@SuppressLint("UnsafeProtectedBroadcastReceiver")
class BootCompletedReceiver : BroadcastReceiver() {


    override fun onReceive(context: Context, intent: Intent) {
        ensureBackgroundThread {
            context.apply {
                checkAndBackupNotesOnBoot()
            }
        }
    }
}
