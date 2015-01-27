package com.laimiux.timeline;

import java.util.Comparator;
import java.util.Date;

public class DateComparator implements Comparator<TimelineHeader> {

    @Override public int compare(TimelineHeader lhs, TimelineHeader rhs) {
        final Date leftDate = lhs.getDate();
        final Date rightDate = rhs.getDate();

        final boolean sameDay = TimelineHeader.isSameDay(leftDate, rightDate);
        if (sameDay) return 0;
        else return leftDate.compareTo(rightDate);
    }
}
