package it.poli.android.scoutthisme;
 
import it.poli.android.scouthisme.R;
import it.poli.android.scoutthisme.fragments.AlarmsFragment;
import it.poli.android.scoutthisme.fragments.FindFriendsFrameFragment;
import it.poli.android.scoutthisme.fragments.GpsFrameFragment;
import it.poli.android.scoutthisme.fragments.NewsFeedFragment;
import it.poli.android.scoutthisme.fragments.StepCounterFrameFragment;

import java.util.Locale;

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
        private String[] CONTENT;
        private int[] ICONS = new int[] {R.drawable.actionb_news, R.drawable.actionb_gps,
                R.drawable.actionb_foot, R.drawable.actionb_friends1, R.drawable.actionb_alarm};
 
        @Override
        protected void onCreate(Bundle savedInstanceState)
        {
                super.onCreate(savedInstanceState);
               
                CONTENT = new String[] {getString(R.string.fragments_1_title),
                                getString(R.string.fragments_2_title), getString(R.string.fragments_3_title),
                                getString(R.string.fragments_4_title), getString(R.string.fragments_5_title)};
               
                setContentView(R.layout.activity_main_iconed_tabs);
 
                /*ActionBar actionbar = getSupportActionBar();
                Drawable d = (Drawable) (getResources().getDrawable(R.drawable.background_actionbar));
                actionbar.setBackgroundDrawable(d);
               
                /*AnimationDrawable frameAnimation = (AnimationDrawable) getResources().getAnimation(R.drawable.scout_icon);
                frameAnimation.start();*/
 
                /*//d = (Drawable) (getResources().getDrawable(R.drawable.scout_icon));
                actionbar.setIcon(frameAnimation.getFrame(0));
               
                //getSupportActionBar().hide();
                int a = android.support.v7.appcompat.R.dimen.abc_action_bar_default_height;
                log.i("AAA", String.valueOf(a));
               
                int b = android.support.v7.appcompat.R.dimen.abc_action_bar_;
                log.i("BBB", String.valueOf(b));*/
 
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