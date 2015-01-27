package com.laimiux.animage.core;

import java.util.Date;

/**
 * Created by laimiux on 8/22/14.
 */
public interface SmartImage extends Image {

    public void setDateTaken(Date date);

    public Date getDateTaken();

    public Double getLatitude();

    public void setLatitude(double latitude);

    public Double getLongitude();

    public void setLongitude(double longitude);
}

