# Android 优先级对话框

Android 默认对话框是不带优先级的，并且可以同时弹出多个，最后弹出的在最上面。往往我们更习惯一次只显示一个对话框，然后前一个关闭后一个才弹出。于是有了PriorityDialog

PriorityDialog基于DialogFragment实现，以最小的倾入性实现优先级对话框。

### gradle依赖

```groovy
dependencies {
    implementation 'com.github.qq549631030:priority-dialog:x.x.x'
}
```

### 初始化配置

#### 1、初始化

在Application的onCreate方法调用PriorityDialogManager.init(this)初始化

```kotlin
class BaseApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        PriorityDialogManager.init(this)
    }
}
```

#### 2、PriorityDialog配置

通过代理方式：

PriorityDialog by PriorityDialogImpl()可实现将任意一个DialogFragment转变成优先级对话框

```kotlin
open class BaseDialog : DialogFragment(), PriorityDialog by PriorityDialogImpl() {
}
```

#### 3、DialogManager，DialogHost配置

通过代理方式：

DialogManager by DialogManagerImpl() 可实现将任意一个FragmentActivity转变成对话框管理类

DialogHost by DialogHostImpl() 可实现将任意一个FragmentActivity、Fragment转变成对话框宿主

```kotlin
open class BaseActivity : AppCompatActivity(), DialogManager by DialogManagerImpl(),
    DialogHost by DialogHostImpl() {
}
```

```kotlin
open class BaseFragment : Fragment(), DialogHost by DialogHostImpl() {
}
```

#### 4、使用

在DialogHost(宿主)中调用showPriorityDialog(priorityDialog)即可

```kotlin
val dialog = BaseDialog()
dialog.priorityConfig.priority = 1
showPriorityDialog(dialog)
```

### 属性介绍

#### 1、priority优先级，值越大优先级越高(默认值为0)

当同一宿主页面同时有多个对话框弹出时，优先级最高的先显示，等高优先级的关闭后，次优先级的再弹出。

分三种情况：

1、先**低**后**高**，**高**取代**低**显示，**高**关闭后**低**再显示

```kotlin
val dialog1 = BaseDialog()
dialog1.priorityConfig.priority = 1
showPriorityDialog(dialog1)
//当前显示dialog1
val dialog2 = BaseDialog()
dialog2.priorityConfig.priority = 2
showPriorityDialog(dialog2)
//当前显示dialog2
dialog2.dismiss()
//当前显示dialog1
dialog1.dismiss()
//当前无dialog
```

2、先**高**后**低**，**低**不显示，**高**关闭后**低**再显示

```kotlin
val dialog1 = BaseDialog()
dialog1.priorityConfig.priority = 2
showPriorityDialog(dialog1)
//当前显示dialog1
val dialog2 = BaseDialog()
dialog2.priorityConfig.priority = 1
showPriorityDialog(dialog2)
//当前仍然显示dialog1
dialog1.dismiss()
//当前显示dialog2
dialog2.dismiss()
//当前无dialog
```

3、同优先级，后弹出的优先级高

```kotlin
val dialog1 = BaseDialog()
dialog1.priorityConfig.priority = 1
showPriorityDialog(dialog1)
//当前显示dialog1
val dialog2 = BaseDialog()
dialog2.priorityConfig.priority = 1
showPriorityDialog(dialog2)
//当前显示dialog2
dialog2.dismiss()
//当前显示dialog1
dialog1.dismiss()
//当前无dialog
```

以上是默认的优先级处理方式，也可以通过配置PriorityStrategy来自定义规则

通过PriorityDialogManager.init(this，priorityStrategy)初始化全局策略

或通过PriorityDialogManager.updatePriorityStrategy(priorityStrategy)修改全局策略

也可以通过DialogManager.setCurrentPriorityStrategy(priorityStrategy)来设置单个Activity的策略

具体实现方式可参数DefaultPriorityStrategy

```java
public interface PriorityStrategy {
    //新对话框是否能取代现有对话框而显示
    boolean canNewShow(@NonNull PriorityDialog preDialog, @NonNull PriorityDialog newDialog);

    //新对话框和等待队列里的比是否能显示，默认true
    boolean canNewShowCasePending(@NonNull List<PendingDialogState> pendingList, @NonNull PriorityDialog newDialog);

    //等待队列中相同优先级的对话框弹出顺序是不是先进先出，默认false
    boolean firstInFirstOutWhenSamePriority();
}
```

