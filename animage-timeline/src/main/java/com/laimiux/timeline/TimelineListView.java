package com.laimiux.timeline;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ListView;

public class TimelineListView extends ListView {
    // Adjusts the bottom of the view.
    final private View mFillerFooter;

    public TimelineListView(Context context, AttributeSet attrs) {
        super(context, attrs);

        // Init filler footer view
        mFillerFooter = new View(getContext());
        addFooterView(mFillerFooter, null, false);
    }

    public void setFooterHeight(int footerHeight) {
        mFillerFooter.getLayoutParams().height = footerHeight;
    }
}
