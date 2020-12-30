package com.linruan.carconnection.moudle.presenter

import android.content.Context
import android.graphics.Color
import android.util.TypedValue
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.linruan.carconnection.R
import com.linruan.carconnection.UserManager
import com.linruan.carconnection.adapter.RecordAdapter
import com.linruan.carconnection.entities.net.GetConsumeRecordResponse
import com.linruan.carconnection.entities.net.GetUserInfo
import com.linruan.carconnection.isSuccess
import com.linruan.carconnection.moudle.views.WalletView
import com.linruan.carconnection.net.BaseResponse
import com.linruan.carconnection.net.Client
import com.linruan.carconnection.net.JsonCallback
import com.linruan.carconnection.utils.Util
import com.linruan.carconnection.view.refreshlayout.MRefreshLayout
import com.lzy.okgo.model.Response
import java.math.BigDecimal

/**
 * author：Administrator on 2020/5/29 16:44
 * email：1328911009@qq.com
 */
class WalletPresenter(view: WalletView?) : BasePresenter<WalletView>(view) {
    private var currentTab: TextView? = null
    private var currentMold = 0
    private var currentPage = 1
    private val PREPAGE = 10
    fun handleTabs(
        tab1: TextView,
        tab2: TextView,
        tab3: TextView,
        adatper: RecordAdapter?,
        mRefreshLayout: MRefreshLayout
    ) {
        tab1.setOnClickListener {
            tab1.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15f)
            tab1.setTextColor(ContextCompat.getColor(mContext!!, R.color.black_2))
            var p = tab1.layoutParams
            p.height = Util.px2dp(45)
            tab1.layoutParams = p
            tab1?.setBackgroundResource(R.mipmap.img_tab_rect)
            currentTab?.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 13f)
            currentTab?.setTextColor(Color.parseColor("#666666"))
            currentTab?.layoutParams =
                (currentTab?.layoutParams as LinearLayout.LayoutParams?)?.apply {
                    height = Util.px2dp(40)
                }
            currentTab?.setBackgroundColor(Color.TRANSPARENT)
            currentTab?.requestLayout()
            currentTab = tab1
            currentMold = 0
            adatper?.clear()
//            mRefreshLayout.autoRefresh()
            getConsumeRecord(adatper,mRefreshLayout,false)
        }
        tab2.setOnClickListener {
            tab2.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15f)
            tab2.setTextColor(ContextCompat.getColor(mContext!!, R.color.black_2))
            var p2 = tab2.layoutParams
            p2.height = Util.px2dp(45)
            tab2.layoutParams = p2
            tab2?.setBackgroundResource(R.mipmap.img_tab_rect)
            currentTab?.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 13f)
            currentTab?.setTextColor(Color.parseColor("#666666"))
            currentTab?.layoutParams =
                (currentTab?.layoutParams as LinearLayout.LayoutParams?)?.apply {
                    height = Util.px2dp(40)
                }
            currentTab?.setBackgroundColor(Color.TRANSPARENT)
            currentTab?.requestLayout()
            currentTab = tab2
            currentMold = 1
            adatper?.clear()
            getConsumeRecord(adatper,mRefreshLayout,false)
        }
        tab3.setOnClickListener {
            tab3.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15f)
            tab3.setTextColor(ContextCompat.getColor(mContext!!, R.color.black_2))
            var p3 = tab3.layoutParams
            p3.height = Util.px2dp(45)
            tab3.layoutParams = p3
            tab3?.setBackgroundResource(R.mipmap.img_tab_rect_right)
            currentTab?.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 13f)
            currentTab?.setTextColor(Color.parseColor("#666666"))
            currentTab?.layoutParams =
                (currentTab?.layoutParams as LinearLayout.LayoutParams?)?.apply {
                    height = Util.px2dp(40)
                }
            currentTab?.setBackgroundColor(Color.TRANSPARENT)
            currentTab?.postInvalidate(0, 0, 0, 0)
            currentTab?.requestLayout()
            currentTab = tab3
            currentMold = 2
            adatper?.clear()
            getConsumeRecord(adatper,mRefreshLayout,false)
        }
        tab1.performClick()
    }

    fun getConsumeRecord(adatper: RecordAdapter?, mRefreshLayout: MRefreshLayout, isMore: Boolean) {
        if (isMore) {
            currentPage++
        } else {
            currentPage = 1
        }
        Client.getInstance().getConsumeRecord(
            currentMold,
            currentPage,
            PREPAGE,
            object : JsonCallback<GetConsumeRecordResponse>() {
                override fun onFinish() {
                    mRefreshLayout.finishRefresh()
                }

                override fun onSuccess(response: Response<GetConsumeRecordResponse?>?) {
                    mRefreshLayout.finishRefresh()
                    if (response?.body().isSuccess()) {
                        if (isMore) {
                            adatper?.addData(response?.body()?.data?.list)
                        } else {
                            adatper?.putData(response?.body()?.data?.list)
                        }
                        if (response?.body()?.data?.list.isNullOrEmpty() || response?.body()?.data?.list?.size ?: 0 < PREPAGE){
                            mRefreshLayout.finishLoadMoreWithNoMoreData()
                        }else{
                            mRefreshLayout.finishLoadMore()
                        }

                    } else {
                        mView?.onError(response?.body()?.msg ?: "拉取记录失败")
                    }
                }
            })
    }

    /**
     * 获取用户余额
     */
    fun getBalance(){
        Client.getInstance()
            .getUserInfo(object : JsonCallback<GetUserInfo>() {
                override fun onSuccess(response: Response<GetUserInfo?>?) {
                    var body = response?.body()
                    if (body?.isSuccess() == true) {
                        var nick = body.data?.name
                        var balance = body.data?.balance //余额
                        var avatar = body.data?.avatar
                        UserManager.getUser()?.balance =
                            BigDecimal(balance ?: "0").setScale(2, BigDecimal.ROUND_DOWN).toString()
                        mView?.onGetBalanceSuccess()
                    } else {
                    }
                }
            })
    }
}
