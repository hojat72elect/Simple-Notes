package com.simplemobiletools.notes.pro

import android.app.Application
import com.simplemobiletools.notes.pro.extensions.checkUseEnglish

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        checkUseEnglish()
    }
}
