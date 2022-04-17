package cn.hx.dialogmanager

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog

class BaseAlertDialog : BaseDialog() {

    private val onClickListener: OnClickListener?
        get() {
            return parentFragment?.let {
                it as? OnClickListener?
            } ?: activity as? OnClickListener?

        }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return context?.run {
            val builder = AlertDialog.Builder(this)
            builder.setTitle(arguments?.getString(TITLE))
                .setMessage(arguments?.getString(MESSAGE))
            arguments?.getString(POSITIVE)?.let {
                builder.setPositiveButton(it) { _, which ->
                    onClickListener?.onClick(this@BaseAlertDialog, which)
                }
            }
            arguments?.getString(NEGATIVE)?.let {
                builder.setNegativeButton(it) { _, which ->
                    onClickListener?.onClick(this@BaseAlertDialog, which)
                }
            }
            arguments?.getString(NEUTRAL)?.let {
                builder.setNeutralButton(it) { _, which ->
                    onClickListener?.onClick(this@BaseAlertDialog, which)
                }
            }
            return builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
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

    //用Fragment/Activity实现些接口的方法，可以保证Activity重建后按钮点击事件有效
    interface OnClickListener {
        fun onClick(dialog: BaseAlertDialog, which: Int)
    }

    companion object {
        private const val TITLE = "title"
        private const val MESSAGE = "message"
        private const val POSITIVE = "positive"
        private const val NEGATIVE = "negative"
        private const val NEUTRAL = "neutral"
    }
}