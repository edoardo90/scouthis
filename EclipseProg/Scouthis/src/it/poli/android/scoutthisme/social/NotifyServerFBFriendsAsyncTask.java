package it.poli.android.scoutthisme.social;

import it.poli.android.scoutthisme.constants.Constants;
import it.poli.android.scoutthisme.constants.UserInformation;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.util.Log;

public class NotifyServerFBFriendsAsyncTask extends AsyncTask<String, Void, String>
{
	@Override
    protected String doInBackground(String... urls) {
          
        return getStringFromUrl(urls[0]) + "@@@@@@" + getStringFromUrl(urls[1]); 
    }
        
    private List<String> getMyFriendsFromJSON(JSONObject job)
    {
    	List<String> myFriends = new ArrayList<String>();
    	String id = ""; String name = "";
    	try {
    		JSONArray jarr = job.getJSONArray("data");
    		
    		for(int i =0; i<jarr.length(); i++)
    		{
    			JSONObject usr = jarr.getJSONObject(i);
    			id =  usr.getString("id");
    			name =  usr.getString("name");
    			myFriends.add(id + "@@@@" + name);
    		}
    	} catch (JSONException e) {
			e.printStackTrace();
		}
    	
    	return myFriends;
    }
        
        
    
    // onPostExecute displays the results of the AsyncTask.
    @Override
    protected void onPostExecute(String result)
    {    	  
    	String listOfFriendsJson = result.split("@@@@@@")[0];
    	String userInfoJson = result.split("@@@@@@")[1];
	  
    	String userId = this.getUserIDFromJson(userInfoJson, UserInformation.USERID);
	  
    	Log.i("user", userId);
	  
    	List<String> myFriends = null;
	  
    	JSONObject jsonMyFriends = null;
		try {
			jsonMyFriends = new JSONObject(listOfFriendsJson);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	    	  
		myFriends = this.getMyFriendsFromJSON(jsonMyFriends);
		UpdateFriendsList ufl = new UpdateFriendsList(listOfFriendsJson, userId, Constants.urlToUpdateFriends);
		ufl.execute();
	}


	private String getStringFromUrl(String url)
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
	    BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));
	    
	    String line = "", result = "";
	    while((line = bufferedReader.readLine()) != null) {
	        result += line;
	    }
	    inputStream.close();
	    
	    return result;
	}

	private String getUserIDFromJson(String userInfoJson, UserInformation infoToGet)
	{
		String s = "";
		try {
			JSONObject job = new JSONObject(userInfoJson);
			s = job.getString("id");
			if(infoToGet == UserInformation.USERID_AND_NAME)
			{
				s = s + " " + job.get("first_name") + " " + job.get("last_name");
			}			
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		return s;
	}
}
