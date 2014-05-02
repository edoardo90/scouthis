package it.poli.android.scoutthisme.social;

import it.poli.android.scouthisme.R;
import it.poli.android.scoutthisme.Constants;
import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.facebook.LoggingBehavior;
import com.facebook.Session;
import com.facebook.Settings;

public class GetFriendsPositionsService extends IntentService
{
	private double userLatitude;
	private double userLongitude;

	public GetFriendsPositionsService()
	{
		super("GetFriendsPositionsService");
		Log.i(" download service ", "costructore");
	}

	// will be called asynchronously by Android
	@Override
	protected void onHandleIntent(Intent intent)
	{
	  Log.i("NotifyFacebookFriendsService", "Service running");
      
	  userLatitude = intent.getDoubleExtra(Constants.PARAM_POSITION_LATITUDE, 0);
	  userLongitude = intent.getDoubleExtra(Constants.PARAM_POSITION_LONGITUDE, 0);
	  
      Settings.addLoggingBehavior(LoggingBehavior.INCLUDE_ACCESS_TOKENS);
      
      getFriendsPositions();
	}

	private void getFriendsPositions()
 	{
		Session session = Session.openActiveSessionFromCache(getApplicationContext());
		String userGPSCoords = "";
	  
		if (session == null) {
			Log.i("friends position service",  "session cache is null, maybe the user is not logged");
			session = new Session.Builder(getApplicationContext())
  	        	.setApplicationId(getString(R.string.app_id))
  	        	.build();
			if (session == null) //if session is still null
			{
				Log.i("background service ", "we can't create session");
				return;
			}
		} else if (session.isOpened()) {
			String userInfoURL = Constants.URL_PREFIX_ME +	session.getAccessToken();
			Log.i(" notify service background " , " userURL:" + userInfoURL);
			
			// call AsynTask to perform network operation on separate thread
			// vedi: onPostExecute
			userGPSCoords =  new GetFBFriendsPositions(userInfoURL, userLatitude, userLongitude).getFriendsGPS();
		}
      	this.publishResults(userGPSCoords, 1);
	}
  
	//what does the thread says back to caller
	private void publishResults(String userGPSCoords, int result) {
		Intent intent = new Intent(Constants.INTENT_NOTIFICATION);
		intent.putExtra(Constants.PARAM_GPS_COORDINATES, userGPSCoords);
		intent.putExtra(Constants.PARAM_RESULT, result);
		
		sendBroadcast(intent);
	}
} 