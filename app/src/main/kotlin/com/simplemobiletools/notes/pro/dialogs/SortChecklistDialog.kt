package com.simplemobiletools.notes.pro.dialogs

import com.simplemobiletools.notes.pro.R
import com.simplemobiletools.notes.pro.activities.BaseActivity
import com.simplemobiletools.notes.pro.databinding.DialogSortChecklistBinding
import com.simplemobiletools.notes.pro.new_architecture.shared.extensions.beGoneIf
import com.simplemobiletools.notes.pro.new_architecture.shared.extensions.config
import com.simplemobiletools.notes.pro.new_architecture.shared.extensions.getAlertDialogBuilder
import com.simplemobiletools.notes.pro.new_architecture.shared.extensions.setupDialogStuff
import com.simplemobiletools.notes.pro.new_architecture.shared.helpers.SORT_BY_CUSTOM
import com.simplemobiletools.notes.pro.new_architecture.shared.helpers.SORT_BY_DATE_CREATED
import com.simplemobiletools.notes.pro.new_architecture.shared.helpers.SORT_BY_TITLE
import com.simplemobiletools.notes.pro.new_architecture.shared.helpers.SORT_DESCENDING

class SortChecklistDialog(private val activity: BaseActivity, private val callback: () -> Unit) {
    private val binding = DialogSortChecklistBinding.inflate(activity.layoutInflater)
    private val view = binding.root
    private val config = activity.config
    private var currSorting = config.sorting

    init {
        setupSortRadio()
        setupOrderRadio()
        setupMoveUndoneChecklistItems()

        activity.getAlertDialogBuilder()
            .setPositiveButton(R.string.ok) { _, _ -> dialogConfirmed() }
            .setNegativeButton(R.string.cancel, null)
            .apply {
                activity.setupDialogStuff(
                    view,
                    this,
                    R.string.sort_by
                )
            }
    }

    private fun setupSortRadio() {
        val fieldRadio = binding.sortingDialogRadioSorting
        fieldRadio.setOnCheckedChangeListener { _, checkedId ->
            val isCustomSorting = checkedId == binding.sortingDialogRadioCustom.id
            binding.sortingDialogRadioOrder.beGoneIf(isCustomSorting)
            binding.sortingDialogOrderDivider.root.beGoneIf(isCustomSorting)
            binding.moveUndoneChecklistItemsDivider.root.beGoneIf(isCustomSorting)
            binding.settingsMoveUndoneChecklistItemsHolder.beGoneIf(isCustomSorting)
        }

        var fieldBtn = binding.sortingDialogRadioTitle

        if (currSorting and SORT_BY_DATE_CREATED != 0) {
            fieldBtn = binding.sortingDialogRadioDateCreated
        }

        if (currSorting and SORT_BY_CUSTOM != 0) {
            fieldBtn = binding.sortingDialogRadioCustom
        }

        fieldBtn.isChecked = true
    }

    private fun setupOrderRadio() {
        var orderBtn = binding.sortingDialogRadioAscending

        if (currSorting and SORT_DESCENDING != 0) {
            orderBtn = binding.sortingDialogRadioDescending
        }

        orderBtn.isChecked = true
    }

    private fun setupMoveUndoneChecklistItems() {
        binding.settingsMoveUndoneChecklistItems.isChecked = config.moveDoneChecklistItems
        binding.settingsMoveUndoneChecklistItemsHolder.setOnClickListener {
            binding.settingsMoveUndoneChecklistItems.toggle()
        }
    }

    private fun dialogConfirmed() {
        val sortingRadio = binding.sortingDialogRadioSorting
        var sorting = when (sortingRadio.checkedRadioButtonId) {
            R.id.sorting_dialog_radio_date_created -> SORT_BY_DATE_CREATED
            R.id.sorting_dialog_radio_custom -> SORT_BY_CUSTOM
            else -> SORT_BY_TITLE
        }

        if (sortingRadio.checkedRadioButtonId != R.id.sorting_dialog_radio_custom
            && binding.sortingDialogRadioOrder.checkedRadioButtonId == R.id.sorting_dialog_radio_descending
        ) {
            sorting = sorting or SORT_DESCENDING
        }

        if (currSorting != sorting) {
            config.sorting = sorting
        }

        config.moveDoneChecklistItems = binding.settingsMoveUndoneChecklistItems.isChecked
        callback()
    }
}
