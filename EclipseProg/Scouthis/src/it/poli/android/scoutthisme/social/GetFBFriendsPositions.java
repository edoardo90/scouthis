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

import android.util.Log;

public class GetFBFriendsPositions
{
	private double userLatitude;
	private double userLongitude;
	private String userIDURL;

	public GetFBFriendsPositions(String userInfoURL, double userLat, double userLong)
	{
		userLatitude = userLat;
		userLongitude = userLong;
		this.userIDURL = userInfoURL;
	}

	protected String getFriendsGPS()
	{      
		String result = getStringFromUrl(this.userIDURL) ;
		String userInfoJson = result;
		String userId = this.getUserIDFromJson(userInfoJson, Constants.PARAM_USERID);

		if (Constants.DEBUG_ENABLED)
			Log.i("user", userId);

		GetFriendsGPSFromURL ufl = new GetFriendsGPSFromURL(userId, Constants.URL_GET_FRIENDSLIST,
						userLatitude , userLongitude);
		return ufl.getGPSFriends();
	}

	private String getStringFromUrl(String url){
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
			if(inputStream != null)
				result = convertInputStreamToString(inputStream);
			else
				result = "Did not work!";

		} catch (Exception e) {
			Log.d("InputStream", e.getLocalizedMessage());
		}

		return result;
	}

	private static String convertInputStreamToString(InputStream inputStream) throws IOException{
		BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));
		String line = "";
		String result = "";
		while((line = bufferedReader.readLine()) != null)
			result += line;

		inputStream.close();
		return result;
	}


	private String getUserIDFromJson(String userInfoJson, String infoToGet)
	{		
		String s = "";

		try {
			JSONObject job = new JSONObject(userInfoJson);
			s = job.getString("id");
			if(infoToGet == Constants.PARAM_USERID_AND_NAME)
			{
				s = s + " " + job.get("first_name") + " " + job.get("last_name");
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return s;
	}

	class GetFriendsGPSFromURL
	{
		private JSONParser jsonParser = new JSONParser();

		private String userId;
		private String urlGetGPSFriendList;

		private double latitude;
		private double longitude;

		public GetFriendsGPSFromURL(String userId, String urltogetcoords, double latitude, double longitude) {
			this.userId = userId;
			this.urlGetGPSFriendList = urltogetcoords;
			this.latitude = latitude;
			this.longitude = longitude;
		}

		public String getGPSFriends()
		{    
			// Building Parameters for POST METHOD 
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair(Constants.PARAM_USERID, this.userId));
			params.add(new BasicNameValuePair(Constants.PARAM_POSITION_LATITUDE, String.valueOf(this.latitude)));
			params.add(new BasicNameValuePair(Constants.PARAM_POSITION_LONGITUDE, String.valueOf(this.longitude)));

			// getting JSON Object FROM PHP PAGE, WE GET GPS FRIENDS COORD USING PHP PAGE
			JSONObject json = jsonParser.makeHttpRequest(this.urlGetGPSFriendList, "POST", params);
			if(json != null)
			{
				// check for success tag
				try {
					int success = json.getInt("success");
					if(success!=0) {
						Log.i("get friends gps", "wooo! lette coord gps !");
						return json.toString();
					}

				} catch (JSONException e) {
					e.printStackTrace();
					return "error";
				}
			}
			return "error requesting gps coords to php";
		}
	}
}