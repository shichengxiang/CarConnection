package com.linruan.carconnection.entities.net

import com.linruan.carconnection.entities.RepairOrder
import com.linruan.carconnection.net.BaseResponse

/**
 * author：shichengxiang on 2020/6/26 09:44
 * email：1328911009@qq.com
 */
class GetRunningOrderResponse:BaseResponse() {
    var data:DataBean?=null
    class DataBean{
        var slides:ArrayList<SlideBean>?=null
        var orders:RunningOrder?=null
        var worker:WorkerBean?=null
    }
    class SlideBean{
        var id=""
        var title=""
        var cover=""
        var url:String?=null
    }
    class WorkerBean{
        var name:String?=null
        var avatar:String?=null
        var leixing:String?=null
        var repair_count=0
        var comments_count=0
    }
    class RunningOrder{
        var id:String?=null
        var create_time:String?=null
        var remaining=0
        var step=-1
        var worker_id=""
    }
}
