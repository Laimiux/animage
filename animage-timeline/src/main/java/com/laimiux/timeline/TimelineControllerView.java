package com.laimiux.timeline;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.laimiux.timeline.internal.Constants;

import it.sephiroth.android.library.widget.AbsHListView;
import it.sephiroth.android.library.widget.HListView;

// TODO should we still use HListView ?
public class TimelineControllerView extends HListView {
    private final static String TAG = TimelineControllerView.class.getSimpleName();

    // Controller footer views
    final private View mControllerLeftFillerView;
    final private View mControllerRightFillerView;

    final private int itemWidth;
    private int mLastWidth;

    public TimelineControllerView(Context context, AttributeSet attrs) {
        super(context, attrs);

        // Get item width
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.TimelineControllerView);
        itemWidth = typedArray.getDimensionPixelSize(R.styleable.TimelineControllerView_item_width, -1);
        typedArray.recycle();

        if(itemWidth == -1) {
            throw new IllegalStateException("item_width is required");
        }

        mControllerLeftFillerView = new View(getContext());
        mControllerRightFillerView = new View(getContext());

        addHeaderView(mControllerLeftFillerView);
        addFooterView(mControllerRightFillerView);

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        final int measuredWidth = getMeasuredWidth();
        if (mLastWidth != measuredWidth) {
            updateControllerFillerViewSize(measuredWidth);
        }
    }


    private void updateControllerFillerViewSize(int width) {
        if (Constants.DEBUG) {
            Log.d(TAG, "updatedControllerFillerViewSize() -> width " + width);
        }

        mLastWidth = width;
        setControllerFillerWidth(mControllerLeftFillerView, width);
        setControllerFillerWidth(mControllerRightFillerView, width);
    }

    private void setControllerFillerWidth(View view, int viewWidth) {
        int width = viewWidth / 2 - itemWidth / 2;
        AbsHListView.LayoutParams params = new AbsHListView.LayoutParams(width, 1);
        view.setLayoutParams(params);
    }
}
