package it.poli.android.scoutthisme.fragments;

import it.poli.android.scouthisme.R;
import it.poli.android.scoutthisme.gps.utils.GpsHandler;
import it.poli.android.scoutthisme.gps.utils.GpsListener;
import it.poli.android.scoutthisme.gps.utils.LegMovementDetector;
import it.poli.android.scoutthisme.gps.utils.LegMovementDetector.ILegMovementListener;
import it.poli.android.scoutthisme.tools.ImageToolz;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.hardware.SensorManager;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.SnapshotReadyCallback;
import com.google.android.gms.maps.LocationSource.OnLocationChangedListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.PolylineOptions;

/**
 * Step Counter fragment:
 * Fragment used to display user position on the google map,
 * used to count user's steps.
 * The user path is displayed on the google map as a spline.
 */
public  class StepCounterRunFragment extends StepCounterFragmentArchetype implements ILegMovementListener, GpsListener
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
	int steps;

	/**
	 * Initialize gpsHandler (used to know user gps position)
	 * Initialize legDetector (used to know each user step)
	 * see: {@link GpsHandler}
	 * see: {@link LegMovementDetector}
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		gpsHandler = new GpsHandler(getActivity());
		
		mSensorManager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
		legDect = new LegMovementDetector(mSensorManager);
		legDect.addListener(this);
		
		needDefaultZoom = true;
		steps = 0;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View rootView = inflater.inflate(R.layout.fragment_section_stepcounter_run, container, false);
		this.gMap = ((SupportMapFragment) getFragmentManager().findFragmentById(R.id.mapStepCounter)).getMap();
		return rootView;
	}
	/**
	 * Unregisters  listeners (gps and leg detector)
	 */
	public void onDestroyView()
	{
		super.onDestroyView();
		
		gpsHandler.removeListener();
		legDect.stopDetector();
		
		SupportMapFragment mapFragment = ((SupportMapFragment) getFragmentManager().findFragmentById(R.id.mapStepCounter));
		if(mapFragment != null) {
			clearMap();
			
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
		
		Button btnEndRun = (Button)this.getActivity().findViewById(R.id.step_btn_end_run);
		btnEndRun.setOnClickListener(new OnClickListener() {
			
			@Override
	            public void onClick(View v) {
					transitionTowars(new StepCounterHomeFragment());
			}
		});
		
		
		Button btnPauseRun = (Button)this.getActivity().findViewById(R.id.step_btn_pause);
		btnPauseRun.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
			}
		});
		
	}
	
	
	private void saveMapAsImage()
	{
		SnapshotReadyCallback callback = new SnapshotReadyCallback() {
            Bitmap bitmap;
            
            @Override
            public void onSnapshotReady(Bitmap snapshot) {
                // TODO Auto-generated method stub
                bitmap = snapshot;
                writeBMPOnDisk(bitmap);
                
            }
        };

        gMap.snapshot(callback);
	}

	
	private void writeBMPOnDisk(Bitmap bmp)
	{
		ImageToolz.writeBitmapOnDisk(bmp, "gatto");
	}
	
	private void clearMap() {
		gMap.clear();
	}
	
	/**
	 * Executed each time the user changes its position
	 * Information from position comes from the gps andler
	 * see {@link OnLocationChangedListener}
	 */
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
	public void onLegActivity(int activity) {
		if (loc != null && lastSensorLoc != null && !loc.equals(lastSensorLoc)) {
			gMap.addPolyline(new PolylineOptions()
				.add(new LatLng(lastSensorLoc.getLatitude(), lastSensorLoc.getLongitude()),
						new LatLng(loc.getLatitude(), loc.getLongitude()))
				.width(4)
				.color(Color.WHITE));
			steps++;
				   
			TextView txtPss = (TextView) getView().findViewById(R.id.txtPassi);    
			txtPss.setText(String.format("%d", steps));
			
			lastSensorLoc = loc;
		}
	}
}

