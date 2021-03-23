package si.uni_lj.fri.pbd.miniapp1.ui.contacts

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Patterns
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import si.uni_lj.fri.pbd.miniapp1.R
import si.uni_lj.fri.pbd.miniapp1.databinding.ActivityAddContactBinding
import si.uni_lj.fri.pbd.miniapp1.services.ContactsService
import si.uni_lj.fri.pbd.miniapp1.utils.hasPermission
import si.uni_lj.fri.pbd.miniapp1.utils.requestPermissionWithRationale


class AddContactActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddContactBinding

    companion object {
        private const val CONTACTS_WRITE_REQ_CODE = 300
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddContactBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.button.setOnClickListener { onButtonClick() }
    }

    private fun onButtonClick() {
        val contact: NewContactDTO = createNewContactDTO()
        val invalidReasons = getInvalidReasons(contact)
        if (invalidReasons.isNotEmpty()) {
            val invalidReasonsStr = invalidReasons.joinToString("\n")
            showSnackbar(invalidReasonsStr)
            return
        }

        if (!hasPermission(Manifest.permission.WRITE_CONTACTS)) {
            requestPermissionWithRationale(
                Manifest.permission.WRITE_CONTACTS,
                CONTACTS_WRITE_REQ_CODE,
                getString(R.string.contact_write_permission_rationale),
                getString(R.string.contact_permission_title),
            )
            return
        }

        try {
            ContactsService(contentResolver).insertContact(contact)
            val returnIntent = Intent()
            setResult(RESULT_OK, returnIntent)
            finish()
        } catch (e: Exception) {
            e.printStackTrace()
            showSnackbar(e.message.toString())
        }
    }

    private fun createNewContactDTO(): NewContactDTO {
        val name = binding.name.text.trim().toString()
        val email = binding.email.text.trim().toString()
        val number = binding.number.text.trim().toString()
        return NewContactDTO(name, email, number)
    }

    private fun getInvalidReasons(contact: NewContactDTO): ArrayList<String> {
        val (name, email, number) = contact
        val reasons = ArrayList<String>()

        if (name.isEmpty()) {
            reasons.add("Name is required")
        }
        if (email.isEmpty()) {
            reasons.add("Email is required")
        }
        if (email.isNotEmpty() && !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            reasons.add("Email format is invalid")
        }
        if (number.isEmpty()) {
            reasons.add("Number is required")
        }
        if (number.isNotEmpty() && !Patterns.PHONE.matcher(number).matches()) {
            reasons.add("Number format is invalid")
        }

        return reasons
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when {
            requestCode == CONTACTS_WRITE_REQ_CODE && grantResults[0] == PackageManager.PERMISSION_GRANTED
            -> onButtonClick()
        }
    }

    fun showSnackbar(msg: String) {
        Snackbar.make(binding.root, msg, Snackbar.LENGTH_LONG).show()
    }
}