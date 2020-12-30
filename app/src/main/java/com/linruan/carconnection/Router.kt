package com.linruan.carconnection

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import java.io.Serializable

object Router {
    private var lastClickTime = 0L
    private val MAXINTERVAL = 400L //间隔时间
    fun openUI(context: Context?, cls: Class<*>) {
        if (System.currentTimeMillis() - lastClickTime < MAXINTERVAL) {
            logE("间隔短 拦截了")
            return
        }
        lastClickTime = System.currentTimeMillis()
        context?.startActivity(Intent(context, cls))
    }

    fun openUI(context: Context?, cls: Class<*>, bundle: Map<String, Any>) {
        if (System.currentTimeMillis() - lastClickTime < MAXINTERVAL) {
            return
        }
        lastClickTime = System.currentTimeMillis()
        context?.startActivity(Intent(context, cls).apply {
            for ((key, v) in bundle) {
                when (v) {
                    Int -> putExtra(key, v as Int)
                    String -> putExtra(key, v as String)
                    Boolean -> putExtra(key, v as Boolean)
                    else -> {
                        var b = Bundle()
                        b.putSerializable(key, v as Serializable)
                        putExtras(b)
                    }
                }
            }
        })
    }

    fun openUI(context: Context?, cls: Class<*>, bundle: Bundle) {
        if (System.currentTimeMillis() - lastClickTime < MAXINTERVAL) {
            return
        }
        lastClickTime = System.currentTimeMillis()
        context?.startActivity(Intent(context, cls).apply {
            putExtras(bundle)
        })
    }

    fun openUIForResult(context: Context?, cls: Class<*>, requestCode: Int) {
        if (System.currentTimeMillis() - lastClickTime < MAXINTERVAL) {
            return
        }
        lastClickTime = System.currentTimeMillis()
        (context as Activity).startActivityForResult(Intent(context, cls), requestCode)
    }

    fun openUIForResult(context: Context?, cls: Class<*>, requestCode: Int, bundle: Map<String, Any>) {
        if (System.currentTimeMillis() - lastClickTime < MAXINTERVAL) {
            return
        }
        lastClickTime = System.currentTimeMillis()
        (context as Activity)?.startActivityForResult(Intent(context, cls).apply {
            for ((key, v) in bundle) {
                when (v) {
                    Int -> putExtra(key, v as Int)
                    String -> putExtra(key, v as String)
                    Boolean -> putExtra(key, v as Boolean)
                    else -> {
                        var b = Bundle()
                        b.putSerializable(key, v as Serializable)
                        putExtras(b)
                    }
                }
            }
        }, requestCode)
    }

    fun openUIForResult(context: Context?, cls: Class<*>, requestCode: Int, bundle: Bundle) {
        if (System.currentTimeMillis() - lastClickTime < MAXINTERVAL) {
            return
        }
        lastClickTime = System.currentTimeMillis()
        (context as Activity).startActivityForResult(Intent(context, cls).apply {
            putExtras(bundle)
        }, requestCode)
    }
}
