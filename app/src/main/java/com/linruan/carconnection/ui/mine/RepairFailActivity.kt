package com.linruan.carconnection.ui.mine

import android.util.TypedValue
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.blankj.utilcode.util.ActivityUtils
import com.linruan.carconnection.BaseActivity
import com.linruan.carconnection.R
import com.linruan.carconnection.Router
import com.linruan.carconnection.adapter.LoadUpImageAdapter
import com.linruan.carconnection.adapter.MTextWatcher
import com.linruan.carconnection.adapter.OnItemClickListener
import com.linruan.carconnection.decorview.SpaceItemDecoraton
import com.linruan.carconnection.entities.ImageLoadUp
import com.linruan.carconnection.entities.MessageEvent
import com.linruan.carconnection.entities.RepairOrder
import com.linruan.carconnection.moudle.RepairFailPresenter
import com.linruan.carconnection.moudle.RepairFailView
import com.linruan.carconnection.toast
import com.linruan.carconnection.ui.MainActivity
import com.linruan.carconnection.utils.ImageUtil
import com.linruan.carconnection.utils.Util
import com.yanzhenjie.album.Album
import kotlinx.android.synthetic.main.activity_repairfail.*
import kotlinx.android.synthetic.main.layout_toolbar.view.*
import org.greenrobot.eventbus.EventBus

/**
 * author：shichengxiang on 2020/5/30 11:35
 * email：1328911009@qq.com
 */
class RepairFailActivity : BaseActivity(), RepairFailView {
    companion object{
        @JvmField
        val REPAIRID_KEY="repairId"
    }

    private var mFailPhotoAdapter: LoadUpImageAdapter? = null
    private var mRepairId: String? = null
    private var mPresenter: RepairFailPresenter? = null
    private val MAXCOUNT = 6

    override fun getLayout() = R.layout.activity_repairfail

