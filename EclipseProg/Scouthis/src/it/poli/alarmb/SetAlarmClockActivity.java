package it.poli.alarmb;

import it.poli.android.scouthisme.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Rect;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.TouchDelegate;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.TimePicker.OnTimeChangedListener;

@SuppressLint("SimpleDateFormat")
public class SetAlarmClockActivity extends Activity  implements OnTimeChangedListener{
	
	boolean[] activeDays = new boolean[]{true, true, true, true, true, true, true}; 
	private Calendar mCalendar;
	@SuppressWarnings("unused")
	private String hourAM_PM = "PM";
	private String timeChoosed;
	private String birdChoosed = "bird_cardellino";
	private boolean activeAlarm = true;
    Animation swingAnimation ;
	
	public static Map<String, String> birdsImageMap  = new HashMap<String, String>();
	
	public final static String ACTIVE_DAYS_MESSAGE = "it.polimi.scout.activedays";
	public final static String TIME_MESSAGE = "it.polimi.scout.time";
	public static final String BIRD = "it.polimi.scout.bird";
	public static final String ACTIVE_ALARM = "it.polimi.scout.active";
	
	private Activity act;
	private MediaPlayer mp1 = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_set_alarm_clock);
		
		 act = this;
		
		swingAnimation =   AnimationUtils.loadAnimation(getApplicationContext(), R.anim.swing_bird);
		
		
		this.initBirdMap();
		
		ImageView imgCaredellino = (ImageView)findViewById(R.id.imgCardellino);
		
		this.addClickListenerBirds();
		
        mCalendar = Calendar.getInstance();

        TimePicker tp = (TimePicker)findViewById(R.id.timePicker1);
        tp.setIs24HourView(false);
        tp.setOnTimeChangedListener(this);
		
        imgCaredellino.bringToFront();
		imgCaredellino.startAnimation(swingAnimation);
		
		
        this.increaseArea();
		
	}
	
	private void increaseArea()
	{
		final LinearLayout subCategoryLayout = (LinearLayout) findViewById(R.id.weekdayll);
		subCategoryLayout.post(new Runnable() {

            @Override
            public void run() {
                for (int i = 0; i < subCategoryLayout.getChildCount(); i++) {
                    Rect delegateArea = new Rect();
                    TextView d = (TextView) subCategoryLayout.getChildAt(i);
                    d.getHitRect(delegateArea);
                    delegateArea.bottom += 100;
                    delegateArea.top += 200;
                    delegateArea.left += 100;
                    delegateArea.right += 100;

                    TouchDelegate expandedArea = new TouchDelegate(delegateArea, d);
                    subCategoryLayout.setTouchDelegate(expandedArea);
//                    if (View.class.isInstance(d.getParent())) {
//                        ((View) d.getParent()).setTouchDelegate(expandedArea);
//                    }
                }
            }
        });
		
		
	}
	
	
	
	private void initBirdMap()
	{
		birdsImageMap.put("imgCardellino", "bird_cardellino");
		birdsImageMap.put("imgPasseraSc", "bird_passera");
		birdsImageMap.put("imgMerlo", "bird_merlo");
		
	}
	private void addClickListenerToBirdView(View birdImageView)
	{
		int id = birdImageView.getId();
		final String imageId = getResources().getResourceEntryName(id); // ritorna ad es. "imgCardellino
		if (SetAlarmClockActivity.birdsImageMap.containsKey(imageId))
		{
			birdImageView.setOnClickListener(new OnClickListener() {
		        // Start new list activity
		        public void onClick(View birdImgView) {
		            birdChoosed = birdsImageMap.get(imageId);  // a questo punto es. "bird_cardellino"
		            
		            this.stopAll();
		            birdImgView.startAnimation(swingAnimation);
		            
		             if(mp1 != null)
		            	 mp1.stop();
		             mp1 = MediaPlayer.create( act, 
		            		getResources().getIdentifier(birdChoosed, "raw", getPackageName()));
		    		
		            mp1.setLooping(false);
		    		mp1.start();

		        }
		        
		        
		        public void stopAll()
		        {
		        	LinearLayout ll = (LinearLayout) findViewById(R.id.firstlinebird);
		    		for(int i=0; i< ll.getChildCount(); i++)
		    		{
		    			View v = ll.getChildAt(i);
		    			if(v instanceof ImageView)
		    				v.clearAnimation();
		    		}
		    		ll = (LinearLayout) findViewById(R.id.secondlinebirds);
		    		for(int i=0; i< ll.getChildCount(); i++)
		    		{
		    			View v = ll.getChildAt(i);
		    			if(v instanceof ImageView)
		    				v.clearAnimation();
		    		}
		        	
		        }
			
			
			
			}
					
		    );
		}
		
	}
	private void addClickListenerBirds()
	{
		LinearLayout ll = (LinearLayout) findViewById(R.id.firstlinebird);
		for(int i=0; i< ll.getChildCount(); i++)
		{
			View v = ll.getChildAt(i);
			if(v instanceof ImageView)
				addClickListenerToBirdView(v);
		}
		ll = (LinearLayout) findViewById(R.id.secondlinebirds);
		for(int i=0; i< ll.getChildCount(); i++)
		{
			View v = ll.getChildAt(i);
			if(v instanceof ImageView)
				addClickListenerToBirdView(v);
		}
	}
	
	
	
	
	private String pad(String number) { return ((number.length() == 1) ? "0" + number : number); } 
	
	public void addCurrentAlarmClock(View view)
	{
		TimePicker tp = (TimePicker) findViewById(R.id.timePicker1);
		tp.clearFocus();
		
		Integer h = tp.getCurrentHour();
		Integer m = tp.getCurrentMinute();
		this.timeChoosed = h.toString() + ":"  + pad( m.toString()) ;
		
		
		Intent i = new Intent(getApplicationContext(), AlarmsHome.class);
		i.putExtra(SetAlarmClockActivity.ACTIVE_DAYS_MESSAGE, this.activeDays);
		i.putExtra(SetAlarmClockActivity.TIME_MESSAGE, this.timeChoosed);
		i.putExtra(SetAlarmClockActivity.BIRD, this.birdChoosed);
		i.putExtra(SetAlarmClockActivity.ACTIVE_ALARM, this.activeAlarm);
		
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


