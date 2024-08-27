package com.simplemobiletools.notes.pro.new_architecture.shared.ui.compose.extensions

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import com.simplemobiletools.notes.pro.R
import com.simplemobiletools.notes.pro.new_architecture.shared.extensions.launchViewIntent
import com.simplemobiletools.notes.pro.new_architecture.shared.compose.alert_dialog.rememberAlertDialogState
import com.simplemobiletools.notes.pro.new_architecture.shared.compose.extensions.DEVELOPER_PLAY_STORE_URL
import com.simplemobiletools.notes.pro.new_architecture.shared.compose.extensions.FAKE_VERSION_APP_LABEL
import com.simplemobiletools.notes.pro.new_architecture.shared.compose.extensions.fakeVersionCheck
import com.simplemobiletools.notes.pro.new_architecture.shared.compose.extensions.getActivity
import com.simplemobiletools.notes.pro.new_architecture.shared.dialogs.ConfirmationAlertDialog

@Composable
fun FakeVersionCheck() {
    val context = LocalContext.current
    val confirmationDialogAlertDialogState = rememberAlertDialogState().apply {
        DialogMember {
            ConfirmationAlertDialog(
                alertDialogState = this,
                message = FAKE_VERSION_APP_LABEL,
                positive = R.string.ok,
                negative = null
            ) {
                context.getActivity().launchViewIntent(DEVELOPER_PLAY_STORE_URL)
            }
        }
    }
    LaunchedEffect(Unit) {
        context.fakeVersionCheck(confirmationDialogAlertDialogState::show)
    }
}

