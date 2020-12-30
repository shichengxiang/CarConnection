package com.linruan.carconnection.ui.store

import android.content.res.ColorStateList
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager.widget.ViewPager
import cn.jiguang.share.android.api.PlatActionListener
import cn.jiguang.share.android.api.Platform
import cn.jiguang.share.android.api.ShareParams
import com.blankj.utilcode.util.LogUtils
import com.bumptech.glide.Glide
import com.google.android.material.tabs.TabLayout
import com.linruan.carconnection.*
import com.linruan.carconnection.adapter.FlowTagAdapter
import com.linruan.carconnection.adapter.EvaluateAdapter
import com.linruan.carconnection.adapter.ImagePageAdapter
import com.linruan.carconnection.dialog.CommonDialog
import com.linruan.carconnection.entities.Goods
import com.linruan.carconnection.entities.SkuBean
import com.linruan.carconnection.entities.net.GetGoodsDetailResponse
import com.linruan.carconnection.entities.net.GetShareDataResponse
import com.linruan.carconnection.moudle.GoodsDetailPresenter
import com.linruan.carconnection.moudle.GoodsDetailView
import com.linruan.carconnection.utils.Util
import com.linruan.carconnection.view.ScrollView
import kotlinx.android.synthetic.main.activity_goodsdetail.*
import kotlinx.android.synthetic.main.layout_dialog_goodssize.view.*
import java.math.BigDecimal
import java.util.HashMap

class GoodsDetailActivity : BaseActivity(), GoodsDetailView {

    private var isOriginColor = true
    private var minDistance = Util.px2dp(100)
    private var maxDistance = Util.px2dp(200)
    private var mEvaluateAdapter: EvaluateAdapter? = null
    private var mPresenter: GoodsDetailPresenter? = null

    private var mGoodsId = 0
    private var mSkuId = "" //规格
    private var mGoodsNum = 0 //数量
    private var mCurrentPrice = BigDecimal.ZERO
    private var mSkipUrl = ""
    private var mSkuList = arrayListOf<GetGoodsDetailResponse.SkuBean>()
    private var mCurrentGoods: Goods? = null
    private var mShareParams: ShareParams? = null

    override fun getLayout() = R.layout.activity_goodsdetail

