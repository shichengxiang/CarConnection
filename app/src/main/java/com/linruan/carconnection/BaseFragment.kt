package com.linruan.carconnection

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment

abstract class BaseFragment : Fragment() {
    var mRoot: View? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
                             ): View? {
        mRoot = inflater.inflate(getLayout(), container, false)
        return mRoot
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initView(view, savedInstanceState)
        getData()
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
//        getData()
    }

    abstract fun getLayout(): Int
    abstract fun initView(view: View, savedInstanceState: Bundle?)
    abstract fun getData()
}