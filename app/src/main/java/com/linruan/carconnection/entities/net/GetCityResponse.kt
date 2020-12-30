package com.linruan.carconnection.entities.net

import com.linruan.carconnection.entities.City

/**
 * author：shichengxiang on 2020/6/12 13:07
 * email：1328911009@qq.com
 */
class GetCityResponse {
    var data:DataBean?=null
    class DataBean{
        var list:ArrayList<City>?=null
    }
}