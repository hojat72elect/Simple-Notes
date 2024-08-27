package ca.hojat.messenger.niki.shared.data.models

data class TextHistoryItem(
    val start: Int,
    val before: CharSequence?,
    val after: CharSequence?
)
