# Android 优先级对话框

Android 默认对话框是不带优先级的，并且可以同时弹出多个，最后弹出的在最上面。往往我们更习惯一次只显示一个对话框，然后前一个关闭后一个才弹出。于是有了PriorityDialog

PriorityDialog基于DialogFragment实现，以最小的倾入性实现优先级对话框。

### 使用方法

#### gradle依赖

```groovy
dependencies {
    implementation 'com.github.qq549631030:priority-dialog:1.0.0'
}
```

#### 初始化配置

##### 1、PriorityDialog配置

通过代理方式：

PriorityDialog by PriorityDialogImpl()可实现将任意一个DialogFragment转变成优先级对话框

并在onCreate方法调用initAsPriorityDialog(this)完成初始化

```kotlin
open class BaseDialog : DialogFragment(), PriorityDialog by PriorityDialogImpl() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initAsPriorityDialog(this)
    }
}
```

##### 2、DialogHost配置

通过代理方式：

ActivityDialogHost by ActivityDialogHostImpl() 可实现将任意一个FragmentActivity转变成对话框宿主FragmentDialogHost by
FragmentDialogHostImpl()可实现将任意一个Fragment转变成对话框宿主

并在onCreate方法调用initAsDialogHost(this)完成初始化

```kotlin
open class BaseActivity : AppCompatActivity(), ActivityDialogHost by ActivityDialogHostImpl() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initAsDialogHost(this)
    }
}
```

```kotlin
open class BaseFragment : Fragment(), FragmentDialogHost by FragmentDialogHostImpl() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initAsDialogHost(this)
    }
}
```

##### 3、使用

在DialogHost(宿主)中调用showPriorityDialog(priorityDialog)即可

```kotlin
val dialog = BaseDialog()
dialog.priority = 1
showPriorityDialog(dialog)
```

#### 属性介绍

##### 1、priority优先级，值越大优先级越高(默认值为0)

当同一宿主页面同时有多个对话框弹出时，优先级最高的先显示，等高优先级的关闭后，次优先级的再弹出。

分三种情况：

1、先**低**后**高**，**高**取代**低**显示，**高**关闭后**低**再显示

```kotlin
val dialog1 = BaseDialog()
dialog1.priority = 1
showPriorityDialog(dialog1)
//当前显示dialog1
val dialog2 = BaseDialog()
dialog2.priority = 2
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
dialog1.priority = 2
showPriorityDialog(dialog1)
//当前显示dialog1
val dialog2 = BaseDialog()
dialog2.priority = 1
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
dialog1.priority = 1
showPriorityDialog(dialog1)
//当前显示dialog1
val dialog2 = BaseDialog()
dialog2.priority = 1
showPriorityDialog(dialog2)
//当前显示dialog2
dialog2.dismiss()
//当前显示dialog1
dialog1.dismiss()
//当前无dialog
```

##### 2、onlyDismissByUser 话框是否只有用户才能真正关闭（默认值true）

如前面情况1，**低**被**高**取代，当**高**关闭后**低**会再次显示，如果想**低**不再显示，可以设置**低**的onlyDismissByUser=false

```kotlin
val dialog1 = BaseDialog()
dialog1.priority = 1
dialog1.onlyDismissByUser = false
showPriorityDialog(dialog1)
//当前显示dialog1
val dialog2 = BaseDialog()
dialog2.priority = 2
showPriorityDialog(dialog2)
//当前显示dialog2
dialog2.dismiss()
//当前无dialog
```

##### 3、lockWindow是否锁定当前页面(默认值false)

若为true则对话框显示时只能停留在当前页面，无法关闭无法跳走，即startActivity()、startActivityForResult()、finish()
三个方法不生效，会把这些操作缓存起来，当对话框关闭时会自动执行之前未执行的操作。

要使这个属性生效，前的BaseActivity配置要做如下调整：

```kotlin
open class BaseActivity : AppCompatActivity(), ActivityDialogHost by ActivityDialogHostImpl() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initAsDialogHost(this)
    }
    
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
dialog.lockWindow = true
showPriorityDialog(dialog)
//当前显示dialog
startActivity(Intent(this, SecondActivity::class.java))
finish()
//SecondActivity不会启动，当前页面不会关闭
dialog.dismiss()
//SecondActivity启动，当前页面关闭
```

