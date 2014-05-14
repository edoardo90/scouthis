package it.poli.android.scouthisme.alarm;

import it.poli.android.scoutthisme.Constants;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class DailyReceiver extends BroadcastReceiver
{
	// Triggered by the Alarm periodically (starts the service to run task)
	@Override
	public void onReceive(Context context, Intent intent)
	{
		UserAlarm userAlarm = (UserAlarm) intent.getSerializableExtra(Constants.INTENT_ALARM);
		Intent i = new Intent(context, DailyService.class);
		i.putExtra(Constants.INTENT_ALARM, userAlarm);
		context.startService(i);
	}
}