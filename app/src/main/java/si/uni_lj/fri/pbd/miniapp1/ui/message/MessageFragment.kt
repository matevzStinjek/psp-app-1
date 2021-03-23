package si.uni_lj.fri.pbd.miniapp1.ui.message

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import si.uni_lj.fri.pbd.miniapp1.R
import si.uni_lj.fri.pbd.miniapp1.databinding.FragmentMessageBinding

class MessageFragment : Fragment() {

    companion object {
        private const val MESSAGE_SUBJECT = "PBD2021 Group Email"
        private const val MESSAGE_BODY = "Send from my Android mini app 1"
    }

    private var _binding: FragmentMessageBinding? = null
    private val binding get() = _binding!!

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
        val mockEmails = arrayOf("asd@gmail.com", "dnf@gmail.com") // TODO: remove
        val intent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:")
            putExtra(Intent.EXTRA_EMAIL, mockEmails)
            putExtra(Intent.EXTRA_SUBJECT, MESSAGE_SUBJECT)
            putExtra(Intent.EXTRA_TEXT, MESSAGE_BODY)
        }
        startActivity(intent)
    }

    private fun startMmsIntent() {
        val mockNumbers = arrayOf("040123123", "050123123") // TODO: remove
//        val intent = Intent(Intent.ACTION_SENDTO).apply {
//            val numbersStr = mockNumbers.joinToString(";")
//            data = Uri.parse("mmsto:$numbersStr")
//            putExtra("sms_body", MESSAGE_BODY)
//            type = "vnd.android-dir/mms-sms"
//        }
//        startActivity(intent)
        val intent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mmsto:")
            putExtra("sms_body", MESSAGE_BODY)
        }
        startActivity(intent)
        TODO("IDK make it work")
    }
}