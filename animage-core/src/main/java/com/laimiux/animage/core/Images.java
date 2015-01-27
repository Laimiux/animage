package com.laimiux.animage.core;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.TreeSet;

public class Images {
    private TreeSet<SmartImage> mImages;

    public Images(SmartImage... initialImages) {
        mImages = new TreeSet<>(new Comparator<SmartImage>() {
            @Override
            public int compare(SmartImage lhs, SmartImage rhs) {
                Date lhsDate = lhs.getDateTaken();
                Date rhsDate = rhs.getDateTaken();

                return lhsDate.compareTo(rhsDate);
            }
        });

        for(SmartImage image : initialImages) {
            addImage(image);
        }
    }

    public void addImage(SmartImage object) {
        mImages.add(object);
    }

    public ArrayList<SmartImage> getImages() {
        return new ArrayList<>(mImages);
    }
}

