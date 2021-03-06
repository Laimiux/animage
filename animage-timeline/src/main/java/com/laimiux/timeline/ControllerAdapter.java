package com.laimiux.timeline;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * An implementation of {@link android.widget.BaseAdapter} which uses the new/bind pattern for its views.
 */
public abstract class ControllerAdapter<T extends ControllerItem> extends BaseAdapter {
    private final Context context;
    private final LayoutInflater inflater;

    final private List<T> items = new ArrayList<>();

    public ControllerAdapter(Context context) {
        this.context = context;
        this.inflater = LayoutInflater.from(context);
    }

    public void setItems(Collection<? extends T> newItems) {
        items.clear();
        items.addAll(newItems);
        notifyDataSetChanged();
    }

    public Context getContext() {
        return context;
    }

    @Override
    public int getCount() {
        return items.size(); // last one is TODAY
    }

    @Override
    public T getItem(int position) {
        return position < items.size() ? items.get(position) : null;
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

    /**
     * Create a new instance of a view for the specified position.
     */
    public abstract View newView(LayoutInflater inflater, int position, ViewGroup container);

    /**
     * Bind the data for the specified {@code position} to the view.
     */
    public abstract void bindView(T item, int position, View view);

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
    public void bindDropDownView(T item, int position, View view) {
        bindView(item, position, view);
    }
}

