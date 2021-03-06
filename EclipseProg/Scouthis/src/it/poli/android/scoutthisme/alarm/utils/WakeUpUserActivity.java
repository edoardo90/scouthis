package it.poli.android.scoutthisme.alarm.utils;

import it.poli.android.scouthisme.R;
import it.poli.android.scoutthisme.Constants;
import it.poli.android.scoutthisme.MainActivityWithIcons;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class WakeUpUserActivity extends Activity
{
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
		this.playSound();
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
		
		
	}
	
	@Override
	public void onWindowFocusChanged(boolean hasFocus)
	{
		RelativeLayout linearLayout = (RelativeLayout)findViewById(R.id.llBirdEscape);

		float height = linearLayout.getHeight();
		float width = linearLayout.getWidth();

		float uH = height / 100;
		float uL = width / 100;

		ImageView birdImg = (ImageView)findViewById(R.id.imgBird);
		
		TextView txtExplaining = (TextView) findViewById(R.id.wake_txt_explain);
		txtExplaining.setText(userAlarm.getExplainingMessage());
		
		this.moveRandom(uH, uL, birdImg);
	}
	
	
	
	

	private void enlargeBird(ImageView birdImg)
	{
		float w = birdImg.getDrawable().getIntrinsicWidth();
		float h = birdImg.getDrawable().getIntrinsicHeight();
		w *= 1.5f;
		h *= 1.5f;
		birdImg.getLayoutParams().height = (int)h;
		birdImg.getLayoutParams().width = (int)w;
	}
	
	private void setGraphics()
	{
		ImageView birdImg = (ImageView)findViewById(R.id.imgBird);
		birdImg.setImageResource(this.getBirdImgId());
		
				
		birdImg.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				stopAlarm(); 
				
				if(timer != null)
				{
					timer.cancel();
					timer.purge();
					timer = null;
				}
				loadAlarmClockHome();
				
			}
		});
	}
	
	private void stopAlarm()
	{
		Intent musicBirdService = new Intent(this, MusicService.class);
		stopService(musicBirdService);
	}
	
	private void playSound() {

		Intent musicBirdService = new Intent(this, MusicService.class);
		musicBirdService.putExtra(Constants.INTENT_ALARM, this.userAlarm);
		startService(musicBirdService);
	}

	
	
	
	
	private void loadAlarmClockHome()
	{
		final Handler handler = new Handler();
		handler.postDelayed(new Runnable() {
		@Override
		public void run() {
		
		
			
		Intent i = new Intent(getApplicationContext(), MainActivityWithIcons.class);
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

							RelativeLayout.LayoutParams relParams = new RelativeLayout.LayoutParams(
									RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
							
							relParams.setMargins((int)(px*uL),(int)(py*uH),0,0);
							img.setLayoutParams(relParams);
							enlargeBird(img);

							
						} catch (Exception e) { /* TODO Auto-generated catch block */ }
					}
				});
			}
		};
		timer.schedule(doAsynchronousTask, 0, Constants.ALARM_SPEED_BIRD); 
	}
}