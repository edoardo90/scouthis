package it.poli.android.scoutthisme.fragments;

import it.poli.android.scouthisme.R;
import it.poli.android.scoutthisme.Constants;
import it.poli.android.scoutthisme.gps.utils.GpsListener;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

public abstract class AllertableFragment extends Fragment implements GpsListener {

	private int idGpsAlertLayoutContainer = 0;
	private int fragmentInserted = 0;
	private String message = "" ;
	
	
	protected void setIdGpsAlertContainer(int idGpsAlertContainer)
	{
		this.idGpsAlertLayoutContainer = idGpsAlertContainer;
	}
	protected void setAlertMessage(String alertMessage)
	{
		this.message = alertMessage;
	}
	
	
	protected void addGpsOffAlert()
	{
		String messageToDisplay = "";
		if(idGpsAlertLayoutContainer != 0 && fragmentInserted == 0)
		{
			FragmentManager fm = getFragmentManager();
			FragmentTransaction fragmentTransaction = fm.beginTransaction();
			Fragment fm2 = new AlertUserGpsFragment();
			Bundle bundle = new Bundle();
			
			if (this.message.equals(""))
				messageToDisplay = getString(R.string.fragments_general_alert);
			else
				messageToDisplay = this.message;
			
			bundle.putString(Constants.ALLERTABLE_FRAGMENT, messageToDisplay);
			fm2.setArguments(bundle);
			fragmentTransaction.add(this.idGpsAlertLayoutContainer, fm2);
			
			fragmentTransaction.commit();
			fragmentInserted++;
		}
	}

	protected void removeGpsOffAler()
	{
		if(idGpsAlertLayoutContainer != 0 && fragmentInserted == 1)
		{
			FragmentManager fm = getFragmentManager();
			FragmentTransaction fragmentTransaction = fm.beginTransaction();
			fragmentTransaction.remove(this.getActivity().getSupportFragmentManager()
					.findFragmentById(this.idGpsAlertLayoutContainer));
			fragmentTransaction.commit();
			fragmentInserted--;
		}
	}

	@Override
	public abstract void onLocationChanged(Location location);

	@Override
	public void onProvidereEnabled(String provider) {
		this.removeGpsOffAler();
	}

	@Override
	public void onProviderDisabled(String provider) {
		this.addGpsOffAlert();

	}

}
