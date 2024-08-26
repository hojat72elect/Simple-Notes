package com.simplemobiletools.notes.pro.new_architecture.shared.compose.extensions

import android.app.Activity
import android.content.Context
import com.simplemobiletools.notes.pro.R
import com.simplemobiletools.notes.pro.new_architecture.shared.extensions.baseConfig
import com.simplemobiletools.notes.pro.new_architecture.shared.extensions.redirectToRateUs
import com.simplemobiletools.notes.pro.new_architecture.shared.extensions.toast
import com.simplemobiletools.notes.pro.new_architecture.shared.helpers.BaseConfig

val Context.config: BaseConfig get() = BaseConfig.newInstance(applicationContext)

fun Activity.rateStarsRedirectAndThankYou(stars: Int) {
    if (stars == 5) {
        redirectToRateUs()
    }
    toast(R.string.thank_you)
    baseConfig.wasAppRated = true
}
