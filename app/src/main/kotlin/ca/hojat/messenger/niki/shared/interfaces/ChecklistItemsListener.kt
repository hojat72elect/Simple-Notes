package ca.hojat.messenger.niki.shared.interfaces

interface ChecklistItemsListener {
    fun refreshItems()

    fun saveChecklist(callback: () -> Unit = {})
}
