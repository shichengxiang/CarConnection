package com.linruan.carconnection

import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.blankj.utilcode.util.LogUtils
import com.linruan.carconnection.net.BaseResponse
import java.lang.Exception

val TAG = "linruan==="
fun logI(msg: String, vararg tag: String?) {
    LogUtils.i(msg)
}

fun logE(err: String, vararg tag: String?) {
    LogUtils.e(err)
}
fun Context.toast(msg:String){
    Toast.makeText(this.applicationContext,msg,Toast.LENGTH_SHORT).show()
}
fun tryCatch(block:()->Unit){
    try {
        block()
    }catch (e:Exception){
        e.printStackTrace()
    }
}
fun BaseResponse?.isSuccess():Boolean{
    return this?.error==0
}
