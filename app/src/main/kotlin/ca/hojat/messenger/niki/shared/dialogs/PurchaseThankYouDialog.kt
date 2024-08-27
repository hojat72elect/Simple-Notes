package ca.hojat.messenger.niki.shared.dialogs

import android.app.Activity
import android.text.Html
import android.text.method.LinkMovementMethod
import ca.hojat.messenger.niki.R
import ca.hojat.messenger.niki.databinding.DialogPurchaseThankYouBinding
import ca.hojat.messenger.niki.shared.extensions.baseConfig
import ca.hojat.messenger.niki.shared.extensions.getAlertDialogBuilder
import ca.hojat.messenger.niki.shared.extensions.launchPurchaseThankYouIntent
import ca.hojat.messenger.niki.shared.extensions.removeUnderlines
import ca.hojat.messenger.niki.shared.extensions.setupDialogStuff

class PurchaseThankYouDialog(val activity: Activity) {
    init {
        val view =
            DialogPurchaseThankYouBinding.inflate(activity.layoutInflater, null, false).apply {
                var text = activity.getString(R.string.purchase_thank_you)
                if (activity.baseConfig.appId.removeSuffix(".debug").endsWith(".pro")) {
                    text += "<br><br>${activity.getString(R.string.shared_theme_note)}"
                }

                purchaseThankYou.text = Html.fromHtml(text)
                purchaseThankYou.movementMethod = LinkMovementMethod.getInstance()
                purchaseThankYou.removeUnderlines()
            }

        activity.getAlertDialogBuilder()
            .setPositiveButton(R.string.purchase) { _, _ -> activity.launchPurchaseThankYouIntent() }
            .setNegativeButton(R.string.later, null)
            .apply {
                activity.setupDialogStuff(view.root, this, cancelOnTouchOutside = false)
            }
    }
}




