package com.linruan.carconnection.moudle

import androidx.annotation.IntRange
import com.linruan.carconnection.dialog.LoadingDialog
import com.linruan.carconnection.entities.Address
import com.linruan.carconnection.entities.City
import com.linruan.carconnection.entities.net.GetAddressListResponse
import com.linruan.carconnection.entities.net.GetCityResponse
import com.linruan.carconnection.moudle.presenter.BasePresenter
import com.linruan.carconnection.moudle.views.BaseView
import com.linruan.carconnection.net.BaseResponse
import com.linruan.carconnection.net.Client
import com.linruan.carconnection.net.JsonCallback
import com.lzy.okgo.model.Response

/**
 * author：shichengxiang on 2020/6/12 11:02
 * email：1328911009@qq.com
 */
class AddressListPresenter(view: AddressListView?) : BasePresenter<AddressListView>(view) {
    /**
     * 地址列表
     */
    fun getAddressList() {
        LoadingDialog.loading(mContext)
        Client.getInstance()
                .getAddressList(object : JsonCallback<GetAddressListResponse>() {
                    override fun onFinish() {
                        LoadingDialog.dismiss()
                    }

                    override fun onSuccess(response: Response<GetAddressListResponse?>?) {
                        var body = response?.body()
                        if (body?.isSuccess() == true) {
                            mView?.onGetAddressSuccess(body?.data?.list)
                        } else {
                            mView?.onError(body?.msg ?: "地址拉取失败")
                        }
                    }
                })
    }
    /**
     * 删除地址
     */
    fun deleteAddress(id: String) {
        LoadingDialog.loading(mContext)
        Client.getInstance()
                .deleteAddress(id, object : JsonCallback<BaseResponse>() {
                    override fun onFinish() {
                        LoadingDialog.dismiss()
                    }
                    override fun onSuccess(response: Response<BaseResponse?>?) {
                        var body = response?.body()
                        if (body?.isSuccess() == true) {
                            mView?.onDeleteAddressSuccess()
                            mView?.onError("已删除选定的收货地址")
                        } else {
                            mView?.onError(body?.msg ?: "地址拉取失败")
                        }
                    }
                })
    }

    /**
     * 添加 修改地址
     */
    fun addAddress(id: String, name: String, phone: String, province_id: String, city_id: String, area_id: String, address: String) {
        LoadingDialog.loading(mContext)
        Client.getInstance()
                .addAddress(id, name, phone, province_id, city_id, area_id, address, object : JsonCallback<BaseResponse>() {
                    override fun onFinish() {
                        LoadingDialog.dismiss()
                    }

                    override fun onSuccess(response: Response<BaseResponse?>?) {
                        var body = response?.body()
                        if (body?.isSuccess() == true) {
                            mView?.onError(if (id.isNullOrBlank()) "添加地址成功" else "修改地址成功")
                            mView?.onAddOrModifyAddressSuccess()
                        } else {
                            mView?.onError(body?.msg ?: "地址拉取失败")
                        }
                    }
                })
    }
    fun getCity(parentId:Int,@IntRange(from = 0,to = 2)type:Int){
        Client.getInstance().getCity(parentId,object :JsonCallback<GetCityResponse>(){
            override fun onSuccess(response: Response<GetCityResponse?>?) {
                if(!response?.body()?.data?.list.isNullOrEmpty()){
                    mView?.onGetCityListSuccess(response?.body()?.data?.list!!,type)
                }
            }

            override fun onError(response: Response<GetCityResponse?>?) {
            }
        })
    }
}

interface AddressListView : BaseView {
    fun onGetAddressSuccess(list: ArrayList<Address>?)
    fun onGetCityListSuccess(cities:ArrayList<City>,type:Int)
    fun onAddOrModifyAddressSuccess()
    fun onDeleteAddressSuccess()
}