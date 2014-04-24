package it.poli.android.scoutthisme.tools;

import android.location.Location;

public interface GpsListener {
    public void onLocationChanged(Location location);
    public void onError(String string);
}