package com.simplemobiletools.notes.pro.interfaces

import androidx.biometric.auth.AuthPromptHost
import com.simplemobiletools.notes.pro.views.MyScrollView

interface SecurityTab {
    fun initTab(
        requiredHash: String,
        listener: HashListener,
        scrollView: MyScrollView,
        biometricPromptHost: AuthPromptHost,
        showBiometricAuthentication: Boolean
    )

    fun visibilityChanged(isVisible: Boolean)
}