    override fun initView() {
        toolbar.setTitle("维修失败")
        toolbar.tvRight.apply {
            text = "投诉"
            setTextSize(TypedValue.COMPLEX_UNIT_DIP, 10f)
            var drawable = ContextCompat.getDrawable(this@RepairFailActivity, R.mipmap.ic_complain)
            drawable?.setBounds(0, 0, drawable?.minimumWidth, drawable?.minimumHeight)
            setCompoundDrawables(null, drawable, null, null)
            setOnClickListener { Router.openUI(this@RepairFailActivity, ComplainActivity::class.java) }
        }
        mPresenter = RepairFailPresenter(this)
        mRepairId = intent.getStringExtra(REPAIRID_KEY)
        tvCommitOrder.setOnClickListener {
            commitOrder()
        }
//        tvReplaceOrder.setOnClickListener {
//            EventBus.getDefault()
//                    .post(MessageEvent(MessageEvent.MAIN_SWITCHTAB, MainActivity.MAIN_TAB_HOME.toString()))
//            ActivityUtils.finishToActivity(MainActivity::class.java, false)
//        }
        etInputFailReason.addTextChangedListener(object : MTextWatcher() {
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                tvInputCount.text = "${s?.length ?: 0}/200"
            }
        })
        //失败图片上传
        rvFailPhotos.layoutManager = GridLayoutManager(this, 5)
        mFailPhotoAdapter = LoadUpImageAdapter(this, MAXCOUNT)
        rvFailPhotos.addItemDecoration(SpaceItemDecoraton(0,Util.px2dp(10),0,0,RecyclerView.VERTICAL,true))
        rvFailPhotos.adapter = mFailPhotoAdapter
        mFailPhotoAdapter?.setOnItemClickListener(object : OnItemClickListener {
            override fun onItemClickListener(position: Int) {
                openPhotoAlbum2()
            }
        })
        mFailPhotoAdapter?.setOnReLoadUpListener(object : LoadUpImageAdapter.OnReloadUpListener {
            override fun onReloadup(position: Int) {
                ImageUtil.build(this@RepairFailActivity)
                        .withImagelist(arrayListOf(mFailPhotoAdapter?.getDataAt(position)!!))
                        .onListener(object : ImageUtil.onUpLoadListener {
                            override fun onLoadupSuccess(id: String, index: Int) {
                                val data = mFailPhotoAdapter?.getData()
                                if (data?.size ?: 0 > index) {
                                    data!![position].apply {
                                        isLoaded = true
                                        this.id = id
                                    }
                                    mFailPhotoAdapter?.notifyDataSetChanged()
                                }
                            }

                            override fun onFinishAll() {
                            }

                            override fun onUpLoadError(index: Int) {
                                val data = mFailPhotoAdapter?.getData()
                                if (data?.size ?: 0 > index) {
                                    data!![position].isError = true
                                }
                                var holder = rvFailPhotos.findViewHolderForAdapterPosition(position) as LoadUpImageAdapter.Holder
                                holder.onError()
                            }

                            override fun onProgress(p: Int, index: Int) {
                                var holder = rvFailPhotos.findViewHolderForAdapterPosition(position) as LoadUpImageAdapter.Holder
                                holder.setProgress(p)
                            }
                        })
                        .start()
            }
        })
    }

    fun openPhotoAlbum2() {
        var count = MAXCOUNT - (mFailPhotoAdapter?.getData()?.size ?: 0)
        Album.image(this)
                .multipleChoice()
                .camera(true)
                .columnCount(3)
                .selectCount(count)
                .onResult {
                    it.forEach {
                        mFailPhotoAdapter?.addData(ImageLoadUp().apply {
                            filepath = it.path
                        })
                    }
                }
                .start()
    }

    fun commitOrder() {
        var canCommit = true
        var imgs=StringBuffer()
        mFailPhotoAdapter?.getData()
                ?.forEach {
                    if (!it.isLoaded){
                        canCommit = false
                        return@forEach
                    }
                    imgs.append(it.id).append(",")
                }
        if (canCommit) {
            mPresenter?.commitFailReason(mRepairId?:"", if(imgs.isNullOrBlank()) "" else imgs.substring(0,imgs.length-1), etInputFailReason.text.toString())
        } else {
            uploadImages()
        }
    }

    override fun onCommitReasonSuccess() {
        toast("提交成功")
        ActivityUtils.finishToActivity(RepairCarOrderDetailActivity::class.java, true)
//        EventBus.getDefault().post(MessageEvent(MessageEvent.REFRESH_HOMEFRAGMENTRUNNINGORDER))
    }

    private fun uploadImages() {
        //失败图片上传
        ImageUtil.build(this)
                .withImagelist(mFailPhotoAdapter?.getData())
                .onListener(object : ImageUtil.onUpLoadListener {
                    override fun onLoadupSuccess(id: String, index: Int) {
                        val data = mFailPhotoAdapter?.getData()
                        if (data?.size ?: 0 > index) {
                            data!![index].apply {
                                isLoaded = true
                                this.id = id
                            }
                            mFailPhotoAdapter?.notifyDataSetChanged()
                        }
                    }

                    override fun onFinishAll() {
                        commitOrder()
                    }

                    override fun onUpLoadError(index: Int) {
                        val data = mFailPhotoAdapter?.getData()
                        if (data?.size ?: 0 > index) {
                            data!![index].isError = true
                        }
                        var holder = rvFailPhotos.findViewHolderForAdapterPosition(index) as LoadUpImageAdapter.Holder
                        holder.onError()
                    }

                    override fun onProgress(p: Int, index: Int) {
                        var holder = rvFailPhotos.findViewHolderForAdapterPosition(index) as LoadUpImageAdapter.Holder
                        holder.setProgress(p)
                    }

                })
                .start()
    }

    override fun onError(err: String?) {
        toast(err?:"")
    }
}
