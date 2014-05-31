package it.poli.android.scoutthisme;

import java.util.Locale;

import it.poli.android.scouthisme.R;
import it.poli.android.scoutthisme.fragments.AlarmsFragment;
import it.poli.android.scoutthisme.fragments.FindFriendsFrameFragment;
import it.poli.android.scoutthisme.fragments.GpsFrameFragment;
import it.poli.android.scoutthisme.fragments.NewsFeedFragment;
import it.poli.android.scoutthisme.fragments.StepCounterFrameFragment;
import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;

import com.viewpagerindicator.IconPagerAdapter;
import com.viewpagerindicator.TabPageIndicator;

public class MainActivityWithIcons extends ActionBarActivity
{
	private Activity mAct;
	
	private String[] CONTENT;
	private int[] ICONS = new int[] {R.drawable.actionb_news, R.drawable.actionb_gps,
		R.drawable.actionb_foot, R.drawable.actionb_friends1, R.drawable.actionb_alarm};

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		
		mAct = this.getParent();
		CONTENT = new String[] {mAct.getString(R.string.fragments_1_title),
				mAct.getString(R.string.fragments_2_title), mAct.getString(R.string.fragments_3_title),
				mAct.getString(R.string.fragments_4_title), mAct.getString(R.string.fragments_5_title)};
		
		setContentView(R.layout.activity_main_iconed_tabs);

		//getSupportActionBar().setBackgroundDrawable((Drawable) (getResources().getDrawable(R.drawable.sfnd_tit_menu)));
		getSupportActionBar().hide();

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

	class GoogleMusicAdapter extends FragmentPagerAdapter implements IconPagerAdapter
	{
		public GoogleMusicAdapter(FragmentManager fm)
		{
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

		@Override
		public CharSequence getPageTitle(int position) {
			return CONTENT[position % CONTENT.length].toUpperCase(Locale.ITALY);
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