package com.linruan.carconnection.entities

import com.linruan.carconnection.utils.Util

/**
 * author：shichengxiang on 2020/6/22 16:52
 * email：1328911009@qq.com
 */
class FaultPart {
    var id = 0
    var title = ""
    var price=0.0

    constructor()
    constructor(id: Int, title: String, price: String){
        this.id=id
        this.title=title
        this.price=Util.stringToDouble(price)
    }
}