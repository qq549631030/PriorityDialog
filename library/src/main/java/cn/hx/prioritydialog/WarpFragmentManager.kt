package cn.hx.prioritydialog

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import java.io.FileDescriptor
import java.io.PrintWriter

class WarpFragmentManager(private val fragmentManager: FragmentManager, private val dialogHost: AbsDialogHostImpl) : FragmentManager() {
    override fun beginTransaction(): FragmentTransaction {
        return WarpFragmentTransaction(fragmentManager.beginTransaction(), dialogHost)
    }

    override fun executePendingTransactions(): Boolean {
        return fragmentManager.executePendingTransactions()
    }

    override fun findFragmentById(id: Int): Fragment? {
        return fragmentManager.findFragmentById(id)
    }

    override fun findFragmentByTag(tag: String?): Fragment? {
        return fragmentManager.findFragmentByTag(tag)
    }

    override fun popBackStack() {
        if (dialogHost.isWindowLockedByDialog()) {
            dialogHost.pendingPopBackStack = Bundle().apply {
                putInt(POP_BACK_STACK_TYPE, TYPE_POP)
            }
            return
        }
        fragmentManager.popBackStack()
    }

    override fun popBackStack(name: String?, flags: Int) {
        if (dialogHost.isWindowLockedByDialog()) {
            dialogHost.pendingPopBackStack = Bundle().apply {
                putInt(POP_BACK_STACK_TYPE, TYPE_POP_WITH_NAME)
                putString(POP_BACK_STACK_NAME, name)
                putInt(POP_BACK_STACK_FLAG, flags)
            }
            return
        }
        return fragmentManager.popBackStack(name, flags)
    }

    override fun popBackStack(id: Int, flags: Int) {
        if (dialogHost.isWindowLockedByDialog()) {
            dialogHost.pendingPopBackStack = Bundle().apply {
                putInt(POP_BACK_STACK_TYPE, TYPE_POP_WITH_ID)
                putInt(POP_BACK_STACK_ID, id)
                putInt(POP_BACK_STACK_FLAG, flags)
            }
            return
        }
        fragmentManager.popBackStack(id, flags)
    }

    override fun popBackStackImmediate(): Boolean {
        if (dialogHost.isWindowLockedByDialog()) {
            // add to pending, this will lose the returned result
            dialogHost.pendingPopBackStack = Bundle().apply {
                putInt(POP_BACK_STACK_TYPE, TYPE_POP_IMMEDIATE)
            }
            return false
        }
        return fragmentManager.popBackStackImmediate()
    }

    override fun popBackStackImmediate(name: String?, flags: Int): Boolean {
        if (dialogHost.isWindowLockedByDialog()) {
            // add to pending, this will lose the returned result
            dialogHost.pendingPopBackStack = Bundle().apply {
                putInt(POP_BACK_STACK_TYPE, TYPE_POP_IMMEDIATE_WITH_NAME)
                putString(POP_BACK_STACK_NAME, name)
                putInt(POP_BACK_STACK_FLAG, flags)
            }
            return false
        }
        return fragmentManager.popBackStackImmediate(name, flags)
    }

    override fun popBackStackImmediate(id: Int, flags: Int): Boolean {
        if (dialogHost.isWindowLockedByDialog()) {
            // add to pending, this will lose the returned result
            dialogHost.pendingPopBackStack = Bundle().apply {
                putInt(POP_BACK_STACK_TYPE, TYPE_POP_IMMEDIATE_WITH_ID)
                putInt(POP_BACK_STACK_ID, id)
                putInt(POP_BACK_STACK_FLAG, flags)
            }
            return false
        }
        return fragmentManager.popBackStackImmediate(id, flags)
    }

    override fun getBackStackEntryCount(): Int {
        return fragmentManager.backStackEntryCount
    }

    override fun getBackStackEntryAt(index: Int): BackStackEntry {
        return fragmentManager.getBackStackEntryAt(index)
    }

