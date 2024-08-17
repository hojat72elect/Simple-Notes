package com.simplemobiletools.notes.pro.dialogs

import androidx.appcompat.app.AlertDialog
import com.simplemobiletools.notes.pro.R
import com.simplemobiletools.notes.pro.activities.SimpleActivity
import com.simplemobiletools.notes.pro.databinding.DialogDeleteNoteBinding
import com.simplemobiletools.notes.pro.extensions.beVisible
import com.simplemobiletools.notes.pro.extensions.getAlertDialogBuilder
import com.simplemobiletools.notes.pro.extensions.setupDialogStuff
import com.simplemobiletools.notes.pro.models.Note

class DeleteNoteDialog(
    val activity: SimpleActivity,
    val note: Note,
    val callback: (deleteFile: Boolean) -> Unit
) {
    private var dialog: AlertDialog? = null

    init {
        val message =
            String.format(activity.getString(R.string.delete_note_prompt_message), note.title)
        val binding = DialogDeleteNoteBinding.inflate(activity.layoutInflater).apply {
            if (note.path.isNotEmpty()) {
                deleteNoteCheckbox.text =
                    String.format(activity.getString(R.string.delete_file_itself), note.path)
                deleteNoteCheckboxHolder.beVisible()
                deleteNoteCheckboxHolder.setOnClickListener {
                    deleteNoteCheckbox.toggle()
                }
            }
            deleteNoteDescription.text = message
        }

        activity.getAlertDialogBuilder()
            .setPositiveButton(R.string.delete) { _, _ ->
                dialogConfirmed(
                    binding.deleteNoteCheckbox.isChecked
                )
            }
            .setNegativeButton(R.string.cancel, null)
            .apply {
                activity.setupDialogStuff(binding.root, this)
            }
    }

    private fun dialogConfirmed(deleteFile: Boolean) {
        callback(deleteFile && note.path.isNotEmpty())
        dialog?.dismiss()
    }
}
