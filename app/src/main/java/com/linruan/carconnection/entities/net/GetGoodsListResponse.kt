package com.linruan.carconnection.entities.net

import com.linruan.carconnection.entities.Goods
import com.linruan.carconnection.net.BaseResponse

/**
 * author：shichengxiang on 2020/6/11 17:17
 * email：1328911009@qq.com
 */
class GetGoodsListResponse : BaseResponse() {
    var data: DataBean? = null

    class DataBean {
        var brandlist: ArrayList<ItemsBean>? = null
        var list: ArrayList<Goods>? = null
    }
}