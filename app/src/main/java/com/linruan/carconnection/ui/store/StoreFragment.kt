package com.linruan.carconnection.ui.store

import android.content.Context
import android.content.Intent
import android.graphics.Outline
import android.graphics.Rect
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewOutlineProvider
import android.widget.ImageView
import androidx.core.app.ActivityOptionsCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.PagerAdapter
import com.bumptech.glide.Glide
import com.jaeger.library.StatusBarUtil
import com.linruan.carconnection.*
import com.linruan.carconnection.adapter.StoreGridListAdapter
import com.linruan.carconnection.adapter.DiscountAdapter
import com.linruan.carconnection.adapter.StoreCategoryAdapter
import com.linruan.carconnection.entities.Goods
import com.linruan.carconnection.entities.net.GetGoodsIndex
import com.linruan.carconnection.moudle.StorePresenter
import com.linruan.carconnection.moudle.StoreView
import com.linruan.carconnection.utils.Util
import com.scwang.smartrefresh.layout.api.RefreshLayout
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener
import com.scwang.smartrefresh.layout.listener.OnRefreshListener
import kotlinx.android.synthetic.main.fragment_store.*
import java.lang.ref.SoftReference


class StoreFragment : BaseFragment(), StoreView, OnRefreshListener {

    private var mItemGridAdapter: StoreCategoryAdapter? = null
    private var discountAdapter: DiscountAdapter? = null
    private var storeGridAdapter: StoreGridListAdapter? = null
    private var mPresenter: StorePresenter? = null
    override fun getLayout() = R.layout.fragment_store

    override fun initView(view: View, savedInstanceState: Bundle?) {
        mPresenter = StorePresenter(this)
        rlShopCar.setOnClickListener {
            if(!UserManager.isLoginAndSkip(activity)){
                return@setOnClickListener
            }
            Router.openUI(activity, ShopCarActivity::class.java)
        }
        //广告区
        vpAdverts.adapter = activity?.let { StorePageAdapter(it) }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            vpAdverts.outlineProvider = object : ViewOutlineProvider() {
                override fun getOutline(view: View?, outline: Outline?) {
                    outline?.setRoundRect(
                        0, 0, vpAdverts.measuredWidth, vpAdverts.measuredHeight,
                        Util.px2dp(5)
                            .toFloat()
                    )
                }

            }
            vpAdverts.clipToOutline = true
        }
        lineIndicator.setViewPager(vpAdverts)
        //分类
        rvCategory.layoutManager = GridLayoutManager(activity, 4)
        mItemGridAdapter = activity?.let { StoreCategoryAdapter(it) }
        rvCategory.adapter = mItemGridAdapter
        //优惠
        var linearLayoutManager = LinearLayoutManager(activity).apply {
            orientation = LinearLayoutManager.HORIZONTAL
        }
        rvDiscounts.layoutManager = linearLayoutManager
        discountAdapter = activity?.let { DiscountAdapter(it) }
        rvDiscounts.adapter = discountAdapter
        //底部列表
        rvBottomList.layoutManager = GridLayoutManager(activity, 2)
        storeGridAdapter = activity?.let { StoreGridListAdapter(it) }
        rvBottomList.adapter = storeGridAdapter
        rvBottomList.addItemDecoration(object : RecyclerView.ItemDecoration() {
            override fun getItemOffsets(
                outRect: Rect,
                view: View,
                parent: RecyclerView,
                state: RecyclerView.State
            ) {
                var space = 5 * resources.displayMetrics.density.toInt()
                outRect.left = space;
                outRect.right = space;
                outRect.bottom = space;

                // Add top margin only for the first item to avoid double space between items
                //                if (parent.getChildAdapterPosition(view) > 1) {
                outRect.top = space * 2
                //                }
                if (parent.getChildAdapterPosition(view) % 2 == 0) {
                    outRect.right = space;
                } else {
                    outRect.left = space
                }
            }
        })
        rvBottomList.setHasFixedSize(true)
        rvBottomList.isNestedScrollingEnabled = false //滑动不流畅
        llSearchLayout.setOnClickListener {
            val intent = Intent(activity, SearchGoodsListActivity::class.java)
            val options = ActivityOptionsCompat
                .makeSceneTransitionAnimation(
                    activity!!,
                    llSearchLayout, "search"
                )
            activity?.startActivity(intent, options.toBundle())
//            Router.openUI(activity, SearchGoodsListActivity::class.java)
        }
        //刷新
        mRefreshLayout.setOnRefreshListener(this)
    }

    override fun getData() {
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (!hidden) {
            StatusBarUtil.setDarkMode(activity)
        }
    }

    override fun onResume() {
        super.onResume()
        mRefreshLayout?.autoRefresh()
    }

    class StorePageAdapter : PagerAdapter {
        private var mContext: SoftReference<Context?>? = null
        private var mData = arrayListOf<GetGoodsIndex.SlidesBean>()

        constructor(context: Context?) : super() {
            mContext = SoftReference(context)
        }

        constructor(context: Context?, list: ArrayList<GetGoodsIndex.SlidesBean>) : super() {
            mContext = SoftReference(context)
            this.mData = list
        }

        override fun instantiateItem(container: ViewGroup, position: Int): Any {
            var view = LayoutInflater.from(mContext?.get())
                .inflate(R.layout.item_vp_img, null)
            var imageView = view.findViewById<ImageView>(R.id.iv_advert)
            var bean = mData[position]
            Glide.with(mContext?.get()!!).load(bean.cover ?: "")
                .error(R.mipmap.img_store_banner_default)
                .placeholder(R.mipmap.img_store_banner_default).dontAnimate().into(imageView)
            imageView.setOnClickListener {
                if (!bean.url.isNullOrBlank())
                    tryCatch {
                        Router.openUI(
                            mContext?.get(),
                            GoodsDetailActivity::class.java,
                            HashMap<String, Int>().apply {
                                put("goodsId", bean.url?.toInt() ?: 0)
                            })
                    }
            }
            container.addView(view)
            return view
        }

        override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
            container.removeView(`object` as View?)
        }

        override fun isViewFromObject(view: View, `object`: Any): Boolean {
            return view == `object`
        }

        override fun getCount() = mData.size

    }

    override fun onError(err: String?) {
        activity?.toast(err ?: "")
    }

    override fun onRefresh(refreshLayout: RefreshLayout) {
        mPresenter?.getGoodsIndex(mRefreshLayout)
    }

    override fun onGetGoodsIndexSuccess(
        slides: ArrayList<GetGoodsIndex.SlidesBean>?,
        discounts: ArrayList<GetGoodsIndex.DiscountBean>?,
        list: ArrayList<Goods>?,
        itemList: ArrayList<GetGoodsIndex.ItemListBean>?,
        cartNum: Int
    ) {
        if (!slides.isNullOrEmpty()) {
            vpAdverts?.adapter = StorePageAdapter(activity, slides)
            //            lineIndicator.se
        }
        if (!itemList.isNullOrEmpty()) {
            mItemGridAdapter?.setData(itemList)//分类
        }
        if (!discounts.isNullOrEmpty())
            discountAdapter?.setData(discounts)
        if (!list.isNullOrEmpty())
            storeGridAdapter?.setData(list)

        if (cartNum >= 1) {
            tvCartNum?.visibility = View.VISIBLE
            tvCartNum?.text = "$cartNum"
        } else {
            tvCartNum?.visibility = View.GONE
        }

    }
}
