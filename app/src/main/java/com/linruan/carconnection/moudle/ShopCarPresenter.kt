package com.linruan.carconnection.moudle

import android.view.View
import com.linruan.carconnection.dialog.LoadingDialog
import com.linruan.carconnection.entities.Goods
import com.linruan.carconnection.entities.net.GetCartListResponse
import com.linruan.carconnection.moudle.presenter.BasePresenter
import com.linruan.carconnection.moudle.views.BaseView
import com.linruan.carconnection.net.BaseResponse
import com.linruan.carconnection.net.Client
import com.linruan.carconnection.net.JsonCallback
import com.linruan.carconnection.toast
import com.lzy.okgo.model.Response

/**
 * author：shichengxiang on 2020/6/10 17:48
 * email：1328911009@qq.com
 */
class ShopCarPresenter(view: ShopCarView?) : BasePresenter<ShopCarView>(view) {

    /**
     * 购物车列表
     */
    fun getCartList(){
        LoadingDialog.loading(mContext)
        Client.getInstance().getCartList(object:JsonCallback<GetCartListResponse>(){
            override fun onFinish() {
                LoadingDialog.dismiss()
            }
            override fun onSuccess(response: Response<GetCartListResponse?>?) {
                var body=response?.body()
                if(body?.isSuccess()==true){
                    mView?.onGetCartListSuccess(body?.data?.list)
                }else{
                    mView?.onError(body?.msg?:"数据异常")
                }
            }
        })
    }
    fun deleteGoodsInCart(view:View,vararg ids:String){
        if(ids.isNullOrEmpty()){
            return
        }
        LoadingDialog.loading(mContext)
        view.isEnabled=false
        var sids=StringBuffer()
        ids.forEach {
            sids.append(it).append(",")
        }
        Client.getInstance().deleteGoodsInCart(sids.substring(0,sids.length-1),object :JsonCallback<BaseResponse>(){
            override fun onFinish() {
                LoadingDialog.dismiss()
                view.isEnabled=true
            }

            override fun onSuccess(response: Response<BaseResponse?>?) {
                var body=response?.body()
                if(body?.isSuccess()==true){
                    mView?.onDeleteGoodsInCartSuccess()
                }else{
                    mView?.onError(body?.msg?:"删除购物车异常")
                }
            }
        })

    }
}
interface ShopCarView:BaseView{
    fun onGetCartListSuccess(list:ArrayList<Goods>?)
    fun onDeleteGoodsInCartSuccess()
}