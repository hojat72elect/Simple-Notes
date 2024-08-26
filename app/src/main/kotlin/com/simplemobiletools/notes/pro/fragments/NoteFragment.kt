package com.simplemobiletools.notes.pro.fragments

import android.os.Build
import android.util.TypedValue
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import com.simplemobiletools.notes.pro.new_architecture.shared.activities.MainActivity
import com.simplemobiletools.notes.pro.new_architecture.shared.extensions.applyColorFilter
import com.simplemobiletools.notes.pro.new_architecture.shared.extensions.beVisibleIf
import com.simplemobiletools.notes.pro.new_architecture.shared.extensions.config
import com.simplemobiletools.notes.pro.new_architecture.shared.extensions.getPercentageFontSize
import com.simplemobiletools.notes.pro.new_architecture.shared.extensions.getProperPrimaryColor
import com.simplemobiletools.notes.pro.new_architecture.shared.extensions.getProperTextColor
import com.simplemobiletools.notes.pro.new_architecture.shared.extensions.performSecurityCheck
import com.simplemobiletools.notes.pro.new_architecture.shared.extensions.underlineText
import com.simplemobiletools.notes.pro.new_architecture.shared.helpers.NotesHelper
import com.simplemobiletools.notes.pro.new_architecture.shared.helpers.PROTECTION_NONE
import com.simplemobiletools.notes.pro.new_architecture.shared.data.models.Note

@RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
abstract class NoteFragment : Fragment() {
    protected var note: Note? = null
    var shouldShowLockedContent = false

    protected fun setupLockedViews(binding: CommonNoteBinding, note: Note) {
        binding.apply {
            noteLockedLayout.beVisibleIf(note.isLocked() && !shouldShowLockedContent)
            noteLockedImage.applyColorFilter(requireContext().getProperTextColor())

            noteLockedLabel.setTextColor(requireContext().getProperTextColor())
            noteLockedLabel.setTextSize(
                TypedValue.COMPLEX_UNIT_PX,
                binding.root.context.getPercentageFontSize()
            )

            noteLockedShow.underlineText()
            noteLockedShow.setTextColor(requireContext().getProperPrimaryColor())
            noteLockedShow.setTextSize(
                TypedValue.COMPLEX_UNIT_PX,
                binding.root.context.getPercentageFontSize()
            )
            noteLockedShow.setOnClickListener {
                handleUnlocking()
            }
        }
    }


    protected fun saveNoteValue(note: Note, content: String?) {
        if (note.path.isEmpty()) {
            NotesHelper(requireActivity()).insertOrUpdateNote(note) {
                (activity as? MainActivity)?.noteSavedSuccessfully(note.title)
            }
        } else {
            if (content != null) {
                val displaySuccess = activity?.config?.displaySuccess ?: false
                (activity as? MainActivity)?.tryExportNoteValueToFile(
                    note.path,
                    note.title,
                    content,
                    displaySuccess
                )
            }
        }
    }

    fun handleUnlocking(callback: (() -> Unit)? = null) {
        if (callback != null && (note!!.protectionType == PROTECTION_NONE || shouldShowLockedContent)) {
            callback()
            return
        }

        activity?.performSecurityCheck(
            protectionType = note!!.protectionType,
            requiredHash = note!!.protectionHash,
            successCallback = { _, _ ->
                shouldShowLockedContent = true
                checkLockState()
                callback?.invoke()
            }
        )
    }

    fun updateNoteValue(value: String) {
        note?.value = value
    }

    fun updateNotePath(path: String) {
        note?.path = path
    }

    abstract fun checkLockState()

    interface CommonNoteBinding {
        val root: View
        val noteLockedLayout: View
        val noteLockedImage: ImageView
        val noteLockedLabel: TextView
        val noteLockedShow: TextView
    }
}
