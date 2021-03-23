package si.uni_lj.fri.pbd.miniapp1.ui.contacts

import android.app.Application
import android.database.Cursor
import android.provider.ContactsContract
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import si.uni_lj.fri.pbd.miniapp1.services.ContactsService

class ContactsViewModel(private val mApplication: Application) : AndroidViewModel(mApplication) {

    private val _contactsLiveData = MutableLiveData<ArrayList<ContactDTO>>()
    val contactsLiveData: LiveData<ArrayList<ContactDTO>> = _contactsLiveData

    fun fetchContacts() {
        viewModelScope.launch {
            val contacts = ContactsService(mApplication).fetchContacts()
            _contactsLiveData.postValue(contacts)
        }
    }

}