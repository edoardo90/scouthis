package it.poli.android.scoutthisme.fragments;

import it.poli.android.scouthisme.R;
import it.poli.android.scoutthisme.tools.CircleButton;
import it.poli.android.scoutthisme.tools.GifAnimationDrawable;

import java.io.IOException;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
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
		
		
		final GifAnimationDrawable big ;
		final ImageView imageview = (ImageView)this.getActivity().findViewById(R.id.step_counter_gigidag);
		try{
			big = new GifAnimationDrawable(getResources().openRawResource(R.raw.img_gigi_dag));
			big.setOneShot(false);
			 Handler handler = new Handler();
			 handler.postDelayed(new Runnable(){
		            @Override
		            public void run() {
		            	if(imageview.getDrawable() == null) imageview.setImageDrawable(big);
		        		big.setVisible(true, true);
		            }
		        }, 1000 );
		}catch(IOException ioe){
			Log.i("ooo", "Ooo");
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