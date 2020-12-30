package com.linruan.carconnection.decorview

import android.graphics.Rect
import android.view.View
import androidx.core.view.get
import androidx.recyclerview.widget.RecyclerView

open class GridSpaceItemDecoraton : RecyclerView.ItemDecoration {

    var left = 0
    var top = 0
    var right = 0
    var bottom = 0
    var spanCount=2

    constructor(left: Int, top: Int, right: Int, bottom: Int,spanCount:Int) {
        this.left = left
        this.top = top
        this.right = right
        this.bottom = bottom
        this.spanCount=spanCount
    }

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
                               ) {
        super.getItemOffsets(outRect, view, parent, state)
        outRect.left = this.left
        outRect.top = this.top
        outRect.right = this.right
        outRect.bottom = this.bottom
        if(parent.getChildAdapterPosition(view)%spanCount==0){
            outRect.left = 0
        }
    }
}