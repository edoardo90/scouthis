package it.poli.android.scoutthisme.social.utils;

import it.poli.android.scoutthisme.Constants;
import it.poli.android.scoutthisme.social.ExchangeCoordinatesService;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

public class FacebookHandler
{	
	Activity mAct;
	FacebookListener listener;
	Timer timer;
	Location loc = null;
    
    public FacebookHandler (Activity mAct) {
    	this.mAct = mAct;
    }

    public void setListener(FacebookListener l)
    {
    	this.listener = l;
    	
		mAct.registerReceiver(facebookExchangeCoordinatesServiceReceiver, new IntentFilter(Constants.INTENT_EXCHANGECOORDSRECEIVER_NOTIFICATION));
		
		// Timer per lanciare ogni quanto di tempo il servizio che trova le coordinate degli amici online
		timer = new Timer();
		final Handler handler = new Handler();
		TimerTask doAsynchronousTask = new TimerTask() {       
			@Override
			public void run() {
				handler.post(new Runnable() {
					public void run() { 
						try {
							Log.i("Friends frag", "Updating friends positions...");

							if(loc != null)
							{
								Intent intent =  new Intent(mAct, ExchangeCoordinatesService.class);
								intent.putExtra(Constants.PARAM_POSITION_LATITUDE, loc.getLatitude());
								intent.putExtra(Constants.PARAM_POSITION_LONGITUDE, loc.getLongitude());
								mAct.startService(intent);
							}
						}
						catch (Exception e) {
							e.getLocalizedMessage();
						}
					}
				});
			}
		};
		timer.schedule(doAsynchronousTask, 0, Constants.TIME_EXCHANGE_FRIENDS_POSITION); 
    }
    
    public void removeListener()
    {
    	this.listener = null;
    	
		timer.cancel();
		timer.purge();
		
		if (Constants.DEBUG_ENABLED)
			Log.i("pause", "timer purged");
		
		mAct.unregisterReceiver(facebookExchangeCoordinatesServiceReceiver);
    }
    
    public void updatePosition(Location loc) {
    	this.loc = loc;
    }
	
	private BroadcastReceiver facebookExchangeCoordinatesServiceReceiver = new BroadcastReceiver()
	{
		@Override
		public void onReceive(Context context, Intent intent) {
			// What does the tread says me when finished ..
			//  say putExtra in ExchangeCoordinatesService e getString..
			Log.i("FbExCoordSvcReceiver", "Received friends coords by ExchangeCoordinatesService");
			Bundle bundle = intent.getExtras();
			if (bundle != null) {
				String gpsCoordJSONStr = bundle.getString(Constants.PARAM_GPS_COORDINATES);
				// Send response to APP to check if network is available
				int resultCode = bundle.getInt(Constants.PARAM_RESULT);
				listener.onFacebookResponse(resultCode);
				// If network is OK, send friends list
				if (Constants.DEBUG_ENABLED)
					Log.i("FbExCoordSvcReceiver", String.valueOf(resultCode).contentEquals("0") ? "NO" : "OK");
				if (resultCode == Constants.RESULT_OK) {
					listener.onFriendsUpdates(gpsCoordJSONStr);
				}
			}
		}
	};
}