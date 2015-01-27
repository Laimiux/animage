package com.laimiux.animage.local;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;

import java.io.File;

/**
 * Created by laimiux on 11/12/14.
 */
public class SquarePicassoImageView extends ImageView {
    private RequestCreator mImageRequest;

    public SquarePicassoImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
    }

    public void bindTo(Picasso picasso, File imageFile) {
        mImageRequest = picasso.load(imageFile);

        // Refresh the image
        requestLayout();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int mode = MeasureSpec.getMode(widthMeasureSpec);
        if (mode != MeasureSpec.EXACTLY) {
            throw new IllegalStateException("layout_width must be match_parent");
        }

        int width = MeasureSpec.getSize(widthMeasureSpec);

        // Image is a square
        int height = width;

        heightMeasureSpec = MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
//
        if (mImageRequest != null) {
            mImageRequest.resize(width, height).centerCrop().into(this);
            mImageRequest = null;
        }
    }
}
