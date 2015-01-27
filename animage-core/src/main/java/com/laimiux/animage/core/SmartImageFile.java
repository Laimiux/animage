package com.laimiux.animage.core;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import java.io.File;
import java.util.Date;


/**
 * Created by laimiux on 8/21/14.
 */
public class SmartImageFile extends File implements Parcelable, SmartImage {
    private long mDateTakenInMillis;
    private double mLatitude;
    private double mLongitude;

    private Date mDateTaken;

    public SmartImageFile(String path, long dateTakenInMillis, double latitude, double longitude) {
        super(path);
        mDateTakenInMillis = dateTakenInMillis;
        mLatitude = latitude;
        mLongitude = longitude;

        mDateTaken = new Date(mDateTakenInMillis);
    }

    public SmartImageFile(Parcel parcel) {
        this(parcel.readString(), parcel.readLong(), parcel.readDouble(), parcel.readDouble());
    }


    public long getDateTakenInMillis() {
        return mDateTakenInMillis;
    }

    @Override
    public void setDateTaken(Date date) {
        mDateTaken = date;
        mDateTakenInMillis = (int) date.getTime();
    }

    @Override
    public Date getDateTaken() {
        return mDateTaken;
    }

    @Override
    public Double getLatitude() {
        return mLatitude;
    }

    @Override
    public void setLatitude(double latitude) {

        mLatitude = latitude;
    }

    @Override
    public Double getLongitude() {
        return mLongitude;
    }

    @Override
    public void setLongitude(double longitude) {
        mLongitude = longitude;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(getAbsolutePath());
        dest.writeLong(mDateTakenInMillis);
        dest.writeDouble(mLatitude);
        dest.writeDouble(mLongitude);
    }

    public static final Creator CREATOR = new Creator<SmartImageFile>() {

        @Override
        public SmartImageFile createFromParcel(Parcel source) {
            return new SmartImageFile(source);
        }

        @Override
        public SmartImageFile[] newArray(int size) {
            return new SmartImageFile[size];
        }
    };

    @Override
    public void setImageUri(String uri) {
        throw new IllegalStateException("Cannot change path of image");
    }

    @Override
    public Uri getImageUri() {
        return Uri.fromFile(this);
    }

    @Override
    public void setThumbnailUri(String uri) {
        throw new IllegalStateException("Cannot change path of image");
    }

    @Override
    public Uri getThumbnailUri() {
        return Uri.fromFile(this);
    }

    @Override
    public boolean isLocal() {
        return true;
    }
}
