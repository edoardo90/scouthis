package it.poli.android.scoutthisme.social;

import it.poli.android.scouthisme.R;
import it.poli.android.scoutthisme.Constants;
import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.facebook.LoggingBehavior;
import com.facebook.Session;
import com.facebook.Settings;

public class FBNotifyFriendsService extends IntentService
{
	public FBNotifyFriendsService() {
       super("NotifyFacebookFriendsService");
	}
	
    @Override
    protected void onHandleIntent(Intent intent)
    {
    	Settings.addLoggingBehavior(LoggingBehavior.INCLUDE_ACCESS_TOKENS);
    	Session session = Session.openActiveSessionFromCache(getApplicationContext());

    	if (session == null) {
    		Log.i("background service",  "session cache is null, maybe the user is not logged");
    		session = new Session.Builder(getApplicationContext())
    		.setApplicationId(getString(R.string.app_id))
    		.build();
        	if (session == null)
        	{
        		Log.i("background service ", "we can't create session");
        		return;
        	}
    	}
    	
    	if (session.isOpened()) {
    		//if everything goes fine, now we got the url with all our friends stuff
    		String urlFriendsInfo = Constants.URL_PREFIX_FRIENDS + session.getAccessToken();
    		String userInfo = Constants.URL_PREFIX_ME +	session.getAccessToken();

    		Log.i(" notify service background " , " userURL:" + userInfo);

    		// call AsynTask to perform network operation on separate thread
    		// vedi: onPostExecute
    		new NotifyFriendsAsyncTask().execute(urlFriendsInfo, userInfo);
    	}
    }
}