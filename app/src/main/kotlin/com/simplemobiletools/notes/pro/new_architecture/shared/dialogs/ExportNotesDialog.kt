package com.simplemobiletools.notes.pro.new_architecture.shared.dialogs

import androidx.appcompat.app.AlertDialog
import com.simplemobiletools.notes.pro.R
import com.simplemobiletools.notes.pro.new_architecture.shared.activities.BaseActivity
import com.simplemobiletools.notes.pro.databinding.DialogExportNotesBinding
import com.simplemobiletools.notes.pro.new_architecture.shared.extensions.getAlertDialogBuilder
import com.simplemobiletools.notes.pro.new_architecture.shared.extensions.getCurrentFormattedDateTime
import com.simplemobiletools.notes.pro.new_architecture.shared.extensions.isAValidFilename
import com.simplemobiletools.notes.pro.new_architecture.shared.extensions.setupDialogStuff
import com.simplemobiletools.notes.pro.new_architecture.shared.extensions.toast
import com.simplemobiletools.notes.pro.new_architecture.shared.extensions.value

class ExportNotesDialog(val activity: BaseActivity, callback: (filename: String) -> Unit) {

    init {
        val binding = DialogExportNotesBinding.inflate(activity.layoutInflater).apply {
            exportNotesFilename.setText(
                buildString {
                    append(root.context.getString(R.string.notes))
                    append("_")
                    append(getCurrentFormattedDateTime())
                }
            )
        }

        activity.getAlertDialogBuilder()
            .setPositiveButton(R.string.ok, null)
            .setNegativeButton(R.string.cancel, null).apply {
                activity.setupDialogStuff(
                    binding.root,
                    this,
                    R.string.export_notes
                ) { alertDialog ->
                    alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {

                        val filename = binding.exportNotesFilename.value
                        when {
                            filename.isEmpty() -> activity.toast(R.string.empty_name)
                            filename.isAValidFilename() -> {
                                callback(filename)
                                alertDialog.dismiss()
                            }

                            else -> activity.toast(R.string.invalid_name)
                        }
                    }
                }
            }
    }
}

