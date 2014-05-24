package it.poli.android.scoutthisme.tools;

import it.poli.android.scoutthisme.Constants;
import it.poli.android.scoutthisme.alarm.utils.AlarmUtils;

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

	public static void removeLastLineXmlFile(Activity activity, String fileName) 
	{  
		List<String> fileContent = getFileContent(activity, fileName);
		fileContent.remove(fileContent.size() - 1);

		FileOutputStream outputStream;
		try {
			outputStream = activity.openFileOutput(fileName, Context.MODE_PRIVATE );
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
		List<String> fileContent =  getFileContent(activity, fileName);
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

		List<String> fileContent =  getFileContent(activity, fileName);
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


	public static void initializeXML(Activity activity, String rootXmlFile, String xmlFilePath)
	{
		Log.i("inizialize ",  "creo il nuovo file xml");

		FileOutputStream outputStream;

		String intestazione = "<" + rootXmlFile + ">";
		String fine = "</" + rootXmlFile + ">";

		try {
			outputStream = activity.openFileOutput(xmlFilePath, Context.MODE_PRIVATE);
			outputStream.write(intestazione.getBytes());
			outputStream.write("\n".getBytes());
			outputStream.write(fine.getBytes());
			outputStream.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	
	public static void appendXmlElement(Activity activity, String xmlPath,
			Map<String, String> elementAttributesMap, String rootXmlDocument) {

		
		List<String> fcont = getFileContent(activity, xmlPath);
		if (fcont.size() == 0)
		{	
			initializeXML(activity, rootXmlDocument, xmlPath);
		}
		
		fcont = getFileContent(activity, xmlPath);
		removeLastLineXmlFile(activity, xmlPath);  //remove  </alarms> or </rootwhatever>
		FileOutputStream outputStream;
		try {
			outputStream = activity.openFileOutput(xmlPath, Context.MODE_PRIVATE | Context.MODE_APPEND);

			writeAttributeMapOnXML(outputStream, elementAttributesMap);   

			outputStream.write(("</" + rootXmlDocument + ">").getBytes());  // re-add </alarms>
			outputStream.close();
		} catch (Exception e) { /* TODO */ }
	}


	private static void writeAttributeMapOnXML(FileOutputStream outputStream, Map<String, String> mapElements)
			throws IOException
			{

		outputStream.write(("\n   <" + mapElements.get(Constants.XML_ROOT_MAP) + "> 	\n").getBytes());
		for( Entry<String, String> elementField : mapElements.entrySet())
		{

			String elemChildName = elementField.getKey();
			String elemChildValue = elementField.getValue();
			if( ! elemChildName.equalsIgnoreCase(Constants.XML_ROOT_MAP))
			{
				outputStream.write(("        <" + elemChildName + "> " +  
						elemChildValue 
						+ " </" + elemChildName + ">\n" ).getBytes());
			}

		}

		outputStream.write(("\n   </" + mapElements.get(Constants.XML_ROOT_MAP) + "> 	\n").getBytes());
			}

	public static int getLastIdFromXml(Activity mAct, String fileName)
	{
		List<String> xmlFile =  getFileContent(mAct, fileName);
		int id = 0;
		for(String s : xmlFile)
			if (s.contains("<" + Constants.XML_TAG_ID + ">"))
				if ( Integer.valueOf(s.replaceAll("[^0-9]+", " ").replaceAll("\\s", "")) > id)
					id = Integer.valueOf(s.replaceAll("[^0-9]+", " ").replaceAll("\\s", ""));
		return id;
	}
}







