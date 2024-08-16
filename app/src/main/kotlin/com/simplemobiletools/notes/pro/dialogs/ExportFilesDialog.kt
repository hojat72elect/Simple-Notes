package com.simplemobiletools.notes.pro.dialogs

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import com.simplemobiletools.notes.pro.R
import com.simplemobiletools.notes.pro.activities.SimpleActivity
import com.simplemobiletools.notes.pro.databinding.DialogExportFilesBinding
import com.simplemobiletools.notes.pro.extensions.config
import com.simplemobiletools.notes.pro.extensions.getAlertDialogBuilder
import com.simplemobiletools.notes.pro.extensions.humanizePath
import com.simplemobiletools.notes.pro.extensions.setupDialogStuff
import com.simplemobiletools.notes.pro.extensions.showKeyboard
import com.simplemobiletools.notes.pro.extensions.value
import com.simplemobiletools.notes.pro.models.Note

@RequiresApi(Build.VERSION_CODES.O)
class ExportFilesDialog(
    val activity: SimpleActivity,
    val notes: ArrayList<Note>,
    val callback: (parent: String, extension: String) -> Unit
) {
    init {
        var realPath = activity.config.lastUsedSavePath
        val binding = DialogExportFilesBinding.inflate(activity.layoutInflater).apply {
            folderPath.setText(activity.humanizePath(realPath))

            extension.setText(activity.config.lastUsedExtension)
            folderPath.setOnClickListener {
                FilePickerDialog(activity, realPath, false, false, true, true) {
                    folderPath.setText(activity.humanizePath(it))
                    realPath = it
                }
            }
        }

        activity.getAlertDialogBuilder()
            .setPositiveButton(R.string.ok, null)
            .setNegativeButton(R.string.cancel, null)
            .apply {
                activity.setupDialogStuff(
                    binding.root,
                    this,
                    R.string.export_as_file
                ) { alertDialog ->
                    alertDialog.showKeyboard(binding.extension)
                    alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                        activity.handleSAFDialog(realPath) {
                            val extension = binding.extension.value
                            activity.config.lastUsedExtension = extension
                            activity.config.lastUsedSavePath = realPath
                            callback(realPath, extension)
                            alertDialog.dismiss()
                        }
                    }
                }
            }
    }
}
