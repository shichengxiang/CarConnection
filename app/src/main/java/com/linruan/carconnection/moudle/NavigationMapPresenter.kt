package com.linruan.carconnection.moudle

import com.linruan.carconnection.dialog.LoadingDialog
import com.linruan.carconnection.isSuccess
import com.linruan.carconnection.moudle.presenter.BasePresenter
import com.linruan.carconnection.moudle.views.BaseView
import com.linruan.carconnection.net.BaseResponse
import com.linruan.carconnection.net.Client
import com.linruan.carconnection.net.JsonCallback
import com.lzy.okgo.model.Response

/**
 * author：shichengxiang on 2020/6/22 13:53
 * email：1328911009@qq.com
 */
class NavigationMapPresenter(view: NavigationMapView?) : BasePresenter<NavigationMapView>(view) {

    fun reachDestination(repairId:String){
        LoadingDialog.loading(mContext)
        Client.getInstance().reachDestination(repairId,object :JsonCallback<BaseResponse>(){
            override fun onFinish() {
                LoadingDialog.dismiss()
            }

            override fun onSuccess(response: Response<BaseResponse?>?) {
                var body=response?.body()
                if(body?.isSuccess()==true){
                    mView?.onReachDestinationSuccess()
                }else{
                    mView?.onError(body?.msg?:"网络异常")
                }
            }
        })
    }
    fun cancelRepairOrder(repairId:String){
        LoadingDialog.loading(mContext)
        Client.getInstance().cancelRepairOrder(repairId,"",object :JsonCallback<BaseResponse>(){
            override fun onFinish() {
                LoadingDialog.dismiss()
            }

            override fun onSuccess(response: Response<BaseResponse?>?) {
                if(response?.body().isSuccess()){
                    mView?.onCancelRepairOrderSuccess()
                }else{
                    mView?.onError(response?.body()?.msg?:"取消订单异常")
                }
            }
        },true)
    }
}
interface NavigationMapView:BaseView{
    /**
     * 我已到达
     */
    fun onReachDestinationSuccess()
    fun onCancelRepairOrderSuccess()
}
