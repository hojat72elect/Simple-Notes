package com.simplemobiletools.notes.pro.dialogs

import android.app.Activity
import androidx.appcompat.app.AlertDialog
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.simplemobiletools.notes.pro.R
import com.simplemobiletools.notes.pro.compose.extensions.MyDevices
import com.simplemobiletools.notes.pro.compose.theme.AppThemeSurface
import com.simplemobiletools.notes.pro.databinding.DialogMessageBinding
import com.simplemobiletools.notes.pro.new_architecture.shared.extensions.getAlertDialogBuilder
import com.simplemobiletools.notes.pro.new_architecture.shared.extensions.setupDialogStuff
import com.simplemobiletools.notes.pro.compose.alert_dialog.AlertDialogState
import com.simplemobiletools.notes.pro.compose.alert_dialog.dialogBorder
import com.simplemobiletools.notes.pro.compose.alert_dialog.dialogContainerColor
import com.simplemobiletools.notes.pro.compose.alert_dialog.dialogElevation
import com.simplemobiletools.notes.pro.compose.alert_dialog.dialogShape
import com.simplemobiletools.notes.pro.compose.alert_dialog.rememberAlertDialogState

class PermissionRequiredDialog(
    val activity: Activity,
    textId: Int,
    private val positiveActionCallback: () -> Unit,
    private val negativeActionCallback: (() -> Unit)? = null
) {
    private var dialog: AlertDialog? = null

    init {
        val view = DialogMessageBinding.inflate(activity.layoutInflater, null, false)
        view.message.text = activity.getString(textId)

        activity.getAlertDialogBuilder()
            .setPositiveButton(R.string.grant_permission) { _, _ -> positiveActionCallback() }
            .setNegativeButton(R.string.cancel) { _, _ -> negativeActionCallback?.invoke() }.apply {
                val title = activity.getString(R.string.permission_required)
                activity.setupDialogStuff(view.root, this, titleText = title) { alertDialog ->
                    dialog = alertDialog
                }
            }
    }
}

@Composable
fun PermissionRequiredAlertDialog(
    alertDialogState: AlertDialogState,
    text: String,
    modifier: Modifier = Modifier,
    negativeActionCallback: (() -> Unit)? = null,
    positiveActionCallback: () -> Unit
) {
    AlertDialog(
        containerColor = dialogContainerColor,
        modifier = modifier
            .dialogBorder,
        onDismissRequest = alertDialogState::hide,
        shape = dialogShape,
        tonalElevation = dialogElevation,
        dismissButton = {
            TextButton(onClick = {
                alertDialogState.hide()
                negativeActionCallback?.invoke()
            }) {
                Text(text = stringResource(id = R.string.cancel))
            }
        },
        confirmButton = {
            TextButton(onClick = {
                alertDialogState.hide()
                positiveActionCallback()
            }) {
                Text(text = stringResource(id = R.string.grant_permission))
            }
        },
        title = {
            Text(
                text = stringResource(id = R.string.permission_required),
                fontSize = 21.sp,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Text(
                fontSize = 16.sp,
                text = text
            )
        }
    )
}

@Composable
@MyDevices
private fun PermissionRequiredAlertDialogPreview() {
    AppThemeSurface {
        PermissionRequiredAlertDialog(
            alertDialogState = rememberAlertDialogState(),
            text = "Test",
            negativeActionCallback = {}
        ) {}
    }
}
