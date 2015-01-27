package com.laimiux.animage.gridview;

import android.animation.Animator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.support.v8.renderscript.RSRuntimeException;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.laimiux.imagegridview.R;
import com.squareup.picasso.Callback;

public class ExpandableImageGridItemView extends ImageGridItemView {
    // Children views
    private ImageView mBlurView;
    private TextView mCountView;

    // Count
    private int mCount;

    public ExpandableImageGridItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override protected void onFinishInflate() {
        super.onFinishInflate();
        mBlurView = (ImageView) findViewById(R.id.grid_image_blur_view);
        mCountView = (TextView) findViewById(R.id.image_count);
    }

    public void setCount(int count) {
        mCount = count;
        mCountView.setText(String.valueOf(count));
    }

    public void hideBlurView() {
        mCountView.animate().alpha(0).setDuration(300).setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                mCountView.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        }).start();

        mBlurView.animate().alpha(0).setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                mBlurView.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        }).setDuration(300).start();
    }

    @Override
    protected Callback getCallback() {
        final Callback callback = super.getCallback();
        return new Callback() {
            @Override
            public void onSuccess() {
                showBlurView();
                callback.onSuccess();
            }

            @Override
            public void onError() {
                callback.onError();
            }
        };
    }

    public void showBlurView() {
//        if(true) return;

        int sizeDivider = 2;


        final BitmapDrawable drawable = (BitmapDrawable) gridImageView.getDrawable();
        Bitmap cachedBitmap = drawable.getBitmap();

        if (cachedBitmap != null) {
            // Scale the bitmap down
            Bitmap scaledBitmap = Bitmap.createScaledBitmap(cachedBitmap,
                    cachedBitmap.getWidth() / sizeDivider,
                    cachedBitmap.getHeight() / sizeDivider,
                    true);

            Bitmap blurred;
            try {
                blurred = BitmapUtil.renderScriptBlur(getContext(), scaledBitmap, 2);
            } catch (RSRuntimeException exception) {
                blurred = null;
            }

            // Set blur view
            mBlurView.setImageBitmap(blurred);
            mBlurView.setVisibility(View.VISIBLE);
        }

        // Image count
        mCountView.setVisibility(View.VISIBLE);

    }
}
