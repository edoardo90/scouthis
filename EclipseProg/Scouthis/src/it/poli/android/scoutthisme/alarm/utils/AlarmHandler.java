package it.poli.android.scoutthisme.alarm.utils;

import it.poli.android.scoutthisme.Constants;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class AlarmHandler extends Service
{	
	public static void setAlarm(Alarm userAlarm, Activity activity)
	{
		Calendar updateTime = Calendar.getInstance();
		updateTime.setTimeZone(TimeZone.getDefault());
		
		updateTime.set(Calendar.HOUR_OF_DAY, userAlarm.getHour());
		updateTime.set(Calendar.MINUTE, userAlarm.getMinute());
				
		Intent alarmIntent = new Intent(activity, AlarmHandler.class);
		alarmIntent.putExtra(Constants.INTENT_ALARM, userAlarm);
		
		AlarmManager alarms = (AlarmManager) activity.getSystemService(Context.ALARM_SERVICE);
		
		//NB: id sveglia come request code!
		int pendID = userAlarm.getId();
		
		PendingIntent recurringAlarm = 
				PendingIntent.getService
				(activity, pendID , alarmIntent, PendingIntent.FLAG_CANCEL_CURRENT);
		Log.i("LOG", "SETTO PENDING : " + pendID );

		
		if (userAlarm.isActive())
			alarms.setRepeating(AlarmManager.RTC_WAKEUP,
				updateTime.getTimeInMillis() -  Constants.ALARM_SLIGHTLY_SOONER,
				AlarmManager.INTERVAL_DAY, 
				recurringAlarm);
		else
			alarms.cancel(recurringAlarm);
	}
	
	public static void removeAlarm(Alarm userAlarm, Activity activity)
	{
		Intent alarmIntent = new Intent(activity, AlarmHandler.class);
		alarmIntent.putExtra(Constants.INTENT_ALARM, userAlarm);

		//NB: id sveglia come request code!
		PendingIntent recurringAlarm = PendingIntent.getService(activity,
				userAlarm.getId(), alarmIntent, PendingIntent.FLAG_CANCEL_CURRENT);

		AlarmManager alarms = (AlarmManager) activity.getSystemService(
				Context.ALARM_SERVICE);
    	alarms.cancel(recurringAlarm);
	}
	
	
			@Override
			public IBinder onBind(Intent intent) {
				// TODO Auto-generated method stub
				return null;
			}

			
			 @Override
			  public int onStartCommand(Intent intent, int flags, int startId) {
				 
				 

				 Alarm userAlarm = (Alarm) intent.getSerializableExtra(Constants.INTENT_ALARM);
				 Log.i("SERVICE", userAlarm + " . ");
				 
				 Intent ii1 = new Intent(getBaseContext(), WakeUpUserActivity.class);
				 ii1.putExtra(Constants.INTENT_ALARM, userAlarm);
				 ii1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				 
				 if(timeIsGood(userAlarm)) 
				 {
					 startActivity(ii1);
				 }
				 
				 return 0;
			  }


			  private boolean timeIsGood(Alarm userAlarm) {
			  
				  String days = userAlarm.getActiveDays();
					boolean [] activeAlarmDays = AlarmUtils.daysStringToBooleanArray(days);
					
					Calendar localCalendar = Calendar.getInstance(TimeZone.getDefault());
					localCalendar.setFirstDayOfWeek(0);
					
					
					int currentDayOfWeek = localCalendar.get(Calendar.DAY_OF_WEEK);
					currentDayOfWeek -= 2;
					if(currentDayOfWeek==-1)
						currentDayOfWeek = 6;
					
					//controlla se la sveglia è stata impostata per un'ora passata
					//rispetto all'ora corrente, nel caso il service parte (così sono fatti 
					// da Java ... ) ma la sveglia NON deve suonare
					
					// es. se alle 4 del pomeriggio imposto una sveglia alle 2 del pomeriggio
					// il service viene fatto partire e farebbe suonare la sveglia
					Calendar calendNow = Calendar.getInstance();
					calendNow.setTimeZone(TimeZone.getDefault());
					
					Calendar alarmCalendar = Calendar.getInstance();
					alarmCalendar.setTimeZone(TimeZone.getDefault());
					alarmCalendar.set(Calendar.HOUR_OF_DAY, userAlarm.getHour());
					alarmCalendar.set(Calendar.MINUTE, userAlarm.getMinute());
					
					Date dateAlarm = alarmCalendar.getTime();
					Date dateNow = calendNow.getTime();
					
					boolean diffIsVeryHigh = true;
					long diff = dateNow.getTime() - dateAlarm.getTime(); //differenza in millisecondi
					float secDiff = (Float.valueOf(diff))/ 1000f;  //differenza in secondi
					secDiff = Math.abs(secDiff);
					
					if(secDiff <= Constants.ALARM_SECONDS_ACCURACY)
						diffIsVeryHigh = false;
					
					if(!activeAlarmDays[currentDayOfWeek] || diffIsVeryHigh)
					{
						return false;
					}
					else
					{
						return true;
					}
				}
			


			@Override
			  public void onDestroy() {
			   
			  }

	
	

}