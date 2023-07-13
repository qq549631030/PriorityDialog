package cn.hx.dialogmanager

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import cn.hx.dialogmanager.databinding.ActivityMainBinding
import cn.hx.prioritydialog.PriorityDialog

class MainActivity : BaseActivity() {

    lateinit var binding: ActivityMainBinding

    lateinit var handler: Handler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        handler = Handler(Looper.getMainLooper())
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.btnPriorityHigh.setOnClickListener {
            showAlertDialog(
                    "first dialog",
                    "this is the first dialog with priority  = 1\nthis will dismiss when second dialog show",
                    1
            )

            handler.postDelayed({
                showAlertDialog(
                        "second dialog",
                        "this is the second dialog with priority  = 2 \nthe first dialog will reshow after this dismissed",
                        2
                )
            }, 3000L)
        }
        binding.btnPriorityLow.setOnClickListener {
            showAlertDialog(
                    "first dialog",
                    "this is the first dialog with priority  = 2\nthis will stop the second dialog to show\nthe second dialog will show after this dismissed",
                    2
            )

            handler.postDelayed({
                showAlertDialog(
                        "second dialog",
                        "this is the second dialog with priority  = 1\nthis show after first dialog dismiss ",
                        1
                )
            }, 3000L)
        }

        binding.btnDismissByUserFalse.setOnClickListener {
            showAlertDialog(
                    "first dialog",
                    "this is the first dialog with priority  = 1\nand with isAddToPendingWhenReplaceByOther = false\nthis will dismiss when second dialog show",
                    1,
                    false
            )

            handler.postDelayed({
                showAlertDialog(
                        "second dialog",
                        "this is the second dialog with priority  = 2\nthe first dialog will not reshow when this dismissed",
                        2
                )
            }, 3000L)
        }
        binding.btnUnSupportRecreate.setOnClickListener {
            showAlertDialog(
                    "first dialog",
                    "this is the first dialog with  with isSupportRecreate = false\nthis will dismiss when recreate",
                    isSupportRecreate = false
            )
        }
        binding.btnForStart.setOnClickListener {
            showAlertDialog(
                    "first dialog",
                    "this is the first dialog with priority  = 1\nthis will dismiss when second dialog show",
                    1,
                    lockWindow = true
            )

            handler.postDelayed({
                showAlertDialog(
                        "second dialog starter",
                        "this is the second dialog with priority  = 2 \nthe first dialog will reshow after this dismissed",
                        2,
                        uuid = "mock_uuid_1"
                )
            }, 3000L)

        }
        binding.btnLockWindowForStart.setOnClickListener {
            showAlertDialog(
                    "lock window dialog",
                    "this dialog with lockWindow  = true\nit will stop start Second Activity\nafter this dismiss the Second Activity will start again",
                    lockWindow = true
            )

            handler.post {
                startActivity(Intent(this, SecondActivity::class.java))
            }
        }

        binding.btnLockWindowForFinish.setOnClickListener {
            showAlertDialog(
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

        binding.btnLockShowTwo.setOnClickListener {
            showAlertDialog(
                    "first dialog",
                    "this is the first dialog with priority  = 1\nthis will not dismiss when second dialog show\nthis will not dismiss when second dialog show\nthis will not dismiss when second dialog show\nthis will not dismiss when second dialog show\n",
                    1,
                    isCanBeReplace = false
            )

            handler.postDelayed({
                showAlertDialog(
                        "second dialog",
                        "this is the second dialog with priority  = 2",
                        2
                )
            }, 1000L)
        }
    }

    override fun onDestroy() {
        handler.removeCallbacksAndMessages(null)
        super.onDestroy()
    }

    override fun onDialogEvent(priorityDialog: PriorityDialog, event: Any) {
        Log.d(TAG, "onDialogEvent() called with: priorityDialog = $priorityDialog, event = $event")
        if (event is BaseAlertDialog.AlertDialogClickEvent) {
            if (priorityDialog.priorityConfig.uuid == "mock_uuid_1" && event.which == DialogInterface.BUTTON_POSITIVE) {
                startActivity(Intent(this, SecondActivity::class.java))
            }
        }
    }
}