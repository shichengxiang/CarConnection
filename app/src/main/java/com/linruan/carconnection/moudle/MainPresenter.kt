package com.linruan.carconnection.moudle

import com.blankj.utilcode.util.LogUtils
import com.linruan.carconnection.Constant
import com.linruan.carconnection.UserManager
import com.linruan.carconnection.entities.FaultPart
import com.linruan.carconnection.entities.net.GetFaults
import com.linruan.carconnection.entities.net.GetRepairType
import com.linruan.carconnection.entities.net.GetTelResponse
import com.linruan.carconnection.isSuccess
import com.linruan.carconnection.moudle.presenter.BasePresenter
import com.linruan.carconnection.moudle.views.BaseView
import com.linruan.carconnection.net.BaseResponse
import com.linruan.carconnection.net.Client
import com.linruan.carconnection.net.JsonCallback
import com.lzy.okgo.model.Response

/**
 * author：shichengxiang on 2020/6/2 15:39
 * email：1328911009@qq.com
 */
class MainPresenter(view: MainView) : BasePresenter<MainView>(view) {

    fun initData() {
        //车辆类型保存
        Client.getInstance()
                .getRepairType(object : JsonCallback<GetRepairType>() {
                    override fun onSuccess(response: Response<GetRepairType?>?) {
                        super.onSuccess(response)
                        var res = response?.body()
                        if (res?.isSuccess() == true) {
                            UserManager.mCarTypes = res?.data?.list
                        }

                    }

                    override fun onError(response: Response<GetRepairType?>?) {
                    }
                })
        //故障类型保存
        Client.getInstance().getFaults(object :JsonCallback<GetFaults>(){
            override fun onSuccess(response: Response<GetFaults?>?) {
                UserManager.mFaults=response?.body()?.data?.list?: arrayListOf<FaultPart>()
            }

            override fun onError(response: Response<GetFaults?>?) {
            }
        })
        //客服电话
        Client.getInstance().getSericeTel(object :JsonCallback<GetTelResponse>(){
            override fun onSuccess(response: Response<GetTelResponse?>?) {
                if(response?.body().isSuccess()){
                    Constant.CUSTOMERSERVICE_MOBILE=response?.body()?.data?.phone?:""
                }
            }

            override fun onError(response: Response<GetTelResponse?>?) {
                LogUtils.e("客服电话获取失败")
            }
        })
    }
}

interface MainView : BaseView {

}
