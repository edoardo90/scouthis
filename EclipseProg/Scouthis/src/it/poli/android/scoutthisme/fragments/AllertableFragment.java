package it.poli.android.scoutthisme.fragments;

import it.poli.android.scouthisme.R;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

public class AllertableFragment extends Fragment {
	
	protected int idGpsAlertLayoutContainer;
	
	protected void addGpsOffAlert()
	{
		FragmentManager fm = getFragmentManager();
		FragmentTransaction fragmentTransaction = fm.beginTransaction();
		Fragment fm2 = new AlertUserGpsFragment();
		fragmentTransaction.add(R.id.findf_linearl_container, fm2);
		fragmentTransaction.remove(this.getActivity().getSupportFragmentManager()
				.findFragmentById(this.idGpsAlertLayoutContainer));
		fragmentTransaction.commit();
	}
	
	protected void removeGpsOffAler()
	{
		FragmentManager fm = getFragmentManager();
		FragmentTransaction fragmentTransaction = fm.beginTransaction();
		fragmentTransaction.remove(this.getActivity().getSupportFragmentManager()
				.findFragmentById(this.idGpsAlertLayoutContainer));
		fragmentTransaction.commit();
	}
	
}
