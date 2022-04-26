package cn.hx.prioritydialog;

import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentFactory;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.savedstate.SavedStateRegistry;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class FragmentUtil {

    @Nullable
    static Parcelable saveFragment(@NonNull Fragment fragment) {
        try {
            Fragment.SavedState savedState;
            Object mState = null;
            Field fragmentStateField = Fragment.class.getDeclaredField("mState");
            fragmentStateField.setAccessible(true);
            int state = fragmentStateField.getInt(fragment);
            FragmentManager fragmentManager = fragment.getFragmentManager();
            if (fragmentManager != null && state > -1) {
                savedState = fragmentManager.saveFragmentInstanceState(fragment);
                Class<?> savedStateClass = Class.forName("androidx.fragment.app.Fragment$SavedState");
                if (savedState != null) {
                    Field mStateField = savedStateClass.getDeclaredField("mState");
                    mStateField.setAccessible(true);
                    mState = mStateField.get(savedState);
                }

            } else {//not attached
                Bundle outState = new Bundle();
                fragment.onSaveInstanceState(outState);
                SavedStateRegistry savedStateRegistry = fragment.getSavedStateRegistry();
                Method performSaveMethod = SavedStateRegistry.class.getDeclaredMethod("performSave", Bundle.class);
                performSaveMethod.setAccessible(true);
                performSaveMethod.invoke(savedStateRegistry, outState);
                mState = outState;
            }
            Class<?> fragmentStateClass = Class.forName("androidx.fragment.app.FragmentState");
            Constructor<?> constructor = fragmentStateClass.getDeclaredConstructor(Fragment.class);
            constructor.setAccessible(true);
            Object fragmentState = constructor.newInstance(fragment);
            if (mState != null) {
                Field mSavedFragmentStateField = fragmentStateClass.getDeclaredField("mSavedFragmentState");
                mSavedFragmentStateField.setAccessible(true);
                mSavedFragmentStateField.set(fragmentState, mState);
            }
            return (Parcelable) fragmentState;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Nullable
    static Fragment restoreFragment(@Nullable Object fragmentState, @NonNull FragmentManager fragmentManager) {
        try {
            Class<?> fragmentStateManagerClass = Class.forName("androidx.fragment.app.FragmentStateManager");
            if (!hasDeclaredField(fragmentStateManagerClass, "mFragmentStore")) {
                //fragment 1.2.x
                return restoreFragment1_2(fragmentState, fragmentManager);
            } else {
                //fragment 1.3.x
                return restoreFragment1_3(fragmentState, fragmentManager);
            }
        } catch (ClassNotFoundException e) {
            //fragment 1.1.x
            return restoreFragment1_1(fragmentState, fragmentManager);
        }
    }

    @Nullable
    private static Fragment restoreFragment1_1(@Nullable Object fragmentState, @NonNull FragmentManager fragmentManager) {
        try {
            String who = getWhoFromFragmentState(fragmentState);
            if (who != null) {
                Class<?> fragmentManagerImplClass = Class.forName("androidx.fragment.app.FragmentManagerImpl");
                Method findFragmentByWhoMethod = fragmentManagerImplClass.getDeclaredMethod("findFragmentByWho", String.class);
                findFragmentByWhoMethod.setAccessible(true);
                Fragment fragment = (Fragment) findFragmentByWhoMethod.invoke(fragmentManager, who);
                if (fragment != null) {
                    return fragment;
                }
                Field mHostField = fragmentManagerImplClass.getDeclaredField("mHost");
                mHostField.setAccessible(true);
                Object fragmentHostCallback = mHostField.get(fragmentManager);
                Class<?> fragmentHostCallbackClass = Class.forName("androidx.fragment.app.FragmentHostCallback");
                Method getContextMethod = fragmentHostCallbackClass.getDeclaredMethod("getContext");
                getContextMethod.setAccessible(true);
                Context context = (Context) getContextMethod.invoke(fragmentHostCallback);
                if (context != null) {
                    ClassLoader classLoader = context.getClassLoader();
                    Class<?> fragmentStateClass = Class.forName("androidx.fragment.app.FragmentState");
                    Method instantiateMethod = fragmentStateClass.getDeclaredMethod("instantiate", ClassLoader.class, FragmentFactory.class);
                    instantiateMethod.setAccessible(true);
                    return (Fragment) instantiateMethod.invoke(fragmentState, classLoader, fragmentManager.getFragmentFactory());
                }
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Nullable
    private static Fragment restoreFragment1_2(@Nullable Object fragmentState, @NonNull FragmentManager fragmentManager) {
        String who = getWhoFromFragmentState(fragmentState);
        if (who != null) {
            try {
                Class<?> fragmentManagerClass = Class.forName("androidx.fragment.app.FragmentManager");
                Method findFragmentByWhoMethod = fragmentManagerClass.getDeclaredMethod("findFragmentByWho", String.class);
                findFragmentByWhoMethod.setAccessible(true);
                Fragment fragment = (Fragment) findFragmentByWhoMethod.invoke(fragmentManager, who);
                if (fragment != null) {
                    return fragment;
                }
                Method getLifecycleCallbacksDispatcherMethod = fragmentManagerClass.getDeclaredMethod("getLifecycleCallbacksDispatcher");
                getLifecycleCallbacksDispatcherMethod.setAccessible(true);
                Object mLifecycleCallbacksDispatcher = getLifecycleCallbacksDispatcherMethod.invoke(fragmentManager);
                Field mHostField = fragmentManagerClass.getDeclaredField("mHost");
                mHostField.setAccessible(true);
                Object fragmentHostCallback = mHostField.get(fragmentManager);
                Class<?> fragmentHostCallbackClass = Class.forName("androidx.fragment.app.FragmentHostCallback");
                Method getContextMethod = fragmentHostCallbackClass.getDeclaredMethod("getContext");
                getContextMethod.setAccessible(true);
                Context context = (Context) getContextMethod.invoke(fragmentHostCallback);
                if (context != null) {
                    ClassLoader classLoader = context.getClassLoader();
                    Class<?> FragmentStateManagerClass = Class.forName("androidx.fragment.app.FragmentStateManager");
                    Class<?> fragmentLifecycleCallbacksDispatcherClass = Class.forName("androidx.fragment.app.FragmentLifecycleCallbacksDispatcher");
                    Class<?> fragmentStateClass = Class.forName("androidx.fragment.app.FragmentState");
                    Constructor<?> constructor = FragmentStateManagerClass.getDeclaredConstructor(fragmentLifecycleCallbacksDispatcherClass, ClassLoader.class, FragmentFactory.class, fragmentStateClass);
                    constructor.setAccessible(true);
                    Object fragmentStateManager = constructor.newInstance(mLifecycleCallbacksDispatcher, classLoader, fragmentManager.getFragmentFactory(), fragmentState);
                    Method getFragmentMethod = FragmentStateManagerClass.getDeclaredMethod("getFragment");
                    getFragmentMethod.setAccessible(true);
                    return (Fragment) getFragmentMethod.invoke(fragmentStateManager);
                }
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @Nullable
    private static Fragment restoreFragment1_3(@Nullable Object fragmentState, @NonNull FragmentManager fragmentManager) {
        String who = getWhoFromFragmentState(fragmentState);
        if (who != null) {
            try {
                Class<?> fragmentManagerClass = Class.forName("androidx.fragment.app.FragmentManager");
                Method findFragmentByWhoMethod = fragmentManagerClass.getDeclaredMethod("findFragmentByWho", String.class);
                findFragmentByWhoMethod.setAccessible(true);
                Fragment fragment = (Fragment) findFragmentByWhoMethod.invoke(fragmentManager, who);
                if (fragment != null) {
                    return fragment;
                }
                Method getLifecycleCallbacksDispatcherMethod = fragmentManagerClass.getDeclaredMethod("getLifecycleCallbacksDispatcher");
                getLifecycleCallbacksDispatcherMethod.setAccessible(true);
                Object mLifecycleCallbacksDispatcher = getLifecycleCallbacksDispatcherMethod.invoke(fragmentManager);
                Field mHostField = fragmentManagerClass.getDeclaredField("mHost");
                mHostField.setAccessible(true);
                Object fragmentHostCallback = mHostField.get(fragmentManager);
                Class<?> fragmentHostCallbackClass = Class.forName("androidx.fragment.app.FragmentHostCallback");
                Method getContextMethod = fragmentHostCallbackClass.getDeclaredMethod("getContext");
                getContextMethod.setAccessible(true);
                Context context = (Context) getContextMethod.invoke(fragmentHostCallback);
                if (context != null) {
                    ClassLoader classLoader = context.getClassLoader();
                    Class<?> FragmentStateManagerClass = Class.forName("androidx.fragment.app.FragmentStateManager");
                    Class<?> fragmentLifecycleCallbacksDispatcherClass = Class.forName("androidx.fragment.app.FragmentLifecycleCallbacksDispatcher");
                    Class<?> FragmentStoreClass = Class.forName("androidx.fragment.app.FragmentStore");
                    Class<?> fragmentStateClass = Class.forName("androidx.fragment.app.FragmentState");
                    Method getFragmentStoreMethod = fragmentManagerClass.getDeclaredMethod("getFragmentStore");
                    getFragmentStoreMethod.setAccessible(true);
                    Object fragmentStore = getFragmentStoreMethod.invoke(fragmentManager);
                    Constructor<?> constructor = FragmentStateManagerClass.getDeclaredConstructor(fragmentLifecycleCallbacksDispatcherClass, FragmentStoreClass, ClassLoader.class, FragmentFactory.class, fragmentStateClass);
                    constructor.setAccessible(true);
                    Object fragmentStateManager = constructor.newInstance(mLifecycleCallbacksDispatcher, fragmentStore, classLoader, fragmentManager.getFragmentFactory(), fragmentState);
                    Method getFragmentMethod = FragmentStateManagerClass.getDeclaredMethod("getFragment");
                    getFragmentMethod.setAccessible(true);
                    return (Fragment) getFragmentMethod.invoke(fragmentStateManager);
                }
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @Nullable
    private static String getWhoFromFragmentState(@Nullable Object fragmentState) {
        try {
            Class<?> fragmentStateClass = Class.forName("androidx.fragment.app.FragmentState");
            Field mWhoField = fragmentStateClass.getDeclaredField("mWho");
            mWhoField.setAccessible(true);
            return (String) mWhoField.get(fragmentState);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Nullable
    static PendingTransactionState saveTransaction(int type, @NonNull FragmentTransaction transaction) {
        try {
            Class<?> fragmentTransactionClass = Class.forName("androidx.fragment.app.FragmentTransaction");
            Field mAddToBackStackField = fragmentTransactionClass.getDeclaredField("mAddToBackStack");
            mAddToBackStackField.setAccessible(true);
            boolean mAddToBackStack = mAddToBackStackField.getBoolean(transaction);
            if (!mAddToBackStack) {//make it true first
                mAddToBackStackField.setBoolean(transaction, true);
            }
            Class<?> backStackStateClass = Class.forName("androidx.fragment.app.BackStackState");
            Class<?> backStackRecordClass = Class.forName("androidx.fragment.app.BackStackRecord");
            Constructor<?> constructor = backStackStateClass.getDeclaredConstructor(backStackRecordClass);
            constructor.setAccessible(true);
            Parcelable backStackState = (Parcelable) constructor.newInstance(transaction);
            Bundle fragmentStates = new Bundle();
            Field mOpsField = fragmentTransactionClass.getDeclaredField("mOps");
            mOpsField.setAccessible(true);
            List<Object> mOps = (List<Object>) mOpsField.get(transaction);
            if (mOps != null && !mOps.isEmpty()) {
                Class<?> opClass = Class.forName("androidx.fragment.app.FragmentTransaction$Op");
                Field mFragmentField = opClass.getDeclaredField("mFragment");
                mFragmentField.setAccessible(true);
                for (Object mOp : mOps) {
                    Fragment fragment = (Fragment) mFragmentField.get(mOp);
                    if (fragment != null) {
                        Parcelable fragmentState = saveFragment(fragment);
                        if (fragmentState != null) {
                            String who = getWhoFromFragmentState(fragmentState);
                            if (who != null) {
                                fragmentStates.putParcelable(who, fragmentState);
                            }
                        }
                    }
                }
            }
            return new PendingTransactionState(type, mAddToBackStack, backStackState, fragmentStates);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }
        return null;
    }

    static void startPendingTransaction(@NonNull PendingTransactionState pendingTransactionState, @NonNull FragmentManager fragmentManager) {
        try {
            Class<?> fragmentTransactionClass = Class.forName("androidx.fragment.app.FragmentTransaction");
            Class<?> backStackStateClass = Class.forName("androidx.fragment.app.BackStackState");
            Method instantiateMethod = backStackStateClass.getDeclaredMethod("instantiate", FragmentManager.class);
            instantiateMethod.setAccessible(true);
            FragmentTransaction newBackStackRecord = (FragmentTransaction) instantiateMethod.invoke(pendingTransactionState.backStackState, fragmentManager);
            if (newBackStackRecord != null) {
                if (!pendingTransactionState.addToBackStack) {
                    Field mAddToBackStackField = fragmentTransactionClass.getDeclaredField("mAddToBackStack");
                    mAddToBackStackField.setAccessible(true);
                    mAddToBackStackField.setBoolean(newBackStackRecord, false);
                }
                Field mFragmentWhosField = backStackStateClass.getDeclaredField("mFragmentWhos");
                mFragmentWhosField.setAccessible(true);
                ArrayList<String> mFragmentWhos = (ArrayList<String>) mFragmentWhosField.get(pendingTransactionState.backStackState);
                Field mOpsField = fragmentTransactionClass.getDeclaredField("mOps");
                mOpsField.setAccessible(true);
                List<Object> mOps = (List<Object>) mOpsField.get(newBackStackRecord);
                if (mOps != null && mFragmentWhos != null && mOps.size() == mFragmentWhos.size()) {
                    Class<?> opClass = Class.forName("androidx.fragment.app.FragmentTransaction$Op");
                    Field mFragmentField = opClass.getDeclaredField("mFragment");
                    mFragmentField.setAccessible(true);
                    for (int i = 0; i < mOps.size(); i++) {
                        Object op = mOps.get(i);
                        if (mFragmentField.get(op) == null) {
                            String who = mFragmentWhos.get(i);
                            if (who != null) {
                                Parcelable fragmentState = pendingTransactionState.fragmentStates.getParcelable(who);
                                if (fragmentState != null) {
                                    Fragment fragment = restoreFragment(fragmentState, fragmentManager);
                                    if (fragment != null) {
                                        mFragmentField.set(op, fragment);
                                    }
                                }
                            }
                        }
                    }
                }
                switch (pendingTransactionState.type) {
                    case PendingTransactionState.TYPE_COMMIT:
                        newBackStackRecord.commit();
                        break;
                    case PendingTransactionState.TYPE_COMMIT_ALLOWING_STATE_LOSS:
                        newBackStackRecord.commitAllowingStateLoss();
                        break;
                    case PendingTransactionState.TYPE_COMMIT_NOW:
                        newBackStackRecord.commitNow();
                        break;
                    case PendingTransactionState.TYPE_COMMIT_NOW_ALLOWING_STATE_LOSS:
                        newBackStackRecord.commitNowAllowingStateLoss();
                        break;
                }
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
    }

    private static boolean hasDeclaredField(@NonNull Class<?> clz, @NonNull String fieldName) {
        Field[] declaredFields = clz.getDeclaredFields();
        for (Field declaredField : declaredFields) {
            if (declaredField.getName().equals(fieldName)) {
                return true;
            }
        }
        return false;
    }
}
