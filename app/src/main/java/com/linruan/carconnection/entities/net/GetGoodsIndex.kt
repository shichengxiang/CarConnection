package com.linruan.carconnection.entities.net

import com.linruan.carconnection.entities.Goods
import com.linruan.carconnection.net.BaseResponse

/**
 * author：shichengxiang on 2020/6/9 14:27
 * email：1328911009@qq.com
 * 商城首页
 */
class GetGoodsIndex : BaseResponse() {
    var data: DataBean? = null

    class DataBean {
        var slides: ArrayList<SlidesBean>? = null
        var discount: ArrayList<DiscountBean>? = null
        var list: ArrayList<Goods>? = null
        var itemlist:ArrayList<ItemListBean>?=null
        var cartnum=0
    }
    class ItemListBean{
        var id = 0
        var cover: String? = null
        var title:String?=null
    }

    class SlidesBean {
        /**
         * id : 84
         * cover : http://chelianyizhong.com/public/uploads/20200608/9b3c2882a49fdbed17df03c8f289f609.png
         */
        var id = 0
        var cover: String? = null
        var url:String?=null

    }

    class DiscountBean {
        /**
         * id : 56
         * cover : http://chelianyizhong.com/public/uploads/20200604/2a1953475bb8f1016110d1761c137422.png
         * title : sadfsdwerwerdd1111
         * price : 39.00
         * markprice : 50.00
         */
        var id = 0
        var cover: String? = null
        var title: String? = null
        var price: String? = null
        var markprice: String? = null

    }
}
