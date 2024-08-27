package ca.hojat.messenger.niki.shared.dialogs

import android.annotation.SuppressLint
import ca.hojat.messenger.niki.R
import ca.hojat.messenger.niki.shared.activities.BaseActivity
import ca.hojat.messenger.niki.shared.extensions.getAlertDialogBuilder
import ca.hojat.messenger.niki.shared.extensions.setupDialogStuff

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
