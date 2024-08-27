package com.simplemobiletools.notes.pro.new_architecture.shared.dialogs

import android.app.Activity
import android.content.DialogInterface.BUTTON_POSITIVE
import com.simplemobiletools.notes.pro.R
import com.simplemobiletools.notes.pro.databinding.DialogRenameChecklistItemBinding
import com.simplemobiletools.notes.pro.new_architecture.shared.extensions.getAlertDialogBuilder
import com.simplemobiletools.notes.pro.new_architecture.shared.extensions.setupDialogStuff
import com.simplemobiletools.notes.pro.new_architecture.shared.extensions.showKeyboard
import com.simplemobiletools.notes.pro.new_architecture.shared.extensions.toast
import com.simplemobiletools.notes.pro.new_architecture.shared.extensions.value

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
