package com.simplemobiletools.notes.pro.adapters

import android.app.Activity
import android.os.Bundle
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.simplemobiletools.commons.extensions.showErrorToast
import com.simplemobiletools.notes.pro.fragments.NoteFragment
import com.simplemobiletools.notes.pro.helpers.NOTE_ID
import com.simplemobiletools.notes.pro.models.Note

class NotesPagerAdapter(fm: FragmentManager, val notes: List<Note>, val activity: Activity) : FragmentStatePagerAdapter(fm) {
    private var fragments: HashMap<Int, NoteFragment> = LinkedHashMap()

    override fun getCount() = notes.size

    override fun getItem(position: Int): NoteFragment {
        val bundle = Bundle()
        val id = notes[position].id
        bundle.putInt(NOTE_ID, id!!.toInt())

        if (fragments.containsKey(position)) {
            return fragments[position]!!
        }

        val fragment = NoteFragment()
        fragment.arguments = bundle
        fragments[position] = fragment
        return fragment
    }

    override fun getPageTitle(position: Int) = notes[position].title

    fun getCurrentNotesView(position: Int) = fragments[position]?.getNotesView()

    fun getCurrentNoteViewText(position: Int) = fragments[position]?.getCurrentNoteViewText()

    fun appendText(position: Int, text: String) = fragments[position]?.getNotesView()?.append(text)

    fun saveCurrentNote(position: Int, force: Boolean) = fragments[position]?.saveText(force)

    fun focusEditText(position: Int) = fragments[position]?.focusEditText()

    fun anyHasUnsavedChanges() = fragments.values.any { it.hasUnsavedChanges() }

    fun saveAllFragmentTexts() = fragments.values.forEach { it.saveText(false) }

    fun undo(position: Int) = fragments[position]?.undo()

    fun redo(position: Int) = fragments[position]?.redo()

    override fun finishUpdate(container: ViewGroup) {
        try {
            super.finishUpdate(container)
        } catch (e: Exception) {
            activity.showErrorToast(e)
        }
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        super.destroyItem(container, position, `object`)
        fragments.remove(position)
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val fragment = super.instantiateItem(container, position) as NoteFragment
        fragments[position] = fragment
        return fragment
    }
}
