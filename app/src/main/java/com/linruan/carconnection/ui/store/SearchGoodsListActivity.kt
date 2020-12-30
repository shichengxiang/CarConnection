package com.linruan.carconnection.ui.store

import android.graphics.Rect
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.linruan.carconnection.BaseActivity
import com.linruan.carconnection.MCallBack
import com.linruan.carconnection.R
import com.linruan.carconnection.adapter.StoreGridListAdapter
import com.linruan.carconnection.adapter.TextCheckListAdapter
import com.linruan.carconnection.dialog.BasePopupWindow
import com.linruan.carconnection.entities.Goods
import com.linruan.carconnection.entities.net.ItemsBean
import com.linruan.carconnection.moudle.SearchGoodsListPresenter
import com.linruan.carconnection.moudle.SearchGoodsListView
import com.linruan.carconnection.toast
import com.scwang.smartrefresh.layout.api.RefreshLayout
import com.scwang.smartrefresh.layout.listener.OnRefreshListener
import kotlinx.android.synthetic.main.activity_searchgoods.*
import kotlinx.android.synthetic.main.layout_dialog_brandfilter.*

class SearchGoodsListActivity : BaseActivity(), SearchGoodsListView, OnRefreshListener {

    private var mListAdapter: StoreGridListAdapter? = null
    private var mCompositeFiltrateWindow: BasePopupWindow? = null
    private var mBrandFilterWindow: BasePopupWindow? = null
    private var mSaleFilterWindow: BasePopupWindow? = null
    private var mBrands: ArrayList<ItemsBean>? = null
    private var mTypeId = 0 //类别id 0 全部
    private var mCurrentSort = 1 //	排序方式：1综合 2价格升 3价格降 4 销量降 5 销量升
    private var mCurrentBrandId = 0 //0 全部 品牌
    private var mPresenter: SearchGoodsListPresenter? = null
    override fun getLayout() = R.layout.activity_searchgoods
    override fun initView() {
        mPresenter = SearchGoodsListPresenter(this)
        ivBack.setOnClickListener { onBackPressed() }
        tvCompositeFiltrate.setOnClickListener { FiltrateController(0) }
        tvSalesFiltrate.setOnClickListener { FiltrateController(1) }
        tvBrandFiltrate.setOnClickListener { FiltrateController(2) }
        mListAdapter = StoreGridListAdapter(this)
        mRefreshLayout.apply {
            setLayoutManager(GridLayoutManager(this@SearchGoodsListActivity, 2))
            setAdapter(mListAdapter!!)
            addItemDecoration(object : RecyclerView.ItemDecoration() {
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
                    outRect.top = space * 2
                    if (parent.getChildAdapterPosition(view) % 2 == 0) {
                        outRect.right = space
                    } else {
                        outRect.left = space
                    }
                }
            })
            //            setEmptyView(emptyView)
            setOnRefreshListener(this@SearchGoodsListActivity)
        }
        if (intent.hasExtra("keyword")) {
            mTypeId = intent.getIntExtra("id", 0)
            searchTagLayout.visibility = View.VISIBLE
            tvSearchKeyWord.text = intent.getStringExtra("keyword")
            etInputSearch.visibility = View.GONE
        } else {
            searchTagLayout.visibility = View.GONE
            etInputSearch.visibility = View.VISIBLE
        }
        ivRemoveKeyWord.setOnClickListener {
            mTypeId = 0
            searchTagLayout.visibility = View.GONE
            etInputSearch.visibility = View.VISIBLE
            mRefreshLayout.autoRefresh()
        }
        etInputSearch.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                mRefreshLayout.autoRefresh()
            }
            return@setOnEditorActionListener true
        }
        mRefreshLayout.autoRefresh()

    }

    override fun onBackPressed() {
        supportFinishAfterTransition()
    }

    private fun FiltrateController(index: Int) {
        when (index) {
            0 -> {
                mSaleFilterWindow?.clear()
                showCompositeFilter()
                if (tvCompositeFiltrate.isSelected)
                    return
                tvCompositeFiltrate.isSelected = true
                tvSalesFiltrate.isSelected = false
                tvBrandFiltrate.isSelected = false
            }
            1 -> {
                mCompositeFiltrateWindow?.clear()
                showSalesFilter()
                if (tvSalesFiltrate.isSelected)
                    return
                tvCompositeFiltrate.isSelected = false
                tvSalesFiltrate.isSelected = true
                tvBrandFiltrate.isSelected = false
            }
            2 -> {
                showBrandFilter()
                if (tvBrandFiltrate.isSelected)
                    return
                tvCompositeFiltrate.isSelected = false
                tvSalesFiltrate.isSelected = false
                tvBrandFiltrate.isSelected = true
            }
        }

    }

    //综合筛选
    private fun showCompositeFilter() {
        if (mCompositeFiltrateWindow == null) {
            mCompositeFiltrateWindow =
                object : BasePopupWindow(this, R.layout.layout_dialog_brandfilter) {
                    private var mListAdapter:TextCheckListAdapter?=null
                    override fun initView(content: View) {
                        content.findViewById<View>(R.id.vBottom)
                                .setOnClickListener { dismiss() }
                        var rvBrands = content.findViewById<RecyclerView>(R.id.rvBrands)
                        rvBrands.layoutManager = LinearLayoutManager(this@SearchGoodsListActivity)
                        var list = arrayListOf<String>("综合", "价格升序", "价格降序")
                        mListAdapter = TextCheckListAdapter(this@SearchGoodsListActivity, list, object : MCallBack<String> {
                            override fun call(t: String, position: Int) {
                                mCurrentSort = position + 1
                                mRefreshLayout.autoRefresh()
                                dismiss()
                            }
                        })
                        rvBrands.adapter=mListAdapter

                    }
                    override fun clear(){
                        mListAdapter?.clear()
                    }
                }
        }
        if (mCompositeFiltrateWindow?.isShowing == false) {
            mCompositeFiltrateWindow?.showAsDropDown(rlFiltrates)
        }
    }

    //销量
    private fun showSalesFilter() {
        if (mSaleFilterWindow == null) {
            mSaleFilterWindow = object : BasePopupWindow(this, R.layout.layout_dialog_brandfilter) {
                private var mListAdapter:TextCheckListAdapter?=null
                override fun initView(content: View) {
                    content.findViewById<View>(R.id.vBottom)
                            .setOnClickListener { dismiss() }
                    var rvBrands = content.findViewById<RecyclerView>(R.id.rvBrands)
                    rvBrands.layoutManager = LinearLayoutManager(this@SearchGoodsListActivity)
                    var list = arrayListOf<String>("销量由高到低", "销量由低到高")
                    mListAdapter=TextCheckListAdapter(this@SearchGoodsListActivity, list, object : MCallBack<String> {
                        override fun call(t: String, position: Int) {
                            if (position == 0) mCurrentSort = 4 else 5
                            dismiss()
                            mRefreshLayout.autoRefresh()
                        }
                    })
                    rvBrands.adapter=mListAdapter
                }
                override fun clear(){
                    mListAdapter?.clear()
                }

            }
        }
        if (mSaleFilterWindow?.isShowing == false)
            mSaleFilterWindow?.showAsDropDown(rlFiltrates)
    }

    private fun showBrandFilter() {
        if (mBrandFilterWindow == null) {
            mBrandFilterWindow = object : BasePopupWindow(this, R.layout.layout_dialog_brandfilter) {
                private var mListAdapter: TextCheckListAdapter? = null
                override fun initView(content: View) {
                    content.findViewById<View>(R.id.vBottom)
                            .setOnClickListener { dismiss() }
                    var rvBrands = content.findViewById<RecyclerView>(R.id.rvBrands)
                    rvBrands.layoutManager = LinearLayoutManager(this@SearchGoodsListActivity)
                    var list = arrayListOf<String>()
                    mBrands?.forEach {
                        list.add(it.title ?: "")
                    }
                    mListAdapter = TextCheckListAdapter(this@SearchGoodsListActivity, list, object : MCallBack<String> {
                        override fun call(t: String, position: Int) {
                            mCurrentBrandId = mBrands!![position].id
                            dismiss()
                            mRefreshLayout.autoRefresh()
                        }
                    })
                    rvBrands.adapter = mListAdapter
                }

                override fun clear() {
                    mListAdapter?.clear()
                }

            }
        }
        if (mBrandFilterWindow?.isShowing == false)
            mBrandFilterWindow?.showAsDropDown(rlFiltrates)
    }

    override fun onError(err: String?) {
        toast(err ?: "")
    }

    override fun onRefresh(refreshLayout: RefreshLayout) {
        mPresenter?.getGoodsList(mTypeId.toString(), mCurrentSort, mCurrentBrandId.toString(), etInputSearch.text.toString())
    }

    override fun onGetGoodsListSuccess(brands: ArrayList<ItemsBean>?, goodsList: ArrayList<Goods>?) {
        mBrands = brands
        mListAdapter?.setData(goodsList)
    }

    override fun onFinishNet(isMore: Boolean) {
        mRefreshLayout.finishRefresh()
    }
}
