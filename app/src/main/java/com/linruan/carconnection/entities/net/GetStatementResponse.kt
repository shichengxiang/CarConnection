package com.linruan.carconnection.entities.net

import com.linruan.carconnection.net.BaseResponse

/**
 * 项目名称：CarConnection
 * 类描述：
 * 创建人：shichengxiang
 * 创建时间：2020/8/9 16:54
 * @version
 */
class GetStatementResponse:BaseResponse() {
    var data:DataBean?=null
    class DataBean{
        var pricing_rule:String?=null
        var statement:String?=null
    }
}
