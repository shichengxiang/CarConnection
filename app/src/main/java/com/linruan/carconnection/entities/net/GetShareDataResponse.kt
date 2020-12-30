package com.linruan.carconnection.entities.net

import com.linruan.carconnection.net.BaseResponse

/**
 * 项目名称：CarConnection
 * 类描述：
 * 创建人：shichengxiang
 * 创建时间：2020/8/10 18:55
 * @version
 */
class GetShareDataResponse:BaseResponse() {
    var data:DataBean?=null
    class DataBean{
        var title:String?=null
        var image:String?=null
        var content:String?=null
        var url:String?=null
    }
}
