package ca.hojat.messenger.niki.shared.dialogs

import android.app.Activity
import android.content.DialogInterface.BUTTON_POSITIVE
import ca.hojat.messenger.niki.R
import ca.hojat.messenger.niki.databinding.DialogRenameChecklistItemBinding
import ca.hojat.messenger.niki.shared.extensions.getAlertDialogBuilder
import ca.hojat.messenger.niki.shared.extensions.setupDialogStuff
import ca.hojat.messenger.niki.shared.extensions.showKeyboard
import ca.hojat.messenger.niki.shared.extensions.toast
import ca.hojat.messenger.niki.shared.extensions.value

class RenameChecklistItemDialog(
    val activity: Activity,
    private val oldTitle: String,
    callback: (newTitle: String) -> Unit
) {
    init {
        val binding = DialogRenameChecklistItemBinding.inflate(activity.layoutInflater).apply {
            checklistItemTitle.setText(oldTitle)
        }

        activity.getAlertDialogBuilder()
            .setPositiveButton(R.string.ok, null)
            .setNegativeButton(R.string.cancel, null)
            .apply {
                activity.setupDialogStuff(binding.root, this) { alertDialog ->
                    alertDialog.showKeyboard(binding.checklistItemTitle)
                    alertDialog.getButton(BUTTON_POSITIVE).setOnClickListener {
                        val newTitle = binding.checklistItemTitle.value
                        when {
                            newTitle.isEmpty() -> activity.toast(R.string.empty_name)
                            else -> {
                                callback(newTitle)
                                alertDialog.dismiss()
                            }
                        }
                    }
                }
            }
    }
}