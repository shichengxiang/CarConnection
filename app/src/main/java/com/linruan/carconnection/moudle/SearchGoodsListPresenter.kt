package com.linruan.carconnection.moudle

import com.linruan.carconnection.entities.Goods
import com.linruan.carconnection.entities.net.GetGoodsListResponse
import com.linruan.carconnection.entities.net.ItemsBean
import com.linruan.carconnection.moudle.presenter.BasePresenter
import com.linruan.carconnection.moudle.views.BaseView
import com.linruan.carconnection.net.BaseResponse
import com.linruan.carconnection.net.Client
import com.linruan.carconnection.net.JsonCallback
import com.linruan.carconnection.view.refreshlayout.MRefreshLayout
import com.lzy.okgo.model.Response

/**
 * author：shichengxiang on 2020/6/11 15:45
 * email：1328911009@qq.com
 */
class SearchGoodsListPresenter(view: SearchGoodsListView?) : BasePresenter<SearchGoodsListView>(view) {

    fun getGoodsList(itemId:String,sort:Int,brandId:String,keys:String){
        Client.getInstance().getGoodsList(itemId,sort,brandId,keys,object :JsonCallback<GetGoodsListResponse>(){
            override fun onFinish() {
                mView?.onFinishNet(false)
            }
            override fun onSuccess(response: Response<GetGoodsListResponse?>?) {
                var body=response?.body()
                if(body?.isSuccess()==true){
                    mView?.onGetGoodsListSuccess(body.data?.brandlist,body.data?.list)
                }else{
                    mView?.onError(body?.msg?:"搜索数据异常")
                }
            }
        })
    }
}
interface SearchGoodsListView:BaseView{
    fun onFinishNet(isMore:Boolean)
    fun onGetGoodsListSuccess(brands:ArrayList<ItemsBean>?,goodsList:ArrayList<Goods>?)
}