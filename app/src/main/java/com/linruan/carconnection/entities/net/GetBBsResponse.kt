package com.linruan.carconnection.entities.net

import com.linruan.carconnection.entities.BBS
import com.linruan.carconnection.net.BaseResponse

/**
 * author：shichengxiang on 2020/6/8 11:24
 * email：1328911009@qq.com
 */
class GetBBsResponse:BaseResponse() {
    var data:DataBean?=null
    class DataBean{
        var list:ArrayList<BBS>?=null
    }
}