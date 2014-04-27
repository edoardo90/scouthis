package it.poli.android.scoutthisme.fragments;
import it.poli.android.scouthisme.R;
import it.poli.android.scoutthisme.constants.Constants;
import it.poli.android.scoutthisme.social.GetFriendsPositionsService;
import it.poli.android.scoutthisme.tools.GpsHandler;
import it.poli.android.scoutthisme.tools.GpsListener;
import it.poli.android.scoutthisme.tools.LegMovementDetector;
import it.poli.android.scoutthisme.tools.LegMovementDetector.ILegMovementListener;
import it.poli.android.scoutthisme.tools.ScalarKalmanFilter;
import it.poli.android.scoutthisme.tools.SensorHandler;
import it.poli.android.scoutthisme.tools.SensorListener;
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
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

/**
 * A fragment that launches other parts of the demo application.
 */
public  class StepCounterSectionFragment extends Fragment implements ILegMovementListener, GpsListener
{
	public static final String strLatitudeExtra = "it.poli.latitude";
	public static final String strLongitudeExtra = "it.poli.longitude";
	private GoogleMap gMap;

	GpsHandler gpsHandler;
	SensorManager mSensorManager;
	LegMovementDetector legDect;
	Location loc, lastSensorLoc;
	Marker marker;
	boolean needDefaultZoom;
	final int defaultZoom = 13;
	int passi;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		gpsHandler = new GpsHandler(getActivity());
		
		mSensorManager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
		legDect = new LegMovementDetector(mSensorManager);
		legDect.addListener(this);
		
		needDefaultZoom = true;
		passi = 0;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View rootView = inflater.inflate(R.layout.fragment_section_stepcounter, container, false);
		this.gMap = ((SupportMapFragment) getFragmentManager().findFragmentById(R.id.mapStepCounter)).getMap();
		return rootView;
	}

	public void onDestroyView()
	{
		super.onDestroyView();
		SupportMapFragment mapFragment = ((SupportMapFragment) getFragmentManager().findFragmentById(R.id.mapStepCounter));
		if(mapFragment != null) {
			FragmentManager fM = getFragmentManager();
			fM.beginTransaction().remove(mapFragment).commit();
		}
	}

	@Override
	public void onResume()
	{
		super.onResume();
		
		gpsHandler.setListener(this);
		legDect.startDetector();
	}

	@Override
	public void onPause()
	{
		super.onPause();
		
		clearMap();
		gpsHandler.removeListener(this);
		legDect.stopDetector();
	}

	private void clearMap() {
		gMap.clear();
	}

	public void updateMap() {
		super.onResume();
		
		
		if (needDefaultZoom) {
			needDefaultZoom = false;
			gMap.moveCamera(CameraUpdateFactory.
			newLatLngZoom(new LatLng(loc.getLatitude(), loc.getLongitude()), defaultZoom));
		} else {
			gMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(loc.getLatitude(), loc.getLongitude())));
		}
	}

	@Override
	public void onLocationChanged(Location location) {
		this.loc = location;
		if (lastSensorLoc == null) {
			this.lastSensorLoc = location;
		}
		updateMap();
	}
	
	@Override
	public void onError(String string) {
		
	}

	@Override
	public void onLegActivity(int activity) {
		if (loc != null && lastSensorLoc != null && !loc.equals(lastSensorLoc) && activity == LegMovementDetector.LEG_MOVEMENT_FORWARD) {
			gMap.addPolyline(new PolylineOptions()
				.add(new LatLng(lastSensorLoc.getLatitude(), lastSensorLoc.getLongitude()),
						new LatLng(loc.getLatitude(), loc.getLongitude()))
				.width(4)
				.color(Color.WHITE));
			passi++;
				   
			TextView txtPss = (TextView) getView().findViewById(R.id.txtPassi);    
			txtPss.setText(String.format("%d", passi));
			
			lastSensorLoc = loc;
		}
	}
}