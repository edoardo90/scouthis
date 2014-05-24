package it.poli.android.scoutthisme.gps.utils;

import android.location.Location;

public interface GpsListener {
    public void onLocationChanged(Location location);
}