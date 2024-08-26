package com.simplemobiletools.notes.pro.dialogs

import android.content.DialogInterface
import androidx.appcompat.app.AlertDialog
import com.simplemobiletools.notes.pro.new_architecture.shared.activities.BaseActivity
import com.simplemobiletools.notes.pro.new_architecture.shared.extensions.applyColorFilter
import com.simplemobiletools.notes.pro.new_architecture.shared.extensions.getAlertDialogBuilder
import com.simplemobiletools.notes.pro.new_architecture.shared.extensions.performSecurityCheck
import com.simplemobiletools.notes.pro.new_architecture.shared.extensions.setupDialogStuff
import com.simplemobiletools.notes.pro.new_architecture.shared.extensions.updateTextColors
import com.simplemobiletools.notes.pro.R
import com.simplemobiletools.notes.pro.databinding.DialogUnlockNotesBinding
import com.simplemobiletools.notes.pro.databinding.ItemLockedNoteBinding
import com.simplemobiletools.notes.pro.new_architecture.shared.data.models.Note

class UnlockNotesDialog(
    val activity: BaseActivity,
    val notes: List<Note>,
    callback: (unlockedNotes: List<Note>) -> Unit
) {
    private var dialog: AlertDialog? = null
    private val binding = DialogUnlockNotesBinding.inflate(activity.layoutInflater)
    private val view = binding.root
    private val redColor = activity.getColor(R.color.md_red)
    private val greenColor = activity.getColor(R.color.md_green)
    private val unlockedNoteIds = mutableListOf<Long>()

    init {
        for (note in notes) {
            addLockedNoteView(note)
        }

        activity.getAlertDialogBuilder()
            .setPositiveButton(R.string.skip, null)
            .setNegativeButton(R.string.cancel, null)
            .apply {
                activity.setupDialogStuff(
                    view,
                    this,
                    R.string.unlock_notes,
                    cancelOnTouchOutside = false
                ) { alertDialog ->
                    dialog = alertDialog
                    alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener {
                        callback(unlockedNoteIds.mapNotNull { id -> notes.firstOrNull { it.id == id } })
                        alertDialog.dismiss()
                    }
                }
            }
    }

    private fun addLockedNoteView(note: Note) {
        ItemLockedNoteBinding.inflate(activity.layoutInflater).apply {
            binding.notesHolder.addView(this.root)
            activity.updateTextColors(binding.notesHolder)
            lockedNoteTitle.text = note.title
            lockedUnlockedImage.applyColorFilter(redColor)
            lockedNoteHolder.setOnClickListener {
                if (note.id !in unlockedNoteIds) {
                    activity.performSecurityCheck(
                        protectionType = note.protectionType,
                        requiredHash = note.protectionHash,
                        successCallback = { _, _ ->
                            unlockedNoteIds.add(note.id!!)
                            lockedUnlockedImage.apply {
                                setImageResource(R.drawable.ic_lock_open_vector)
                                applyColorFilter(greenColor)
                            }
                            updatePositiveButton()
                        }
                    )
                } else {
                    unlockedNoteIds.remove(note.id)
                    lockedUnlockedImage.apply {
                        setImageResource(R.drawable.ic_lock_vector)
                        applyColorFilter(redColor)
                    }
                    updatePositiveButton()
                }
            }
        }
    }

    private fun updatePositiveButton() {
        dialog?.getButton(DialogInterface.BUTTON_POSITIVE)?.text =
            if (unlockedNoteIds.isNotEmpty()) {
                activity.getString(R.string.ok)
            } else {
                activity.getString(R.string.skip)
            }
    }
}
