package com.example.androidhive;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.AlarmClock;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;

public class CustomizedListView extends Activity {
	// All static variables
	static final String URL = "http://api.androidhive.info/music/music.xml";
	// XML node keys
	static final String KEY_ALARM = "alarm"; // parent node
	
	static final String KEY_DAYS = "days";
	static final String KEY_HOUR = "hour";
	static final String KEY_BIRD = "bird";
	ListView list;
    LazyAdapter adapter;
	private final String ALARMS_FILE_NAME = "alarms.xml";

    
    public void addAlarmClock(View view)
    {
    	Log.i("custom view" , "cliccatomi!");
    	Intent i = new Intent(this, SetAlarmClockActivity.class);
    	startActivity(i);
    }
    
    
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		//se chiamato da "aggiungi sveglia" deve estrarre i dati della nuova sveglia
		Intent intent=null;
		intent = getIntent();
		
		if(intent != null && intent.getExtras() != null)
		{	
			boolean [] activeDays =  intent.getBooleanArrayExtra(SetAlarmClockActivity.ACTIVE_DAYS_MESSAGE);
			Log.i("CUstom prendo i dati extra", "" + activeDays);
			String alarmTime = intent.getStringExtra(SetAlarmClockActivity.TIME_MESSAGE);
			String bird  = intent.getStringExtra(SetAlarmClockActivity.BIRD);
			this.addNewAlarmClock(alarmTime, activeDays, bird);
		}
		
		
		//parsing del file xml che contiene le sveglie
		
		ArrayList<HashMap<String, String>> songsList = new ArrayList<HashMap<String, String>>();

		XMLParser parser = new XMLParser();
		String xml ;///////////!!// = parser.getXmlFromUrl(URL); // getting XML from URL
		
		Context context = getApplicationContext();
		xml = parser.getXmlFromPath(this.ALARMS_FILE_NAME, context);
		
		if (xml.equals(""))
		{	
			
			Log.i("home - customized lst view", "xml file non esistente o vuoto");
			this.initializeAlarmXML();
			return;
		}
		else
		{ 	    Document doc = parser.getDomElement(xml); // getting DOM element
				NodeList nl = doc.getElementsByTagName(KEY_ALARM);
				// looping through all song nodes <alarm>
				for (int i = 0; i < nl.getLength(); i++) {
					// creating new HashMap
					HashMap<String, String> map = new HashMap<String, String>();
					Element e = (Element) nl.item(i);
					// adding each child node to HashMap key => value
					map.put(KEY_HOUR, parser.getValue(e, KEY_HOUR));
					map.put(KEY_DAYS, parser.getValue(e, KEY_DAYS));
					map.put(KEY_BIRD, parser.getValue(e, KEY_BIRD));
					// adding HashList to ArrayList
					songsList.add(map);
				}
		}
		
		list=(ListView)findViewById(R.id.list);
		
		// Getting adapter by passing xml data ArrayList
        adapter=new LazyAdapter(this, songsList);        
        list.setAdapter(adapter);
		
        list.setOnItemLongClickListener((new OnItemLongClickListener() {
    		
            public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
                    int pos, long id) {
                // TODO Auto-generated method stub

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
							Log.i("list view", "hai cliccato l'item" + pos.toString());

			}
		});
		
		
	}

	
	private void initializeAlarmXML() {
		
		Log.i("inizialize ",  "creo il nuovo file xml");
		
		String fileName = this.ALARMS_FILE_NAME;
		
		FileOutputStream outputStream;
		
		String intestazione = "<alarms>";
		String fine = "</alarms>";
		
		try {
		  outputStream = openFileOutput(fileName, Context.MODE_PRIVATE);
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
		Context context = this.getApplicationContext();
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
		  outputStream = openFileOutput(fileName, Context.MODE_PRIVATE );
		  
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

	private void addNewAlarmClock(String alarmTime, boolean[] activeDays, String bird) {
		
		this.removeLastLineAlarms();  //remove  </alarms>
		
		Log.i(" add new alarm ", alarmTime);
		Log.i(" add new alarm - active days ", Arrays.toString(activeDays));
		
		String fileName = this.ALARMS_FILE_NAME;
		
		FileOutputStream outputStream;

		try {
		  outputStream = openFileOutput(fileName, Context.MODE_PRIVATE | Context.MODE_APPEND);
		  this.writeAlarmFields(outputStream, alarmTime, activeDays, bird);   //xml fields for alarm
		  outputStream.write("</alarms>".getBytes());  // re-add </alarms>
		  outputStream.close();
		} catch (Exception e) {
		  e.printStackTrace();
		}
		
	}


	private void writeAlarmFields(FileOutputStream outputStream,	String alarmTime, boolean[] activeDays, String bird) throws IOException {
		
		Log.i("asd", " scrivo!");
		outputStream.write("\n   <alarm> 	\n".getBytes());
		outputStream.write( ("        <hour> " + alarmTime + " </hour>\n" ).getBytes() );
		outputStream.write(  "        <days> ".getBytes() );
		outputStream.write( ("              " + Arrays.toString(activeDays)).getBytes());
		outputStream.write(  "        </days> \n".getBytes());
		outputStream.write( ("        <bird> " + bird + " </bird>\n" ).getBytes() );
		outputStream.write("   </alarm>\n ".getBytes());
		
		
	}


	


	
}