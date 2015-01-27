package com.laimiux.animage.core;

import android.net.Uri;

/**
* Created by laimiux on 11/12/14.
*/
public interface Image {
    public void setImageUri(String uri);

    public Uri getImageUri();

    public void setThumbnailUri(String uri);

    public Uri getThumbnailUri();

    public boolean isLocal();
}
