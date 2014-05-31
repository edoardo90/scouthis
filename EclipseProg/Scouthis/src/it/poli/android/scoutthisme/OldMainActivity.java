/*
 * Copyright 2014 Scouthis.Me Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package it.poli.android.scoutthisme;

import it.poli.android.scouthisme.R;
import it.poli.android.scoutthisme.fragments.AlarmsFragment;
import it.poli.android.scoutthisme.fragments.GpsFragment;
import it.poli.android.scoutthisme.fragments.NewsFeedFragment;
import it.poli.android.scoutthisme.fragments.StepCounterFragment;
import it.poli.android.scoutthisme.social.FriendshipsUpdatesTrigger;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBar.Tab;
import android.support.v7.app.ActionBarActivity;

public class OldMainActivity extends ActionBarActivity implements ActionBar.TabListener {

	/**
	 * The {@link android.support.v4.view.PagerAdapter} that will provide fragments for each of the
	 * three primary sections of the app. We use a {@link android.support.v4.app.FragmentPagerAdapter}
	 * derivative, which will keep every loaded fragment in memory. If this becomes too memory
	 * intensive, it may be best to switch to a {@link android.support.v4.app.FragmentStatePagerAdapter}.
	 */
	AppSectionsPagerAdapter mAppSectionsPagerAdapter;
	
	/**
	 * The {@link ViewPager} that will display the three primary sections of the app, one at a
	 * time.
	 */
	ViewPager mViewPager;
	String[] tabTitles;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		tabTitles = new String[]{};

	
		// Create the adapter that will return a fragment for each of the three primary sections
		// of the app.
		mAppSectionsPagerAdapter = new AppSectionsPagerAdapter(getSupportFragmentManager());

		// Set up the action bar.
		final ActionBar actionBar = getSupportActionBar();

		// Specify that the Home/Up button should not be enabled, since there is no hierarchical
		// parent.
		actionBar.setHomeButtonEnabled(false);

		// Specify that we will be displaying tabs in the action bar.
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

		// Set up the ViewPager, attaching the adapter and setting up a listener for when the
		// user swipes between sections.
		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(mAppSectionsPagerAdapter);
		mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
			@Override
			public void onPageSelected(int position) {
				// When swiping between different app sections, select the corresponding tab.
				// We can also use ActionBar.Tab#select() to do this if we have a reference to the
				// Tab.
				actionBar.setSelectedNavigationItem(position);
			}
		});

		// For each of the sections in the app, add a tab to the action bar.
		for (int i = 0; i < mAppSectionsPagerAdapter.getCount(); i++) {
			// Create a tab with text corresponding to the page title defined by the adapter.
			// Also specify this Activity object, which implements the TabListener interface, as the
			// listener for when this tab is selected.
			actionBar.addTab(
					actionBar.newTab()
					.setText(mAppSectionsPagerAdapter.getPageTitle(i))
					.setTabListener(this));
		}
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

	/**
	 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to one of the primary
	 * sections of the app.
	 */
	public  class AppSectionsPagerAdapter extends FragmentPagerAdapter {

		public AppSectionsPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int i) {
			final int ZERO_OFFESET = 1;
			switch (i) {
			case 1 - ZERO_OFFESET:
				return new NewsFeedFragment();
			case 2 - ZERO_OFFESET:
				return new GpsFragment();
			case 3 - ZERO_OFFESET:
				return new StepCounterFragment();
			/*case (4 - ZERO_OFFESET):
				return new FindFriendsFrameFragment(); */
			case 4 - ZERO_OFFESET:
				return new AlarmsFragment();
			default:
				return new AlarmsFragment();
			}
		}

		@Override
		public int getCount() {
			return tabTitles.length;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			return tabTitles[position];
		}
	}

	@Override
	public void onTabReselected(Tab arg0, android.support.v4.app.FragmentTransaction arg1) { }

	@Override
	public void onTabSelected(Tab tab,
			android.support.v4.app.FragmentTransaction ft)
	{
		mViewPager.setCurrentItem(tab.getPosition());
		setTitle(tab.getText());
	}

	@Override
	public void onTabUnselected(Tab arg0,
			android.support.v4.app.FragmentTransaction arg1) { }
}
