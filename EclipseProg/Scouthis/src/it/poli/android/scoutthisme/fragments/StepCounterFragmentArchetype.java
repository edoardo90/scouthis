package it.poli.android.scoutthisme.fragments;

import it.poli.android.scouthisme.R;
import android.location.Location;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;

public  class StepCounterFragmentArchetype extends Fragment
{	
	protected void transitionTowars(Fragment frag)
	{
		FragmentTransaction transaction = getFragmentManager().beginTransaction();
		transaction.replace(R.id.stepcounter_frame, frag);
		transaction.commit();
	}

	public  void onLocationChanged(Location location) {}
}
