package com.laimiux.animage.local;

import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;

import com.laimiux.animage.core.SmartImageFile;

import java.util.ArrayList;
import java.util.List;

public class LocalGalleryUtil {

    public static List<SmartImageFile> getGalleryImages(Context context) {
        // Set up an array of the Thumbnail Image ID column we want
//        String[] projection = {MediaStore.Images.Thumbnails._ID};
//        String[] projection = { MediaStore.Images.ImageColumns.DATE_TAKEN,
//                                MediaStore.Images.ImageColumns.LATITUDE,
//                                MediaStore.Images.ImageColumns.LONGITUDE,
//                                MediaStore.};
        // Create the cursor pointing to the SDCard
        Cursor cursor = context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            null, // Which columns to return
            null,       // Return all rows
            null,
            MediaStore.Images.Media.DATE_ADDED + " DESC");

        List<SmartImageFile> files = new ArrayList<>();
        if (cursor != null) {
            int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);

            int dateTakenColumnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.ImageColumns.DATE_TAKEN);
            int latColumnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.ImageColumns.LATITUDE);
            int longColumnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.ImageColumns.LONGITUDE);


            while (cursor.moveToNext()) {
                String filePath = cursor.getString(columnIndex);
                long dateTaken = cursor.getLong(dateTakenColumnIndex);
                double latitude = cursor.getDouble(latColumnIndex);
                double longitude = cursor.getDouble(longColumnIndex);

                SmartImageFile file = new SmartImageFile(filePath, dateTaken, latitude, longitude);
                files.add(file);


            }

            cursor.close();
        }

        return files;
    }
}
