package com.linruan.carconnection.entities.net

import com.linruan.carconnection.entities.Goods
import com.linruan.carconnection.net.BaseResponse

/**
 * author：shichengxiang on 2020/6/10 17:57
 * email：1328911009@qq.com
 */
class GetCartListResponse:BaseResponse() {
    var data:DataBean?=null
    class DataBean{
        var list:ArrayList<Goods>?=null
    }
}