package com.simplemobiletools.notes.pro.activities

import android.os.Build
import androidx.annotation.RequiresApi
import com.simplemobiletools.notes.pro.R

@RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
open class SimpleActivity : BaseSimpleActivity() {
    override fun getAppIconIDs() = arrayListOf(
        R.mipmap.ic_launcher,

    )

    override fun getAppLauncherName() = getString(R.string.app_launcher_name)
}
