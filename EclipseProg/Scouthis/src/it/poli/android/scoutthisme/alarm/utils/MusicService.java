package it.poli.android.scoutthisme.alarm.utils;

import it.poli.android.scoutthisme.Constants;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.IBinder;

public class MusicService extends Service {

	
	MediaPlayer player;
	Alarm userAlarm;
	AudioManager am ;


	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
	
	
	@Override
	public void onCreate() {
		
	}

	@Override
	public void onDestroy() {

		player.stop();
	}
	
	@Override
	public void onStart(Intent intent, int startid) {
		
		
		this.userAlarm = (Alarm)intent.getSerializableExtra(Constants.INTENT_ALARM);
		
		int idSoundBird = this.getBirdSoundId();
		
		player = MediaPlayer.create(this, idSoundBird);
		player.setLooping(true);
		
		AudioManager am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
		am.setStreamVolume(AudioManager.STREAM_MUSIC, am.getStreamMaxVolume(AudioManager.STREAM_MUSIC), 0);
		
		player.start();
	}
	
	
	private int getBirdSoundId()
	{
		String bird = (userAlarm != null) ? userAlarm.getBird() : "bird_cardellino";
		return this.getResources().getIdentifier(bird, "raw", this.getPackageName());
	}

	
	
	
}	