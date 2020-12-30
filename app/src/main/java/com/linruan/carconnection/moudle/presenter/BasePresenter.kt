package com.linruan.carconnection.moudle.presenter

import android.content.Context
import com.linruan.carconnection.moudle.views.BaseView

open class BasePresenter<T : BaseView?> {
    var mContext: Context? = null
    var mView: T?

    constructor(view: T?) {
        mView = view
        if (view is Context)
            mContext = view
    }

    fun onDestory() {
        mView = null
        mContext = null
    }
}