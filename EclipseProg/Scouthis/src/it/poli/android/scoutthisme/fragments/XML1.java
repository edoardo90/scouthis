package it.poli.android.scoutthisme.fragments;

import com.google.android.gms.maps.SupportMapFragment;

import it.poli.android.scouthisme.R;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

public class XML1 extends Fragment {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	Bundle savedInstanceState) {
		//this.savedInstance = savedInstanceState;
		View rootView = inflater.inflate(R.layout.fragment_section_findfriends_logged, container, false);
		return rootView;
	}
	
	@Override
	public void onResume()
	{
		super.onResume();
		
        this.getActivity().findViewById(R.id.button1).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
            	FragmentTransaction transaction = getFragmentManager().beginTransaction();
        		// Replace whatever is in the fragment_container view with this fragment,
        		// and add the transaction to the back stack
        		transaction.replace(R.id.findfriends_frame, new XML2());
        		transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        		transaction.addToBackStack(null);
        		// Commit the transaction
        		transaction.commit();
            }
        });
	}
	
	@Override
	public void onPause()
	{
		super.onPause();
		
		SupportMapFragment mapFragment = ((SupportMapFragment) getFragmentManager().findFragmentById(R.id.mapFindFriends));
		if(mapFragment != null) {
			FragmentManager fM = getFragmentManager();
			fM.beginTransaction().remove(mapFragment).commit();
		}
	}

}