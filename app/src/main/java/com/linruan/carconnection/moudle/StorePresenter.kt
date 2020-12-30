package com.linruan.carconnection.moudle

import com.linruan.carconnection.entities.Goods
import com.linruan.carconnection.entities.net.GetGoodsIndex
import com.linruan.carconnection.moudle.presenter.BasePresenter
import com.linruan.carconnection.moudle.views.BaseView
import com.linruan.carconnection.net.BaseResponse
import com.linruan.carconnection.net.Client
import com.linruan.carconnection.net.JsonCallback
import com.lzy.okgo.model.Response
import com.scwang.smartrefresh.layout.SmartRefreshLayout

/**
 * author：shichengxiang on 2020/6/8 09:24
 * email：1328911009@qq.com
 */
class StorePresenter(view: StoreView) : BasePresenter<StoreView>(view) {

    fun getGoodsIndex(refreshLayout: SmartRefreshLayout) {
        Client.getInstance()
                .getGoodsIndex(object : JsonCallback<GetGoodsIndex>() {
                    override fun onSuccess(response: Response<GetGoodsIndex?>?) {
                        var res=response?.body()
                        if(res?.isSuccess()==true){
                            var slides=res.data?.slides
                            var discounts=res.data?.discount
                            var list=res.data?.list
                            var itemListBean=res.data?.itemlist
                            var cartnum=res.data?.cartnum?:0
                            mView?.onGetGoodsIndexSuccess(slides,discounts,list,itemListBean,cartnum)
                        }
                        refreshLayout.finishRefresh()
                    }

                    override fun onError(response: Response<GetGoodsIndex?>?) {
                        super.onError(response)
                        refreshLayout.finishRefresh()
                    }
                })
    }
}

interface StoreView : BaseView {
    fun onGetGoodsIndexSuccess(slides:ArrayList<GetGoodsIndex.SlidesBean>?,discounts:ArrayList<GetGoodsIndex.DiscountBean>?,list:ArrayList<Goods>?,itemList: ArrayList<GetGoodsIndex.ItemListBean>?,cartnum:Int)
}