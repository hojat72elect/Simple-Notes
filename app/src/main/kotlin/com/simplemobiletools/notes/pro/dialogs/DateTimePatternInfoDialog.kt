package com.simplemobiletools.notes.pro.dialogs

import com.simplemobiletools.notes.pro.activities.BaseSimpleActivity
import com.simplemobiletools.notes.pro.extensions.getAlertDialogBuilder
import com.simplemobiletools.notes.pro.extensions.setupDialogStuff
import com.simplemobiletools.notes.pro.R

class DateTimePatternInfoDialog(activity: BaseSimpleActivity) {

    init {
        val view = activity.layoutInflater.inflate(R.layout.datetime_pattern_info_layout, null)
        activity.getAlertDialogBuilder()
            .setPositiveButton(com.simplemobiletools.commons.R.string.ok) { _, _ -> { } }
            .apply {
                activity.setupDialogStuff(view, this)
            }
    }
}
