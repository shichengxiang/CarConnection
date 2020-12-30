package com.linruan.carconnection.moudle

import com.linruan.carconnection.adapter.RepairCarOrderAdapter
import com.linruan.carconnection.entities.net.GetRepairOrderListResponse
import com.linruan.carconnection.logE
import com.linruan.carconnection.moudle.presenter.BasePresenter
import com.linruan.carconnection.moudle.views.BaseView
import com.linruan.carconnection.net.BaseResponse
import com.linruan.carconnection.net.Client
import com.linruan.carconnection.net.JsonCallback
import com.linruan.carconnection.view.refreshlayout.MRefreshLayout
import com.lzy.okgo.model.Response

/**
 * author：shichengxiang on 2020/6/17 17:38
 * email：1328911009@qq.com
 */
class RepairCarOrderPresenter(view: RepairCarOrderView?) : BasePresenter<RepairCarOrderView>(view) {
    private val PERPAGE=10
    fun getRepairCarOrderList(mRefreshLayout:MRefreshLayout,adapter:RepairCarOrderAdapter,status:Int,currentPage:Int,isLoadMore:Boolean){
        Client.getInstance().getRepairCarOrderList(status,currentPage,PERPAGE,object :JsonCallback<GetRepairOrderListResponse>(){
            override fun onSuccess(response: Response<GetRepairOrderListResponse?>?) {
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

            override fun onError(response: Response<GetRepairOrderListResponse?>?) {
                super.onError(response)
                if(isLoadMore){
                    mRefreshLayout.finishLoadMore()
                }else{
                    mRefreshLayout.finishRefresh()
                }
            }
        })
    }
}
interface RepairCarOrderView:BaseView{

}