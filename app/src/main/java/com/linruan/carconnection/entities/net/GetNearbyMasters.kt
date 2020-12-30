package com.linruan.carconnection.entities.net

import com.linruan.carconnection.entities.Master
import com.linruan.carconnection.net.BaseResponse

/**
 * author：shichengxiang on 2020/6/1 13:34
 * email：1328911009@qq.com
 */
class GetMasterList : BaseResponse() {
    var data: DataBean? = null

    class DataBean {
        var list: ArrayList<Master>? = null
    }

}
