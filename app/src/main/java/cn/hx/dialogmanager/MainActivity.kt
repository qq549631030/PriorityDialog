package cn.hx.dialogmanager

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import cn.hx.dialogmanager.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity(), BaseAlertDialog.OnClickListener {

    lateinit var binding: ActivityMainBinding

    lateinit var handler: Handler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        handler = Handler(Looper.getMainLooper())
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.btnShow.setOnClickListener {
            showDialog(
                "first dialog",
                "this is the first dialog\nthis is the first dialog\nthis is the first dialog\nthis is the first dialog\nthis is the first dialog\n",
                1
            )

            handler.postDelayed({
                showDialog("second dialog", "this is the second dialog", 2)
            }, 3000L)
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
        onlyDismissByUser: Boolean = true
    ) {
        Log.d("huangx", "showDialog() activity = $this")
        val dialog = BaseAlertDialog.Builder()
            .title(title)
            .message(message)
            .positive("Confirm")
            .negative("Cancel")
            .create()
        dialog.priority = priority
        dialog.onlyDismissByUser = onlyDismissByUser
        dialog.showBaseDialog(supportFragmentManager)
    }

    override fun onClick(dialog: BaseAlertDialog, which: Int) {
        Log.d(
            "MainActivity",
            "onClick() called with:activity = $this dialog = $dialog, which = $which"
        )
    }
}