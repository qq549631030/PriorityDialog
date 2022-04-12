package cn.hx.dialogmanager

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import cn.hx.dialogmanager.databinding.ActivityMainBinding

class MainActivity : BaseActivity(), BaseAlertDialog.OnClickListener {

    private val TAG: String = "MainActivity"

    lateinit var binding: ActivityMainBinding

    lateinit var handler: Handler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        handler = Handler(Looper.getMainLooper())
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.btnPriorityHigh.setOnClickListener {
            showDialog(
                "first dialog",
                "this is the first dialog with priority  = 1\nthis will dismiss when second dialog show",
                1
            )

            handler.postDelayed({
                showDialog(
                    "second dialog",
                    "this is the second dialog with priority  = 2 \nthe first dialog will reshow after this dismissed",
                    2
                )
            }, 3000L)
        }
        binding.btnPriorityLow.setOnClickListener {
            showDialog(
                "first dialog",
                "this is the first dialog with priority  = 2\nthis will stop the second dialog to show\nthe second dialog will show after this dismissed",
                2
            )

            handler.postDelayed({
                showDialog(
                    "second dialog",
                    "this is the second dialog with priority  = 1\nthis show after first dialog dismiss ",
                    1
                )
            }, 3000L)
        }

        binding.btnDismissByUserFalse.setOnClickListener {
            showDialog(
                "first dialog",
                "this is the first dialog with priority  = 1\nand with onlyDismissByUser = false\nthis will dismiss when second dialog show",
                1,
                false
            )

            handler.postDelayed({
                showDialog(
                    "second dialog",
                    "this is the second dialog with priority  = 2\nthe first dialog will not reshow when this dismissed",
                    2
                )
            }, 3000L)
        }

        binding.btnLockWindowForStart.setOnClickListener {
            showDialog(
                "lock window dialog",
                "this dialog with lockWindow  = true\nit will stop start Second Activity\nafter this dismiss the Second Activity will start again",
                lockWindow = true
            )

            handler.post {
                startActivity(
                    Intent(
                        this,
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
                finish()
            }
        }
        binding.btnLockWindowOnFragment.setOnClickListener {
            startActivity(Intent(this, FragmentTestActivity::class.java))
        }
    }

    override fun onDestroy() {
        handler.removeCallbacksAndMessages(null)
        super.onDestroy()
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
        dialog.showBaseDialog(supportFragmentManager)
    }

    override fun onClick(dialog: BaseAlertDialog, which: Int) {
        Log.d(
            TAG,
            "onClick() called with:activity = $this dialog = $dialog, which = $which"
        )
    }
}