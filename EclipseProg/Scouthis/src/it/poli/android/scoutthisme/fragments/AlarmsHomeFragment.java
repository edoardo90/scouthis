package it.poli.android.scoutthisme.fragments;

import it.poli.android.scouthisme.R;
import it.poli.android.scouthisme.alarm.LazyAdapter;
import it.poli.android.scouthisme.alarm.XMLParser;
import it.poli.android.scoutthisme.Constants;
import it.poli.android.scoutthisme.tools.AlarmHandler;
import it.poli.android.scoutthisme.tools.AlarmUtils;
import it.poli.android.scoutthisme.tools.Alarm;
import it.poli.android.scoutthisme.undobar.UndoBar;
import it.poli.android.scoutthisme.undobar.UndoBar.Listener;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import android.content.Context;
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


public class AlarmsHomeFragment extends Fragment implements Listener
{
	ListView list;
	LazyAdapter adapter;
	LinkedList<Alarm> alarmList;

	private Alarm lastUARemoved;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		super.onCreateView(inflater, container, savedInstanceState);
		View rootView = inflater.inflate(R.layout.fragment_section_alarms_home, container, false);
		return rootView;
	}

	@Override
	public void onResume()
	{
		super.onResume();

		ImageView addAlarm = (ImageView)this.getActivity().findViewById(R.id.addAlarmImg);
		addAlarm.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// Create new fragment and transaction
				FragmentTransaction transaction = getFragmentManager().beginTransaction();
				// Replace whatever is in the fragment_container view with this fragment,
				// and add the transaction to the back stack
				transaction.replace(R.id.alarms_frame, new AlarmsSetClockFragment());
				transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
				transaction.addToBackStack(null);
				// Commit the transaction
				transaction.commit();
			}
		});

		//se chiamato da "aggiungi sveglia" deve estrarre i dati della nuova sveglia
		Bundle paramAlarm = getArguments();
		if(paramAlarm != null)
		{	
			boolean [] activeDays =  paramAlarm.getBooleanArray(Constants.PARAM_ALARM_DAYS);
			String alarmTime = paramAlarm.getString(Constants.PARAM_ALARM_TIME);
			String bird  = paramAlarm.getString(Constants.PARAM_ALARM_BIRD);
			boolean active = paramAlarm.getBoolean(Constants.PARAM_ALARM_ACTIVE, true);
			
			int id = this.getLastIdFromFile();
			this.addNewAlarmClock(active, alarmTime, activeDays, bird, id++);
		}

		this.updateListViewFromFile();

		Log.i("aggiungo listener", "add lst");

		list = (ListView)this.getActivity().findViewById(R.id.list);
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

	int getPosFromView(View v)
	{	
		return ((ListView)this.getActivity().findViewById(R.id.list)).getPositionForView(v);
	}

	public void switchAlarm(View v)
	{
		// Get the associated view's alarm and toggle its state
		int pos = getPosFromView(v);
		Alarm alarm = this.alarmList.get(pos);
		alarm.toggleActive();
		
		boolean newActive = alarm.isActive();
		this.alarmList.set(pos, alarm);
		updateListViewFromAlarmList();

		this.setAlarmFileActive(newActive, pos);
		AlarmHandler.setAlarm(this.alarmList.get(pos), getActivity().getApplicationContext(), getActivity());
	}

	// TODO: Does we need int returned?
	private int setAlarmFileActive(boolean newActive, int positionToDelete)
	{
		List<String> fileContent =  this.getAlarmsContent();
		List<String> fileNewContent = new ArrayList<String>();

		String rowClean = "";
		int id = 0;
		int pos = 0;
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
				{
					fileNewContent.add(rowClean);
					if(rowClean.contains(Constants.XML_TAG_ID))
						id = Integer.valueOf(rowClean.replaceAll("[^0-9]+", " ").replaceAll("\\s", "")); 
				}
			}
			if (rowClean.equals("</" + Constants.XML_TAG_ALARM + ">") )
				editing = false;
		}
		
		this.writeListOnFile(fileNewContent);
		return id;
	}

	public void deleteAlarm(View v)
	{
		ListView lview = (ListView)this.getActivity().findViewById(R.id.list);
		int position = lview.getPositionForView(v);

		this.lastUARemoved = this.alarmList.get(position);
		AlarmHandler.removeAlarm(this.lastUARemoved, 
				this.getActivity().getApplicationContext(), this.getActivity());
		deleteAlarmInPosition(position);
		updateListViewFromFile();
		new UndoBar.Builder(this.getActivity())
			.setMessage("Sveglia cancellata")
			.setListener(this)
			.show();
	}

	private int deleteAlarmInPosition(int positionToDelete)
	{
		List<String> fileContent =  this.getAlarmsContent();
		List<String> fileNewContent = new ArrayList<String>();

		String rowClean = "";
		int id = 0;
		int pos = 0;
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
			else if(rowClean.contains(Constants.XML_TAG_ID))
					id = Integer.valueOf(rowClean.replaceAll("[^0-9]+", " ").replaceAll("\\s", ""));

			if (rowClean.equals("</" + Constants.XML_TAG_ALARM + ">") )
				skipping = false;
		}
		
		this.writeListOnFile(fileNewContent);
		return id;
	}

	private void writeListOnFile(List<String> fileContent)
	{
		FileOutputStream outputStream;
		String fileName = Constants.XML_PATH_ALARM;
		try {
			outputStream = this.getActivity().openFileOutput(fileName, Context.MODE_PRIVATE );
			for(String fileRow : fileContent)
			{
				outputStream.write(fileRow.getBytes());
				outputStream.write("\n".getBytes());
			}
			outputStream.close();
		} catch (Exception e) { /* TODO */}
	}

	private int getLastIdFromFile()
	{
		List<String> alarmsFile =  this.getAlarmsContent();
		int id = 0;
		for(String s : alarmsFile)
			if (s.contains("<" + Constants.XML_TAG_ID + ">"))
				if ( Integer.valueOf(s.replaceAll("[^0-9]+", " ").replaceAll("\\s", "")) > id)
					id = Integer.valueOf(s.replaceAll("[^0-9]+", " ").replaceAll("\\s", ""));
		return id;
	}

	private void updateListViewFromAlarmList()
	{
		list=(ListView)this.getActivity().findViewById(R.id.list);
		// Getting adapter by passing xml data ArrayList
		adapter = new LazyAdapter(this.getActivity(), this, this.alarmList);        
		list.setAdapter(adapter);
	}

	private void updateListViewFromFile()
	{
		//parsing del file xml che contiene le sveglie
		this.alarmList = new LinkedList<Alarm>();

		this.populateAlarmListFromFile();				
		list = (ListView)this.getActivity().findViewById(R.id.list);
		// Getting adapter by passing xml data ArrayList
		adapter = new LazyAdapter(this.getActivity(), this, alarmList);        
		list.setAdapter(adapter);
	}

	private void populateAlarmListFromFile()
	{
		XMLParser parser = new XMLParser();
		Context context = this.getActivity().getApplicationContext();
		
		String xml ;
		xml = parser.getXmlFromPath(Constants.XML_PATH_ALARM, context);
		if (xml.equals(""))
		{	
			Log.i("home - customized lst view", "xml file non esistente o vuoto");
			this.initializeAlarmXML();
			return;
		}
		else
		{
			Document doc = parser.getDomElement(xml); // getting DOM element
			NodeList nl = doc.getElementsByTagName(Constants.XML_TAG_ALARM);
	
			for (int i = 0; i < nl.getLength(); i++) {
				// creating new HashMap: single alarm
				Element e = (Element) nl.item(i);
				// adding each child node to HashMap key => value
				String time   =  parser.getValue(e, Constants.XML_TAG_HOUR).replaceAll("\\s","");
				String days   =  parser.getValue(e, Constants.XML_TAG_DAYS).replaceAll("\\s","");
				String bird   =  parser.getValue(e, Constants.XML_TAG_BIRD).replaceAll("\\s","");
				String active =  parser.getValue(e, Constants.XML_TAG_SWITCH).replaceAll("\\s","");
				String id     =  parser.getValue(e, Constants.XML_TAG_ID).replaceAll("\\s","");
	
				Alarm usrAlarm = new Alarm(days,
						Boolean.valueOf(active),
						Boolean.valueOf(true),
						strTimeToHour(time),
						strTimeToMinute(time),
						bird,
						Integer.valueOf(id));
				this.alarmList.add(usrAlarm);
			}
		}
	}

	private int strTimeToHour(String time)
	{
		return Integer.parseInt((time.split(":")[0]));
	}

	private int strTimeToMinute(String time)
	{
		return Integer.parseInt((time.split(":")[1]));
	}

	private void initializeAlarmXML()
	{
		Log.i("inizialize ",  "creo il nuovo file xml");

		FileOutputStream outputStream;

		String intestazione = "<" + Constants.XML_TAG_ALARM + ">";
		String fine = "</" + Constants.XML_TAG_ALARM + ">";

		try {
			outputStream = this.getActivity().openFileOutput(Constants.XML_PATH_ALARM, Context.MODE_PRIVATE);
			outputStream.write(intestazione.getBytes());
			outputStream.write("\n".getBytes());
			outputStream.write(fine.getBytes());
			outputStream.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		Log.i("inizialize ",  "file creato ");
	}

	private List<String> getAlarmsContent()
	{
		List<String> fileContent = new LinkedList<String>();
		Context context = this.getActivity().getApplicationContext();
		try {
			FileInputStream fis = context.openFileInput(Constants.XML_PATH_ALARM);
			InputStreamReader isr = new InputStreamReader(fis);
			BufferedReader bufferedReader = new BufferedReader(isr);
			String line;
			while ((line = bufferedReader.readLine()) != null) {
				fileContent.add(line);
			}
			isr.close();
			fis.close();
			bufferedReader.close();
		}
		catch(Exception e)
		{
			Log.i("customized list view", "something screwed during file reading");
		}
		return fileContent;
	}

	private void removeLastLineAlarms() //open file alarms.xml remove last line and saves it
	{  
		List<String> fileContent =  this.getAlarmsContent();
		fileContent.remove(fileContent.size() - 1);

		FileOutputStream outputStream;
		try {
			outputStream = this.getActivity().openFileOutput(Constants.XML_PATH_ALARM, Context.MODE_PRIVATE );
			for(String s : fileContent)
			{
				outputStream.write(s.getBytes());
				outputStream.write("\n".getBytes());
			}
			outputStream.close();
		} catch (Exception e) { /* TODO */ }
	}

	private void addNewAlarmClock(Alarm ua)
	{
		this.addNewAlarmClock(ua.isActive(),
				ua.getStringTime(), 
				AlarmUtils.daysStringToBooleanArray(ua.getDays()),
				ua.getBird(),
				ua.getId());
	}

	/**
	 * 1. Adds the alarm to XML file saved in the phone
	 * 2. Set the alarm, a service will awake the phone at a given time
	 */
	private void addNewAlarmClock(boolean active, String alarmTime, boolean[] activeDays, String bird, int id)
	{
		Alarm ua = new Alarm(Arrays.toString(activeDays), active, true,
				strTimeToHour(alarmTime), strTimeToMinute(alarmTime), bird, id);
		AlarmHandler.setAlarm(ua, this.getActivity().getApplicationContext(), this.getActivity());

		this.removeLastLineAlarms();  //remove  </alarms>

		Log.i(" add new alarm ", alarmTime);
		Log.i(" add new alarm - active days ", Arrays.toString(activeDays));

		FileOutputStream outputStream;
		try {
			outputStream = this.getActivity().openFileOutput(Constants.XML_PATH_ALARM, Context.MODE_PRIVATE | Context.MODE_APPEND);
			this.writeAlarmFields(outputStream, active, alarmTime, activeDays, bird, id);   //xml fields for alarm
			outputStream.write(("</" + Constants.XML_TAG_ALARM + ">").getBytes());  // re-add </alarms>
			outputStream.close();
		} catch (Exception e) { /* TODO */ }
	}

	private void writeAlarmFields(FileOutputStream outputStream, boolean active,	
			String alarmTime, boolean[] activeDays, String bird, int id) throws IOException
	{
		outputStream.write(("\n   <" + Constants.XML_TAG_ALARM + "> 	\n").getBytes());
		outputStream.write(("        <" + Constants.XML_TAG_ID + "> " +  id + " </" + Constants.XML_TAG_ID + ">\n" ).getBytes());
		outputStream.write(("        <" + Constants.XML_TAG_HOUR + "> " + alarmTime + " </" + Constants.XML_TAG_HOUR + ">\n" ).getBytes());
		outputStream.write(("        <" + Constants.XML_TAG_DAYS + "> ").getBytes());
		outputStream.write(("              " + Arrays.toString(activeDays)).getBytes());
		outputStream.write(("        </" + Constants.XML_TAG_DAYS + "> \n").getBytes());
		outputStream.write(("        <" + Constants.XML_TAG_SWITCH + ">" +  String.valueOf(active)   +  "</" + Constants.XML_TAG_SWITCH + ">"  +  "\n").getBytes());
		outputStream.write(("        <" + Constants.XML_TAG_BIRD + "> " + bird + " </" + Constants.XML_TAG_BIRD + ">\n" ).getBytes() );
		outputStream.write(("   </" + Constants.XML_TAG_ALARM + ">\n ").getBytes());
	}

	@Override
	public void onUndo(Parcelable token) {
		Log.i("undo!", " undo.. ");
		this.addNewAlarmClock(this.lastUARemoved);
		this.alarmList.add(this.lastUARemoved);
		this.updateListViewFromAlarmList();
	}

	@Override
	public void onHide() { /* TODO Auto-generated method stub */ }
}