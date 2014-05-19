package it.poli.android.scoutthisme.fragments;

import it.poli.android.scouthisme.R;
import it.poli.android.scoutthisme.Constants;
import it.poli.android.scoutthisme.alarm.utils.LazyAdapter;

import it.poli.android.scoutthisme.tools.Alarm;
import it.poli.android.scoutthisme.tools.AlarmHandler;
import it.poli.android.scoutthisme.tools.AlarmUtils;
import it.poli.android.scoutthisme.undobar.UndoBar;
import it.poli.android.scoutthisme.undobar.UndoBar.Listener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;


import com.sleepbot.datetimepicker.time.RadialPickerLayout;
import com.sleepbot.datetimepicker.time.TimePickerDialog;

import android.app.Activity;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ImageView;
import android.widget.ListView;

public class AlarmsHomeFragment extends Fragment implements 
	Listener, TimePickerDialog.OnTimeSetListener
{
	ListView list;
	LazyAdapter adapter;
	LinkedList<Alarm> alarmList;
	Alarm lastAlarmRemoved;
	Bundle savedInstance;
	Activity mAct;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		super.onCreateView(inflater, container, savedInstanceState);
		this.savedInstance = savedInstanceState;
		View rootView = inflater.inflate(R.layout.fragment_section_alarms_home, container, false);
		return rootView;
	}
	
	
	
    @Override
    public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute) {
    	
    	
    	Bundle bundle = new Bundle();
    	bundle.putInt(Constants.ALARM_HOUR, hourOfDay);
    	bundle.putInt(Constants.ALARM_MINUTE, minute);
    	// set Fragmentclass Arguments
    	AlarmsSetClockFragment alarmsSetClockFrag = new AlarmsSetClockFragment();
    	alarmsSetClockFrag.setArguments(bundle);
    	
    	FragmentTransaction transaction = getFragmentManager().beginTransaction();
		// Replace whatever is in the fragment_container view with this fragment,
		// and add the transaction to the back stack
		transaction.replace(R.id.alarms_frame, alarmsSetClockFrag);
		transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
		transaction.addToBackStack(null);
		// Commit the transaction
		transaction.commit();
    }
	
	
	@Override
	public void onResume()
	{
		super.onResume();
		mAct = getActivity();
		
		final Calendar calendar = Calendar.getInstance();
        final TimePickerDialog timePickerDialog = TimePickerDialog.newInstance(this, calendar.get(Calendar.HOUR_OF_DAY) ,calendar.get(Calendar.MINUTE), false, false);

        this.getActivity().findViewById(R.id.addAlarmImg).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                timePickerDialog.setVibrate(true);
                timePickerDialog.setCloseOnSingleTapMinute(false);
                timePickerDialog.show(getActivity().getSupportFragmentManager(), Constants.TIMEPICKER_TAG);
            }
        });
        if (this.savedInstance != null) {
            TimePickerDialog tpd = (TimePickerDialog) getActivity().
            		getSupportFragmentManager().
            		findFragmentByTag(Constants.TIMEPICKER_TAG);
            if (tpd != null) {
                tpd.setOnTimeSetListener(this);
            }
        }

		
		
		//se chiamato da "aggiungi sveglia" deve estrarre i dati della nuova sveglia
		Bundle paramAlarm = getArguments();
		if(paramAlarm != null)
		{	
			boolean [] activeDays =  paramAlarm.getBooleanArray(Constants.PARAM_ALARM_DAYS);
			String alarmTime = paramAlarm.getString(Constants.PARAM_ALARM_TIME);
			String bird  = paramAlarm.getString(Constants.PARAM_ALARM_BIRD);
			boolean active = paramAlarm.getBoolean(Constants.PARAM_ALARM_ACTIVE, true);
			
			int id = this.getLastIdFromFile();
			AlarmUtils.addNewAlarmClock(active, alarmTime, activeDays, bird, id++, mAct);
		}

		this.updateListViewFromFile();

		Log.i("aggiungo listener", "add lst");

		list = (ListView)mAct.findViewById(R.id.list);
		list.setOnItemLongClickListener((new OnItemLongClickListener() {
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
					int pos, long id) {
				Log.i("long clicked","pos: " + pos);
				return true;
			}
		})); 

		// Click event for single list row
		list.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Integer pos = position;
				Log.e("!! list view", "hai cliccato l'item" + pos.toString());
			}
		});

		View vv = list.getAdapter().getView(0, null, list);
		ImageView trashAlarm = (ImageView) vv.findViewById(R.id.trash_alarm);
		trashAlarm.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				deleteAlarm(v);
			}
		});

		ImageView interrutAlarm = (ImageView)vv.findViewById(R.id.interrutt);
		interrutAlarm.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				switchAlarm(v);
			}
		});
	}
	
	@Override
	public void onUndo(Parcelable token) {
		AlarmUtils.addNewAlarmClock(this.lastAlarmRemoved, mAct);
		this.alarmList.add(this.lastAlarmRemoved);
		this.updateAlarmListView();
	}

	@Override
	public void onHide() { /* TODO */ }
	
	public void switchAlarm(View v)
	{
		// Get the associated view's alarm and toggle its state
		int position = ((ListView)mAct.findViewById(R.id.list)).getPositionForView(v);
		Alarm alarm = this.alarmList.get(position);
		alarm.toggleActive();
		//Get the new active alarm
		boolean newActive = alarm.isActive();
		this.alarmList.set(position, alarm);
		updateAlarmListView();

		this.setAlarmFileActive(newActive, position);
		AlarmHandler.setAlarm(this.alarmList.get(position), mAct);
	}

	private void setAlarmFileActive(boolean newActive, int positionToDelete)
	{
		List<String> fileContent =  AlarmUtils.getAlarmsContent(mAct);
		List<String> fileNewContent = new ArrayList<String>();

		String rowClean = "";
		int /*id = 0, */pos = 0;
		boolean editing = false;
		for(String row : fileContent)
		{
			rowClean = row.replaceAll("\\s","");
			if (rowClean.equals("<" + Constants.XML_TAG_ALARM + ">") && positionToDelete == pos)
				editing = true;
			
			if(rowClean.equals("<" + Constants.XML_TAG_ALARM + ">"))
				pos++;

			if (!editing)
				fileNewContent.add(rowClean);
			else
			{
				if(rowClean.contains(Constants.XML_TAG_SWITCH))
					fileNewContent.add("<" + Constants.XML_TAG_SWITCH + ">" + newActive + "</" + Constants.XML_TAG_SWITCH + ">");
				else
				//{
					fileNewContent.add(rowClean);
					/*if(rowClean.contains(Constants.XML_TAG_ID))
						id = Integer.valueOf(rowClean.replaceAll("[^0-9]+", " ").replaceAll("\\s", "")); */
				//}
			}
			if (rowClean.equals("</" + Constants.XML_TAG_ALARM + ">") )
				editing = false;
		}
		AlarmUtils.writeListOnXML(fileNewContent, mAct);
	}

	/**
	 * Remove Alarm view from main list (listview item)
	 * @param view
	 */
	public void deleteAlarm(View view)
	{
		ListView listView = (ListView)mAct.findViewById(R.id.list);
		// Remove alarm view (listview item)
		int position = listView.getPositionForView(view);
		this.lastAlarmRemoved = this.alarmList.get(position);
		AlarmHandler.removeAlarm(this.lastAlarmRemoved, mAct);
		deleteAlarmFromXML(position);
		updateListViewFromFile();
		// Show undobar, do you want to undo?
		new UndoBar.Builder(mAct)
			.setMessage("Sveglia cancellata")
			.setListener(this)
			.show();
	}
	
	private void deleteAlarmFromXML(int positionToDelete)
	{
		List<String> fileContent =  AlarmUtils.getAlarmsContent(mAct);
		List<String> fileNewContent = new ArrayList<String>();

		String rowClean = "";
		int /*id = 0, */pos = 0;
		boolean skipping = false;
		for(String row : fileContent)
		{
			rowClean = row.replaceAll("\\s","");
			if (rowClean.equals("<" + Constants.XML_TAG_ALARM + ">") && positionToDelete == pos)
				skipping = true;

			if(rowClean.equals("<" + Constants.XML_TAG_ALARM + ">"))
				pos++;

			if (!skipping)
				fileNewContent.add(rowClean);
			/*else if(rowClean.contains(Constants.XML_TAG_ID))
					id = Integer.valueOf(rowClean.replaceAll("[^0-9]+", " ").replaceAll("\\s", ""));*/

			if (rowClean.equals("</" + Constants.XML_TAG_ALARM + ">") )
				skipping = false;
		}
		AlarmUtils.writeListOnXML(fileNewContent, mAct);
	}

	private int getLastIdFromFile()
	{
		List<String> alarmsFile =  AlarmUtils.getAlarmsContent(mAct);
		int id = 0;
		for(String s : alarmsFile)
			if (s.contains("<" + Constants.XML_TAG_ID + ">"))
				if ( Integer.valueOf(s.replaceAll("[^0-9]+", " ").replaceAll("\\s", "")) > id)
					id = Integer.valueOf(s.replaceAll("[^0-9]+", " ").replaceAll("\\s", ""));
		return id;
	}

	private void updateAlarmListView()
	{
		list = (ListView)mAct.findViewById(R.id.list);
		// Getting adapter by passing xml data ArrayList
		adapter = new LazyAdapter(mAct, this, this.alarmList);        
		list.setAdapter(adapter);
	}

	private void updateListViewFromFile()
	{
		// XML alarm file parsing
		LinkedList<Alarm> aList = AlarmUtils.populateAlarmListFromFile(mAct);
		this.alarmList = (aList != null) ? aList : new LinkedList<Alarm>();
		list = (ListView)mAct.findViewById(R.id.list);
		// Getting adapter by passing xml data ArrayList
		adapter = new LazyAdapter(mAct, this, alarmList);        
		list.setAdapter(adapter);
	}


}