package si.uni_lj.fri.pbd.miniapp1.ui.contacts

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ContactsViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is contacts Fragment"
    }

//    private val contacts = MutableLiveData<List<>>

    val text: LiveData<String> = _text
}