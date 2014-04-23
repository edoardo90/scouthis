package it.poli.android.scoutthisme.social;

import it.poli.android.scouthisme.R;
import it.poli.android.scoutthisme.constants.Constants;
import it.poli.android.scoutthisme.fragments.FindFriendsFragment;
import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.facebook.LoggingBehavior;
import com.facebook.Session;
import com.facebook.Settings;

public class GetFriendsPositionsService extends IntentService
{
	public static final String GPSCOORD = "gpsCoord";
	public static final String RESULT = "result";
	public static final String NOTIFICATION = "it.poli.android.scoutthisme";
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
      
	  this.userLatitude = intent.getDoubleExtra(FindFriendsFragment.strLatitudeExtra, 0);
	  this.userLongitude = intent.getDoubleExtra(FindFriendsFragment.strLongitudeExtra, 0);
	  
      Settings.addLoggingBehavior(LoggingBehavior.INCLUDE_ACCESS_TOKENS);
      
      this.getFriendsPositions();
	}

	private void getFriendsPositions()
 	{
		Session session = Session.openActiveSessionFromCache(getApplicationContext());
		String userGPSCoords = "";
	  
		//SHALL WE TRY A SECOND TIME? MAYBE NOT
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
			userGPSCoords =  new GetFBFriendsPositions(userInfoURL, this.userLatitude, this.userLongitude).getFriendsGPS();
		}
      	this.publishResults(userGPSCoords, 1);
	}
  
	//what does the thread says back to caller
	private void publishResults(String userGPSCoords, int result) {
		Intent intent = new Intent(NOTIFICATION);
		intent.putExtra(GPSCOORD, userGPSCoords);
		intent.putExtra(RESULT, result);
		
		sendBroadcast(intent);
	}
} 