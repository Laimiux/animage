package com.laimiux.timeline;

import java.util.Date;

public class TimelineHeader {
    private static final int MILLIS_PER_DAY = 24 * 60 * 60 * 1000;

    final private Date date;

    public TimelineHeader(Date date) {
        this.date = date;
    }

    public Date getDate() {
        return date;
    }

    public static boolean isSameDay(Date date1, Date date2) {

        // Strip out the time part of each date.
        long julianDayNumber1 = date1.getTime() / MILLIS_PER_DAY;
        long julianDayNumber2 = date2.getTime() / MILLIS_PER_DAY;

        // If they now are equal then it is the same day.
        return julianDayNumber1 == julianDayNumber2;
    }
}
