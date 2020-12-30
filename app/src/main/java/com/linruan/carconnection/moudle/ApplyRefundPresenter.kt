package com.linruan.carconnection.moudle

import com.linruan.carconnection.dialog.LoadingDialog
import com.linruan.carconnection.entities.GoodsOrder
import com.linruan.carconnection.entities.net.GetOrderDetailResponse
import com.linruan.carconnection.isSuccess
import com.linruan.carconnection.moudle.presenter.BasePresenter
import com.linruan.carconnection.moudle.views.BaseView
import com.linruan.carconnection.net.BaseResponse
import com.linruan.carconnection.net.Client
import com.linruan.carconnection.net.JsonCallback
import com.lzy.okgo.model.Response

/**
 * 项目名称：CarConnection
 * 类描述：
 * 创建人：shichengxiang
 * 创建时间：2020/8/10 19:40
 * @version
 */
class ApplyRefundPresenter(view: ApplyRefundView?) : BasePresenter<ApplyRefundView>(view) {
    fun getOrderDetail(orderId: String) {
        LoadingDialog.loading(mContext)
        Client.getInstance()
            .getGoodsOrderDetail(orderId, object : JsonCallback<GetOrderDetailResponse>() {
                override fun onFinish() {
                    LoadingDialog.dismiss()
                }

                override fun onSuccess(response: Response<GetOrderDetailResponse?>?) {
                    if (response?.body().isSuccess()) {
                        mView?.onGetGoodsOrderSuccess(response?.body()?.data)
                    }else{
                        mView?.onError(response?.body()?.msg?:"拉取订单信息失败")
                    }
                }
            })
    }

    fun applyRefund(orderId: String, explain: String, imgs: String, goodsId: String) {
        Client.getInstance()
            .applyRefund(orderId, explain, imgs, goodsId, object : JsonCallback<BaseResponse>() {
                override fun onFinish() {
                    LoadingDialog.dismiss()
                }

                override fun onSuccess(response: Response<BaseResponse?>?) {
                    if (response?.body().isSuccess()) {
                        mView?.onApllyRefundSuccess()
                    } else {
                        mView?.onError(response?.body()?.msg ?: "退款异常")
                    }
                }
            })
    }
}

interface ApplyRefundView : BaseView {
    fun onApllyRefundSuccess()
    fun onGetGoodsOrderSuccess(detail:GoodsOrder? )
}
