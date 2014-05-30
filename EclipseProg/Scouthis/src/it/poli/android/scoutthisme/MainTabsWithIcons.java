package it.poli.android.scoutthisme;

import it.poli.android.scouthisme.R;
import it.poli.android.scoutthisme.fragments.AlarmsFragment;
import it.poli.android.scoutthisme.fragments.FindFriendsFrameFragment;
import it.poli.android.scoutthisme.fragments.GpsFrameFragment;
import it.poli.android.scoutthisme.fragments.NewsFeedFragment;
import it.poli.android.scoutthisme.fragments.StepCounterFrameFragment;
import it.poli.android.scoutthisme.social.FriendshipsUpdatesTrigger;
import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;

import com.viewpagerindicator.IconPagerAdapter;
import com.viewpagerindicator.TabPageIndicator;

public class MainTabsWithIcons extends ActionBarActivity
{
    private static final String[] CONTENT = new String[] {"News", "GPS", "Run", "Trovamici", "Alarm"};
    
    private static final int[] ICONS = new int[] {
            R.drawable.actionb_news,
            R.drawable.actionb_gps,
            R.drawable.actionb_foot,
            R.drawable.actionb_friends1,
            R.drawable.actionb_alarm};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_iconed_tabs);
	
		//getSupportActionBar().setBackgroundDrawable((Drawable) (getResources().getDrawable(R.drawable.sfnd_tit_menu)));
        getSupportActionBar().hide();
        
        FragmentPagerAdapter adapter = new GoogleMusicAdapter(getSupportFragmentManager());

        ViewPager pager = (ViewPager)findViewById(R.id.pager);
        pager.setAdapter(adapter);

        TabPageIndicator indicator = (TabPageIndicator)findViewById(R.id.indicator);
        indicator.setViewPager(pager);
        
        scheduleFBUpdateFriendshipAlarm();
    }
    
	public void scheduleFBUpdateFriendshipAlarm() {
		// Construct an intent that will execute the MyAlarmReceiver
		Intent intent = new Intent(getApplicationContext(), FriendshipsUpdatesTrigger.class);
		// Create a PendingIntent to be triggered when the alarm goes off
		final PendingIntent pIntent = PendingIntent.getBroadcast(this,
				Constants.INTENT_FRIENDSHIPUPDATESTRIGGER_REQUESTCODE, intent, PendingIntent.FLAG_UPDATE_CURRENT);
		// Setup periodic alarm every TOT - SEE CONSTANTS  seconds
		long firstMillis = System.currentTimeMillis(); // first run of alarm is immediate

		AlarmManager fbUpdateFriendsAlarm = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
		fbUpdateFriendsAlarm.setInexactRepeating(AlarmManager.RTC_WAKEUP, firstMillis, Constants.TIME_UPDATE_FRIENDSLIST, pIntent);
	}
    
   /* @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	MenuInflater inflater = getMenuInflater();
    	inflater.inflate(R.menu.main_activity_actions, menu);
    	return super.onCreateOptionsMenu(menu);
    }*/

    class GoogleMusicAdapter extends FragmentPagerAdapter implements IconPagerAdapter {
        public GoogleMusicAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position)
        {    
			switch (position) {
			case 0:
				return new NewsFeedFragment();
			case 1:
				return new GpsFrameFragment();
			case 2:
				return new StepCounterFrameFragment();
			case 3:
				return new FindFriendsFrameFragment();
			case 4:
				return new AlarmsFragment();
			default:
				return new AlarmsFragment();
			}
        }

        @SuppressLint("DefaultLocale")
		@Override
        public CharSequence getPageTitle(int position) {
            return CONTENT[position % CONTENT.length].toUpperCase();
        }

        @Override public int getIconResId(int index) {
          return ICONS[index];
        }

      @Override
        public int getCount() {
          return CONTENT.length;
        }
    }
}
