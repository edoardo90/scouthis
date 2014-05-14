package it.poli.android.scoutthisme.fragments;

import it.poli.alarmb.LazyAdapter;
import it.poli.alarmb.XMLParser;
import it.poli.alarmb.alarmstime.AlarmsTool;
import it.poli.alarmb.alarmstime.UserAlarm;
import it.poli.android.scouthisme.R;
import it.poli.android.scoutthisme.Constants;
import it.poli.android.scoutthisme.ScoutMiniAppEnum;
import it.poli.android.scoutthisme.tools.Utils;

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

import com.jensdriller.libs.undobar.UndoBar;
import com.jensdriller.libs.undobar.UndoBar.Listener;

public class AlarmsHomeFragment extends Fragment implements Listener {

	// XML node keys
	static final String KEY_ALARM_ROOT = "alarm"; // parent node

	static final String KEY_DAYS = "days";
	static final String KEY_TIME = "hour";
	static final String KEY_BIRD = "bird";
	static final String KEY_SWITCH = "switch";
	static final String KEY_ID = "id";

	private final String ALARMS_FILE_NAME = "alarms.xml";


	ListView list;
	LazyAdapter adapter;
	LinkedList<UserAlarm> alarmList;

	private UserAlarm lastUARemoved;
	
	private void addAlarmClock(View view)
	{
		Log.i("custom view" , "cliccatomi!");
		
		
		/*for(String tagFr : Constants.fragmentMap.keySet())
		{
			Fragment fragToDet = this.getActivity().getSupportFragmentManager().findFragmentByTag(tagFr);
			if ( fragToDet != null)
				transaction.remove(fragToDet);
				
		}*/
		
		Fragment setAlarmClock = new SetAlarmClockFragment();
		FragmentTransaction transaction = getFragmentManager().beginTransaction();

		
		// Replace whatever is in the fragment_container view with this fragment,
		// and add the transaction to the back stack
		transaction.replace(R.id.pager, setAlarmClock);
		transaction.addToBackStack(null);

		// Commit the transaction
		transaction.commit();
		
		
		/*transaction.add(android.R.id.content, setAlarmClock,  ScoutMiniAppEnum.WakeSet.toString());
		transaction.attach( setAlarmClock);
		transaction.show( setAlarmClock);
		transaction.commit();
		*/

		
	}

	


	int getPosFromView(View v)
	{	return  ((ListView)this.getActivity().findViewById(R.id.list)).getPositionForView(v);  }

	public void switchAlarm(View v)
	{
		//prende la sveglia in posizione corrispondente alla view  
		//(es. se v fosse la riga della terza immagine, prenderebbe la sveglia 3)
		//e la spegne se accesa o viceversa
		int pos = getPosFromView(v);
		UserAlarm alarm = this.alarmList.get(pos);
		alarm.toggleActive();
		boolean newActive = alarm.isActive();
		this.alarmList.set(pos, alarm);
		this.updateListViewFromAlarmList();



		int id = this.setAlarmFileActive(newActive, pos);
		this.updatePhisicalAlarm(this.alarmList.get(pos), id);
	}


	private void updatePhisicalAlarm(UserAlarm uAlarm, int id) {
		AlarmsTool.setAlarmFor(uAlarm, this.getActivity().getApplicationContext(), this.getActivity());

	}



	private int setAlarmFileActive(boolean newActive, int positionToDelete)
	{
		List<String> fileContent =  this.getAlarmsContent();

		List<String> fileNewContent = new ArrayList<String>();

		String rowClean = "";
		int id=0;
		int pos = 0; boolean editing = false;
		for(String row : fileContent )
		{
			rowClean = row.replaceAll("\\s","");


			if (rowClean.equals("<alarm>") && positionToDelete == pos)
			{
				editing = true;
			}
			if(rowClean.equals("<alarm>"))
			{
				pos++;
			}

			if (!editing)
				fileNewContent.add(rowClean);
			else
			{
				if(rowClean.contains(AlarmsHomeFragment.KEY_SWITCH))
					fileNewContent.add("<switch>" + newActive + "</switch>");
				else
				{
					fileNewContent.add(rowClean);
					if(rowClean.contains(KEY_ID))
						id = Integer.valueOf(rowClean.replaceAll("[^0-9]+", " ").replaceAll("\\s", "")); 
				}
			}
			if (rowClean.equals("</alarm>") )
			{
				editing = false;
			}

		}

		this.writeListOnFile(fileNewContent);

		return id;
	}


