package com.linruan.carconnection.moudle

import com.linruan.carconnection.UserManager
import com.linruan.carconnection.adapter.BattleOrderAdapter
import com.linruan.carconnection.dialog.LoadingDialog
import com.linruan.carconnection.entities.net.GetMasterInfoResponse
import com.linruan.carconnection.entities.net.GetRepairOrderListResponse
import com.linruan.carconnection.isSuccess
import com.linruan.carconnection.moudle.presenter.BasePresenter
import com.linruan.carconnection.moudle.views.BaseView
import com.linruan.carconnection.net.BaseResponse
import com.linruan.carconnection.net.Client
import com.linruan.carconnection.net.JsonCallback
import com.linruan.carconnection.view.refreshlayout.MRefreshLayout
import com.lzy.okgo.model.Response

/**
 * author：shichengxiang on 2020/6/19 16:01
 * email：1328911009@qq.com
 */
class BattlePresenter(view: BattleView?) : BasePresenter<BattleView>(view) {
    val PERPAGE = 10

    /**
     * 师傅抢单列表
     *  status 1 待付款 2进行中  3已完成 4已取消
     *  step 1等待师傅接单 2师傅已接单 3师傅已到达 4维修完成待确认 5用户已确认  6订单失败
     */
    fun getRepairOrderOfMaster(
        mRefreshLayout: MRefreshLayout,
        adapter: BattleOrderAdapter,
        position: Int,
        curretPgae: Int,
        isLoadMore: Boolean
    ) {
        if (position == 0) {
            Client.getInstance()
                .getBattleOrderOfMaster(
                    curretPgae,
                    PERPAGE,
                    object : JsonCallback<GetRepairOrderListResponse>() {
                        override fun onSuccess(response: Response<GetRepairOrderListResponse?>?) {
                            var body = response?.body()
                            if (body?.isSuccess() == true) {
                                var list = body.data?.list
                                if (!isLoadMore) {
                                    adapter.setData(list, 0)
                                    mRefreshLayout.finishRefresh()
                                } else {
                                    adapter.addData(list)
                                    if (list?.size ?: 0 < PERPAGE) {
                                        mRefreshLayout.finishLoadMoreWithNoMoreData()
                                    } else {
                                        mRefreshLayout.finishLoadMore()
                                    }
                                }
                            } else {
                                mView?.onError(body?.msg ?: "订单拉取失败")
                            }
                        }

                        override fun onError(response: Response<GetRepairOrderListResponse?>?) {
                            super.onError(response)
                            if (isLoadMore) {
                                mRefreshLayout.finishLoadMore()
                            } else {
                                mRefreshLayout.finishRefresh()
                            }
                        }
                    })
        } else {
            var status = 2
            var step = 0
            if (position == 1) {
                status = 2
                step = 2
            } else if (position == 2) {
                status = 2
                step = 3
            } else if (position == 3) {
                status = 3
            }
            Client.getInstance()
                .getRepairCarOrderListOfMaster(
                    status,
                    curretPgae,
                    PERPAGE,
                    object : JsonCallback<GetRepairOrderListResponse>() {
                        override fun onSuccess(response: Response<GetRepairOrderListResponse?>?) {
                            var body = response?.body()
                            if (body?.isSuccess() == true) {
                                var list = body.data?.list
                                if (!isLoadMore) {
                                    adapter.setData(list, position)
                                    mRefreshLayout.finishRefresh()
                                } else {
                                    adapter.addData(list)
                                    if (list?.size ?: 0 < PERPAGE) {
                                        mRefreshLayout.finishLoadMoreWithNoMoreData()
                                    } else {
                                        mRefreshLayout.finishLoadMore()
                                    }
                                }
                            } else {
                                mView?.onError(body?.msg ?: "订单拉取失败")
                            }
                        }

                        override fun onError(response: Response<GetRepairOrderListResponse?>?) {
                            super.onError(response)
                            if (isLoadMore) {
                                mRefreshLayout.finishLoadMore()
                            } else {
                                mRefreshLayout.finishRefresh()
                            }
                        }
                    },
                    step
                )
        }
    }

    fun getMasterInfo() {
        //抢单设置信息
        Client.getInstance().getMasterSettingInfo(object : JsonCallback<GetMasterInfoResponse>() {
            override fun onSuccess(response: Response<GetMasterInfoResponse?>?) {
                if (response?.body().isSuccess())
                    UserManager.masterStatus = response?.body()?.data
            }

            override fun onError(response: Response<GetMasterInfoResponse?>?) {
            }
        })
    }

    /**
     * 删除订单
     */
    fun deleteRepairOrder(repairId:String){
        Client.getInstance().deleteRepairOrder(repairId,object :JsonCallback<BaseResponse>(){
            override fun onFinish() {
                LoadingDialog.dismiss()
            }

            override fun onSuccess(response: Response<BaseResponse?>?) {
                if(response?.body().isSuccess()){
                    mView?.onDeleteRepairOrderSuccess()
                }else{
                    mView?.onError(response?.body()?.msg?:"删除异常")
                }
            }
        },true)
    }
}

interface BattleView : BaseView {
    fun onDeleteRepairOrderSuccess()
}
