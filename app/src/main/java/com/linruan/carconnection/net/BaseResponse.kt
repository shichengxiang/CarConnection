package com.linruan.carconnection.net

/**
 * author：shichengxiang on 2020/6/1 09:48
 * email：1328911009@qq.com
 */
open class BaseResponse {
    var error=-1
    var msg=""

    fun isSuccess()=error==0
}