    override fun initView() {
        mGoodsId = intent.getIntExtra("goodsId", 0)
        mPresenter = GoodsDetailPresenter(this)
        toolbar.background.mutate().alpha = 0
        ivBack.setOnClickListener { onBackPressed() }
        llTopTabs.apply {
            addTab(newTab().setText("商品")
                .apply {
                    tag = 0
                })
            addTab(newTab().setText("详情")
                .apply {
                    tag = 1
                })
            addTab(newTab().setText("评价")
                .apply {
                    tag = 2
                })
            addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
                override fun onTabReselected(tab: TabLayout.Tab?) {
                }

                override fun onTabUnselected(tab: TabLayout.Tab?) {
                }

                override fun onTabSelected(tab: TabLayout.Tab?) {
                    if (scrollView.isDraging) return
                    if (tab?.tag == 0) {
                        scrollView.smoothScrollTo(0, 0)
                    } else if (tab?.tag == 1) {
                        scrollView.smoothScrollTo(
                            0,
                            vDivider.y.toInt() + vDivider.measuredHeight - toolbar.measuredHeight
                        )
                    } else {
                        scrollView.smoothScrollTo(
                            0,
                            rlEvaluateArea.y.toInt() + vDivider.measuredHeight - toolbar.measuredHeight
                        )
                    }
                }
            })
        }
        ivShopCar.setOnClickListener {
            if (!UserManager.isLoginAndSkip(this)) {
                return@setOnClickListener
            }
            Router.openUI(this, ShopCarActivity::class.java)
        }
        tvCallService.setOnClickListener { Util.callPhone(this, Constant.CUSTOMERSERVICE_MOBILE) }
        tvGotoShopCar.setOnClickListener {
            if (!UserManager.isLoginAndSkip(this)) {
                return@setOnClickListener
            }
            Router.openUI(this, ShopCarActivity::class.java)
        }
        tvAddShopCar.setOnClickListener {
            if (!UserManager.isLoginAndSkip(this)) {
                return@setOnClickListener
            }
            showGoodsSize()
//            mPresenter?.addGoodsToCar(tvAddShopCar, mGoodsId.toString(), mSkuId, mGoodsNum)
        }
        tvShowSize.setOnClickListener {
            showGoodsSize() }
        tvBuyNow.setOnClickListener {
            if (!UserManager.isLoginAndSkip(this)) {
                return@setOnClickListener
            }
            showGoodsSize()
        }
        //        scrollView.setOnDragListener { v, event ->
        //            when (event.action) {
        //                MotionEvent.ACTION_DOWN -> isDraging = true
        //                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> isDraging = false
        //            }
        //            return@setOnDragListener true
        //        }
        scrollView.setOnScrollChanged(object : ScrollView.OnScrollChanged {
            override fun onScroll(l: Int, t: Int, oldl: Int, oldt: Int) {
                if (t <= minDistance) {
                    //                    ViewCompat.setBackgroundTintList(
                    //                        ivBack,
                    //                        ColorStateList.valueOf(Color.WHITE)
                    //                    )
                    //                    ViewCompat.setBackgroundTintList(
                    //                        ivShopCar,
                    //                        ColorStateList.valueOf(Color.WHITE)
                    //                    )
                    ivBack.setImageResource(R.drawable.ic_back_white)
                    ivShopCar.setImageResource(R.drawable.ic_shopcar_white)
                    isOriginColor = true
                    ivBack.background = ContextCompat.getDrawable(
                        this@GoodsDetailActivity,
                        R.drawable.bg_circle_halftransf
                    )
                    ivShopCar.background = ContextCompat.getDrawable(
                        this@GoodsDetailActivity,
                        R.drawable.bg_circle_halftransf
                    )
                    toolbar.background.mutate().alpha = 0
                    //                    ivBack.alpha = 1f
                    //                    ivShopCar.alpha = 1f
                    var scale = (minDistance - t).toFloat() / minDistance
                    ivBack.alpha = scale
                    ivShopCar.alpha = scale
                    llTopTabs.alpha = 0f
                    llTopTabs.visibility = View.GONE
                } else if (t in (minDistance + 1) until maxDistance) {
                    var scale = (t - minDistance).toFloat() / minDistance
                    ivBack.alpha = scale
                    ivShopCar.alpha = scale
                    toolbar.background.mutate().alpha = (scale * 255).toInt() //最大255
                    llTopTabs.alpha = scale
                    llTopTabs.visibility = View.VISIBLE
                    ViewCompat.setBackgroundTintList(
                        ivBack,
                        ColorStateList.valueOf(Color.parseColor("#FF333333"))
                    )
                    ViewCompat.setBackgroundTintList(
                        ivShopCar,
                        ColorStateList.valueOf(Color.parseColor("#FF333333"))
                    )
                    ivBack.setImageResource(R.drawable.ic_back_black)
                    ivShopCar.setImageResource(R.drawable.ic_shopcar_black)
                    ivBack.setBackgroundColor(Color.TRANSPARENT)
                    ivShopCar.setBackgroundColor(Color.TRANSPARENT)
                    //                    isOriginColor = false
                    //                    llTopTabs.visibility = View.VISIBLE
                    //                    ivBack.alpha = t.toFloat() / maxDistance
                    //                    ivShopCar.alpha = t.toFloat() / maxDistance
                    //                    toolbar.background.mutate().alpha = (((t-maxDistance) / maxDistance) * 255).toInt() //最大255
                } else {
                    ivBack.alpha = 1f
                    ivShopCar.alpha = 1f
                    toolbar.background.mutate().alpha = 255 //最大255
                }
                //切换tab
                var spaceHeight = Util.getScreenHeight() / 2
                if (scrollView.isDraging)
                    if (t < vDivider.y - spaceHeight) { //商品
                        if (llTopTabs.selectedTabPosition != 0)
                            llTopTabs.selectTab(llTopTabs.getTabAt(0))
                    } else if (t > vDivider.y - spaceHeight && t < rlEvaluateArea.y - spaceHeight) {
                        if (llTopTabs.selectedTabPosition != 1)
                            llTopTabs.selectTab(llTopTabs.getTabAt(1))
                    } else {
                        if (llTopTabs.selectedTabPosition != 2)
                            llTopTabs.selectTab(llTopTabs.getTabAt(2))
                    }
                //                if (t > vDivider.y) {
                //                    if (llTopTabs.selectedTabPosition == 0)
                //                        llTopTabs.selectTab(llTopTabs.getTabAt(1))
                //                } else if (t < vDivider.y - toolbar.measuredHeight) {
                //                    if (llTopTabs.selectedTabPosition == 1)
                //                        llTopTabs.selectTab(llTopTabs.getTabAt(0))
                //                }
            }
        })
        rvEvaluatesList.layoutManager = LinearLayoutManager(this)
        mEvaluateAdapter = EvaluateAdapter(this)
        rvEvaluatesList.adapter = mEvaluateAdapter
        //        mRefreshLayout.finishLoadMoreWithNoMoreData()
