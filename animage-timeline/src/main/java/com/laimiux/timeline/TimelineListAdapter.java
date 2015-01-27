package com.laimiux.timeline;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;
import java.util.List;

abstract public class TimelineListAdapter extends BaseAdapter {
    public static final int HEADER_TYPE = 0;
    public static final int ITEM_TYPE = 1;

    protected final List<Object> items = new ArrayList<>();

    private final Context context;
    private final LayoutInflater inflater;

    public TimelineListAdapter(Context context) {
        this.context = context;
        this.inflater = LayoutInflater.from(context);
    }

    public TimelineListAdapter(Context context, List<Object> items) {
        this(context);
        this.items.addAll(items);
    }

    public void setItems(List<Object> newItems) {
        items.clear();
        items.addAll(newItems);
        notifyDataSetChanged();
    }

    public Context getContext() {
        return context;
    }


    @Override public int getCount() {
        return items.size();
    }

    @Override public Object getItem(int position) {
        return position < items.size() ? items.get(position) : null;
    }

    @Override public long getItemId(int position) {
        return position;
    }

    @Override
    public final View getView(int position, View view, ViewGroup container) {
        if (view == null) {
            view = newView(inflater, position, container);
            if (view == null) {
                throw new IllegalStateException("newView result must not be null.");
            }
        }
        bindView(getItem(position), position, view);
        return view;
    }

    @Override
    public int getItemViewType(int position) {
        // Even positions are headers
        if (position % 2 == 0) {
            return HEADER_TYPE;
        } else {
            // Odd positions are items
            return ITEM_TYPE;
        }
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    /**
     * Create a new instance of a view for the specified position.
     */
    public abstract View newView(LayoutInflater inflater, int position, ViewGroup container);

    /**
     * Bind the data for the specified {@code position} to the view.
     */
    public abstract void bindView(Object item, int position, View view);

    @Override
    public final View getDropDownView(int position, View view, ViewGroup container) {
        if (view == null) {
            view = newDropDownView(inflater, position, container);
            if (view == null) {
                throw new IllegalStateException("newDropDownView result must not be null.");
            }
        }
        bindDropDownView(getItem(position), position, view);
        return view;
    }

    /**
     * Create a new instance of a drop-down view for the specified position.
     */
    public View newDropDownView(LayoutInflater inflater, int position, ViewGroup container) {
        return newView(inflater, position, container);
    }

    /**
     * Bind the data for the specified {@code position} to the drop-down view.
     */
    public void bindDropDownView(Object item, int position, View view) {
        bindView(item, position, view);
    }

}
