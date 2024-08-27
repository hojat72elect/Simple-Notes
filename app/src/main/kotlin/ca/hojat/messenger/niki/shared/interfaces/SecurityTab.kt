package ca.hojat.messenger.niki.shared.interfaces

import androidx.biometric.auth.AuthPromptHost
import ca.hojat.messenger.niki.shared.views.MyScrollView

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
