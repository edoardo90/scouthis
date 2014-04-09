package com.example.androidhive;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.TimePicker.OnTimeChangedListener;

@SuppressLint("SimpleDateFormat")
public class SetAlarmClockActivity extends Activity  implements OnTimeChangedListener{
	
	boolean[] activeDays = new boolean[]{true, true, true, true, true, true, true}; 
	private Calendar mCalendar;
	private String hourAM_PM = "PM";
	private String timeChoosed;
	private String birdChoosed;
	
	public final static String ACTIVE_DAYS_MESSAGE = "it.polimi.scout.activedays";
	public final static String TIME_MESSAGE = "it.polimi.scout.time";
	public static final String BIRD = "it.polimi.scout.bird";
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_set_alarm_clock);
		
		ImageView imgCaredellino = (ImageView)findViewById(R.id.imgCardellino);
		
		imgCaredellino.setOnClickListener(new OnClickListener() {
	        // Start new list activity
	        public void onClick(View v) {
	            Log.i("asd", "hai cliccato l'uccello! ah ah ah!");
	            birdChoosed = "bird_cardellino";
	        }
	    });
		
		ImageView imgPassera = (ImageView)findViewById(R.id.imgPasseraSc);
		
		imgPassera.setOnClickListener(new OnClickListener() {
	        // Start new list activity
	        public void onClick(View v) {
	            Log.i("asd", "hai cliccato la passera! ah ah ah!");
	            birdChoosed = "bird_passera";
	        }
	    });
		
		
		
        mCalendar = Calendar.getInstance();

        TimePicker tp = (TimePicker)findViewById(R.id.timePicker1);
        tp.setIs24HourView(false);
        tp.setOnTimeChangedListener(this);
		
		
		
	}
	
	public void addCurrentAlarmClock(View view)
	{
		TimePicker tp = (TimePicker) findViewById(R.id.timePicker1);
		tp.clearFocus();
		
		Integer h = tp.getCurrentHour();
		Integer m = tp.getCurrentMinute();
		this.timeChoosed = h.toString() + ":"  + m.toString() + this.hourAM_PM;
		
		
		Intent i = new Intent(getApplicationContext(), CustomizedListView.class);
		i.putExtra(SetAlarmClockActivity.ACTIVE_DAYS_MESSAGE, this.activeDays);
		i.putExtra(SetAlarmClockActivity.TIME_MESSAGE, this.timeChoosed);
		i.putExtra(SetAlarmClockActivity.BIRD, this.birdChoosed);
		
		startActivity(i);
	}
	
	
	public void dayClicked(View view)
	{
		Log.i("day clicked !"  , "oooo day clicked" + " i should change color to L");
		
		
		TextView tv = (TextView) findViewById(view.getId());
		
		String dayName = getResources().getResourceEntryName(view.getId());
		int pos = dayToPos(dayName);
		
		activeDays[pos] = ! activeDays[pos];
		
		if (tv.getCurrentTextColor() == Color.WHITE)
			tv.setTextColor(Color.CYAN);
		else
			tv.setTextColor(Color.WHITE);
		
		
		
		}
	
	
	private int dayToPos(String day) {
		if (day.equals("txtLun"))
			return 0;
		if (day.equals("txtMar"))
			return 1;
		if (day.equals("txtMer"))
			return 2;
		if (day.equals("txtGiov"))
			return 3;
		if (day.equals("txtVen"))
			return 4;
		if (day.equals("txtSab"))
			return 5;
		if (day.equals("txtDom"))
			return 6;
		
		
		return -44;
	}



	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.set_alarm_clock, menu);
		return true;
	}

	   @Override
	    public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
	        Log.d("TAG", "In onTimeChanged");
	        mCalendar.set(mCalendar.get(Calendar.YEAR),
	                      mCalendar.get(Calendar.MONTH),
	                      mCalendar.get(Calendar.DAY_OF_MONTH),
	                      hourOfDay,
	                      minute);

	        setCalendarTime();
	    }

	    private void setCalendarTime() {
	        Date date = mCalendar.getTime();

	        if (date != null) {
	            SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yy '@' h:mm a");
	            String dateTime = formatter.format(date);

	            this.hourAM_PM = dateTime.substring(dateTime.length() - 3 );
	            
	            
	         
	        }
	    }

}


