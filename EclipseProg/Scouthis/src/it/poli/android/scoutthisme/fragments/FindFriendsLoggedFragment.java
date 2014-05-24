package it.poli.android.scoutthisme.fragments;
import it.poli.android.scouthisme.R;
import it.poli.android.scoutthisme.Constants;
import it.poli.android.scoutthisme.gps.utils.GpsHandler;
import it.poli.android.scoutthisme.gps.utils.GpsListener;
import it.poli.android.scoutthisme.social.FacebookHandler;
import it.poli.android.scoutthisme.social.FacebookListener;
import it.poli.android.scoutthisme.social.NotifyFriendsAsyncTask;
import it.poli.android.scoutthisme.tools.RoundedImage;
import it.poli.android.scoutthisme.tools.UserMarker;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.Session.StatusCallback;
import com.facebook.SessionState;
import com.facebook.model.GraphUser;
import com.facebook.widget.ProfilePictureView;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
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

	GpsHandler gpsHandler;
	FacebookHandler facebookHandler;
	Location loc;
	
	Map<Marker, UserMarker> usersMap;
	Map<String, Bitmap>     cachedUserImage;
	static String lastUserIdClicked = null;
	
	GraphUser graphUser;
	boolean needDefaultZoom;
	final int defaultZoom = 13;
	
	Session session;
	Activity mAct;
	Bundle savedInstanceState;
	List<UserMarker> lstUsersMarkers;
    private StatusCallback statusCallback;	
    
	@Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Session.getActiveSession().onActivityResult(mAct, requestCode, resultCode, data);
    }
	
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        session = Session.getActiveSession();
        Session.saveSession(session, outState);
    }

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		mAct = getActivity();
		gpsHandler = new GpsHandler(mAct);
		facebookHandler = new FacebookHandler(mAct);
		statusCallback = new SessionStatusCallback();
		session = Session.getActiveSession();
		this.savedInstanceState = savedInstanceState;
		
		View rootView = inflater.inflate(R.layout.fragment_section_findfriends_logged, container, false);
		return rootView;
	}

	@Override
	public void onResume()
	{
		super.onResume();
			
		this.gMap = ((SupportMapFragment) getFragmentManager().findFragmentById(R.id.mapFindFriends)).getMap();
		gMap.setOnMarkerClickListener(new OnMarkerClickListener() {
			@Override
			public boolean onMarkerClick(Marker marker) {
				//TODO null pointer
				lastUserIdClicked = usersMap.get(marker).getId();
				return false;
			}
		});
		
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
        
		if (session.isOpened()) {
			setLogoutButton();
			needDefaultZoom = true;
			gpsHandler.setListener(this);
			facebookHandler.setListener(this);
		} else {
			graphUser = null;
			switchToDisconnectedFragment();
		}
		session = Session.getActiveSession();
		session.addCallback(statusCallback);
		
		displayUserDetails(session);
	}

	@Override
	public void onPause()
	{
		super.onPause();
		
		session = Session.getActiveSession();
		session.removeCallback(statusCallback);
		gpsHandler.removeListener();
		facebookHandler.removeListener();
		gMap.clear();
		
		SupportMapFragment mapFragment = ((SupportMapFragment) getFragmentManager().findFragmentById(R.id.mapFindFriends));
		if(mapFragment != null) {
			FragmentManager fM = getFragmentManager();
			fM.beginTransaction().remove(mapFragment).commit();
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
        TextView textInstructionsOrLink = (TextView)mAct.findViewById(R.id.txtFFLogoutMessage);
        /*JSONObject json = Util.parseJson(facebook.request("me", params));
        String userId = json.getString("id");*/
    	textInstructionsOrLink.setText(getString(R.string.findfriends_logout_message));
    	
		Button buttonLoginLogout = (Button)getView().findViewById(R.id.btnLogOut);
		buttonLoginLogout.setText(getString(R.string.findfriends_logout));
		buttonLoginLogout.setOnClickListener(
        	new OnClickListener() {
        		public void onClick(View view) { onClickLogout(); }
        	}
        );
		View mapp = getView().findViewById(R.id.mapFindFriends);
		mapp.setVisibility(View.VISIBLE);
    }    
    
    /**
     * Effettua il logout su richiesta dell'utente
     */
    private void onClickLogout() {
        Session session = Session.getActiveSession();
        if (!session.isClosed()) {
            session.closeAndClearTokenInformation();
        }
        switchToDisconnectedFragment();
    }

	@Override
	public void onLocationChanged(Location location) {
		this.loc = location;
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
		if (gMap != null)
		{
			gMap.clear();
			this.usersMap = new HashMap<Marker, UserMarker>();
			this.cachedUserImage = new HashMap<String, Bitmap>();
			if (lstUsersMarkers != null)
			{
				for(UserMarker usrMarker : lstUsersMarkers)
				{
					if (Constants.DEBUG_ENABLED) {
						Log.w("i place markers", " placing " + usrMarker.toString());
					}
					placeMarkerOnMap(usrMarker, false);
				}
			}
			if (loc != null && graphUser != null) {
				UserMarker me = new UserMarker(graphUser, loc.getLatitude(), loc.getLongitude());
				placeMarkerOnMap(me, true);
				
				if (needDefaultZoom) {
					needDefaultZoom = false;
					gMap.moveCamera(CameraUpdateFactory
							.newLatLngZoom(new LatLng(loc.getLatitude(), loc.getLongitude()), defaultZoom));
				}
				/*else {
					gMap.moveCamera(CameraUpdateFactory.
							newLatLngZoom(new LatLng(loc.getLatitude(), loc.getLongitude()), gMap.getCameraPosition().zoom));
				}*/
				//marker.showInfoWindow();
			}
		}
	}
	
	public void addMeToMap() {
		MarkerOptions mo = new MarkerOptions().position(new LatLng(loc.getLatitude(), loc.getLongitude()));
		Marker marker = gMap.addMarker(mo);
		
		marker.setTitle(graphUser.getFirstName());

	}
	
	private void placeMarkerOnMap(final UserMarker userMarker, boolean IAm)
	{
		if (!IAm) {
			new ImageLoader() { 
				@Override
				protected void onPostExecute(Bitmap bitmap)
				{
					MarkerOptions mo = new MarkerOptions().position(new LatLng(userMarker.getLatitude(), userMarker.getLongitude()));
					final Marker marker = gMap.addMarker(mo);
					marker.setTitle(userMarker.toString());
					
					usersMap.put(marker, userMarker);
	
					marker.setIcon(BitmapDescriptorFactory.fromBitmap(bitmap));
					marker.setAnchor(0.5f, 1);
				}
		    }.execute(userMarker.getImageUrl()); // start the background processing
		}
		else
		{
			MarkerOptions mo = new MarkerOptions().position(new LatLng(userMarker.getLatitude(), userMarker.getLongitude()));
			final Marker marker = gMap.addMarker(mo);
			marker.setTitle(userMarker.toString());
			
			usersMap.put(marker, userMarker);
			
			if (lastUserIdClicked != null  && lastUserIdClicked.contentEquals(userMarker.getId()))
				marker.showInfoWindow();
			
		}
		
	}
	
		
	class ImageLoader extends AsyncTask<String, Integer, Bitmap>{

	
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
			
			//facciamo CACHING! - get   
			Bitmap bitmap = cachedUserImage.get(urls[0]);
			
			//if not in cash DOWNLOAD it and we SAVE IT IN CACH
			if(bitmap== null)
			{	
				try {
					in = (InputStream) imgUrl.getContent();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				bitmap = BitmapFactory.decodeStream(in);
				try {
					if(in!=null)
						in.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				RoundedImage rv = new RoundedImage(mAct.getApplicationContext());
				bitmap = rv.getCroppedBitmap(bitmap, 80);
				//SAVE IT IN CACHE
				cachedUserImage.put(urls[0], bitmap);
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
			e.printStackTrace();
		}
		return usersMarkers;
	}
	
	private void displayUserDetails(final Session session)
	{   
		final ProfilePictureView profilePictureView = (ProfilePictureView) mAct.findViewById(R.id.ffriends_img_user); 
		final TextView  userNameView = (TextView) mAct.findViewById(R.id.ff_txtUserFirstName);
		
		Request request = Request.newMeRequest(session, 
	            new Request.GraphUserCallback() {
	        @Override
	        public void onCompleted(GraphUser user, Response response) {
	            // If the response is successful
	            if (session == Session.getActiveSession()) {
	                if (user != null && profilePictureView != null && userNameView != null) {
	                	
	                	profilePictureView.setProfileId(user.getId());
	                    // Set the Textview's text to the user's name.
	                    userNameView.setText(user.getName());
	    	        	graphUser = user;
	                }
	            }
	            if (response.getError() != null) {
	                // Handle errors, will do so later.
	            }
	        }
	    });
	    request.executeAsync();
	}
	
    /**
     * Classe a servizio di Facebook
     */    
    private class SessionStatusCallback implements StatusCallback {
        @Override
        public void call(Session sessionF, SessionState state, Exception exception) {
            session = Session.getActiveSession();
            
            displayUserDetails(session);
            if (session.isOpened()) {        	
            	// If everything goes fine, now we got the url with all our friends stuff
            	String urlFriendsInfo = Constants.URL_PREFIX_FRIENDS + session.getAccessToken();
            	String userInfo = Constants.URL_PREFIX_ME +	session.getAccessToken();
            	// Call AsynTask to perform network operation on separate thread
            	// see: doInBackground and onPostExecute
        		new NotifyFriendsAsyncTask(getActivity()).execute(urlFriendsInfo, userInfo);
            }
        }
    }
}