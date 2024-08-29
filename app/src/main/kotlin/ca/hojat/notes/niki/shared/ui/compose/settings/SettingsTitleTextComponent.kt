package ca.hojat.notes.niki.shared.ui.compose.settings

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.sp
import ca.hojat.notes.niki.shared.ui.compose.theme.AppThemeSurface
import ca.hojat.notes.niki.shared.ui.compose.extensions.MyDevices
import ca.hojat.notes.niki.shared.ui.compose.theme.SimpleTheme

@Composable
fun SettingsTitleTextComponent(
    modifier: Modifier = Modifier,
    text: String,
    color: Color = SimpleTheme.colorScheme.primary,
    maxLines: Int = 1,
    overflow: TextOverflow = TextOverflow.Ellipsis
) {
    Box(modifier = Modifier.padding(top = SimpleTheme.dimens.padding.extraLarge)) {
        Text(
            text = text.uppercase(),
            modifier = modifier
                .padding(horizontal = SimpleTheme.dimens.padding.small),
            color = color,
            fontSize = 14.sp,
            maxLines = maxLines,
            overflow = overflow
        )
    }
}

@MyDevices
@Composable
private fun SettingsTitleTextComponentPreview() = AppThemeSurface {
    SettingsTitleTextComponent(text = "Color customization")
}
