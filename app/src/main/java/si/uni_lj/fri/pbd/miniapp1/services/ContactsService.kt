package si.uni_lj.fri.pbd.miniapp1.services

import android.content.ContentProviderOperation
import android.content.ContentResolver
import android.database.Cursor
import android.provider.ContactsContract
import si.uni_lj.fri.pbd.miniapp1.ui.contacts.ContactDTO
import si.uni_lj.fri.pbd.miniapp1.ui.contacts.NewContactDTO

// inspired by https://github.com/kednaik/Coroutines-Contact-Fetching/blob/master/app/src/main/java/com/kedar/coroutinescontactsfetching/ContactsViewModel.kt

class ContactsService(private val resolver: ContentResolver) {

    fun getPhoneContacts(): ArrayList<ContactDTO> {
        val contactsList = ArrayList<ContactDTO>()
        val contactsCursor = resolver.query(
            ContactsContract.Contacts.CONTENT_URI, null, null, null,
            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC"
        )

        if (contactsCursor == null || contactsCursor.count == 0) {
            return contactsList
        }
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
        return contactsList
    }

    fun getContactNumbers(): HashMap<String, ArrayList<String>> {
        val contactsNumberMap = HashMap<String, ArrayList<String>>()
        val phoneCursor: Cursor? = resolver.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null
        )

        if (phoneCursor == null || phoneCursor.count == 0) {
            return contactsNumberMap
        }
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
        return contactsNumberMap
    }

    fun getContactEmails(): HashMap<String, ArrayList<String>> {
        val contactsEmailMap = HashMap<String, ArrayList<String>>()
        val emailCursor = resolver.query(
            ContactsContract.CommonDataKinds.Email.CONTENT_URI, null, null, null, null
        )

        if (emailCursor == null || emailCursor.count == 0) {
            return contactsEmailMap
        }
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
        return contactsEmailMap
    }

    fun insertContact(contact: NewContactDTO) {
        val (name, email, number) = contact
        val operations: ArrayList<ContentProviderOperation> = arrayListOf()

        val initialOp = ContentProviderOperation.newInsert(
            ContactsContract.RawContacts.CONTENT_URI)
            .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, null)
            .withValue(ContactsContract.RawContacts.ACCOUNT_NAME, null)
            .build()
        operations.add(initialOp)

        val op2 = ContentProviderOperation.newInsert(
            ContactsContract.Data.CONTENT_URI)
            .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
            .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
            .withValue(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, name)
            .build()
        operations.add(op2)

        val numberOp = ContentProviderOperation.
        newInsert(ContactsContract.Data.CONTENT_URI)
            .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
            .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
            .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, number)
            .withValue(ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE)
            .build()
        operations.add(numberOp)

        val emailOp = ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
            .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
            .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE)
            .withValue(ContactsContract.CommonDataKinds.Email.DATA, email)
            .withValue(ContactsContract.CommonDataKinds.Email.TYPE, ContactsContract.CommonDataKinds.Email.TYPE_WORK)
            .build()
        operations.add(emailOp)

        resolver.applyBatch(ContactsContract.AUTHORITY, operations)
    }
}