package com.linruan.carconnection.moudle

import androidx.recyclerview.widget.RecyclerView
import com.linruan.carconnection.UserManager
import com.linruan.carconnection.adapter.LoadUpImageAdapter
import com.linruan.carconnection.entities.ImageLoadUp
import com.linruan.carconnection.entities.net.GetStatementResponse
import com.linruan.carconnection.entities.net.LoadUpFile
import com.linruan.carconnection.entities.net.PlaceRepairOrderResponse
import com.linruan.carconnection.isSuccess
import com.linruan.carconnection.logI
import com.linruan.carconnection.moudle.presenter.BasePresenter
import com.linruan.carconnection.moudle.views.BaseView
import com.linruan.carconnection.net.Client
import com.linruan.carconnection.net.JsonCallback
import com.lzy.okgo.OkGo
import com.lzy.okgo.model.Progress
import com.lzy.okgo.model.Response
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.schedulers.Schedulers
import top.zibin.luban.Luban
import java.io.File

/**
 * author：shichengxiang on 2020/6/2 15:30
 * email：1328911009@qq.com
 */
class ConfirmOrderPresenter(view: ConfirmOrderView) : BasePresenter<ConfirmOrderView>(view) {

    /**
     * 图片上传 选择图片后就上传
     */
    fun loadUpImages(recyclerView: RecyclerView?, imgs: ArrayList<ImageLoadUp>?) {
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
                            mView?.autoPlaceOrder()
                        }
                        break
                    }
                    var file = list[i]
                    var index = imgs!![i].position
                    val holder =
                        recyclerView?.findViewHolderForAdapterPosition(index) as LoadUpImageAdapter.Holder
                    Client.getInstance()
                        .loadupImages(file, "loadup", object : JsonCallback<LoadUpFile>() {
                            override fun onSuccess(response: Response<LoadUpFile?>?) {
                                if (response?.body()
                                        ?.isSuccess() == true
                                ) {
                                    var pathId = response?.body()?.data?.id
                                    count--
                                    mView?.onLoadUpSuccess(pathId ?: "", index)
                                    if (count <= 0) {
                                        mView?.autoPlaceOrder()
                                    }
                                } else {
                                    mView?.onLoadUpError(index)
                                    holder.onError()
                                }
                            }

                            override fun uploadProgress(progress: Progress?) {
                                super.uploadProgress(progress)
                                if (progress != null) {
                                    var p =
                                        (progress.currentSize * 100 / progress.totalSize).toInt()
                                    holder.setProgress(p)
                                }
                            }

                            override fun onError(response: Response<LoadUpFile?>?) {
                            }
                        })
                }

            }
    }

    /**
     * 单个文件上传
     */
    fun loadUpImages(recyclerView: RecyclerView?, file: String, position: Int) {
        Observable.create<File> {
            var fs = Luban.with(mContext)
                .get(file)
            logI("缩略图创建成功")
            it.onNext(fs)
        }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { t ->
                val holder =
                    recyclerView?.findViewHolderForAdapterPosition(position) as LoadUpImageAdapter.Holder
                Client.getInstance()
                    .loadupImages(t, "loadup", object : JsonCallback<LoadUpFile>() {
                        override fun onSuccess(response: Response<LoadUpFile?>?) {
                            if (response?.body()
                                    ?.isSuccess() == true
                            ) {
                                var pathId = response?.body()?.data?.id
                                mView?.onLoadUpSuccess(pathId ?: "", position)
                            } else {
                                mView?.onLoadUpError(position)
                                holder.onError()
                            }
                        }

                        override fun uploadProgress(progress: Progress?) {
                            super.uploadProgress(progress)
                            if (progress != null) {
                                var p = (progress.currentSize * 100 / progress.totalSize).toInt()
                                holder.setProgress(p)
                            }
                        }

                        override fun onError(response: Response<LoadUpFile?>?) {
                        }
                    })

            }
    }

    /**
     * 修车订单提交
     * @param workId 师傅id
     * @param typeId 车辆类型id
     * @param fault_ids 故障原因
     * @param imgs 图片资源ids
     */
    fun placeOrder(
        workId: String,
        typeId: String,
        fault_ids: String,
        imgs: String,
        intro: String,
        lat: String,
        lng: String
    ) {
        Client.getInstance()
            .placeRepairOrder(
                workId,
                typeId,
                fault_ids,
                imgs,
                intro,
                lng,
                lat,
                "placeOrder",
                object : JsonCallback<PlaceRepairOrderResponse>() {
                    override fun onSuccess(response: Response<PlaceRepairOrderResponse?>?) {
                        super.onSuccess(response)
                        if (response?.body()
                                ?.isSuccess() == true
                        ) {
                            var data = response?.body()?.data
                            UserManager.limitTime = data?.time ?: 3 * 60L
                            mView?.onPlaceOrderSuccess(data?.orderno ?: "", data?.status ?: 0 == 2)
                        } else {
                            mView?.onError(response?.body()?.msg)
                        }
                    }

                    override fun onError(response: Response<PlaceRepairOrderResponse?>?) {
                        super.onError(response)
                        mView?.onError("网络异常")
                    }
                })
    }

    /**
     * 拉取计价规则和声明
     */
    fun getStatement() {
        Client.getInstance().getStatement(object : JsonCallback<GetStatementResponse>() {
            override fun onSuccess(response: Response<GetStatementResponse?>?) {
                if (response?.body().isSuccess()) {
                    var bean = response?.body()?.data
                    mView?.onGetStatementSuccess(bean?.pricing_rule ?: "", bean?.statement ?: "")
                } else {
                    mView?.onError(response?.body()?.msg ?: "信息拉取失败")
                }
            }
        })
    }

}

interface ConfirmOrderView : BaseView {
    /**
     * 下单成功
     */
    fun onPlaceOrderSuccess(orderNo: String, needPay: Boolean)

    fun onLoadUpSuccess(id: String, position: Int)
    fun onLoadUpError(position: Int)

    /**
     * 上传成功后自主下单
     */
    fun autoPlaceOrder()

    fun onGetStatementSuccess(rule: String, statement: String)
}
