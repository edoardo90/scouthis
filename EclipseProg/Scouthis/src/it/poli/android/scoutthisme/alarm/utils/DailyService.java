package it.poli.android.scoutthisme.alarm.utils;

import it.poli.android.scoutthisme.Constants;
import it.poli.android.scoutthisme.tools.Alarm;
import it.poli.android.scoutthisme.tools.AlarmUtils;

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
		Alarm ua = (Alarm) intent.getSerializableExtra(Constants.INTENT_ALARM);
		Log.i("PlayAlarmService", "msg: " + ua.getBird()+ "  .. " + ua.getHour());

		String days = ua.getDays();
		boolean [] activeAlarmDays = AlarmUtils.daysStringToBooleanArray(days);
		
		Calendar localCalendar = Calendar.getInstance(TimeZone.getDefault());
		localCalendar.setFirstDayOfWeek(0);
		
		//TODO: RUFY RIGUARDALO
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
		alarmCalendar.set(Calendar.HOUR_OF_DAY, ua.getHour());
		alarmCalendar.set(Calendar.MINUTE, ua.getMinute());
		
		Date dateAlarm = alarmCalendar.getTime();
		Date dateNow = calendNow.getTime();
		
		
		int minutiMancantiAllaSveglia = dateAlarm.compareTo(dateNow); //an int < 0 if this Date is less than the specified Date, 0 if they are equal, and an int > 0 if this Date is greater
		// min < 0 data della sveglia è minore della data attuale
		// min < 0 sveglia è prima di adesso
		// min < 0 sveglia è nel passato
		//minutiMancantiAllaSveglia < 0  => la sveglia si riferisce a un'ora precedente
		// a quella attuale e quindi non deve suonare!
		
		if(!activeAlarmDays[currentDayOfWeek] || minutiMancantiAllaSveglia < 0)
		{
			Log.w("play alarm service", "Non è uno dei giorni di attività della sveglia " +
					" quindi non faccio partire l'attività ");
			return;
		}
		else
		{
			Intent i = new Intent(this, WakeUpUserActivity.class);
			i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(i);
		}
	}
}