package com.linruan.carconnection.moudle

import com.linruan.carconnection.entities.GetRepairOrderDetailResponse
import com.linruan.carconnection.entities.RepairOrderDetail
import com.linruan.carconnection.isSuccess
import com.linruan.carconnection.moudle.presenter.BasePresenter
import com.linruan.carconnection.moudle.views.BaseView
import com.linruan.carconnection.net.BaseResponse
import com.linruan.carconnection.net.Client
import com.linruan.carconnection.net.JsonCallback
import com.lzy.okgo.model.Response

/**
 * author：shichengxiang on 2020/6/17 18:28
 * email：1328911009@qq.com
 */
class RepairOrderDetailPresenter(view: RepairOrderDetailView?) : BasePresenter<RepairOrderDetailView>(view) {

    /**
     * 确认维修成功
     */
    fun confirmRepairSuccess(orderId:String){
        Client.getInstance().confirmRepairSuccess(orderId,"confirm",object :JsonCallback<BaseResponse>(){
            override fun onSuccess(response: Response<BaseResponse?>?) {
                var body=response?.body()
                if(body?.isSuccess()==true){
                    mView?.onConfirmRepairedSuccess()
                }else{
                    mView?.onError(body?.msg?:"提交异常")
                    mView?.showError()
                }
            }
        })
    }

    /**
     * 修车订单详情
     */
    fun getOrderDetail(repairId:String){
        Client.getInstance().getDetailOfRepairOrder(repairId,object :JsonCallback<GetRepairOrderDetailResponse>(){
            override fun onSuccess(response: Response<GetRepairOrderDetailResponse?>?) {
                var body=response?.body()
                if(body?.isSuccess()==true){
                    mView?.onGetOrderDetailSuccess(body?.data)
                }else{
                    mView?.onError(body?.msg?:"获取订单信息失败")
                }
            }
        })
    }
    /**
     * 取消修车订单
     */
    fun cancelRepairOrder(repairId:String){
        Client.getInstance().cancelRepairOrder(repairId,"cancelorder",object :JsonCallback<BaseResponse>(){
            override fun onSuccess(response: Response<BaseResponse?>?) {
                if(response?.body()?.isSuccess()==true){
                    mView?.onCancelRepairOrderSuccess()
                }else{
                    mView?.onError(response?.body()?.msg?:"取消失败")
                }
            }
        })
    }
    /**
     * 删除修车订单
     */
    fun deleteRepairOrder(repairId:String){
        Client.getInstance().deleteRepairOrder(repairId,object :JsonCallback<BaseResponse>(){
            override fun onSuccess(response: Response<BaseResponse?>?) {
                if(response?.body().isSuccess()){
                    mView?.onDeleteRepairOrderSuccess()
                }else{
                    mView?.onError(response?.body()?.msg?:"删除订单异常")
                }
            }
        })

    }

}
interface RepairOrderDetailView:BaseView{
    fun onConfirmRepairedSuccess()
    fun onGetOrderDetailSuccess(repairOrderDetail: RepairOrderDetail?)
    fun showError()
    fun onCancelRepairOrderSuccess()
    fun onDeleteRepairOrderSuccess()
}
