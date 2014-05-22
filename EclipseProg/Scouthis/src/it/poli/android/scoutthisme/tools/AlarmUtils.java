package it.poli.android.scoutthisme.tools;

import it.poli.android.scoutthisme.Constants;
import it.poli.android.scoutthisme.alarm.utils.XMLParser;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

public class AlarmUtils
{
	public static void CopyStream(InputStream inputStream, OutputStream outputStream)
	{
		final int buffer_size = 1024;
		try
		{
			byte[] bytes = new byte[buffer_size];
			for(;;)
			{
				int count = inputStream.read(bytes, 0, buffer_size);
				if(count == -1)
					break;
				outputStream.write(bytes, 0, count);
			}
		}
		catch(Exception ex) { } //TODO
	}

	public static boolean[] daysStringToBooleanArray(String days)
	{
		days = days.substring(1, days.length() - 1);
		String[] bdays = days.split(",");
		boolean[] bbdays = {false, false, false, false, false, false, false};
		for(int i = 0; i < 7; i++)
			if(bdays[i].equalsIgnoreCase("true"))
				bbdays[i] = true;
		return bbdays;
	}
	
	public static String addZeroToOneDigit(int i)
	{
		String s  = String.valueOf(i);
		return (s.length() == 1 ? "0" + s : s);
	}
	
	/* --- USED BY AlarmsHomeFragment.java --- */
	
	public static void initializeAlarmXML(Activity activity)
	{
		Log.i("inizialize ",  "creo il nuovo file xml");

		FileOutputStream outputStream;

		String intestazione = "<" + Constants.XML_TAG_ALARMS + ">";
		String fine = "</" + Constants.XML_TAG_ALARMS + ">";

		try {
			outputStream = activity.openFileOutput(Constants.XML_PATH_ALARM, Context.MODE_PRIVATE);
			outputStream.write(intestazione.getBytes());
			outputStream.write("\n".getBytes());
			outputStream.write(fine.getBytes());
			outputStream.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		Log.i("inizialize ",  "file creato ");
	}
	
	public static List<String> getAlarmsContent(Activity activity)
	{
		List<String> fileContent = new LinkedList<String>();
		//Context context = this.getActivity().getApplicationContext();
		try {
			FileInputStream fis = activity.openFileInput(Constants.XML_PATH_ALARM);
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
	
	private static void removeLastLineAlarms(Activity activity) //open file alarms.xml remove last line and saves it
	{  
		List<String> fileContent = getAlarmsContent(activity);
		fileContent.remove(fileContent.size() - 1);

		FileOutputStream outputStream;
		try {
			outputStream = activity.openFileOutput(Constants.XML_PATH_ALARM, Context.MODE_PRIVATE );
			for(String s : fileContent)
			{
				outputStream.write(s.getBytes());
				outputStream.write("\n".getBytes());
			}
			outputStream.close();
		} catch (Exception e) { /* TODO */ }
	}
	
	/**
	 * 1. Adds the alarm to XML file saved in the phone
	 * 2. Set the alarm, a service will awake the phone at a given time
	 * @param ua
	 */
	public static void addNewAlarmClock(Alarm ua, Activity activity)
	{
		addNewAlarmClock(ua.isActive(),
				ua.getStringTime(), 
				AlarmUtils.daysStringToBooleanArray(ua.getDays()),
				ua.getBird(),
				ua.getId(), activity);
	}

	/**
	 * 1. Adds the alarm to XML file saved in the phone
	 * 2. Set the alarm, a service will awake the phone at a given time
	 * @param active
	 * @param alarmTime
	 * @param activeDays
	 * @param bird
	 * @param id
	 */
	public static void addNewAlarmClock(boolean active, String alarmTime, boolean[] activeDays, String bird, int id, Activity activity)
	{
		Alarm ua = new Alarm(Arrays.toString(activeDays), active, true,
				AlarmUtils.strTimeToHour(alarmTime), AlarmUtils.strTimeToMinute(alarmTime), bird, id);
		AlarmHandler.setAlarm(ua, activity);

		removeLastLineAlarms(activity);  //remove  </alarms>

		Log.i(" add new alarm ", alarmTime);
		Log.i(" add new alarm - active days ", Arrays.toString(activeDays));

		FileOutputStream outputStream;
		try {
			outputStream = activity.openFileOutput(Constants.XML_PATH_ALARM, Context.MODE_PRIVATE | Context.MODE_APPEND);
			writeAlarmOnXML(outputStream, active, alarmTime, activeDays, bird, id);   //xml fields for alarm
			outputStream.write(("</" + Constants.XML_TAG_ALARMS + ">").getBytes());  // re-add </alarms>
			outputStream.close();
		} catch (Exception e) { /* TODO */ }
	}

	/**
	 * Write alarm fields on XML file
	 * @param outputStream
	 * @param active
	 * @param alarmTime
	 * @param activeDays
	 * @param bird
	 * @param id
	 * @throws IOException
	 */
	private static void writeAlarmOnXML(FileOutputStream outputStream, boolean active,	
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
	
	public static void writeListOnXML(List<String> fileContent, Activity activity)
	{
		FileOutputStream outputStream;
		String fileName = Constants.XML_PATH_ALARM;
		try {
			outputStream = activity.openFileOutput(fileName, Context.MODE_PRIVATE );
			for(String fileRow : fileContent)
			{
				outputStream.write(fileRow.getBytes());
				outputStream.write("\n".getBytes());
			}
			outputStream.close();
		} catch (Exception e) { /* TODO */ }
	}
	
	public static LinkedList<Alarm> populateAlarmListFromFile(Activity activity)
	{
		LinkedList<Alarm> alarmList = new LinkedList<Alarm>();
		XMLParser parser = new XMLParser();
		
		String xml ;
		xml = parser.getXmlFromPath(Constants.XML_PATH_ALARM, activity);
		if (xml.equals("")/* || xml.equals("<" + Constants.XML_TAG_ALARM + "></" + Constants.XML_TAG_ALARM + ">")*/)
		{	
//			File file = new File("alarms.xml");
//			file.delete();
			Log.i("home - customized lst view", "xml file non esistente o vuoto");
			AlarmUtils.initializeAlarmXML(activity);
			return null;
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
						AlarmUtils.strTimeToHour(time),
						AlarmUtils.strTimeToMinute(time),
						bird,
						Integer.valueOf(id));

				alarmList.add(usrAlarm);
			}
		}
		return alarmList;
	}
	
	public static int strTimeToHour(String time)
	{
		return Integer.parseInt((time.split(":")[0]));
	}

	public static int strTimeToMinute(String time)
	{
		return Integer.parseInt((time.split(":")[1]));
	}
	
	/* --- USED BY AlarmsSetClockFragment.java --- */
	
	public static int dayToPos(String day)
	{
		if (day.equals("txtLun"))
			return 0;
		if (day.equals("txtMar"))
			return 1;
		if (day.equals("txtMer"))
			return 2;
		if (day.equals("txtGiov"))
			return 3;
		if (day.equals("txtVen"))
			return 4;
		if (day.equals("txtSab"))
			return 5;
		if (day.equals("txtDom"))
			return 6;		
		return -44;
	}
	
	public static String getCalendarTime(Calendar mCalendar) {
		Date date = mCalendar.getTime();
		if (date != null) {
			SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yy '@' h:mm a", Locale.US);
			String dateTime = formatter.format(date);
			return dateTime.substring(dateTime.length() - 3 );
		}
		return "";
	}
}