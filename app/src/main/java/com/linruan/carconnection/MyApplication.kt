package com.linruan.carconnection

import android.app.ActivityManager
import android.app.Application
import android.content.Context
import android.os.Build
import android.webkit.WebView
import androidx.annotation.RequiresApi
import cn.jiguang.share.android.api.JShareInterface

/**
 * 项目名称：CarConnection
 * 类描述：
 * 创建人：shichengxiang
 * 创建时间：2020/5/13 16:09
 * @version
 */
class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        webviewSetPath(this)
        Global.init(this)
    }

    override fun onTrimMemory(level: Int) {
        super.onTrimMemory(level)
    }

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
    }

    override fun registerActivityLifecycleCallbacks(callback: ActivityLifecycleCallbacks?) {
        super.registerActivityLifecycleCallbacks(callback)
    }

    override fun unregisterActivityLifecycleCallbacks(callback: ActivityLifecycleCallbacks?) {
        super.unregisterActivityLifecycleCallbacks(callback)
    }

    //Android P 以及之后版本不支持同时从多个进程使用具有相同数据目录的WebView
    //为其它进程webView设置目录
    public fun webviewSetPath(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            var processName = getProcessName(context);
            if (!"com.linruan.carconnection".equals(processName)) {//判断不等于默认进程名称
                WebView.setDataDirectorySuffix(processName)
            }
        }
    }

    public fun getProcessName(context: Context): String? {
        if (context == null) return null
        var manager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        manager.runningAppProcesses?.forEach {
            if (it.pid == android.os.Process.myPid()) {
                return it.processName
            }
        }
        return null

    }
}
