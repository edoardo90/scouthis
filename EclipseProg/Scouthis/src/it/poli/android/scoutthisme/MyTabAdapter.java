package it.poli.android.scoutthisme;

import it.poli.android.scoutthisme.fragments.AlarmsFragment;
import it.poli.android.scoutthisme.fragments.FindFriendsFrameFragment;
import it.poli.android.scoutthisme.fragments.GpsFrameFragment;
import it.poli.android.scoutthisme.fragments.NewsFeedFragment;
import it.poli.android.scoutthisme.fragments.StepCounterFrameFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class MyTabAdapter extends FragmentPagerAdapter{
	
//	String[] CONTENT = new String[] {getString(R.string.fragments_1_title),
//			getString(R.string.fragments_2_title), getString(R.string.fragments_3_title),
//			getString(R.string.fragments_4_title), getString(R.string.fragments_5_title)};
	
	public MyTabAdapter(FragmentManager fm) {
		super(fm);
		
	}

	@Override
	public Fragment getItem(int position) {
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

	@Override
	public int getCount() {
		return 0;
	}

}
