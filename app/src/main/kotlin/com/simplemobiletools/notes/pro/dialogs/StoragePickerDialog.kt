package com.simplemobiletools.notes.pro.dialogs

import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.RadioGroup
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import com.simplemobiletools.notes.pro.R
import com.simplemobiletools.notes.pro.activities.BaseSimpleActivity
import com.simplemobiletools.notes.pro.databinding.DialogRadioGroupBinding
import com.simplemobiletools.notes.pro.databinding.RadioButtonBinding
import com.simplemobiletools.notes.pro.extensions.getAlertDialogBuilder
import com.simplemobiletools.notes.pro.extensions.getBasePath
import com.simplemobiletools.notes.pro.extensions.hasExternalSDCard
import com.simplemobiletools.notes.pro.extensions.hasOTGConnected
import com.simplemobiletools.notes.pro.extensions.internalStoragePath
import com.simplemobiletools.notes.pro.extensions.otgPath
import com.simplemobiletools.notes.pro.extensions.sdCardPath
import com.simplemobiletools.notes.pro.extensions.setupDialogStuff

/**
 * A dialog for choosing between internal, root, SD card (optional) storage
 *
 * @param activity has to be activity to avoid some Theme.AppCompat issues
 * @param currPath current path to decide which storage should be preselected
 * @param pickSingleOption if only one option like "Internal" is available, select it automatically
 * @param callback an anonymous function
 *
 */
@RequiresApi(Build.VERSION_CODES.O)
class StoragePickerDialog(
    val activity: BaseSimpleActivity,
    private val currPath: String,
    private val showRoot: Boolean,
    pickSingleOption: Boolean,
    val callback: (pickedPath: String) -> Unit
) {


    private lateinit var radioGroup: RadioGroup
    private var dialog: AlertDialog? = null
    private var defaultSelectedId = 0
    private val availableStorages = ArrayList<String>()

    init {
        availableStorages.add(activity.internalStoragePath)
        when {
            activity.hasExternalSDCard() -> availableStorages.add(activity.sdCardPath)
            activity.hasOTGConnected() -> availableStorages.add("otg")
            showRoot -> availableStorages.add("root")
        }

        if (pickSingleOption && availableStorages.size == 1) {
            callback(availableStorages.first())
        } else {
            initDialog()
        }
    }

    private fun initDialog() {
        val inflater = LayoutInflater.from(activity)
        val resources = activity.resources
        val layoutParams = RadioGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        val view = DialogRadioGroupBinding.inflate(inflater, null, false)
        radioGroup = view.dialogRadioGroup
        val basePath = currPath.getBasePath(activity)

        val internalButton = RadioButtonBinding.inflate(inflater, null, false).root
        internalButton.apply {
            id = ID_INTERNAL
            text = resources.getString(R.string.internal)
            isChecked = basePath == context.internalStoragePath
            setOnClickListener { internalPicked() }
            if (isChecked) {
                defaultSelectedId = id
            }
        }
        radioGroup.addView(internalButton, layoutParams)

        if (activity.hasExternalSDCard()) {
            val sdButton = RadioButtonBinding.inflate(inflater, null, false).root
            sdButton.apply {
                id = ID_SD
                text = resources.getString(R.string.sd_card)
                isChecked = basePath == context.sdCardPath
                setOnClickListener { sdPicked() }
                if (isChecked) {
                    defaultSelectedId = id
                }
            }
            radioGroup.addView(sdButton, layoutParams)
        }

        if (activity.hasOTGConnected()) {
            val otgButton = RadioButtonBinding.inflate(inflater, null, false).root
            otgButton.apply {
                id = ID_OTG
                text = resources.getString(R.string.usb)
                isChecked = basePath == context.otgPath
                setOnClickListener { otgPicked() }
                if (isChecked) {
                    defaultSelectedId = id
                }
            }
            radioGroup.addView(otgButton, layoutParams)
        }

        // allow for example excluding the root folder at the gallery
        if (showRoot) {
            val rootButton = RadioButtonBinding.inflate(inflater, null, false).root
            rootButton.apply {
                id = ID_ROOT
                text = resources.getString(R.string.root)
                isChecked = basePath == "/"
                setOnClickListener { rootPicked() }
                if (isChecked) {
                    defaultSelectedId = id
                }
            }
            radioGroup.addView(rootButton, layoutParams)
        }

        activity.getAlertDialogBuilder().apply {
            activity.setupDialogStuff(view.root, this, R.string.select_storage) { alertDialog ->
                dialog = alertDialog
            }
        }
    }

    private fun internalPicked() {
        dialog?.dismiss()
        callback(activity.internalStoragePath)
    }

    private fun sdPicked() {
        dialog?.dismiss()
        callback(activity.sdCardPath)
    }

    private fun otgPicked() {
        activity.handleOTGPermission {
            if (it) {
                callback(activity.otgPath)
                dialog?.dismiss()
            } else {
                radioGroup.check(defaultSelectedId)
            }
        }
    }

    private fun rootPicked() {
        dialog?.dismiss()
        callback("/")
    }

    companion object {
        private const val ID_INTERNAL = 1
        private const val ID_SD = 2
        private const val ID_OTG = 3
        private const val ID_ROOT = 4
    }
}
