package com.linruan.carconnection.entities

import java.io.Serializable
import java.math.BigDecimal

class Goods : Serializable {
    var id = ""
    var title = ""
    var goods_title: String? = null
    var goods_id = 0
    var sku_id: String? = null
    var cover: String? = null
    var goods_cover: String? = null
    var sku: String? = null
    var price = BigDecimal.ZERO
    var selectedSkuBean:SkuBean?=null //选中后的规格
    var isSelected = false
    var sales = 0
    var num = 1

    constructor()
    constructor(id: String, price: BigDecimal) {
        this.id = id
        this.price = price
    }
}
