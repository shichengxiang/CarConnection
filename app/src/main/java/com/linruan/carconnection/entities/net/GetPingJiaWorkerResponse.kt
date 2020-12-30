package com.linruan.carconnection.entities.net

import com.linruan.carconnection.net.BaseResponse

/**
 * 项目名称：CarConnection
 * 类描述：
 * 创建人：shichengxiang
 * 创建时间：2020/8/6 23:02
 * @version
 */
class GetPingJiaWorkerResponse:BaseResponse() {
    var data:ListBean?=null
    class ListBean{
        var list:ArrayList<PingJia>?=null
    }
    class PingJia{
        var id:String?=null
        var title:String?=null
        var isChecked=false
    }
}
