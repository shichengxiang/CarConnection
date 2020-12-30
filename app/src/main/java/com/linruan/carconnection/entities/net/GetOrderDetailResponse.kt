package com.linruan.carconnection.entities.net

import com.linruan.carconnection.entities.GoodsOrder
import com.linruan.carconnection.net.BaseResponse

/**
 * author：shichengxiang on 2020/6/17 10:38
 * email：1328911009@qq.com
 */
class GetOrderDetailResponse : BaseResponse() {
    var data: GoodsOrder? = null
}