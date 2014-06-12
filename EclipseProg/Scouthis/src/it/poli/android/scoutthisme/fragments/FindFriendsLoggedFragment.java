package it.poli.android.scoutthisme.fragments;
import it.poli.android.scouthisme.R;
import it.poli.android.scoutthisme.Constants;
import it.poli.android.scoutthisme.gps.utils.GpsHandler;
import it.poli.android.scoutthisme.gps.utils.GpsListener;
import it.poli.android.scoutthisme.social.FriendshipsUpdatesNotifier;
import it.poli.android.scoutthisme.social.FriendshipsUpdatesTrigger;
import it.poli.android.scoutthisme.social.utils.FacebookHandler;
import it.poli.android.scoutthisme.social.utils.FacebookListener;
import it.poli.android.scoutthisme.tools.RoundedImage;
import it.poli.android.scoutthisme.tools.UserMarker;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.Session.StatusCallback;
import com.facebook.SessionState;
import com.facebook.model.GraphUser;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * A fragment that launches other parts of the demo application.
 */
public  class FindFriendsLoggedFragment extends Fragment implements GpsListener, FacebookListener
{
	private GoogleMap gMap;
	AlarmManager fbUpdateFriendsAlarm;

	GpsHandler gpsHandler;
	FacebookHandler facebookHandler;
	Map<String, Marker> markersMap;

	Location loc;

	GraphUser graphUser;
	final int defaultZoom = 13;

	Activity mAct;
	List<UserMarker> lstUsersMarkers;
	private StatusCallback statusCallback;
	AsyncTask<String, String, String> notifyFriendsAsyncTask;

	private Intent friendshipsUpdatesIntent;
	private PendingIntent friendshipsUpdatesPendingIntent;
	private boolean isNetworkOk;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		mAct = getActivity();
		fbUpdateFriendsAlarm = (AlarmManager) mAct.getSystemService(Context.ALARM_SERVICE);
		statusCallback = new SessionStatusCallback();
		gpsHandler = new GpsHandler(this);
		facebookHandler = new FacebookHandler(mAct);
		markersMap = new HashMap<String, Marker>();

		Session session = Session.getActiveSession();
		if (session == null) {
			if (savedInstanceState != null) {
				session = Session.restoreSession(mAct, null, statusCallback, savedInstanceState);
			}
			if (session == null) {
				session = new Session(mAct);
			}
			Session.setActiveSession(session);
			if (session.getState().equals(SessionState.CREATED_TOKEN_LOADED)) {
				session.openForRead(new Session.OpenRequest(this).setCallback(statusCallback));
			}
		}

