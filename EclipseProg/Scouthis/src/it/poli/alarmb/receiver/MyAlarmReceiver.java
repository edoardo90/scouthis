package it.poli.alarmb.receiver;

import it.poli.alarmb.alarmstime.AlarmsTool;
import it.poli.alarmb.alarmstime.UserAlarm;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class MyAlarmReceiver extends BroadcastReceiver {


	  // Triggered by the Alarm periodically (starts the service to run task)
	  @Override
	  public void onReceive(Context context, Intent intent) {
		
		UserAlarm ua =  (UserAlarm) intent.getSerializableExtra(AlarmsTool.alarmExtra);
	    Intent i = new Intent(context, PlayAlarmService.class);
	    i.putExtra(AlarmsTool.alarmExtra, ua);
	    context.startService(i);
	  }
	}