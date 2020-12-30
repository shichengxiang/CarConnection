package com.linruan.carconnection.ui.master

import android.os.Handler
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.linruan.carconnection.*
import com.linruan.carconnection.adapter.*
import com.linruan.carconnection.dialog.LoadingDialog
import com.linruan.carconnection.dialog.TipDialog
import com.linruan.carconnection.entities.FaultPart
import com.linruan.carconnection.entities.ImageLoadUp
import com.linruan.carconnection.entities.MessageEvent
import com.linruan.carconnection.entities.RepairOrderDetail
import com.linruan.carconnection.moudle.MasterRepairOrderDetailPresenter
import com.linruan.carconnection.moudle.MasterRepairOrderDetailView
import com.linruan.carconnection.ui.mine.RepairCarOrderDetailActivity
import com.linruan.carconnection.utils.ImageUtil
import com.linruan.carconnection.utils.Util
import com.yanzhenjie.album.Album
import kotlinx.android.synthetic.main.activity_repairdetail.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

/**
 * author：shichengxiang on 2020/6/3 16:58
 * email：1328911009@qq.com
 */
class MasterRepairDetailActivity : BaseActivity(), MasterRepairOrderDetailView {

    private var mSupplementFaultPhotoAdapter: LoadUpImageAdapter? = null //另行修复照片上传
    private var mFailPhotoAdapter: ShowImageAdapter? = null //失败照片上传
    private var currentType = 2
    private val MAXCOUNT = 6
    private var mFaultOfCustomerPartAdapter: FaultPartsAdapter? = null //客户编辑的损坏部位
    private var mSuppleFaultPartAdapter: FaultPartsAdapter? = null //另行修复部位列表
    private var mFaultPhotoOfCustomerAdapter: ShowImageAdapter? = null //客户图片展示
    private var mTagFlowAdapter: FlowTagAdapter? = null
    private var mPresenter: MasterRepairOrderDetailPresenter? = null
    private var mRepairId = ""
    private var canEdit = false
    private val BUTTON_TIPTXT1 = "提交客户确认"
    private val BUTTON_TIPTXT2 = "重新提交客户确认"

    override fun getLayout() = R.layout.activity_repairdetail