//        flowLayout.addView(layoutInflater.inflate(R.layout.item_tag_motorbike, null))
//        flowLayout.addView(layoutInflater.inflate(R.layout.item_tag_motorbike, null))
//        flowLayout.addView(layoutInflater.inflate(R.layout.item_tag_motorbike, null))
//        flowLayout.addView(layoutInflater.inflate(R.layout.item_tag_motorbike, null))
//        flowLayout.addView(layoutInflater.inflate(R.layout.item_tag_motorbike, null))
        mPresenter?.getGoodsDetail(mGoodsId.toString())
        mPresenter?.getShareData(mGoodsId.toString())
        tvGotoShare.setOnClickListener {
            if (mShareParams != null)
                Util.sharePlatform(this, mShareParams!!, object : PlatActionListener {
                    override fun onComplete(p0: Platform?, p1: Int, p2: HashMap<String, Any>?) {
                        runOnUiThread {
                            toast("分享成功")
                        }
                    }

                    override fun onCancel(p0: Platform?, p1: Int) {
                        runOnUiThread {
                            toast("分享取消")
                        }
                    }

                    override fun onError(p0: Platform?, p1: Int, p2: Int, p3: Throwable?) {
                        LogUtils.e("$p2  ${p3?.message}")
                        runOnUiThread {
                            toast("分享错误")
                        }
                    }
                })
        }
    }

    private var sizeDialog: CommonDialog? = null
    private fun showGoodsSize() {
        if (sizeDialog == null) {
            sizeDialog = object : CommonDialog(this, R.layout.layout_dialog_goodssize) {
                override fun initView(root: View) {
                    window?.setWindowAnimations(R.style.BottomDialogAnim)
                    window?.setGravity(Gravity.BOTTOM)
                    root.findViewById<View>(R.id.ivClose)
                        .setOnClickListener { dismiss() }
                    var prices = arrayListOf<BigDecimal>()
                    var titles = arrayListOf<String>()
                    var skuids = arrayListOf<String>()
                    var tvSub = root.findViewById<View>(R.id.tvSub)
                    var tvAdd = root.findViewById<View>(R.id.tvAdd)
                    var tvNumber = root.findViewById<TextView>(R.id.tvNumber)
                    var tvAmount = root.findViewById<TextView>(R.id.tvAmount)
                    var tvChooseTip = root.findViewById<TextView>(R.id.tvChooseTip)
                    var currentSku = ""
                    if (!mSkuList.isNullOrEmpty()) {
                        mSkuList.forEach {
                            titles.add(it.title ?: "")
                            prices.add(BigDecimal(it.price ?: "0"))
                            skuids.add(it.id ?: "")
                        }
                        var adapter = FlowTagAdapter(this@GoodsDetailActivity, titles)
                        root.tagFlowLayout.adapter = adapter
                        root.tagFlowLayout.setOnTagClickListener { view, position, parent ->
                            currentSku = titles[position]
                            tvChooseTip.text = "$currentSku ,$mGoodsNum 个"
                            mCurrentPrice = prices[position]
                            mSkuId = skuids[position]
                            tvAmount.text =
                                Util.moneyformat(mCurrentPrice.multiply(BigDecimal(mGoodsNum)))
                            return@setOnTagClickListener true
                        }
                        mCurrentPrice = prices[0]
                        mSkuId = skuids[0]
                        currentSku = titles[0]
                        adapter.setSelectedList(0)
                        root.tagFlowLayout.setMaxSelectCount(1)
                    }
                    mGoodsNum = 1
                    tvChooseTip.text = "$currentSku  $mGoodsNum 个"
                    tvAmount.text = Util.moneyformat(mCurrentPrice.multiply(BigDecimal(mGoodsNum)))
                    tvSub.setOnClickListener {
                        if (mGoodsNum > 1) {
                            mGoodsNum--
                            tvNumber.text = "$mGoodsNum"
                            tvAmount.text =
                                Util.moneyformat(mCurrentPrice.multiply(BigDecimal(mGoodsNum)))
                            tvChooseTip.text = "$currentSku ,$mGoodsNum 个"
                        }
                    }
                    tvAdd.setOnClickListener {
                        mGoodsNum++
                        tvNumber.text = "$mGoodsNum"
                        tvAmount.text =
                            Util.moneyformat(mCurrentPrice.multiply(BigDecimal(mGoodsNum)))
                        tvChooseTip.text = "$currentSku ,$mGoodsNum 个"
                    }
                    root.findViewById<View>(R.id.btnPlaceOrderNow)
                        .setOnClickListener {
                            dismiss()
                            gotoConfirmPage(currentSku, mSkuId, mCurrentPrice, mGoodsNum)
                        }
                    var btnAdd = root.findViewById<View>(R.id.btnAddCart)
                    btnAdd.setOnClickListener {
                        dismiss()
                        mPresenter?.addGoodsToCar(
                            btnAdd,
                            mGoodsId.toString(),
                            mSkuId,
                            mGoodsNum
                        )
                    }
                }

            }
        }
        if (sizeDialog?.isShowing == false)
            sizeDialog?.show()
    }

    override fun onAddCartSuccess() {
        toast("加入购物车成功")
        tvAddShopCar.setText("已加入购物车")
        tvAddShopCar.isEnabled = false
        //刷新购物车数量
    }

    override fun onGetShareData(data: GetShareDataResponse.DataBean?) {
        if (data != null)
            mShareParams = ShareParams().apply {
                shareType = Platform.SHARE_WEBPAGE
                title = data.title
                text = data.content
                if (data.image.isNullOrBlank()) {
                    imageData = BitmapFactory.decodeResource(resources, R.mipmap.ic_launcher)
                } else {
                    imageUrl = data.image
                }
                url = data.url
            }
    }

    override fun onGetGoodsDetailSuccess(data: GetGoodsDetailResponse.DataBean?) {
        mCurrentGoods = Goods().apply {
            id = data?.id ?: ""
            title = data?.title ?: ""
            price = BigDecimal(data?.price ?: "0")
            cover = data?.cover
        }

        mSkuList = data?.sku ?: arrayListOf()
        if (!mSkuList.isNullOrEmpty()) {
            mSkuId = mSkuList[0].id ?: ""
        }
        mCurrentPrice = BigDecimal(data?.price ?: "0")
        mSkipUrl = data?.content ?: ""
        var albums = arrayListOf<String>()
        var scale = 0f
        if (data?.album?.isNotEmpty() == true) {
            val album = data?.album?.get(0)
            scale = (album?.height ?: 0f) / (album?.width ?: 0f)
        }
        data?.album?.forEach {
            albums.add(it.filepath)
        }
        vpPictures.adapter = ImagePageAdapter(this, albums)
        tvPageIndicator.setText("1/${albums.size}")
        if (scale > 0) {
            val lp_vp = rlTopLayout.layoutParams
            lp_vp.height = (Util.getScreenWidth() * scale).toInt()
            rlTopLayout.layoutParams = lp_vp
        }
        vpPictures.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {
            }

            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
            }

            override fun onPageSelected(position: Int) {
                tvPageIndicator.setText("${position + 1}/${albums.size}")
            }
        })
