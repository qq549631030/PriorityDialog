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
import java.util.List;
import java.util.Objects;

public class FragmentUtil {

    @SuppressWarnings("deprecation")
    @Nullable
    static FragmentStateData saveFragment(@NonNull Fragment fragment) {
        try {
            Field mStateField = Fragment.class.getDeclaredField("mState");
            mStateField.setAccessible(true);
            int state = mStateField.getInt(fragment);
            FragmentManager fragmentManager = fragment.getFragmentManager();
            if (state > 0 && fragmentManager != null) {
                return saveFragmentStateWhenAttached(fragmentManager, fragment);
            } else {
                return saveFragmentStateWhenNotAttached(fragment);
            }
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static FragmentStateData saveFragmentStateWhenAttached(@NonNull FragmentManager fragmentManager, @NonNull Fragment fragment) throws NoSuchFieldException, ClassNotFoundException, InvocationTargetException, NoSuchMethodException, IllegalAccessException, InstantiationException {
        if (isAfter1_2()) {//1.2.0+
            Object fragmentStateManager = createFragmentStateManagerForSave(fragmentManager, fragment);
            Class<?> FragmentStateManagerClass = Class.forName("androidx.fragment.app.FragmentStateManager");
            Method saveStateMethod = FragmentStateManagerClass.getDeclaredMethod("saveState");
            saveStateMethod.setAccessible(true);
            if (isAfter1_6()) {//1.6.0+
                Bundle savedFragmentState = (Bundle) saveStateMethod.invoke(fragmentStateManager);
                if (savedFragmentState != null) {
                    Parcelable fragmentState = savedFragmentState.getParcelable("state");
                    if (fragmentState != null) {
                        return new FragmentStateData(fragmentState, savedFragmentState);
                    }
                }
            } else if (isAfter1_4()) {//1.4.0+
                saveStateMethod.invoke(fragmentStateManager);
                String who = (String) getFieldValue(fragment, Fragment.class, "mWho");
                Object fragmentStore = getFragmentStore(fragmentManager);
                Class<?> FragmentStoreClass = Class.forName("androidx.fragment.app.FragmentStore");
                Method getSavedStateMethod = FragmentStoreClass.getDeclaredMethod("getSavedState", String.class);
                getSavedStateMethod.setAccessible(true);
                Parcelable fragmentState = (Parcelable) getSavedStateMethod.invoke(fragmentStore, who);
                if (fragmentState != null) {
                    return new FragmentStateData(fragmentState, new Bundle());
                }
            } else {//1.2.0+
                Parcelable fragmentState = (Parcelable) saveStateMethod.invoke(fragmentStateManager);
                if (fragmentState != null) {
                    return new FragmentStateData(fragmentState, new Bundle());
                }
            }
        } else {//1.1.0
            Parcelable fragmentState = createFragmentState(fragment);
            Class<?> fragmentManagerImplClass = Class.forName("androidx.fragment.app.FragmentManagerImpl");
            Method saveFragmentBasicStateMethod = fragmentManagerImplClass.getDeclaredMethod("saveFragmentBasicState", Fragment.class);
            saveFragmentBasicStateMethod.setAccessible(true);
            Bundle savedFragmentState = (Bundle) saveFragmentBasicStateMethod.invoke(fragmentManager, fragment);

            setFieldValue(fragmentState, "mSavedFragmentState", savedFragmentState);//fragment 1.6.0以前要把savedFragmentState放入FragmentState
            return new FragmentStateData(fragmentState, new Bundle());
        }
        return null;
    }

    private static FragmentStateData saveFragmentStateWhenNotAttached(@NonNull Fragment fragment) throws ClassNotFoundException, InvocationTargetException, NoSuchMethodException, IllegalAccessException, InstantiationException, NoSuchFieldException {
        Parcelable fragmentState = createFragmentState(fragment);

        Bundle savedFragmentState = new Bundle();
        if (isAfter1_6()) {//fragment 1.6.0以后保存方式变了
            savedFragmentState.putParcelable("state", fragmentState);
            savedFragmentState.putBundle("arguments", fragment.getArguments());
        }

        Bundle savedInstanceState = new Bundle();
        fragment.onSaveInstanceState(savedInstanceState);
        if (!savedInstanceState.isEmpty()) {
            if (isAfter1_6()) {//fragment 1.6.0以后保存方式变了
                savedFragmentState.putBundle("savedInstanceState", savedInstanceState);
            } else {
                savedFragmentState.putAll(savedInstanceState);
            }
        }

        Bundle savedStateRegistryState = new Bundle();
        SavedStateRegistry savedStateRegistry = fragment.getSavedStateRegistry();
        Method performSaveMethod = SavedStateRegistry.class.getDeclaredMethod("performSave", Bundle.class);
        performSaveMethod.setAccessible(true);
        performSaveMethod.invoke(savedStateRegistry, savedStateRegistryState);
        if (!savedStateRegistryState.isEmpty()) {
            if (isAfter1_6()) {//fragment 1.6.0以后保存方式变了
                savedFragmentState.putBundle("registryState", savedStateRegistryState);
            } else {
                savedFragmentState.putAll(savedStateRegistryState);
            }
        }
        if (!isAfter1_6()) {//fragment 1.6.0 以前要把savedFragmentState放入FragmentState中
            setFieldValue(fragmentState, "mSavedFragmentState", savedFragmentState);
        }
        return new FragmentStateData(fragmentState, savedFragmentState);
    }

    @Nullable
    static Fragment restoreFragment(@NonNull FragmentStateData fragmentStateData, @NonNull FragmentManager fragmentManager) {
        try {
            String who = (String) getFieldValue(fragmentStateData.fragmentState, "mWho");
            if (who != null) {
                Fragment fragment = findFragmentByWho(fragmentManager, who);
                if (fragment != null) {
                    return fragment;
                } else {
                    ClassLoader classLoader = getClassLoader(fragmentManager);
                    if (classLoader != null) {
                        if (isAfter1_2()) {//fragment 1.2.0以后用FragmentStateManager.getFragment
                            Object fragmentStateManager = createFragmentStateManagerForRestore(fragmentStateData, fragmentManager, classLoader);
                            Class<?> fragmentStateManagerClass = Class.forName("androidx.fragment.app.FragmentStateManager");
                            Method getFragmentMethod = fragmentStateManagerClass.getDeclaredMethod("getFragment");
                            getFragmentMethod.setAccessible(true);
                            return (Fragment) getFragmentMethod.invoke(fragmentStateManager);
                        } else {//fragment 1.1.0用FragmentState.instantiate
                            Class<?> fragmentStateClass = Class.forName("androidx.fragment.app.FragmentState");
                            Method instantiateMethod = fragmentStateClass.getDeclaredMethod("instantiate", ClassLoader.class, FragmentFactory.class);
                            instantiateMethod.setAccessible(true);
                            return (Fragment) instantiateMethod.invoke(fragmentStateData.fragmentState, classLoader, fragmentManager.getFragmentFactory());
                        }
                    }
                }
            }
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }
        return null;
    }

    @SuppressWarnings({"JavaReflectionInvocation", "unchecked"})
    @Nullable
    static PendingTransactionState saveTransaction(@NonNull FragmentTransaction transaction) {
        try {
            Class<?> fragmentTransactionClass = Class.forName("androidx.fragment.app.FragmentTransaction");
            Field mAddToBackStackField = fragmentTransactionClass.getDeclaredField("mAddToBackStack");
            mAddToBackStackField.setAccessible(true);
            boolean mAddToBackStack = mAddToBackStackField.getBoolean(transaction);
            if (!mAddToBackStack) {//make it true first
                mAddToBackStackField.setBoolean(transaction, true);
            }
            Class<?> backStackStateClass;
            try {
                //fragment 1.4.x
                backStackStateClass = Class.forName("androidx.fragment.app.BackStackRecordState");
            } catch (ClassNotFoundException e) {
                //fragment 1.1.x ~ 1.3.x
                backStackStateClass = Class.forName("androidx.fragment.app.BackStackState");
            }
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
                        FragmentStateData fragmentStateData = saveFragment(fragment);
                        if (fragmentStateData != null) {
                            String who = (String) getFieldValue(fragmentStateData.fragmentState, "mWho");
                            if (who != null) {
                                fragmentStates.putParcelable(who, fragmentStateData);
                            }
                        }
                    }
                }
            }
            return new PendingTransactionState(mAddToBackStack, backStackState, fragmentStates);
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

    @SuppressWarnings("unchecked")
    @Nullable
    static FragmentTransaction restorePendingTransaction(@NonNull PendingTransactionState pendingTransactionState, @NonNull FragmentManager fragmentManager) {
        try {
            Class<?> fragmentTransactionClass = Class.forName("androidx.fragment.app.FragmentTransaction");
            Class<?> backStackStateClass;
            try {
                //fragment 1.4.x
                backStackStateClass = Class.forName("androidx.fragment.app.BackStackRecordState");
            } catch (ClassNotFoundException e) {
                //fragment 1.1.x ~ 1.3.x
                backStackStateClass = Class.forName("androidx.fragment.app.BackStackState");
            }
            Method instantiateMethod;
            try {
                Class.forName("androidx.fragment.app.FragmentStateManager");
                //fragment 1.2.0 +
                instantiateMethod = backStackStateClass.getDeclaredMethod("instantiate", FragmentManager.class);
            } catch (ClassNotFoundException e) {
                //fragment 1.1.0
                Class<?> fragmentManagerImplClass = Class.forName("androidx.fragment.app.FragmentManagerImpl");
                instantiateMethod = backStackStateClass.getDeclaredMethod("instantiate", fragmentManagerImplClass);
            }
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
                List<String> mFragmentWhos = (List<String>) mFragmentWhosField.get(pendingTransactionState.backStackState);
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
                                FragmentStateData fragmentStateData = (FragmentStateData) pendingTransactionState.fragmentStates.getParcelable(who);
                                if (fragmentStateData != null) {
                                    Fragment fragment = restoreFragment(fragmentStateData, fragmentManager);
                                    if (fragment != null) {
                                        mFragmentField.set(op, fragment);
                                    }
                                }
                            }
                        }
                    }
                }
                return newBackStackRecord;
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

    private static boolean isAfter1_2() {
        try {
            Class.forName("androidx.fragment.app.FragmentStateManager");//fragment 1.2.0开始用FragmentStateManager管理
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    private static boolean isAfter1_3() {
        if (isAfter1_2()) {
            try {
                Class<?> fragmentStateManagerClass = Class.forName("androidx.fragment.app.FragmentStateManager");
                if (hasDeclaredField(fragmentStateManagerClass, "mFragmentStore")) {//fragment 1.3.0起使用FragmentStore保存
                    return true;
                }
            } catch (ClassNotFoundException e) {
            }
        }
        return false;
    }

    private static boolean isAfter1_4() {
        if (isAfter1_3()) {
            try {
                Class<?> fragmentStateManagerClass = Class.forName("androidx.fragment.app.FragmentStateManager");
                Method saveStateMethod = fragmentStateManagerClass.getDeclaredMethod("saveState");
                Class<?> returnType = saveStateMethod.getReturnType();
                if (returnType.equals(void.class)) {//fragment 1.4.0起saveState返回值为空
                    return true;
                }
            } catch (ClassNotFoundException e) {
            } catch (NoSuchMethodException e) {
            }
        }
        return false;
    }

    private static boolean isAfter1_6() {
        if (isAfter1_3()) {
            try {
                Class<?> fragmentStateManagerClass = Class.forName("androidx.fragment.app.FragmentStateManager");
                if (hasDeclaredField(fragmentStateManagerClass, "FRAGMENT_STATE_KEY")) {//fragment 1.6.0开始保存方式变了
                    return true;
                }
            } catch (ClassNotFoundException e) {
            }
        }
        return false;
    }

    @SuppressWarnings("SameParameterValue")
    private static boolean hasDeclaredField(@NonNull Class<?> clz, @NonNull String fieldName) {
        Field[] declaredFields = clz.getDeclaredFields();
        for (Field declaredField : declaredFields) {
            if (declaredField.getName().equals(fieldName)) {
                return true;
            }
        }
        return false;
    }

    @Nullable
    private static Object getFieldValue(@NonNull Object obj, @NonNull String fieldName) throws IllegalAccessException, NoSuchFieldException {
        return getFieldValue(obj, obj.getClass(), fieldName);
    }

    @Nullable
    private static Object getFieldValue(@NonNull Object obj, @NonNull Class<?> clz, @NonNull String fieldName) throws IllegalAccessException, NoSuchFieldException {
        Field field = clz.getDeclaredField(fieldName);
        field.setAccessible(true);
        return field.get(obj);
    }

    private static void setFieldValue(@NonNull Object obj, @NonNull String fieldName, @Nullable Object fieldValue) throws IllegalAccessException, NoSuchFieldException {
        Class<?> clz = obj.getClass();
        Field field = clz.getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(obj, fieldValue);
    }

    @NonNull
    private static Parcelable createFragmentState(@NonNull Fragment fragment) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException, InstantiationException {
        Class<?> fragmentStateClass = Class.forName("androidx.fragment.app.FragmentState");
        Constructor<?> constructor = fragmentStateClass.getDeclaredConstructor(Fragment.class);
        constructor.setAccessible(true);
        return (Parcelable) constructor.newInstance(fragment);
    }

    @NonNull
    private static Object getLifecycleCallbacksDispatcher(@NonNull FragmentManager fragmentManager) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method getLifecycleCallbacksDispatcherMethod = FragmentManager.class.getDeclaredMethod("getLifecycleCallbacksDispatcher");
        getLifecycleCallbacksDispatcherMethod.setAccessible(true);
        return Objects.requireNonNull(getLifecycleCallbacksDispatcherMethod.invoke(fragmentManager));
    }

    @NonNull
    private static Object getFragmentStore(@NonNull FragmentManager fragmentManager) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method getFragmentStoreMethod = FragmentManager.class.getDeclaredMethod("getFragmentStore");
        getFragmentStoreMethod.setAccessible(true);
        return Objects.requireNonNull(getFragmentStoreMethod.invoke(fragmentManager));
    }

    @SuppressWarnings({"JavaReflectionInvocation", "JavaReflectionMemberAccess"})
    @NonNull
    private static Object createFragmentStateManagerForSave(@NonNull FragmentManager fragmentManager, @NonNull Fragment fragment) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException, InstantiationException {
        Class<?> FragmentStateManagerClass = Class.forName("androidx.fragment.app.FragmentStateManager");
        Class<?> fragmentLifecycleCallbacksDispatcherClass = Class.forName("androidx.fragment.app.FragmentLifecycleCallbacksDispatcher");
        Object dispatcher = getLifecycleCallbacksDispatcher(fragmentManager);
        if (isAfter1_3()) {
            Class<?> FragmentStoreClass = Class.forName("androidx.fragment.app.FragmentStore");
            Object fragmentStore = getFragmentStore(fragmentManager);
            Constructor<?> constructor = FragmentStateManagerClass.getDeclaredConstructor(fragmentLifecycleCallbacksDispatcherClass, FragmentStoreClass, Fragment.class);
            constructor.setAccessible(true);
            return constructor.newInstance(dispatcher, fragmentStore, fragment);
        } else {
            Constructor<?> constructor = FragmentStateManagerClass.getDeclaredConstructor(fragmentLifecycleCallbacksDispatcherClass, Fragment.class);
            constructor.setAccessible(true);
            return constructor.newInstance(dispatcher, fragment);
        }
    }

    @Nullable
    private static Fragment findFragmentByWho(@NonNull FragmentManager fragmentManager, @NonNull String who) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method findFragmentByWhoMethod;
        if (isAfter1_2()) {
            findFragmentByWhoMethod = FragmentManager.class.getDeclaredMethod("findFragmentByWho", String.class);
        } else {
            Class<?> fragmentManagerImplClass = Class.forName("androidx.fragment.app.FragmentManagerImpl");
            findFragmentByWhoMethod = fragmentManagerImplClass.getDeclaredMethod("findFragmentByWho", String.class);
        }
        findFragmentByWhoMethod.setAccessible(true);
        return (Fragment) findFragmentByWhoMethod.invoke(fragmentManager, who);
    }

    @Nullable
    private static ClassLoader getClassLoader(@NonNull FragmentManager fragmentManager) throws ClassNotFoundException, NoSuchFieldException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        Class<?> fragmentManagerClass;
        if (isAfter1_2()) {
            fragmentManagerClass = FragmentManager.class;
        } else {
            fragmentManagerClass = Class.forName("androidx.fragment.app.FragmentManagerImpl");
        }
        Field mHostField = fragmentManagerClass.getDeclaredField("mHost");
        mHostField.setAccessible(true);
        Object fragmentHostCallback = mHostField.get(fragmentManager);
        Class<?> fragmentHostCallbackClass = Class.forName("androidx.fragment.app.FragmentHostCallback");
        Method getContextMethod = fragmentHostCallbackClass.getDeclaredMethod("getContext");
        getContextMethod.setAccessible(true);
        Context context = (Context) getContextMethod.invoke(fragmentHostCallback);
        if (context != null) {
            return context.getClassLoader();
        }
        return null;
    }

    @SuppressWarnings({"JavaReflectionInvocation", "JavaReflectionMemberAccess"})
    @NonNull
    private static Object createFragmentStateManagerForRestore(@NonNull FragmentStateData fragmentStateData, @NonNull FragmentManager fragmentManager, @NonNull ClassLoader classLoader) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException, InstantiationException, NoSuchFieldException {
        Object dispatcher = getLifecycleCallbacksDispatcher(fragmentManager);
        Class<?> FragmentStateManagerClass = Class.forName("androidx.fragment.app.FragmentStateManager");
        Class<?> fragmentLifecycleCallbacksDispatcherClass = Class.forName("androidx.fragment.app.FragmentLifecycleCallbacksDispatcher");
        Class<?> fragmentStateClass = Class.forName("androidx.fragment.app.FragmentState");
        if (isAfter1_3()) {
            Class<?> FragmentStoreClass = Class.forName("androidx.fragment.app.FragmentStore");
            Object fragmentStore = getFragmentStore(fragmentManager);
            if (isAfter1_6()) {//fragment 1.6.0
                Constructor<?> constructor = FragmentStateManagerClass.getDeclaredConstructor(fragmentLifecycleCallbacksDispatcherClass, FragmentStoreClass, ClassLoader.class, FragmentFactory.class, Bundle.class);
                constructor.setAccessible(true);
                return constructor.newInstance(dispatcher, fragmentStore, classLoader, fragmentManager.getFragmentFactory(), fragmentStateData.savedFragmentState);
            } else {//fragment 1.3.0
                Constructor<?> constructor = FragmentStateManagerClass.getDeclaredConstructor(fragmentLifecycleCallbacksDispatcherClass, FragmentStoreClass, ClassLoader.class, FragmentFactory.class, fragmentStateClass);
                constructor.setAccessible(true);
                return constructor.newInstance(dispatcher, fragmentStore, classLoader, fragmentManager.getFragmentFactory(), fragmentStateData.fragmentState);
            }
        } else {//fragment 1.2.0
            Constructor<?> constructor = FragmentStateManagerClass.getDeclaredConstructor(fragmentLifecycleCallbacksDispatcherClass, ClassLoader.class, FragmentFactory.class, fragmentStateClass);
            constructor.setAccessible(true);
            return constructor.newInstance(dispatcher, classLoader, fragmentManager.getFragmentFactory(), fragmentStateData.fragmentState);
        }
    }
}
