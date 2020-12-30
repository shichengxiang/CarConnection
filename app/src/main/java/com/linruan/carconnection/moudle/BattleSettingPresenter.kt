package com.linruan.carconnection.moudle

import com.linruan.carconnection.dialog.LoadingDialog
import com.linruan.carconnection.isSuccess
import com.linruan.carconnection.moudle.presenter.BasePresenter
import com.linruan.carconnection.moudle.views.BaseView
import com.linruan.carconnection.net.BaseResponse
import com.linruan.carconnection.net.Client
import com.linruan.carconnection.net.JsonCallback
import com.lzy.okgo.model.Response

class BattleSettingPresenter(view: BattleSettingView?) : BasePresenter<BattleSettingView>(view) {

    fun battleSetting(grab: Int, scope: Int, leixingIds: String?) {
        LoadingDialog.loading(mContext)
        Client.getInstance()
            .setupBattle(grab, scope, leixingIds, object : JsonCallback<BaseResponse>() {
                override fun onFinish() {
                    LoadingDialog.dismiss()
                }

                override fun onSuccess(response: Response<BaseResponse?>?) {
                    if (!response?.body().isSuccess()) {
                        mView?.onError(response?.body()?.msg ?: "设置失败")
                    } else {
                        mView?.onError("设置成功")
                    }
                }
            })
    }
}

interface BattleSettingView : BaseView {

}