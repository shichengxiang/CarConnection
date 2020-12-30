package com.linruan.carconnection.entities.net

import com.linruan.carconnection.net.BaseResponse

/**
 * author：shichengxiang on 2020/6/8 15:22
 * email：1328911009@qq.com
 */
class GetTypeAndClassify : BaseResponse() {
    var data: DataBean? = null

    class DataBean {
        var leixing: ArrayList<LeixingBean>? = null
        var items: ArrayList<ItemsBean>? = null
    }
}

class LeixingBean {
    /**
     * id : 442
     * title : 类型一
     * cover :
     */
    var id = 0
    var title: String? = null
    var cover: String? = null

}

class ItemsBean {
    /**
     * id : 445
     * title : 分类一
     * cover :
     */
    var id = 0
    var title: String? = null
    var cover: String? = null

}
