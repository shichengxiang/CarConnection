package com.linruan.carconnection.entities.net

import com.linruan.carconnection.net.BaseResponse

/**
 * author：shichengxiang on 2020/6/15 16:40
 * email：1328911009@qq.com
 */
class GetUserInfo:BaseResponse() {
    var data:DataBean?=null
    class DataBean{
        var balance:String?=null
        var group_id=1
        var login_time=1L
        var name:String?=null
        var avatar:String?=null
        var repair_obligation=0 //待
        var repair_finish=0
        var repair_cancel=0
        var order_obligation=0
        var order_wait_sending=0
        var order_wait_pickup=0
        var order_refund=0
    }
}
