package ca.hojat.messenger.niki.shared.dialogs

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import ca.hojat.messenger.niki.shared.views.AutoStaggeredGridLayoutManager
import ca.hojat.messenger.niki.R
import ca.hojat.messenger.niki.shared.activities.BaseActivity
import ca.hojat.messenger.niki.shared.ui.adapters.OpenNoteAdapter
import ca.hojat.messenger.niki.databinding.DialogOpenNoteBinding
import ca.hojat.messenger.niki.shared.extensions.getAlertDialogBuilder
import ca.hojat.messenger.niki.shared.extensions.setupDialogStuff
import ca.hojat.messenger.niki.shared.helpers.NotesHelper
import ca.hojat.messenger.niki.shared.data.models.Note

@RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
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
