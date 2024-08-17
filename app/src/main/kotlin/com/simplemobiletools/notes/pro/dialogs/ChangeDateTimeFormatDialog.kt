package com.simplemobiletools.notes.pro.dialogs

import android.text.format.DateFormat
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ripple.LocalRippleTheme
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxColors
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.simplemobiletools.notes.pro.R
import com.simplemobiletools.notes.pro.compose.alert_dialog.AlertDialogState
import com.simplemobiletools.notes.pro.compose.alert_dialog.DialogSurface
import com.simplemobiletools.notes.pro.compose.alert_dialog.rememberAlertDialogState
import com.simplemobiletools.notes.pro.compose.components.RadioGroupDialogComponent
import com.simplemobiletools.notes.pro.compose.extensions.MyDevices
import com.simplemobiletools.notes.pro.compose.extensions.NoRippleTheme
import com.simplemobiletools.notes.pro.compose.extensions.rememberMutableInteractionSource
import com.simplemobiletools.notes.pro.compose.settings.SettingsHorizontalDivider
import com.simplemobiletools.notes.pro.compose.theme.AppThemeSurface
import com.simplemobiletools.notes.pro.compose.theme.SimpleTheme
import com.simplemobiletools.notes.pro.compose.theme.preferenceLabelColor
import com.simplemobiletools.notes.pro.extensions.baseConfig
import com.simplemobiletools.notes.pro.helpers.DATE_FORMAT_EIGHT
import com.simplemobiletools.notes.pro.helpers.DATE_FORMAT_FIVE
import com.simplemobiletools.notes.pro.helpers.DATE_FORMAT_FOUR
import com.simplemobiletools.notes.pro.helpers.DATE_FORMAT_ONE
import com.simplemobiletools.notes.pro.helpers.DATE_FORMAT_SEVEN
import com.simplemobiletools.notes.pro.helpers.DATE_FORMAT_SIX
import com.simplemobiletools.notes.pro.helpers.DATE_FORMAT_THREE
import com.simplemobiletools.notes.pro.helpers.DATE_FORMAT_TWO
import java.util.Calendar
import java.util.Locale
import kotlinx.collections.immutable.toImmutableList


@Composable
fun ChangeDateTimeFormatAlertDialog(
    alertDialogState: AlertDialogState,
    is24HourChecked: Boolean,
    modifier: Modifier = Modifier,
    callback: (selectedFormat: String, is24HourChecked: Boolean) -> Unit
) {
    val context = LocalContext.current
    val selections = remember {
        mapOf(
            Pair(DATE_FORMAT_ONE, formatDateSample(DATE_FORMAT_ONE)),
            Pair(DATE_FORMAT_TWO, formatDateSample(DATE_FORMAT_TWO)),
            Pair(DATE_FORMAT_THREE, formatDateSample(DATE_FORMAT_THREE)),
            Pair(DATE_FORMAT_FOUR, formatDateSample(DATE_FORMAT_FOUR)),
            Pair(DATE_FORMAT_FIVE, formatDateSample(DATE_FORMAT_FIVE)),
            Pair(DATE_FORMAT_SIX, formatDateSample(DATE_FORMAT_SIX)),
            Pair(DATE_FORMAT_SEVEN, formatDateSample(DATE_FORMAT_SEVEN)),
            Pair(DATE_FORMAT_EIGHT, formatDateSample(DATE_FORMAT_EIGHT)),
        )
    }
    val kinds = remember {
        selections.values.toImmutableList()
    }
    val initiallySelected = remember {
        requireNotNull(selections[context.baseConfig.dateFormat]) {
            "Incorrect format, please check selections"
        }
    }
    val (selected, setSelected) = remember { mutableStateOf(initiallySelected) }

    var is24HoursSelected by remember { mutableStateOf(is24HourChecked) }

    AlertDialog(
        onDismissRequest = alertDialogState::hide,
    ) {
        DialogSurface {
            Box {
                Column(
                    modifier = modifier
                        .padding(bottom = 64.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    RadioGroupDialogComponent(
                        items = kinds, selected = selected,
                        setSelected = setSelected,
                        modifier = Modifier.padding(
                            vertical = SimpleTheme.dimens.padding.extraLarge,
                        )
                    )
                    SettingsHorizontalDivider()

                    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                        DialogCheckBoxWithRadioAlignmentComponent(
                            label = stringResource(id = R.string.use_24_hour_time_format),
                            initialValue = is24HoursSelected,
                            onChange = { is24HoursSelected = it },
                            modifier = Modifier.padding(horizontal = SimpleTheme.dimens.padding.medium)
                        )
                    }
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.End,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            top = SimpleTheme.dimens.padding.extraLarge,
                            bottom = SimpleTheme.dimens.padding.extraLarge,
                            end = SimpleTheme.dimens.padding.extraLarge
                        )
                        .align(Alignment.BottomStart)
                ) {
                    TextButton(onClick = {
                        alertDialogState.hide()
                    }) {
                        Text(text = stringResource(id = R.string.cancel))
                    }

                    TextButton(onClick = {
                        alertDialogState.hide()
                        callback(
                            selections.filterValues { it == selected }.keys.first(),
                            is24HoursSelected
                        )
                    }) {
                        Text(text = stringResource(id = R.string.ok))
                    }
                }
            }
        }
    }
}


private const val timeSample = 1676419200000    // February 15, 2023
private fun formatDateSample(format: String): String {
    val cal = Calendar.getInstance(Locale.ENGLISH)
    cal.timeInMillis = timeSample
    return DateFormat.format(format, cal).toString()
}

@Composable
internal fun DialogCheckBoxWithRadioAlignmentComponent(
    modifier: Modifier = Modifier,
    label: String,
    initialValue: Boolean = false,
    isPreferenceEnabled: Boolean = true,
    onChange: ((Boolean) -> Unit)? = null,
    checkboxColors: CheckboxColors = CheckboxDefaults.colors(
        checkedColor = SimpleTheme.colorScheme.primary,
        checkmarkColor = SimpleTheme.colorScheme.surface,
    )
) {
    val interactionSource = rememberMutableInteractionSource()
    val indication = LocalIndication.current

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                onClick = { onChange?.invoke(!initialValue) },
                interactionSource = interactionSource,
                indication = indication
            )
            .then(modifier),
    ) {
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                modifier = Modifier
                    .fillMaxWidth(),
                text = label,
                color = preferenceLabelColor(isEnabled = isPreferenceEnabled),
                fontSize = with(LocalDensity.current) {
                    dimensionResource(id = R.dimen.normal_text_size).toSp()
                },
                textAlign = TextAlign.End
            )
        }
        CompositionLocalProvider(LocalRippleTheme provides NoRippleTheme) {
            Checkbox(
                checked = initialValue,
                onCheckedChange = { onChange?.invoke(it) },
                enabled = isPreferenceEnabled,
                colors = checkboxColors,
                interactionSource = interactionSource
            )
        }
    }
}

@Composable
@MyDevices
private fun ChangeDateTimeFormatAlertDialogPreview() {
    AppThemeSurface {
        ChangeDateTimeFormatAlertDialog(
            alertDialogState = rememberAlertDialogState(),
            is24HourChecked = true
        ) { _, _ -> }
    }
}
