package it.poli.android.scoutthisme.social;

import it.poli.android.scoutthisme.Constants;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.util.Log;

public class NotifyFriendsAsyncTask extends AsyncTask<String, Void, String>
{
	String listOfFriendsJson;
	String userInfoJson;
	String userId;
	
	@Override
    protected String doInBackground(String... urls)
	{
		listOfFriendsJson = getContentFromUrl(urls[0]);
		
		userInfoJson = getContentFromUrl(urls[1]);
		userId = getUserInfoFromJson(userInfoJson)[0];
		
		//BEGIN onPostExecute
    	JSONParser jsonParser = new JSONParser();
    	
		// Building Parameters for POST METHOD 
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair(Constants.PARAM_FRIENDSLIST, listOfFriendsJson));
		params.add(new BasicNameValuePair(Constants.PARAM_USERID, userId));

		// getting JSON Object FROM PHP PAGE, WE UPDATE FRIENDS LIST USING PHP PAGE
		JSONObject json = jsonParser.makeHttpRequest(Constants.URL_SEND_FRIENDSLIST, "POST", params);
		if(json != null)
		{
			// check for success tag
			try {
				json.getInt("success");
			} catch (JSONException e) {
				e.getLocalizedMessage();
			}
		}
		//END onPostExecute
		
		return null;
    }

	private String getContentFromUrl(String url)
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
			} else {
				result = "Did not work!";
			}
		} catch (Exception e) {
			Log.d("InputStream", e.getLocalizedMessage());
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

	private String[] getUserInfoFromJson(String userInfoJson)
	{
		String s[] = new String[3];
		try {
			JSONObject job = new JSONObject(userInfoJson);
			s[0] = job.getString("id");
			s[1] = job.get("first_name").toString();
			s[2] = job.get("last_name").toString();		
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return s;
	}
}
