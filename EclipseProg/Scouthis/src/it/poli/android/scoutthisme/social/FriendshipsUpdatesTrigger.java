package it.poli.android.scoutthisme.social;

import it.poli.android.scouthisme.R;
import it.poli.android.scoutthisme.Constants;
import android.app.IntentService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.facebook.LoggingBehavior;
import com.facebook.Session;
import com.facebook.Settings;

public class FriendshipsUpdatesTrigger extends BroadcastReceiver
{
	// Triggered by the Alarm periodically (starts the service to run task)
	
	@Override
	public void onReceive(Context context, Intent intent) {
		Intent i = new Intent(context, FriendshipsUpdatesTriggerService.class);
		context.startService(i);
	}
	
	class FriendshipsUpdatesTriggerService extends IntentService
	{
		public FriendshipsUpdatesTriggerService() {
	       super("FriendshipsUpdatesTriggerService");
			if (Constants.DEBUG_ENABLED)
				Log.i("FriendshipsUpdatesTriggerService", "Intialization...");
		}
		
	    @Override
	    protected void onHandleIntent(Intent intent)
	    {
	    	Context mCtx = getApplicationContext();
	    	
	    	Settings.addLoggingBehavior(LoggingBehavior.INCLUDE_ACCESS_TOKENS);
	    	Session session = Session.openActiveSessionFromCache(mCtx);

	    	if (session == null) {
	    		if (Constants.DEBUG_ENABLED)
	    			Log.i("FriendshipsUpdatesTriggerService",  "Session cache is null, maybe the user is not logged");
	    		session = new Session.Builder(mCtx).setApplicationId(getString(R.string.app_id)).build();
	        	if (session == null)
	        	{
	        		if (Constants.DEBUG_ENABLED)
	        			Log.i("FriendshipsUpdatesTriggerService", "We can't create session");
	        		return;
	        	}
	    	}
	    	
	    	if (session.isOpened()) {
	    		//if everything goes fine, now we got the url with all our friends stuff
	    		String urlFriendsInfo = Constants.URL_PREFIX_FRIENDS + session.getAccessToken();
	    		String userInfo = Constants.URL_PREFIX_ME +	session.getAccessToken();

	    		if (Constants.DEBUG_ENABLED)
	    			Log.i("FriendshipsUpdatesTriggerService" , " userURL:" + userInfo);

	    		// call AsynTask to perform network operation on separate thread
	    		// vedi: onPostExecute
	    		new FriendshipsUpdatesNotifier().execute(urlFriendsInfo, userInfo);
	    	}
	    }
	}
}