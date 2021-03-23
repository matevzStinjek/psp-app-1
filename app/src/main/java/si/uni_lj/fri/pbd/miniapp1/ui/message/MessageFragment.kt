package si.uni_lj.fri.pbd.miniapp1.ui.message

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.google.android.material.snackbar.Snackbar
import si.uni_lj.fri.pbd.miniapp1.MainViewModel
import si.uni_lj.fri.pbd.miniapp1.databinding.FragmentMessageBinding

class MessageFragment : Fragment() {

    private val model: MainViewModel by activityViewModels()

    private var _binding: FragmentMessageBinding? = null
    private val binding get() = _binding!!

    companion object {
        private const val MESSAGE_SUBJECT = "PBD2021 Group Email"
        private const val MESSAGE_BODY = "Send from my Android mini app 1"
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMessageBinding.inflate(inflater, container, false)

        binding.emailBtn.setOnClickListener { startEmailIntent() }
        binding.mmsBtn.setOnClickListener { startMmsIntent() }

        return binding.root
    }

    private fun startEmailIntent() {
        val emails = model.getSelectedUserEmails()
        if (emails.isEmpty()) {
            showToast( "None of the selected users have a set email")
            return
        }
        val intent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:")
            putExtra(Intent.EXTRA_EMAIL, emails)
            putExtra(Intent.EXTRA_SUBJECT, MESSAGE_SUBJECT)
            putExtra(Intent.EXTRA_TEXT, MESSAGE_BODY)
        }
        startActivity(intent)
    }

    private fun startMmsIntent() {
        val numbers = model.getSelectedUserNumbers()
        if (numbers.isEmpty()) {
            showToast( "None of the selected users have a set phone number")
            return
        }
        val intent = Intent(Intent.ACTION_SENDTO).apply {
            val numbersStr = numbers.joinToString(";")
            data = Uri.parse("mmsto:$numbersStr")
            putExtra("sms_body", MESSAGE_BODY)
        }
        startActivity(intent)
    }

    private fun showToast(text: String) {
        Snackbar.make(binding.root, text, Snackbar.LENGTH_LONG).show()
    }
}