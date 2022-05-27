package cn.hx.dialogmanager

import android.os.Bundle

class FragmentTestActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fragment_test)
        savedInstanceState ?: supportFragmentManager.beginTransaction()
                .add(R.id.container, TestFragment()).commit()
    }
}