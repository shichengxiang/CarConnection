package com.linruan.carconnection.ui.store

import android.os.Handler
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.linruan.carconnection.BaseActivity
import com.linruan.carconnection.R
import com.linruan.carconnection.adapter.InnerGoodsAdapter
import com.linruan.carconnection.adapter.LoadUpImageAdapter
import com.linruan.carconnection.adapter.LoadUpImageAdapter3
import com.linruan.carconnection.adapter.OnItemClickListener
import com.linruan.carconnection.decorview.SpaceItemDecoraton
import com.linruan.carconnection.dialog.LoadingDialog
import com.linruan.carconnection.entities.GoodsOrder
import com.linruan.carconnection.entities.ImageLoadUp
import com.linruan.carconnection.entities.MessageEvent
import com.linruan.carconnection.moudle.ApplyRefundPresenter
import com.linruan.carconnection.moudle.ApplyRefundView
import com.linruan.carconnection.toast
import com.linruan.carconnection.utils.ImageUtil
import com.linruan.carconnection.utils.Util
import com.yanzhenjie.album.Album
import kotlinx.android.synthetic.main.activity_refundapply.*
import org.greenrobot.eventbus.EventBus
import java.lang.StringBuilder
import java.math.BigDecimal

/**
 * author：shichengxiang on 2020/6/5 10:03
 * email：1328911009@qq.com
 */
class RefundApplyActivity : BaseActivity(), ApplyRefundView {

    private var maxCount = 9 //可上传最大张数
    private var mEvidenceAdapter: LoadUpImageAdapter3? = null
    private var mPresenter: ApplyRefundPresenter? = null
    private var mOrderId = ""

    companion object {
        @JvmField
        val ORDERID_KEY = "orderId"
    }

    override fun getLayout() = R.layout.activity_refundapply

    override fun initView() {
        toolbar.setTitle("退款申请")
        mPresenter = ApplyRefundPresenter(this)
        mOrderId = intent.getStringExtra(ORDERID_KEY) ?: ""
        rvLoadupEvidence.layoutManager = GridLayoutManager(this, 3)
        mEvidenceAdapter = LoadUpImageAdapter3(this, maxCount, 3)
        rvGoods.layoutManager = LinearLayoutManager(this)
        rvLoadupEvidence.addItemDecoration(
            SpaceItemDecoraton(
                0,
                Util.px2dp(8),
                0,
                0,
                RecyclerView.VERTICAL,
                true
            )
        )
        rvLoadupEvidence.adapter = mEvidenceAdapter
        mEvidenceAdapter?.setOnItemClickListener(object : OnItemClickListener {
            override fun onItemClickListener(position: Int) {
                openAlbum()
            }
        })
        tvCommitRefund.setOnClickListener {
            var explain = etReason.text.toString()
            if (explain.isNullOrBlank()) {
                toast("请填写退款原因")
                return@setOnClickListener
            }
            LoadingDialog.loading(this)
            if (mEvidenceAdapter?.getData().isNullOrEmpty()) {
                mPresenter?.applyRefund(mOrderId, explain, "", "")
            } else {
                var imgSb = StringBuilder()
                ImageUtil.build(this)
                    .withImagelist(mEvidenceAdapter?.getData())
                    .onListener(object : ImageUtil.onUpLoadListener {
                        override fun onLoadupSuccess(id: String, index: Int) {
                            imgSb.append(id).append(",")
                        }

                        override fun onFinishAll() {
                            mPresenter?.applyRefund(
                                mOrderId,
                                explain,
                                if (imgSb.isNotEmpty()) imgSb.substring(
                                    0,
                                    imgSb.length - 1
                                ) else "",
                                ""
                            )
                        }

                        override fun onUpLoadError(index: Int) {
//                        var holder=rvGoods.findViewHolderForAdapterPosition(index) as LoadUpImageAdapter3.Holder
                        }

                        override fun onProgress(progres: Int, index: Int) {
                        }

                    }).start()
            }
        }
        mPresenter?.getOrderDetail(mOrderId)
    }

    private fun openAlbum() {
        var count = maxCount - (mEvidenceAdapter?.getData()?.size ?: 0)
        Album.image(this)
            .multipleChoice()
            .camera(true)
            .columnCount(3)
            .selectCount(count)
            .onResult {
                it.forEach {
                    mEvidenceAdapter?.addData(ImageLoadUp().apply {
                        filepath = it.path
                    })
                }
            }
            .start()
    }

    override fun onApllyRefundSuccess() {
        toast("提交退款申请成功，请耐心等待")
        EventBus.getDefault().post(MessageEvent(MessageEvent.REFRESH_STOREORDERACTIVITY, "4"))
        Handler().postDelayed({
            if (!isDestroyed)
                onBackPressed()
        }, 1500)
    }

    override fun onGetGoodsOrderSuccess(detail: GoodsOrder?) {
        if (detail == null) return
        tvRefundAmount.text = Util.moneyformat(BigDecimal(detail?.price ?: "0"))
        var goodss = detail.ordersGoods
        rvGoods.adapter = InnerGoodsAdapter(
            this,
            GoodsOrder.STATE_ALL,
            goodss
        )
    }

    override fun onError(err: String?) {
        toast(err ?: "")
    }
}