//        Glide.with(this)
//            .load(data?.cover ?: "")

        tvGoodsName.text = data?.title ?: ""
        tvSalesVolume.text = "月销 " + Util.numToW(data?.sales ?: 0, "w") + "笔"
        tvPrice.text = Util.moneyformat(BigDecimal(data?.price ?: ""))
        //详情webview
        webViewDetail.loadUrl(data?.content ?: "")
        //评论
        mEvaluateAdapter?.setData(data?.comments)
        //购物车数量
        if (data?.cart_count ?: 0 > 0) {
            tvCartNum.visibility = View.VISIBLE
            tvCartNum.text = data?.cart_count?.toString() ?: "0"
        } else {
            tvCartNum.visibility = View.GONE
        }
    }

    fun gotoConfirmPage(
        currentSku: String,
        currentSkuId: String,
        currentPrice: BigDecimal,
        currentNum: Int
    ) {
        var list = arrayListOf<Goods>()
        if (mCurrentGoods != null) {
            mCurrentGoods?.num = currentNum
            mCurrentGoods?.selectedSkuBean = SkuBean().apply {
                skuId = currentSkuId
                sku = currentSku
                price = currentPrice
            }
            mCurrentGoods?.sku_id = currentSkuId
            list.add(mCurrentGoods!!)
        }
        var bundle = Bundle().apply {
            putSerializable(ConfirmGoodsOrderActivity.CARTLIST, list)
            putInt(ConfirmGoodsOrderActivity.ORDERTYPE, 1)
        }
        Router.openUI(this, ConfirmGoodsOrderActivity::class.java, bundle)
    }

    override fun onError(err: String?) {
        toast(err ?: "")
    }

    override fun onDestroy() {
        super.onDestroy()
        mPresenter?.onDestory()
    }
}
