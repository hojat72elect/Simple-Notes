package com.simplemobiletools.notes.pro

import android.app.Application
import com.simplemobiletools.notes.pro.extensions.baseConfig
import com.simplemobiletools.notes.pro.helpers.isNougatPlus
import java.util.Locale

/**
 * The main entry to our application.
 */
class NotesApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        checkUseEnglish()
    }

    private fun checkUseEnglish(){
        if (baseConfig.useEnglish && !isNougatPlus()) {
            val conf = resources.configuration
            conf.locale = Locale.ENGLISH
            resources.updateConfiguration(conf, resources.displayMetrics)
        }
    }
}
