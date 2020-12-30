package com.linruan.carconnection.entities.net

import com.linruan.carconnection.entities.RepairOrder
import com.linruan.carconnection.net.BaseResponse

/**
 * author：shichengxiang on 2020/6/17 17:45
 * email：1328911009@qq.com
 */
class GetRepairOrderListResponse:BaseResponse() {
    var data:DataBean?=null
    class DataBean{
        var list:ArrayList<RepairOrder>?=null
    }
}