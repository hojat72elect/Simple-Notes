package ca.hojat.messenger.niki.shared.dialogs

import android.app.Activity
import androidx.appcompat.app.AlertDialog
import ca.hojat.messenger.niki.R
import ca.hojat.messenger.niki.databinding.DialogMessageBinding
import ca.hojat.messenger.niki.shared.extensions.getAlertDialogBuilder
import ca.hojat.messenger.niki.shared.extensions.setupDialogStuff

class PermissionRequiredDialog(
    val activity: Activity,
    textId: Int,
    private val positiveActionCallback: () -> Unit,
    private val negativeActionCallback: (() -> Unit)? = null
) {
    private var dialog: AlertDialog? = null

    init {
        val view = DialogMessageBinding.inflate(activity.layoutInflater, null, false)
        view.message.text = activity.getString(textId)

        activity.getAlertDialogBuilder()
            .setPositiveButton(R.string.grant_permission) { _, _ -> positiveActionCallback() }
            .setNegativeButton(R.string.cancel) { _, _ -> negativeActionCallback?.invoke() }.apply {
                val title = activity.getString(R.string.permission_required)
                activity.setupDialogStuff(view.root, this, titleText = title) { alertDialog ->
                    dialog = alertDialog
                }
            }
    }
}


