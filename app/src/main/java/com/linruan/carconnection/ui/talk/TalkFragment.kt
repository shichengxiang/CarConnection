package com.linruan.carconnection.ui.talk

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.viewpager.widget.ViewPager
import com.jaeger.library.StatusBarUtil
import com.linruan.carconnection.*
import com.linruan.carconnection.adapter.BBSPageAdapter
import com.linruan.carconnection.entities.BBS
import com.linruan.carconnection.entities.MessageEvent
import com.linruan.carconnection.moudle.TalkPresenter
import com.linruan.carconnection.moudle.TalkView
import kotlinx.android.synthetic.main.customer_notitfication_layout.view.*
import kotlinx.android.synthetic.main.fragment_talk.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class TalkFragment : BaseFragment(), TalkView {

    private var mRecommendList = arrayListOf<BBS>()
    private var mNearByList = arrayListOf<BBS>()
    private var mAttentionList = arrayListOf<BBS>()
    private var mPresenter: TalkPresenter? = null
    private var mPageAdapter:BBSPageAdapter?=null
    override fun getLayout() = R.layout.fragment_talk
    override fun initView(view: View, savedInstanceState: Bundle?) {
        mPresenter = TalkPresenter(this)
        ivGoRelease.setOnClickListener {
            if (!UserManager.isLoginAndSkip(activity)) {
                return@setOnClickListener
            }
            Router.openUI(activity, ReleaseTalkActivity::class.java) }
        mPageAdapter= BBSPageAdapter(activity)
        vpBBSs.adapter = mPageAdapter
        tabLayout.apply {
            var view = layoutInflater.inflate(R.layout.item_tablayout_tab, null)
            addTab(newTab().setText("推荐")
                    .apply {
                        tag = 0
                    })
            addTab(newTab().setText("附近")
                    .apply {
                        tag = 1
                    })
            addTab(newTab().setText("关注")
                    .apply {
                        tag = 2
                    })
        }
        var textview=View.inflate(activity,R.layout.item_tablayout_tab,null) as TextView
        textview.text="推荐"
        tabLayout.getTabAt(0)?.setCustomView(textview)
        tabLayout.addOnTabSelectedListener(object :
            com.google.android.material.tabs.TabLayout.OnTabSelectedListener {
            override fun onTabReselected(tab: com.google.android.material.tabs.TabLayout.Tab?) {
            }

            override fun onTabUnselected(tab: com.google.android.material.tabs.TabLayout.Tab?) {
                tab?.setCustomView(null)
                //                (tab?.customView as TextView).setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15f)
            }

            override fun onTabSelected(tab: com.google.android.material.tabs.TabLayout.Tab?) {
                vpBBSs.currentItem = tab?.tag as Int
                var textview=View.inflate(activity,R.layout.item_tablayout_tab,null) as TextView
                textview.text=tab.text
                tab.setCustomView(textview)
            }

        })
        vpBBSs.offscreenPageLimit=3
        vpBBSs.addOnPageChangeListener(object :ViewPager.OnPageChangeListener{
            override fun onPageScrollStateChanged(state: Int) {
            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
            }

            override fun onPageSelected(position: Int) {
                tabLayout.selectTab(tabLayout.getTabAt(position))
            }
        })
        //关注
        //        mTalkAdapter?.setOnClickListener(object :TalkAdapter.OnViewClickListener{
        //            override fun onClick(view: View, position: Int) {
        //                mPresenter?.setAttentino(view as TextView,"position")
        //            }
        //        })
        if(!EventBus.getDefault().isRegistered(this)){
            EventBus.getDefault().register(this)
        }
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEventTalkFragment(event:MessageEvent){
        if(event.code==MessageEvent.REFRESH_TALKFRAGMENT){
            if (event.message.isNullOrEmpty()) {
                mPageAdapter?.refresh()
            } else {
                var index = event.message[0].toInt()
                tabLayout.selectTab(tabLayout.getTabAt(index))
                mPageAdapter?.refresh(index)
            }
        }
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (!hidden) {
            StatusBarUtil.setLightMode(activity)
        }
    }

    override fun getData() {
    }

    override fun onGetBBSSuccess(list: ArrayList<BBS>?, isMore: Boolean, tag: Int) {
    }

    override fun onAttentionSuccess(followId: String, isFollow: Int) {

    }

    override fun onDestroyView() {
        super.onDestroyView()
        EventBus.getDefault().unregister(this)
    }

    override fun onError(err: String?) {
        activity?.toast(err ?: "")
    }

}