		gpsHandler.setListener(this);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View rootView = inflater.inflate(R.layout.fragment_section_findfriends_logged, container, false);
		return rootView;
	}

	@Override
	public void onDestroyView()
	{
		super.onDestroyView();

		// Destroy map
		if (gMap != null) {
			gMap.clear();
		}

		Fragment fragment = (getFragmentManager().findFragmentById(R.id.mapFindFriends));  
		FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
		ft.remove(fragment);
		ft.commit();
	}

	@Override
	public void onDestroy()
	{
		super.onDestroy();

		fbUpdateFriendsAlarm.cancel(friendshipsUpdatesPendingIntent);
		gpsHandler.removeListener();

		notifyFriendsAsyncTask.cancel(false);
	}

	@Override
	public void onResume() {
		super.onResume();

		scheduleFriendshipsUpdatesAlarm();
		setLogoutButton();
		updateView(true);

		gpsHandler.setViewActive(true);

		Session.getActiveSession().addCallback(statusCallback);
		facebookHandler.setListener(this);
	}

	@Override
	public void onPause() {
		super.onPause();

		gpsHandler.setViewActive(false);

		facebookHandler.removeListener();
		Session.getActiveSession().removeCallback(statusCallback);
	}

	private void updateView(boolean justCreated)
	{
		Session session = Session.getActiveSession();
		if (session != null && session.isOpened()) {
			displayUserDetails(session);
			if (justCreated) {				
				FragmentManager fm = getFragmentManager();
				if  (fm != null)
				{	
					SupportMapFragment mapFragment = (SupportMapFragment) fm.findFragmentById(R.id.mapFindFriends);
					if (mapFragment != null) {
						this.gMap = mapFragment.getMap();
						if (loc == null) 
							gMap.moveCamera(CameraUpdateFactory
									.newLatLngZoom(new LatLng(41.29246, 12.5736108), 5));
					}
				}
				// If everything goes fine, now we got the url with all our friends stuff
				String urlFriendsInfo = Constants.URL_PREFIX_FRIENDS + session.getAccessToken();
				String userInfo = Constants.URL_PREFIX_ME +	session.getAccessToken();
				// Call AsynTask to perform network operation on separate thread
				// see: doInBackground and onPostExecute
				notifyFriendsAsyncTask = new FriendshipsUpdatesNotifier().execute(urlFriendsInfo, userInfo);
			}
		} else if (session != null && session.isClosed()) {
			graphUser = null;
			switchToDisconnectedFragment();
		}

		updateGpsStatusView();
	}

	public void updateGpsStatusView()
	{
		//UPDATE GPS STATUS
		TextView txtStatus = (TextView) mAct.findViewById(R.id.ff_txtStatus);
		if (txtStatus != null) {
			if (gpsHandler.isGpsEnabled()) {
				if (loc != null) {
					if (isNetworkOk) {
						if (lstUsersMarkers != null) {
							txtStatus.setText(Html.fromHtml(mAct.getString(R.string.findfriends_status_ready)));
						} else {
							txtStatus.setText(Html.fromHtml(mAct.getString(R.string.findfriends_status_waiting_friends)));
						}
					} else {
						txtStatus.setText(Html.fromHtml(mAct.getString(R.string.findfriends_status_network_unavailable)));
					}
				} else {
					txtStatus.setText(Html.fromHtml(mAct.getString(R.string.findfriends_status_waiting_gps)));
				}
			} else {
				txtStatus.setText(Html.fromHtml(mAct.getString(R.string.findfriends_status_gps_deactivated)));
			}
		}
	}

	private void switchToDisconnectedFragment()
	{
		FragmentTransaction transaction = getFragmentManager().beginTransaction();
		// Replace whatever is in the fragment_container view with this fragment,
		// and add the transaction to the back stack
		transaction.replace(R.id.findfriends_frame, new FindFriendsDisconnectedFragment());
		transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
		transaction.addToBackStack(null);
		// Commit the transaction
		transaction.commit();
	}

	/**
	 * Imposta l'aspetto del pulsante Logout
	 */
	private void setLogoutButton()
	{    	
		Button buttonLogout = (Button)getView().findViewById(R.id.btnLogOut);

		buttonLogout.setOnClickListener(
				new OnClickListener() {
					public void onClick(View view) { 
						Session session = Session.getActiveSession();
						if (!session.isClosed()) {
							session.closeAndClearTokenInformation();
						}
						switchToDisconnectedFragment();
					}
				}
				);
	}

	@Override
	public void onLocationChanged(Location location) {
		loc = location;
		facebookHandler.updatePosition(location);
		updateMap();
	}

	@Override
	public void onFriendsUpdates(String gpsCoordJSONStr) {
		lstUsersMarkers = usersMarkersFromJson(gpsCoordJSONStr);
		updateMap();
	}

	private void updateMap() 
	{
		Map<String, Marker> tempMap = new HashMap<String, Marker>(markersMap);

		if (gMap != null)
		{
			if (loc != null && lstUsersMarkers != null)
			{
				for(UserMarker userMarker : lstUsersMarkers) {
					// Get user's id and check if it's alrealdy on map
					String thisUserId = userMarker.getId();
					Marker mapMarker = markersMap.get(thisUserId);
					// if yes, update position
					if (mapMarker != null) {
						tempMap.remove(thisUserId);
						mapMarker.setPosition(new LatLng(userMarker.getLatitude(), userMarker.getLongitude()));
					} else {
						placeMarkerOnMap(userMarker, false);
					}
				}
			}

			if (loc != null && graphUser != null) {
				// Get details of me and my position
				UserMarker me = new UserMarker(graphUser, loc.getLatitude(), loc.getLongitude());
				String thisUserId = me.getId();
				Marker mapMarker = markersMap.get(thisUserId.toString());
				// Check if I am already on map. If yes: update position, if not: add me on map
				if (mapMarker != null) {
					tempMap.remove(thisUserId);
					mapMarker.setPosition(new LatLng(me.getLatitude(), me.getLongitude()));
				} else {
					placeMarkerOnMap(me, true);
					gMap.moveCamera(CameraUpdateFactory
							.newLatLngZoom(new LatLng(loc.getLatitude(), loc.getLongitude()), defaultZoom));
				}
			}

			updateGpsStatusView();
		}

		// Remove all friends not online anymore
		Collection<String> usersMarkersToRemove = tempMap.keySet();
		for (String s : usersMarkersToRemove) {
			((Marker) markersMap.get(s)).remove();
			markersMap.remove(s);
		}
	}

	private void placeMarkerOnMap(final UserMarker userMarker, boolean IAm)
	{
		if (!IAm) {
			MarkerOptions mo = new MarkerOptions().position(new LatLng(userMarker.getLatitude(), userMarker.getLongitude()));
			final Marker marker = gMap.addMarker(mo);
			marker.setTitle(userMarker.toString());

			markersMap.put(userMarker.getId(), marker);

			new ImageLoader() { 
				@Override
				protected void onPostExecute(Bitmap bitmap)
				{
					RoundedImage rv = new RoundedImage(mAct.getApplicationContext());
					if (bitmap != null) {
						bitmap = rv.getCroppedBitmap(bitmap, 80);

						try {
							marker.setIcon(BitmapDescriptorFactory.fromBitmap(bitmap));
						} catch(Throwable ex) {
							Log.i(getClass().getSimpleName(), "Marker.setIcon failed: " + ex.getMessage());
						}}

					marker.setAnchor(0.5f, 1);
				}
			}.execute(userMarker.getImageUrl()); // start the background processing
		}
		else
		{
			MarkerOptions mo = new MarkerOptions().position(new LatLng(userMarker.getLatitude(), userMarker.getLongitude()));
			final Marker marker = gMap.addMarker(mo);
			marker.setTitle(userMarker.toString());

			markersMap.put(userMarker.getId(), marker);
		}
	}

	private class ImageLoader extends AsyncTask<String, Integer, Bitmap>
	{	
		@Override
		protected Bitmap doInBackground(String... urls) {
			URL imgUrl = null;
			InputStream in = null;
			try {
				imgUrl = new URL(urls[0]);
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				in = (InputStream) imgUrl.getContent();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Bitmap  bitmap = BitmapFactory.decodeStream(in);
			try {
				if(in!=null)
					in.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return bitmap;
		}
	}

	private List<UserMarker> usersMarkersFromJson(String gpsCoordJSONStr)
	{
		List<UserMarker> usersMarkers = new ArrayList<UserMarker>();
		try {
			JSONObject job = new JSONObject(gpsCoordJSONStr);
			JSONArray markk = job.getJSONArray("markers");
			for(int i=0; i<markk.length(); i++)
			{
				JSONObject juser = markk.getJSONObject(i);

				String userId = juser.getString(Constants.PARAM_USERID);
				String name = juser.getString(Constants.PARAM_NAME);
				double longit = juser.getDouble(Constants.PARAM_POSITION_LONGITUDE);
				double latit = juser.getDouble(Constants.PARAM_POSITION_LATITUDE);

				UserMarker um = new UserMarker(userId, name, latit, longit);
				usersMarkers.add(um);
			}
		} catch (JSONException e) {
			if (Constants.DEBUG_ENABLED)
				e.printStackTrace();
		}
		return usersMarkers;
	}

	private void setProfileImage(String userID)
	{
		String facebookUserImageUrl = "https://graph.facebook.com/" + userID + "/picture?type=large";

		new ImageLoader(){ 
			@Override
			protected void onPostExecute(Bitmap bitmap)
			{
				final ImageView profilePic = (ImageView)mAct.findViewById(R.id.ff_profilePic);
				if(profilePic != null && bitmap != null)
				{
					profilePic.setImageBitmap(bitmap);
				}
			}
		}.execute(facebookUserImageUrl); 
	}

	private void displayUserDetails(final Session session)
	{
		final TextView  userNameView = (TextView) mAct.findViewById(R.id.ff_txtUserFFLoggedMessage);

		Request request = Request.newMeRequest(session, 
				new Request.GraphUserCallback() {
			@Override
			public void onCompleted(GraphUser user, Response response) {
				// If the response is successful
				if (session == Session.getActiveSession()) {
					if (user != null) {
						// Set the Textview's text to the user's name. TODO
						if (userNameView != null)
						{
							userNameView.setText(Html.fromHtml(getString(R.string.findfriends_wellcome_name) + " <b>" + user.getFirstName() + "</b>"));
							setProfileImage(user.getId());
						}
						graphUser = user;
					}
				}
				if (response.getError() != null) {
					// Handle errors, will do so later.
					if (Constants.DEBUG_ENABLED)
						Log.i("FindFriends", response.getError().toString());
				}
			}
		});
		request.executeAsync();
	}

	public void scheduleFriendshipsUpdatesAlarm() {
		friendshipsUpdatesIntent = new Intent(mAct, FriendshipsUpdatesTrigger.class);
		friendshipsUpdatesPendingIntent = PendingIntent.getBroadcast(mAct,
				Constants.INTENT_FRIENDSHIPUPDATESTRIGGER_REQUESTCODE, friendshipsUpdatesIntent,
				PendingIntent.FLAG_UPDATE_CURRENT);

		// Setup periodic alarm every TOT - SEE CONSTANTS  seconds
		long firstMillis = System.currentTimeMillis(); // first run of alarm is immediate

		fbUpdateFriendsAlarm.setInexactRepeating(AlarmManager.RTC_WAKEUP, firstMillis,
				Constants.TIME_UPDATE_FRIENDSLIST, friendshipsUpdatesPendingIntent);
	}

	/**
	 * Classe a servizio di Facebook
	 */    
	private class SessionStatusCallback implements StatusCallback {
		@Override
		public void call(Session sessionF, SessionState state, Exception exception) {
			updateView(false);
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		Session.getActiveSession().onActivityResult(mAct, requestCode, resultCode, data);
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		Session session = Session.getActiveSession();
		Session.saveSession(session, outState);
	}

	@Override
	public void onFacebookResponse(int response) {
		// Check network status
		isNetworkOk = (response == Constants.RESULT_OK) ? true : false;
		// Check GPS coordinates' age
		if (loc != null) {
			if (gpsHandler.ageMilliseconds(loc) >= Constants.GPS_LAST_UPDATE_MILLISECONDS) {
				loc = null;
				facebookHandler.updatePosition(loc);
			}
		}
		// Update user details if never done before (caused by network unavailability)
		if (graphUser == null)
			updateView(false);
		updateGpsStatusView();
	}
}