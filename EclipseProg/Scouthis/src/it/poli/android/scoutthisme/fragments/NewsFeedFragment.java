package it.poli.android.scoutthisme.fragments;

import it.poli.android.scouthisme.R;
import it.poli.android.scoutthisme.newsfeed.utils.NewsfeedService;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class NewsFeedFragment extends Fragment
{
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) 
	{
		final View rootView = inflater.inflate(R.layout.fragment_section_newsfeed, container, false);
				
		Button btnHomeReload = (Button) rootView.findViewById(R.id.btnHomeReload);
		btnHomeReload.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				new NewsfeedService().execute(rootView);
			}
		});
		
		TextView lblNews = (TextView) rootView.findViewById(R.id.lblNewsMessage);
		lblNews.setText(R.string.news_rss_loading);
		
		new NewsfeedService().execute(rootView);	
		return rootView;
	}
}