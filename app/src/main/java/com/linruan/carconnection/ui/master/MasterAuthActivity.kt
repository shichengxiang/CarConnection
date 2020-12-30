package com.linruan.carconnection.ui.master

import android.view.Gravity
import android.view.View
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.linruan.carconnection.*
import com.linruan.carconnection.adapter.CarTypeAdapter
import com.linruan.carconnection.adapter.LoadUpImageAdapter3
import com.linruan.carconnection.adapter.OnItemClickListener
import com.linruan.carconnection.decorview.SpaceItemDecoraton
import com.linruan.carconnection.dialog.CommonDialog
import com.linruan.carconnection.dialog.LoadingDialog
import com.linruan.carconnection.entities.ImageLoadUp
import com.linruan.carconnection.net.BaseResponse
import com.linruan.carconnection.net.Client
import com.linruan.carconnection.net.JsonCallback
import com.linruan.carconnection.ui.MainActivity
import com.linruan.carconnection.utils.ImageUtil
import com.linruan.carconnection.utils.Util
import com.lzy.okgo.model.Response
import com.yanzhenjie.album.Album
import kotlinx.android.synthetic.main.activity_masterauth.*
import kotlinx.android.synthetic.main.layout_dialog_sex.view.*
import java.lang.StringBuilder

/**
 * author：shichengxiang on 2020/6/5 15:31
 * email：1328911009@qq.com
 */
class MasterAuthActivity : BaseActivity() {
    var mTvShowRepairType: TextView? = null
    var mCurrentRepairType=""
    var mTvSelectSex: TextView? = null
    var mToolsAdapter: LoadUpImageAdapter3? = null
    var mStoreAdapter: LoadUpImageAdapter3? = null
    val MAXCOUNT = 9

    //
    var headImageId = ""//头像id
    var idCardFrontId = ""//身份证前 id
    var idCardBackId = ""//身份证后 id
    var toolsImageIds = StringBuilder()
    var storeImageIds = StringBuilder() //图片列表

    override fun getLayout() = R.layout.activity_masterauth

    override fun initView() {
        toolbar.setTitle("师傅认证")
        mTvShowRepairType = tvSHowRepairType
        mTvSelectSex = tvSelectSex
        tvSHowRepairType.setOnClickListener { showRepairType() }
        llChooseHead.setOnClickListener { selectHeadImage() }
        tvSelectSex.setOnClickListener { selectSex() }

        rvToolsPhotos.layoutManager = GridLayoutManager(this, 3)
        mToolsAdapter = LoadUpImageAdapter3(this, MAXCOUNT, 3)
        rvToolsPhotos.adapter = mToolsAdapter
        rvToolsPhotos.addItemDecoration(
            SpaceItemDecoraton(
                0,
                Util.px2dp(8),
                0,
                0,
                RecyclerView.VERTICAL,
                true
            )
        )
        mStoreAdapter = LoadUpImageAdapter3(this, MAXCOUNT, 3)
        mToolsAdapter?.setOnItemClickListener(object : OnItemClickListener {
            override fun onItemClickListener(position: Int) {
                openAlbumForTools()
            }
        })
        mStoreAdapter = LoadUpImageAdapter3(this, MAXCOUNT, 3)
        rvStorePhotos.layoutManager = GridLayoutManager(this, 3)
        rvStorePhotos.adapter = mStoreAdapter
        rvStorePhotos.addItemDecoration(
            SpaceItemDecoraton(
                0,
                Util.px2dp(8),
                0,
                0,
                RecyclerView.VERTICAL,
                true
            )
        )
        mStoreAdapter?.setOnItemClickListener(object : OnItemClickListener {
            override fun onItemClickListener(position: Int) {
                openAlbumForStore()
            }
        })
        //身份证
        ivIDCard1.setOnClickListener {
            Album.image(this)
                .singleChoice()
                .camera(true)
                .onResult {
                    Glide.with(this)
                        .load(it[0].path)
                        .dontAnimate()
                        .into(ivIDCard1)
                    loadUpIdCard(it[0].path, true)
                }
                .start()
        }
        ivIDCard2.setOnClickListener {
            Album.image(this)
                .singleChoice()
                .camera(true)
                .onResult {
                    Glide.with(this)
                        .load(it[0].path)
                        .dontAnimate()
                        .into(ivIDCard2)
                    loadUpIdCard(it[0].path, false)
                }
                .start()
        }
        btnCommit.setOnClickListener {
            commit()
        }

    }

    /**
     * 选择修车类型
     */
    private var typeDialog: CommonDialog? = null

