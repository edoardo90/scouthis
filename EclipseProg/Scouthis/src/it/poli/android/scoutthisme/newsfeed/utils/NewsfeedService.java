package it.poli.android.scoutthisme.newsfeed.utils;

import it.poli.android.scouthisme.R;
import it.poli.android.scoutthisme.fragments.NewsFeedFragment;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

public class NewsfeedService  extends AsyncTask<NewsFeedFragment, Void, String>
{
	ArrayList<NewsItem> RSSItems;

	View rootView;
	boolean connectionError;
	NewsFeedFragment mFragment;

	protected String doInBackground(NewsFeedFragment... params)
	{
		mFragment = params[0];
		rootView = mFragment.getView();
		try {
			URL url;
			url = new URL("http://xml.corriereobjects.it/rss/ambiente.xml");

			XmlPullParserFactory factory;
			factory = XmlPullParserFactory.newInstance();
			factory.setNamespaceAware(false);

			XmlPullParser xpp;
			xpp = factory.newPullParser();
			// We will get the XML from an input stream
			xpp.setInput(getInputStream(url), "UTF_8");

			/* We will parse the XML content looking for the "<title>" tag which appears inside the "<item>" tag.
			 * However, we should take in consideration that the rss feed name also is enclosed in a "<title>" tag.
			 * As we know, every feed begins with these lines: "<channel><title>Feed_Name</title>...."
			 * so we should skip the "<title>" tag which is a child of "<channel>" tag,
			 * and take in consideration only "<title>" tag which is a child of "<item>"
			 *
			 * In order to achieve this, we will make use of a boolean variable.
			 */
			boolean insideItem = false;

			// Returns the type of current event: START_TAG, END_TAG, etc..
			int eventType = xpp.getEventType();
			RSSItems = new ArrayList<NewsItem>();
			NewsItem rssI = new NewsItem();
			String imageUrlString;
			URL imageUrl;
			while (eventType != XmlPullParser.END_DOCUMENT) {
				if (eventType == XmlPullParser.START_TAG) {
					if (xpp.getName().equalsIgnoreCase("item")) {
						insideItem = true;
					} else if (xpp.getName().equalsIgnoreCase("title")) {
						if (insideItem)
							rssI.setTitle(xpp.nextText().trim().replaceAll("\\s+", " ")); //extract the headline
					} else if (xpp.getName().equalsIgnoreCase("link")) {
						if (insideItem)
							rssI.setUrl(xpp.nextText());
					} else if (xpp.getName().equalsIgnoreCase("description")) {
						if (insideItem)
							rssI.setDescription(xpp.nextText().trim().replaceAll("\\s+", " "));
					} else if (xpp.getName().equalsIgnoreCase("thumbimage")) {
						if (insideItem) {
							imageUrlString = xpp.getAttributeValue(null, "url");
							imageUrl = new URL(imageUrlString);
							//Drawable d = Drawable.createFromPath(imageUrlString);
							Bitmap bmp = BitmapFactory.decodeStream(imageUrl.openConnection().getInputStream());
							rssI.setImage(bmp);
							//xpp.nextText();
						}
					}
				} else if (eventType == XmlPullParser.END_TAG && xpp.getName().equalsIgnoreCase("item")) {
					RSSItems.add(rssI);
					rssI = new NewsItem();
					insideItem=false;
				}
				eventType = xpp.next(); //move to next element
			}			
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (XmlPullParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			connectionError = true;
		}
		return null;
	}

	// onPostExecute displays the results of the AsyncTask.
	@Override
	protected void onPostExecute(String result)
	{
		try
		{
			if (connectionError)
				throw new IOException();

			if (rootView != null) {
				final Context mCtx = rootView.getContext();

				ListView lstNews = (ListView)rootView.findViewById(R.id.lstNews);
				lstNews.setOnItemClickListener(new AdapterView.OnItemClickListener() {
					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {
						Uri uri = Uri.parse((String) RSSItems.get(position).getUrl());
						Intent intent = new Intent(Intent.ACTION_VIEW, uri);
						mCtx.startActivity(intent);
					}
				});

				// Binding data
				NewsfeedAdapter adapter = new NewsfeedAdapter(rootView.getContext(), RSSItems);
				lstNews.setAdapter(adapter);

				mFragment.setNewsfeedView(true, false);
			}
		}
		catch (IOException e)
		{
			mFragment.setNewsfeedView(false, false);
		}
	}

	public InputStream getInputStream(URL url) throws IOException {
		return url.openConnection().getInputStream();
	}
}
