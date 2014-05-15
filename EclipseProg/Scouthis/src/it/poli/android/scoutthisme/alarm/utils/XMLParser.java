package it.poli.android.scoutthisme.alarm.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import android.content.Context;
import android.util.Log;

public class XMLParser
{
	public String getXmlFromPath(String fileName, Context context)
	{
		File fileXML = new File(context.getFilesDir() + "/" + fileName);
		StringBuilder total = new StringBuilder();
		if (fileXML.isFile())
		{
			try{
				FileInputStream inputStream;
				inputStream = context.openFileInput(fileName);
				BufferedReader r = new BufferedReader(new InputStreamReader(inputStream));
				String line;
				while ((line = r.readLine()) != null)
					total.append(line);
			}
			catch (Exception e) { Log.i("getXml from path", "something wrong happened while reading xml file"); };
		}	
		else
			Log.i(" xml parser" , " non trovo il file sveglia"); //TODO
		return total.toString();
	}
	
	/**
	 * Getting XML DOM element
	 * @param XML string
	 * */
	public Document getDomElement(String xml){
		Document doc = null;
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		try {
			DocumentBuilder db = dbf.newDocumentBuilder();
			InputSource is = new InputSource();
			is.setCharacterStream(new StringReader(xml));
			doc = db.parse(is); 
		} catch (ParserConfigurationException e) {
			Log.e("Error: ", e.getMessage());
			return null;
		} catch (SAXException e) {
			Log.e("Error: ", e.getMessage());
			return null;
		} catch (IOException e) {
			Log.e("Error: ", e.getMessage());
			return null;
		}
	    return doc;
	}
	
	 /**
	  * Getting node value
	  * @param Element node
	  * @param key string
	  * */
	 public String getValue(Element item, String str) {
			NodeList n = item.getElementsByTagName(str);	
			return this.getElementValue(n.item(0));
	}
	
	 private final String getElementValue(Node elem) {
	     Node child;
	     if( elem != null){
	         if (elem.hasChildNodes()){
	             for( child = elem.getFirstChild(); child != null; child = child.getNextSibling() ){
	                 if( child.getNodeType() == Node.TEXT_NODE  ){
	                     return child.getNodeValue();
	                 }
	             }
	         }
	     }
	     return "";
	 }
}