	public void deleteAlarm(View v)
	{
		Log.i("delete ", " delete");

		ListView lview = (ListView)this.getActivity().findViewById(R.id.list);
		int position = lview.getPositionForView(v);

		Log.i(" delete " , " pos : " + position);
		this.lastUARemoved = this.alarmList.get(position);
		AlarmsTool.removeAlarmFor(this.lastUARemoved, 
				this.getActivity().getApplicationContext(), this.getActivity());
		this.deleteAlarmInPosition(position);
		this.updateListViewFromFile();
		new UndoBar.Builder(this.getActivity())//
		.setMessage("Sveglia cancellata")//
		.setListener(this)//
		.show();


	}

	private int deleteAlarmInPosition(int positionToDelete) {

		List<String> fileContent =  this.getAlarmsContent();

		List<String> fileNewContent = new ArrayList<String>();

		String rowClean = "";
		int id=0;
		int pos = 0; boolean skipping = false;
		for(String row : fileContent )
		{
			rowClean = row.replaceAll("\\s","");


			if (rowClean.equals("<alarm>") && positionToDelete == pos)
			{
				skipping = true;
			}
			if(rowClean.equals("<alarm>"))
			{
				pos++;
			}

			if (!skipping)
				fileNewContent.add(rowClean);
			else
			{
				if(rowClean.contains(KEY_ID))
					id = Integer.valueOf(rowClean.replaceAll("[^0-9]+", " ").replaceAll("\\s", ""));
			}

			if (rowClean.equals("</alarm>") )
			{
				skipping = false;
			}

		}

		this.writeListOnFile(fileNewContent);
		return id;
	}

