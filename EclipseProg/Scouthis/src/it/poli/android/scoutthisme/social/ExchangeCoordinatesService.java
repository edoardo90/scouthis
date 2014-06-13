package it.poli.android.scoutthisme.social;

import it.poli.android.scouthisme.R;
import it.poli.android.scoutthisme.Constants;
import it.poli.android.scoutthisme.social.utils.FacebookNetUtils;
import it.poli.android.scoutthisme.social.utils.JSONParser;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;

import com.facebook.LoggingBehavior;
import com.facebook.Session;
import com.facebook.Settings;

public class ExchangeCoordinatesService extends IntentService
{
	private double userLatitude;
	private double userLongitude;

	Context mCtx;
	private String userInfoURL;

	public ExchangeCoordinatesService()
	{
		super("ExchangeCoordinatesService");
		
			
	}

	// will be called asynchronously by Android
	@Override
	protected void onHandleIntent(Intent intent)
	{
		mCtx = getApplicationContext();

		if (Constants.DEBUG_ENABLED)
			

		userLatitude = intent.getDoubleExtra(Constants.PARAM_POSITION_LATITUDE, 0);
		userLongitude = intent.getDoubleExtra(Constants.PARAM_POSITION_LONGITUDE, 0);

		Settings.addLoggingBehavior(LoggingBehavior.INCLUDE_ACCESS_TOKENS);

		exchangeFriendsCooordinatesPositions();
	}

	private void exchangeFriendsCooordinatesPositions()
	{
		Session session = Session.openActiveSessionFromCache(mCtx);
		String userGPSCoords = "";

		if (session == null) {
			if (Constants.DEBUG_ENABLED)
				
			session = new Session.Builder(mCtx).setApplicationId(getString(R.string.app_id)).build();
			if (session == null) //if session is still null
			{
				if (Constants.DEBUG_ENABLED)
					
				return;
			}
		} else if (session.isOpened()) {
			userInfoURL = Constants.URL_PREFIX_ME +	session.getAccessToken();

			if (Constants.DEBUG_ENABLED)
				
			// call AsynTask to perform network operation on separate thread
			// vedi: onPostExecute
			userGPSCoords =  getFriendsGPS();
		}
		
		int result = userGPSCoords.contentEquals("") ? Constants.RESULT_ERROR : Constants.RESULT_OK;
		this.publishResults(userGPSCoords, result);
	}

	protected String getFriendsGPS()
	{
		JSONParser jsonParser = new JSONParser();
		
		String userInfoJson = FacebookNetUtils.getStringFromUrl(userInfoURL);
		String userId = jsonParser.getUserInfoFromJson(userInfoJson)[0];
		
		if (userInfoJson.contentEquals("") || userInfoJson.contentEquals(""))
			return "";

		
			

		// Building Parameters for POST METHOD 
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair(Constants.PARAM_USERID, userId));
		params.add(new BasicNameValuePair(Constants.PARAM_POSITION_LATITUDE, String.valueOf(userLatitude)));
		params.add(new BasicNameValuePair(Constants.PARAM_POSITION_LONGITUDE, String.valueOf(userLongitude)));

		// getting JSON Object FROM PHP PAGE, WE GET GPS FRIENDS COORD USING PHP PAGE
		JSONObject json = jsonParser.makeHttpRequest(Constants.URL_GET_FRIENDSLIST, "POST", params);
		if(json != null)
		{
			// check for success tag
			try {
				int success = json.getInt("success");
				if(success != 0) {
					if (Constants.DEBUG_ENABLED)
						
					return json.toString();
				}
			} catch (JSONException e) {
				if (Constants.DEBUG_ENABLED)
					e.printStackTrace();
				return "";
			}
		}
		return "";
	}

	//what does the thread says back to caller
	private void publishResults(String userGPSCoords, int result) {
		Intent intent = new Intent(Constants.INTENT_EXCHANGECOORDSRECEIVER_NOTIFICATION);
		intent.putExtra(Constants.PARAM_GPS_COORDINATES, userGPSCoords);
		intent.putExtra(Constants.PARAM_RESULT, result);

		sendBroadcast(intent);
	}
} 