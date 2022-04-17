package cn.hx.dialogmanager

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import cn.hx.dialogmanager.databinding.FragmentSecondBinding

class SecondFragment : BaseFragment() {

    private var _binding: FragmentSecondBinding? = null

    private val binding get() = _binding!!

    lateinit var handler: Handler
    override fun onAttach(context: Context) {
        super.onAttach(context)
        handler = Handler(Looper.getMainLooper())
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentSecondBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnPop.setOnClickListener {
            showDialog(
                    "first dialog",
                    "this is the first dialog with priority  = 1\nthis will lock the window\nthe follow popBackStack will be stop and will restart after this dialog dismiss",
                    1,
                    lockWindow = true
            )
            handler.post {
                warpParentFragmentManager.popBackStack()
            }
        }
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