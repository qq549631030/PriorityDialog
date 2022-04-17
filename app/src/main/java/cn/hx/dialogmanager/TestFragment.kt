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

class TestFragment : BaseFragment() {

    private var _binding: FragmentTestBinding? = null

    private val binding get() = _binding!!


    lateinit var handler: Handler
    override fun onAttach(context: Context) {
        super.onAttach(context)
        handler = Handler(Looper.getMainLooper())
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentTestBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnLockWindowForStart.setOnClickListener {
            showAlertDialog(
                    "lock window dialog",
                    "this dialog with lockWindow  = true\nit will stop start Second Activity\nafter this dismiss the Second Activity will start again",
                    lockWindow = true
            )

            handler.post {
                startActivity(Intent(requireContext(), SecondActivity::class.java))
            }
        }
        binding.btnLockWindowForFinish.setOnClickListener {
            showAlertDialog(
                    "lock window dialog",
                    "this dialog with lockWindow  = true\nit will stop finish current Activity\nafter this dismiss the this Activity will finish",
                    lockWindow = true
            )

            handler.post {
                activity?.finish()
            }
        }
        binding.btnReplaceByOther.setOnClickListener {
            showAlertDialog(
                    "first dialog",
                    "this is the first dialog with priority  = 1\nthis will dismiss when second dialog show",
                    1
            )

            handler.postDelayed({
                showAlertDialog(
                        "second dialog",
                        "this is the second dialog with priority  = 2 \nthe first dialog will reshow after this dismissed",
                        2,
                        lockWindow = true
                )
            }, 3000L)
            handler.postDelayed({
                warpParentFragmentManager.beginTransaction().replace(R.id.container, OtherFragment()).commit()
            }, 6000)
        }

        binding.btnReplaceByOtherToBack.setOnClickListener {
            showAlertDialog(
                    "first dialog",
                    "this is the first dialog with priority  = 1\nthis will dismiss when second dialog show",
                    1
            )

            handler.postDelayed({
                showAlertDialog(
                        "second dialog",
                        "this is the second dialog with priority  = 2 \nthe first dialog will reshow after this dismissed",
                        2,
                        lockWindow = true
                )
            }, 3000L)
            handler.postDelayed({
                warpParentFragmentManager.beginTransaction().replace(R.id.container, SecondFragment()).addToBackStack(null).commit()
            }, 6000)
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