package com.linruan.carconnection.net

import com.blankj.utilcode.util.ToastUtils
import com.google.gson.Gson
import com.linruan.carconnection.BuildConfig
import com.linruan.carconnection.UserManager
import com.linruan.carconnection.dialog.LoadingDialog
import com.linruan.carconnection.utils.Util
import com.litesuits.common.utils.MD5Util
import com.lzy.okgo.callback.AbsCallback
import com.lzy.okgo.model.Progress
import com.lzy.okgo.request.base.Request
import okhttp3.Response
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

open class JsonCallback<T> : AbsCallback<T?> {
    private var type: Type? = null
    private var clazz: Class<T>? = null

    constructor() {}
    constructor(type: Type?) {
        this.type = type
    }

    constructor(clazz: Class<T>?) {
        this.clazz = clazz
    }

    override fun onStart(request: Request<T?, out Request<Any, Request<*, *>>>?) {
        super.onStart(request)
        request?.apply {
            params("appId", Client.APPID)
            params("token", UserManager.getUser()?.token)
            params("nonce", System.currentTimeMillis().toString())
            params("timestramp", System.currentTimeMillis().toString())
            params("version", BuildConfig.VERSION_NAME)
        }
        var params = request?.params?.urlParamsMap
        val keys = ArrayList<String>(params?.keys!!)
        keys.sort()
        var stringBuilder = StringBuilder()
        keys.forEach {
            stringBuilder.append("$it =${params[it]}")
                .append("&")
        }
        stringBuilder.append(Client.APPSECRET)
        request?.params("sign", MD5Util.md5(stringBuilder.toString()).toString()) //签名

    }

    override fun onSuccess(response: com.lzy.okgo.model.Response<T?>?) {
        var body = response?.body()
        if (body is BaseResponse) {
            if (body.error == 401) {
                ToastUtils.showLong("用户登录已失效，请重新登录")
                UserManager.logout()
                return
            }
        }
    }

    override fun onError(response: com.lzy.okgo.model.Response<T?>?) {
        super.onError(response)
        if (response?.code() == 401) {
            // token失效
            ToastUtils.showLong("用户登录已失效，请重新登录")
            UserManager.logout()
            return
        }
        Util.toast("网络异常")

    }

    override fun onFinish() {
        super.onFinish()
        LoadingDialog.dismiss()
    }

    @Throws(Throwable::class)
    override fun convertResponse(response: Response): T? {
        val body = response.body() ?: return null
        var data: T? = null
        val gson = Gson()
        val jsonReader = com.google.gson.stream.JsonReader(body.charStream())
        data = if (type != null) {
            gson.fromJson(jsonReader, type)
        } else if (clazz != null) {
            gson.fromJson(jsonReader, clazz)
        } else {
            val genType = javaClass.genericSuperclass
            val type =
                (genType as ParameterizedType?)!!.actualTypeArguments[0]
            gson.fromJson(jsonReader, type)
        }
        return data
    }
}
