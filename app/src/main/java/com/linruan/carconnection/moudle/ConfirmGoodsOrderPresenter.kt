package com.linruan.carconnection.moudle

import com.linruan.carconnection.entities.Address
import com.linruan.carconnection.entities.net.GetAddressListResponse
import com.linruan.carconnection.entities.net.GetPlaceGoodsOrderResponse
import com.linruan.carconnection.moudle.presenter.BasePresenter
import com.linruan.carconnection.moudle.views.BaseView
import com.linruan.carconnection.net.Client
import com.linruan.carconnection.net.JsonCallback
import com.lzy.okgo.model.Response

/**
 * author：shichengxiang on 2020/6/15 11:35
 * email：1328911009@qq.com
 */
class ConfirmGoodsOrderPresenter(view: ConfirmGoodsOrderView?) : BasePresenter<ConfirmGoodsOrderView>(view) {

    /**
     * 购物车商品下单
     */
    fun placeGoodsOrder(cartIds: String, addressId: String) {
        Client.getInstance()
                .placeGoodsOrderFromCart(cartIds, addressId, object : JsonCallback<GetPlaceGoodsOrderResponse>() {
                    override fun onSuccess(response: Response<GetPlaceGoodsOrderResponse?>?) {
                        if (response?.body()
                                    ?.isSuccess() == true) {
                            mView?.onPlaceOrderSuccess(response?.body()?.data?:"")
                        } else {
                            mView?.onError(response?.body()?.msg ?: "下单异常")
                        }
                    }
                })
    }
    /**
     * 商品详情下单
     */
    fun placeGoodsOrderInDetail(goodsId:String,skuId:String,num:Int,addressId: String) {
        Client.getInstance()
                .placeGoodsOrderInDetail(goodsId,skuId,num, addressId, object : JsonCallback<GetPlaceGoodsOrderResponse>() {
                    override fun onSuccess(response: Response<GetPlaceGoodsOrderResponse?>?) {
                        if (response?.body()
                                    ?.isSuccess() == true) {
                            mView?.onPlaceOrderSuccess(response?.body()?.data?:"")
                        } else {
                            mView?.onError(response?.body()?.msg ?: "下单异常")
                        }
                    }
                })
    }

    /**
     * 拉取地址
     */
    fun getDefaultAddress() {
        Client.getInstance()
                .getAddressList(object : JsonCallback<GetAddressListResponse>() {
                    override fun onSuccess(response: Response<GetAddressListResponse?>?) {
                        if (response?.body()
                                    ?.isSuccess() == true) {
                            var list = response?.body()?.data?.list
                            if (list?.size ?: 0 > 0) {
                                mView?.onGetAddress(list!![0])
                            } else {
                                mView?.onGetAddress(null)
                            }
                        } else {
                            mView?.onError("获取收货地址失败")
                        }
                    }
                })
    }
}

interface ConfirmGoodsOrderView : BaseView {
    fun onGetAddress(address: Address?)
    fun onPlaceOrderSuccess(orderNo:String)
    fun onPaySuccess(res:String)
}
