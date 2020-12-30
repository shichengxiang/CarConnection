package com.linruan.carconnection.entities.net

import com.linruan.carconnection.net.BaseResponse

/**
 * author：shichengxiang on 2020/6/1 14:53
 * email：1328911009@qq.com
 */
class GetRepairType:BaseResponse() {
    /**
     * error : 0
     * msg : 数据获取成功
     * data : {"list":[{"id":426,"title":"电动车","cover":"http://chelianyizhong.com/public/uploads/20200518/9acc0f9d1fb6f4347dbc5cbdda214bc1.png"},{"id":427,"title":"自行车","cover":"http://chelianyizhong.com/public/uploads/20200518/4eb75fd0d32e8cd8e4cf008ee2894d72.png"},{"id":428,"title":"摩托车","cover":"http://chelianyizhong.com/public/uploads/20200518/f529aaa153db6b2cdfeaf386e6ad7327.png"}]}
     */
    var data: DataBean? = null

    class DataBean {
        var list: ArrayList<ListBean>? = null

        class ListBean {
            /**
             * id : 426
             * title : 电动车
             * cover : http://chelianyizhong.com/public/uploads/20200518/9acc0f9d1fb6f4347dbc5cbdda214bc1.png
             */
            var id = ""
            var title: String? = null
            var cover: String? = null
            var cover_checked:String?=null
            var isChecked=false
        }
    }
}
