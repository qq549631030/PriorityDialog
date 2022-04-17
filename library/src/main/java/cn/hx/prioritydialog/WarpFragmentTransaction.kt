package cn.hx.prioritydialog

import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.Lifecycle

class WarpFragmentTransaction(private val transaction: FragmentTransaction, private val dialogHost: AbsDialogHostImpl) : FragmentTransaction() {

    var type = TYPE_COMMIT

    override fun commit(): Int {
        if (dialogHost.isWindowLockedByDialog()) {
            type = TYPE_COMMIT
            // add to pending, this will lose the returned backStackIndex
            dialogHost.pendingTransaction = this
            return -1
        }
        return transaction.commit()
    }

    override fun commitAllowingStateLoss(): Int {
        if (dialogHost.isWindowLockedByDialog()) {
            type = TYPE_COMMIT_ALLOWING_STATE_LOSS
            // add to pending, this will lose the returned backStackIndex
            dialogHost.pendingTransaction = this
            return -1
        }
        return transaction.commitAllowingStateLoss()
    }

    override fun commitNow() {
        if (dialogHost.isWindowLockedByDialog()) {
            type = TYPE_COMMIT_NOW
            // add to pending
            dialogHost.pendingTransaction = this
            return
        }
        transaction.commitNow()
    }

    override fun commitNowAllowingStateLoss() {
        if (dialogHost.isWindowLockedByDialog()) {
            type = TYPE_COMMIT_NOW_ALLOWING_STATE_LOSS
            // add to pending
            dialogHost.pendingTransaction = this
            return
        }
        transaction.commitNowAllowingStateLoss()
    }

    fun tryPendingAction(): Boolean {
        if (dialogHost.isWindowLockedByDialog()) {
            return false
        }
        when (type) {
            TYPE_COMMIT -> {
                commit()
            }
            TYPE_COMMIT_ALLOWING_STATE_LOSS -> {
                commitAllowingStateLoss()
            }
            TYPE_COMMIT_NOW -> {
                commitNow()
            }
            TYPE_COMMIT_NOW_ALLOWING_STATE_LOSS -> {
                commitNowAllowingStateLoss()
            }
            else -> {}
        }
        return true
    }

    override fun add(fragment: Fragment, tag: String?): FragmentTransaction {
        transaction.add(fragment, tag)
        return this
    }

    override fun add(containerViewId: Int, fragment: Fragment): FragmentTransaction {
        transaction.add(containerViewId, fragment)
        return this
    }

    override fun add(containerViewId: Int, fragment: Fragment, tag: String?): FragmentTransaction {
        transaction.add(containerViewId, fragment, tag)
        return this
    }


    override fun replace(containerViewId: Int, fragment: Fragment): FragmentTransaction {
        transaction.replace(containerViewId, fragment)
        return this
    }

    override fun replace(containerViewId: Int, fragment: Fragment, tag: String?): FragmentTransaction {
        transaction.replace(containerViewId, fragment, tag)
        return this
    }

    override fun remove(fragment: Fragment): FragmentTransaction {
        transaction.remove(fragment)
        return this
    }

    override fun hide(fragment: Fragment): FragmentTransaction {
        transaction.hide(fragment)
        return this
    }

    override fun show(fragment: Fragment): FragmentTransaction {
        transaction.show(fragment)
        return this
    }

    override fun detach(fragment: Fragment): FragmentTransaction {
        transaction.detach(fragment)
        return this
    }

    override fun attach(fragment: Fragment): FragmentTransaction {
        transaction.attach(fragment)
        return this
    }

    override fun setPrimaryNavigationFragment(fragment: Fragment?): FragmentTransaction {
        transaction.setPrimaryNavigationFragment(fragment)
        return this
    }

    override fun setMaxLifecycle(fragment: Fragment, state: Lifecycle.State): FragmentTransaction {
        transaction.setMaxLifecycle(fragment, state)
        return this
    }

    override fun isEmpty(): Boolean {
        return transaction.isEmpty()
    }

    override fun setCustomAnimations(enter: Int, exit: Int): FragmentTransaction {
        transaction.setCustomAnimations(enter, exit)
        return this
    }

    override fun setCustomAnimations(enter: Int, exit: Int, popEnter: Int, popExit: Int): FragmentTransaction {
        transaction.setCustomAnimations(enter, exit, popEnter, popExit)
        return this
    }

    override fun addSharedElement(sharedElement: View, name: String): FragmentTransaction {
        transaction.addSharedElement(sharedElement, name)
        return this
    }

    override fun setTransition(transition: Int): FragmentTransaction {
        transaction.setTransition(transition)
        return this
    }

    override fun setTransitionStyle(styleRes: Int): FragmentTransaction {
        transaction.setTransitionStyle(styleRes)
        return this
    }

    override fun addToBackStack(name: String?): FragmentTransaction {
        transaction.addToBackStack(name)
        return this
    }

    override fun isAddToBackStackAllowed(): Boolean {
        return transaction.isAddToBackStackAllowed()
    }

    override fun disallowAddToBackStack(): FragmentTransaction {
        transaction.disallowAddToBackStack()
        return this
    }

    override fun setBreadCrumbTitle(res: Int): FragmentTransaction {
        transaction.setBreadCrumbTitle(res)
        return this
    }

    override fun setBreadCrumbTitle(text: CharSequence?): FragmentTransaction {
        transaction.setBreadCrumbTitle(text)
        return this
    }

    override fun setBreadCrumbShortTitle(res: Int): FragmentTransaction {
        transaction.setBreadCrumbShortTitle(res)
        return this
    }

    override fun setBreadCrumbShortTitle(text: CharSequence?): FragmentTransaction {
        transaction.setBreadCrumbShortTitle(text)
        return this
    }

    override fun setReorderingAllowed(reorderingAllowed: Boolean): FragmentTransaction {
        transaction.setReorderingAllowed(reorderingAllowed)
        return this
    }

    override fun setAllowOptimization(allowOptimization: Boolean): FragmentTransaction {
        transaction.setAllowOptimization(allowOptimization)
        return this
    }

    override fun runOnCommit(runnable: Runnable): FragmentTransaction {
        transaction.runOnCommit(runnable)
        return this
    }

    companion object {
        const val TYPE_COMMIT = 1
        const val TYPE_COMMIT_ALLOWING_STATE_LOSS = 2
        const val TYPE_COMMIT_NOW = 3
        const val TYPE_COMMIT_NOW_ALLOWING_STATE_LOSS = 4
    }
}