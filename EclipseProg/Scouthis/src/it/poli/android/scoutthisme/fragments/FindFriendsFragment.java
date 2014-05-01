package it.poli.android.scoutthisme.fragments;
import it.poli.android.scouthisme.R;
import it.poli.android.scoutthisme.constants.Constants;
import it.poli.android.scoutthisme.social.GetFriendsPositionsService;
import it.poli.android.scoutthisme.tools.GpsHandler;
import it.poli.android.scoutthisme.tools.GpsListener;
import it.poli.android.scoutthisme.tools.UserMarker;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.opengl.Visibility;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.android.Util;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * A fragment that launches other parts of the demo application.
 */
public  class FindFriendsFragment extends Fragment implements GpsListener
{
	public static final String strLatitudeExtra = "it.poli.latitude";
	public static final String strLongitudeExtra = "it.poli.longitude";
	private GoogleMap gMap;
	private Timer timer = null;

	GpsHandler gpsHandler;
	Location loc;
	Marker marker;
	boolean needDefaultZoom;
	final int defaultZoom = 13;
	
	ViewGroup container;
	Session session;
	Activity mAct;
	
	boolean needRepaint;
	
    private Session.StatusCallback statusCallback;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		gpsHandler = new GpsHandler(getActivity());
		statusCallback = new SessionStatusCallback();
		mAct = getActivity();
	}
	
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
		this.container = container;
		session = Session.getActiveSession();
        if (savedInstanceState != null) {
            session = Session.restoreSession(getActivity(), null, statusCallback, savedInstanceState);
        }
        if (session == null) {
            session = new Session(getActivity());
        }
        Session.setActiveSession(session);
        if (session.getState().equals(SessionState.CREATED_TOKEN_LOADED)) {
            session.openForRead(new Session.OpenRequest(this).setCallback(statusCallback));
        }
		rootView = inflater.inflate(R.layout.fragment_section_findfriends, container, false);
		this.gMap = ((SupportMapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
		return rootView;
	}

	public void onDestroyView()
	{
		super.onDestroyView();
		
		if (session.isOpened()) {
			SupportMapFragment mapFragment = ((SupportMapFragment) getFragmentManager().findFragmentById(R.id.map));
			if(mapFragment != null) {
				FragmentManager fM = getFragmentManager();
				fM.beginTransaction().remove(mapFragment).commit();
			}
		}
	}

	@Override
	public void onResume()
	{
		super.onResume();
		if (session.isOpened()) {
			needDefaultZoom = true;
			gpsHandler.setListener(this);
	
			setReceiver();
		}
	}
	
	@Override
	public void onStart()
	{
		super.onStart();
		
		session = Session.getActiveSession();
		session.addCallback(statusCallback);

		if (session.isOpened()) {
			setLogoutButton();
		} else {
			setLoginButton();
		}
	}

	@Override
	public void onStop()
	{
		super.onStop();
		
		session = Session.getActiveSession();
		session.removeCallback(statusCallback);
	}
	
	@Override
	public void onPause()
	{
		super.onPause();
		if (session.isOpened()) {
			gpsHandler.removeListener(this);
			clearMap();
	
			removeReceiver();
		}
	}

	private void clearMap() {
		gMap.clear();
	}

	public void updateMap() {
		super.onResume();
		
		clearMap();

		MarkerOptions mo = new MarkerOptions().position(new LatLng(loc.getLatitude(), loc.getLongitude()));
		Marker marker = gMap.addMarker(mo);
		
		gMap.setMyLocationEnabled(true);
		
		if (needDefaultZoom) {
			needDefaultZoom = false;
			gMap.moveCamera(CameraUpdateFactory.
			newLatLngZoom(marker.getPosition(), defaultZoom));
		}
		
		marker.setTitle("Tu sei qui");
		marker.showInfoWindow();
	}

	@Override
	public void onLocationChanged(Location location) {
		this.loc = location;
		updateMap();
	}
	
	@Override
	public void onError(String string) {
		
	}
	
	private List<UserMarker> lumFromJsonStr(String gpsCoordJSONStr)
	{
		List<UserMarker> lum = new ArrayList<UserMarker>();
		try {
			JSONObject job = new JSONObject(gpsCoordJSONStr);
			JSONArray markk = job.getJSONArray("markers");
			for(int i=0; i<markk.length(); i++)
			{
				JSONObject juser = markk.getJSONObject(i);

				String name = juser.getString("name");
				double longit = juser.getDouble("longitude");
				double latit = juser.getDouble("latitude");
				UserMarker um = new UserMarker(latit, longit, name);
				lum.add(um);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return lum;
	}
	
	private void setReceiver()
	{
		getActivity().registerReceiver(receiver, new IntentFilter(GetFriendsPositionsService.NOTIFICATION));

		//timer per lanciare ogni TOT (VEDI COSTANTI) tempo il servizio che trova le coordinate
		//degli amici online
		timer = new Timer();
		final Handler handler = new Handler();

		TimerTask doAsynchronousTask = new TimerTask() {       
			@Override
			public void run() {
				handler.post(new Runnable() {
					public void run() { 
						try {
							Log.i("Find friends", "Calling updatePosit()..." );
							updatePosit();
						}
						catch (Exception e) {
							e.printStackTrace();
						}
					}
				});
			}
		};
		timer.schedule(doAsynchronousTask, 0, Constants.UPDATE_FRIENDS_POSITION); 
	}
	
	private void removeReceiver()
	{
		timer.cancel();
		timer.purge();
		Log.i("pause", "timer purged");
		this.getActivity().unregisterReceiver(receiver);
	}

	private void placeOnMap(UserMarker um)
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
	
	private void updatePosit() {
		Log.i(" friends frag", "Updating friends positions...");

		Intent intent = null;
		if(loc.getLatitude() != 0 && loc.getLongitude() != 0)
		{
			intent =  new Intent(this.getActivity().getApplicationContext(), GetFriendsPositionsService.class);
			intent.putExtra(FindFriendsFragment.strLatitudeExtra, loc.getLatitude());
			intent.putExtra(FindFriendsFragment.strLatitudeExtra, loc.getLongitude());
			Log.i(" updatePosit()", " now i start service ");
			this.getActivity().startService(intent);
		}
	}
	
	protected void updateMarkers(String gpsCoordJSONStr)
	{
		List<UserMarker> lum = this.lumFromJsonStr(gpsCoordJSONStr);
		for(UserMarker um : lum)
		{
			Log.w("i place markers", " placing " + um.toString());
			this.placeOnMap(um);
		}
	}
	
	private BroadcastReceiver receiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			//what does the tread says me when finished .. say putExtra in GetFriendsPositionsService e getString..
			Log.i("Find friends fragment onReceive", " i finished ");
			Bundle bundle = intent.getExtras();
			if (bundle != null) {
				String gpsCoordJSONStr = bundle.getString(GetFriendsPositionsService.GPSCOORD);
				int resultCode = bundle.getInt(GetFriendsPositionsService.RESULT);
				if (resultCode == Constants.RESULT_OK) {
					updateMarkers(gpsCoordJSONStr);
				} 
			}
		}
	};
	
    /**
     * Classe a servizio di Facebook
     */    
    private class SessionStatusCallback implements Session.StatusCallback {
        @Override
        public void call(Session sessionF, SessionState state, Exception exception) {
        	//Session session = Session.getActiveSession();
    		/*if (needRepaint) {
    			repaintLayout();
    		}*/
    		/*if (!session.isOpened()) {
    			setLoginButton();
    			//Session.getActiveSession().addCallback(statusCallback);
    		} else {
    			setLogoutButton();
    		}*/
        }
    }

    /**
     * Imposta l'aspetto del pulsante Logout
     */
    private void setLogoutButton()
    {    
        TextView textInstructionsOrLink = (TextView)getActivity().findViewById(R.id.txtFFMessage);
        /*JSONObject json = Util.parseJson(facebook.request("me", params));
        String userId = json.getString("id");*/
    	textInstructionsOrLink.setText("Benvenuto! " + "https://graph.facebook.com/me/friends?access_token=" + session.getAccessToken());
    	
		Button buttonLoginLogout = (Button)getView().findViewById(R.id.btnLogInOut);
		buttonLoginLogout.setText("Logout");
		buttonLoginLogout.setOnClickListener(
            	new OnClickListener() {
            		public void onClick(View view) { onClickLogout(); }
            	}
        );
		View mapp = getView().findViewById(R.id.map);
		mapp.setVisibility(View.VISIBLE);
    } 
    
    /**
     * Imposta l'aspetto del pulsante Login
     */
    private void setLoginButton()
    {
        TextView textInstructionsOrLink = (TextView)getActivity().findViewById(R.id.txtFFMessage);
    	textInstructionsOrLink.setText("Fai il login! " + session.getState().toString());

    	Button buttonLoginLogout = (Button)getView().findViewById(R.id.btnLogInOut);
        buttonLoginLogout.setText("Login");
        buttonLoginLogout.setOnClickListener(
        	new OnClickListener() {
        		public void onClick(View view) { onClickLogin(); }
        	}
        );
        View mapp = getView().findViewById(R.id.map);
        mapp.setVisibility(View.GONE);
    }    

    /**
     * Effettua il login su richiesta dell'utente
     */
    private void onClickLogin() {
        Session session = Session.getActiveSession();
        if (!session.isOpened() && !session.isClosed()) {
            session.openForRead(new Session.OpenRequest(this).setCallback(statusCallback));
        } else {
            Session.openActiveSession(getActivity(), true, statusCallback);
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
}