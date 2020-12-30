package com.linruan.carconnection.moudle

import android.view.View
import android.widget.TextView
import com.linruan.carconnection.dialog.LoadingDialog
import com.linruan.carconnection.entities.net.GetGoodsDetailResponse
import com.linruan.carconnection.entities.net.GetShareDataResponse
import com.linruan.carconnection.isSuccess
import com.linruan.carconnection.moudle.presenter.BasePresenter
import com.linruan.carconnection.moudle.views.BaseView
import com.linruan.carconnection.net.BaseResponse
import com.linruan.carconnection.net.Client
import com.linruan.carconnection.net.JsonCallback
import com.lzy.okgo.model.Response

/**
 * author：shichengxiang on 2020/6/10 15:35
 * email：1328911009@qq.com
 */
class GoodsDetailPresenter(view: GoodsDetailView?) : BasePresenter<GoodsDetailView>(view) {

    fun getGoodsDetail(id:String){
        Client.getInstance().getGoodsDetail(id,object :JsonCallback<GetGoodsDetailResponse>(){
            override fun onSuccess(response: Response<GetGoodsDetailResponse?>?) {
                var body=response?.body()
                if(body?.isSuccess()==true){
                        mView?.onGetGoodsDetailSuccess(body.data)
                }else{
                    mView?.onError(body?.msg?:"数据异常")
                }
            }
        })
    }
    fun addGoodsToCar(view: View?, goodsId:String, skuId:String, num:Int){
        view?.isEnabled=false
        LoadingDialog.loading(mContext)
        Client.getInstance().addGoodsToCar(goodsId,skuId,num,object :JsonCallback<BaseResponse>(){
            override fun onFinish() {
                LoadingDialog.dismiss()
                view?.isEnabled=true
            }
            override fun onSuccess(response: Response<BaseResponse?>?) {
                var body=response?.body()
                if(body?.isSuccess()==true){
                    if(view is TextView){
                        view?.text = "已加入购物车"
                        view?.isEnabled=false
                    }
                    mView?.onAddCartSuccess()
                }else{
                    mView?.onError(body?.msg?:"加入购物车失败")
                }
            }
        })
    }

    /**
     * 获取分享数据
     */
    fun getShareData(id:String){
        Client.getInstance().getShareData(id,1,object :JsonCallback<GetShareDataResponse>(){
            override fun onSuccess(response: Response<GetShareDataResponse?>?) {
                if(response?.body().isSuccess()){
                    mView?.onGetShareData(response?.body()?.data)
                }else{
                    mView?.onError(response?.body()?.msg?:"拉取数据异常")
                }
            }

            override fun onError(response: Response<GetShareDataResponse?>?) {
                mView?.onError("分享数据异常")
            }
        })
    }
}
interface GoodsDetailView:BaseView{
    fun onGetGoodsDetailSuccess(data:GetGoodsDetailResponse.DataBean?)
    fun onAddCartSuccess()
    fun onGetShareData(data:GetShareDataResponse.DataBean?)
}
