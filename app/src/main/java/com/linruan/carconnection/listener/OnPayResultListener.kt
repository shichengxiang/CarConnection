package com.linruan.carconnection.listener

/**
 * 项目名称：CarConnection
 * 类描述：
 * 创建人：shichengxiang
 * 创建时间：2020/8/7 1:24
 * @version
 */
interface OnPayResultListener {
    fun onSuccess()
    fun onFail(err:String)
    fun onCancel()
}
