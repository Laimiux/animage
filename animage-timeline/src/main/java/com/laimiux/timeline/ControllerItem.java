package com.laimiux.timeline;

import java.util.Date;

public class ControllerItem {
    final private Date mDate;
    final private int mPosition;

    public ControllerItem(int position, Date date) {
        mPosition = position;
        mDate = date;
    }


    public Date getDate() {
        return mDate;
    }

    public int getPosition() {
        return mPosition;
    }
}
