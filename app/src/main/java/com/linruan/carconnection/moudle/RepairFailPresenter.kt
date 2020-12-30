package com.linruan.carconnection.moudle

import com.linruan.carconnection.dialog.LoadingDialog
import com.linruan.carconnection.moudle.presenter.BasePresenter
import com.linruan.carconnection.moudle.views.BaseView
import com.linruan.carconnection.net.BaseResponse
import com.linruan.carconnection.net.Client
import com.linruan.carconnection.net.JsonCallback
import com.lzy.okgo.model.Response

/**
 * author：shichengxiang on 2020/6/17 18:41
 * email：1328911009@qq.com
 */
class RepairFailPresenter(view: RepairFailView?) : BasePresenter<RepairFailView>(view) {
    fun commitFailReason(orderId:String,failImgs:String,reason:String){
        LoadingDialog.loading(mContext)
        Client.getInstance().confirmRepairFail(orderId,failImgs,reason,"",object :JsonCallback<BaseResponse>(){
            override fun onFinish() {
                LoadingDialog.dismiss()
            }
            override fun onSuccess(response: Response<BaseResponse?>?) {
                var body=response?.body()
                if(body?.isSuccess()==true){
                    mView?.onCommitReasonSuccess()
                }else{
                    mView?.onError(body?.msg?:"提交异常")
                }
            }
        })
    }
}
interface RepairFailView:BaseView{
    /**
     * 维修失败原因提交成功
     */
    fun onCommitReasonSuccess()
}