    override fun initView() {
        mPresenter = MasterRepairOrderDetailPresenter(this)
//        currentType = intent.getStringExtra("type")
//                .toInt()
        mRepairId = intent.getStringExtra("repairId")
        tvTitle.setOnClickListener { onBackPressed() }
        //客户填写信息
        rvFaultPriceWithCustomer.layoutManager = LinearLayoutManager(this)
        mFaultOfCustomerPartAdapter =
            FaultPartsAdapter(this, UserManager.getUser()?.isMaster ?: true)
        rvFaultPriceWithCustomer.adapter = mFaultOfCustomerPartAdapter
        rvFaultPhotosOfCustomer.layoutManager = GridLayoutManager(this, 3)
        mFaultPhotoOfCustomerAdapter = ShowImageAdapter(this)
        rvFaultPhotosOfCustomer.adapter = mFaultPhotoOfCustomerAdapter

        mSupplementFaultPhotoAdapter = LoadUpImageAdapter(this, MAXCOUNT)
        rvSupplementFaultPhoto.apply {
            layoutManager = GridLayoutManager(this@MasterRepairDetailActivity, 3)
            adapter = mSupplementFaultPhotoAdapter
        }
        mSupplementFaultPhotoAdapter?.setOnItemClickListener(object : OnItemClickListener {
            override fun onItemClickListener(position: Int) {
                openAlbum(mSupplementFaultPhotoAdapter)
            }
        })
        //点击单个图片上传
        mSupplementFaultPhotoAdapter?.setOnReLoadUpListener(object :
            LoadUpImageAdapter.OnReloadUpListener {
            override fun onReloadup(position: Int) {
                ImageUtil.build(this@MasterRepairDetailActivity)
                    .withImagelist(arrayListOf(mSupplementFaultPhotoAdapter!!.getDataAt(position)))
                    .onListener(object : ImageUtil.onUpLoadListener {
                        override fun onLoadupSuccess(id: String, index: Int) {
                            val data = mSupplementFaultPhotoAdapter?.getData()
                            if (data?.size ?: 0 > index) {
                                data!![index].apply {
                                    isLoaded = true
                                    this.id = id
                                }
                                mSupplementFaultPhotoAdapter?.notifyDataSetChanged()
                            }
                        }

                        override fun onFinishAll() {
                        }

                        override fun onUpLoadError(index: Int) {
                            val data = mSupplementFaultPhotoAdapter?.getData()
                            if (data?.size ?: 0 > index) {
                                data!![index].isError = true
                            }
                            var holder =
                                rvSupplementFaultPhoto.findViewHolderForAdapterPosition(index) as LoadUpImageAdapter.Holder
                            holder.onError()
                        }

                        override fun onProgress(p: Int, index: Int) {
                            var holder =
                                rvSupplementFaultPhoto.findViewHolderForAdapterPosition(index) as LoadUpImageAdapter.Holder
                            holder.setProgress(p)
                        }
                    })
                    .start()
            }

        })
        mFailPhotoAdapter = ShowImageAdapter(this)
        rvFailPhotos.apply {
            layoutManager = GridLayoutManager(this@MasterRepairDetailActivity, 3)
            adapter = mFailPhotoAdapter
        }
//        mFailPhotoAdapter?.setOnItemClickListener(object : OnItemClickListener {
//            override fun onItemClickListener(position: Int) {
//                openAlbum(mFailPhotoAdapter)
//            }
//        })
        btnCommitForConfim.setOnClickListener {
            commit()
        }
        btnConfirmFail.setOnClickListener {
            TipDialog(this).apply {
                getTitleView()?.text = "提示"
                getDescView()?.text = "是否确认维修失败"
                getLeftView()?.apply {
                    text = "否"
                    setOnClickListener { dismiss() }
                }
                getRightView()?.apply {
                    text = "是"
                    setOnClickListener {
                        dismiss()
                        mPresenter?.confirmRepairFail(mRepairId)
                    }
                }
            }.show()
        }
//        initLayoutByType(currentType)
        //另行修复原因
        var faults = arrayListOf<String>()
        UserManager.mFaults?.forEach {
            faults.add(it.title ?: "")
        }
        mTagFlowAdapter = object : FlowTagAdapter(this, faults) {
            override fun onSelected(position: Int, view: View?) {
                mSuppleFaultPartAdapter?.addData(UserManager.mFaults?.get(position))
            }

            override fun unSelected(position: Int, view: View?) {
                mSuppleFaultPartAdapter?.removeData(faults[position])
            }
        }
        flowLayoutReasonsSupplement.adapter = mTagFlowAdapter
        rvSupplementFaultPartPrice.layoutManager = LinearLayoutManager(this)
        mSuppleFaultPartAdapter = FaultPartsAdapter(this, true)
        rvSupplementFaultPartPrice.adapter = mSuppleFaultPartAdapter
        mPresenter?.getDetail(mRepairId)
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this)
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onRefreshPage(event: MessageEvent) {
        if (event.code == MessageEvent.REFRESH_REPAIRORDERD_TOMASTER) {
            mPresenter?.getDetail(mRepairId)
        }
    }


    private fun initLayoutByType(status: Int, step: Int) {
        if (status == 3) {
            tvTitle.text = "已完成"
            tvRemarkSupplement.isEnabled = false
            mSupplementFaultPhotoAdapter?.canAdd(false)
            bottomBtn.visibility = View.GONE
            rlRepairResult.visibility = View.VISIBLE
            btnConfirmFail.visibility = View.GONE
            if (step == 5) {//5成功  6失败
                llFailLayout.visibility = View.GONE
                ivRepairState.setImageResource(R.mipmap.icon_repair_success_circle)
                tvRepairState.text = "维修成功"
                tvRepairTip.text = "维修成功该订单已结束"
            } else {
                llFailLayout.visibility = View.VISIBLE
                ivRepairState.setImageResource(R.mipmap.icon_repair_fail)
                tvRepairState.text = "维修失败"
                tvRepairTip.text = "该订单维修失败"
            }
        } else {
            bottomBtn.visibility = View.VISIBLE
            tvTitle.text = "师傅维修中"
            rlRepairResult.visibility = View.GONE
            tvWaitCustomerTip.visibility = View.GONE
            if (step == 7) {
                llFailLayout.visibility = View.VISIBLE
                btnConfirmFail.visibility = View.VISIBLE
                btnCommitForConfim.text = BUTTON_TIPTXT2
                btnCommitForConfim.isEnabled = true
                rlRepairResult.visibility = View.VISIBLE
                ivRepairState.setImageResource(R.mipmap.icon_repair_fail)
                tvRepairState.text = "维修失败"
                tvRepairTip.text = "客户提交订单维修失败"
            } else if (step==4) {
                btnCommitForConfim.text = BUTTON_TIPTXT1
                btnCommitForConfim.isEnabled = false
                tvWaitCustomerTip.visibility=View.VISIBLE
            } else {
                llFailLayout.visibility = View.GONE
                btnConfirmFail.visibility = View.GONE
                btnCommitForConfim.text = BUTTON_TIPTXT1
                btnCommitForConfim.isEnabled = true
            }
        }
    }