#### 2、isAddToPendingWhenReplaceByOther 当被其它对话框顶下去的时候是否要加入等待队列（默认值true）

如前面情况1，**低**被**高**取代，当**高**关闭后**低**会再次显示，如果想**低**不再显示，可以设置**低**
isAddToPendingWhenReplaceByOther=false

```kotlin
val dialog1 = BaseDialog()
dialog1.priorityConfig.priority = 1
dialog1.priorityConfig.isAddToPendingWhenReplaceByOther = false
showPriorityDialog(dialog1)
//当前显示dialog1
val dialog2 = BaseDialog()
dialog2.priorityConfig.priority = 2
showPriorityDialog(dialog2)
//当前显示dialog2
dialog2.dismiss()
//当前无dialog
```

#### 3、isLockWindow是否锁定当前页面(默认值false)

若为true则对话框显示时只能停留在当前页面，无法关闭无法跳走，会把这些操作缓存起来，当对话框关闭时会自动执行之前未执行的操作。

##### Activity级别操作

Activity的 startActivity()、startActivityForResult()、finish()

Fragment的 startActivity()、startActivityForResult()

要使这个属性生效，前面的BaseActivity配置要做如下调整：

```kotlin
open class BaseActivity : AppCompatActivity(), DialogManager by DialogManagerImpl(),
    DialogHost by DialogHostImpl() {

    override fun startActivityForResult(intent: Intent, requestCode: Int, options: Bundle?) {
        if (warpStartActivityForResult(intent, requestCode, options)) {
            return
        }
        super.startActivityForResult(intent, requestCode, options)
    }

    override fun finish() {
        if (warpFinish()) {
            return
        }
        super.finish()
    }
}
```

```kotlin
val dialog = BaseDialog()
dialog.priorityConfig.isLockWindow = true
showPriorityDialog(dialog)
//当前显示dialog
startActivity(Intent(this, SecondActivity::class.java))
finish()
//SecondActivity不会启动，当前页面不会关闭
dialog.dismiss()
//SecondActivity启动，当前页面关闭
```

##### Fragment级别操作

FragmentManager的各种Transaction以及popBackStack

要使这个属性生效，执行Transaction以及popBackStack操作时要使用warpParentFragmentManager或者warpChildFragmentManager

```kotlin
val dialog = BaseDialog()
dialog.priorityConfig.isLockWindow = true
showPriorityDialog(dialog)
//当前显示dialog
warpParentFragmentManager.beginTransaction().replace(R.id.container, SecondFragment()).commit()
//当前显示的Fragment不变
dialog.dismiss()
//当前显示的Fragment变成了SecondFragment
```

##### 4、isSupportRecreate是否支持Activity重建前的dialog恢复显示（默认值true）

```kotlin
val dialog = BaseDialog()
dialog.priorityConfig.isSupportRecreate = true
showPriorityDialog(dialog)
//当前显示dialog

//Activity recreate

//当前显示dialog
```

```kotlin
val dialog = BaseDialog()
dialog.priorityConfig.isSupportRecreate = false
showPriorityDialog(dialog)
//当前显示dialog

//Activity recreate

//当前无dialog
```

##### 5、isAllowStateLoss是否支持onStop状态下显示dialog，默认false

默认在stop状态下弹对话框会被丢弃，可以通过设置isAllowStateLoss来改变这一行为

```kotlin
val dialog = BaseDialog()
//activity.onStop()
showPriorityDialog(dialog)
//当前无dialog
//activity.onResume()
//当前无dialog

dialog.priorityConfig.isAllowStateLoss = true
//activity.onStop()
showPriorityDialog(dialog)
//当前无dialog
//activity.onResume()
//当前有dialog

```

##### 6、isAddToPendingWhenCanNotShow当不能显示的时候（如比优先级比当前显示的低）是否要加入等待队列，默认true

默认想弹的对话框比现在显示中或者比等待队列中最高优先级对话框（isCasePending=true时）的优先级低会加入等待队列，
可以通过设置isAddToPendingWhenCanNotShow来改变这一行为

```kotlin
val dialog1 = BaseDialog()
dialog1.priorityConfig.priority = 2
showPriorityDialog(dialog1)
//当前显示dialog1
val dialog2 = BaseDialog()
dialog2.priorityConfig.priority = 1
dialog2.priorityConfig.isAddToPendingWhenCanNotShow = false
showPriorityDialog(dialog2)
//当前仍然显示dialog1（dialog2被丢弃）
dialog1.dismiss()
//当前无dialog
```

