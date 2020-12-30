package com.linruan.carconnection.entities.net

import com.linruan.carconnection.net.BaseResponse

/**
 * author：shichengxiang on 2020/6/10 15:27
 * email：1328911009@qq.com
 */
class GetGoodsDetailResponse : BaseResponse() {
    var data: DataBean? = null

    class DataBean {
        /**
         * id : 56
         * group_id : 1
         * items_id : 438
         * brand_id : 2
         * title : sadfsdwerwerdd1111
         * cover : http://yz.d.cn/public/uploads/20200604/2a1953475bb8f1016110d1761c137422.png
         * album :
         * markprice : 50.00
         * price : 39.00
         * sales : 0
         * content : http://yz.d.cn/api/goods/detail/id/56.html
         * is_index : 0
         * is_discount : 1
         * sort : 0
         * status : 1
         * create_time : 1591264313
         * sku : [{"id":1,"title":"345","markprice":"3.45","price":"3.45"},{"id":2,"title":"123","markprice":"123.12","price":"0.03"},{"id":3,"title":"123","markprice":"12.31","price":"23.12"}]
         * comments : []
         */
        var id = ""
        var group_id = 0
        var items_id = 0
        var brand_id = 0
        var title: String? = null
        var cover: String? = null
        var album: ArrayList<AlbumBean>? = null
        var markprice: String? = null
        var price: String? = null
        var sales = 0
        var content: String? = null
        var is_index = 0
        var is_discount = 0
        var sort = 0
        var status = 0
        var create_time = 0
        var sku: ArrayList<SkuBean>? = null
        var comments: ArrayList<Comment>? = null
        var cart_count=0
    }
    class AlbumBean{
        var id=""
        var width=0f
        var height=0f
        var filepath=""
    }

    class SkuBean {
        /**
         * id : 1
         * title : 345
         * markprice : 3.45
         * price : 3.45
         */
        var id :String?=null
        var title: String? = null
        var markprice: String? = null
        var price: String? = null

    }
    class Comment{
        var id:String?=null
        var name:String?=null
        var avatar:String?=null
        var content:String?=null
        var create_time:String?=null
    }
}
