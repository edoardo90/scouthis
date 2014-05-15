package it.poli.android.scouthisme.alarm;

import it.poli.android.scoutthisme.Constants;
import it.poli.android.scoutthisme.tools.AlarmUtils;
import it.poli.android.scoutthisme.tools.Alarm;

import java.util.Calendar;
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

		if(!activeAlarmDays[currentDayOfWeek])
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