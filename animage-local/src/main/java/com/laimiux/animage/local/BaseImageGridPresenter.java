package com.laimiux.animage.local;

import android.content.Context;
import android.os.Build;
import android.view.ViewTreeObserver;
import android.widget.AbsListView;
import android.widget.BaseAdapter;

import java.io.File;
import java.util.List;

public abstract class BaseImageGridPresenter {

    public void show(final AbsListView view, final int columnCount) {
        view.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                // Remove global layout listener
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    view.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                } else {
                    view.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                }


                final Context context = view.getContext();
                BaseAdapter adapter = createAdapter(context, LocalGalleryUtil.getGalleryImages(context), view.getWidth() / columnCount);
                view.setAdapter(adapter);
            }
        });


    }


    abstract public BaseAdapter createAdapter(Context context, List<? extends File> imageFiles, int itemWidth);

}
