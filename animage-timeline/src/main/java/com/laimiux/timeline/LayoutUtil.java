package com.laimiux.timeline;

import android.util.Log;
import android.view.View;
import android.widget.ListAdapter;
import android.widget.ListView;

import it.sephiroth.android.library.widget.HListView;

public class LayoutUtil {

    public static int getListHeight(ListView listView, int initialPosition, int endPosition) {

        ListAdapter mAdapter = listView.getAdapter();

        int totalHeight = 0;

        for (int i = initialPosition; i < endPosition; i++) {
            View mView = mAdapter.getView(i, null, listView);

            mView.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));

            totalHeight += mView.getMeasuredHeight();
        }

        return totalHeight;
    }

    public static View getViewAtPosition(HListView listView, int wantedPosition) {
        int firstPosition = listView.getFirstVisiblePosition(); // - listView.getHeaderViewsCount(); // This is the same as child #0
        int wantedChild = wantedPosition - firstPosition;

        // Say, first visible position is 8, you want position 10, wantedChild will now be 2
        // So that means your view is child #2 in the ViewGroup:
        if (wantedChild < 0 || wantedChild >= listView.getChildCount()) {
            Log.w("LayoutUtil", "Unable to get view for desired position, because it's not being displayed on screen.");
            return null;
        }

        // Could also check if wantedPosition is between listView.getFirstVisiblePosition()
        // and listView.getLastVisiblePosition() instead.
        return listView.getChildAt(wantedChild);
    }
}
