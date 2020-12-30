package com.linruan.carconnection.moudle

import com.linruan.carconnection.dialog.LoadingDialog
import com.linruan.carconnection.moudle.presenter.BasePresenter
import com.linruan.carconnection.moudle.views.BaseView
import com.linruan.carconnection.net.BaseResponse
import com.linruan.carconnection.net.Client
import com.linruan.carconnection.net.JsonCallback
import com.lzy.okgo.model.Response

/**
 * author：shichengxiang on 2020/6/17 11:26
 * email：1328911009@qq.com
 */
class GoodsOrderDetailPresenter(view: GoodOrderDetailView?) : BasePresenter<GoodOrderDetailView>(view) {
    /**
     * 提醒发货
     */
    fun remindSend(orderId:String){
        LoadingDialog.loading(mContext)
        Client.getInstance().remindSendGoods(orderId,object :JsonCallback<BaseResponse>(){
            override fun onFinish() {
                LoadingDialog.dismiss()
            }
            override fun onSuccess(response: Response<BaseResponse?>?) {
                var body=response?.body()
                if(body?.isSuccess()==true){
                    mView?.onRemindSuccess()
                }else{
                    mView?.onError(body?.msg?:"提醒发货异常")
                }
            }
        })
    }

    fun deleteOrder(orderId:String){
        LoadingDialog.loading(mContext)
        Client.getInstance().deleteGoodsOrder(orderId,object :JsonCallback<BaseResponse>(){
            override fun onFinish() {
                LoadingDialog.dismiss()
            }
            override fun onSuccess(response: Response<BaseResponse?>?) {
                var body=response?.body()
                if(body?.isSuccess()==true){
                    mView?.onDeletGoodsOrderSuccess()
                }else{
                    mView?.onError(body?.msg?:"订单异常")
                }
            }
        })
    }
    fun cancelGoodsOrder(orderId:String){
        LoadingDialog.loading(mContext)
        Client.getInstance().cancelGoodsOrder(orderId,object :JsonCallback<BaseResponse>(){
            override fun onFinish() {
                LoadingDialog.dismiss()
            }
            override fun onSuccess(response: Response<BaseResponse?>?) {
                var body=response?.body()
                if(body?.isSuccess()==true){
                    mView?.onCancelGoodsOrderSuccess()
                }else{
                    mView?.onError(body?.msg?:"订单异常")
                }
            }
        })
    }
    fun confirmReceived(orderId:String){
        LoadingDialog.loading(mContext)
        Client.getInstance().confirmReceivedGoods(orderId,object :JsonCallback<BaseResponse>(){
            override fun onFinish() {
                LoadingDialog.dismiss()
            }
            override fun onSuccess(response: Response<BaseResponse?>?) {
                var body=response?.body()
                if(body?.isSuccess()==true){
                    mView?.onConfirmReceivedSuccess()
                }else{
                    mView?.onError(body?.msg?:"订单异常")
                }
            }
        })
    }
}
interface GoodOrderDetailView:BaseView{
    fun onDeletGoodsOrderSuccess()
    fun onCancelGoodsOrderSuccess()
    fun onConfirmReceivedSuccess()
    fun onRemindSuccess()
}