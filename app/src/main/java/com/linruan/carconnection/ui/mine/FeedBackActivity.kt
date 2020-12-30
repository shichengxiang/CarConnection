package com.linruan.carconnection.ui.mine

import android.os.Handler
import com.linruan.carconnection.BaseActivity
import com.linruan.carconnection.R
import com.linruan.carconnection.moudle.FeedBackView
import com.linruan.carconnection.moudle.FeedbackPresenter
import com.linruan.carconnection.toast
import kotlinx.android.synthetic.main.activity_feedback.*

class FeedBackActivity:BaseActivity(), FeedBackView {
    private var mPresenter:FeedbackPresenter?=null
    override fun getLayout()= R.layout.activity_feedback

    override fun initView() {
        mPresenter= FeedbackPresenter(this)
        toolbar.setTitle("意见反馈")
        tvCommit.setOnClickListener {
            var content=etContent.text.toString()
            var contact=etContact.text.toString()
            if(content.isBlank()){
                toast("请填写您的问题或意见")
                return@setOnClickListener
            }
            mPresenter?.feedback(content,contact)
        }
    }

    override fun onFeedBackSuccess() {
        toast("提交成功")
        Handler().postDelayed({onBackPressed()},1500)
    }

    override fun onError(err: String?) {
        toast(err?:"")
    }

    override fun onDestroy() {
        super.onDestroy()
        mPresenter?.onDestory()
    }

}