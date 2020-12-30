package com.linruan.carconnection.ui.mine

import android.graphics.Color
import com.linruan.carconnection.BaseActivity
import com.linruan.carconnection.R
import kotlinx.android.synthetic.main.activity_ruleintro.*
import kotlinx.android.synthetic.main.layout_toolbar.view.*

class RuleIntroActivity:BaseActivity() {
    override fun getLayout()= R.layout.activity_ruleintro

    override fun initView() {
        toolbar.tvTitle.apply {
            text=context.getString(R.string.rule_intro)
            setTextColor(Color.parseColor("#FF232323"))
        }
        toolbar.ivBack.setImageResource(R.drawable.ic_back_black)
    }
}