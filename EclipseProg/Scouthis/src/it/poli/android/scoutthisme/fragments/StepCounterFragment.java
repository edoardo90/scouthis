package it.poli.android.scoutthisme.fragments;

import it.poli.android.scouthisme.R;
import it.poli.android.scoutthisme.tools.CircleButton;
import it.poli.android.scoutthisme.tools.GifAnimationDrawable;

import java.io.IOException;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;

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
	
	private void setGifImage()
	{
		
		
		ImageView imgGigiImageView;
		GifAnimationDrawable gigDagAnimation;
		
		imgGigiImageView = (ImageView)this.getActivity().findViewById(R.id.step_counter_gigidag);

		try{
			
			gigDagAnimation = new GifAnimationDrawable(getResources().openRawResource(R.raw.img_gigi_dag));
			gigDagAnimation.setOneShot(false);
			imgGigiImageView.setImageDrawable(gigDagAnimation);			
		}catch(IOException ioe){
			
		}
	}
	
	
	@Override
	public void onResume()
	{
		super.onResume();
		
		
		this.setGifImage();
		
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