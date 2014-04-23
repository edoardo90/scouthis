package it.poli.android.scoutthisme.social;

import it.poli.android.scouthisme.R;
import it.poli.android.scoutthisme.constants.Constants;

import com.facebook.LoggingBehavior;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.Settings;

import android.app.Activity;
import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

public class NotifyFacebookFriendsService extends IntentService
{
	public NotifyFacebookFriendsService() {
       super("NotifyFacebookFriendsService");
	}
	
    @Override
    protected void onHandleIntent(Intent intent)
    {
       Settings.addLoggingBehavior(LoggingBehavior.INCLUDE_ACCESS_TOKENS);
       this.getFriendsAndUpdate();
    }

    private void getFriendsAndUpdate()
    {
        Session session = Session.openActiveSessionFromCache(getApplicationContext());
        
        //SHALL WE TRY A SECOND TIME? MAYBE NOT
        if (session == null) {
        	Log.i("background service",  "session cache is null, maybe the user is not logged");
        	session = new Session.Builder(getApplicationContext())
    	        .setApplicationId(getString(R.string.app_id))
    	        .build();
    	}
    	
        if (session /* still is */ == null)
        {
        	Log.i("background service ", "we can't create session");
        	return ;
        }
        else if (session.isOpened()) {
        	//if everything goes fine, now we got the url with all our friends stuff
        	String urlFriendsInfo = Constants.URL_PREFIX_FRIENDS + session.getAccessToken();
        	String userInfo = Constants.URL_PREFIX_ME +	session.getAccessToken();
        			
        	Log.i(" notify service background " , " userURL:" + userInfo);
        	
        	// call AsynTask to perform network operation on separate thread
        	// vedi: onPostExecute
        	new NotifyServerFBFriendsAsyncTask().execute(urlFriendsInfo, userInfo);
        }
        
    }
}