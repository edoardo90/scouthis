package it.poli.android.scoutthisme.gps.utils;

import it.poli.android.scouthisme.R;
import it.poli.android.scoutthisme.fragments.FindFriendsLoggedFragment;
import it.poli.android.scoutthisme.fragments.GpsAlertFragment;
import it.poli.android.scoutthisme.fragments.GpsFragment;
import it.poli.android.scoutthisme.fragments.StepCounterRunFragment;
import android.annotation.TargetApi;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.widget.LinearLayout;

public class GpsHandler implements LocationListener
{	
	Fragment mFragment;
	GpsListener listener;
    LocationManager locationManager;
    Location lastLocation;
	private GpsAlertFragment gpsAlertFragment;
    
    public GpsHandler (Fragment f) {
    	this.mFragment = f;
	    locationManager = (LocationManager) mFragment.getActivity().getSystemService(Context.LOCATION_SERVICE);  
    }

    public void setListener(GpsListener l) {
    	this.listener = l;
    	locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
	    //Check if GPS is enabled and ask user to activate it
    	/*if (!isGpsEnabled(mFragment)) {
    		addAlertView();
	    }*/
    }
    
    public void removeListener() {
		locationManager.removeUpdates(this);
    	this.listener = null;
    }
    
    @TargetApi(Build.VERSION_CODES.KITKAT)
	@SuppressWarnings("deprecation")
	public static boolean isGpsEnabled(Fragment fragment)
    {        
	    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
	        String providers = Settings.Secure.getString(fragment.getActivity().getContentResolver(),
	        		Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
	        return providers.contains(LocationManager.GPS_PROVIDER);
	    } else {
	        final int locationMode;
	        try {
	            locationMode = Settings.Secure.getInt(fragment.getActivity().getContentResolver(), Settings.Secure.LOCATION_MODE);
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
    
    public void addAlertView()
    {
    	gpsAlertFragment = new GpsAlertFragment();
		gpsAlertFragment.setFragment(mFragment);
		int idContainer = 0;
		
		if (mFragment instanceof GpsFragment) {
			idContainer = R.id.gps_alert_container;
		} else if (mFragment instanceof FindFriendsLoggedFragment) {
			idContainer = R.id.ffriends_alert_container;
		} else if (mFragment instanceof StepCounterRunFragment) {
			idContainer = R.id.step_alert_gps_container;
		}
		
		LinearLayout container = (LinearLayout)mFragment.getView().findViewById(idContainer);
		if (container != null) {
	    	FragmentTransaction transaction = mFragment.getFragmentManager().beginTransaction();
			// Replace whatever is in the fragment_container view with this fragment,
			// and add the transaction to the back stack
			if (container.getChildCount() == 0) {
				transaction.add(idContainer, gpsAlertFragment);
				transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
				transaction.addToBackStack(null);
				// Commit the transaction
				transaction.commit();
			}
		}
    }
    
	public void removeAlertView()
	{	
		int idContainer = 0;

    	//FragmentTransaction transaction = mFragment.getFragmentManager().beginTransaction();
		// Replace whatever is in the fragment_container view with this fragment,
		// and add the transaction to the back stack		
		if (mFragment instanceof GpsFragment) {
			idContainer = R.id.gps_alert_container;
		} else if (mFragment instanceof FindFriendsLoggedFragment) {
			idContainer = R.id.ffriends_alert_container;
		} else if (mFragment instanceof StepCounterRunFragment) {
			idContainer = R.id.step_alert_gps_container;
		}	
		
		final LinearLayout container = (LinearLayout)mFragment.getView().findViewById(idContainer);
		
		if (container != null && gpsAlertFragment != null) {
			if (container.getChildCount() > 0) {
				/*container.post(new Runnable() {
				    @Override
				    public void run()
				    {
				    	container.removeAllViews();
				    }
				});*/
				//container.removeAllViews();
				FragmentTransaction transaction = mFragment.getFragmentManager().beginTransaction();
				transaction.remove(gpsAlertFragment);
				transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
				transaction.addToBackStack(null);
				// Commit the transaction
				transaction.commit();
				
				gpsAlertFragment = null;
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
		/*int i=0;
		i--;*/
	}

	@Override
	public void onProviderEnabled(String provider) { 
		removeAlertView();
		/*int j=0;
		j--;*/
	}

	@Override
	public void onProviderDisabled(String provider) 
	{
		addAlertView();
		/*int k=0;
		k--;*/
	}
}