package com.linruan.carconnection.ui.home

import android.content.Intent
import android.graphics.drawable.Drawable
import android.graphics.drawable.DrawableContainer
import android.graphics.drawable.StateListDrawable
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.linruan.carconnection.*
import com.linruan.carconnection.adapter.FlowTagAdapter
import com.linruan.carconnection.adapter.LoadUpImageAdapter
import com.linruan.carconnection.adapter.OnItemClickListener
import com.linruan.carconnection.decorview.SpaceItemDecoraton
import com.linruan.carconnection.entities.ImageLoadUp
import com.linruan.carconnection.entities.Master
import com.linruan.carconnection.moudle.ConfirmOrderPresenter
import com.linruan.carconnection.ui.wallet.PayPlatformActivity
import com.linruan.carconnection.utils.Util
import com.yanzhenjie.album.Album
import kotlinx.android.synthetic.main.activity_confirmorder.*
import kotlinx.android.synthetic.main.activity_confirmorder.view.*
import kotlinx.android.synthetic.main.layout_item_master_show.*
import kotlinx.android.synthetic.main.layout_toolbar.view.*


class ConfirmRepairOrderActivity : BaseActivity(),
    com.linruan.carconnection.moudle.ConfirmOrderView {

    private var mCurrentPayType = Constant.PAY_TYPE_WEIXIN //当前支付方式
    private var mLoadUpImageAdapter: LoadUpImageAdapter? = null
    private var REQUESTCODE_CAMERA = 1003
    private var mPresenter: ConfirmOrderPresenter? = null
    private var mSelectedMaster: Master? = null //是否选中师傅 默认0
    private var mLeixingId = ""

    //    private var selectedTags = StringBuffer()
    private val MAXPICCOUNT = 9

    override fun getLayout() = R.layout.activity_confirmorder

    override fun initView() {
        mSelectedMaster = intent.extras?.get("master") as Master?
        tvPlatformPrice.text = Util.decimalFormat2(Constant.PLATFORM_USER_PRICE)
        if (mSelectedMaster != null) {
            layoutMaster.visibility = View.VISIBLE
            Glide.with(this).load(mSelectedMaster?.avatar ?: "")
                .placeholder(R.mipmap.img_default_head).error(R.mipmap.img_default_head)
                .dontAnimate()
                .into(iv_head)
            tvMasterName.text = mSelectedMaster?.name ?: ""
            tvOrderNum.text = "已接： ${mSelectedMaster?.ordernum ?: ""}单"
            var leixing = StringBuffer().append("本店专营：")
            mSelectedMaster?.leixing?.forEach {
                leixing.append(it).append("/")
            }
            tvMonopoly.text = leixing.substring(0, leixing.length - 1)
            tvCommentNum.text = "评价(${mSelectedMaster?.comments})"
            tvWorkTime.text = mSelectedMaster?.worktime ?: "9:00-18:00"
        } else {
            layoutMaster.visibility = View.GONE
        }
        mPresenter = ConfirmOrderPresenter(this)
        toolbar.setTitle("确认订单")
        toolbar.ivRight.apply {
            visibility = View.VISIBLE
            setImageResource(R.drawable.ic_service_head)
            setOnClickListener {
                Util.callPhone(
                    this@ConfirmRepairOrderActivity,
                    Constant.CUSTOMERSERVICE_MOBILE
                )
            }
        }
        btnConfirmPay.setOnClickListener {
            if (flowLayout.selectedList.size < 1) {
                toast("请选择故障原因")
                return@setOnClickListener
            }
//            if (mLoadUpImageAdapter?.getData()?.size ?: 0 < 1) {
//                toast("请上传损坏部位照片")
//                return@setOnClickListener
//            }
            var canPay = true
            val data = mLoadUpImageAdapter?.getData()
            data?.forEach {
                if (!it.isLoaded)
                    canPay = false
            }
            if (canPay) {
                placeOrder()
            } else {
                mPresenter?.loadUpImages(rvImgLoadUp, data) //成功后提交
            }
        }
        var layoutParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        //标签
        var tags = arrayListOf<String>()
        UserManager.mFaults.forEach {
            tags.add(it.title ?: "")
        }
        flowLayout.adapter = FlowTagAdapter(this.applicationContext, tags)
        //        flowLayout.setOnSelectListener {
        //            selectedTags.setLength(0)
        //            it.forEach { p ->
        //                selectedTags.append(tags[p])
        //                selectedTags.append(",")
        //            }
        //        }
        //图片上传
        rvImgLoadUp.layoutManager = GridLayoutManager(this, 5)
        mLoadUpImageAdapter = LoadUpImageAdapter(this, MAXPICCOUNT)
        rvImgLoadUp.adapter = mLoadUpImageAdapter
        rvImgLoadUp.addItemDecoration(
            SpaceItemDecoraton(
                0,
                Util.px2dp(8),
                Util.px2dp(4),
                0,
                RecyclerView.HORIZONTAL,
                false
            )
        )
        mLoadUpImageAdapter?.setOnItemClickListener(object : OnItemClickListener {
            override fun onItemClickListener(position: Int) {
                //                val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                //                startActivityForResult(intent, REQUESTCODE_CAMERA)
                if (mLoadUpImageAdapter?.getData()?.size ?: 0 >= MAXPICCOUNT) return
                openPhotAlbum()
            }
        })
        mLoadUpImageAdapter?.setOnReLoadUpListener(object : LoadUpImageAdapter.OnReloadUpListener {
            override fun onReloadup(position: Int) {
                mPresenter?.loadUpImages(
                    rvImgLoadUp,
                    mLoadUpImageAdapter?.getDataAt(position)?.filepath ?: "",
                    position
                )
            }
        })
        etInputRemark.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                tvTxtNumber.text = "${s?.length ?: 0}/200"
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })
        for (i in 0 until (UserManager.mCarTypes!!.size)) {
            if (i == 0) {
                var bean = UserManager.mCarTypes?.get(0)
                intoImageView(rbType0, bean?.cover_checked)
                tvType0.text = bean?.title ?: ""
                mLeixingId = bean?.id ?: ""
            } else if (i == 2) {
                var bean = UserManager.mCarTypes?.get(1)
                intoImageView(rbType1, bean?.cover)
                tvType1.text = bean?.title ?: ""
            } else {
                var bean = UserManager.mCarTypes?.get(2)
                intoImageView(rbType2, bean?.cover)
                tvType2.text = bean?.title ?: ""
            }
        }
        rbType0.setOnClickListener {
            if (UserManager.mCarTypes?.size ?: 0 > 0) {
                var s = UserManager.mCarTypes!![0]
                intoImageView(rbType0, s.cover_checked)
                mLeixingId = s.id
            }
            if (UserManager.mCarTypes?.size ?: 0 > 1) {
                var s = UserManager.mCarTypes!![1]
                intoImageView(rbType1, s.cover)
            }
            if (UserManager.mCarTypes?.size ?: 0 > 2) {
                var s = UserManager.mCarTypes!![2]
                intoImageView(rbType2, s.cover)
            }
        }
        rbType1.setOnClickListener {
            if (UserManager.mCarTypes?.size ?: 0 > 0) {
                var s = UserManager.mCarTypes!![0]
                intoImageView(rbType0, s.cover)
            }
            if (UserManager.mCarTypes?.size ?: 0 > 1) {
                var s = UserManager.mCarTypes!![1]
                intoImageView(rbType1, s.cover_checked)
                mLeixingId = s.id
            }
            if (UserManager.mCarTypes?.size ?: 0 > 2) {
                var s = UserManager.mCarTypes!![2]
                intoImageView(rbType2, s.cover)
            }
        }
        rbType2.setOnClickListener {
            if (UserManager.mCarTypes?.size ?: 0 > 0) {
                var s = UserManager.mCarTypes!![2]
                intoImageView(rbType2, s.cover)
            }
            if (UserManager.mCarTypes?.size ?: 0 > 1) {
                var s = UserManager.mCarTypes!![1]
                intoImageView(rbType1, s.cover)
            }
            if (UserManager.mCarTypes?.size ?: 0 > 2) {
                var s = UserManager.mCarTypes!![2]
                intoImageView(rbType2, s.cover_checked)
                mLeixingId = s.id
            }
        }
        mPresenter?.getStatement()
    }

    private fun intoImageView(iv: ImageView, url: String?) {
        Glide.with(this).load(url ?: "").error(R.mipmap.img_default_image).into(iv)
    }

    override fun onPlaceOrderSuccess(orderNo: String, needPay: Boolean) {
        toast("下单成功")
        if (needPay) {
            if (!isDestroyed) {
                setResult(1, Intent().apply {
                    putExtra("isSuccess", true)
                })
                finish()
            }
            return
        }
        Router.openUIForResult(
            this@ConfirmRepairOrderActivity,
            PayPlatformActivity::class.java,
            101,
            HashMap<String, String>().apply {
                put(PayPlatformActivity.ORDERNO_KEY, orderNo)
            })
    }

    override fun onLoadUpError(position: Int) {
        val data = mLoadUpImageAdapter?.getData()
        toast("照片上传失败")
        if (data?.size ?: 0 > position) {
            mLoadUpImageAdapter?.getDataAt(position)?.isError = true
            mLoadUpImageAdapter?.notifyDataSetChanged()
        }
    }

    override fun onLoadUpSuccess(ids: String, position: Int) {
        val data = mLoadUpImageAdapter?.getData()
        if (data?.size ?: 0 > position) {
            mLoadUpImageAdapter?.getDataAt(position)
                ?.apply {
                    isLoaded = true
                    id = ids
                }
            mLoadUpImageAdapter?.notifyDataSetChanged()
        }
        //        var faults = StringBuffer()
        //        flowLayout.selectedList.forEach {
        //            var id = UserManager.mFaults[it].id
        //            faults.append(id)
        //                    .append(",")
        //        }
        //        mPresenter?.placeOrder(mSelectedMaster?.id ?: "",
        //            mLeixingId,
        //            faults.substring(0, faults.length - 1),
        //            ids,
        //            etInputRemark.text.toString(),
        //            UserManager.currentLocation[0].toString(),
        //            UserManager.currentLocation[1].toString())
    }

    override fun autoPlaceOrder() {
        placeOrder()
    }

    /**
     * 下单
     */
    private fun placeOrder() {
        var ids = StringBuffer()
        mLoadUpImageAdapter?.getData()
            ?.forEach {
                ids.append(it.id)
                    .append(",")
            }
        var imgs = ""
        if (ids.isNotEmpty()) {
            imgs = ids.substring(0, ids.length - 1)
        }
        var faults = StringBuffer()
        flowLayout.selectedList.forEach {
            var id = UserManager.mFaults[it].id
            faults.append(id)
                .append(",")
        }
        mPresenter?.placeOrder(
            mSelectedMaster?.id ?: "",
            mLeixingId,
            faults.substring(0, faults.length - 1),
            imgs,
            etInputRemark.text.toString(),
            UserManager.currentLocation[0].toString(),
            UserManager.currentLocation[1].toString()
        )
    }

    override fun onError(err: String?) {
        toast(err ?: "")
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 101 && resultCode == 1) {
            setResult(1, Intent().apply {
                putExtra("isSuccess", true)
//                putExtra("orderId",)
            })
            finish()
        }
    }

    fun openPhotAlbum() {
        var count = MAXPICCOUNT - (mLoadUpImageAdapter?.getData()?.size ?: 0)
        Album.image(this)
            .multipleChoice()
            .camera(true)
            .columnCount(3)
            .selectCount(count)
            .onResult {
                it.forEach {
                    mLoadUpImageAdapter?.addData(ImageLoadUp().apply {
                        filepath = it.path
                    })
                }
                //                    LoadingDialog.loading(this)
                //                    mPresenter?.loadUpImages(rvImgLoadUp,mLoadUpImageAdapter?.getData())
            }
            .start()

    }

    override fun onGetStatementSuccess(rule: String, statement: String) {
        tvRule?.text = rule
        tvStatement?.text = statement
    }

    override fun onDestroy() {
        super.onDestroy()
    }

}
