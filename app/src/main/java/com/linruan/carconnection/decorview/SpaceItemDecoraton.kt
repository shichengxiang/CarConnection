package com.linruan.carconnection.decorview

import android.graphics.Rect
import android.view.View
import androidx.core.view.get
import androidx.recyclerview.widget.RecyclerView

open class SpaceItemDecoraton : RecyclerView.ItemDecoration {

    var left = 0
    var top = 0
    var right = 0
    var bottom = 0
    var o = 1
    var hasFirst = true

    constructor(left: Int, top: Int, right: Int, bottom: Int, oritation: Int) {
        this.left = left
        this.top = top
        this.right = right
        this.bottom = bottom
        this.o = oritation
    }

    constructor(left: Int, top: Int, right: Int, bottom: Int, oritation: Int, hasFirst: Boolean) {
        this.left = left
        this.top = top
        this.right = right
        this.bottom = bottom
        this.o = oritation
        this.hasFirst = hasFirst
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
        if (o == RecyclerView.HORIZONTAL) {
            if (parent.getChildAdapterPosition(view) == 0) {
                outRect.left = 0
            }
        } else {
            if (parent.getChildAdapterPosition(view) == 0 && !hasFirst) {
                outRect.top = 0
            }
        }
    }
}