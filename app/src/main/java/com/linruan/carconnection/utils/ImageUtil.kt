package com.linruan.carconnection.utils

import android.content.Context
import com.linruan.carconnection.entities.ImageLoadUp
import com.linruan.carconnection.entities.net.LoadUpFile
import com.linruan.carconnection.logI
import com.linruan.carconnection.net.Client
import com.linruan.carconnection.net.JsonCallback
import com.lzy.okgo.model.Progress
import com.lzy.okgo.model.Response
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.schedulers.Schedulers
import top.zibin.luban.Luban
import java.io.File

/**
 * author：shichengxiang on 2020/6/18 09:54
 * email：1328911009@qq.com
 */
class ImageUtil {
    private var mContext: Context? = null
    private var mImgsList: ArrayList<ImageLoadUp>? = null
    private var singleImage = ""
    private var isSingle = false//是否是一个文件

    private constructor(context: Context) {
        this.mContext = context
    }

    companion object {
        fun build(context: Context): ImageUtil {
            return ImageUtil(context)
        }
    }

    fun withImagelist(list: ArrayList<ImageLoadUp>?): ImageUtil {
        mImgsList = list
        isSingle = false
        return this
    }

    fun withImage(file: String): ImageUtil {
        singleImage = file
        isSingle = true
        return this
    }

    fun onListener(loadListener: onUpLoadListener): ImageUtil {
        this.monUpLoadListener = loadListener
        return this
    }

    fun start() {
        if (!isSingle)
            loadUpImages(mImgsList)
        else
            loadUpImage(singleImage)
    }

    private fun loadUpImages(imgs: ArrayList<ImageLoadUp>?) {
        if (imgs?.size ?: 0 == 0) {
            //默认可以不传照片
            return
        }
        var count = imgs?.size ?: 0
        var files = arrayListOf<String>()
        imgs?.forEach {
            files.add(it.filepath)
        }
        Observable.create<List<File>> {
            var fs = Luban.with(mContext)
                .load(files)
                .get()
            logI("缩略图创建成功")
            it.onNext(fs)
        }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { list ->
                for (i in list.indices) {
                    if (imgs!![i].isLoaded) {
                        count--
                        if (count <= 0) {
                            monUpLoadListener?.onFinishAll()
                        }
                        break
                    }
                    var file = list[i]
                    var index = imgs!![i].position
                    Client.getInstance()
                        .loadupImages(file, "loadup", object : JsonCallback<LoadUpFile>() {
                            override fun onSuccess(response: Response<LoadUpFile?>?) {
                                count--
                                if (response?.body()
                                        ?.isSuccess() == true
                                ) {
                                    var pathId = response?.body()?.data?.id
                                    monUpLoadListener?.onLoadupSuccess(pathId ?: "", index)
                                } else {
                                    monUpLoadListener?.onUpLoadError(index)
                                    //                                            holder.onError()
                                }
                                if (count <= 0) {
                                    monUpLoadListener?.onFinishAll()
                                    mContext = null
                                }
                            }

                            override fun uploadProgress(progress: Progress?) {
                                super.uploadProgress(progress)
                                if (progress != null) {
                                    var p =
                                        (progress.currentSize * 100 / progress.totalSize).toInt()
                                    //                                            holder.setProgress(p)
                                    monUpLoadListener?.onProgress(p, index)
                                }
                            }

                            override fun onError(response: Response<LoadUpFile?>?) {
                                count--
                                if (count <= 0) {
                                    monUpLoadListener?.onFinishAll()
                                    mContext = null
                                }
                            }
                        })
                }

            }
    }

    /**
     * 单个文件上传
     */
    fun loadUpImage(file: String) {
        Observable.create<File> {
            var fs = Luban.with(mContext)
                .get(file)
            logI("缩略图创建成功")
            it.onNext(fs)
        }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { t ->
                Client.getInstance()
                    .loadupImages(t, "loadup", object : JsonCallback<LoadUpFile>() {
                        override fun onSuccess(response: Response<LoadUpFile?>?) {
                            if (response?.body()
                                    ?.isSuccess() == true
                            ) {
                                var pathId = response?.body()?.data?.id
                                monUpLoadListener?.onLoadupSuccess(pathId ?: "", 0)
                            } else {
                                monUpLoadListener?.onUpLoadError(0)
                            }
                        }

                        override fun uploadProgress(progress: Progress?) {
                            super.uploadProgress(progress)
                            if (progress != null) {
                                var p = (progress.currentSize * 100 / progress.totalSize).toInt()
                                monUpLoadListener?.onProgress(p, 0)
                            }
                        }

                        override fun onError(response: Response<LoadUpFile?>?) {
                        }
                    })

            }
    }

    interface onUpLoadListener {
        /**
         * 单一上传成功
         */
        fun onLoadupSuccess(id: String, index: Int)

        /**
         * 全部上传成功
         */
        fun onFinishAll()

        /**
         * 单一上传失败
         */
        fun onUpLoadError(index: Int)

        fun onProgress(progres: Int, index: Int)
    }

    private var monUpLoadListener: onUpLoadListener? = null
}
