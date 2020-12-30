package com.linruan.carconnection.view.stateview

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.RelativeLayout
import androidx.annotation.IntDef
import com.linruan.carconnection.R
import com.linruan.carconnection.logE
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy

/**
 * Created by PCPC on 2015/11/16.
 */
/**
 * 描述: TODO
 * 名称: StateView
 * User: csx
 * Date: 11-16
 */
class StateView : RelativeLayout {
    private var mBuilder: StateViewBuilder? = null

    @Retention(RetentionPolicy.SOURCE)
    @IntDef(
        STATE_CONTENT,
        STATE_LOADING,
        STATE_ERROR,
        STATE_EMPTY,
        STATE_CONTENT_LOADING
    )
    annotation class ViewState

    //default show state
    @ViewState
    var mCurrentState = STATE_CONTENT

    //各种状态下的View
    private var mContentView: View? = null
    private var mLoadingView: View? = null
    private var mErrorView: View? = null
    private var mEmptyView: View? = null
    private var inflater: LayoutInflater? = null

    constructor(context: Context?) : super(context!!) {}
    constructor(context: Context?, attrs: AttributeSet?) : super(
        context!!,
        attrs
    ) {
        initStateView(attrs)
    }

    constructor(
        context: Context?,
        attrs: AttributeSet?,
        defStyleAttr: Int
    ) : super(context!!, attrs, defStyleAttr) {
        initStateView(attrs)
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        if (this.childCount >= 1) {
            mContentView = getChildAt(this.childCount-1) //当xml引用时 不会在constructor中获取到
        }
    }

    /**
     * @param attrs
     */
    private fun initStateView(attrs: AttributeSet?) {
        logE("constructor")
        mBuilder = StateViewBuilder(this, attrs)
        inflater = LayoutInflater.from(context)
        val a = context.obtainStyledAttributes(attrs, R.styleable.StateView)

        //初始化各种状态下的布局并添加到stateView中
        if (this.childCount >= 1) {
            mContentView = getChildAt(0)
            //默认子布局第一个为内容布局
        }
        val emptyViewResId = a.getResourceId(R.styleable.StateView_state_empty, -1)
        if (emptyViewResId > -1) {
            mEmptyView = inflater!!.inflate(emptyViewResId, this, false)
        }else{
            mEmptyView= inflater?.inflate(R.layout.layout_default_emptyview,this,false)
        }
        addView(mEmptyView, mEmptyView?.layoutParams)
        val errorViewResId = a.getResourceId(R.styleable.StateView_state_error, -1)
        if (errorViewResId > -1) {
            mErrorView = inflater!!.inflate(errorViewResId, this, false)
            addView(mErrorView, mErrorView?.getLayoutParams())
        }
        val loadingViewResId = a.getResourceId(R.styleable.StateView_state_loading, -1)
        if (loadingViewResId > -1) {
            mLoadingView = inflater!!.inflate(loadingViewResId, this, false)
            addView(mLoadingView, mLoadingView?.getLayoutParams())
        }
        //获取指定的状态，如未指定则默认content
        val givenState =
            a.getInt(R.styleable.StateView_state_current, STATE_CONTENT)
        when (givenState) {
            STATE_CONTENT -> mCurrentState = STATE_CONTENT
            STATE_LOADING -> mCurrentState = STATE_LOADING
            STATE_EMPTY -> mCurrentState = STATE_EMPTY
            STATE_ERROR -> mCurrentState = STATE_ERROR
            STATE_CONTENT_LOADING -> mCurrentState =
                STATE_CONTENT_LOADING
        }
        a.recycle()
    }

    /**
     * 设置各个状态下View的显示与隐藏
     */
    private fun setStateView() {
        when (mCurrentState) {
            STATE_EMPTY -> showStateView(mEmptyView, "Empty View")
            STATE_ERROR -> showStateView(mErrorView, "Error View")
            STATE_LOADING -> showStateView(mLoadingView, "Loading View")
            STATE_CONTENT_LOADING -> {
                if (mContentView == null) {
                    throw NullPointerException("Content View with Loading View")
                }
                mContentView!!.visibility = View.VISIBLE
                if (mLoadingView == null) {
                    throw NullPointerException("Loading View with Content View")
                }
                mLoadingView!!.visibility = View.VISIBLE
                if (mEmptyView != null) mEmptyView!!.visibility = View.GONE
                if (mErrorView != null) mErrorView!!.visibility = View.GONE
            }
            else -> showStateView(mContentView, "Content View")
        }
    }

    /**
     * 获取指定状态下的View
     *
     * @param state
     * @return
     */
    fun getStateView(@ViewState state: Int): View? {
        return when (state) {
            STATE_CONTENT -> mContentView
            STATE_EMPTY -> mEmptyView
            STATE_ERROR -> mErrorView
            STATE_LOADING -> mLoadingView
            else -> null
        }
    }

