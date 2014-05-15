package it.poli.android.scoutthisme.tools;

import it.poli.android.scouthisme.alarm.DailyReceiver;
import it.poli.android.scoutthisme.Constants;

import java.util.Calendar;
import java.util.TimeZone;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

public class AlarmHandler
{	
	public static void  setAlarm(Alarm userAlarm, Context context, Activity activity)
	{
		Calendar updateTime = Calendar.getInstance();
		updateTime.setTimeZone(TimeZone.getDefault());
		updateTime.set(Calendar.HOUR_OF_DAY, userAlarm.getHour());
		updateTime.set(Calendar.MINUTE, userAlarm.getMinute());

		Intent alarmIntent = new Intent(context, DailyReceiver.class);
		alarmIntent.putExtra(Constants.INTENT_ALARM, userAlarm);
		
		//NB: id sveglia come request code!
		PendingIntent recurringAlarm = PendingIntent.getBroadcast(context, 
				userAlarm.getId(), alarmIntent, PendingIntent.FLAG_CANCEL_CURRENT);

		AlarmManager alarms = (AlarmManager) activity.getSystemService(Context.ALARM_SERVICE);
		alarms.setRepeating(AlarmManager.RTC_WAKEUP,
				updateTime.getTimeInMillis(),
				AlarmManager.INTERVAL_DAY, 
				recurringAlarm);

		if (!userAlarm.isActive())
			alarms.cancel(recurringAlarm);
	}
	
	public static void  removeAlarm(Alarm userAlarm, Context context, Activity activity)
	{
		Intent alarmIntent = new Intent(context, DailyReceiver.class);
		alarmIntent.putExtra(Constants.INTENT_ALARM, userAlarm);

		//NB: id sveglia come request code!
		PendingIntent recurringAlarm = PendingIntent.getBroadcast(context,
				userAlarm.getId(), alarmIntent, PendingIntent.FLAG_CANCEL_CURRENT);

		AlarmManager alarms = (AlarmManager) activity.getSystemService(
				Context.ALARM_SERVICE);
    	alarms.cancel(recurringAlarm);
	}
}