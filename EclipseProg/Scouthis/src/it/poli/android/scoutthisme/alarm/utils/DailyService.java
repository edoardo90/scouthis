package it.poli.android.scoutthisme.alarm.utils;

import it.poli.android.scoutthisme.Constants;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

public class DailyService extends IntentService
{
	public DailyService() {
		super("PlayAlarmService");
	}

	@Override
	protected void onHandleIntent(Intent intent)
	{
		Alarm userAlarm = (Alarm) intent.getSerializableExtra(Constants.INTENT_ALARM);
		Log.i("PlayAlarmService", "msg: " + userAlarm.getBird()+ "  .. " + userAlarm.getHour());

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
			Log.w("play alarm service", "Non è uno dei giorni di attività della sveglia " +
					" quindi non faccio partire l'attività ");
			return;
		}
		else
		{
			Intent newIntent = new Intent(this, WakeUpUserActivity.class);
			newIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			newIntent.putExtra(Constants.INTENT_ALARM, userAlarm);
			startActivity(newIntent);
		}
	}
}