package com.linruan.carconnection.entities.net

import com.linruan.carconnection.net.BaseResponse

/**
 * 项目名称：CarConnection
 * 类描述：
 * 创建人：shichengxiang
 * 创建时间：2020/9/4 19:25
 * @version
 */
class GetTelResponse:BaseResponse() {
    var data:DataBean?=null
    class DataBean{
        var phone:String?=null
    }
}
