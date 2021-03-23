package si.uni_lj.fri.pbd.miniapp1.ui.contacts

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import si.uni_lj.fri.pbd.miniapp1.MainViewModel
import si.uni_lj.fri.pbd.miniapp1.R
import si.uni_lj.fri.pbd.miniapp1.databinding.FragmentContactsBinding
import si.uni_lj.fri.pbd.miniapp1.utils.hasPermission
import si.uni_lj.fri.pbd.miniapp1.utils.requestPermissionWithRationale

class ContactsFragment : Fragment() {

    private val model: MainViewModel by activityViewModels()

    private var _binding: FragmentContactsBinding? = null
    private val binding get() = _binding!!

    companion object {
        private const val CONTACTS_READ_REQ_CODE = 100
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentContactsBinding.inflate(inflater, container, false)
        init()
        return binding.root
    }

    private fun init() {
        // show loading spinner
        binding.loading.visibility = View.VISIBLE

        val adapter = ContactsAdapter()
        binding.list.adapter = adapter

        model.contactsLiveData.observe(viewLifecycleOwner, {
            binding.loading.visibility = View.GONE
            adapter.contacts = it
        })

        if (requireContext().hasPermission(Manifest.permission.READ_CONTACTS)) {

            model.fetchContacts(requireContext().contentResolver)
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
            -> model.fetchContacts(requireContext().contentResolver)
        }
    }
}
