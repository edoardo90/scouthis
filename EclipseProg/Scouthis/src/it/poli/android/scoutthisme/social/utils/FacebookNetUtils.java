package it.poli.android.scoutthisme.social.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

public class FacebookNetUtils
{
	public static String getStringFromUrl(String url)
	{
		InputStream inputStream = null;
		String result = "";
		try {
			// create HttpClient
			HttpClient httpclient = new DefaultHttpClient();
			// make GET request to the given URL
			HttpResponse httpResponse = httpclient.execute(new HttpGet(url));
			// receive response as inputStream
			inputStream = httpResponse.getEntity().getContent();
			// convert inputstream to string
			if(inputStream != null) {
				result = convertInputStreamToString(inputStream);
			}
		} catch (Exception e) {
			
		}
		return result;
	}
	
	private static String convertInputStreamToString(InputStream inputStream) throws IOException
	{
	    String result = "";
	    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
	    
	    String line;
	    while((line = bufferedReader.readLine()) != null) {
	        result += line;
	    }
	    
	    inputStream.close();
	    return result;
	}
}
