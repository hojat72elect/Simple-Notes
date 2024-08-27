package com.simplemobiletools.notes.pro.new_architecture.shared.dialogs

import androidx.appcompat.app.AlertDialog
import com.simplemobiletools.notes.pro.R
import com.simplemobiletools.notes.pro.new_architecture.shared.activities.BaseActivity
import com.simplemobiletools.notes.pro.databinding.DialogDeleteNoteBinding
import com.simplemobiletools.notes.pro.new_architecture.shared.extensions.beVisible
import com.simplemobiletools.notes.pro.new_architecture.shared.extensions.getAlertDialogBuilder
import com.simplemobiletools.notes.pro.new_architecture.shared.extensions.setupDialogStuff
import com.simplemobiletools.notes.pro.new_architecture.shared.data.models.Note

class DeleteNoteDialog(
    val activity: BaseActivity,
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
