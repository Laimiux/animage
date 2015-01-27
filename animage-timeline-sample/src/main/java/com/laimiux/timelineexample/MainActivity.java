package com.laimiux.timelineexample;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

import com.laimiux.animage.core.Images;
import com.laimiux.animage.core.SmartImageFile;
import com.laimiux.animage.local.LocalGalleryUtil;
import com.laimiux.timeline.ControllerItem;
import com.laimiux.timeline.DateComparator;
import com.laimiux.timeline.TimelineHeader;
import com.laimiux.timeline.TimelineUtil;
import com.laimiux.timeline.TimelineView;

import java.util.List;
import java.util.TreeMap;


public class MainActivity extends ActionBarActivity {
    TimelineView timelineView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        timelineView = (TimelineView) findViewById(R.id.timeline_container);

        final List<SmartImageFile> galleryImages = LocalGalleryUtil.getGalleryImages(this);
        List<Object> list = TimelineUtil.toList(mapImageList(galleryImages));

        final List<ControllerItem> controllerItems = TimelineUtil.getControllerItems(list);


        TimelineAdapter adapter = new TimelineAdapter(this);
        timelineView.setListAdapter(adapter);

        TimelineControllerAdapter controllerAdapter = new TimelineControllerAdapter(this);
        timelineView.setControllerAdapter(controllerAdapter);

        timelineView.updateItems(list, controllerItems);
    }

    private TreeMap<TimelineHeader, Images> mapImageList(List<SmartImageFile> images) {
        TreeMap<TimelineHeader, Images> map = new TreeMap<>(new DateComparator());

        for (SmartImageFile image : images) {
            final TimelineHeader key = new TimelineHeader(image.getDateTaken());
            if (map.containsKey(key)) {
                map.get(key).addImage(image);
            } else {
                map.put(key, new Images(image));
            }
        }


        return map;
    }
}
