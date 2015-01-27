package com.laimiux.animage.gridview;

import android.content.Context;
import android.content.res.TypedArray;
import android.net.Uri;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.laimiux.animage.core.Image;
import com.laimiux.imagegridview.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ImageGridView extends FrameLayout {
    private static final String TAG = ImageGridView.class.getSimpleName();
    public static final boolean DEBUG = false;

    public static final int MAX_IMAGES_IN_COLLAPSED_GRID = 9;

    final private int columnCount = 3;

    private int mPaddingBetweenImages;
    private int mMaxImageHeight;

    // When more than 9 images, it can be expanded
    private boolean mIsExpandable;

    // If the view is expanded.
    private boolean mIsExpanded;

    private List<? extends Image> mImages;

    private OnImageClickListener mOnImageClickListener;
    private OnImageGridExpandListener mOnImageGridExpandListener;

    private int mCollapsedHeight;
    private int mExpandedHeight;

    public ImageGridView(Context context) {
        super(context);
    }

    public ImageGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public ImageGridView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.ImageGridView);
        if (typedArray != null) {
            mMaxImageHeight = typedArray.getDimensionPixelSize(R.styleable.ImageGridView_maxImageHeight, -1);
            mPaddingBetweenImages = typedArray.getDimensionPixelSize(R.styleable.ImageGridView_paddingBetweenImages, 0);

            typedArray.recycle();
        }
    }

    public void setImages(List<? extends Image> images, Picasso picasso) {
        mImages = images;

        final int imageCount = images.size();
        mIsExpandable = imageCount > MAX_IMAGES_IN_COLLAPSED_GRID;

        final int childCount = getChildCount();
        for (int i = 0; i < imageCount; i++) {
            final int position = i;
            Image image = images.get(i);

            boolean shouldBeAdded = false;
            final ImageGridItemView gridItem;

            // Reuse view
            if (i <= childCount - 1) {
                gridItem = (ImageGridItemView) getChildAt(position);
            } else {
                gridItem = createGridItem(position, LayoutInflater.from(getContext()));
                shouldBeAdded = true;
            }

            final boolean isSingleImage = imageCount == 1;
            Uri uriToLoad = isSingleImage ? image.getImageUri() : image.getThumbnailUri();


            gridItem.bindTo(picasso, uriToLoad);

            if (gridItem instanceof ExpandableImageGridItemView) {
                final ExpandableImageGridItemView expandableGridItem = (ExpandableImageGridItemView) gridItem;
                expandableGridItem.setCount(imageCount - MAX_IMAGES_IN_COLLAPSED_GRID);

                gridItem.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mOnImageGridExpandListener != null) {
                            mOnImageGridExpandListener.onImageGridExpand(mExpandedHeight);
                        }

                        expandableGridItem.hideBlurView();
                        // Update click listener
                        setImageItemClickListener(expandableGridItem, position);

                        mIsExpanded = true;
                        requestLayout();
                    }
                });
            } else {
                setImageItemClickListener(gridItem, position);
            }

            if (shouldBeAdded) {
                addView(gridItem);
            }
        }

        // Remove not used views
        for (int k = childCount - 1; k >= imageCount; k--) {
            removeViewAt(k);
        }

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int mode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);

        if (heightMode != MeasureSpec.UNSPECIFIED) {
            if (DEBUG) {
                printOnMeasureMode(mode, "ImageGridView.onMeasure() -> mode is");
                printOnMeasureMode(heightMode, "ImageGridView.onMeasure() -> height mode is");
            }

            throw new IllegalStateException("layout_height should be specified wrap_content");
        }


        int parentWidth = MeasureSpec.getSize(widthMeasureSpec);

        // Part of hack to measure list height
        if (parentWidth == 0) {
            parentWidth = getLayoutParams().width;
        }

        int measuredHeight = 0;

        int imageSize = parentWidth / columnCount;

        // Adjust size for spacing in between images
        imageSize -= (mPaddingBetweenImages * (columnCount - 1));

        int childCount = getChildCount();

        for (int i = 0; i < childCount; i++) {
            View child = getChildAt(i);

            FrameLayout.LayoutParams params = (LayoutParams) child.getLayoutParams();
            // Set specific layout params
            if (childCount == 1) {
                params.width = ViewGroup.LayoutParams.MATCH_PARENT;
                if (mMaxImageHeight == -1) {
                    params.height = ViewGroup.LayoutParams.MATCH_PARENT;
                } else {
                    params.height = mMaxImageHeight;
                }

                measuredHeight = params.height;
            } else {
                params.width = imageSize;
                params.height = imageSize;

                int divider;
                if (childCount == 4) {
                    divider = 2;
                } else {
                    divider = 3;
                }

                int sideMargin = (i % divider) * imageSize;

                // Add side padding between images
                int columnPosition = i % divider;
                boolean notFirstInRow = columnPosition != 0;
                if (notFirstInRow) {
                    sideMargin += columnPosition * mPaddingBetweenImages;
                }

                // Add top padding between images
                int marginTop = (i / divider) * imageSize;

                // Starts at 0
                int row = i / divider;

                if (i >= divider) {
                    marginTop += row * mPaddingBetweenImages;
                }

                params.topMargin = marginTop;
                params.leftMargin = sideMargin;
            }
        }

        if (measuredHeight == 0) {
            if (childCount == 0) {
                // Collapse completely if no children
                measuredHeight = 0;
            } else {
                int divider = childCount / 3;
                int remainder = childCount % 3;

                int rows = divider;
                // Add row if there is overflow;
                if (remainder > 0) {
                    rows += 1;
                }

                mExpandedHeight = rows * imageSize + (rows - 1) * mPaddingBetweenImages;

                if (mIsExpandable) {
                    rows = rows > 3 ? 3 : rows;
                    mCollapsedHeight = rows * imageSize + (rows - 1) * mPaddingBetweenImages;

                    if (!mIsExpanded) {
                        measuredHeight = mCollapsedHeight;
                    } else {
                        measuredHeight = mExpandedHeight;
                    }
                } else {
                    measuredHeight = mExpandedHeight;
                }
            }
        }

        int widthSpec = MeasureSpec.makeMeasureSpec(parentWidth, mode);
        int heightSpec = MeasureSpec.makeMeasureSpec(measuredHeight, MeasureSpec.EXACTLY);


        super.onMeasure(widthSpec, heightSpec);
    }

    protected ImageGridItemView createGridItem(final int position, LayoutInflater inflater) {
        final ImageGridItemView gridImageViewContainer;

        if (position == 8 && mIsExpandable && !mIsExpanded) {
            gridImageViewContainer = (ImageGridItemView) inflater.inflate(R.layout.expandable_image_grid_view_item, this, false);
        } else {
            gridImageViewContainer = (ImageGridItemView) inflater.inflate(R.layout.image_grid_view_item, this, false);
        }

        return gridImageViewContainer;
    }

    private void setImageItemClickListener(ViewGroup gridImageViewContainer, final int position) {
        gridImageViewContainer.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnImageClickListener != null) {
                    int[] location = new int[2];
                    v.getLocationOnScreen(location);

                    int statusBarHeight = getStatusBarHeight(getContext());

                    if (DEBUG) {
                        Log.d(TAG, "X -> " + location[0] + ", Y -> " + location[1]);
                        Log.d(TAG, "Width -> " + v.getWidth() + ", height -> " + v.getHeight());
                    }

                    mOnImageClickListener.onImageClick(location[0],
                            location[1] - statusBarHeight,
                            v.getWidth(),
                            v.getHeight(),
                            mImages.get(position));
                }
            }
        });
    }

    public void setOnImageGridExpandListener(OnImageGridExpandListener onImageGridExpandListener) {
        mOnImageGridExpandListener = onImageGridExpandListener;
    }

    public void setOnImageClickListener(OnImageClickListener onImageClickListener) {
        mOnImageClickListener = onImageClickListener;
    }

    public void setExpanded(boolean expanded) {
        mIsExpanded = expanded;
    }


    public interface OnImageGridExpandListener {
        public void onImageGridExpand(int expandedHeight);
    }

    public interface OnImageClickListener {
        public void onImageClick(int left, int top, int width, int height, Image image);
    }

    private static int getStatusBarHeight(Context context) {
        int result = 0;
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    private static void printOnMeasureMode(int mode, String tag) {
        if (DEBUG) {
            String modeString;
            if (mode == View.MeasureSpec.UNSPECIFIED) {
                modeString = "unspecified";
            } else if (mode == View.MeasureSpec.AT_MOST) {
                modeString = "at-most";
            } else {
                modeString = "exactly";
            }

            Log.d(TAG, tag + " " + modeString);
        }
    }

}
