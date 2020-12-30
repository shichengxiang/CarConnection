package com.linruan.carconnection.moudle

import com.linruan.carconnection.dialog.LoadingDialog
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
 * author：shichengxiang on 2020/6/23 09:25
 * email：1328911009@qq.com
 */
class MasterRepairOrderDetailPresenter(view: MasterRepairOrderDetailView?) : BasePresenter<MasterRepairOrderDetailView>(view) {

    /**
     * 提交等待客户确认
     */
    fun commit(repairId: String,partPrices:String,partTotal:String,otherPartPrice:String,otherPartTotal:String,otherImgs:String,workerIntro:String){
        Client.getInstance().commitFinishAndWaitConfirm(repairId,partPrices,partTotal,otherPartPrice,otherPartTotal,otherImgs,workerIntro,object :JsonCallback<BaseResponse>(){
            override fun onFinish() {
                LoadingDialog.dismiss()
            }

            override fun onSuccess(response: Response<BaseResponse?>?) {
                if(response?.body()?.isSuccess()==true){
                    mView?.onCommitSuccess()
                }else{
                    mView?.onError(response?.body()?.msg?:"提交失败")
                }
            }
        })
    }

    /**
     * 师傅确认提交失败
     */
    fun confirmRepairFail(repairId: String){
        LoadingDialog.loading(mContext)
        Client.getInstance().confirmRepairFailForWorker(repairId,object :JsonCallback<BaseResponse>(){
            override fun onFinish() {
                LoadingDialog.dismiss()
            }
            override fun onSuccess(response: Response<BaseResponse?>?) {
                if(response?.body().isSuccess()){
                    mView?.onConfirmFailSuccess()
                }else{
                    mView?.onError(response?.body()?.msg?:"提交异常")
                }
            }
        })
    }
    /**
     * 维修详情
     */
    fun getDetail(repairId: String) {
        LoadingDialog.loading(mContext)
        Client.getInstance()
                .getDetailOfRepairOrder(repairId, object : JsonCallback<GetRepairOrderDetailResponse>() {
                    override fun onFinish() {
                        LoadingDialog.dismiss()
                    }

                    override fun onSuccess(response: Response<GetRepairOrderDetailResponse?>?) {
                        var body = response?.body()
                        if (body?.isSuccess() == true) {
                            mView?.onGetDetailSuccess(body.data)
                        } else {
                            mView?.onError(body?.msg ?: "获取订单信息失败")
                        }
                    }
                })
    }
}

interface MasterRepairOrderDetailView : BaseView {
    fun onGetDetailSuccess(repairOrderDetail: RepairOrderDetail?)
    fun onCommitSuccess()
    fun onConfirmFailSuccess()
}
