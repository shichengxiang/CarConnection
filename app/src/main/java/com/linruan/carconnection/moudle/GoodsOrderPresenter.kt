package com.linruan.carconnection.moudle

import com.cjt2325.cameralibrary.util.LogUtil
import com.linruan.carconnection.adapter.StoreOrderAdapter
import com.linruan.carconnection.dialog.LoadingDialog
import com.linruan.carconnection.entities.net.GetExpressResponse
import com.linruan.carconnection.entities.net.GetGoodsOrderListResponse
import com.linruan.carconnection.isSuccess
import com.linruan.carconnection.moudle.presenter.BasePresenter
import com.linruan.carconnection.moudle.views.BaseView
import com.linruan.carconnection.net.BaseResponse
import com.linruan.carconnection.net.Client
import com.linruan.carconnection.net.JsonCallback
import com.linruan.carconnection.view.refreshlayout.MRefreshLayout
import com.lzy.okgo.model.Response

/**
 * author：shichengxiang on 2020/6/12 10:06
 * email：1328911009@qq.com
 */
class GoodsOrderPresenter(view: GoodsOrderView?) : BasePresenter<GoodsOrderView>(view) {
    val PERPAGE = 10

    //状态码  0 全部 1：待付款  2：代发货  3：待收货   4：退货售后
    fun getGoodsOrderList(mRefreshLayout: MRefreshLayout, adapter: StoreOrderAdapter, status: Int, curretPgae: Int,isLoadMore:Boolean) {
        Client.getInstance()
                .getGoodsOrderList(status, curretPgae, PERPAGE, object : JsonCallback<GetGoodsOrderListResponse>() {
                    override fun onSuccess(response: Response<GetGoodsOrderListResponse?>?) {
                        var body=response?.body()
                        if(body?.isSuccess()==true){
                            var list=body.data?.list
                            if(!isLoadMore){
                                adapter.setData(list)
                                mRefreshLayout.finishRefresh()
                            }else{
                                adapter.addData(list)
                                if(list?.size?:0<PERPAGE){
                                    mRefreshLayout.finishLoadMoreWithNoMoreData()
                                }else{
                                    mRefreshLayout.finishLoadMore()
                                }
                            }
                        }else{
                            mView?.onError(body?.msg?:"订单拉取失败")
                        }
                    }

                    override fun onError(response: Response<GetGoodsOrderListResponse?>?) {
                        super.onError(response)
                        if(isLoadMore){
                            mRefreshLayout.finishLoadMore()
                        }else{
                            mRefreshLayout.finishRefresh()
                        }
                    }
                })
    }

    /**
     * 获取物流公司信息
     */
    fun  getExpress(){
        Client.getInstance().getExpress(object :JsonCallback<GetExpressResponse>(){
            override fun onSuccess(response: Response<GetExpressResponse?>?) {
                if(response?.body().isSuccess()){
                    mView?.onGetExpressSuccess(response?.body()?.data?.list)
                }else{
                    LogUtil.e("拉取物流信息失败")
                }
            }
        })
    }
    fun pushExpress(express:String,number:String,orderId:String){
        LoadingDialog.loading(mContext)
        Client.getInstance()
            .pushExpress(express, number, orderId, object : JsonCallback<BaseResponse>() {
                override fun onFinish() {
                    LoadingDialog.dismiss()
                }
                override fun onSuccess(response: Response<BaseResponse?>?) {
                    if (response?.body().isSuccess()) {
                        mView?.onPushExpressSuccess()
                    } else {
                        mView?.onError(response?.body()?.msg ?: "提交异常")
                    }
                }
            })
    }
}

interface GoodsOrderView : BaseView {
    fun onGetExpressSuccess(data:ArrayList<GetExpressResponse.ExpressBean>?)
    fun onPushExpressSuccess()
}
