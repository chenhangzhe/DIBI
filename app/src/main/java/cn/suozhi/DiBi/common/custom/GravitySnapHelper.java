package cn.suozhi.DiBi.common.custom;

import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;

import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.OrientationHelper;
import androidx.recyclerview.widget.RecyclerView;

/**
 * 辅助RecyclerView对齐
 */
public class GravitySnapHelper extends LinearSnapHelper {

    private int gravity;
    private boolean snapLastItem;
    private boolean snapping;
    private boolean isRtlHorizontal;
    private SnapListener listener;//回调不准确，不建议使用

    private OrientationHelper horizontalHelper;
    private OrientationHelper verticalHelper;

    private int offset = 0;//偏移量

    private RecyclerView.OnScrollListener mScrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
            if (newState == RecyclerView.SCROLL_STATE_SETTLING) {
                snapping = false;
            }
            if (newState == RecyclerView.SCROLL_STATE_IDLE && snapping && listener != null) {
                int position = getSnappedPosition(recyclerView);
                if (position != RecyclerView.NO_POSITION) {
                    listener.onSnap(position);
                }
                snapping = false;
            }
        }
    };

    public GravitySnapHelper(int gravity) {
        this(gravity, null);
    }

    public GravitySnapHelper(int gravity, SnapListener listener) {
        this(gravity, false, listener);
    }

    public GravitySnapHelper(int gravity, boolean snapLastItem, SnapListener listener) {
        if (gravity != Gravity.START && gravity != Gravity.END
                && gravity != Gravity.BOTTOM && gravity != Gravity.TOP) {
            throw new IllegalArgumentException("Invalid gravity value. Use START " +
                    "| END | BOTTOM | TOP constants");
        }
        this.gravity = gravity;
        this.snapLastItem = snapLastItem;
        this.listener = listener;
    }

    public GravitySnapHelper setOffset(int px) {
        offset = px;
        return this;
    }

    @Override
    public void attachToRecyclerView(@Nullable RecyclerView recyclerView) throws IllegalStateException {
        if (recyclerView != null) {
            recyclerView.setOnFlingListener(null);
            if ((gravity == Gravity.START || gravity == Gravity.END)) {
                isRtlHorizontal = isRtl();
            }
            if (listener != null) {
                recyclerView.addOnScrollListener(mScrollListener);
            }
        }
        super.attachToRecyclerView(recyclerView);
    }

    private boolean isRtl() {
        /*if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) {
            return false;
        }*/
        return TextUtils.getLayoutDirectionFromLocale(Locale.getDefault())
                == View.LAYOUT_DIRECTION_RTL;
    }

    @Override
    public int[] calculateDistanceToFinalSnap(@NonNull RecyclerView.LayoutManager layoutManager, @NonNull View targetView) {
        int[] out = new int[2];
        if (layoutManager.canScrollHorizontally()) {
            if (gravity == Gravity.START) {
                out[0] = distanceToStart(targetView, getHorizontalHelper(layoutManager), false) - offset;
            } else { // END
                out[0] = distanceToEnd(targetView, getHorizontalHelper(layoutManager), false) - offset;
            }
        } else {
            out[0] = 0;
        }

        if (layoutManager.canScrollVertically()) {
            if (gravity == Gravity.TOP) {
                out[1] = distanceToStart(targetView, getVerticalHelper(layoutManager), false) - offset;
            } else { // BOTTOM
                out[1] = distanceToEnd(targetView, getVerticalHelper(layoutManager), false) - offset;
            }
        } else {
            out[1] = 0;
        }
        return out;
    }

    private int distanceToStart(View targetView, @NonNull OrientationHelper helper, boolean fromEnd) {
        if (isRtlHorizontal && !fromEnd) {
            return distanceToEnd(targetView, helper, true);
        }
        return helper.getDecoratedStart(targetView) - helper.getStartAfterPadding();
    }

    private int distanceToEnd(View targetView, @NonNull OrientationHelper helper, boolean fromStart) {
        if (isRtlHorizontal && !fromStart) {
            return distanceToStart(targetView, helper, true);
        }
        return helper.getDecoratedEnd(targetView) - helper.getEndAfterPadding();
    }

    private OrientationHelper getHorizontalHelper(RecyclerView.LayoutManager layoutManager) {
        if (horizontalHelper == null) {
            horizontalHelper = OrientationHelper.createHorizontalHelper(layoutManager);
        }
        return horizontalHelper;
    }

    private OrientationHelper getVerticalHelper(RecyclerView.LayoutManager layoutManager) {
        if (verticalHelper == null) {
            verticalHelper = OrientationHelper.createVerticalHelper(layoutManager);
        }
        return verticalHelper;
    }

    @Override
    public View findSnapView(RecyclerView.LayoutManager layoutManager) {
        View snapView = null;
        if (layoutManager instanceof LinearLayoutManager) {
            switch (gravity) {
                case Gravity.START:
                    snapView = findStartView(layoutManager, getHorizontalHelper(layoutManager));
                    break;
                case Gravity.END:
                    snapView = findEndView(layoutManager, getHorizontalHelper(layoutManager));
                    break;
                case Gravity.TOP:
                    snapView = findStartView(layoutManager, getVerticalHelper(layoutManager));
                    break;
                case Gravity.BOTTOM:
                    snapView = findEndView(layoutManager, getVerticalHelper(layoutManager));
                    break;
            }
        }
        snapping = snapView != null;
        return snapView;
    }

    private View findStartView(RecyclerView.LayoutManager layoutManager,
                               @NonNull OrientationHelper helper) {

        if (layoutManager instanceof LinearLayoutManager) {
            LinearLayoutManager linearLayoutManager = (LinearLayoutManager) layoutManager;
            boolean reverseLayout = linearLayoutManager.getReverseLayout();
            int firstChild = reverseLayout ? linearLayoutManager.findLastVisibleItemPosition()
                    : linearLayoutManager.findFirstVisibleItemPosition();
            int offset = 1;

            if (layoutManager instanceof GridLayoutManager) {
                offset += ((GridLayoutManager) layoutManager).getSpanCount() - 1;
            }

            if (firstChild == RecyclerView.NO_POSITION) {
                return null;
            }

            View child = layoutManager.findViewByPosition(firstChild);

            float visibleWidth;

            // We should return the child if it's visible width
            // is greater than 0.5 of it's total width.
            // In a RTL configuration, we need to check the start point and in LTR the end point
            if (isRtlHorizontal) {
                visibleWidth = (float) (helper.getTotalSpace() - helper.getDecoratedStart(child))
                        / helper.getDecoratedMeasurement(child);
            } else {
                visibleWidth = (float) helper.getDecoratedEnd(child)
                        / helper.getDecoratedMeasurement(child);
            }

            // If we're at the end of the list, we shouldn't snap
            // to avoid having the last item not completely visible.
            boolean endOfList;
            if (!reverseLayout) {
                endOfList = ((LinearLayoutManager) layoutManager)
                        .findLastCompletelyVisibleItemPosition()
                        == layoutManager.getItemCount() - 1;
            } else {
                endOfList = ((LinearLayoutManager) layoutManager)
                        .findFirstCompletelyVisibleItemPosition()
                        == 0;
            }

            if (visibleWidth > 0.5f && !endOfList) {
                return child;
            } else if (snapLastItem && endOfList) {
                return child;
            } else if (endOfList) {
                return null;
            } else {
                // If the child wasn't returned, we need to return
                // the next view close to the start.
                return reverseLayout ? layoutManager.findViewByPosition(firstChild - offset)
                        : layoutManager.findViewByPosition(firstChild + offset);
            }
        }

        return null;
    }

    @Nullable
    private View findEndView(RecyclerView.LayoutManager layoutManager,
                             @NonNull OrientationHelper helper) {

        if (layoutManager instanceof LinearLayoutManager) {
            LinearLayoutManager linearLayoutManager = (LinearLayoutManager) layoutManager;
            boolean reverseLayout = linearLayoutManager.getReverseLayout();
            int lastChild = reverseLayout ? linearLayoutManager.findFirstVisibleItemPosition()
                    : linearLayoutManager.findLastVisibleItemPosition();
            int offset = 1;

            if (layoutManager instanceof GridLayoutManager) {
                offset += ((GridLayoutManager) layoutManager).getSpanCount() - 1;
            }

            if (lastChild == RecyclerView.NO_POSITION) {
                return null;
            }

            View child = layoutManager.findViewByPosition(lastChild);

            float visibleWidth;

            if (isRtlHorizontal) {
                visibleWidth = (float) helper.getDecoratedEnd(child)
                        / helper.getDecoratedMeasurement(child);
            } else {
                visibleWidth = (float) (helper.getTotalSpace() - helper.getDecoratedStart(child))
                        / helper.getDecoratedMeasurement(child);
            }

            // If we're at the start of the list, we shouldn't snap
            // to avoid having the first item not completely visible.
            boolean startOfList;
            if (!reverseLayout) {
                startOfList = ((LinearLayoutManager) layoutManager)
                        .findFirstCompletelyVisibleItemPosition() == 0;
            } else {
                startOfList = ((LinearLayoutManager) layoutManager)
                        .findLastCompletelyVisibleItemPosition()
                        == layoutManager.getItemCount() - 1;
            }

            if (visibleWidth > 0.5f && !startOfList) {
                return child;
            } else if (snapLastItem && startOfList) {
                return child;
            } else if (startOfList) {
                return null;
            } else {
                // If the child wasn't returned, we need to return the previous view
                return reverseLayout ? layoutManager.findViewByPosition(lastChild + offset)
                        : layoutManager.findViewByPosition(lastChild - offset);
            }
        }
        return null;
    }

    private int getSnappedPosition(@NonNull RecyclerView recyclerView) {
        RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
        if (layoutManager instanceof LinearLayoutManager) {
            if (gravity == Gravity.START || gravity == Gravity.TOP) {
                return ((LinearLayoutManager) layoutManager).findFirstCompletelyVisibleItemPosition();
            } else if (gravity == Gravity.END || gravity == Gravity.BOTTOM) {
                return ((LinearLayoutManager) layoutManager).findLastCompletelyVisibleItemPosition();
            }
        }
        return RecyclerView.NO_POSITION;
    }

    public void enableLastItemSnap(boolean snap) {
        snapLastItem = snap;
    }

    public interface SnapListener {
        void onSnap(int position);
    }
}
