package it.poli.android.scoutthisme.alarm.utils;

import it.poli.android.scoutthisme.Constants;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class AlarmDailyReceiver extends BroadcastReceiver
{
	// Triggered by the Alarm periodically (starts the service to run task)
	@Override
	public void onReceive(Context context, Intent intent)
	{
		Alarm userAlarm = (Alarm) intent.getSerializableExtra(Constants.INTENT_ALARM);
		Intent i = new Intent(context, AlarmDailyService.class);
		i.putExtra(Constants.INTENT_ALARM, userAlarm);
		context.startService(i);
	}
}