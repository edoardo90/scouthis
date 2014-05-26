package it.poli.android.scoutthisme.fragments;

import it.poli.android.scouthisme.DialogFloatActivity;
import it.poli.android.scouthisme.R;
import it.poli.android.scoutthisme.Constants;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

public class AlertUserGpsFragment extends Fragment
{
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		/* Inflate the layout for this fragment */
		View view = inflater.inflate(R.layout.fragment_section_alert_user_gps, container, false);
		return view;
	}
	
	public void onResume()
	{
		super.onResume();
		String message = "";
		Bundle bundle = getArguments();
		if(bundle != null && bundle.containsKey(Constants.ALLERTABLE_FRAGMENT))
			message = bundle.getString(Constants.ALLERTABLE_FRAGMENT);
		else
			message = getString(R.string.fragments_general_alert);
		
		TextView txtMessage = (TextView)this.getActivity().findViewById(R.id.alert_warn_message);
		txtMessage.setText(Html.fromHtml(message));

		ImageButton imgSetGpsOn = (ImageButton)this.getActivity().findViewById(R.id.setGpsOn);
		imgSetGpsOn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				//displaySettingsGPS();
				startFloatDialog();
			}
		});
	}

	private void startFloatDialog()
	{
		Intent intent=new Intent(this.getActivity().getApplicationContext(), DialogFloatActivity.class);
			startActivity(intent);
		
	}
	
	private void displaySettingsGPS()
	{
		
		final ComponentName toLaunch = new ComponentName("com.android.settings","com.android.settings.SecuritySettings");
        final Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        
        startActivity(intent);
		
		//// va ma poi crasha ///startActivityForResult(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS), 0);
	}



}