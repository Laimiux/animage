package com.laimiux.animage.local;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.List;

public class SquarePicassoImageAdapter extends BindableAdapter<File> {
    private List<File> mImages;

    private Picasso mPicasso;

    public SquarePicassoImageAdapter(Context context, Picasso picasso, List<File> imageFiles) {
        super(context);
        mPicasso = picasso;
        mImages = imageFiles;
    }

    @Override
    public int getCount() {
        return mImages.size();
    }

    @Override
    public File getItem(int position) {
        return mImages.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View newView(LayoutInflater inflater, int position, ViewGroup container) {
        return inflater.inflate(R.layout.square_image_view, container, false);
    }

    @Override
    public void bindView(File item, int position, View view) {
        ((SquarePicassoImageView) view).bindTo(mPicasso, item);
    }
}
