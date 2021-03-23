package si.uni_lj.fri.pbd.miniapp1

import android.content.ContentResolver
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.async
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import si.uni_lj.fri.pbd.miniapp1.services.ContactsService
import si.uni_lj.fri.pbd.miniapp1.ui.contacts.ContactDTO

class MainViewModel : ViewModel() {

    private val _contactsLiveData = MutableLiveData<ArrayList<ContactDTO>>()
    val contactsLiveData: LiveData<ArrayList<ContactDTO>> = _contactsLiveData
    private var areContactsFetched = false

    fun fetchContacts(resolver: ContentResolver) {
        viewModelScope.launch {
            if (areContactsFetched) return@launch

            val service = ContactsService(resolver)

            // use coroutines to fetch all data concurrently
            val contactsListAsync = async { service.getPhoneContacts() }
            val contactNumbersAsync = async { service.getContactNumbers() }
            val contactEmailAsync = async { service.getContactEmails() }

            // await for coroutines to resolve
            val contacts = contactsListAsync.await()
            val contactNumbers = contactNumbersAsync.await()
            val contactEmails = contactEmailAsync.await()

            // compile them into a single structure
            compileContacts(contacts, contactNumbers, contactEmails)
            _contactsLiveData.postValue(contacts)
            areContactsFetched = true
        }
    }

    fun getSelectedUserEmails(): Array<String> {
        val emails = contactsLiveData.value
            ?.filter { it.isSelected && it.emails.isNotEmpty() }
            ?.map { it.emails.first() }
        return emails?.toTypedArray() ?: arrayOf()
    }

    fun getSelectedUserNumbers(): Array<String> {
        val numbers = contactsLiveData.value
            ?.filter { it.isSelected && it.numbers.isNotEmpty() }
            ?.map { it.numbers.first() }
        return numbers?.toTypedArray() ?: arrayOf()
    }

    fun refetchContacts(resolver: ContentResolver) {
        areContactsFetched = false
        fetchContacts(resolver)
    }

    private fun compileContacts(
        contacts: ArrayList<ContactDTO>,
        contactNumbers: HashMap<String, ArrayList<String>>,
        contactEmails: HashMap<String, ArrayList<String>>
    ) {
        contacts.forEach {
            contactNumbers[it.id]?.let { numbers -> it.numbers = numbers }
            contactEmails[it.id]?.let { emails -> it.emails = emails }
        }
    }
}