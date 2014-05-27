package it.poli.android.scoutthisme.fragments;

import it.poli.android.scouthisme.R;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;

public class StepCounterHomeFragment extends StepCounterFragmentArchetype {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	Bundle savedInstanceState) {
		/* Inflate the layout for this fragment */
		View view = inflater.inflate(R.layout.fragment_section_stepcounter_home, container, false);

		return view;
	}
	
	@Override
	public void onResume()
	{
		super.onResume();
		

		
		ImageButton btnNewRun = (ImageButton)this.getActivity().findViewById(R.id.step_button_newrun);
		btnNewRun.setOnClickListener(new OnClickListener() {
			
			@Override
	            public void onClick(View v) {
					transitionTowars(new StepCounterRunFragment());
			}
		});
		
		
		ImageButton btnHistory = (ImageButton)this.getActivity().findViewById(R.id.step_button_history);
		btnHistory.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				transitionTowars(new StepCounterHistoryFragment());
			}
		});
	}
	
	

	
	
}