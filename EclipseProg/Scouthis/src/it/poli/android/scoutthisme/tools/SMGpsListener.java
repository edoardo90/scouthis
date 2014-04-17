package it.poli.android.scoutthisme.tools;

import java.util.*;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

interface SMGpsListener {
    public void onLocationChanged(Location location);
}

class SMGpsInitiater implements LocationListener
{	
    static List<SMGpsListener> listeners = new ArrayList<SMGpsListener>();
    static Activity mAct = new Activity();
    static LocationManager locationManager;
    static Location lastLocation;
    
    public void addListener(SMGpsListener toAdd) {
    	if (listeners.size() == 0)
    	{
    		locationManager = (LocationManager) mAct.getSystemService(Context.LOCATION_SERVICE);
    		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
    	}
    	listeners.add(toAdd);
		if (lastLocation != null)
			toAdd.onLocationChanged(lastLocation);
    }
    
    public void removeListener(SMGpsListener toRemove) {
    	listeners.remove(toRemove);
    	if (listeners.size() == 0)
    	{
    		locationManager.removeUpdates(this);
    	}
    }

	@Override
	public void onLocationChanged(Location location) {
		for (SMGpsListener listener : listeners) {
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

/**/
class Responder implements SMGpsListener
{
	@Override
	public void onLocationChanged(Location location) { }
}