package it.poli.android.scoutthisme.fragments;

import it.poli.android.scouthisme.R;
import it.poli.android.scoutthisme.Constants;
import it.poli.android.scoutthisme.stepcounter.utils.RunEpisode;
import it.poli.android.scoutthisme.stepcounter.utils.StepCounterLazyAdapter;
import it.poli.android.scoutthisme.stepcounter.utils.StepCounterUtilXML;
import it.poli.android.scoutthisme.tools.CircleButton;
import it.poli.android.scoutthisme.tools.TextFilesUtils;
import it.poli.android.scoutthisme.undobar.UndoBar;
import it.poli.android.scoutthisme.undobar.UndoBar.Listener;

import java.util.LinkedList;

import android.app.Activity;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ListView;

public class StepCounterHistoryFragment extends StepCounterFragmentArchetype implements Listener
{
	ListView listViewRunEpisodes;
	StepCounterLazyAdapter adapter;
	LinkedList<RunEpisode> runEpisodesList;
	Bundle savedInstance;
	RunEpisode lastRunRemoved = null;
	Activity mAct;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View view;
		view = inflater.inflate(R.layout.fragment_section_stepcounter_history, container, false);
		return view;
	}

	@Override
	public void onResume()
	{	
		super.onResume();

		this.mAct = this.getActivity();

		CircleButton btnGoHome = (CircleButton)this.getActivity().findViewById(R.id.step_btn_goHome);
		btnGoHome.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				transitionTowars(new StepCounterFragment());
			}
		});

		listViewRunEpisodes = (ListView)mAct.findViewById(R.id.step_listview);
		this.updateListViewFromFile();
	}

	public void deleteRunEpisode(View view)
	{
		ListView listView = (ListView)mAct.findViewById(R.id.step_listview);
		// Remove alarm view (listview item)
		int position = listView.getPositionForView(view);
		this.lastRunRemoved = this.runEpisodesList.get(position);

		deleteRunEpisodeFromXML(position);

		updateListViewFromFile();
		//Show undobar, do you want to undo?
		new UndoBar.Builder(mAct)
		.setMessage("Corsa cancellata")
		.setListener(this)
		.show();
	}

	private void deleteRunEpisodeFromXML(int positionToDelete)
	{
		TextFilesUtils.removeXmlElement(mAct,Constants.XML_PATH_STEPCOUNTER,positionToDelete, Constants.XML_TAG_RUNEPISODE);
	}

	private void updateListViewFromFile()
	{
		// XML alarm file parsing
		LinkedList<RunEpisode> aList = StepCounterUtilXML.populateAlarmListFromFile(mAct);
		this.runEpisodesList = (aList != null) ? aList : new LinkedList<RunEpisode>();
		listViewRunEpisodes = (ListView)mAct.findViewById(R.id.step_listview);
		// Getting adapter by passing xml data ArrayList
		adapter = new StepCounterLazyAdapter(mAct, runEpisodesList, this);        
		listViewRunEpisodes.setAdapter(adapter);
	}

	/**
	 * Undo Bar method
	 */
	@Override
	public void onHide() {	}
	
	/**
	 * Undo bar method
	 */
	@Override
	public void onUndo(Parcelable token) {
		TextFilesUtils.appendXmlElement(this.getActivity(), Constants.XML_PATH_STEPCOUNTER,
				this.lastRunRemoved.getXmlTagFieldMap(), Constants.XML_TAG_RUNEPISODE);
		this.runEpisodesList.add(this.lastRunRemoved);
		this.updateRunepisodeListView();
	}

	private void updateRunepisodeListView() {
		listViewRunEpisodes = (ListView)mAct.findViewById(R.id.step_listview);
		// Getting adapter by passing xml data ArrayList
		adapter = new StepCounterLazyAdapter(mAct, this.runEpisodesList, this);        
		listViewRunEpisodes.setAdapter(adapter);
	}
}