	private void writeListOnFile(List<String> fileContent)
	{
		FileOutputStream outputStream;
		String fileName = this.ALARMS_FILE_NAME;
		try {
			outputStream = this.getActivity().openFileOutput(fileName, Context.MODE_PRIVATE );

			for(String fileRow : fileContent)
			{
				outputStream.write(fileRow.getBytes());
				outputStream.write("\n".getBytes());
			}

			outputStream.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		super.onCreateView(inflater, container, savedInstanceState);
		View rootView = inflater.inflate(R.layout.fragment_alarms_home, container, false);
		
        return rootView;
	}
	
	@Override
	 public void onResume() {
	        
		super.onResume();
		Log.i(" Alarms Home ", " Alarms Home ");
		
		ImageView addAlarm = (ImageView)this.getActivity().findViewById(R.id.addAlarmImg);
		addAlarm.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				addAlarmClock(v);
				
			}
		});
		
		
		
		//se chiamato da "aggiungi sveglia" deve estrarre i dati della nuova sveglia

		Bundle paramAlarm = getArguments();
		if(paramAlarm != null)
		{	
			boolean [] activeDays =  paramAlarm.getBooleanArray(SetAlarmClockFragment.ACTIVE_DAYS_MESSAGE);
			Log.i("CUstom prendo i dati extra", "" + activeDays);
			String alarmTime = paramAlarm.getString(SetAlarmClockFragment.TIME_MESSAGE);
			String bird  = paramAlarm.getString(SetAlarmClockFragment.BIRD);
			boolean active = paramAlarm.getBoolean(SetAlarmClockFragment.ACTIVE_ALARM, true);
			int id = this.getLastIdFromFile(); 
			id++;
			this.addNewAlarmClock(active, alarmTime, activeDays, bird, id);
		}

		this.updateListViewFromFile();


		Log.i("aggiungo listenre", "add lst");

		list=(ListView)this.getActivity().findViewById(R.id.list);


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

	private int getLastIdFromFile() {

		List<String> alarmsFile =  this.getAlarmsContent();
		int id=0;
		for(String s : alarmsFile)
		{
			if (s.contains("<id>"))
			{
				if ( Integer.valueOf(s.replaceAll("[^0-9]+", " ").replaceAll("\\s", "")) > id)
					id = Integer.valueOf(s.replaceAll("[^0-9]+", " ").replaceAll("\\s", ""));

			}
		}



		return id;
	}



	private void updateListViewFromAlarmList()
	{
		list=(ListView)this.getActivity().findViewById(R.id.list);
		// Getting adapter by passing xml data ArrayList
		adapter=new LazyAdapter(this.getActivity(), this, this.alarmList);        
		list.setAdapter(adapter);
	}

	private void updateListViewFromFile()
	{
		//parsing del file xml che contiene le sveglie
		this.alarmList = new LinkedList<UserAlarm>();

		this.populateAlarmListFromFile();				
		list=(ListView)this.getActivity().findViewById(R.id.list);
		// Getting adapter by passing xml data ArrayList
		adapter=new LazyAdapter(this.getActivity(), this, alarmList);        
		list.setAdapter(adapter);

	}


	private void populateAlarmListFromFile()
	{
		XMLParser parser = new XMLParser();
		String xml ;
		Context context = this.getActivity().getApplicationContext();
		xml = parser.getXmlFromPath(this.ALARMS_FILE_NAME, context);

		if (xml.equals(""))
		{	
			Log.i("home - customized lst view", "xml file non esistente o vuoto");
			this.initializeAlarmXML();
			return;
		}
		else
		{ 	    Document doc = parser.getDomElement(xml); // getting DOM element
		NodeList nl = doc.getElementsByTagName(KEY_ALARM_ROOT);

		for (int i = 0; i < nl.getLength(); i++) {
			// creating new HashMap: single alarm

			Element e = (Element) nl.item(i);
			// adding each child node to HashMap key => value
			String time   =  parser.getValue(e, KEY_TIME).replaceAll("\\s","");
			String days   =  parser.getValue(e, KEY_DAYS).replaceAll("\\s","");
			String bird   =  parser.getValue(e, KEY_BIRD).replaceAll("\\s","");
			String active =  parser.getValue(e, KEY_SWITCH).replaceAll("\\s","");
			String id     =  parser.getValue(e, KEY_ID).replaceAll("\\s","");

			UserAlarm usrAlarm = new UserAlarm(days,
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
		return Integer.parseInt(  (time.split(":")[0]));
	}

	private int strTimeToMinute(String time)
	{
		return Integer.parseInt(  (time.split(":")[1]));
	}




	private void initializeAlarmXML() {

		Log.i("inizialize ",  "creo il nuovo file xml");

		String fileName = this.ALARMS_FILE_NAME;

		FileOutputStream outputStream;

		String intestazione = "<alarms>";
		String fine = "</alarms>";

		try {
			outputStream = this.getActivity().openFileOutput(fileName, Context.MODE_PRIVATE);
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

			FileInputStream fis = context.openFileInput(this.ALARMS_FILE_NAME);
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

	private void removeLastLineAlarms() {  //open file alarms.xml remove last line and saves it

		List<String> fileContent =  this.getAlarmsContent();
		fileContent.remove(fileContent.size() - 1);

		String fileName = this.ALARMS_FILE_NAME;

		FileOutputStream outputStream;

		try {
			outputStream = this.getActivity().openFileOutput(fileName, Context.MODE_PRIVATE );

			for(String s : fileContent)
			{
				outputStream.write(s.getBytes());
				outputStream.write("\n".getBytes());
			}


			outputStream.close();

		} catch (Exception e) {
			e.printStackTrace();
		}


	}


	private void addNewAlarmClock(UserAlarm ua)
	{
		this.addNewAlarmClock(ua.isActive(),
				ua.getStringTime(), 
				Utils.strToBAR(ua.getDays()),
				ua.getBird(),
				ua.getId());

	}



	private void addNewAlarmClock(boolean active, String alarmTime, boolean[] activeDays, String bird, int id) {

		UserAlarm ua = new UserAlarm(Arrays.toString(activeDays), active, true, strTimeToHour(alarmTime), strTimeToMinute(alarmTime), bird, id);
		AlarmsTool.setAlarmFor(ua, this.getActivity().getApplicationContext(), this.getActivity());

		this.removeLastLineAlarms();  //remove  </alarms>

		Log.i(" add new alarm ", alarmTime);
		Log.i(" add new alarm - active days ", Arrays.toString(activeDays));

		String fileName = this.ALARMS_FILE_NAME;

		FileOutputStream outputStream;

		try {
			outputStream = this.getActivity().
								openFileOutput(fileName, Context.MODE_PRIVATE | Context.MODE_APPEND);
			this.writeAlarmFields(outputStream, active, alarmTime, activeDays, bird, id);   //xml fields for alarm
			outputStream.write("</alarms>".getBytes());  // re-add </alarms>
			outputStream.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}


	private void writeAlarmFields(FileOutputStream outputStream, boolean active,	
			String alarmTime, boolean[] activeDays, String bird, int id) throws IOException {

		Log.i("asd", " scrivo!");
		outputStream.write("\n   <alarm> 	\n".getBytes());
		outputStream.write( ("        <id> " +  id + " </id>\n" ).getBytes() );
		outputStream.write( ("        <hour> " + alarmTime + " </hour>\n" ).getBytes() );
		outputStream.write(  "        <days> ".getBytes() );
		outputStream.write( ("              " + Arrays.toString(activeDays)).getBytes());
		outputStream.write(  "        </days> \n".getBytes());
		outputStream.write(  ("        <switch>" +  String.valueOf(active)   +  "</switch>"  +  "\n").getBytes());
		outputStream.write( ("        <bird> " + bird + " </bird>\n" ).getBytes() );
		outputStream.write("   </alarm>\n ".getBytes());


	}



	@Override
	public void onHide() {
		// TODO Auto-generated method stub

	}



	@Override
	public void onUndo(Parcelable token) {
		Log.i("undo!", " undo.. ");
		this.addNewAlarmClock(this.lastUARemoved);
		this.alarmList.add(this.lastUARemoved);
		this.updateListViewFromAlarmList();

	}

}