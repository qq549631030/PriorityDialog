package cn.hx.dialogmanager

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import androidx.appcompat.app.AlertDialog

class BaseAlertDialog : BaseDialog(), DialogInterface.OnClickListener {

    private var title: CharSequence? = null
    private var message: CharSequence? = null
    private var positive: CharSequence? = null
    private var negative: CharSequence? = null
    private var neutral: CharSequence? = null

    init {
        savedStateRegistry.registerSavedStateProvider(KEY_ALERT_DIALOG_STATE) {
            Bundle().apply {
                putCharSequence(TITLE, title)
                putCharSequence(MESSAGE, message)
                putCharSequence(POSITIVE, positive)
                putCharSequence(NEGATIVE, negative)
                putCharSequence(NEUTRAL, neutral)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        savedStateRegistry.consumeRestoredStateForKey(KEY_ALERT_DIALOG_STATE)?.run {
            title = getCharSequence(TITLE)
            message = getCharSequence(MESSAGE)
            positive = getCharSequence(POSITIVE)
            negative = getCharSequence(NEGATIVE)
            neutral = getCharSequence(NEUTRAL)
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle(title)
                .setMessage(message)
        positive?.let {
            builder.setPositiveButton(it, this@BaseAlertDialog)
        }
        negative?.let {
            builder.setNegativeButton(it, this@BaseAlertDialog)
        }
        neutral?.let {
            builder.setNeutralButton(it, this@BaseAlertDialog)
        }
        return builder.create()
    }

    override fun onClick(dialog: DialogInterface, which: Int) {
        onDialogEvent(AlertDialogClickEvent(which))
    }

    class Builder {
        private var title: CharSequence? = null
        private var message: CharSequence? = null
        private var positive: CharSequence? = null
        private var negative: CharSequence? = null
        private var neutral: CharSequence? = null

        fun title(title: CharSequence?): Builder {
            this.title = title
            return this
        }

        fun message(message: CharSequence?): Builder {
            this.message = message
            return this
        }

        fun positive(positive: CharSequence?): Builder {
            this.positive = positive
            return this
        }

        fun negative(negative: CharSequence?): Builder {
            this.negative = negative
            return this
        }

        fun neutral(neutral: CharSequence): Builder {
            this.neutral = neutral
            return this
        }

        fun create(): BaseAlertDialog {
            return BaseAlertDialog().also {
                it.title = title
                it.message = message
                it.positive = positive
                it.negative = negative
                it.neutral = neutral
            }
        }
    }

    class AlertDialogClickEvent(val which: Int)

    companion object {

        private const val KEY_ALERT_DIALOG_STATE = "cn.hx.base.alertDialog.state"

        private const val TITLE = "title"
        private const val MESSAGE = "message"
        private const val POSITIVE = "positive"
        private const val NEGATIVE = "negative"
        private const val NEUTRAL = "neutral"
    }
}