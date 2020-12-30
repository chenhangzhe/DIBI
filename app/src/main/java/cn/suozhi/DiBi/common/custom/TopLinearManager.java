package cn.suozhi.DiBi.common.custom;

import android.content.Context;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSmoothScroller;
import androidx.recyclerview.widget.RecyclerView;

/**
 * 使RecyclerView滑动到指定位置并置顶
 */
public class TopLinearManager extends LinearLayoutManager {

    private int offset;

    public TopLinearManager(Context context) {
        this(context, 0);
    }

    public TopLinearManager(Context context, int offset) {
        super(context);
        this.offset = offset;
    }

    @Override
    public void scrollToPosition(int position) {
        scrollToPositionWithOffset(position, offset);
    }

    @Override
    public void smoothScrollToPosition(RecyclerView recyclerView, RecyclerView.State state, int position) {
        RecyclerView.SmoothScroller smoothScroller = new TopSmoothScroller(recyclerView.getContext());
        smoothScroller.setTargetPosition(position);
        startSmoothScroll(smoothScroller);
    }

    private class TopSmoothScroller extends LinearSmoothScroller {

        public TopSmoothScroller(Context context) {
            super(context);
        }

        @Override
        public int calculateDtToFit(int viewStart, int viewEnd, int boxStart, int boxEnd, int snapPreference) {
            return boxStart - viewStart + offset;
        }
    }
}
