package com.simplemobiletools.notes.pro.models.contacts

import kotlinx.serialization.Serializable

@Serializable
data class PhoneNumber(
    var value: String,
    var type: Int,
    var label: String,
    var normalizedNumber: String,
    var isPrimary: Boolean = false
)
