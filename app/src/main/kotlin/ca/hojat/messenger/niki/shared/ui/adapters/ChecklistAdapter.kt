package ca.hojat.messenger.niki.shared.ui.adapters

import android.annotation.SuppressLint
import android.graphics.Paint
import android.graphics.drawable.Drawable
import android.os.Build
import android.util.TypedValue
import android.view.Menu
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import ca.hojat.messenger.niki.R
import ca.hojat.messenger.niki.databinding.ItemChecklistBinding
import ca.hojat.messenger.niki.shared.dialogs.RenameChecklistItemDialog
import ca.hojat.messenger.niki.shared.interfaces.ChecklistItemsListener
import ca.hojat.messenger.niki.shared.interfaces.ItemMoveCallback
import ca.hojat.messenger.niki.shared.interfaces.ItemTouchHelperContract
import ca.hojat.messenger.niki.shared.interfaces.StartReorderDragListener
import ca.hojat.messenger.niki.shared.activities.BaseActivity
import ca.hojat.messenger.niki.shared.data.models.ChecklistItem
import ca.hojat.messenger.niki.shared.extensions.applyColorFilter
import ca.hojat.messenger.niki.shared.extensions.beVisibleIf
import ca.hojat.messenger.niki.shared.extensions.config
import ca.hojat.messenger.niki.shared.extensions.getColoredDrawableWithColor
import ca.hojat.messenger.niki.shared.extensions.getPercentageFontSize
import ca.hojat.messenger.niki.shared.extensions.removeBit
import ca.hojat.messenger.niki.shared.helpers.DONE_CHECKLIST_ITEM_ALPHA
import ca.hojat.messenger.niki.shared.helpers.SORT_BY_CUSTOM
import ca.hojat.messenger.niki.shared.views.MyRecyclerView
import java.util.Collections

@RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
@SuppressLint("ClickableViewAccessibility")
class ChecklistAdapter(
    activity: BaseActivity,
    var items: MutableList<ChecklistItem>,
    val listener: ChecklistItemsListener?,
    recyclerView: MyRecyclerView,
    val showIcons: Boolean,
    itemClick: (Any) -> Unit
) :
    MyRecyclerViewAdapter(activity, recyclerView, itemClick), ItemTouchHelperContract {

    private lateinit var crossDrawable: Drawable
    private lateinit var checkDrawable: Drawable
    private var touchHelper: ItemTouchHelper? = null
    private var startReorderDragListener: StartReorderDragListener

    init {
        setupDragListener(true)
        initDrawables()

        touchHelper = ItemTouchHelper(ItemMoveCallback(this))
        touchHelper!!.attachToRecyclerView(recyclerView)

        startReorderDragListener = object : StartReorderDragListener {
            override fun requestDrag(viewHolder: RecyclerView.ViewHolder) {
                touchHelper?.startDrag(viewHolder)
            }
        }
    }

    override fun getActionMenuId() = R.menu.cab_checklist

    override fun actionItemPressed(id: Int) {
        if (selectedKeys.isEmpty()) {
            return
        }

        when (id) {
            R.id.cab_move_to_top -> moveSelectedItemsToTop()
            R.id.cab_move_to_bottom -> moveSelectedItemsToBottom()
            R.id.cab_rename -> renameChecklistItem()
            R.id.cab_delete -> deleteSelection()
        }
    }

    override fun getSelectableItemCount() = items.size

    override fun getIsItemSelectable(position: Int) = true

    override fun getItemSelectionKey(position: Int) = items.getOrNull(position)?.id

    override fun getItemKeyPosition(key: Int) = items.indexOfFirst { it.id == key }

    override fun onActionModeCreated() {}

    override fun onActionModeDestroyed() {}

    override fun prepareActionMode(menu: Menu) {
        val selectedItems = getSelectedItems()
        if (selectedItems.isEmpty()) {
            return
        }

        menu.findItem(R.id.cab_rename).isVisible = isOneItemSelected()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return createViewHolder(ItemChecklistBinding.inflate(layoutInflater, parent, false).root)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.bindView(item, true, true) { itemView, _ ->
            setupView(itemView, item, holder)
        }
        bindViewHolder(holder)
    }

    override fun getItemCount() = items.size

    private fun initDrawables() {
        val res = activity.resources
        crossDrawable = res.getColoredDrawableWithColor(
            R.drawable.ic_cross_vector,
            res.getColor(R.color.md_red_700)
        )
        checkDrawable = res.getColoredDrawableWithColor(
            R.drawable.ic_check_vector,
            res.getColor(R.color.md_green_700)
        )
    }

    private fun renameChecklistItem() {
        val item = getSelectedItems().first()
        RenameChecklistItemDialog(activity, item.title) {
            val position = getSelectedItemPositions().first()
            item.title = it
            listener?.saveChecklist()
            notifyItemChanged(position)
            finishActMode()
        }
    }

    private fun deleteSelection() {
        val removeItems = ArrayList<ChecklistItem>(selectedKeys.size)
        val positions = ArrayList<Int>()
        selectedKeys.forEach {
            val key = it
            val position = items.indexOfFirst { it.id == key }
            if (position != -1) {
                positions.add(position)

                val favorite = getItemWithKey(key)
                if (favorite != null) {
                    removeItems.add(favorite)
                }
            }
        }

        items.removeAll(removeItems.toSet())
        positions.sortDescending()
        removeSelectedItems(positions)

        listener?.saveChecklist {
            if (items.isEmpty()) {
                listener.refreshItems()
            }
        }
    }

    private fun moveSelectedItemsToTop() {
        activity.config.sorting = SORT_BY_CUSTOM
        val movedPositions = mutableListOf<Int>()
        selectedKeys.reversed().forEach { checklistId ->
            val position = items.indexOfFirst { it.id == checklistId }
            val tempItem = items[position]
            items.removeAt(position)
            movedPositions.add(position)
            items.add(0, tempItem)
        }

        movedPositions.forEach {
            notifyItemMoved(it, 0)
        }
        listener?.saveChecklist()
    }

    private fun moveSelectedItemsToBottom() {
        activity.config.sorting = SORT_BY_CUSTOM
        val movedPositions = mutableListOf<Int>()
        selectedKeys.forEach { checklistId ->
            val position = items.indexOfFirst { it.id == checklistId }
            val tempItem = items[position]
            items.removeAt(position)
            movedPositions.add(position)
            items.add(items.size, tempItem)
        }

        movedPositions.forEach {
            notifyItemMoved(it, items.size - 1)
        }
        listener?.saveChecklist()
    }

    private fun getItemWithKey(key: Int): ChecklistItem? = items.firstOrNull { it.id == key }

    private fun getSelectedItems() =
        items.filter { selectedKeys.contains(it.id) } as ArrayList<ChecklistItem>


    private fun setupView(view: View, checklistItem: ChecklistItem, holder: ViewHolder) {
        val isSelected = selectedKeys.contains(checklistItem.id)
        ItemChecklistBinding.bind(view).apply {
            checklistTitle.apply {
                text = checklistItem.title
                setTextColor(textColor)
                setTextSize(TypedValue.COMPLEX_UNIT_PX, context.getPercentageFontSize())
                gravity = context.config.getTextGravity()

                if (checklistItem.isDone) {
                    paintFlags = paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                    alpha = DONE_CHECKLIST_ITEM_ALPHA
                } else {
                    paintFlags = paintFlags.removeBit(Paint.STRIKE_THRU_TEXT_FLAG)
                    alpha = 1f
                }
            }

            checklistImage.setImageDrawable(if (checklistItem.isDone) checkDrawable else crossDrawable)
            checklistImage.beVisibleIf(showIcons && selectedKeys.isEmpty())
            checklistHolder.isSelected = isSelected

            checklistDragHandle.beVisibleIf(selectedKeys.isNotEmpty())
            checklistDragHandle.applyColorFilter(textColor)
            checklistDragHandle.setOnTouchListener { _, event ->
                if (event.action == MotionEvent.ACTION_DOWN) {
                    startReorderDragListener.requestDrag(holder)
                }
                false
            }
        }
    }

    override fun onRowClear(myViewHolder: ViewHolder?) {
        listener?.saveChecklist()
    }

    override fun onRowMoved(fromPosition: Int, toPosition: Int) {
        activity.config.sorting = SORT_BY_CUSTOM
        if (fromPosition < toPosition) {
            for (i in fromPosition until toPosition) {
                Collections.swap(items, i, i + 1)
            }
        } else {
            for (i in fromPosition downTo toPosition + 1) {
                Collections.swap(items, i, i - 1)
            }
        }
        notifyItemMoved(fromPosition, toPosition)
    }

    override fun onRowSelected(myViewHolder: ViewHolder?) {
    }

}
