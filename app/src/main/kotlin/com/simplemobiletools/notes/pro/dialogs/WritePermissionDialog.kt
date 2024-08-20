package com.simplemobiletools.notes.pro.dialogs

import android.app.Activity
import android.os.Build
import android.text.Html
import android.text.Spanned
import androidx.annotation.DrawableRes
import androidx.annotation.RequiresApi
import androidx.annotation.StringRes
import androidx.appcompat.app.AlertDialog
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import com.bumptech.glide.Glide
import com.bumptech.glide.integration.compose.GlideImage
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.transition.DrawableCrossFadeFactory
import com.simplemobiletools.notes.pro.R
import com.simplemobiletools.notes.pro.activities.BaseActivity
import com.simplemobiletools.notes.pro.compose.alert_dialog.AlertDialogState
import com.simplemobiletools.notes.pro.compose.alert_dialog.DialogSurface
import com.simplemobiletools.notes.pro.compose.alert_dialog.dialogTextColor
import com.simplemobiletools.notes.pro.compose.alert_dialog.rememberAlertDialogState
import com.simplemobiletools.notes.pro.compose.components.LinkifyTextComponent
import com.simplemobiletools.notes.pro.compose.extensions.MyDevices
import com.simplemobiletools.notes.pro.compose.extensions.andThen
import com.simplemobiletools.notes.pro.compose.theme.AppThemeSurface
import com.simplemobiletools.notes.pro.compose.theme.SimpleTheme
import com.simplemobiletools.notes.pro.databinding.DialogWritePermissionBinding
import com.simplemobiletools.notes.pro.databinding.DialogWritePermissionOtgBinding
import com.simplemobiletools.notes.pro.extensions.fromHtml
import com.simplemobiletools.notes.pro.extensions.getAlertDialogBuilder
import com.simplemobiletools.notes.pro.extensions.humanizePath
import com.simplemobiletools.notes.pro.extensions.setupDialogStuff

