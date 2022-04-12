package cn.hx.dialogmanager

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import cn.hx.dialogmanager.databinding.FragmentTestBinding

class TestFragment : BaseFragment(R.layout.fragment_test) {


    private var _binding: FragmentTestBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!


    lateinit var handler: Handler
    override fun onAttach(context: Context) {
        super.onAttach(context)
        handler = Handler(Looper.getMainLooper())
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentTestBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnLockWindowForStart.setOnClickListener {
            showDialog(
                "lock window dialog",
                "this dialog with lockWindow  = true\nit will stop start Second Activity\nafter this dismiss the Second Activity will start again",
                lockWindow = true
            )

            handler.post {
                startActivity(
                    Intent(
                        requireContext(),
                        SecondActivity::class.java
                    )
                )
            }
        }
        binding.btnLockWindowForFinish.setOnClickListener {
            showDialog(
                "lock window dialog",
                "this dialog with lockWindow  = true\nit will stop finish current Activity\nafter this dismiss the this Activity will finish",
                lockWindow = true
            )

            handler.post {
                activity?.finish()
            }
        }
    }


    private fun showDialog(
        title: String,
        message: String,
        priority: Int = 0,
        onlyDismissByUser: Boolean = true,
        lockWindow: Boolean = false
    ) {
        val dialog = BaseAlertDialog.Builder()
            .title(title)
            .message(message)
            .positive("Confirm")
            .negative("Cancel")
            .create()
        dialog.priority = priority
        dialog.onlyDismissByUser = onlyDismissByUser
        dialog.lockWindow = lockWindow
        dialog.showBaseDialog(childFragmentManager)
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onDetach() {
        handler.removeCallbacksAndMessages(null)
        super.onDetach()
    }
}