##### 7、isAddToPendingWhenReplaceByOther当被其它高优先级对话框顶下去的时候是否要加入等待队列，默认true

默认被其它高优先级对话框顶下去的时候要加入等待队列，可以通过设置isAddToPendingWhenReplaceByOther = false来改变这一行为

```kotlin
val dialog1 = BaseDialog()
dialog1.priorityConfig.priority = 1
dialog1.priorityConfig.isAddToPendingWhenReplaceByOther = false
showPriorityDialog(dialog1)
//当前显示dialog1
val dialog2 = BaseDialog()
dialog2.priorityConfig.priority = 2
showPriorityDialog(dialog2)
//当前显示dialog2（dialog1被丢弃）
dialog2.dismiss()
//当前无dialog
```

##### 8、isShowImmediateAfterPreDismiss处于等待队列中的对话框是否在上一个显示中的对话框关闭后立即弹出，默认true

默认正在显示的对话框关闭后，等待队列里最高优先级的一个会立即弹出，可以通过设置isShowImmediateAfterPreDismiss = false来改变这一行为  
之后可通过DialogManager.tryShowNextPendingDialog()或者DialogManager.tryShowPendingDialog(dialogInfo)
来手动显示等待队列中的对话框

```kotlin
val dialog1 = BaseDialog()
dialog1.priorityConfig.priority = 1
dialog21.priorityConfig.isShowImmediateAfterPreDismiss = false
showPriorityDialog(dialog1)
//当前显示dialog1
val dialog2 = BaseDialog()
dialog2.priorityConfig.priority = 2
showPriorityDialog(dialog2)
//当前显示dialog2
dialog2.dismiss()
//当前无dialog （dialog1还在笔队列中）

tryShowNextPendingDialog()
//当前显示dialog1
```

##### 9、isCasePending新对话框弹出时是否需要和等待队列中的对话框比较优先级，默认false

默认要弹对话框的时候只和现在正在显示的对话框比较优先级，不考虑等待队列中的，可以通过设置isCasePending = true来改变这一行为

```kotlin
val dialog1 = BaseDialog()
dialog1.priorityConfig.priority = 2
dialog1.priorityConfig.isShowImmediateAfterPreDismiss = false
showPriorityDialog(dialog1)
//当前显示dialog1
val dialog2 = BaseDialog()
dialog2.priorityConfig.priority = 3
showPriorityDialog(dialog2)
//当前显示dialog2
dialog2.dismiss()
//当前无dialog （dialog1还在笔队列中）

val dialog3 = BaseDialog()
dialog3.priorityConfig.priority = 1
dialog3.priorityConfig.isCasePending = true
showPriorityDialog(dialog3)
//当前显示dialog1（dialog3进入等待队列）
dialog1.dismiss()
//当前显示dialog3
```

##### 10、isShowImmediateAfterPreCanNotShowCasePending是否在上一个对话框无法弹出（和等待队列比）后立即弹出，默认true

默认情况下，当想要弹的对话框比等待队列里最高优先级的对话框优先级低时，等待队列中最高优先级的对话框会自动弹出，可以通过设置isShowImmediateAfterPreCanNotShowCasePending
= false来改变这一行为

```kotlin
val dialog1 = BaseDialog()
dialog1.priorityConfig.priority = 2
dialog1.priorityConfig.isShowImmediateAfterPreDismiss = false
dialog1.priorityConfig.isShowImmediateAfterPreCanNotShowCasePending = false
showPriorityDialog(dialog1)
//当前显示dialog1
val dialog2 = BaseDialog()
dialog2.priorityConfig.priority = 3
showPriorityDialog(dialog2)
//当前显示dialog2
dialog2.dismiss()
//当前无dialog （dialog1还在笔队列中）

val dialog3 = BaseDialog()
dialog3.priorityConfig.priority = 1
dialog3.priorityConfig.isCasePending = true
showPriorityDialog(dialog3)
//当前无dialog（dialog1,dialog3都在等待孤队列）

tryShowNextPendingDialog()
//当前显示dialog1
dialog1.dismiss()
//当前显示dialog3
```

