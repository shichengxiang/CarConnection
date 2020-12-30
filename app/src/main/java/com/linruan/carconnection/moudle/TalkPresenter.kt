package com.linruan.carconnection.moudle

import android.widget.TextView
import com.linruan.carconnection.UserManager
import com.linruan.carconnection.dialog.LoadingDialog
import com.linruan.carconnection.entities.BBS
import com.linruan.carconnection.entities.net.GetBBsResponse
import com.linruan.carconnection.moudle.presenter.BasePresenter
import com.linruan.carconnection.moudle.views.BaseView
import com.linruan.carconnection.net.BaseResponse
import com.linruan.carconnection.net.Client
import com.linruan.carconnection.net.JsonCallback
import com.linruan.carconnection.utils.Util
import com.linruan.carconnection.view.refreshlayout.MRefreshLayout
import com.lzy.okgo.OkGo
import com.lzy.okgo.model.Response

/**
 * author：shichengxiang on 2020/6/8 10:11
 * email：1328911009@qq.com
 */
class TalkPresenter(view: TalkView) : BasePresenter<TalkView>(view) {
    private val URL_RECOMMEND = "/bbs/getlist" //推荐
    private val URL_NEARBY = "/bbs/nearby" //附近
    private val URL_ATTENTIN = "/bbs/follow" //关注
    private val start=1
    private var currentPage_recommend = start
    private var currentPage_nearby = start
    private var currentPage_attention = start
    private val PERPAGE = 10
    private var currentTag = 0

    /**
     * 拉取贴吧列表（推荐 附近 关注）
     */
    fun getBbs(refreshLayout: MRefreshLayout, tag: Int, isMore: Boolean) {
        var currentPage = 0
        var url = when (tag) {
            0 -> {
                if (isMore) {
                    currentPage_recommend++
                } else {
                    currentPage_recommend = start
                }
                currentPage = currentPage_recommend
                "${Client.BASEURL_API}$URL_RECOMMEND"
            }
            1 -> {
                if (isMore) {
                    currentPage_nearby++
                } else {
                    currentPage_nearby = start
                }
                currentPage = currentPage_nearby
                "${Client.BASEURL_API}$URL_NEARBY"
            }
            else -> {
                if (isMore) {
                    currentPage_attention++
                } else {
                    currentPage_attention = start
                }
                currentPage = currentPage_attention
                "${Client.BASEURL_API}$URL_ATTENTIN"
            }
        }

        OkGo.post<GetBBsResponse>(url)
                .params("page", currentPage)
                .params("perpage ", PERPAGE)
                .params("lat", UserManager.currentLocation[0])
                .params("lng", UserManager.currentLocation[1])
                .tag("bbs")
                .execute(object : JsonCallback<GetBBsResponse>() {
                    override fun onSuccess(response: Response<GetBBsResponse?>?) {
                        super.onSuccess(response)
                        var res = response?.body()
                        if (res?.isSuccess() == true) {
                            var list = res.data?.list
                            mView?.onGetBBSSuccess(list, isMore, tag)
                            if (isMore) {
                                if (list.isNullOrEmpty()) {
                                    refreshLayout.finishLoadMoreWithNoMoreData()
                                } else {
                                    refreshLayout.finishLoadMore() //区分是否有数据
                                }
                            } else {
                                refreshLayout.finishRefresh()
                            }
                        } else {
                            mView?.onError("数据异常")
                            refreshLayout.finishRefresh()
                            refreshLayout.finishLoadMore()
                        }
                    }

                    override fun onError(response: Response<GetBBsResponse?>?) {
                        super.onError(response)
                        if (isMore) {
                            refreshLayout.finishLoadMore()
                        } else {
                            refreshLayout.finishRefresh()
                        }
                    }
                })

    }

    /**
     * 关注
     */
    fun setAttentino(tv: TextView, followId: String) {
        tv.isEnabled = false
        LoadingDialog.loading(tv.context)
        OkGo.post<BaseResponse>("${Client.BASEURL_API}/public/follow")
                .params("follow_id", followId)
                .tag("attention")
                .execute(object : JsonCallback<BaseResponse>() {
                    override fun onFinish() {
                        super.onFinish()
                        tv.isEnabled = true
                    }

                    override fun onSuccess(response: Response<BaseResponse?>?) {
                        super.onSuccess(response)
                        var res = response?.body()
                        if (res?.isSuccess() == true) {
                            var isFollow=0
                            if (res.msg == "关注成功") {
                                tv.text = "已关注"
                                Util.toast("关注成功")
                                isFollow=1
                            } else {
                                tv.text = "关注"
                                Util.toast("已取消关注")
                                isFollow=0
                            }
                            mView?.onAttentionSuccess(followId,isFollow)
                        }
                    }

                })
    }
}

interface TalkView : BaseView {
    fun onGetBBSSuccess(list: ArrayList<BBS>?, isMore: Boolean, tag: Int)
    fun onAttentionSuccess(followId:String,isFollow:Int)
}