@RequiresApi(Build.VERSION_CODES.O)
class WritePermissionDialog(
    activity: Activity,
    private val writePermissionDialogMode: WritePermissionDialogMode,
    val callback: () -> Unit
) {

    @Immutable
    sealed class WritePermissionDialogMode {
        @Immutable
        data object Otg : WritePermissionDialogMode()

        @Immutable
        data object SdCard : WritePermissionDialogMode()

        @Immutable
        data class OpenDocumentTreeSDK30(val path: String) : WritePermissionDialogMode()

        @Immutable
        data object CreateDocumentSDK30 : WritePermissionDialogMode()
    }

    private var dialog: AlertDialog? = null

    init {
        val sdCardView = DialogWritePermissionBinding.inflate(activity.layoutInflater, null, false)
        val otgView = DialogWritePermissionOtgBinding.inflate(
            activity.layoutInflater,
            null,
            false
        )

        var dialogTitle = R.string.confirm_storage_access_title

        val glide = Glide.with(activity)
        val crossFade = DrawableTransitionOptions.withCrossFade()
        when (writePermissionDialogMode) {
            WritePermissionDialogMode.Otg -> {
                otgView.writePermissionsDialogOtgText.setText(R.string.confirm_usb_storage_access_text)
                glide.load(R.drawable.img_write_storage_otg).transition(crossFade)
                    .into(otgView.writePermissionsDialogOtgImage)
            }

            WritePermissionDialogMode.SdCard -> {
                glide.load(R.drawable.img_write_storage).transition(crossFade)
                    .into(sdCardView.writePermissionsDialogImage)
                glide.load(R.drawable.img_write_storage_sd).transition(crossFade)
                    .into(sdCardView.writePermissionsDialogImageSd)
            }

            is WritePermissionDialogMode.OpenDocumentTreeSDK30 -> {
                dialogTitle = R.string.confirm_folder_access_title
                val humanizedPath = activity.humanizePath(writePermissionDialogMode.path)
                otgView.writePermissionsDialogOtgText.text =
                    Html.fromHtml(
                        activity.getString(
                            R.string.confirm_storage_access_android_text_specific,
                            humanizedPath
                        )
                    )
                glide.load(R.drawable.img_write_storage_sdk_30).transition(crossFade)
                    .into(otgView.writePermissionsDialogOtgImage)

                otgView.writePermissionsDialogOtgImage.setOnClickListener {
                    dialogConfirmed()
                }
            }

            WritePermissionDialogMode.CreateDocumentSDK30 -> {
                dialogTitle = R.string.confirm_folder_access_title
                otgView.writePermissionsDialogOtgText.text =
                    Html.fromHtml(activity.getString(R.string.confirm_create_doc_for_new_folder_text))
                glide.load(R.drawable.img_write_storage_create_doc_sdk_30).transition(crossFade)
                    .into(otgView.writePermissionsDialogOtgImage)

                otgView.writePermissionsDialogOtgImage.setOnClickListener {
                    dialogConfirmed()
                }
            }
        }

        activity.getAlertDialogBuilder()
            .setPositiveButton(R.string.ok) { _, _ -> dialogConfirmed() }
            .setOnCancelListener {
                BaseActivity.funAfterSAFPermission?.invoke(false)
                BaseActivity.funAfterSAFPermission = null
            }
            .apply {
                activity.setupDialogStuff(
                    if (writePermissionDialogMode == WritePermissionDialogMode.SdCard) sdCardView.root else otgView.root,
                    this,
                    dialogTitle
                ) { alertDialog ->
                    dialog = alertDialog
                }
            }
    }

    private fun dialogConfirmed() {
        dialog?.dismiss()
        callback()
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun WritePermissionAlertDialog(
    alertDialogState: AlertDialogState,
    writePermissionDialogMode: WritePermissionDialog.WritePermissionDialogMode,
    modifier: Modifier = Modifier,
    callback: () -> Unit,
    onCancelCallback: () -> Unit
) {
    val dialogTitle = remember {
        adjustDialogTitle(
            writePermissionDialogMode = writePermissionDialogMode,
            dialogTitle = R.string.confirm_storage_access_title
        )
    }
    val crossFadeTransition = remember {
        DrawableTransitionOptions().crossFade(
            DrawableCrossFadeFactory.Builder(350).setCrossFadeEnabled(true).build()
        )
    }

    AlertDialog(
        onDismissRequest = alertDialogState::hide andThen onCancelCallback,
        modifier = modifier
            .fillMaxWidth(0.9f),
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        DialogSurface {
            Box {
                Column(
                    modifier = modifier
                        .padding(bottom = 64.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    Text(
                        text = stringResource(id = dialogTitle),
                        color = dialogTextColor,
                        modifier = Modifier
                            .padding(
                                horizontal = SimpleTheme.dimens.padding.extraLarge.plus(
                                    SimpleTheme.dimens.padding.large
                                )
                            )
                            .padding(top = SimpleTheme.dimens.padding.extraLarge.plus(SimpleTheme.dimens.padding.small)),
                        fontSize = 21.sp,
                        fontWeight = FontWeight.Bold,
                    )

                    when (writePermissionDialogMode) {
                        WritePermissionDialog.WritePermissionDialogMode.CreateDocumentSDK30 -> CreateDocumentSDK30(
                            crossFadeTransition = crossFadeTransition,
                            onImageClick = alertDialogState::hide andThen callback
                        )

                        is WritePermissionDialog.WritePermissionDialogMode.OpenDocumentTreeSDK30 -> OpenDocumentTreeSDK30(
                            crossFadeTransition = crossFadeTransition,
                            onImageClick = alertDialogState::hide andThen callback,
                            path = writePermissionDialogMode.path
                        )

                        WritePermissionDialog.WritePermissionDialogMode.Otg -> OTG(
                            crossFadeTransition
                        )

                        WritePermissionDialog.WritePermissionDialogMode.SdCard -> SDCard(
                            crossFadeTransition
                        )
                    }
                    Spacer(Modifier.padding(vertical = SimpleTheme.dimens.padding.extraLarge))
                }

                TextButton(
                    onClick = alertDialogState::hide andThen callback,
                    modifier = Modifier
                        .padding(
                            top = SimpleTheme.dimens.padding.extraLarge,
                            bottom = SimpleTheme.dimens.padding.extraLarge,
                            end = SimpleTheme.dimens.padding.extraLarge
                        )
                        .align(Alignment.BottomEnd)
                ) {
                    Text(text = stringResource(id = R.string.ok))
                }
            }
        }
    }
}

@Composable
private fun CreateDocumentSDK30(
    crossFadeTransition: DrawableTransitionOptions,
    onImageClick: () -> Unit
) {
    WritePermissionText(stringResource(R.string.confirm_create_doc_for_new_folder_text).fromHtml())
    WritePermissionImage(
        crossFadeTransition = crossFadeTransition,
        drawable = R.drawable.img_write_storage_create_doc_sdk_30,
        modifier = Modifier.clickable(onClick = onImageClick)
    )
}

@Composable
private fun OpenDocumentTreeSDK30(
    crossFadeTransition: DrawableTransitionOptions,
    onImageClick: () -> Unit,
    path: String
) {
    val context = LocalContext.current
    val view = LocalView.current

    val humanizedPath = remember { if (!view.isInEditMode) context.humanizePath(path) else "" }
    WritePermissionText(
        stringResource(
            R.string.confirm_storage_access_android_text_specific,
            humanizedPath
        ).fromHtml()
    )
    WritePermissionImage(
        crossFadeTransition = crossFadeTransition,
        drawable = R.drawable.img_write_storage_sdk_30,
        modifier = Modifier.clickable(onClick = onImageClick)
    )
}

@Composable
private fun SDCard(crossFadeTransition: DrawableTransitionOptions) {
    WritePermissionText(R.string.confirm_storage_access_text)
    WritePermissionImage(
        crossFadeTransition = crossFadeTransition,
        drawable = R.drawable.img_write_storage
    )
    WritePermissionText(R.string.confirm_storage_access_text_sd)
    WritePermissionImage(
        crossFadeTransition = crossFadeTransition,
        drawable = R.drawable.img_write_storage_sd
    )
}

@Composable
private fun OTG(
    crossFadeTransition: DrawableTransitionOptions
) {
    WritePermissionText(R.string.confirm_usb_storage_access_text)
    WritePermissionImage(
        crossFadeTransition = crossFadeTransition,
        drawable = R.drawable.img_write_storage_otg
    )
}

@Composable
private fun WritePermissionImage(
    modifier: Modifier = Modifier,
    crossFadeTransition: DrawableTransitionOptions,
    @DrawableRes drawable: Int
) {
    GlideImage(
        modifier = modifier
            .padding(horizontal = SimpleTheme.dimens.padding.extraLarge.plus(SimpleTheme.dimens.padding.large)),
        model = drawable,
        contentDescription = null,
    ) { requestBuilder ->
        requestBuilder.transition(crossFadeTransition)
    }
}

@Composable
private fun WritePermissionText(@StringRes text: Int) {
    Text(
        text = stringResource(id = text),
        color = dialogTextColor,
        modifier = Modifier
            .padding(horizontal = SimpleTheme.dimens.padding.extraLarge.plus(SimpleTheme.dimens.padding.medium))
            .padding(vertical = SimpleTheme.dimens.padding.extraLarge),
    )
}

@Composable
private fun WritePermissionText(text: Spanned) {
    LinkifyTextComponent(
        modifier = Modifier
            .padding(horizontal = SimpleTheme.dimens.padding.extraLarge.plus(SimpleTheme.dimens.padding.medium))
            .padding(vertical = SimpleTheme.dimens.padding.extraLarge),
    ) {
        text
    }
}

@RequiresApi(Build.VERSION_CODES.O)
private fun adjustDialogTitle(
    writePermissionDialogMode: WritePermissionDialog.WritePermissionDialogMode,
    dialogTitle: Int
): Int =
    when (writePermissionDialogMode) {
        WritePermissionDialog.WritePermissionDialogMode.CreateDocumentSDK30 -> R.string.confirm_folder_access_title
        is WritePermissionDialog.WritePermissionDialogMode.OpenDocumentTreeSDK30 -> R.string.confirm_folder_access_title
        else -> dialogTitle
    }

private class WritePermissionDialogModePreviewParameter :
    PreviewParameterProvider<WritePermissionDialog.WritePermissionDialogMode> {
    override val values: Sequence<WritePermissionDialog.WritePermissionDialogMode>
        @RequiresApi(Build.VERSION_CODES.O)
        get() = sequenceOf(
            WritePermissionDialog.WritePermissionDialogMode.SdCard,
            WritePermissionDialog.WritePermissionDialogMode.Otg,
            WritePermissionDialog.WritePermissionDialogMode.CreateDocumentSDK30,
            WritePermissionDialog.WritePermissionDialogMode.OpenDocumentTreeSDK30(""),
        )
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
@MyDevices
private fun WritePermissionAlertDialogPreview(
    @PreviewParameter(
        WritePermissionDialogModePreviewParameter::class
    ) mode: WritePermissionDialog.WritePermissionDialogMode
) {
    AppThemeSurface {
        WritePermissionAlertDialog(
            alertDialogState = rememberAlertDialogState(),
            writePermissionDialogMode = WritePermissionDialog.WritePermissionDialogMode.OpenDocumentTreeSDK30(
                "."
            ),
            callback = {}
        ) {}
    }
}
