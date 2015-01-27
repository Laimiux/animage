package com.laimiux.timelineexample;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.laimiux.animage.core.Images;
import com.laimiux.animage.core.SmartImage;
import com.laimiux.animage.gridview.ImageGridView;
import com.laimiux.timeline.TimelineHeader;
import com.laimiux.timeline.TimelineListAdapter;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class TimelineAdapter extends TimelineListAdapter {
    final private DatePresenter mDatePresenter = new DatePresenter();

    // Parent width;
    private int mParentWidth = 0;

    public TimelineAdapter(Context context) {
        super(context);
    }

    @Override
    public View newView(LayoutInflater inflater, int position, ViewGroup container) {
        // HACK to help measure list height
        mParentWidth = container.getWidth();

        if (getItemViewType(position) == HEADER_TYPE) {
            return newHeaderView(inflater, position, container);
        } else {
            return newImageGridView(inflater, position, container);
        }
    }


    @Override
    public void bindView(Object item, int position, View view) {
        if (getItemViewType(position) == HEADER_TYPE) {
            bindHeaderView((TimelineHeader) item, position, view);
        } else {
            bindImageGridView((Images) item, position, view);
        }
    }


    private View newHeaderView(LayoutInflater inflater, int position, ViewGroup container) {
        return inflater.inflate(R.layout.timeline_header, container, false);
    }

    private void bindHeaderView(TimelineHeader item, int position, View view) {
        TextView mainTextView = (TextView) view.findViewById(R.id.timeline_header_main_text_view);
        TextView secondaryTextView = (TextView) view.findViewById(R.id.timeline_header_secondary_text_view);

        final Date date = item.getDate();
        String formattedDate = mDatePresenter.getTimeAgo(date);
        String dateString = formattedDate != null ? formattedDate : date.toString();

        mainTextView.setText(dateString);
        secondaryTextView.setVisibility(View.GONE);
    }

    private View newImageGridView(LayoutInflater inflater, int position, ViewGroup container) {
        return inflater.inflate(R.layout.timeline_item, container, false);
    }

    private void bindImageGridView(Images item, final int position, View view) {
        final ImageGridView imageGridView = (ImageGridView) view;

        // HACK to help measure list height
        imageGridView.getLayoutParams().width = mParentWidth;

        // Restore imageGridView to be expanded if its header is marked.
        final TimelineHeader header = (TimelineHeader) getItem(position - 1);

        final ArrayList<SmartImage> images = item.getImages();
//        imageGridView.setExpanded(mExpandedItems.contains(header));
        imageGridView.setImages(images, Picasso.with(getContext()));

    }

    private static class DatePresenter {
        private static final int SECOND_MILLIS = 1000;
        private static final int MINUTE_MILLIS = 60 * SECOND_MILLIS;
        private static final int HOUR_MILLIS = 60 * MINUTE_MILLIS;
        private static final int DAY_MILLIS = 24 * HOUR_MILLIS;

        final private DateFormat mDisplayDateFormat;


        public DatePresenter() {
            mDisplayDateFormat = DateFormat.getDateInstance();
        }

        public DatePresenter(String format) {
            mDisplayDateFormat = new SimpleDateFormat(format, Locale.getDefault());
        }

        public String getTimeAgo(Date date) {
            if (date == null) return null;

            long time = date.getTime();

            if (time < 1000000000000L) {
                // if timestamp given in seconds, convert to millis
                time *= 1000;
            }

            long now = System.currentTimeMillis();
            if (time > now || time <= 0) {
                return null;
            }

            // TODO: localize
            final long diff = now - time;
            if (diff < MINUTE_MILLIS) {
                return "just now";
            } else if (diff < 2 * MINUTE_MILLIS) {
                return "a minute ago";
            } else if (diff < 50 * MINUTE_MILLIS) {
                return diff / MINUTE_MILLIS + " minutes ago";
            } else if (diff < 90 * MINUTE_MILLIS) {
                return "an hour ago";
            } else if (diff < 24 * HOUR_MILLIS) {
                return diff / HOUR_MILLIS + " hours ago";
            } else if (diff < 48 * HOUR_MILLIS) {
                return "yesterday";
            } else {
                return mDisplayDateFormat.format(date);
            }
        }

    }
}

