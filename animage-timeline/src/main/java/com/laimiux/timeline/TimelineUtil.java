package com.laimiux.timeline;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class TimelineUtil {

    /**
     * Helper to convert map to a list
     *
     * List ends up being: Key, Value, Key, Value, ...
     * @return List<Object> that contains map entries.
     */
    public static List<Object> toList(Map<?, ?> map) {
        List<Object> list = new ArrayList<>(map.size() * 2);
        for (Map.Entry<?, ?> entry : map.entrySet()) {
            list.add(entry.getKey());
            list.add(entry.getValue());
        }

        return list;
    }

    public static List<ControllerItem> getControllerItems(List<Object> timelineItems) {
        List<ControllerItem> controllerItems = new ArrayList<>();

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMM", Locale.getDefault());
        TimelineHeader previousHeader = null;


        // Skip last item header because it mights contain one item
        for (int i = 0; i < timelineItems.size() - 2; i = i + 2) {
            TimelineHeader header = (TimelineHeader) timelineItems.get(i);
            if (previousHeader == null) {
                controllerItems.add(new ControllerItem(i, header.getDate()));
                previousHeader = header;
            } else {
                Date previousDate = previousHeader.getDate();
                Date currentDate = header.getDate();

                if (!simpleDateFormat.format(previousDate).equals(simpleDateFormat.format(currentDate))) {
                    controllerItems.add(new ControllerItem(i, currentDate));
                    previousHeader = header;
                }
            }
        }

        return controllerItems;
    }
}
