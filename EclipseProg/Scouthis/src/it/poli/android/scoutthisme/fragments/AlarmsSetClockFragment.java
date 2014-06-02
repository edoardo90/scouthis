package it.poli.android.scoutthisme.fragments;

import it.poli.android.scouthisme.R;
import it.poli.android.scoutthisme.Constants;
import it.poli.android.scoutthisme.alarm.utils.AlarmUtils;

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

@SuppressLint("SimpleDateFormat")
public class AlarmsSetClockFragment extends Fragment
{
	boolean[] activeDays = new boolean[]{true, true, true, true, true, true, true}; 
	private String timeChoosed;
	private String birdChoosed = "bird_cardellino";
	private boolean activeAlarm = true;
	Animation swingAnimation ;

	public static Map<String, String> birdsImageMap  = new HashMap<String, String>();

	private Activity mAct;
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

		mAct = getActivity();
		
		this.initClickListeners();
		swingAnimation = AnimationUtils.loadAnimation(mAct, R.anim.swing_bird);

		ImageView imgCaredellino = (ImageView)mAct.findViewById(R.id.imgCardellino);

		this.addClickListenerBirds(R.id.alarm_firstline_birds, R.id.alarm_secondline_birds, R.id.alarms_centerline_birds);

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
		LinearLayout ll = (LinearLayout) mAct.findViewById(R.id.weekdayll);
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
				updateAfterDayClick(v);
			}
		});
	}

	private void initDoneButton()
	{
		Button b = (Button)mAct.findViewById(R.id.btnAlarmSetOk);
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
				mAct.findViewById(R.id.weekdayll);
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

	private void addClickListenerToBirdView(View birdImageView)
	{
		int id = birdImageView.getId();
		final String imageId = getResources().getResourceEntryName(id); // ritorna ad es. "imgCardellino
		if (Constants.birdsImageMap.containsKey(imageId))
		{
			birdImageView.setOnClickListener(new OnClickListener() {
				// Start new listViewAlarms activity
				public void onClick(View birdImgView) {
					birdChoosed = Constants.birdsImageMap.get(imageId);  // a questo punto es. "bird_cardellino"

					this.stopAll(R.id.alarm_firstline_birds, R.id.alarm_secondline_birds, R.id.alarms_centerline_birds);
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

				public void stopAll(int...birdsLines)
				{
					View view;
					for(int birdsLine : birdsLines)
					{
						ViewGroup groupBirdsLine = (ViewGroup) mAct.findViewById(birdsLine);
						for(int i=0; i< groupBirdsLine.getChildCount(); i++)
						{
							view = groupBirdsLine.getChildAt(i);
							if(view instanceof ImageView)
								view.clearAnimation();
						}
					}
	
				}			
			});
		}
	}
	
	/**
	 * birdsLines are the layouts that contains the birds
	 * to be called e.g.   addClickListenerBirds(R.id.line1,  R.id.line2)
	 */
	private void addClickListenerBirds(int... birdsLines)
	{
		for(int birdsLine : birdsLines)
		{
			ViewGroup groupBirdsLine = (ViewGroup) mAct.findViewById(birdsLine);
			for(int i=0; i< groupBirdsLine.getChildCount(); i++)
			{
				View v = groupBirdsLine.getChildAt(i);
				if(v instanceof ImageView)
					addClickListenerToBirdView(v);
			}
		}

	}

	public void addCurrentAlarmClock(View view)
	{
		
		Integer hours = getArguments().getInt(Constants.ALARM_HOUR);
		Integer minutes = getArguments().getInt(Constants.ALARM_MINUTE);
		this.timeChoosed = AlarmUtils.addZeroToOneDigit(hours) + ":" + 
						   AlarmUtils.addZeroToOneDigit(minutes) ;

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

	public void updateAfterDayClick(View view)
	{
		/* Toggle day status */
		int pos = AlarmUtils.dayToPos(getResources().getResourceEntryName(view.getId()));
		activeDays[pos] = !activeDays[pos];
		/* Toggle day color */
		TextView textView = (TextView) mAct.findViewById(view.getId());
		int color = (textView.getCurrentTextColor() == Color.WHITE) ? Color.CYAN : Color.WHITE;
		textView.setTextColor(color);
	}


}