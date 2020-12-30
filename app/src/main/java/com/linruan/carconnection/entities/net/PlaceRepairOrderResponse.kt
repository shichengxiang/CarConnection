package com.linruan.carconnection.entities.net

import com.linruan.carconnection.net.BaseResponse

/**
 * author：shichengxiang on 2020/6/18 17:26
 * email：1328911009@qq.com
 */
class PlaceRepairOrderResponse:BaseResponse() {
    var data:DataBean?=null
    class DataBean{
        var time=0L
        var orderno:String?=null
        var status=1 //==2时不用支付
    }
}
