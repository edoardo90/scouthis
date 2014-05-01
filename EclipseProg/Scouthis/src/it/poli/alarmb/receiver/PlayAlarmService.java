package it.poli.alarmb.receiver;

import java.util.Calendar;
import java.util.TimeZone;

import it.poli.alarmb.Utils;
import it.poli.alarmb.alarmstime.AlarmsTool;
import it.poli.alarmb.alarmstime.UserAlarm;
import it.poli.alarmb.sound.SoundActivity;
import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

public class PlayAlarmService extends IntentService {

	public PlayAlarmService() {
		super("PlayAlarmService");
	}

	@Override
	protected void onHandleIntent(Intent intent) {

		UserAlarm ua = (UserAlarm) intent.getSerializableExtra(AlarmsTool.alarmExtra);
		Log.i("PlayAlarmService", "msg: " + ua.getBird()+ "  .. " + ua.getHour());

		String days = ua.getDays();
		boolean [] bbdays = Utils.strToBAR(days);
		Calendar localCalendar = Calendar.getInstance(TimeZone.getDefault());
		localCalendar.setFirstDayOfWeek(0);
		int currentDayOfWeek = localCalendar.get(Calendar.DAY_OF_WEEK);
		currentDayOfWeek -= 2;
		if(currentDayOfWeek==-1)
			currentDayOfWeek = 6;

		if(!bbdays[currentDayOfWeek])
		{
			Log.w("play alarm service", " non è uno dei giorni di attività della sveglia" +
					"quindi non faccio partire l'attività ");
			return;
		}
		else
		{
			Intent i = new Intent(this, SoundActivity.class);
			i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(i);
		}
	}
	
	
}