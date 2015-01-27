package com.laimiux.timelineexample;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.laimiux.timeline.ControllerAdapter;
import com.laimiux.timeline.ControllerItem;

import java.text.DateFormat;
import java.text.SimpleDateFormat;


public class TimelineControllerAdapter extends ControllerAdapter<ControllerItem> {
    private DateFormat mMonthFormat;

    public TimelineControllerAdapter(Context context) {
        super(context);

        mMonthFormat = new SimpleDateFormat("MMM");
    }

    @Override
    public int getCount() {
        return super.getCount() + 1; // last one is TODAY
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View newView(LayoutInflater inflater, int position, ViewGroup container) {
        return inflater.inflate(R.layout.timeline_controller_item, container, false);
    }

    @Override
    public void bindView(ControllerItem item, int position, View view) {
        TextView textView = (TextView) view.findViewById(R.id.text1);

        ControllerItem controllerItem = getItem(position);
        if (controllerItem == null) {
            textView.setText("Today");
        } else {
            textView.setText(mMonthFormat.format(controllerItem.getDate()));
        }
    }
}

