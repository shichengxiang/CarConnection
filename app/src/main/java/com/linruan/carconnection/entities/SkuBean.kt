package com.linruan.carconnection.entities

import java.io.Serializable
import java.math.BigDecimal

/**
 * 项目名称：CarConnection
 * 类描述：
 * 创建人：shichengxiang
 * 创建时间：2020/8/26 15:46
 * @version
 */
class SkuBean:Serializable{
    var skuId=""
    var price=BigDecimal.ZERO
    var sku:String?=null
}
