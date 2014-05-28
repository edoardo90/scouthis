package it.poli.android.scoutthisme.fragments;

import it.poli.android.scouthisme.R;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

public class GpsAlertFragment extends Fragment
{	
	Fragment mFragment;
	View rootView;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		rootView = inflater.inflate(R.layout.fragment_section_alert_user_gps, container, false);
		return rootView;
	}
	
	public void setFragment(Fragment fragment)
	{
		mFragment = fragment;
	}
	
	@Override
	public void onResume() {
		super.onResume();
		createView();
	}
	
	public void createView()
	{
		TextView  alertWarnMessage = (TextView) rootView.findViewById(R.id.alert_warn_message);
		if (mFragment instanceof GpsFragment) {
			alertWarnMessage.setText(getString(R.string.fragments_general_alert));
		} else if (mFragment instanceof FindFriendsLoggedFragment) {
			String blablabla = getString(R.string.fragments_friends_alert);
			alertWarnMessage.setText(blablabla);
		} else if (mFragment instanceof StepCounterRunFragment) {
			alertWarnMessage.setText(getString(R.string.fragments_step_alert));
		}
		
    	ImageButton buttonLogin = (ImageButton) rootView.findViewById(R.id.setGpsOn);
        buttonLogin.setOnClickListener(
        	new OnClickListener() {
        		public void onClick(View view) {
        			mFragment.startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS)); }
        	}
        );
	}
}
