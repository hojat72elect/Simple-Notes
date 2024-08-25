package com.simplemobiletools.notes.pro.new_architecture.shared.helpers

inline fun <T> Iterable<T>.sumByLong(selector: (T) -> Long) = this.sumOf { selector(it) }
