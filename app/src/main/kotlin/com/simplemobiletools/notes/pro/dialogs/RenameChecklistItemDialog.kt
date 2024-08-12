package com.simplemobiletools.notes.pro.dialogs

import android.app.Activity
import android.content.DialogInterface.BUTTON_POSITIVE
import com.simplemobiletools.notes.pro.databinding.DialogRenameChecklistItemBinding
import com.simplemobiletools.notes.pro.extensions.getAlertDialogBuilder
import com.simplemobiletools.notes.pro.extensions.setupDialogStuff
import com.simplemobiletools.notes.pro.extensions.showKeyboard
import com.simplemobiletools.notes.pro.extensions.toast
import com.simplemobiletools.notes.pro.extensions.value

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
            .setPositiveButton(com.simplemobiletools.commons.R.string.ok, null)
            .setNegativeButton(com.simplemobiletools.commons.R.string.cancel, null)
            .apply {
                activity.setupDialogStuff(binding.root, this) { alertDialog ->
                    alertDialog.showKeyboard(binding.checklistItemTitle)
                    alertDialog.getButton(BUTTON_POSITIVE).setOnClickListener {
                        val newTitle = binding.checklistItemTitle.value
                        when {
                            newTitle.isEmpty() -> activity.toast(com.simplemobiletools.commons.R.string.empty_name)
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
