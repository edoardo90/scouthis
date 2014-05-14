package it.poli.android.scoutthisme.fragments;

import it.poli.android.scouthisme.R;
import it.poli.android.scoutthisme.social.HomeRSSService;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class HomeSectionFragment extends Fragment
{
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) 
	{
		final View rootView = inflater.inflate(R.layout.fragment_section_launchpad, container, false);
				
		Button btnHomeReload = (Button) rootView.findViewById(R.id.btnHomeReload);
		btnHomeReload.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				new HomeRSSService().execute(rootView);
			}
		});
		
		TextView lblNews = (TextView) rootView.findViewById(R.id.lblNewsMessage);
		lblNews.setText("Caricamento in corso...");
		
		new HomeRSSService().execute(rootView);	
		return rootView;
	}
}




