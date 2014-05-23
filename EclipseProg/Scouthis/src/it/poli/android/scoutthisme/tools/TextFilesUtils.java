package it.poli.android.scoutthisme.tools;

import it.poli.android.scoutthisme.Constants;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

public class TextFilesUtils {

	public static List<String> getFileContent(Activity activity, String fileName)
	{
		List<String> fileContent = new LinkedList<String>();
		//Context context = this.getActivity().getApplicationContext();
		try {
			FileInputStream fis = activity.openFileInput(fileName);
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

	public static void writeListOnXML(Activity activity, String fileName, List<String> fileContent)
	{
		FileOutputStream outputStream;

		try {
			outputStream = activity.openFileOutput(fileName, Context.MODE_PRIVATE );
			for(String fileRow : fileContent)
			{
				outputStream.write(fileRow.getBytes());
				outputStream.write("\n".getBytes());
			}
			outputStream.close();
		} catch (Exception e) { /*  */ }
	}

	public static void removeLastLineAlarms(Activity activity, String fileName) 
	{  
		List<String> fileContent = getFileContent(activity, fileName);
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
		} catch (Exception e) { /*  */ }
	}

	public static void changeXmlChildValue(Activity activity, String fileName, String newValue, int positionToDelete,
			String tagElementToEdit, String childElementToEdit)
	{
		List<String> fileContent =  AlarmUtils.getAlarmsContent(activity);
		List<String> fileNewContent = new ArrayList<String>();

		String rowClean = "";
		int pos = 0;
		boolean editing = false;
		for(String row : fileContent)
		{
			rowClean = row.replaceAll("\\s","");
			if (rowClean.equals("<" + tagElementToEdit + ">") && positionToDelete == pos)
				editing = true;

			if(rowClean.equals("<" + tagElementToEdit + ">"))
				pos++;

			if (!editing)
				fileNewContent.add(rowClean);
			else
			{
				if(rowClean.contains(childElementToEdit))
					fileNewContent.add("<" + childElementToEdit + ">" + newValue + "</" + childElementToEdit + ">");
				else
					fileNewContent.add(rowClean);

			}
			if (rowClean.equals("</" + tagElementToEdit + ">") )
				editing = false;
		}
		TextFilesUtils.writeListOnXML(activity, fileName, fileNewContent);
	}

	public static void removeXmlElement(Activity activity, String fileName, int positionToDelete,
			String xmlTagToRemove) {

		List<String> fileContent =  AlarmUtils.getAlarmsContent(activity);
		List<String> fileNewContent = new ArrayList<String>();

		String rowClean = "";
		int /*id = 0, */pos = 0;
		boolean skipping = false;
		for(String row : fileContent)
		{
			rowClean = row.replaceAll("\\s","");
			if (rowClean.equals("<" + xmlTagToRemove + ">") && positionToDelete == pos)
				skipping = true;

			if(rowClean.equals("<" + xmlTagToRemove + ">"))
				pos++;

			if (!skipping)
				fileNewContent.add(rowClean);


			if (rowClean.equals("</" + xmlTagToRemove + ">") )
				skipping = false;
		}
		TextFilesUtils.writeListOnXML(activity, fileName, fileNewContent);

	}


	public static void appendXmlElement(Activity activity, String xmlPath,
			Map<String, String> alarmMap, String rootXmlDocument) {

		removeLastLineAlarms(activity, xmlPath);  //remove  </alarms>
		FileOutputStream outputStream;
		try {
			outputStream = activity.openFileOutput(xmlPath, Context.MODE_PRIVATE | Context.MODE_APPEND);

			writeAlarmOnXML(outputStream, alarmMap);   

			outputStream.write(("</" + rootXmlDocument + ">").getBytes());  // re-add </alarms>
			outputStream.close();
		} catch (Exception e) { /* TODO */ }
	}


	private static void writeAlarmOnXML(FileOutputStream outputStream, Map<String, String> mapElements)
			throws IOException
	{

		outputStream.write(("\n   <" + mapElements.get("elementName") + "> 	\n").getBytes());
		for( Entry<String, String> elementField : mapElements.entrySet())
		{

			String elemChildName = elementField.getKey();
			String elemChildValue = elementField.getValue();
			outputStream.write(("        <" + elemChildName + "> " +  
					elemChildValue 
					+ " </" + elemChildName + ">\n" ).getBytes());

		}

		outputStream.write(("\n   <" + mapElements.get("elementName") + "> 	\n").getBytes());
	}


}







