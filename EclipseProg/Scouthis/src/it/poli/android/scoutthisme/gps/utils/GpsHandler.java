package it.poli.android.scoutthisme.gps.utils;

import it.poli.android.scouthisme.R;
import it.poli.android.scoutthisme.fragments.FindFriendsLoggedFragment;
import it.poli.android.scoutthisme.fragments.GpsAlertFragment;
import it.poli.android.scoutthisme.fragments.GpsFragment;
import it.poli.android.scoutthisme.fragments.StepCounterRunFragment;
import android.R.string;
import android.annotation.TargetApi;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.Html;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

public class GpsHandler implements LocationListener
{	
	final int COMMIT_ADDALERTVIEW = 0;
	final int COMMIT_REMOVEALERTVIEW = 1;	
	
	Fragment mFragment;
	GpsListener listener;
    LocationManager locationManager;
    Location lastLocation;
	private GpsAlertFragment gpsAlertFragment;
	
	boolean viewActive;
	int lastUnCommittedWork;

	public void setViewActive(boolean viewActive)
	{
		this.viewActive = viewActive;
		if (viewActive && lastUnCommittedWork >= 0 && lastUnCommittedWork < 2)
		{
			switch (lastUnCommittedWork) {
				case 0:
					addAlertView();
				case 1:
				default:
					removeAlertView();
			}
			lastUnCommittedWork = -1;
		}
	}

	public GpsHandler (Fragment f) {
    	this.mFragment = f;
    	viewActive = false;
    	lastUnCommittedWork = -1;
	    locationManager = (LocationManager) mFragment.getActivity().getSystemService(Context.LOCATION_SERVICE);  
    }

    public void setListener(GpsListener l) {
    	this.listener = l;
    	locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
    }
    
    public void removeListener() {
		locationManager.removeUpdates(this);
    	this.listener = null;
    }
    
    @TargetApi(Build.VERSION_CODES.KITKAT)
	@SuppressWarnings("deprecation")
	public boolean isGpsEnabled()
    {        
	    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
	        String providers = Settings.Secure.getString(mFragment.getActivity().getContentResolver(),
	        		Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
	        return providers.contains(LocationManager.GPS_PROVIDER);
	    } else {
	        final int locationMode;
	        try {
	            locationMode = Settings.Secure.getInt(mFragment.getActivity().getContentResolver(), Settings.Secure.LOCATION_MODE);
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
		
		View view = mFragment.getView();
		LinearLayout container = (LinearLayout)view.findViewById(idContainer);
		if (view != null && container != null)
		{	
	    	FragmentTransaction transaction = mFragment.getChildFragmentManager().beginTransaction();
			// Replace whatever is in the fragment_container view with this fragment,
			// and add the transaction to the back stack
			if (container.getChildCount() == 0) {
				transaction.add(idContainer, gpsAlertFragment);
				transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
				transaction.addToBackStack(null);
				// Commit the transaction
				transaction.commit();
			}
			if (mFragment instanceof FindFriendsLoggedFragment) {
				TextView txtStatus = (TextView) view.findViewById(R.id.ff_txtStatus);
				txtStatus.setText(Html.fromHtml(mFragment.getString(R.string.findfriends_status_gps_deactivated)));
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
		
		View view = mFragment.getView();
		LinearLayout container = (LinearLayout)mFragment.getView().findViewById(idContainer);
		if (view != null && container != null && gpsAlertFragment != null) {
			if (container.getChildCount() > 0) {
				FragmentTransaction transaction = mFragment.getFragmentManager().beginTransaction();
				transaction.remove(gpsAlertFragment);
				transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
				transaction.addToBackStack(null);
				// Commit the transaction
				transaction.commit();
				
				gpsAlertFragment = null;
				
				if (mFragment instanceof FindFriendsLoggedFragment) {
					TextView txtStatus = (TextView) view.findViewById(R.id.ff_txtStatus);
					txtStatus.setText(Html.fromHtml(mFragment.getString(R.string.findfriends_status_waiting_gps)));
				}
			}
		}
	}

	@Override
	public void onLocationChanged(Location location) {
		if (listener != null && viewActive) {
			listener.onLocationChanged(location);
		}
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) { }

	@Override
	public void onProviderEnabled(String provider) {
		if (listener != null) {
			if (viewActive)
				removeAlertView();
			else
				lastUnCommittedWork = COMMIT_REMOVEALERTVIEW;
		}
	}

	@Override
	public void onProviderDisabled(String provider) 
	{
		if (listener != null) {
			if (viewActive)
				addAlertView();
			else
				lastUnCommittedWork = COMMIT_ADDALERTVIEW;
		}
	}
	
	public long ageMilliseconds(Location last) {
	    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1)
	        return age_ms_api_17(last);
	    return age_ms_api_pre_17(last);
	}

	@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
	private long age_ms_api_17(Location last) {
	    return (SystemClock.elapsedRealtimeNanos() - last
	            .getElapsedRealtimeNanos()) / 1000000;
	}

	private long age_ms_api_pre_17(Location last) {
	    return System.currentTimeMillis() - last.getTime();
	}
}