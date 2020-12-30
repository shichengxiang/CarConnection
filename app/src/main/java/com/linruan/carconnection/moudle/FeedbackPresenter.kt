package com.linruan.carconnection.moudle

import com.linruan.carconnection.dialog.LoadingDialog
import com.linruan.carconnection.moudle.presenter.BasePresenter
import com.linruan.carconnection.moudle.views.BaseView
import com.linruan.carconnection.net.BaseResponse
import com.linruan.carconnection.net.Client
import com.linruan.carconnection.net.JsonCallback
import com.lzy.okgo.model.Response

class FeedbackPresenter(view: FeedBackView?) : BasePresenter<FeedBackView>(view) {

    /**
     * 意见反馈
     */
    fun feedback(content:String,contact:String){
        LoadingDialog.loading(mContext)
        Client.getInstance().feedback(content,contact,object :JsonCallback<BaseResponse>(){
            override fun onFinish() {
                LoadingDialog.dismiss()
            }
            override fun onSuccess(response: Response<BaseResponse?>?) {
                if(response?.body()?.isSuccess()==true){
                    mView?.onFeedBackSuccess()
                }else{
                    mView?.onError(response?.body()?.msg?:"提交失败")
                }
            }
        })
    }
}
interface FeedBackView:BaseView{
    fun onFeedBackSuccess()
}