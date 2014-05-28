package it.poli.android.scoutthisme.fragments;

import it.poli.android.scouthisme.R;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class StepCounterFrameFragment extends Fragment {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	Bundle savedInstanceState) {
		/* Inflate the layout for this fragment */
		View view = inflater.inflate(R.layout.fragment_section_stepcounter, container, false);
		
		FragmentTransaction transaction = getFragmentManager()
		.beginTransaction();
		/*
		* When this container fragment is created, we fill it with stepcounter home fragment
		*/
		transaction.replace(R.id.stepcounter_frame, new StepCounterFragment());
		transaction.commit();
		
		return view;
	}
	
}
