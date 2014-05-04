package it.poli.android.scoutthisme.fragments;

import it.poli.android.scouthisme.R;
import it.poli.android.scoutthisme.social.HomeRSSService;
import it.poli.android.scoutthisme.social.NotifyFriendsAsyncTask;
import it.poli.android.scoutthisme.tools.RSSHomeAdapter;
import it.poli.android.scoutthisme.tools.RSSItem;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

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




