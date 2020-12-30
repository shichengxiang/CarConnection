package com.linruan.carconnection.entities.net

import com.linruan.carconnection.entities.FaultPart

/**
 * author：shichengxiang on 2020/6/1 14:50
 * email：1328911009@qq.com
 */
class GetFaults {
    /**
     * error : 0
     * msg : 数据获取成功
     * data : {"list":[{"id":429,"title":"电瓶没电","cover":""},{"id":430,"title":"车胎没气","cover":""},{"id":431,"title":"大灯不亮","cover":""},{"id":432,"title":"车胎没气","cover":""},{"id":433,"title":"刹车失灵","cover":""},{"id":434,"title":"没油啦","cover":""},{"id":435,"title":"打不着火","cover":""},{"id":436,"title":"车胎没气","cover":""},{"id":437,"title":"大灯不亮","cover":""}]}
     */
    var data: DataBean? = null

    class DataBean {
        var list: ArrayList<FaultPart>? = null
    }
}