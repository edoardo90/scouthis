package it.poli.android.scoutthisme.fragments;

import it.poli.android.scouthisme.R;
import it.poli.android.scoutthisme.tools.CircleButton;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

public class StepCounterFragment extends StepCounterFragmentArchetype
{
	View rootView;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	Bundle savedInstanceState) {
		/* Inflate the layout for this fragment */
		rootView = inflater.inflate(R.layout.fragment_section_stepcounter_home, container, false);
		return rootView;
	}
	
	@Override
	public void onResume()
	{
		super.onResume();
		
		CircleButton btnNewRun = (CircleButton)rootView.findViewById(R.id.step_button_newrun);
		btnNewRun.setOnClickListener(new OnClickListener()
		{	
			@Override
	            public void onClick(View v) {
					transitionTowars(new StepCounterRunFragment());
			}
		});
		
		CircleButton btnHistory = (CircleButton)rootView.findViewById(R.id.step_button_history);
		btnHistory.setOnClickListener(new OnClickListener()
		{	
			@Override
			public void onClick(View v) {
				transitionTowars(new StepCounterHistoryFragment());
			}
		});
	}
}