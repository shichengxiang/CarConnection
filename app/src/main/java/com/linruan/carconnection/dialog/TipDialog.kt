package com.linruan.carconnection.dialog

import android.app.Dialog
import android.content.Context
import android.view.View
import android.view.WindowManager
import android.widget.TextView
import com.linruan.carconnection.R
import kotlinx.android.synthetic.main.layout_dialog_tip.view.*

open class TipDialog : Dialog {
    private var mTitleView:TextView?=null
    private var mDescView:TextView?=null
    private var mLeftView:TextView?=null
    private var mRightView:TextView?=null
    private var mllDoubleView: View?=null
    private var mllSingleView:View?=null
    var mSingleBtn:TextView?=null
    constructor(context: Context) : super(context, R.style.TipDialog){
        var root=layoutInflater.inflate(R.layout.layout_dialog_tip,null)
        setContentView(root)
        setCanceledOnTouchOutside(true)
        setCancelable(true)
        val dm = context.resources.displayMetrics
        window?.attributes?.width = (dm.widthPixels * 0.8).toInt()
        window?.attributes?.height = WindowManager.LayoutParams.WRAP_CONTENT
        mTitleView=root.tvDialogTitle
        mDescView=root.tvDialogContent
        mLeftView=root.tvLeft
        mRightView=root.tvRight
        mllDoubleView=root.llDoubleView
        mllSingleView=root.llSingleView
        mSingleBtn=root.tvSingleBtn
        setCancelable(false)
    }
    public fun getTitleView()=mTitleView
    public fun getDescView()=mDescView
    public fun getLeftView()=mLeftView
    public fun getRightView()=mRightView

    public fun initView(){

    }

    /**
     * 1：单一button  2 两个button
     */
    fun  setMode(mode:Int){
        if(mode==1){
            mllSingleView?.visibility=View.VISIBLE
            mllDoubleView?.visibility=View.GONE
        }else{
            mllSingleView?.visibility=View.GONE
            mllDoubleView?.visibility=View.VISIBLE
        }
    }

    override fun setContentView(layoutResID: Int) {
        super.setContentView(layoutResID)
    }
}
