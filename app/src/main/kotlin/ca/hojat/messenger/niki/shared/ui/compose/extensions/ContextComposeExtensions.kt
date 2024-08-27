package ca.hojat.messenger.niki.shared.ui.compose.extensions

import android.app.Activity
import android.content.Context
import ca.hojat.messenger.niki.R
import ca.hojat.messenger.niki.shared.extensions.baseConfig
import ca.hojat.messenger.niki.shared.extensions.redirectToRateUs
import ca.hojat.messenger.niki.shared.extensions.toast
import ca.hojat.messenger.niki.shared.helpers.BaseConfig

val Context.config: BaseConfig get() = BaseConfig.newInstance(applicationContext)

fun Activity.rateStarsRedirectAndThankYou(stars: Int) {
    if (stars == 5) {
        redirectToRateUs()
    }
    toast(R.string.thank_you)
    baseConfig.wasAppRated = true
}
