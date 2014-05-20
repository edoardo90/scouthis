package it.poli.android.scoutthisme.fragments;
import it.poli.android.scouthisme.R;
import it.poli.android.scoutthisme.Constants;
import it.poli.android.scoutthisme.social.NotifyFriendsAsyncTask;
import it.poli.android.scoutthisme.tools.FacebookHandler;
import it.poli.android.scoutthisme.tools.FacebookListener;
import it.poli.android.scoutthisme.tools.GpsHandler;
import it.poli.android.scoutthisme.tools.GpsListener;
import it.poli.android.scoutthisme.tools.UserMarker;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
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
import com.facebook.widget.ProfilePictureView;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * A fragment that launches other parts of the demo application.
 */
public  class FindFriendsFragment extends Fragment implements GpsListener, FacebookListener
{
	private GoogleMap gMap;

	GpsHandler gpsHandler;
	FacebookHandler facebookHandler;
	Location loc;
	Marker marker;
	boolean needDefaultZoom;
	final int defaultZoom = 13;
	
	Session session;
	Activity mAct;
	
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
		View rootView;
		mAct = getActivity();
		gpsHandler = new GpsHandler(mAct);
		facebookHandler = new FacebookHandler(mAct);
		statusCallback = new SessionStatusCallback();
		session = Session.getActiveSession();
		
		rootView = inflater.inflate(R.layout.fragment_section_findfriends, container, false);
		this.gMap = ((SupportMapFragment) getFragmentManager().findFragmentById(R.id.mapFindFriends)).getMap();
		
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
		return rootView;
	}

	public void onDestroyView()
	{
		super.onDestroyView();
		int d=0;
		d++;
		
		SupportMapFragment mapFragment = ((SupportMapFragment) getFragmentManager().findFragmentById(R.id.mapFindFriends));
		if(mapFragment != null) {
			FragmentManager fM = getFragmentManager();
			fM.beginTransaction().remove(mapFragment).commit();
		}
	}

	@Override
	public void onResume()
	{
		super.onResume();
		int i=0;
		i++;
		
			
		if (session.isOpened()) {
			setLogoutButton();
			needDefaultZoom = true;
			gpsHandler.setListener(this);
			facebookHandler.setListener(this);
		} else {
			setLoginButton();
			
		}
		session = Session.getActiveSession();
		session.addCallback(statusCallback);
	}


	
	
	@Override
	public void onPause()
	{
		super.onPause();
		int p=0;
		p++;
		
		session = Session.getActiveSession();
		session.removeCallback(statusCallback);
		if (session.isOpened()) {
			gpsHandler.removeListener();
			facebookHandler.removeListener();
			gMap.clear();
		}
	}
	
    /**
     * Imposta l'aspetto del pulsante Login
     */
    private void setLoginButton()
    {
        TextView textInstructionsOrLink = (TextView)mAct.findViewById(R.id.txtFFMessage);
    	textInstructionsOrLink.setText(getString(R.string.findfriends_login_message));

    	Button buttonLoginLogout = (Button) this.getActivity().findViewById(R.id.btnLogInOut);
    	buttonLoginLogout.setText(getString(R.string.findfriends_login));
        buttonLoginLogout.setOnClickListener(
        	new OnClickListener() {
        		public void onClick(View view) { 
        			Log.i("btn ", "button");
        			onClickLogin(); }
        	}
        );
        View mapp = getView().findViewById(R.id.mapFindFriends);
        mapp.setVisibility(View.GONE);
    } 
	
    /**
     * Imposta l'aspetto del pulsante Logout
     */
    private void setLogoutButton()
    {    
        TextView textInstructionsOrLink = (TextView)mAct.findViewById(R.id.txtFFMessage);
        /*JSONObject json = Util.parseJson(facebook.request("me", params));
        String userId = json.getString("id");*/
    	textInstructionsOrLink.setText(getString(R.string.findfriends_logout_message));
    	
		Button buttonLoginLogout = (Button)getView().findViewById(R.id.btnLogInOut);
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
     * Effettua il login su richiesta dell'utente
     */
    private void onClickLogin() {
        Session session = Session.getActiveSession();
        if (!session.isOpened() && !session.isClosed()) {
            session.openForRead(new Session.OpenRequest(this).setCallback(statusCallback));
        } else {
            Session.openActiveSession(mAct, true, statusCallback);
        }
    }
    
    /**
     * Effettua il logout su richiesta dell'utente
     */
    private void onClickLogout() {
        Session session = Session.getActiveSession();
        if (!session.isClosed()) {
            session.closeAndClearTokenInformation();
        }
    }

	public void updateMap() {
		super.onResume();
		
		gMap.clear();

		MarkerOptions mo = new MarkerOptions().position(new LatLng(loc.getLatitude(), loc.getLongitude()));
		Marker marker = gMap.addMarker(mo);
		
		gMap.setMyLocationEnabled(true);
		
		if (needDefaultZoom) {
			needDefaultZoom = false;
			gMap.moveCamera(CameraUpdateFactory.
			newLatLngZoom(marker.getPosition(), defaultZoom));
		}
		
		marker.setTitle(getString(R.string.findfriends_my_marker));
		marker.showInfoWindow();
	}

	@Override
	public void onLocationChanged(Location location) {
		this.loc = location;
		facebookHandler.updatePosition(location);
		updateMap();
	}

	@Override
	public void onFriendsUpdates(String gpsCoordJSONStr) {
		List<UserMarker> lstUsersMarkers = usersMarkersFromJson(gpsCoordJSONStr);
		for(UserMarker usrMarker : lstUsersMarkers)
		{
			if (Constants.DEBUG_ENABLED) {
				Log.w("i place markers", " placing " + usrMarker.toString());
			}
			placeMarkerOnMap(usrMarker);
		}
	}
	
	private void placeMarkerOnMap(UserMarker um)
	{
		MarkerOptions mo = new MarkerOptions().position(new LatLng(um.getLatitude(), um.getLongitude()));
		Marker marker = gMap.addMarker(mo);
		marker.setTitle(um.getName());
		marker.showInfoWindow();

		if (needDefaultZoom) {
			needDefaultZoom = false;
			gMap.moveCamera(CameraUpdateFactory.
					newLatLngZoom(marker.getPosition(), defaultZoom));
		}
		else {
			gMap.moveCamera(CameraUpdateFactory.
					newLatLngZoom(marker.getPosition(), gMap.getCameraPosition().zoom));
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

				String name = juser.getString(Constants.PARAM_NAME);
				double longit = juser.getDouble(Constants.PARAM_POSITION_LONGITUDE);
				double latit = juser.getDouble(Constants.PARAM_POSITION_LATITUDE);
				UserMarker um = new UserMarker(latit, longit, name);
				usersMarkers.add(um);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return usersMarkers;
	}
	
	private void displayUsernameAndProfilePic(final Session session) {
	   
		final ProfilePictureView profilePictureView = (ProfilePictureView) getActivity().findViewById( R.id.ffriends_img_user); 
		final TextView  userNameView = (TextView) getActivity().findViewById(R.id.ff_txtUserFirstName);
		
		Request request = Request.newMeRequest(session, 
	            new Request.GraphUserCallback() {
	        @Override
	        public void onCompleted(GraphUser user, Response response) {
	            // If the response is successful
	            if (session == Session.getActiveSession()) {
	                if (user != null) {
	                   
	                	profilePictureView.setProfileId(user.getId());
	                    // Set the Textview's text to the user's name.
	                    userNameView.setText(user.getName());
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
            
            displayUsernameAndProfilePic(session);
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