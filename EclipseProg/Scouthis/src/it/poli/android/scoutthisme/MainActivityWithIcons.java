package it.poli.android.scoutthisme;

import it.poli.android.scouthisme.R;
import it.poli.android.scoutthisme.fragments.AlarmsFragment;
import it.poli.android.scoutthisme.fragments.FindFriendsFrameFragment;
import it.poli.android.scoutthisme.fragments.GpsFrameFragment;
import it.poli.android.scoutthisme.fragments.NewsFeedFragment;
import it.poli.android.scoutthisme.fragments.StepCounterFrameFragment;

import java.util.Locale;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.viewpagerindicator.IconPagerAdapter;
import com.viewpagerindicator.TabPageIndicator;

public class MainActivityWithIcons extends ActionBarActivity
{
	private String[] CONTENT;
	private int[] ICONS = new int[] {R.drawable.actionb_news, R.drawable.actionb_gps,
			R.drawable.actionb_foot, R.drawable.actionb_friends1, R.drawable.actionb_alarm};

	FragmentPagerAdapter adapter;
	
	static int currentTheme = -1;
	
	private boolean activityIsDestroying;

	public boolean isActivityIsDestroying() {
		return activityIsDestroying;
	}

	public void setActivityIsDestroying(boolean activityIsDestroying) {
		this.activityIsDestroying = activityIsDestroying;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		if (currentTheme < 0)
			currentTheme = R.style.Theme_ScouthisMe;
		setTheme(currentTheme);

		CONTENT = new String[] {getString(R.string.fragments_1_title),
				getString(R.string.fragments_2_title), getString(R.string.fragments_3_title),
				getString(R.string.fragments_4_title), getString(R.string.fragments_5_title)};

		setContentView(R.layout.activity_main_iconed_tabs);

		adapter = new ScouthisMeAdapter(getSupportFragmentManager());
		ViewPager pager = (ViewPager)findViewById(R.id.pager);
		pager.setAdapter(adapter);

		TabPageIndicator indicator = (TabPageIndicator)findViewById(R.id.indicator);
		indicator.setViewPager(pager);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    // Inflate the menu items for use in the action bar
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.scouthisme_actionbar_menu, menu);
	    return super.onCreateOptionsMenu(menu);
	}
	
	@SuppressLint("NewApi")
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle presses on the action bar items
	    switch (item.getItemId()) {
	        case R.id.action_theme_sky:
	        	currentTheme = R.style.Theme_Sky;
	            break;
	        case R.id.action_theme_darkforest:
	        	currentTheme = R.style.Theme_DarkForest;
	        	break;
	        default:
	        case R.id.action_theme_scouthisme:
	        	currentTheme = R.style.Theme_ScouthisMe;
	        	break;
	    }
	    
        if (android.os.Build.VERSION.SDK_INT >= 11)
        {
        	setActivityIsDestroying(true);
            super.recreate();
        }
        else
        {
        	setActivityIsDestroying(true);
            startActivity(getIntent());
            finish();
        }
        
	    return true;
	}

	class ScouthisMeAdapter extends FragmentPagerAdapter implements IconPagerAdapter
	{
		public ScouthisMeAdapter(FragmentManager fm)
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

	@Override
	public void onBackPressed() { }
}