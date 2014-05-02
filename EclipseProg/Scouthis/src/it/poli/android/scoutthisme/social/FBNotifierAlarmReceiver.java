package it.poli.android.scoutthisme.social;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class FBNotifierAlarmReceiver extends BroadcastReceiver
{
	public static final int REQUEST_CODE = 12345;

	// Triggered by the Alarm periodically (starts the service to run task)
	@Override
	public void onReceive(Context context, Intent intent) {
		Intent i = new Intent(context, FBNotifyFriendsService.class);
		i.putExtra("foo", "bar");
		context.startService(i);
	}
}