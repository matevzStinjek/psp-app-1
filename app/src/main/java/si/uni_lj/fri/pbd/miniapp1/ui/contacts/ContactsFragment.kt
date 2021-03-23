package si.uni_lj.fri.pbd.miniapp1.ui.contacts

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import si.uni_lj.fri.pbd.miniapp1.R
import si.uni_lj.fri.pbd.miniapp1.databinding.FragmentContactsBinding
import si.uni_lj.fri.pbd.miniapp1.databinding.FragmentMessageBinding
import si.uni_lj.fri.pbd.miniapp1.utils.hasPermission
import si.uni_lj.fri.pbd.miniapp1.utils.requestPermissionWithRationale

class ContactsFragment : Fragment() {

    private lateinit var contactsViewModel: ContactsViewModel

    private var _binding: FragmentContactsBinding? = null
    private val binding get() = _binding!!

    companion object {
        private const val CONTACTS_READ_REQ_CODE = 100
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        contactsViewModel = ViewModelProvider(this).get(ContactsViewModel::class.java)
        _binding = FragmentContactsBinding.inflate(inflater, container, false)
        init()
        return binding.root
    }

    private fun init() {
        // show loading spinner
        binding.loading.visibility = View.VISIBLE

        val adapter = ContactsAdapter()
        binding.list.adapter = adapter

        contactsViewModel.contactsLiveData.observe(viewLifecycleOwner, Observer {
            binding.loading.visibility = View.GONE
            adapter.contacts = it
        })

        if (requireContext().hasPermission(Manifest.permission.READ_CONTACTS)) {
            contactsViewModel.fetchContacts()
            return
        }

        requireActivity().requestPermissionWithRationale(
            Manifest.permission.READ_CONTACTS,
            CONTACTS_READ_REQ_CODE,
            getString(R.string.contact_permission_rationale),
            getString(R.string.contact_permission_title),
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when {
            requestCode == CONTACTS_READ_REQ_CODE && grantResults[0] == PackageManager.PERMISSION_GRANTED
            -> contactsViewModel.fetchContacts()
        }
    }
}