    /**
     * @param view
     * @param viewName
     */
    private fun showStateView(view: View?, viewName: String) {
        if (mContentView != null) mContentView!!.visibility = View.GONE
        if (mErrorView != null) mErrorView!!.visibility = View.GONE
        if (mEmptyView != null) mEmptyView!!.visibility = View.GONE
        if (mLoadingView != null) mLoadingView!!.visibility = View.GONE
        view?.visibility = View.VISIBLE
    }

    fun show() {
        when (mCurrentState) {
            STATE_LOADING -> {
                if (mContentView != null) mContentView!!.visibility = View.GONE
                if (mErrorView != null) mErrorView!!.visibility = View.GONE
                if (mEmptyView != null) mEmptyView!!.visibility = View.GONE
                if (mLoadingView != null) mLoadingView!!.visibility = View.VISIBLE
            }
            STATE_EMPTY -> {
                if (mContentView != null) mContentView!!.visibility = View.GONE
                if (mErrorView != null) mErrorView!!.visibility = View.GONE
                if (mEmptyView != null) mEmptyView!!.visibility = View.VISIBLE
                if (mLoadingView != null) mLoadingView!!.visibility = View.GONE
            }
            STATE_ERROR -> {
                if (mContentView != null) mContentView!!.visibility = View.GONE
                if (mErrorView != null) mErrorView!!.visibility = View.VISIBLE
                if (mEmptyView != null) mEmptyView!!.visibility = View.GONE
                if (mLoadingView != null) mLoadingView!!.visibility = View.GONE
            }
            else -> {
                if (mContentView != null) mContentView!!.visibility = View.VISIBLE
                if (mErrorView != null) mErrorView!!.visibility = View.GONE
                if (mEmptyView != null) mEmptyView!!.visibility = View.GONE
                if (mLoadingView != null) mLoadingView!!.visibility = View.GONE
            }
        }
    }

    /**
     * 设置当前状态
     *
     * @param state
     */
    @get:ViewState
    var currentState: Int
        get() = mCurrentState
        set(state) {
            if (state != mCurrentState) {
                mCurrentState = state
                setStateView()
            }
        }

    /**
     * 通过代码设置指定状态的View
     *
     * @param stateView     view in state
     * @param state         viewState
     * @param switchToState 是否切换到指定的state
     */
    fun setViewForState(
        stateView: View?,
        @ViewState state: Int,
        switchToState: Boolean
    ) {
        when (state) {
            STATE_CONTENT -> {
                if (mContentView != null) removeView(mContentView)
                mContentView = stateView
                addView(mContentView)
            }
            STATE_LOADING -> {
                if (mLoadingView != null) removeView(mLoadingView)
                mLoadingView = stateView
                addView(mLoadingView)
            }
            STATE_EMPTY -> {
                if (mEmptyView != null) removeView(mEmptyView)
                mEmptyView = stateView
                addView(mEmptyView)
            }
            STATE_ERROR -> {
                if (mErrorView != null) removeView(mErrorView)
                mErrorView = stateView
                addView(mErrorView)
            }
        }
        //切换到指定状态
        if (switchToState) currentState = state
    }

    /**
     * 方法重载，默认不切换至指定状态
     *
     * @param stateView
     * @param state
     */
    fun setViewForState(stateView: View?, @ViewState state: Int) {
        setViewForState(stateView, state, false)
    }

    /**
     * 动态设置指定状态的布局
     *
     * @param layoutRes
     * @param state
     * @param switchToState
     */
    fun setViewForState(
        layoutRes: Int,
        @ViewState state: Int,
        switchToState: Boolean
    ) {
        if (inflater == null) inflater = LayoutInflater.from(context)
        val stateView = inflater!!.inflate(layoutRes, this, false)
        setViewForState(stateView, state, switchToState)
    }

    /**
     * 方法重载，默认不切换至指定状态
     *
     * @param layoutRes
     * @param state
     */
    fun setViewForState(layoutRes: Int, @ViewState state: Int) {
        setViewForState(layoutRes, state, false)
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
//        if (this.childCount>= 1) {
//            mContentView = getChildAt(0)
//            //默认子布局第一个为内容布局
//        }
        requireNotNull(mContentView) { "Content view is not defined" }
        setStateView()
    }

    fun content(): StateViewBuilder? {
        mCurrentState = STATE_CONTENT
        return mBuilder
    }

    fun empty(): StateViewBuilder? {
        mCurrentState = STATE_EMPTY
        return mBuilder
    }

    fun error(): StateViewBuilder? {
        mCurrentState = STATE_ERROR
        return mBuilder
    }

    fun loading(): StateViewBuilder? {
        mCurrentState = STATE_LOADING
        return mBuilder
    }

    inner class StateViewBuilder(
        private val mStateView: StateView,
        attrs: AttributeSet?
    ) {
        fun show() {
            mStateView.show()
        }

    }

    companion object {
        //content
        const val STATE_CONTENT = 0

        //loading
        const val STATE_LOADING = 1

        //error
        const val STATE_ERROR = 2

        //empty
        const val STATE_EMPTY = 3

        //loading with content
        const val STATE_CONTENT_LOADING = 4
    }
}