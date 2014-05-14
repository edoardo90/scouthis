package it.poli.alarmb.alarmstime;

import it.poli.alarmb.receiver.MyAlarmReceiver;

import java.util.Calendar;
import java.util.TimeZone;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class AlarmsTool {
	
	public static String alarmExtra = "it.poli.alarm.bird";

	public static void  setAlarmFor(UserAlarm uAlarm, Context context, Activity activity)
	{

		Log.i("schd day", "sch day");
		Calendar updateTime = Calendar.getInstance();
		updateTime.setTimeZone(TimeZone.getDefault());
		updateTime.set(Calendar.HOUR_OF_DAY, uAlarm.getHour());
		updateTime.set(Calendar.MINUTE, uAlarm.getMinute());


		Intent alarmIntent = new Intent(context, MyAlarmReceiver.class);
		alarmIntent.putExtra(AlarmsTool.alarmExtra, uAlarm);

		
		//NB: id sveglia come request code!
		PendingIntent recurringAlarm = PendingIntent.getBroadcast(context,
				uAlarm.getId(), alarmIntent, PendingIntent.FLAG_CANCEL_CURRENT);


		AlarmManager alarms = (AlarmManager) activity.getSystemService(
				Context.ALARM_SERVICE);

		alarms.setRepeating(AlarmManager.RTC_WAKEUP,
				updateTime.getTimeInMillis(),
				AlarmManager.INTERVAL_DAY, 
				recurringAlarm);

		if (!uAlarm.isActive())
		{
			alarms.cancel(recurringAlarm);
		}

	}
	
	
	public static void  removeAlarmFor(UserAlarm uAlarm, Context context, Activity activity)
	{

		Intent alarmIntent = new Intent(context, MyAlarmReceiver.class);
		alarmIntent.putExtra(AlarmsTool.alarmExtra, uAlarm);

		//NB: id sveglia come request code!
		PendingIntent recurringAlarm = PendingIntent.getBroadcast(context,
				uAlarm.getId(), alarmIntent, PendingIntent.FLAG_CANCEL_CURRENT);


		AlarmManager alarms = (AlarmManager) activity.getSystemService(
				Context.ALARM_SERVICE);

    	alarms.cancel(recurringAlarm);

	}

	

}


