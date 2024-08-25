package com.simplemobiletools.notes.pro.new_architecture.shared.extensions

import androidx.fragment.app.Fragment
import com.simplemobiletools.notes.pro.new_architecture.shared.helpers.Config

val Fragment.config: Config? get() = if (context != null) Config.newInstance(context!!) else null
