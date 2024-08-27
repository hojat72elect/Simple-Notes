package com.simplemobiletools.notes.pro.dialogs

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.simplemobiletools.notes.pro.views.AutoStaggeredGridLayoutManager
import com.simplemobiletools.notes.pro.R
import com.simplemobiletools.notes.pro.new_architecture.shared.activities.BaseActivity
import com.simplemobiletools.notes.pro.new_architecture.shared.ui.adapters.OpenNoteAdapter
import com.simplemobiletools.notes.pro.databinding.DialogOpenNoteBinding
import com.simplemobiletools.notes.pro.new_architecture.shared.extensions.getAlertDialogBuilder
import com.simplemobiletools.notes.pro.new_architecture.shared.extensions.setupDialogStuff
import com.simplemobiletools.notes.pro.new_architecture.shared.helpers.NotesHelper
import com.simplemobiletools.notes.pro.new_architecture.shared.data.models.Note

@RequiresApi(Build.VERSION_CODES.O)
class OpenNoteDialog(
    val activity: BaseActivity,
    val callback: (checkedId: Long, newNote: Note?) -> Unit
) {
    private var dialog: AlertDialog? = null

    init {
        val binding = DialogOpenNoteBinding.inflate(activity.layoutInflater)

        val noteItemWidth = activity.resources.getDimensionPixelSize(R.dimen.grid_note_item_width)
        binding.dialogOpenNoteList.layoutManager =
            AutoStaggeredGridLayoutManager(noteItemWidth, StaggeredGridLayoutManager.VERTICAL)

        NotesHelper(activity).getNotes {
            initDialog(it, binding)
        }
    }


    private fun initDialog(notes: List<Note>, binding: DialogOpenNoteBinding) {
        binding.dialogOpenNoteList.adapter =
            OpenNoteAdapter(activity, notes, binding.dialogOpenNoteList) {
                it as Note
                callback(it.id!!, null)
                dialog?.dismiss()
            }

        binding.newNoteFab.setOnClickListener {
            NewNoteDialog(activity, setChecklistAsDefault = false) {
                callback(0, it)
                dialog?.dismiss()
            }
        }

        activity.getAlertDialogBuilder()
            .setNegativeButton(R.string.cancel, null)
            .apply {
                activity.setupDialogStuff(binding.root, this, R.string.open_note) { alertDialog ->
                    dialog = alertDialog
                }
            }
    }
}
