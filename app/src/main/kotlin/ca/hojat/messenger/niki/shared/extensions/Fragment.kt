package ca.hojat.messenger.niki.shared.extensions

import androidx.fragment.app.Fragment
import ca.hojat.messenger.niki.shared.helpers.Config

val Fragment.config: Config? get() = if (context != null) Config.newInstance(context!!) else null