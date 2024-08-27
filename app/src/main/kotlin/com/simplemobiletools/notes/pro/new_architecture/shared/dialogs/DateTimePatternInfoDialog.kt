package com.simplemobiletools.notes.pro.new_architecture.shared.dialogs

import android.annotation.SuppressLint
import com.simplemobiletools.notes.pro.R
import com.simplemobiletools.notes.pro.new_architecture.shared.activities.BaseActivity
import com.simplemobiletools.notes.pro.new_architecture.shared.extensions.getAlertDialogBuilder
import com.simplemobiletools.notes.pro.new_architecture.shared.extensions.setupDialogStuff

@SuppressLint("InflateParams")
class DateTimePatternInfoDialog(activity: BaseActivity) {

    init {
        val view = activity.layoutInflater.inflate(R.layout.datetime_pattern_info_layout, null)
        activity.getAlertDialogBuilder()
            .setPositiveButton(R.string.ok) { _, _ -> run { } }
            .apply {
                activity.setupDialogStuff(view, this)
            }
    }
}
