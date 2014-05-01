package it.poli.alarmb.sound;

import it.poli.android.scouthisme.R;
import it.poli.alarmb.alarmstime.AlarmsTool;
import it.poli.alarmb.alarmstime.UserAlarm;

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

public class SoundActivity extends Activity {

	MediaPlayer mp1;
	Timer timer = null;
	UserAlarm uAlarm = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sound);

		Intent intent=null;
		intent = getIntent();

		if(intent != null && intent.getExtras() != null)
		{
			UserAlarm ua =  (UserAlarm) intent.getSerializableExtra(AlarmsTool.alarmExtra);
			this.uAlarm = ua;
		}


		this.graphicPart();
		this.soundPart();

	}
	private void soundPart()
	{
		
		int resID = this.getBirdSoundId(); 
		mp1 = MediaPlayer.create(this, resID );
		mp1.setLooping(true);
		mp1.start();

	}

	private void graphicPart()
	{

		ImageView birdImg = (ImageView)findViewById(R.id.imgBird);

		birdImg.setImageResource(this.getBirdImgId());
		

		birdImg.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				stop();
			}
		});

	}

	private int getBirdImgId() 
	{
		int resID = 0;
		if(uAlarm != null)
		{
			String bird = uAlarm.getBird();
			resID = this.getResources().getIdentifier(bird, "drawable", this.getPackageName());
		}
		else
		{
			resID = this.getResources().getIdentifier("bird_cardellino_small", "drawable", this.getPackageName());
			Log.i("sound act", " no bird IMAGE found !");
		}
		return resID;
	}
	
	private int getBirdSoundId()
	{
		int resID = 0;
		if(uAlarm != null)
		{
			String bird = uAlarm.getBird();
			resID = this.getResources().getIdentifier(bird, "raw", this.getPackageName());
		}
		else
		{
			resID = this.getResources().getIdentifier("bird_cardellino", "raw", this.getPackageName());
			Log.i("sound act", " no bird SOUND found !");
		}
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
							int py = r.nextInt(60 - 1 + 1) + 1;
							int px = r.nextInt(60 - 1 + 1) + 1;

							LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
							lp.setMargins( (int) (px * uL) , (int)( py * uH ), 0, 0);
							img.setLayoutParams(lp);

							Log.i("timer", " px * uL :" + (int)(px * uL));

						}
						catch (Exception e) {
							// TODO Auto-generated catch block
						}
					}
				});
			}
		};
		timer.schedule(doAsynchronousTask, 0, 1000); 


	}


	@Override
	public void onWindowFocusChanged(boolean hasFocus) {


		LinearLayout ll = (LinearLayout)findViewById(R.id.llBirdEscape);

		float height = ll.getHeight();
		float width = ll.getWidth();

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
		mp1.stop();
		Log.i("pause", "timer purged");
	}

	@Override
	public void onResume()
	{
		super.onResume();

	}

	public void stop()
	{
		mp1.stop();
	}

	@Override
	public void onDestroy() {
		super.onDestroy ();

	}

}