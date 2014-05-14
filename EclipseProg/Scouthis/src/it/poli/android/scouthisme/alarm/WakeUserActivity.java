package it.poli.android.scouthisme.alarm;

import it.poli.android.scouthisme.R;
import it.poli.android.scoutthisme.Constants;

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

public class WakeUserActivity extends Activity
{
	MediaPlayer alarmPlayer;
	Timer timer = null;
	UserAlarm userAlarm = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.alarms_wake_user);

		Intent intent = getIntent();
		if(intent != null && intent.getExtras() != null)
			this.userAlarm = (UserAlarm) intent.getSerializableExtra(Constants.INTENT_ALARM);

		this.graphicPart();
		this.soundPart();
	}
	
	private void soundPart()
	{
		int resID = this.getBirdSoundId(); 
		
		alarmPlayer = MediaPlayer.create(this, resID );
		alarmPlayer.setLooping(true);
		alarmPlayer.start();
	}

	private void graphicPart()
	{
		ImageView birdImg = (ImageView)findViewById(R.id.imgBird);
		birdImg.setImageResource(this.getBirdImgId());
		birdImg.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				//TODO
				alarmPlayer.stop(); /*Stops the music BUT NOT the animation (WHY?)*/
			}
		});
	}

	private int getBirdImgId() 
	{
		int resID = 0;
		
		String bird;
		if(userAlarm != null)
			bird = userAlarm.getBird();
		else
			bird = "bird_cardellino_small";
		resID = this.getResources().getIdentifier(bird, "drawable", this.getPackageName());
		
		return resID;
	}

	private int getBirdSoundId()
	{
		int resID = 0;
		
		String bird;
		if(userAlarm != null)
			bird = userAlarm.getBird();
		else
			bird = "bird_cardellino";
		resID = this.getResources().getIdentifier(bird, "raw", this.getPackageName());
		
		return resID;
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
						try 
						{
							Random r = new Random();
							
							// Set margins randomly to move the bird
							int py = r.nextInt(60) + 1;
							int px = r.nextInt(60) + 1;

							LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
									LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
							
							lp.setMargins( (int) (px * uL) , (int)( py * uH ), 0, 0);
							img.setLayoutParams(lp);

							Log.i("timer", " px * uL :" + (int)(px * uL));
						}
						catch (Exception e) { /* TODO Auto-generated catch block */ }
					}
				});
			}
		};
		timer.schedule(doAsynchronousTask, 0, 1000); 
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

	@Override
	protected void onPause() {
		super.onPause();
		if(timer != null)
		{
			timer.cancel();
			timer.purge();
		}
		alarmPlayer.stop();
		Log.i("pause", "timer purged");
	}
}