##### PriorityDialog的defaultPriorityConfig(config)方法可以设置所有对话框的初始配置

有时候你的对话框行为和框架默认的不一样，但不想每次弹框都重复性的设置这些属性，可以能过这个方法统一设置

```kotlin
open class BaseDialog : DialogFragment(), PriorityDialog by PriorityDialogImpl() {
    override fun defaultPriorityConfig(config: PriorityDialogConfig) {
        //低优先级的直接丢弃，不加入等待队列
        config.isAddToPendingWhenCanNotShow = false
        config.isAddToPendingWhenReplaceByOther = false
    }
}
```

### 对话框事件处理

#### onCancel

可重写DialogHost的onCancel方法（推荐）

```kotlin
override fun onCancel(priorityDialog: PriorityDialog) {
    if (priorityDialog.uuid == "custom_uuid") {
        //next
    }
}
```

也可用setOnCancelListener

```kotlin
dialog.setOnCancelListener(object : OnCancelListener {
    override fun onCancel(dialog: PriorityDialog) {
        //这里只能通过dialog.dialogHost访问外部类的方法、属性
        //不可直接访问外部类方法、属性，因为Activity recreate后的情况下原dialogHost已经不存在了
        (dialog.dialogHost as? YourHostActivity)?.publicMethod()
        //(dialog.dialogHost as? YourHostFragment)?.publicMethod()
    }
})
```

#### onDismiss

可重写DialogHost的onDismiss方法（推荐）

```kotlin
override fun onDismiss(priorityDialog: PriorityDialog) {
    if (priorityDialog.uuid == "custom_uuid") {
        //next
    }
}
```

也可用setOnDismissListener

```kotlin
dialog.setOnDismissListener(object : OnDismissListener {
    override fun onDismiss(dialog: PriorityDialog) {
        //这里只能通过dialog.dialogHost访问外部类的方法、属性
        //不可直接访问外部类方法、属性，因为Activity recreate后的情况下原dialogHost已经不存在了
        (dialog.dialogHost as? YourHostActivity)?.publicMethod()
        //(dialog.dialogHost as? YourHostFragment)?.publicMethod()
    }
})
```

#### onDialogEvent

可重写DialogHost的onDialogEvent方法（推荐）

event可以是任意的自定义事件，比如按钮点击，文字输入等等。

```kotlin
override fun onDialogEvent(priorityDialog: PriorityDialog, event: Any) {
    if (priorityDialog.uuid == "custom_uuid") {
        when (event) {
            is BaseAlertDialog.AlertDialogClickEvent -> {
                if (event.which == DialogInterface.BUTTON_POSITIVE) {
                    //next
                }
            }
            else -> {}
        }
    }
}
```

也可用setOnDialogEventListener

```kotlin
dialog.setOnDialogEventListener(object : OnDialogEventListener {
    override fun onDialogEvent(dialog: PriorityDialog, event: Any) {
        //这里只能通过dialog.dialogHost访问外部类的方法、属性
        //不可直接访问外部类方法、属性，因为Activity recreate后的情况下原dialogHost已经不存在了
        (dialog.dialogHost as? YourHostActivity)?.publicMethod()
        //(dialog.dialogHost as? YourHostFragment)?.publicMethod()
    }
})
```

### DialogManager相关方法

#### 对话框显示状态监听

registerPriorityDialogListener()
unregisterPriorityDialogListener()

```kotlin
open class BaseActivity : AppCompatActivity(), DialogManager by DialogManagerImpl(),
    DialogHost by DialogHostImpl() {
    private val priorityDialogListener = object : PriorityDialogListener {
        override fun onDialogShow(dialog: PriorityDialog) {
            //对话框显示
        }

        override fun onDialogDismiss(dialog: PriorityDialog) {
            //对话框关闭
        }

        override fun onDialogAddToPending(dialog: PriorityDialog) {
            //对话框加入等待队列
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        registerPriorityDialogListener(priorityDialogListener)
    }

    override fun onDestroy() {
        unregisterPriorityDialogListener(priorityDialogListener)
        super.onDestroy()
    }
}

```

#### 等待队列相关操作

```kotlin
//获取所有等待队列对话框
getAllPendingDialog()
//手动弹出等待队列中最高优先级对话框
tryShowNextPendingDialog()
//手动弹出等待队列中指定对话框（这个方法可以先显示低优先级的）
tryShowPendingDialog(dialogInfo)
```
