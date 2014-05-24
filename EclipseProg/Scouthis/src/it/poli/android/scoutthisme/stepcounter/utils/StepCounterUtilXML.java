package it.poli.android.scoutthisme.stepcounter.utils;

import it.poli.android.scoutthisme.Constants;
import it.poli.android.scoutthisme.tools.XMLParser;

import java.io.FileOutputStream;
import java.util.LinkedList;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import android.app.Activity;
import android.content.Context;

public class StepCounterUtilXML {

	public static LinkedList<RunEpisode> populateAlarmListFromFile(Activity activity)
	{
		LinkedList<RunEpisode> runEpisodeList = new LinkedList<RunEpisode>();
		XMLParser parser = new XMLParser();
		
		String xml ;
		xml = parser.getXmlFromPath(Constants.XML_PATH_STEPCOUNTER, activity);
		if (xml.equals(""))
		{	
			StepCounterUtilXML.initializeStepCounterXML(activity);
			return null;
		}
		else
		{
			Document doc = parser.getDomElement(xml); // getting DOM element
			NodeList nl = doc.getElementsByTagName(Constants.XML_TAG_RUNEPISODE);
			for (int i = 0; i < nl.getLength(); i++) {
				// creating new HashMap: single alarm
				Element e = (Element) nl.item(i);
				// adding each child node to HashMap key => value
				String id   =  parser.getValue(e, Constants.XML_TAG_ID).replaceAll("\\s","");
				String distance =  parser.getValue(e, Constants.XML_TAG_DISTANCE).replaceAll("\\s","");
				String steps   =  parser.getValue(e, Constants.XML_TAG_STEPS).replaceAll("\\s","");
				String time   =  parser.getValue(e, Constants.XML_TAG_TIME).replaceAll("\\s","");
				String speed =  parser.getValue(e, Constants.XML_TAG_SPEED).replaceAll("\\s","");
	
				RunEpisode rep = new RunEpisode(id, distance, steps, time, speed);
				runEpisodeList.add(rep);
			}
		}
		return runEpisodeList;
	}

	
	public static void initializeStepCounterXML(Activity activity)
	{
		FileOutputStream outputStream;

		String intestazione = "<" + Constants.XML_TAG_RUNEPISODES + ">";
		String fine = "</" + Constants.XML_TAG_RUNEPISODES + ">";

		try {
			outputStream = activity.openFileOutput(Constants.XML_PATH_STEPCOUNTER, Context.MODE_PRIVATE);
			outputStream.write(intestazione.getBytes());
			outputStream.write("\n".getBytes());
			outputStream.write(fine.getBytes());
			outputStream.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	
}
