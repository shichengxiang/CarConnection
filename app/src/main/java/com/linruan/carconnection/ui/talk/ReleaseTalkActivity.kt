package com.linruan.carconnection.ui.talk

import android.content.Intent
import android.view.Gravity
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.amap.api.location.AMapLocation
import com.linruan.carconnection.*
import com.linruan.carconnection.adapter.LoadUpImageAdapter2
import com.linruan.carconnection.adapter.OnItemClickListener
import com.linruan.carconnection.adapter.TextListAdapter
import com.linruan.carconnection.decorview.SpaceItemDecoraton
import com.linruan.carconnection.dialog.CommonDialog
import com.linruan.carconnection.entities.MessageEvent
import com.linruan.carconnection.entities.net.ItemsBean
import com.linruan.carconnection.entities.net.LeixingBean
import com.linruan.carconnection.moudle.ReleaseBBSPresenter
import com.linruan.carconnection.moudle.ReleaseView
import com.linruan.carconnection.utils.MapUtil
import com.linruan.carconnection.utils.Util
import com.yanzhenjie.album.Album
import kotlinx.android.synthetic.main.activity_releasetalk.*
import org.greenrobot.eventbus.EventBus

class ReleaseTalkActivity : BaseActivity(), ReleaseView {

    private val REQUESTCODE_PHOTO = 101
    private var mImageAdapter: LoadUpImageAdapter2? = null
    val maxCount = 6
    private var mPresenter: ReleaseBBSPresenter? = null
    private var leixings = arrayListOf<LeixingBean>()
    private var classifies = arrayListOf<ItemsBean>()
    private var currentLeixingId = 0
    private var currentClassifyId = 0


    override fun getLayout() = R.layout.activity_releasetalk

    override fun initView() {
        mPresenter = ReleaseBBSPresenter(this)
        ivClose.setOnClickListener { onBackPressed() }
        mPresenter?.getTypeAndClassify()
        tvRelease.setOnClickListener {
            var content = etInputTalk.text.toString()
            val imgs = mImageAdapter?.getData()
            if (content.isNullOrBlank() && imgs.isNullOrEmpty()) {
                toast("说点什么吧！")
                return@setOnClickListener
            }
            mPresenter?.releaseBBS(
                tvRelease,
                imgs,
                content,
                currentLeixingId.toString(),
                currentClassifyId.toString(),
                UserManager.currentLocation[0].toString(),
                UserManager.currentLocation[1].toString()
            )
        }
        tvLocation.setOnClickListener {
            Router.openUIForResult(this, SelectLocationActivity::class.java, 101)
        }
        MapUtil.getInfoLocation(this, object : MCallBack<AMapLocation> {
            override fun call(t: AMapLocation, position: Int) {
                tvLocation.text = t.aoiName
                UserManager.currentLocation = arrayOf(t.latitude, t.longitude)
            }
        })
        rvImgs.layoutManager = GridLayoutManager(this, 5)
        mImageAdapter = LoadUpImageAdapter2(this, maxCount)
        rvImgs.addItemDecoration(
            SpaceItemDecoraton(
                0,
                Util.px2dp(10),
                0,
                0,
                RecyclerView.VERTICAL,
                true
            )
        )
        rvImgs.adapter = mImageAdapter
        mImageAdapter?.setOnItemClickListener(object : OnItemClickListener {
            override fun onItemClickListener(position: Int) {
                gotoPhotoAlbum()
            }
        })
        tvTypeOfBBs.setOnClickListener { showTypeDialog(tvTypeOfBBs) }
        tvClassifyOfBBS.setOnClickListener { showClassifyDialog(tvClassifyOfBBS) }
    }

    fun gotoPhotoAlbum() {
        var count = mImageAdapter?.getData()?.size ?: 0
        Album.image(this)
            .multipleChoice()
            .camera(true)
            .selectCount(maxCount - count)
            .columnCount(3)
            .onResult { result ->
                runOnUiThread {
                    result.forEach {
                        mImageAdapter?.addData(it.path)
                    }
                }
            }
            .onCancel {}
            .start()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 101 && resultCode == 1) {
            var adr = data?.getStringExtra("address") ?: ""
            tvLocation.text = adr
        }
    }

    override fun onReleaeSuccess() {
        EventBus.getDefault().post(MessageEvent(MessageEvent.REFRESH_TALKFRAGMENT,"1"))
        if (!isDestroyed)
            onBackPressed()
    }

    override fun onGetTypeAndClassifySuccess(
        leixingBean: ArrayList<LeixingBean>?,
        itemsBean: ArrayList<ItemsBean>?
    ) {
        if (!leixingBean.isNullOrEmpty())
            this.leixings = leixingBean
        if (!itemsBean.isNullOrEmpty())
            this.classifies = itemsBean
    }

    override fun onError(err: String?) {
        toast(err ?: "")
    }

    fun showTypeDialog(textView: TextView) {
        var types = arrayListOf<String>()
        leixings.forEach {
            types.add(it.title ?: "")
        }
        object : CommonDialog(this, R.layout.layout_dialog_list) {
            override fun initView(root: View) {
                window?.setWindowAnimations(R.style.BottomDialogAnim)
                window?.setGravity(Gravity.BOTTOM)
                var rv = root.findViewById<RecyclerView>(R.id.rvPopList)
                rv.layoutManager = LinearLayoutManager(this@ReleaseTalkActivity)
                rv.adapter =
                    TextListAdapter(this@ReleaseTalkActivity, types, object : MCallBack<String> {
                        override fun call(t: String, position: Int) {
                            textView.text = t
                            currentLeixingId = leixings[position].id
                            dismiss()
                        }
                    })
            }

        }.show()
    }

    fun showClassifyDialog(textView: TextView) {
        var clz = arrayListOf<String>()
        classifies.forEach {
            clz.add(it.title ?: "")
        }
        object : CommonDialog(this, R.layout.layout_dialog_list) {
            override fun initView(root: View) {
                window?.setWindowAnimations(R.style.BottomDialogAnim)
                window?.setGravity(Gravity.BOTTOM)
                var rv = root.findViewById<RecyclerView>(R.id.rvPopList)
                rv.layoutManager = LinearLayoutManager(this@ReleaseTalkActivity)
                rv.adapter =
                    TextListAdapter(this@ReleaseTalkActivity, clz, object : MCallBack<String> {
                        override fun call(t: String, position: Int) {
                            textView.text = t
                            currentClassifyId = classifies[position].id
                            dismiss()
                        }
                    })
            }

        }.show()
    }


}
