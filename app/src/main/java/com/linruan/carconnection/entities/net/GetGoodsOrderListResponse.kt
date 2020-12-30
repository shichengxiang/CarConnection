package com.linruan.carconnection.entities.net

import com.linruan.carconnection.entities.GoodsOrder
import com.linruan.carconnection.net.BaseResponse

/**
 * author：shichengxiang on 2020/6/12 10:42
 * email：1328911009@qq.com
 */
class GetGoodsOrderListResponse : BaseResponse() {
    var data: DataBean? = null

    class DataBean {
        var list: ArrayList<GoodsOrder>? = null
    }
}