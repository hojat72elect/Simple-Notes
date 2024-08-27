package ca.hojat.messenger.niki.shared.dialogs

import android.app.Activity
import android.text.Html
import android.text.method.LinkMovementMethod
import ca.hojat.messenger.niki.R
import ca.hojat.messenger.niki.databinding.DialogDonateBinding
import ca.hojat.messenger.niki.shared.extensions.applyColorFilter
import ca.hojat.messenger.niki.shared.extensions.getAlertDialogBuilder
import ca.hojat.messenger.niki.shared.extensions.getProperTextColor
import ca.hojat.messenger.niki.shared.extensions.launchViewIntent
import ca.hojat.messenger.niki.shared.extensions.setupDialogStuff

class DonateDialog(val activity: Activity) {
    init {
        val view = DialogDonateBinding.inflate(activity.layoutInflater, null, false).apply {
            dialogDonateImage.applyColorFilter(activity.getProperTextColor())
            dialogDonateText.text = Html.fromHtml(activity.getString(R.string.donate_short))
            dialogDonateText.movementMethod = LinkMovementMethod.getInstance()
            dialogDonateImage.setOnClickListener {
                activity.launchViewIntent(R.string.thank_you_url)
            }
        }

        activity.getAlertDialogBuilder()
            .setPositiveButton(R.string.purchase) { _, _ -> activity.launchViewIntent(R.string.thank_you_url) }
            .setNegativeButton(R.string.later, null)
            .apply {
                activity.setupDialogStuff(view.root, this, cancelOnTouchOutside = false)
            }
    }
}




