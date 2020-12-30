package com.linruan.carconnection.moudle

import com.linruan.carconnection.dialog.LoadingDialog
import com.linruan.carconnection.entities.BBS
import com.linruan.carconnection.entities.net.GetBBSDetailResponse
import com.linruan.carconnection.entities.net.GetShareDataResponse
import com.linruan.carconnection.isSuccess
import com.linruan.carconnection.moudle.presenter.BasePresenter
import com.linruan.carconnection.moudle.views.BaseView
import com.linruan.carconnection.net.BaseResponse
import com.linruan.carconnection.net.Client
import com.linruan.carconnection.net.JsonCallback
import com.lzy.okgo.model.Response
import com.scwang.smartrefresh.layout.SmartRefreshLayout

/**
 * author：shichengxiang on 2020/6/9 15:07
 * email：1328911009@qq.com
 */
class TalkDetailPresenter(view: TalkDetailView?) : BasePresenter<TalkDetailView>(view) {
    private val STARTPAGE=1
    private val PERPAGE=10
    private var currentPgae=STARTPAGE
    /**
     * 拉取贴吧详情
     */
    fun getBBSDetail(id: String,isMore:Boolean=false,smartRefreshLayout: SmartRefreshLayout?=null) {
        if(isMore){
            currentPgae++
        }else{
            LoadingDialog.loading(mContext)
            currentPgae=STARTPAGE
        }
        Client.getInstance()
                .getBBSDetail(id,currentPgae,PERPAGE, object : JsonCallback<GetBBSDetailResponse>() {
                    override fun onSuccess(response: Response<GetBBSDetailResponse?>?) {
                        val body = response?.body()
                        if (body?.isSuccess() == true) {
                            if(isMore){
                                if(body.data?.commentslist?.size?:0>0){
                                    smartRefreshLayout?.finishLoadMore()
                                }else{
                                    smartRefreshLayout?.finishLoadMoreWithNoMoreData()
                                }
                            }
                            if (body.data == null) {
                                mView?.onError("获取数据异常")
                                return
                            }
                            mView?.onGetDetailSuccess(body.data!!,isMore)
                        } else {
                            mView?.onError(body?.msg)
                        }
                    }

                    override fun onError(response: Response<GetBBSDetailResponse?>?) {
                        super.onError(response)
                    }
                })
    }

    /**
     * 发布评论
     */
    fun sendComments(content_id:String,content:String,reply_id:String,topId:String){
        Client.getInstance().sendTalkComments(content_id,content,reply_id,topId,object : JsonCallback<BaseResponse>(){
            override fun onSuccess(response: Response<BaseResponse?>?) {
                super.onSuccess(response)
            }

            override fun onError(response: Response<BaseResponse?>?) {
                super.onError(response)
            }
        })
    }

    /**
     * 拉取分享数据
     */
    fun getShareData(id:String){
        Client.getInstance().getShareData(id,2,object :JsonCallback<GetShareDataResponse>(){
            override fun onSuccess(response: Response<GetShareDataResponse?>?) {
                if(response?.body().isSuccess()){
                    var data=response?.body()?.data
                    mView?.onGetShareDataSuccess(data)
                }else{
                    mView?.onError(response?.body()?.msg?:"拉取数据异常")
                }
            }

            override fun onError(response: Response<GetShareDataResponse?>?) {
                mView?.onError("拉取数据失败")
            }
        })
    }

}

interface TalkDetailView : BaseView {
    fun onGetDetailSuccess(detail: BBS,isMore: Boolean)
    fun onGetShareDataSuccess(data:GetShareDataResponse.DataBean?)
}