    override fun onDestroy() {
        //        KeyboardUtils.unregisterSoftInputChangedListener(this.window)
        mPresenter?.onDestory()
        mPresenter = null
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }

    private fun openAlbum(imageAdapter: LoadUpImageAdapter?) {
        var count = MAXCOUNT - (imageAdapter?.getData()?.size ?: 0)
        Album.image(this)
            .multipleChoice()
            .selectCount(count)
            .columnCount(3)
            .camera(true)
            .onResult {
                it.forEach {
                    imageAdapter?.addData(ImageLoadUp().apply {
                        filepath = it.path
                    })
                }
            }
            .start()
    }

    /**
     * 需要检查图片是否全部上传么
     * 全部图片上传过一次 不论成功失败 就可以提交了
     */
    private fun commit() {
        var canCommit = true
        mSupplementFaultPhotoAdapter?.getData()
            ?.forEach {
                if (!(it.isLoaded || it.isError)) {
                    canCommit = false
                }
            }
        if (canCommit) {
            collectDataAndCommit()
        } else {
            //默认全部上传后 提交信息
            ImageUtil.build(this)
                .withImagelist(mSupplementFaultPhotoAdapter?.getData())
                .onListener(object : ImageUtil.onUpLoadListener {
                    override fun onLoadupSuccess(id: String, index: Int) {
                        val data = mSupplementFaultPhotoAdapter?.getData()
                        if (data?.size ?: 0 > index) {
                            data!![index].apply {
                                isLoaded = true
                                this.id = id
                            }
                            mSupplementFaultPhotoAdapter?.notifyDataSetChanged()
                        }
                    }

                    override fun onFinishAll() {
                        collectDataAndCommit()
                    }

                    override fun onUpLoadError(index: Int) {
                        val data = mSupplementFaultPhotoAdapter?.getData()
                        if (data?.size ?: 0 > index) {
                            data!![index].isError = true
                        }
                        var holder =
                            rvSupplementFaultPhoto.findViewHolderForAdapterPosition(index) as LoadUpImageAdapter.Holder
                        holder.onError()
                    }

                    override fun onProgress(p: Int, index: Int) {
                        var holder =
                            rvSupplementFaultPhoto.findViewHolderForAdapterPosition(index) as LoadUpImageAdapter.Holder
                        holder.setProgress(p)
                    }

                })
                .start()
        }

    }

    /**
     * 图片上传后收集页面数据并上传
     */
    private fun collectDataAndCommit() {
        LoadingDialog.loading(this)
        //客户选择部位价格
        var partPrice = StringBuffer()
        mFaultOfCustomerPartAdapter?.getData()
            ?.forEach {
                partPrice.append(it.id)
                    .append(":")
                    .append(it.price)
                    .append(",")
            }
        var partTotal = mFaultOfCustomerPartAdapter?.getTotalPrice()
            ?.toString() ?: "0"
        //师傅补充
        var otherPartPrice = StringBuffer()
        mSuppleFaultPartAdapter?.getData()
            ?.forEach {
                otherPartPrice.append(it.id)
                    .append(":")
                    .append(it.price)
                    .append(",")
            }
        var otherPartTotal = mSuppleFaultPartAdapter?.getTotalPrice()
            ?.toString() ?: "0"
        //另行修复照片
        var otherImages = StringBuffer()
        mSupplementFaultPhotoAdapter?.getData()
            ?.forEach {
                otherImages.append(it.id)
                    .append(",")
            }
        var intro = tvRemarkSupplement.text.toString()
        mPresenter?.commit(
            mRepairId,
            if (partPrice.isNotEmpty()) partPrice.substring(0, partPrice.length - 1) else "",
            partTotal,
            if (otherPartPrice.isNotEmpty()) otherPartPrice.substring(
                0,
                otherPartPrice.length - 1
            ) else "",
            otherPartTotal,
            if (otherImages.isNotEmpty()) otherImages.substring(0, otherImages.length - 1) else "",
            intro
        )
    }

