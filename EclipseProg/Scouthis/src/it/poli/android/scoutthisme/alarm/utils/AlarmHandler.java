package it.poli.android.scoutthisme.alarm.utils;

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
	public static void setAlarm(Alarm userAlarm, Activity activity)
	{
		Calendar updateTime = Calendar.getInstance();
		updateTime.setTimeZone(TimeZone.getDefault());
		
		updateTime.set(Calendar.HOUR_OF_DAY, userAlarm.getHour());
		updateTime.set(Calendar.MINUTE, userAlarm.getMinute());
				
		Intent alarmIntent = new Intent(activity, DailyReceiver.class);
		alarmIntent.putExtra(Constants.INTENT_ALARM, userAlarm);
			
		//NB: id sveglia come request code!
		PendingIntent recurringAlarm = PendingIntent.getBroadcast(activity, 
				userAlarm.getId(), alarmIntent, PendingIntent.FLAG_CANCEL_CURRENT);

		AlarmManager alarms = (AlarmManager) activity.getSystemService(Context.ALARM_SERVICE);
		
		if (userAlarm.isActive())
			alarms.setRepeating(AlarmManager.RTC_WAKEUP,
					updateTime.getTimeInMillis() -  Constants.ALARM_SLIGHTLY_SOONER,
					AlarmManager.INTERVAL_DAY, 
					recurringAlarm);
		
		if (!userAlarm.isActive())
			alarms.cancel(recurringAlarm);
	}
	
	public static void removeAlarm(Alarm userAlarm, Activity activity)
	{
		Intent alarmIntent = new Intent(activity, DailyReceiver.class);
		alarmIntent.putExtra(Constants.INTENT_ALARM, userAlarm);

		//NB: id sveglia come request code!
		PendingIntent recurringAlarm = PendingIntent.getBroadcast(activity,
				userAlarm.getId(), alarmIntent, PendingIntent.FLAG_CANCEL_CURRENT);

		AlarmManager alarms = (AlarmManager) activity.getSystemService(
				Context.ALARM_SERVICE);
    	alarms.cancel(recurringAlarm);
	}
}