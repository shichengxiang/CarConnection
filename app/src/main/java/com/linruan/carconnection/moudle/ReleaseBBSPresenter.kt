package com.linruan.carconnection.moudle

import android.os.Environment
import android.provider.MediaStore
import android.view.View
import com.blankj.utilcode.util.ZipUtils
import com.google.gson.Gson
import com.linruan.carconnection.dialog.LoadingDialog
import com.linruan.carconnection.entities.net.GetTypeAndClassify
import com.linruan.carconnection.entities.net.ItemsBean
import com.linruan.carconnection.entities.net.LeixingBean
import com.linruan.carconnection.entities.net.LoadUpFile
import com.linruan.carconnection.logE
import com.linruan.carconnection.moudle.presenter.BasePresenter
import com.linruan.carconnection.moudle.views.BaseView
import com.linruan.carconnection.net.BaseResponse
import com.linruan.carconnection.net.Client
import com.linruan.carconnection.net.JsonCallback
import com.linruan.carconnection.utils.FileUtil
import com.litesuits.common.utils.BitmapUtil
import com.lzy.okgo.model.Response
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Scheduler
import io.reactivex.rxjava3.functions.Consumer
import io.reactivex.rxjava3.functions.Function
import io.reactivex.rxjava3.schedulers.Schedulers
import okhttp3.internal.Util
import top.zibin.luban.Luban
import java.io.File
import java.lang.StringBuilder
import java.util.zip.ZipFile
import kotlin.math.log

/**
 * author：shichengxiang on 2020/6/8 14:52
 * email：1328911009@qq.com
 */
class ReleaseBBSPresenter(view: ReleaseView) : BasePresenter<ReleaseView>(view) {

    /**
     * 获取类型和分类
     */
    fun getTypeAndClassify() {
        Client.getInstance()
                .getTypeAndClass(object : JsonCallback<GetTypeAndClassify>() {
                    override fun onSuccess(response: Response<GetTypeAndClassify?>?) {
                        super.onSuccess(response)
                        var res = response?.body()
                        if (res?.isSuccess() == true) {
                            mView?.onGetTypeAndClassifySuccess(res.data?.leixing, res.data?.items)
                        }

                    }

                    override fun onError(response: Response<GetTypeAndClassify?>?) {
                        mView?.onError("获取话题分类失败")
                    }
                })


    }

    /**
     * 发布
     */
    fun releaseBBS(view: View?, imgs: ArrayList<String>?, content: String, leixing_id: String, items_id: String, lat: String, lng: String) {
        view?.isEnabled = false
        LoadingDialog.loading(mContext)
        var count = imgs?.size ?: 0
        var index = 0
        var sbImgs = StringBuilder()
        if (count == 0) {
            onLoadupImageSuccess(view, content, "", leixing_id, items_id, lat, lng)
        } else {
            Observable.create<List<File>> {
                var fs = Luban.with(view?.context)
                        .load(imgs)
                        .get()
                it.onNext(fs)
            }
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe { list ->
                        list.forEach {
                            Client.getInstance()
                                    .loadupImages(it, "loadup", object : JsonCallback<LoadUpFile>() {
                                        override fun onSuccess(response: Response<LoadUpFile?>?) {
                                            if (response?.body()
                                                        ?.isSuccess() == true) {
                                                index++
                                                var pathId = response?.body()?.data?.id
                                                sbImgs.append(pathId)
                                                        .append(",")
                                            }
                                            if (index == count) {
                                                var sb = sbImgs.substring(0, sbImgs.length - 1)
                                                logE("imgsids==${sbImgs.toString()}   $sb")
                                                onLoadupImageSuccess(view, content, sb, leixing_id, items_id, lat, lng)
                                            }
                                        }

                                        override fun onError(response: Response<LoadUpFile?>?) {
                                        }
                                    })
                        }
                    }
        }
        //            imgs?.forEach {
        //                var bm = BitmapUtil.getSmallBitmap(it, 400, 400)
        //                Environment.getExternalStorageDirectory()
        //                //                var path= "${mContext?.getExternalFilesDir(Environment.DIRECTORY_PICTURES)}/linruan/${System.currentTimeMillis()}.jpg"
        //                var path = FileUtil.saveBitmap("talk", bm)
        //                Client.getInstance()
        //                        .loadupImages(File(path), "loadup", object : JsonCallback<LoadUpFile>() {
        //                            override fun onSuccess(response: Response<LoadUpFile?>?) {
        //                                if (response?.body()
        //                                            ?.isSuccess() == true) {
        //                                    index++
        //                                    var pathId = response?.body()?.data?.id
        //                                    sbImgs.append(pathId)
        //                                            .append(",")
        //                                }
        //                                if (index == count) {
        //                                    var sb = sbImgs.substring(0, sbImgs.length - 1)
        //                                    logE("imgsids==${sbImgs.toString()}   $sb")
        //                                    onLoadupImageSuccess(view, content, sb, leixing_id, items_id, lat, lng)
        //                                }
        //                            }
        //
        //                            override fun onError(response: Response<LoadUpFile?>?) {
        //                            }
        //                        })
        //            }
        //    }
    }

    fun onLoadupImageSuccess(view: View?, content: String, imgs: String, leixing_id: String, items_id: String, lat: String, lng: String) {
        LoadingDialog.loading(mContext)
        Client.getInstance()
                .releaseBBS(content, imgs, leixing_id, items_id, lat, lng, object : JsonCallback<BaseResponse>() {
                    override fun onFinish() {
                        view?.isEnabled = true
                    }

                    override fun onSuccess(response: Response<BaseResponse?>?) {
                        super.onSuccess(response)
                        mView?.onReleaeSuccess()
                    }

                    override fun onError(response: Response<BaseResponse?>?) {
                        mView?.onError("发布失败，请重新尝试")
                    }
                })
    }
}

interface ReleaseView : BaseView {
    fun onReleaeSuccess()
    fun onGetTypeAndClassifySuccess(leixingBean: ArrayList<LeixingBean>?, itemsBean: ArrayList<ItemsBean>?)
}
