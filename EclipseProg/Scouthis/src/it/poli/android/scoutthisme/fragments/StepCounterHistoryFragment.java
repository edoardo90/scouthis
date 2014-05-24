package it.poli.android.scoutthisme.fragments;

import it.poli.android.scouthisme.R;
import it.poli.android.scoutthisme.stepcounter.utils.RunEpisode;
import it.poli.android.scoutthisme.stepcounter.utils.StepCounterLazyAdapter;
import it.poli.android.scoutthisme.stepcounter.utils.StepCounterUtilXML;

import java.util.LinkedList;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

public class StepCounterHistoryFragment extends StepCounterFragmentArchetype {

	
	ListView listViewRunEpisodes;
	StepCounterLazyAdapter adapter;
	LinkedList<RunEpisode> runEpisodesList;
	Bundle savedInstance;
	Activity mAct;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	Bundle savedInstanceState) {
		/* Inflate the layout for this fragment */
		View view = inflater.inflate(R.layout.fragment_section_stepcounter_history, container, false);

		return view;
	}
	
	@Override
	public void onResume()
	{	
		super.onResume();
		
		this.mAct = this.getActivity();
		
		Button btnGoHome = (Button)this.getActivity().findViewById(R.id.step_btn_goHome);
		btnGoHome.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				transitionTowars(new StepCounterHomeFragment());
			}
		});
		
		this.updateListViewFromFile();
	}
	
	private void updateListViewFromFile()
	{
		// XML alarm file parsing
		LinkedList<RunEpisode> aList = StepCounterUtilXML.populateAlarmListFromFile(mAct);
		this.runEpisodesList = (aList != null) ? aList : new LinkedList<RunEpisode>();
		listViewRunEpisodes = (ListView)mAct.findViewById(R.id.step_listview);
		// Getting adapter by passing xml data ArrayList
		adapter = new StepCounterLazyAdapter(mAct, runEpisodesList);        
		listViewRunEpisodes.setAdapter(adapter);
	}
	
}
