package com.linruan.carconnection.utils.update

import android.app.PendingIntent.getActivity
import android.content.Context
import android.os.Bundle
import android.os.Environment
import android.text.TextUtils
import androidx.fragment.app.FragmentActivity
import com.linruan.carconnection.BuildConfig

class UpdateManager {
    private var mActivity: Context? = null

    private constructor(context: Context) {
        this.mActivity = context
    }

    companion object {
        private var manager: UpdateManager? = null
        fun build(context: Context): UpdateManager {
//            if (manager == null) {
                manager = UpdateManager(context)
//            }
            return manager!!
        }
    }

    private var isForce = false
    fun checkVersion(
        newVersion: String,
        versionCode: Int,
        apkUrl: String,
        log: String,
        targetSize: String,
        isConstraint: Boolean,
        onUpdateListener:OnUpdateListener?
    ) {

        if (BuildConfig.VERSION_CODE >= versionCode) {
            onUpdateListener?.noNewVersion()
            return
        }
        //sd卡是否存在
        var path: String? = ""
        if (Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED || !Environment.isExternalStorageRemovable()) {
            try {
                path = mActivity?.externalCacheDir?.absolutePath
            } catch (e: Exception) {
                e.printStackTrace()
            }
            if (TextUtils.isEmpty(path)) {
                path =
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                        .absolutePath
            }
        } else {
            path = mActivity?.cacheDir?.absolutePath
        }
        var appBean = UpdateAppBean().apply {
            targetPath = path
            this.newVersion = newVersion
            this.versionCode = versionCode
            apkFileUrl = apkUrl
            updateLog = log
            updateDefDialogTitle = "版本更新"
            this.targetSize = targetSize
            this.isConstraint = isConstraint
            httpManager = OkGoUpdateUtil()
//            //更新日志
//            private String update_log;
//            //配置默认更新dialog 的title
//            private String update_def_dialog_title;
//            //新app大小
//            private String target_size;
//            //是否强制更新
//            private boolean constraint;
        }
        var bundle = Bundle().apply {
            putSerializable(UpdateAppManager.INTENT_KEY, appBean)
        }
        UpdateDialogFragment
            .newInstance(bundle)
            .setUpdateDialogFragmentListener(null) //暂时不处理 取消更新情况
            .show((mActivity as FragmentActivity).supportFragmentManager, "dialog")
    }
    interface OnUpdateListener{
        fun noNewVersion()
    }

}
