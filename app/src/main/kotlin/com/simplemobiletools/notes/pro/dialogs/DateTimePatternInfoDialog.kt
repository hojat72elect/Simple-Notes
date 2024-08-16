package com.simplemobiletools.notes.pro.dialogs

import android.annotation.SuppressLint
import com.simplemobiletools.notes.pro.R
import com.simplemobiletools.notes.pro.activities.BaseSimpleActivity
import com.simplemobiletools.notes.pro.extensions.getAlertDialogBuilder
import com.simplemobiletools.notes.pro.extensions.setupDialogStuff

@SuppressLint("InflateParams")
class DateTimePatternInfoDialog(activity: BaseSimpleActivity) {

    init {
        val view = activity.layoutInflater.inflate(R.layout.datetime_pattern_info_layout, null)
        activity.getAlertDialogBuilder()
            .setPositiveButton(R.string.ok) { _, _ -> run { } }
            .apply {
                activity.setupDialogStuff(view, this)
            }
    }
}