    private fun showRepairType() {
        if (typeDialog == null) {
            typeDialog = object : CommonDialog(this, R.layout.layout_dialog_repairtype) {
                override fun initView(root: View) {
                    var rvTypes=root.findViewById<RecyclerView>(R.id.rvCarTypes)
                    rvTypes.layoutManager=LinearLayoutManager(this@MasterAuthActivity)
                    var adapter= CarTypeAdapter(this@MasterAuthActivity,UserManager.mCarTypes)
                    rvTypes.adapter=adapter
                    window?.setGravity(Gravity.BOTTOM)
                    window?.setWindowAnimations(R.style.BottomDialogAnim)
                    root.findViewById<View>(R.id.btnMakeSure)
                        .setOnClickListener {
                            dismiss()
                            var sb = StringBuffer()
                            var sbIds=StringBuffer()
                            adapter.getData()?.forEach {
                                if(it.isChecked){
                                    sb.append(it.title).append(",")
                                    sbIds.append(it.id).append(",")
                                }
                            }
                            if (!sb.isEmpty()) {
                                sb = sb.replace(sb.length - 1, sb.length, "")
                                mCurrentRepairType=sbIds.substring(0,sbIds.length-1)
                                mTvShowRepairType?.text = sb.toString()
                            }

                        }
                }
            }
        }
        typeDialog?.show()
    }

    private fun selectHeadImage() {
        Album.image(this)
            .singleChoice()
            .camera(true)
            .onResult {
                Glide.with(this)
                    .load(it[0].path)
                    .dontAnimate()
                    .into(ivUserHead)
                loadupHead(it[0].path)
            }
            .start()
    }

    private fun selectSex() {
        var dialog = object : CommonDialog(this, R.layout.layout_dialog_sex) {
            override fun initView(root: View) {
                window?.setWindowAnimations(R.style.BottomDialogAnim)
                window?.setGravity(Gravity.BOTTOM)
                root.tvMan.setOnClickListener {
                    mTvSelectSex?.text = "男"
                    dismiss()
                }
                root.tvWoman.setOnClickListener {
                    mTvSelectSex?.text = "女"
                    dismiss()
                }
//                root.tvHehe.setOnClickListener {
//                    mTvSelectSex?.text = "不男不女"
//                    dismiss()
//                }
            }
        }
        dialog.show()
    }

    private fun openAlbumForTools() {
        var count = MAXCOUNT - (mToolsAdapter?.getData()?.size ?: 0)
        Album.image(this)
            .multipleChoice()
            .camera(true)
            .columnCount(3)
            .selectCount(count)
            .onResult {
                if (mToolsAdapter?.getData()?.size ?: 0 == 0) {
                    toolsImageIds.clear()
                }
                var list = arrayListOf<ImageLoadUp>()
                it.forEach {
                    var img = ImageLoadUp().apply { filepath = it.path }
                    list.add(img)
                    mToolsAdapter?.addData(ImageLoadUp().apply { filepath = it.path })
                }
                ImageUtil.build(this).withImagelist(list)
                    .onListener(object : ImageUtil.onUpLoadListener {
                        override fun onLoadupSuccess(id: String, index: Int) {
                            toolsImageIds.append(id).append(",")
                        }

                        override fun onFinishAll() {
                        }

                        override fun onUpLoadError(index: Int) {
                            toast("设备工具上传失败")
                        }

                        override fun onProgress(progres: Int, index: Int) {
                        }

                    }).start()

            }
            .start()
    }

    private fun openAlbumForStore() {
        var count = MAXCOUNT - (mStoreAdapter?.getData()?.size ?: 0)
        Album.image(this)
            .multipleChoice()
            .camera(true)
            .columnCount(3)
            .selectCount(count)
            .onResult {
                if (mStoreAdapter?.getData()?.size ?: 0 == 0) {
                    storeImageIds.clear()
                }
                var list = arrayListOf<ImageLoadUp>()
                it.forEach {
                    var img = ImageLoadUp().apply { filepath = it.path }
                    mStoreAdapter?.addData(img)
                    list.add(img)
                }
                ImageUtil.build(this).withImagelist(list)
                    .onListener(object : ImageUtil.onUpLoadListener {
                        override fun onLoadupSuccess(id: String, index: Int) {
                            storeImageIds.append(id).append(",")
                        }

                        override fun onFinishAll() {
                        }

                        override fun onUpLoadError(index: Int) {
                            toast("设备工具上传失败")
                        }

                        override fun onProgress(progres: Int, index: Int) {
                        }

                    }).start()
            }
            .start()
    }

