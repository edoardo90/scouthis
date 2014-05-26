package it.poli.android.scoutthisme.gps.utils;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;
import android.util.Log;

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
	    //Check if GPS is enabled and ask user to activate it
    	if ( false && !isGpsEnabled(mContext)) {
	        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
	        
	        builder.setTitle("Gps non attivo");  // GPS not found
	        builder.setMessage("Vuoi abilitare il GPS? Al momento non mi risulta attivo."); // Want to enable?
	        
	        builder.setPositiveButton("Ok, va bene.", new DialogInterface.OnClickListener() {
	            public void onClick(DialogInterface dialogInterface, int i) {
	            	mContext.startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
	            }
	        });
	        
	        builder.setNegativeButton("No, grazie.", null);
	        builder.create().show();
	    }
    	locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
    }
    
    public void removeListener() {
		locationManager.removeUpdates(this);
    	this.listener = null;
    }
    
    @TargetApi(Build.VERSION_CODES.KITKAT)
	@SuppressWarnings("deprecation")
	public static boolean isGpsEnabled(Context context)
    {        
	    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
	        String providers = Settings.Secure.getString(context.getContentResolver(),
	        		Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
	        return providers.contains(LocationManager.GPS_PROVIDER);
	    } else {
	        final int locationMode;
	        try {
	            locationMode = Settings.Secure.getInt(context.getContentResolver(),
	            		Settings.Secure.LOCATION_MODE);
	        } catch (SettingNotFoundException e) {
	            e.printStackTrace();
	            return false;
	        }
	        switch (locationMode) {
		        case Settings.Secure.LOCATION_MODE_SENSORS_ONLY:
		        case Settings.Secure.LOCATION_MODE_BATTERY_SAVING:
		        case Settings.Secure.LOCATION_MODE_HIGH_ACCURACY:
		            return true;
		        case Settings.Secure.LOCATION_MODE_OFF:
		        default:
		            return false;
	        }
	    }
    }

	@Override
	public void onLocationChanged(Location location) {
		
		if (listener != null) {
			listener.onLocationChanged(location);
		}
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras)
	{
		int i=0;
		i--;
	}

	@Override
	public void onProviderEnabled(String provider) { 
		if(listener != null)
			listener.onProvidereEnabled(provider);
	}

	@Override
	public void onProviderDisabled(String provider) 
	{
		if(listener != null)
			listener.onProviderDisabled(provider);
	}
}