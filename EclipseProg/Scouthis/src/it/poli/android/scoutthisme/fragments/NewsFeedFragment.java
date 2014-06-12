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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class NewsFeedFragment extends Fragment
{
	View rootView;
	Button btnHomeReload;
	Runnable r;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) 
	{
		rootView = inflater.inflate(R.layout.fragment_section_newsfeed, container, false);

		final NewsFeedFragment thisFragment = this;
		btnHomeReload = (Button) rootView.findViewById(R.id.btnHomeReload);
		btnHomeReload.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				setNewsfeedView(false, true);
				new NewsfeedService().execute(thisFragment);
			}
		});

		setNewsfeedView(false, true);		
		new NewsfeedService().execute(this);
		return rootView;
	}

	public void setNewsfeedView(boolean ok, boolean loading)
	{
		int visibilityError = ok ? View.GONE : loading ? View.GONE : View.VISIBLE;
		int visibilityLoading = ok ? View.GONE :  loading ? View.VISIBLE : View.GONE;
		int visibilityListView = ok ? View.VISIBLE : View.GONE;

		TextView lblNewsError = (TextView) rootView.findViewById(R.id.txtNewsError);
		lblNewsError.setVisibility(visibilityError);
		TextView lblNewsErrorDescription = (TextView) rootView.findViewById(R.id.txtNewsErrorDescription);
		lblNewsErrorDescription.setVisibility(visibilityError);
		Button btnNewsReload = (Button) rootView.findViewById(R.id.btnHomeReload);
		btnNewsReload.setVisibility(visibilityError);		

		ProgressBar prgBar = (ProgressBar) rootView.findViewById(R.id.newsfeedProgressBar);
		prgBar.setVisibility(visibilityLoading);
		TextView txtLoading = (TextView) rootView.findViewById(R.id.txtNewsProgressBar);
		txtLoading.setVisibility(visibilityLoading);

		ListView lstNews = (ListView) rootView.findViewById(R.id.lstNews);
		lstNews.setVisibility(visibilityListView);

		TextView lblNewsMessage = (TextView) rootView.findViewById(R.id.lblNewsMessage);
		lblNewsMessage.setText(R.string.news_rss_information);

		ImageView imgNews = (ImageView) rootView.findViewById(R.id.imgNewsMessage);
		if (ok)
			imgNews.setImageResource(R.drawable.rss);
		else
			imgNews.setImageResource(R.drawable.rsserror);

		if (!ok && !loading) {
			try {
				Thread.sleep(800);
			} catch (InterruptedException e) { }
		}
	}
}