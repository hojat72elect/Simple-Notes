package ca.hojat.messenger.niki

import android.app.Application
import ca.hojat.messenger.niki.shared.extensions.baseConfig
import ca.hojat.messenger.niki.shared.helpers.isNougatPlus
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
