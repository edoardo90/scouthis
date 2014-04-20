package it.poli.android.scoutthisme.social;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

 class UpdateFriendsList extends AsyncTask<String, String, String> {

    private JSONParser jsonParser = new JSONParser();
	private String listOfFriendsJson;
	private String userId;
	private String urlUpdateFriendList;
	
	public UpdateFriendsList(String listOfFriendsJson, String userId, String urlUpdateFriendList)
	{
		this.listOfFriendsJson = listOfFriendsJson;
		this.userId = userId;
		this.urlUpdateFriendList = urlUpdateFriendList;
	}
    
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    protected String doInBackground(String... args) {
        
        // Building Parameters for POST METHOD 
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("jfriendslist", listOfFriendsJson));
		params.add(new BasicNameValuePair("userid", userId));
		
		// getting JSON Object FROM PHP PAGE, WE UPDATE FRIENDS LIST USING PHP PAGE
        JSONObject json = jsonParser.makeHttpRequest(this.urlUpdateFriendList, "POST", params);
        
        // check for success tag
        try {
            int success = json.getInt("success");
            if(success!=0) {Log.i("update flist", "wooo! fatto!");}
            
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    protected void onPostExecute(String file_url) {
        
        
    }

}