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

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		gpsHandler = new GpsHandler(getActivity());
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View rootView = inflater.inflate(R.layout.fragment_section_findfriends, container, false);
		this.gMap = ((SupportMapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
		return rootView;
	}

	public void onDestroyView()
	{
		super.onDestroyView();
		SupportMapFragment mapFragment = ((SupportMapFragment) getFragmentManager().findFragmentById(R.id.map));
		if(mapFragment != null) {
			FragmentManager fM = getFragmentManager();
			fM.beginTransaction().remove(mapFragment).commit();
		}
	}

	@Override
	public void onResume()
	{
		super.onResume();
		needDefaultZoom = true;
		gpsHandler.setListener(this);

		setReceiver();
	}

	@Override
	public void onPause()
	{
		super.onPause();
		gpsHandler.removeListener(this);
		clearMap();

		removeReceiver();
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
							Log.i(" find friends ", "  i should call something..  " );
							updatePosit(); //questo metodo contatta il nostro server e chiede le coord
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
	
	//what i ask the thread to do...  see putExtra
	private void updatePosit() {
		Log.i(" friends frag", " i should update postions instead i download a file, it'll be fine");

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
}