    override fun onCommitSuccess() {
//        toast("提交完成\n等待用户确认订单")
        btnCommitForConfim.isEnabled = false
        tvWaitCustomerTip.visibility = View.VISIBLE
    }

    override fun onConfirmFailSuccess() {
        toast("已确认维修失败")
        EventBus.getDefault().post(MessageEvent(MessageEvent.REFRESH_BATTLEFRAGMENT, "3"))
        Handler().postDelayed({ onBackPressed() }, 1000)
    }

    override fun onGetDetailSuccess(repairOrderDetail: RepairOrderDetail?) {
        tvOrderNo.text = repairOrderDetail?.orderno ?: ""
        tvCreateTime.text =
            Util.getDataToString(repairOrderDetail?.create_time ?: "", "yyyy-MM-dd HH:mm:ss")
        tvReachTime.text = Util.getDataToString(
            repairOrderDetail?.arrive_time ?: "",
            "yyyy-MM-dd HH:mm:ss"
        ) //到达时间待定
        repairOrderDetail?.fault?.forEach {
            flowLayoutReasons.addView(Util.getReasonTags(this, it.title ?: ""))
            mFaultOfCustomerPartAdapter?.addData(it)
        }
        var imgs = arrayListOf<String>()
        repairOrderDetail?.imgs?.forEach {
            imgs.add(it.filepath)
        }
        mFaultPhotoOfCustomerAdapter?.setData(imgs)

        //等待确认状态
        if (repairOrderDetail?.status == 2 && repairOrderDetail?.step == 4) {
            onCommitSuccess()
        }
        //是否可编辑
        canEdit =
            !((repairOrderDetail?.status == 2 && repairOrderDetail.step == 4) || repairOrderDetail?.status == 3)
        //维修结果
        initLayoutByType(repairOrderDetail?.status ?: 4, repairOrderDetail?.step ?: 0)
        //数据初始化
        tryCatch {
            //客户价格
            val split = repairOrderDetail?.price?.split(",")
            split?.forEach {
                val part = it.split(":")
                mFaultOfCustomerPartAdapter?.refreshPrice(part[0], part[1])
            }
        }
        //故障原因
        if (!canEdit) {
            flowLayoutReasonsSupplement2.visibility = View.VISIBLE
            flowLayoutReasonsSupplement.visibility = View.GONE
        }
        mSuppleFaultPartAdapter?.clearData()
        tryCatch {
            //另行师傅价格
            var other = repairOrderDetail?.other?.split(",")
            other?.forEach { o ->
                var part = o.split(":")
                UserManager.mFaults?.forEach { p ->
                    if (p.id == part[0].toInt()) {
                        mSuppleFaultPartAdapter?.addData(FaultPart(p.id, p.title, part[1])) //另行价格
                        flowLayoutReasonsSupplement2.addView(
                            Util.getReasonTags(
                                this,
                                p.title ?: ""
                            )
                        ) //另行原因
                    }
                }
            }
        }
        val otherImgs = repairOrderDetail?.other_imgs
        var supplePhoto = arrayListOf<ImageLoadUp>()
        otherImgs?.forEach { bean ->
            supplePhoto.add(ImageLoadUp().apply {
                id = bean.id
                filepath = bean.filepath
            })
        }
        mSupplementFaultPhotoAdapter?.putData(supplePhoto)
        tvRemarkSupplement.setText(repairOrderDetail?.worker_intro ?: "")


        mFaultOfCustomerPartAdapter?.setCanEdit(canEdit)
//        mSuppleFaultPartAdapter?.setCanEdit(canEdit)
        mSupplementFaultPhotoAdapter?.canAdd(canEdit)
        mTagFlowAdapter?.setCanSelect(canEdit)
        var fail_imgs = repairOrderDetail?.fail_imgs
        var fail_reason = repairOrderDetail?.fail_reason
        fail_imgs?.forEach {
            mFailPhotoAdapter?.addData(it.filepath)
        }
        etFailReason.setText(fail_reason ?: "无备注")

        //客户信息
        Glide.with(this).load(repairOrderDetail?.user?.avatar ?: "")
            .placeholder(R.mipmap.img_default_head).error(R.mipmap.img_default_head).dontAnimate()
            .into(iv_head)
        tvUserName.text = repairOrderDetail?.user?.name ?: ""
        tvUserPhone.text = repairOrderDetail?.user?.account ?: ""
        tvUserPhone.setOnClickListener { Util.callPhone(this,repairOrderDetail?.user?.account ?: "") }
    }

    override fun onError(err: String?) {
        toast(err ?: "")
    }
}
