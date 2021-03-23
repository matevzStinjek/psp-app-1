package si.uni_lj.fri.pbd.miniapp1.services

import android.app.Application
import android.database.Cursor
import android.provider.ContactsContract
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import si.uni_lj.fri.pbd.miniapp1.ui.contacts.ContactDTO

class ContactsService(private val mApplication: Application) {

    suspend fun fetchContacts(): ArrayList<ContactDTO> {
        // use coroutines to fetch all data concurrently
        val contactsListAsync = GlobalScope.async { getPhoneContacts() }
        val contactNumbersAsync = GlobalScope.async { getContactNumbers() }
        val contactEmailAsync = GlobalScope.async { getContactEmails() }

        // await for coroutines to resolve
        val contacts = contactsListAsync.await()
        val contactNumbers = contactNumbersAsync.await()
        val contactEmails = contactEmailAsync.await()

        // compile them into a single structure
        compileContacts(contacts, contactNumbers, contactEmails)
        return contacts
    }

    private fun getPhoneContacts(): ArrayList<ContactDTO> {
        val contactsList = ArrayList<ContactDTO>()
        val contactsCursor = mApplication.contentResolver?.query(
            ContactsContract.Contacts.CONTENT_URI, null, null, null,
            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC"
        )
        if (contactsCursor != null && contactsCursor.count > 0) {
            val idIndex = contactsCursor.getColumnIndex(ContactsContract.Contacts._ID)
            val nameIndex = contactsCursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME)
            while (contactsCursor.moveToNext()) {
                val id = contactsCursor.getString(idIndex)
                val name = contactsCursor.getString(nameIndex)
                if (name != null) {
                    contactsList.add(ContactDTO(id, name))
                }
            }
            contactsCursor.close()
        }
        return contactsList
    }

    private fun getContactNumbers(): HashMap<String, ArrayList<String>> {
        val contactsNumberMap = HashMap<String, ArrayList<String>>()
        val phoneCursor: Cursor? = mApplication.contentResolver.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null
        )
        if (phoneCursor != null && phoneCursor.count > 0) {
            val contactIdIndex =
                phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID)
            val numberIndex =
                phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
            while (phoneCursor.moveToNext()) {
                val contactId = phoneCursor.getString(contactIdIndex)
                val number: String = phoneCursor.getString(numberIndex)
                // if the key does not exist, initialise it to an empty ArrayList
                if (!contactsNumberMap.containsKey(contactId)) {
                    contactsNumberMap[contactId] = arrayListOf()
                }
                contactsNumberMap[contactId]!!.add(number)
            }
            phoneCursor.close()
        }
        return contactsNumberMap
    }

    private fun getContactEmails(): HashMap<String, ArrayList<String>> {
        val contactsEmailMap = HashMap<String, ArrayList<String>>()
        val emailCursor = mApplication.contentResolver.query(
            ContactsContract.CommonDataKinds.Email.CONTENT_URI, null, null, null, null
        )
        if (emailCursor != null && emailCursor.count > 0) {
            val contactIdIndex =
                emailCursor.getColumnIndex(ContactsContract.CommonDataKinds.Email.CONTACT_ID)
            val emailIndex =
                emailCursor.getColumnIndex(ContactsContract.CommonDataKinds.Email.ADDRESS)
            while (emailCursor.moveToNext()) {
                val contactId = emailCursor.getString(contactIdIndex)
                val email = emailCursor.getString(emailIndex)
                // if the key does not exist, initialise it to an empty ArrayList
                if (!contactsEmailMap.containsKey(contactId)) {
                    contactsEmailMap[contactId] = arrayListOf()
                }
                contactsEmailMap[contactId]!!.add(email)
            }
            emailCursor.close()
        }
        return contactsEmailMap
    }

    private fun compileContacts(
        contacts: ArrayList<ContactDTO>,
        contactNumbers: HashMap<String, ArrayList<String>>,
        contactEmails: HashMap<String, ArrayList<String>>
    ) {
        contacts.forEach {
            contactNumbers[it.id]?.let { numbers ->
                it.numbers = numbers
            }
            contactEmails[it.id]?.let { emails ->
                it.emails = emails
            }
        }
    }
}