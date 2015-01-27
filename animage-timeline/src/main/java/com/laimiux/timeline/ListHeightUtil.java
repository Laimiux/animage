package com.laimiux.timeline;

import android.util.SparseIntArray;
import android.widget.ListAdapter;
import android.widget.ListView;

import java.lang.ref.WeakReference;

public class ListHeightUtil {
    private final WeakReference<ListView> mListViewRef;

    // From current item pos, height to the next item.
    private SparseIntArray mHeightCache;

    public ListHeightUtil(ListView listView) {
        mListViewRef = new WeakReference<>(listView);
        mHeightCache = new SparseIntArray();

        calculateHeights(listView);
    }

    private void calculateHeights(ListView listView) {
        ListAdapter adapter = listView.getAdapter();
        // Why getCount() - 1
        for(int i = 0; i < adapter.getCount() - 1; i++) {
            int height = LayoutUtil.getListHeight(listView, i, i + 1);
            mHeightCache.put(i, height);
        }
    }

    public void recalculateHeights() {
        // Remove old heights
        mHeightCache.clear();

        ListView listView = mListViewRef.get();
        if(listView != null) {
            calculateHeights(listView);
        }
    }

    public int getHeight(int startPos, int endPos) {
        int height = 0;
        for(int i = startPos; i < endPos; i++) {

            int heightAtI = mHeightCache.get(i, -1);
            if(heightAtI == -1) {
                if(mListViewRef.get() != null) {
                    heightAtI = LayoutUtil.getListHeight(mListViewRef.get(), i, i + 1);
                    mHeightCache.put(i, heightAtI);
                }
            }

            height += heightAtI;
        }

        return height;
    }

    public void updateHeight(int position, int expandedHeight) {
        mHeightCache.put(position, expandedHeight);
    }
}
