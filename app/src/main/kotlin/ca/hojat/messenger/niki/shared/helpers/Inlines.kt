package ca.hojat.messenger.niki.shared.helpers

inline fun <T> Iterable<T>.sumByLong(selector: (T) -> Long) = this.sumOf { selector(it) }