package com.simplemobiletools.notes.pro.receivers

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.simplemobiletools.notes.pro.helpers.ensureBackgroundThread
import com.simplemobiletools.notes.pro.extensions.checkAndBackupNotesOnBoot

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
