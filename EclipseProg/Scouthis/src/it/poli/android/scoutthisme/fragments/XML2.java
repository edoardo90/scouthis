package it.poli.android.scoutthisme.fragments;

import it.poli.android.scouthisme.R;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

public class XML2 extends Fragment {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	Bundle savedInstanceState) {
		//this.savedInstance = savedInstanceState;
		View rootView = inflater.inflate(R.layout.fragment_section_findfriends_disconnected, container, false);
		return rootView;
	}
	
	@Override
	public void onResume()
	{
		super.onResume();
		
        this.getActivity().findViewById(R.id.button2).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
            	FragmentTransaction transaction = getFragmentManager().beginTransaction();
        		// Replace whatever is in the fragment_container view with this fragment,
        		// and add the transaction to the back stack
        		transaction.replace(R.id.findfriends_frame, new XML1());
        		transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        		transaction.addToBackStack(null);
        		// Commit the transaction
        		transaction.commit();
            }
        });
	}
}