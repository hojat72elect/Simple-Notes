package com.simplemobiletools.notes.pro

import android.app.Application
import com.simplemobiletools.notes.pro.extensions.checkUseEnglish

/**
 * The main entry to our application.
 */
class NotesApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        checkUseEnglish()
    }
}
