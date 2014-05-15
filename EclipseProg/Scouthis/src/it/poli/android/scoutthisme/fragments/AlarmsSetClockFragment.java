package it.poli.android.scoutthisme.fragments;

import it.poli.android.scouthisme.R;
import it.poli.android.scoutthisme.Constants;
import it.poli.android.scoutthisme.tools.AlarmUtils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Rect;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.TouchDelegate;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.TimePicker.OnTimeChangedListener;

@SuppressLint("SimpleDateFormat")
public class AlarmsSetClockFragment extends Fragment  implements OnTimeChangedListener
{
	boolean[] activeDays = new boolean[]{true, true, true, true, true, true, true}; 
	private Calendar mCalendar;

	private String hourAM_PM = "PM";
	private String timeChoosed;
	private String birdChoosed = "bird_cardellino";
	private boolean activeAlarm = true;
	Animation swingAnimation ;

	public static Map<String, String> birdsImageMap  = new HashMap<String, String>();

	private Activity act;
	private MediaPlayer alarmPlayer = null;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		super.onCreateView(inflater, container, savedInstanceState);
		View rootView = inflater.inflate(R.layout.fragment_section_alarms_set_clock, container, false);
		return rootView;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		this.initClickListeners();

		swingAnimation =   AnimationUtils.loadAnimation(this.getActivity().
				getApplicationContext(), R.anim.swing_bird);
		this.initBirdMap();

		ImageView imgCaredellino = (ImageView)this.getActivity().findViewById(R.id.imgCardellino);

		this.addClickListenerBirds();

		mCalendar = Calendar.getInstance();

		TimePicker tp = (TimePicker)this.getActivity().findViewById(R.id.timePicker1);
		tp.setIs24HourView(false);
		tp.setOnTimeChangedListener(this);

		imgCaredellino.bringToFront();
		imgCaredellino.startAnimation(swingAnimation);

		this.increaseArea();
	}

	private void initClickListeners() {
		this.initDays();
		this.initDoneButton();
	}

	private void initDays()
	{	
		LinearLayout ll = (LinearLayout) this.getActivity().findViewById(R.id.weekdayll);
		for(int i=0; i< ll.getChildCount(); i++)
		{
			View v = ll.getChildAt(i);
			if(v instanceof TextView)
				addClickListenerToDayView((TextView)v);
		}
	}
	private void addClickListenerToDayView(TextView tv)
	{
		tv.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dayClicked(v);
			}
		});
	}

	private void initDoneButton()
	{
		Button b = (Button)this.getActivity().findViewById(R.id.btnAlarmSetOk);
		b.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				addCurrentAlarmClock(v);
			}
		});
	}

	private void increaseArea()
	{
		final LinearLayout subCategoryLayout = (LinearLayout)
				this.getActivity().findViewById(R.id.weekdayll);
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
		if (AlarmsSetClockFragment.birdsImageMap.containsKey(imageId))
		{
			birdImageView.setOnClickListener(new OnClickListener() {
				// Start new list activity
				public void onClick(View birdImgView) {
					birdChoosed = birdsImageMap.get(imageId);  // a questo punto es. "bird_cardellino"

					this.stopAll();
					birdImgView.startAnimation(swingAnimation);

					if(alarmPlayer != null)
						alarmPlayer.stop();
					Activity getAct = getActivity();
					Resources res = getResources();
					int idRawSound = res.getIdentifier(birdChoosed, "raw", getAct.getPackageName());

					alarmPlayer = MediaPlayer.create( getAct, idRawSound);

					alarmPlayer.setLooping(false);
					alarmPlayer.start();
				}

				public void stopAll()
				{
					LinearLayout ll = (LinearLayout) getActivity().findViewById(R.id.firstlinebird);
					for(int i=0; i< ll.getChildCount(); i++)
					{
						View v = ll.getChildAt(i);
						if(v instanceof ImageView)
							v.clearAnimation();
					}
					ll = (LinearLayout) getActivity().findViewById(R.id.secondlinebirds);
					for(int i=0; i< ll.getChildCount(); i++)
					{
						View v = ll.getChildAt(i);
						if(v instanceof ImageView)
							v.clearAnimation();
					}	
				}			
			});
		}
	}
	
	private void addClickListenerBirds()
	{
		LinearLayout ll = (LinearLayout) this.getActivity().findViewById(R.id.firstlinebird);
		for(int i=0; i< ll.getChildCount(); i++)
		{
			View v = ll.getChildAt(i);
			if(v instanceof ImageView)
				addClickListenerToBirdView(v);
		}
		ll = (LinearLayout) this.getActivity().findViewById(R.id.secondlinebirds);
		for(int i=0; i< ll.getChildCount(); i++)
		{
			View v = ll.getChildAt(i);
			if(v instanceof ImageView)
				addClickListenerToBirdView(v);
		}
	}

	public void addCurrentAlarmClock(View view)
	{
		TimePicker timePicker = (TimePicker) this.getActivity().findViewById(R.id.timePicker1);
		timePicker.clearFocus();

		Integer hours = timePicker.getCurrentHour();
		Integer minutes = timePicker.getCurrentMinute();
		this.timeChoosed = AlarmUtils.addZeroToOneDigit(hours) + ":"  + AlarmUtils.addZeroToOneDigit(minutes) ;

		// Start the main alarms fragment
		Fragment alarmsHomeFrag = new AlarmsHomeFragment();

		Bundle i = new Bundle();
		i.putString("edttext", "From Activity"); //TODO Remove?
		i.putBooleanArray(Constants.PARAM_ALARM_DAYS, this.activeDays);
		i.putString(Constants.PARAM_ALARM_TIME, this.timeChoosed);
		i.putSerializable(Constants.PARAM_ALARM_BIRD, this.birdChoosed);
		i.putBoolean(Constants.PARAM_ALARM_ACTIVE, this.activeAlarm);

		alarmsHomeFrag.setArguments(i);

		// Create new fragment and transaction
		FragmentTransaction transaction = getFragmentManager().beginTransaction();
		// Replace whatever is in the fragment_container view with this fragment,
		// and add the transaction to the back stack
		transaction.replace(R.id.alarms_frame, alarmsHomeFrag);
		transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
		transaction.addToBackStack(null);
		// Commit the transaction
		transaction.commit();
	}

	public void dayClicked(View view)
	{
		TextView tv = (TextView) this.getActivity().findViewById(view.getId());

		String dayName = getResources().getResourceEntryName(view.getId());
		int pos = dayToPos(dayName);

		activeDays[pos] = ! activeDays[pos];

		if (tv.getCurrentTextColor() == Color.WHITE)
			tv.setTextColor(Color.CYAN);
		else
			tv.setTextColor(Color.WHITE);		
	}

	private int dayToPos(String day)
	{
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