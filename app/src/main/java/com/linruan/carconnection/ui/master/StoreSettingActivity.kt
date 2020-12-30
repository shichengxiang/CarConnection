package com.linruan.carconnection.ui.master

import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.linruan.carconnection.*
import com.linruan.carconnection.adapter.LoadUpImageAdapter3
import com.linruan.carconnection.adapter.OnItemClickListener
import com.linruan.carconnection.decorview.SpaceItemDecoraton
import com.linruan.carconnection.dialog.LoadingDialog
import com.linruan.carconnection.entities.ImageLoadUp
import com.linruan.carconnection.net.BaseResponse
import com.linruan.carconnection.net.Client
import com.linruan.carconnection.net.JsonCallback
import com.linruan.carconnection.utils.ImageUtil
import com.linruan.carconnection.utils.Util
import com.lzy.okgo.model.Response
import com.yanzhenjie.album.Album
import kotlinx.android.synthetic.main.activity_storesetting.*
import java.lang.StringBuilder

/**
 * author：shichengxiang on 2020/6/3 11:00
 * email：1328911009@qq.com
 */
class StoreSettingActivity : BaseActivity() {

    private var mImageAdapter: LoadUpImageAdapter3? = null
    var imgIds=StringBuilder()
    val MAXCOUNT=9
    override fun getLayout() = R.layout.activity_storesetting

    override fun initView() {
        toolbar.setTitle("门店设置")
        var layoutManager=GridLayoutManager(this, 3)
        rvLoadUp.layoutManager=layoutManager
        rvLoadUp.addItemDecoration(SpaceItemDecoraton(0, Util.px2dp(10), 0, 0, RecyclerView.VERTICAL,true))
        mImageAdapter = LoadUpImageAdapter3(this,MAXCOUNT)
        rvLoadUp.adapter = mImageAdapter
        mImageAdapter?.setOnItemClickListener(object : OnItemClickListener {
            override fun onItemClickListener(position: Int) {
                openAlbum()
            }
        })
        //初始化默认值
        if(!UserManager.masterStatus?.phone.isNullOrBlank()){
            etPhone.setText(UserManager.masterStatus?.phone)
        }
        if(!UserManager.masterStatus?.address.isNullOrBlank()){
            etDetailAddress.setText(UserManager.masterStatus?.address)
        }
        if(!UserManager.masterStatus?.imgs.isNullOrEmpty()){
            mImageAdapter?.addData(UserManager.masterStatus?.imgs)
        }

        btnCommit.setOnClickListener {
            var phone=etPhone.text.toString()
            var address=etDetailAddress.text.toString()
            if(phone.isNullOrBlank()){
                toast("请填写联系电话")
                return@setOnClickListener
            }
            if(address.isNullOrBlank()){
                toast("请填写门店地址")
                return@setOnClickListener
            }
            if(imgIds.isNullOrBlank()){
                toast("请检查是否上传店面照片")
                return@setOnClickListener
            }
            LoadingDialog.loading(this)
            Client.getInstance().setupStore(phone,address,imgIds.substring(0,imgIds.length-1),object :JsonCallback<BaseResponse>(){
                override fun onFinish() {
                    LoadingDialog.dismiss()
                }
                override fun onSuccess(response: Response<BaseResponse?>?) {
                    if(response?.body().isSuccess()){
                        toast("提交成功")
                    }else{
                        toast(response?.body()?.msg?:"提交失败")
                    }
                }
            })
        }

    }

    private fun openAlbum() {
        var count=MAXCOUNT-(mImageAdapter?.getData()?.size?:0)
        Album.image(this)
                .multipleChoice()
                .camera(true)
                .selectCount(count)
                .columnCount(3)
                .onResult {
                    if(mImageAdapter?.getData()?.size?:0==0){
                        imgIds.clear()
                    }
                    var list= arrayListOf<ImageLoadUp>()
                    it.forEach {
                        var img=ImageLoadUp().apply {
                            filepath=it.path
                        }
                        list.add(img)
                        mImageAdapter?.addData(img)
                    }
                    ImageUtil.build(this).withImagelist(list)
                        .onListener(object :ImageUtil.onUpLoadListener{
                            override fun onLoadupSuccess(id: String, index: Int) {
                                imgIds.append(id).append(",")
                            }

                            override fun onFinishAll() {
                            }

                            override fun onUpLoadError(index: Int) {
                            }

                            override fun onProgress(progres: Int, index: Int) {
                            }

                        }).start()
                }
                .start()
    }
}