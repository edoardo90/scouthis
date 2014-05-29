package it.poli.android.scoutthisme;

import it.poli.android.scouthisme.R;
import it.poli.android.scoutthisme.fragments.AlarmsFragment;
import it.poli.android.scoutthisme.fragments.FindFriendsFrameFragment;
import it.poli.android.scoutthisme.fragments.GpsFrameFragment;
import it.poli.android.scoutthisme.fragments.NewsFeedFragment;
import it.poli.android.scoutthisme.fragments.StepCounterFrameFragment;
import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;

import com.viewpagerindicator.IconPagerAdapter;
import com.viewpagerindicator.TabPageIndicator;

public class MainTabsWithIcons extends ActionBarActivity {
    private static final String[] CONTENT = new String[] { 
    		"News", "GPS", 
    
    	   "Run"  , "Trovamici", "Alarm" };
    private static final int[] ICONS = new int[] {
            R.drawable.actionb_news,
            R.drawable.actionb_gps,
            R.drawable.actionb_foot,
            R.drawable.actionb_friends1,
            R.drawable.actionb_alarm,
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_iconed_tabs);
        
		final ActionBar actionBar = getSupportActionBar();

        FragmentPagerAdapter adapter = new GoogleMusicAdapter(getSupportFragmentManager());

        ViewPager pager = (ViewPager)findViewById(R.id.pager);
        pager.setAdapter(adapter);

        TabPageIndicator indicator = (TabPageIndicator)findViewById(R.id.indicator);
        indicator.setViewPager(pager);
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
        public Fragment getItem(int position) {
            
			final int ZERO_OFFESET = 1;
			switch (position) {
			case 1 - ZERO_OFFESET:
				return new NewsFeedFragment();
			case 2 - ZERO_OFFESET:
				return new GpsFrameFragment();
			case 3 - ZERO_OFFESET:
				return new StepCounterFrameFragment();
			case (4 - ZERO_OFFESET):
				return new FindFriendsFrameFragment();
			case 5 - ZERO_OFFESET:
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
