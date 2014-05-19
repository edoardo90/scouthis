package it.poli.android.scoutthisme.alarm.utils;

import it.poli.android.scouthisme.R;
import it.poli.android.scoutthisme.Constants;
import it.poli.android.scoutthisme.MainActivity;
import it.poli.android.scoutthisme.tools.Alarm;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class WakeUpUserActivity extends Activity
{
	MediaPlayer alarmPlayer;
	Timer timer;
	Alarm userAlarm;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		timer = null;
		userAlarm = null;
		setContentView(R.layout.alarms_wake_user);
		
		Intent intent = getIntent();
		if(intent != null && intent.getExtras() != null) {
			this.userAlarm = (Alarm)intent.getSerializableExtra(Constants.INTENT_ALARM);
		}
		
		this.setGraphics();
		this.setSound();
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		if(timer != null)
		{
			timer.cancel();
			timer.purge();
			timer = null;
		}
		alarmPlayer.stop();
		Log.i("pause", "timer purged");
	}
	
	@Override
	public void onWindowFocusChanged(boolean hasFocus)
	{
		LinearLayout linearLayout = (LinearLayout)findViewById(R.id.llBirdEscape);

		float height = linearLayout.getHeight();
		float width = linearLayout.getWidth();

		float uH = height / 100;
		float uL = width / 100;

		ImageView birdImg = (ImageView)findViewById(R.id.imgBird);
		this.moveRandom(uH, uL, birdImg);
	}
	
	private void setSound()
	{		
		alarmPlayer = MediaPlayer.create(this, this.getBirdSoundId());
		alarmPlayer.setLooping(true);
		alarmPlayer.start();
	}

	private void setGraphics()
	{
		ImageView birdImg = (ImageView)findViewById(R.id.imgBird);
		birdImg.setImageResource(this.getBirdImgId());
		birdImg.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				alarmPlayer.stop(); 
				timer.cancel();
				timer.purge();
				timer = null;
				loadAlarmClockHome();
				
			}
		});
	}

	private void loadAlarmClockHome()
	{
		Log.w("wake up user", "you clickd the bird, MTF! FADE......");
		final Handler handler = new Handler();
		handler.postDelayed(new Runnable() {
		@Override
		public void run() {
		
		
			
		Intent i = new Intent(getApplicationContext(), MainActivity.class);
		startActivity(i);
		overridePendingTransition(R.anim.fadein, R.anim.fadeout);
		
		
		}
		}, 1000); //ASPETTA UN SECONDO!!
		
	}
	private int getBirdImgId() 
	{
		String bird = (userAlarm != null) ? userAlarm.getBird() : "bird_cardellino_small";
		return this.getResources().getIdentifier(bird, "drawable", this.getPackageName());
	}

	private int getBirdSoundId()
	{
		String bird = (userAlarm != null) ? userAlarm.getBird() : "bird_cardellino";
		return this.getResources().getIdentifier(bird, "raw", this.getPackageName());
	}

	private void moveRandom(final float uH, final float uL, final ImageView img)
	{
		timer = new Timer();
		final Handler handler = new Handler();

		TimerTask doAsynchronousTask = new TimerTask() {       
			@Override
			public void run() {
				handler.post(new Runnable() {
					public void run() { 
						try {
							Random r = new Random();
							// Set margins randomly to move the bird
							int py = r.nextInt(60) + 1;
							int px = r.nextInt(60) + 1;

							LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
									LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
							
							lp.setMargins((int)(px*uL),(int)(py*uH),0,0);
							img.setLayoutParams(lp);

							Log.i("timer", " px * uL :" + (int)(px * uL));
						} catch (Exception e) { /* TODO Auto-generated catch block */ }
					}
				});
			}
		};
		timer.schedule(doAsynchronousTask, 0, 1000); 
	}
}