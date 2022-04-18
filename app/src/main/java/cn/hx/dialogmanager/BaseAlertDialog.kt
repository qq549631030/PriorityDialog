package cn.hx.dialogmanager

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import androidx.appcompat.app.AlertDialog

class BaseAlertDialog : BaseDialog(), DialogInterface.OnClickListener {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return context?.run {
            val builder = AlertDialog.Builder(this)
            builder.setTitle(arguments?.getString(TITLE))
                    .setMessage(arguments?.getString(MESSAGE))
            arguments?.getString(POSITIVE)?.let {
                builder.setPositiveButton(it, this@BaseAlertDialog)
            }
            arguments?.getString(NEGATIVE)?.let {
                builder.setNegativeButton(it, this@BaseAlertDialog)
            }
            arguments?.getString(NEUTRAL)?.let {
                builder.setNeutralButton(it, this@BaseAlertDialog)
            }
            return builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
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
            return BaseAlertDialog().apply {
                arguments = Bundle().apply {
                    putCharSequence(TITLE, title)
                    putCharSequence(MESSAGE, message)
                    putCharSequence(POSITIVE, positive)
                    putCharSequence(NEGATIVE, negative)
                    putCharSequence(NEUTRAL, neutral)
                }
            }
        }
    }

    class AlertDialogClickEvent(val which: Int)

    companion object {
        private const val TITLE = "title"
        private const val MESSAGE = "message"
        private const val POSITIVE = "positive"
        private const val NEGATIVE = "negative"
        private const val NEUTRAL = "neutral"
    }
}