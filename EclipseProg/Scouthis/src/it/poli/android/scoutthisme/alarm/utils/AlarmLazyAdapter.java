package it.poli.android.scoutthisme.alarm.utils;

import it.poli.android.scouthisme.R;
import it.poli.android.scoutthisme.fragments.AlarmsHomeFragment;
import it.poli.android.scoutthisme.tools.ImageLoader;

import java.util.LinkedList;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class AlarmLazyAdapter extends BaseAdapter {

	private Activity activity;
	private AlarmsHomeFragment alarmHomeFrag;
	private LinkedList<Alarm> data;
	private static LayoutInflater inflater=null;
	public ImageLoader imageLoader; 

	public AlarmLazyAdapter(Activity a, AlarmsHomeFragment alarmHomeFrag , LinkedList<Alarm> alarmList) {
		activity = a;
		data=alarmList;
		this.alarmHomeFrag = alarmHomeFrag;
		inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		imageLoader=new ImageLoader(activity.getApplicationContext());
	}

	public int getCount() {
		return data.size();
	}

	public Object getItem(int position) {
		return position;
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		View vi=convertView;
		if(convertView == null)
			vi = inflater.inflate(R.layout.alarms_list_row, null);

		TextView days = (TextView)vi.findViewById(R.id.days); // title
		TextView hour = (TextView)vi.findViewById(R.id.hour); // artist name
		ImageView thumb_image=(ImageView)vi.findViewById(R.id.list_image); // thumb image
		ImageView interruttore = (ImageView)vi.findViewById(R.id.interrutt);

		Alarm alarm;
		if(data.size() > 0)
		{
			alarm = data.get(position);

			String bird = alarm.getBird();
			String active_alarm  = Boolean.toString(alarm.isActive());

			String daysG =  this.castDays(alarm.getActiveDays() ) ;
			days.setText(daysG);
			hour.setText(String.valueOf( alarm.getHour() )  +  ":" + pad(String.valueOf(alarm.getMinute())));

			Log.i(" lazy ad ", "bird: " + bird);

			int resID = activity.getResources().getIdentifier(bird, "drawable", activity.getPackageName());
			thumb_image.setImageResource(resID);


			int resIDAlarmOn = activity.getResources().getIdentifier("interr_on", "drawable", activity.getPackageName());
			int resIDAlarmOff = activity.getResources().getIdentifier("interr_off", "drawable", activity.getPackageName());

			boolean active = active_alarm.equalsIgnoreCase("true");
			interruttore.setImageResource(active ?  resIDAlarmOn : resIDAlarmOff);

			interruttore.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					alarmHomeFrag.switchAlarm(v);
				}
			});

			ImageView trashImg = (ImageView)vi.findViewById(R.id.trash_alarm);
			trashImg.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					alarmHomeFrag.deleteAlarm(v);

				}
			});
		}
		return vi;
	}

	private String pad(String s)
	{
		return (s.length() == 1) ? "0" + s : s ;
	}

	private String castDays(String daysBool)
	{
		String daysBoolNew = daysBool.replaceAll("\\s",""); 
		daysBoolNew = daysBoolNew.substring(1, daysBoolNew.length() - 1);

		String [] days = daysBoolNew.split(",");

		String activeDays = "";

		String  [] daysOfTheWeek = {"Lun ", "Mar ", "Mer ", "Giov ", "Ven ", "Sab ", "Dom "}; 

		int i =0;
		for(String d : days)
		{
			if(d.equalsIgnoreCase("True"))
				activeDays = activeDays + daysOfTheWeek[i];
			i++;
		}

		return activeDays;
	}
}