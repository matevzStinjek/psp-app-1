package si.uni_lj.fri.pbd.miniapp1.ui.contacts

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import com.google.android.material.floatingactionbutton.FloatingActionButton
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
        private const val CREATE_CONTACT_ACTIVITY_CODE = 200
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

        // setup fab
        val fab: FloatingActionButton = binding.fab
        fab.show()
        fab.setOnClickListener { onFabClick() }

        // setup adapter
        val adapter = ContactsAdapter()
        binding.list.adapter = adapter

        // setup live data observer
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
            getString(R.string.contact_read_permission_rationale),
            getString(R.string.contact_permission_title),
        )
    }

    private fun onFabClick() {
        val intent = Intent(context, AddContactActivity::class.java)
        startActivityForResult(intent, CREATE_CONTACT_ACTIVITY_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when {
            requestCode == CREATE_CONTACT_ACTIVITY_CODE && resultCode == AppCompatActivity.RESULT_OK -> {
                model.refetchContacts(requireContext().contentResolver)
                view?.findNavController()?.navigate(R.id.nav_home)
            }
        }
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
