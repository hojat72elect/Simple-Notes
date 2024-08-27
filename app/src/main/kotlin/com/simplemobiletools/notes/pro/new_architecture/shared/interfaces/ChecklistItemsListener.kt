package com.simplemobiletools.notes.pro.new_architecture.shared.interfaces

interface ChecklistItemsListener {
    fun refreshItems()

    fun saveChecklist(callback: () -> Unit = {})
}
