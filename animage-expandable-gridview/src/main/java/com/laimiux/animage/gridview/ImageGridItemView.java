package com.laimiux.animage.gridview;

import android.content.Context;
import android.net.Uri;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.laimiux.imagegridview.R;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;


public class ImageGridItemView extends FrameLayout {
    private static final String TAG = "ImageGridItemView";

    protected ImageView gridImageView;
    private ProgressBar progressBar;

    // Request
    private RequestCreator request;

    public ImageGridItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        gridImageView = (ImageView) findViewById(R.id.grid_image_view);
    }

    public void bindTo(Picasso picasso, Uri imageUri) {
        // Indicate that view is loading
        if(progressBar == null) {
            progressBar = createLoader();
            addView(progressBar);
        } else {
            progressBar.bringToFront();
        }

        // Not enabled
        setEnabled(false);

        request = picasso.load(imageUri);

        requestLayout();
    }

    protected ProgressBar createLoader() {
        ProgressBar progressIndicator = new ProgressBar(getContext());

        int width = dpToPx(60);

        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(width, width);
        params.gravity = Gravity.CENTER;
        progressIndicator.setLayoutParams(params);

        return progressIndicator;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // Parent chose width and height for this picture, so let's honor it.
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        // If size not specified, let's not load image.
        if (width <= 0 || height <= 0) {
            if(ImageGridView.DEBUG) Log.w(TAG, "ImageGridItemView.onMeasure() -> width = " + width);
            return;
        }

        if (request != null) {
            if(ImageGridView.DEBUG) {
                Log.d(TAG, "ImageGridItemView.onMeasure() -> width = " + width);
                Log.d(TAG, "ImageGridItemView.onMeasure() -> height = " + height);
            }

            request.resize(width, height).centerCrop().into(gridImageView, getCallback());
            request = null;
        }
    }

    protected void removeLoader() {
        if(progressBar != null) {
            removeView(progressBar);
        }
    }

    private int dpToPx(int dp) {
        DisplayMetrics displayMetrics = getContext().getResources().getDisplayMetrics();
        int px = Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
        return px;
    }

    protected Callback getCallback() {
        return new Callback() {
            @Override
            public void onSuccess() {
                setEnabled(true);
                removeLoader();
            }

            @Override
            public void onError() {
                removeLoader();
            }
        };
    }


}
