package com.simplemobiletools.notes.pro.compose.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import com.simplemobiletools.commons.compose.theme.color_accent
import com.simplemobiletools.commons.compose.theme.color_primary
import com.simplemobiletools.commons.compose.theme.color_primary_dark

internal val darkColorScheme = darkColorScheme(
    primary = color_primary,
    secondary = color_primary_dark,
    tertiary = color_accent,
)
internal val lightColorScheme = lightColorScheme(
    primary = color_primary,
    secondary = color_primary_dark,
    tertiary = color_accent,
)
