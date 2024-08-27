package ca.hojat.messenger.niki.shared.dialogs

import androidx.appcompat.app.AlertDialog
import ca.hojat.messenger.niki.R
import ca.hojat.messenger.niki.shared.activities.BaseActivity
import ca.hojat.messenger.niki.databinding.DialogExportNotesBinding
import ca.hojat.messenger.niki.shared.extensions.getAlertDialogBuilder
import ca.hojat.messenger.niki.shared.extensions.getCurrentFormattedDateTime
import ca.hojat.messenger.niki.shared.extensions.isAValidFilename
import ca.hojat.messenger.niki.shared.extensions.setupDialogStuff
import ca.hojat.messenger.niki.shared.extensions.toast
import ca.hojat.messenger.niki.shared.extensions.value

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

