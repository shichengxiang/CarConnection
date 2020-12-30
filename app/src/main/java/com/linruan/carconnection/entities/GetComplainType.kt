package com.linruan.carconnection.entities

import com.linruan.carconnection.net.BaseResponse

/**
 * author：shichengxiang on 2020/6/1 14:53
 * email：1328911009@qq.com
 */
class GetComplainType : BaseResponse() {
    var data: DataBean? = null

    class DataBean {
        var list: ArrayList<ComplainType>? = null
    }

    class ComplainType {
        var id = ""
        var title: String? = null
    }
}