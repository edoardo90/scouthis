package it.poli.android.scoutthisme.fragments;

import it.poli.android.scouthisme.R;
import it.poli.android.scoutthisme.Constants;
import it.poli.android.scoutthisme.alarm.utils.Alarm;
import it.poli.android.scoutthisme.alarm.utils.AlarmHandler;
import it.poli.android.scoutthisme.alarm.utils.AlarmLazyAdapter;
import it.poli.android.scoutthisme.alarm.utils.AlarmUtils;
import it.poli.android.scoutthisme.tools.TextFilesUtils;
import it.poli.android.scoutthisme.undobar.UndoBar;
import it.poli.android.scoutthisme.undobar.UndoBar.Listener;

import java.util.Calendar;
import java.util.LinkedList;

import android.app.Activity;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ListView;

import com.sleepbot.datetimepicker.time.RadialPickerLayout;
import com.sleepbot.datetimepicker.time.TimePickerDialog;

public class AlarmsHomeFragment extends Fragment implements 
	Listener, TimePickerDialog.OnTimeSetListener
{
	ListView listViewAlarms;
	AlarmLazyAdapter adapter;
	LinkedList<Alarm> alarmList;
	Alarm lastAlarmRemoved;
	Bundle savedInstance;
	View rootView;
	Activity mAct;
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		mAct = getActivity();
		savedInstance = savedInstanceState;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		rootView = inflater.inflate(R.layout.fragment_section_alarms_home, container, false);
		return rootView;
	}	
	
    @Override
    public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute)
    {	
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

        rootView.findViewById(R.id.addAlarmImg).setOnClickListener(new OnClickListener() {
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

		listViewAlarms = (ListView)rootView.findViewById(R.id.alarm_listview);
	}
	
	@Override
	public void onUndo(Parcelable token) {
		AlarmUtils.addNewAlarmClock(this.lastAlarmRemoved, mAct);
		this.alarmList.add(this.lastAlarmRemoved);
		this.updateAlarmListView();
	}

	@Override
	public void onHide() { /*  */ }
	
	public void switchAlarm(View v)
	{
		// Get the associated view's alarm and toggle its state
		int position = ((ListView)rootView.findViewById(R.id.alarm_listview)).getPositionForView(v);
		Alarm alarm = this.alarmList.get(position);
		alarm.toggleActive();
		//Get the new active alarm
		boolean newActive = alarm.isActive();
		this.alarmList.set(position, alarm);
		updateAlarmListView();

		TextFilesUtils.changeXmlChildValue(mAct,  Constants.XML_PATH_ALARM ,String.valueOf(newActive), position, Constants.XML_TAG_ALARM, Constants.XML_TAG_SWITCH);
		AlarmHandler.setAlarm(this.alarmList.get(position), mAct);
	}

	
	/**
	 * Remove Alarm view from main listViewAlarms (listview item)
	 * @param view
	 */
	public void deleteAlarm(View view)
	{
		ListView listView = (ListView)rootView.findViewById(R.id.alarm_listview);
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
		TextFilesUtils.removeXmlElement(mAct,Constants.XML_PATH_ALARM,positionToDelete, Constants.XML_TAG_ALARM);
	}

	private int getLastIdFromFile()
	{
		return TextFilesUtils.getLastIdFromXml(mAct, Constants.XML_PATH_ALARM);
	}

	private void updateAlarmListView()
	{
		listViewAlarms = (ListView)rootView.findViewById(R.id.alarm_listview);
		// Getting adapter by passing xml data ArrayList
		adapter = new AlarmLazyAdapter(mAct,  this, this.alarmList);        
		listViewAlarms.setAdapter(adapter);
	}

	private void updateListViewFromFile()
	{
		// XML alarm file parsing
		LinkedList<Alarm> aList = AlarmUtils.populateAlarmListFromFile(mAct);
		this.alarmList = (aList != null) ? aList : new LinkedList<Alarm>();
		listViewAlarms = (ListView)rootView.findViewById(R.id.alarm_listview);
		// Getting adapter by passing xml data ArrayList
		adapter = new AlarmLazyAdapter(mAct, this, alarmList);        
		listViewAlarms.setAdapter(adapter);
	}
}