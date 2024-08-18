package com.simplemobiletools.notes.pro.models.contacts

import android.graphics.Bitmap
import com.simplemobiletools.notes.pro.extensions.normalizeString
import com.simplemobiletools.notes.pro.helpers.SORT_BY_FIRST_NAME
import com.simplemobiletools.notes.pro.helpers.SORT_BY_FULL_NAME
import com.simplemobiletools.notes.pro.helpers.SORT_BY_MIDDLE_NAME
import com.simplemobiletools.notes.pro.helpers.SORT_BY_SURNAME
import com.simplemobiletools.notes.pro.helpers.SORT_DESCENDING
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable

@Serializable
data class Contact(
    var id: Int,
    var prefix: String = "",
    var firstName: String = "",
    var middleName: String = "",
    var surname: String = "",
    var suffix: String = "",
    var nickname: String = "",
    var photoUri: String = "",
    var phoneNumbers: ArrayList<PhoneNumber> = arrayListOf(),
    var emails: ArrayList<Email> = arrayListOf(),
    var addresses: ArrayList<Address> = arrayListOf(),
    var events: ArrayList<Event> = arrayListOf(),
    var source: String = "",
    var starred: Int = 0,
    var contactId: Int,
    var thumbnailUri: String = "",
    @Contextual
    var photo: Bitmap? = null,
    var notes: String = "",
    var groups: ArrayList<Group> = arrayListOf(),
    var organization: Organization = Organization("", ""),
    var websites: ArrayList<String> = arrayListOf(),
    var ims: ArrayList<IM> = arrayListOf(),
    var mimeType: String = "",
    var ringTone: String? = ""
) : Comparable<Contact> {

    val name = getNameToDisplay()

    companion object {
        var sorting = 0
        var startWithSurname = false
    }

    override fun compareTo(other: Contact): Int {
        var result = when {
            sorting and SORT_BY_FIRST_NAME != 0 -> {
                val firstString = firstName.normalizeString()
                val secondString = other.firstName.normalizeString()
                compareUsingStrings(firstString, secondString, other)
            }

            sorting and SORT_BY_MIDDLE_NAME != 0 -> {
                val firstString = middleName.normalizeString()
                val secondString = other.middleName.normalizeString()
                compareUsingStrings(firstString, secondString, other)
            }

            sorting and SORT_BY_SURNAME != 0 -> {
                val firstString = surname.normalizeString()
                val secondString = other.surname.normalizeString()
                compareUsingStrings(firstString, secondString, other)
            }

            sorting and SORT_BY_FULL_NAME != 0 -> {
                val firstString = getNameToDisplay().normalizeString()
                val secondString = other.getNameToDisplay().normalizeString()
                compareUsingStrings(firstString, secondString, other)
            }

            else -> compareUsingIds(other)
        }

        if (sorting and SORT_DESCENDING != 0) {
            result *= -1
        }

        return result
    }

    private fun compareUsingStrings(
        firstString: String,
        secondString: String,
        other: Contact
    ): Int {
        var firstValue = firstString
        var secondValue = secondString

        if (firstValue.isEmpty() && firstName.isEmpty() && middleName.isEmpty() && surname.isEmpty()) {
            val fullCompany = getFullCompany()
            if (fullCompany.isNotEmpty()) {
                firstValue = fullCompany.normalizeString()
            } else if (emails.isNotEmpty()) {
                firstValue = emails.first().value
            }
        }

        if (secondValue.isEmpty() && other.firstName.isEmpty() && other.middleName.isEmpty() && other.surname.isEmpty()) {
            val otherFullCompany = other.getFullCompany()
            if (otherFullCompany.isNotEmpty()) {
                secondValue = otherFullCompany.normalizeString()
            } else if (other.emails.isNotEmpty()) {
                secondValue = other.emails.first().value
            }
        }

        return if (firstValue.firstOrNull()?.isLetter() == true && secondValue.firstOrNull()
                ?.isLetter() == false
        ) {
            -1
        } else if (firstValue.firstOrNull()?.isLetter() == false && secondValue.firstOrNull()
                ?.isLetter() == true
        ) {
            1
        } else {
            if (firstValue.isEmpty() && secondValue.isNotEmpty()) {
                1
            } else if (firstValue.isNotEmpty() && secondValue.isEmpty()) {
                -1
            } else {
                if (firstValue.equals(secondValue, ignoreCase = true)) {
                    getNameToDisplay().compareTo(other.getNameToDisplay(), true)
                } else {
                    firstValue.compareTo(secondValue, true)
                }
            }
        }
    }

    private fun compareUsingIds(other: Contact): Int {
        val firstId = id
        val secondId = other.id
        return firstId.compareTo(secondId)
    }

    private fun getNameToDisplay(): String {
        val firstMiddle = "$firstName $middleName".trim()
        val firstPart = if (startWithSurname) {
            if (surname.isNotEmpty() && firstMiddle.isNotEmpty()) {
                "$surname,"
            } else {
                surname
            }
        } else {
            firstMiddle
        }
        val lastPart = if (startWithSurname) firstMiddle else surname
        val suffixComma = if (suffix.isEmpty()) "" else ", $suffix"
        val fullName = "$prefix $firstPart $lastPart$suffixComma".trim()
        val organization = getFullCompany()
        val email = emails.firstOrNull()?.value?.trim()
        val phoneNumber = phoneNumbers.firstOrNull()?.normalizedNumber

        return when {
            fullName.isNotBlank() -> fullName
            organization.isNotBlank() -> organization
            !email.isNullOrBlank() -> email
            !phoneNumber.isNullOrBlank() -> phoneNumber
            else -> return ""
        }
    }

    private fun getFullCompany(): String {
        var fullOrganization =
            if (organization.company.isEmpty()) "" else "${organization.company}, "
        fullOrganization += organization.jobPosition
        return fullOrganization.trim().trimEnd(',')
    }

}
