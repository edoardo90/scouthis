package it.poli.android.scoutthisme;

import it.poli.android.scouthisme.R;
import it.poli.android.scoutthisme.fragments.AlarmsFragment;
import it.poli.android.scoutthisme.fragments.FindFriendsFragment;
import it.poli.android.scoutthisme.fragments.GpsFragment;
import it.poli.android.scoutthisme.fragments.NewsFeedFragment;
import it.poli.android.scoutthisme.fragments.StepCounterFragment;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;

import com.viewpagerindicator.IconPagerAdapter;
import com.viewpagerindicator.TabPageIndicator;

public class MainTabsWithIcons extends FragmentActivity {
    private static final String[] CONTENT = new String[] { "News", "GPS", "Pedometer", "Trovamici", "Alarm" };
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

        FragmentPagerAdapter adapter = new GoogleMusicAdapter(getSupportFragmentManager());

        ViewPager pager = (ViewPager)findViewById(R.id.pager);
        pager.setAdapter(adapter);

        TabPageIndicator indicator = (TabPageIndicator)findViewById(R.id.indicator);
        indicator.setViewPager(pager);
    }

    class GoogleMusicAdapter extends FragmentPagerAdapter implements IconPagerAdapter {
        public GoogleMusicAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            /////OLD IMPLEMENTATION////return new EmptyFragment();//TestFragment.newInstance(CONTENT[position % CONTENT.length]);
			final int ZERO_OFFESET = 1;
			switch (position) {
			case 1 - ZERO_OFFESET:
				return new NewsFeedFragment();
			case 2 - ZERO_OFFESET:
				return new GpsFragment();
			case 3 - ZERO_OFFESET:
				return new StepCounterFragment();
			case (4 - ZERO_OFFESET):
				return new FindFriendsFragment();
			case 5 - ZERO_OFFESET:
				return new AlarmsFragment();
			default:
				return new AlarmsFragment();
			}
        }

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
