package com.simplemobiletools.notes.pro.helpers

import android.accounts.Account
import android.accounts.AccountManager
import android.content.ContentProviderOperation
import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.os.Build
import android.provider.ContactsContract.AUTHORITY
import android.provider.ContactsContract.Groups
import android.provider.ContactsContract.RawContacts
import android.provider.ContactsContract.Settings
import androidx.annotation.RequiresApi
import com.simplemobiletools.notes.pro.R
import com.simplemobiletools.notes.pro.extensions.baseConfig
import com.simplemobiletools.notes.pro.extensions.getLongValue
import com.simplemobiletools.notes.pro.extensions.getStringValue
import com.simplemobiletools.notes.pro.extensions.groupsDB
import com.simplemobiletools.notes.pro.extensions.hasPermission
import com.simplemobiletools.notes.pro.extensions.queryCursor
import com.simplemobiletools.notes.pro.models.contacts.ContactSource
import com.simplemobiletools.notes.pro.models.contacts.Group
import java.util.Locale

@RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
class ContactsHelper(val context: Context) {

    private fun getContentResolverAccounts(): HashSet<ContactSource> {
        val sources = HashSet<ContactSource>()
        arrayOf(Groups.CONTENT_URI, Settings.CONTENT_URI, RawContacts.CONTENT_URI).forEach {
            fillSourcesFromUri(it, sources)
        }

        return sources
    }

    private fun fillSourcesFromUri(uri: Uri, sources: HashSet<ContactSource>) {
        val projection = arrayOf(
            RawContacts.ACCOUNT_NAME,
            RawContacts.ACCOUNT_TYPE
        )

        context.queryCursor(uri, projection) { cursor ->
            val name = cursor.getStringValue(RawContacts.ACCOUNT_NAME)
            val type = cursor.getStringValue(RawContacts.ACCOUNT_TYPE)
            var publicName = name
            if (type == TELEGRAM_PACKAGE) {
                publicName = context.getString(R.string.telegram)
            }

            val source = ContactSource(name, type, publicName)
            sources.add(source)
        }
    }

    fun getStoredGroupsSync(): ArrayList<Group> {
        val groups = getDeviceStoredGroups()
        groups.addAll(context.groupsDB.getGroups())
        return groups
    }

    private fun getDeviceStoredGroups(): ArrayList<Group> {
        val groups = ArrayList<Group>()
        if (!context.hasPermission(PERMISSION_READ_CONTACTS)) {
            return groups
        }

        val uri = Groups.CONTENT_URI
        val projection = arrayOf(
            Groups._ID,
            Groups.TITLE,
            Groups.SYSTEM_ID
        )

        val selection = "${Groups.AUTO_ADD} = ? AND ${Groups.FAVORITES} = ?"
        val selectionArgs = arrayOf("0", "0")

        context.queryCursor(
            uri,
            projection,
            selection,
            selectionArgs,
            showErrors = true
        ) { cursor ->
            val id = cursor.getLongValue(Groups._ID)
            val title = cursor.getStringValue(Groups.TITLE)

            if (groups.map { it.title }.contains(title)) {
                return@queryCursor
            }

            groups.add(Group(id, title))
        }
        return groups
    }

    fun getDeviceContactSources(): LinkedHashSet<ContactSource> {
        val sources = LinkedHashSet<ContactSource>()
        if (!context.hasPermission(PERMISSION_READ_CONTACTS)) {
            return sources
        }

        if (!context.baseConfig.wasLocalAccountInitialized) {
            initializeLocalPhoneAccount()
            context.baseConfig.wasLocalAccountInitialized = true
        }

        val accounts = AccountManager.get(context).accounts

        if (context.hasPermission(PERMISSION_READ_SYNC_SETTINGS)) {
            accounts.forEach {
                if (ContentResolver.getIsSyncable(it, AUTHORITY) == 1) {
                    var publicName = it.name
                    if (it.type == TELEGRAM_PACKAGE) {
                        publicName = context.getString(R.string.telegram)
                    } else if (it.type == VIBER_PACKAGE) {
                        publicName = context.getString(R.string.viber)
                    }
                    val contactSource = ContactSource(it.name, it.type, publicName)
                    sources.add(contactSource)
                }
            }
        }

        var hadEmptyAccount = false
        val allAccounts = getContentResolverAccounts()
        val contentResolverAccounts = allAccounts.filter {
            if (it.name.isEmpty() && it.type.isEmpty() && allAccounts.none {
                    it.name.lowercase(
                        Locale.getDefault()
                    ) == "phone"
                }) {
                hadEmptyAccount = true
            }

            it.name.isNotEmpty() && it.type.isNotEmpty() && !accounts.contains(
                Account(
                    it.name,
                    it.type
                )
            )
        }
        sources.addAll(contentResolverAccounts)

        if (hadEmptyAccount) {
            sources.add(ContactSource("", "", context.getString(R.string.phone_storage)))
        }

        return sources
    }

    // make sure the local Phone contact source is initialized and available
    // https://stackoverflow.com/a/6096508/1967672
    private fun initializeLocalPhoneAccount() {
        try {
            val operations = ArrayList<ContentProviderOperation>()
            ContentProviderOperation.newInsert(RawContacts.CONTENT_URI).apply {
                withValue(RawContacts.ACCOUNT_NAME, null)
                withValue(RawContacts.ACCOUNT_TYPE, null)
                operations.add(build())
            }

            val results = context.contentResolver.applyBatch(AUTHORITY, operations)
            val rawContactUri = results.firstOrNull()?.uri ?: return
            context.contentResolver.delete(rawContactUri, null, null)
        } catch (ignored: Exception) {
        }
    }


}
