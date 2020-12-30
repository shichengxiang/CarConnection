package com.linruan.carconnection

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.linruan.carconnection.dialog.LoadingDialog

open abstract class BaseActivity : AppCompatActivity() {
    companion object{
        @JvmField
        open var isForeGround=false
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(getLayout())
        initView()
    }

    abstract fun getLayout(): Int
    abstract fun initView()
    override fun onDestroy() {
        super.onDestroy()
        LoadingDialog.dismiss()
    }

    override fun onResume() {
        super.onResume()
        isForeGround=true
    }

    override fun onStop() {
        super.onStop()
        isForeGround=false
    }
}
