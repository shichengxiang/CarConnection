package com.linruan.carconnection.moudle

import com.linruan.carconnection.entities.Master
import com.linruan.carconnection.entities.RepairOrder
import com.linruan.carconnection.entities.net.GetAppVersion
import com.linruan.carconnection.entities.net.GetMasterList
import com.linruan.carconnection.entities.net.GetRunningOrderResponse
import com.linruan.carconnection.isSuccess
import com.linruan.carconnection.moudle.presenter.BasePresenter
import com.linruan.carconnection.moudle.views.BaseView
import com.linruan.carconnection.net.BaseResponse
import com.linruan.carconnection.net.Client
import com.linruan.carconnection.net.JsonCallback
import com.linruan.carconnection.toast
import com.linruan.carconnection.utils.Util.toast
import com.linruan.carconnection.utils.update.UpdateAppManager
import com.linruan.carconnection.utils.update.UpdateManager
import com.lzy.okgo.model.Response

/**
 * author：shichengxiang on 2020/6/2 14:05
 * email：1328911009@qq.com
 */
class HomePresenter(view: HomeView?) : BasePresenter<HomeView>(view) {

    /**
     * 师傅列表
     */
    fun getNearbyMasters(lng: String, lat: String) {
        Client.getInstance()
            .getNearbyMasters(lng, lat, "1", object : JsonCallback<GetMasterList>() {
                override fun onSuccess(response: Response<GetMasterList?>?) {
                    super.onSuccess(response)
                    var res = response?.body()
                    if (res?.isSuccess() == true) {
                        mView?.onGetMasterList(res!!.data!!.list)
                    } else {
                        mView?.onError(res?.msg)
                    }
                }

                override fun onError(response: Response<GetMasterList?>?) {
                    super.onError(response)
                    mView?.onError("网络异常")
                }
            })
    }

    /***
     * 获取进行中订单  广告
     */
    fun getRunningOrder() {
        Client.getInstance().getRunningOrder(object : JsonCallback<GetRunningOrderResponse>() {
            override fun onSuccess(response: Response<GetRunningOrderResponse?>?) {
                var body = response?.body()
                if (body?.isSuccess() == true) {
                    mView?.onGetRunningOrder(body?.data?.slides, body?.data?.orders,body?.data?.worker)
                }
            }

            override fun onError(response: Response<GetRunningOrderResponse?>?) {
            }
        })
    }

    /**
     * 取消订单
     */
    fun cancelRepairOrder(repairId: String) {
        Client.getInstance()
            .cancelRepairOrder(repairId, "cancel repair", object : JsonCallback<BaseResponse>() {
                override fun onSuccess(response: Response<BaseResponse?>?) {
                    if (response?.body().isSuccess()) {
                        mView?.onCancelOrderSuccess()
                    } else {
                        mView?.onError(response?.body()?.msg ?: "取消异常")
                    }
                }
            })
    }

    /**
     * 继续等待
     */
    fun goonWait(repairId: String) {
        Client.getInstance().goonWaitRepair(repairId, object : JsonCallback<BaseResponse>() {
            override fun onSuccess(response: Response<BaseResponse?>?) {
                if (response?.body().isSuccess()) {
                    mView?.onGoonWaitSuccess()
                } else {
                    mView?.onError(response?.body()?.msg ?: "接口异常")
                }
            }
        })
    }

    //版本检测
    fun checkVersion() {
        Client.getInstance().checkAppVersion(object : JsonCallback<GetAppVersion>() {
            override fun onSuccess(response: Response<GetAppVersion?>?) {
                if (response?.body().isSuccess()) {
                    if (response?.body().isSuccess()) {
                        var res = response?.body()?.data
//                        UpdateManager.build(mContext).checkVersion(
//                            res?.version_title ?: "",
//                            res?.version ?: 0,
//                            res?.down_url2 ?: "",
//                            res?.app_intro ?: "",
//                            "${res?.down_filesize?:1}M",
//                            false, object : UpdateManager.OnUpdateListener {
//                                override fun noNewVersion() {
//                                    toast("已经是最新版本了")
//                                }
//                            }
//                        )
                    }
                }
            }

            override fun onError(response: Response<GetAppVersion?>?) {
            }
        })
    }
//    /**
//     * 快速下单
//     */
//    fun fastPlaceOrder(workId:String,typeId:String,fault_ids:String,imgs:String,intro:String,lng:String,lat:String){
//        Client.getInstance().placeRepairOrder(workId,typeId,fault_ids,imgs,intro,lng,lat,"placeOrder",object : JsonCallback<PlaceRepairOrderResponse>(){
//            override fun onSuccess(response: Response<BaseResponse?>?) {
//                super.onSuccess(response)
//                if(response?.body()?.isSuccess()==true){
//                    mView?.onPlaceOrderSuccess()
//                }else{
//                    mView?.onError(response?.body()?.msg)
//                }
//            }
//
//            override fun onError(response: Response<BaseResponse?>?) {
//                super.onError(response)
//                mView?.onError("网络异常")
//            }
//        })
//    }
}

interface HomeView : BaseView {
    fun onGetMasterList(masters: ArrayList<Master>?)
    fun onPlaceOrderSuccess()
    fun onGetRunningOrder(
        slides: ArrayList<GetRunningOrderResponse.SlideBean>?,
        order: GetRunningOrderResponse.RunningOrder?,
        worker:GetRunningOrderResponse.WorkerBean?
    )

    /**
     * 取消订单成功
     */
    fun onCancelOrderSuccess()

    /**
     * 继续等待回调成功
     */
    fun onGoonWaitSuccess()
}