    /**
     * 头像上传
     */
    private fun loadupHead(url: String) {
        ImageUtil.build(this).withImage(url).onListener(object : ImageUtil.onUpLoadListener {
            override fun onLoadupSuccess(id: String, index: Int) {
                headImageId = id
            }

            override fun onFinishAll() {
            }

            override fun onUpLoadError(index: Int) {
            }

            override fun onProgress(progres: Int, index: Int) {
            }

        }).start()
    }

    /**
     * 身份证上传
     */
    private fun loadUpIdCard(img: String, isFront: Boolean) {
        ImageUtil.build(this).withImage(img).onListener(object : ImageUtil.onUpLoadListener {
            override fun onLoadupSuccess(id: String, index: Int) {
                if (isFront) {
                    idCardFrontId = id
                } else {
                    idCardBackId = id
                }
            }

            override fun onFinishAll() {
            }

            override fun onUpLoadError(index: Int) {
                toast("身份证上传失败请重试")
            }

            override fun onProgress(progres: Int, index: Int) {
            }

        }).start()
    }

    private fun commit() {
        var sex = tvSelectSex.text.toString()
        var name = etName.text.toString()
        var address = etAddress.text.toString()
        var time = etTime.text.toString()
        var leixing = tvSHowRepairType.text.toString()
        var idCardNum = etIdCardNum.text.toString()
        var personNum = etPersonNum.text.toString()
        var contactCall = etContact.text.toString()
        var storeAddress = etStoreAddress.text.toString()
        if (headImageId.isEmpty()) {
            toast("请上传头像")
            return
        }
        if (name.isEmpty()) {
            toast("请填写姓名")
            return
        }
        if (address.isEmpty()) {
            toast("请填写地址")
            return
        }
        if (time.isEmpty()) {
            toast("请填写修车经验")
            return
        }
        if (mCurrentRepairType.isEmpty()) {
            toast("请选择类型")
            return
        }
        if (idCardNum.isEmpty()) {
            toast("请填写身份证号码")
            return
        }
        if (personNum.isEmpty()) {
            toast("请填写员工人数")
            return
        }
        if (contactCall.isEmpty()) {
            toast("请填写负责人电话")
            return
        }
        if (storeAddress.isEmpty()) {
            toast("请填写店铺地址")
            return
        }
        if (idCardFrontId.isEmpty() || idCardBackId.isEmpty()) {
            toast("请上传身份证照片")
            return
        }
        //头像上传
        //身份证上传
        //设备图片上传
        //店铺上传
        if (toolsImageIds.isEmpty()) {
            toast("请确认设备照片上传成功")
            return
        }
        if (storeImageIds.isEmpty()) {
            toast("请确认店铺照片上传成功")
            return
        }
        goNetWork(
            headImageId,
            name,
            sex,
            address,
            time,
            mCurrentRepairType,
            toolsImageIds.substring(0, toolsImageIds.length - 1),
            idCardNum,
            idCardFrontId,
            idCardBackId,
            personNum,
            contactCall,
            storeAddress,
            storeImageIds.substring(0, storeImageIds.length - 1)
        )
//        if (storeImageIds.isEmpty()) {
//            toast("请上传设备工具信息")
//            return
//        }
    }

    private fun goNetWork(
        avatarId: String,
        name: String,
        sex: String,
        address: String,
        jingyan: String,
        leixing: String,
        shebei: String,
        idcard: String,
        idcard_front: String,
        idcard_back: String,
        num: String,
        contact: String,
        storeAddress: String,
        storeImg: String
    ) {
        if (shebei.isEmpty() || storeImg.isEmpty()) {
            return
        }
        LoadingDialog.loading(this)
        Client.getInstance().masterAuth(
            avatarId,
            name,
            sex,
            address,
            jingyan,
            leixing,
            shebei,
            idcard,
            idcard_front,
            idcard_back,
            num,
            contact,
            storeAddress,
            storeImg,
            object : JsonCallback<BaseResponse>() {
                override fun onFinish() {
                    LoadingDialog.dismiss()
                }

                override fun onSuccess(response: Response<BaseResponse?>?) {
                    if (response?.body().isSuccess()) {
                        UserManager.logout()
                        toast(response?.body()?.msg ?: "已提交认证信息等待后台审核")
//                        Router.openUI(this@MasterAuthActivity, MainActivity::class.java)
//                        finish()
                    } else {
                        toast(response?.body()?.msg ?: "提交异常")
                    }
                }
            })
    }

    override fun onDestroy() {
        UserManager.logout()//默认退出登录
        super.onDestroy()
    }
}
