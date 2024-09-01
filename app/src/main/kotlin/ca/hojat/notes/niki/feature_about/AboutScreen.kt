package ca.hojat.notes.niki.feature_about

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import ca.hojat.notes.niki.R
import ca.hojat.notes.niki.shared.ui.compose.lists.SimpleColumnScaffold
import ca.hojat.notes.niki.shared.ui.compose.settings.SettingsGroup
import ca.hojat.notes.niki.shared.ui.compose.settings.SettingsHorizontalDivider
import ca.hojat.notes.niki.shared.ui.compose.settings.SettingsListItem
import ca.hojat.notes.niki.shared.ui.compose.settings.SettingsTitleTextComponent
import ca.hojat.notes.niki.shared.ui.compose.theme.SimpleTheme

private val startingTitlePadding = Modifier.padding(start = 60.dp)

@Composable
fun AboutScreen(
    goBack: () -> Unit,
    helpUsSection: @Composable () -> Unit,
    aboutSection: @Composable () -> Unit,
    socialSection: @Composable () -> Unit,
    otherSection: @Composable () -> Unit,
) {
    SimpleColumnScaffold(title = stringResource(id = R.string.about), goBack = goBack) {
        aboutSection()
        helpUsSection()
        socialSection()
        otherSection()
        SettingsListItem(text = stringResource(id = R.string.about_footer))
    }
}

@Composable
fun AboutSection(
    setupFAQ: Boolean,
    onFAQClick: () -> Unit,
    onEmailClick: () -> Unit
) {
    SettingsGroup(title = {
        SettingsTitleTextComponent(
            text = stringResource(id = R.string.support),
            modifier = startingTitlePadding
        )
    }) {
        if (setupFAQ) {
            TwoLinerTextItem(
                click = onFAQClick,
                text = stringResource(id = R.string.frequently_asked_questions),
                icon = R.drawable.ic_question_mark_vector
            )
        }
        TwoLinerTextItem(
            click = onEmailClick,
            text = stringResource(id = R.string.my_email),
            icon = R.drawable.ic_mail_vector
        )
        SettingsHorizontalDivider()
    }
}

@Composable
fun SocialText(
    text: String,
    icon: Int,
    tint: Color? = null,
    click: () -> Unit
) {
    SettingsListItem(
        click = click,
        text = text,
        icon = icon,
        isImage = true,
        tint = tint,
        maxLines = 2,
        overflow = TextOverflow.Ellipsis
    )
}

@Composable
fun TwoLinerTextItem(text: String, icon: Int, click: () -> Unit) {
    SettingsListItem(
        tint = SimpleTheme.colorScheme.onSurface,
        click = click,
        text = text,
        icon = icon,
        maxLines = 2,
        overflow = TextOverflow.Ellipsis
    )
}



