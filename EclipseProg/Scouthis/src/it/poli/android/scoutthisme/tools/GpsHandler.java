package it.poli.android.scoutthisme.tools;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

public class GpsHandler implements LocationListener
{	
	Context mContext;
	GpsListener listener;
    LocationManager locationManager;
    Location lastLocation;
    
    public GpsHandler (Context c) {
    	this.mContext = c;
	    locationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
    }

    public void setListener(GpsListener l) {
    	this.listener = l;
    	locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
    }
    
    public void removeListener(GpsListener l) {
		locationManager.removeUpdates(this);
    	this.listener = null;
    }

	@Override
	public void onLocationChanged(Location location) {
		if (!listener.equals(null)) {
			listener.onLocationChanged(location);
		}
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) { }

	@Override
	public void onProviderEnabled(String provider) { }

	@Override
	public void onProviderDisabled(String provider) { }
}