    override fun addOnBackStackChangedListener(listener: OnBackStackChangedListener) {
        fragmentManager.addOnBackStackChangedListener(listener)
    }

    override fun removeOnBackStackChangedListener(listener: OnBackStackChangedListener) {
        fragmentManager.removeOnBackStackChangedListener(listener)
    }

    override fun putFragment(bundle: Bundle, key: String, fragment: Fragment) {
        fragmentManager.putFragment(bundle, key, fragment)
    }

    override fun getFragment(bundle: Bundle, key: String): Fragment? {
        return fragmentManager.getFragment(bundle, key)
    }

    override fun getFragments(): MutableList<Fragment> {
        return fragmentManager.fragments
    }

    override fun saveFragmentInstanceState(f: Fragment): Fragment.SavedState? {
        return fragmentManager.saveFragmentInstanceState(f)
    }

    override fun isDestroyed(): Boolean {
        return fragmentManager.isDestroyed
    }

    override fun registerFragmentLifecycleCallbacks(cb: FragmentLifecycleCallbacks, recursive: Boolean) {
        fragmentManager.registerFragmentLifecycleCallbacks(cb, recursive)
    }

    override fun unregisterFragmentLifecycleCallbacks(cb: FragmentLifecycleCallbacks) {
        fragmentManager.unregisterFragmentLifecycleCallbacks(cb)
    }

    override fun getPrimaryNavigationFragment(): Fragment? {
        return fragmentManager.primaryNavigationFragment
    }

    override fun dump(prefix: String, fd: FileDescriptor?, writer: PrintWriter, args: Array<out String>?) {
        fragmentManager.dump(prefix, fd, writer, args)
    }

    override fun isStateSaved(): Boolean {
        return fragmentManager.isStateSaved
    }

    override fun setFragmentFactory(fragmentFactory: FragmentFactory) {
        fragmentManager.fragmentFactory = fragmentFactory
    }

    override fun getFragmentFactory(): FragmentFactory {
        return fragmentManager.fragmentFactory
    }

    fun tryPendingAction(pendingPopBackStack: Bundle): Boolean {
        if (dialogHost.isWindowLockedByDialog()) {
            return false
        }
        val type = pendingPopBackStack.getInt(POP_BACK_STACK_TYPE)
        val name = pendingPopBackStack.getString(POP_BACK_STACK_NAME)
        val id = pendingPopBackStack.getInt(POP_BACK_STACK_ID)
        val flags = pendingPopBackStack.getInt(POP_BACK_STACK_FLAG)
        when (type) {
            TYPE_POP -> {
                popBackStack()
            }
            TYPE_POP_WITH_NAME -> {
                popBackStack(name, flags)
            }
            TYPE_POP_WITH_ID -> {
                popBackStack(id, flags)
            }
            TYPE_POP_IMMEDIATE -> {
                popBackStackImmediate()
            }
            TYPE_POP_IMMEDIATE_WITH_NAME -> {
                popBackStackImmediate(name, flags)
            }
            TYPE_POP_IMMEDIATE_WITH_ID -> {
                popBackStackImmediate(id, flags)
            }
            else -> {}
        }
        return true
    }

    companion object {
        const val POP_BACK_STACK_TYPE = "pop_back_stack_type"
        const val POP_BACK_STACK_NAME = "pop_back_stack_name"
        const val POP_BACK_STACK_ID = "pop_back_stack_id"
        const val POP_BACK_STACK_FLAG = "pop_back_stack_flag"

        const val TYPE_POP = 1
        const val TYPE_POP_WITH_NAME = 2
        const val TYPE_POP_WITH_ID = 3
        const val TYPE_POP_IMMEDIATE = 4
        const val TYPE_POP_IMMEDIATE_WITH_NAME = 5
        const val TYPE_POP_IMMEDIATE_WITH_ID = 6
    }
}