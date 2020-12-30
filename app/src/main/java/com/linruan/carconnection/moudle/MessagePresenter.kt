package com.linruan.carconnection.moudle

import com.linruan.carconnection.adapter.MsgAdapter
import com.linruan.carconnection.entities.net.GetMessageListResponse
import com.linruan.carconnection.isSuccess
import com.linruan.carconnection.moudle.presenter.BasePresenter
import com.linruan.carconnection.moudle.views.BaseView
import com.linruan.carconnection.net.Client
import com.linruan.carconnection.net.JsonCallback
import com.linruan.carconnection.view.refreshlayout.MRefreshLayout
import com.lzy.okgo.model.Response

class MessagePresenter(view: MessageView?) : BasePresenter<MessageView>(view) {

    private var mCurrentPage=1
    private val PREPAGE=10
    fun getMessgaeList(mold: Int, isMore: Boolean,adapter:MsgAdapter?,mRefreshLayout: MRefreshLayout?) {
        if(isMore){
            mCurrentPage++
        }else{
            mCurrentPage=1
        }
        Client.getInstance().getMessages(mold,mCurrentPage,PREPAGE, object : JsonCallback<GetMessageListResponse>() {
            override fun onSuccess(response: Response<GetMessageListResponse?>?) {
                var body=response?.body()
                var list=body?.data?.list
                if(body.isSuccess()){
                    if(isMore){
                        if(!list.isNullOrEmpty() && list.size>=10){
                            mRefreshLayout?.finishLoadMore()
                        }else{
                            mRefreshLayout?.finishLoadMoreWithNoMoreData()
                        }
                        adapter?.putMoreMsg(list)
                    }else{
                        adapter?.putMsg(list)
                        mRefreshLayout?.finishRefresh()
                    }
                }else{
                    mView?.onError(body?.msg?:"拉取消息失败")
                    if(isMore){
                        mRefreshLayout?.finishLoadMore()
                    }else{
                        mRefreshLayout?.finishRefresh()
                    }
                }
            }

            override fun onError(response: Response<GetMessageListResponse?>?) {
                super.onError(response)
                if(isMore){
                    mRefreshLayout?.finishLoadMore()
                }else{
                    mRefreshLayout?.finishRefresh()
                }
            }
        })
    }
}

interface MessageView : BaseView {

}