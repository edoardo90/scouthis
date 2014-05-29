package it.poli.android.scoutthisme.fragments;

import it.poli.android.scouthisme.R;
import it.poli.android.scoutthisme.Constants;
import it.poli.android.scoutthisme.gps.utils.GpsHandler;
import it.poli.android.scoutthisme.gps.utils.GpsListener;
import it.poli.android.scoutthisme.gps.utils.LegMovementDetector;
import it.poli.android.scoutthisme.gps.utils.LegMovementDetector.ILegMovementListener;
import it.poli.android.scoutthisme.stepcounter.utils.RunEpisode;
import it.poli.android.scoutthisme.tools.ImageToolz;
import it.poli.android.scoutthisme.tools.TextFilesUtils;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.Calendar;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.hardware.SensorManager;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;
import at.markushi.ui.CircleButton;

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
public  class StepCounterRunFragment extends StepCounterFragmentArchetype implements GpsListener, ILegMovementListener
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
	int steps ;
	int secondsAtTheBeginning = 0;
	int secondsAtTheEnd = 0;
	float stepSize = 0;
	float userHeight = 1.7f;
	private float speed = 0;
	private float distance = 0;
	private int elapsedTime = 0;
	private int secondsNow = 0;
	private TextView txtElapsedTime;
	private TextView txtAverageSpeed;
	private TextView txtDistance;
	private TextView txtStepsDone;
	private int id;

	/**
	 * Initialize gpsHandler (used to know user gps position)
	 * Initialize legDetector (used to know each user step)
	 * see: {@link GpsHandler}
	 * see: {@link LegMovementDetector}
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		gpsHandler = new GpsHandler(this);

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
		/*super.setIdGpsAlertContainer(R.id.step_alert_gps_container);
		super.setAlertMessage(getString(R.string.fragments_step_alert));*/

		secondsAtTheBeginning = Calendar.getInstance().get(Calendar.SECOND);

		gpsHandler.setListener(this);
		legDect.startDetector();

		new Thread(new Runnable() 
		{
			public void run() 
			{
				id = TextFilesUtils.getLastIdFromXml(getActivity(), Constants.XML_PATH_STEPCOUNTER);
			}
		}).start();

		txtElapsedTime = (TextView) getView().findViewById(R.id.txtElapsedTime);
		txtAverageSpeed = (TextView) getView().findViewById(R.id.txtAverageSpeed);
		txtDistance = (TextView) getView().findViewById(R.id.txtDistance);
		txtStepsDone = (TextView) getView().findViewById(R.id.txtPassi);

		CircleButton btnEndRun = (CircleButton)this.getActivity().findViewById(R.id.step_btn_end_run);
		btnEndRun.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				saveRunEpisodeOnFile();	
				transitionTowars(new StepCounterFragment());
			}
		});
	}

	private void saveRunEpisodeOnFile()
	{	
		this.id++;
		this.saveMapAsImage();
		RunEpisode rep = new RunEpisode(this.id, this.distance, this.steps, this.elapsedTime, this.speed);
		TextFilesUtils.appendXmlElement(getActivity(), Constants.XML_PATH_STEPCOUNTER,
				rep.getXmlTagFieldMap(), 
				Constants.XML_TAG_RUNEPISODES);

	}

	private void saveMapAsImage()
	{
		SnapshotReadyCallback callback = new SnapshotReadyCallback()
		{
			@Override
			public void onSnapshotReady(Bitmap snapshot) 
			{
				ImageToolz.storeImage(snapshot, Constants.SD_IMAGE_DIR, Constants.IMAGE_MAP_PREFIX + id);
			}
		};

		gMap.snapshot(callback);
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

	private float getStepsize()
	{
		if (this.stepSize != 0)
			return stepSize;
		else
			return this.userHeight * Constants.STEP_HEIGHT_CONST ;
	}

	/**
	 * Computes some stats like speed, elapsed time
	 * and writes them on txtViews
	 */
	private void updateStats()
	{
		secondsNow = Calendar.getInstance().get(Calendar.SECOND);

		//secondi
		elapsedTime = Math.abs(secondsNow - this.secondsAtTheBeginning);

		//metri
		distance = steps * getStepsize();
		BigDecimal distB = new BigDecimal(distance).round(new MathContext(3, RoundingMode.HALF_UP));
		distance = distB.floatValue();
		if(elapsedTime == 0)
			elapsedTime = 1;

		//metri al secondo
		speed = distance / elapsedTime;

		//km all'ora
		speed = 3.6f * speed;

		BigDecimal speedB = new BigDecimal(speed).round(new MathContext(3, RoundingMode.HALF_UP));
		speed = speedB.floatValue();

		txtAverageSpeed.setText(String.valueOf(speedB) + " km/h");
		txtDistance.setText(String.valueOf(distB) + " m ");
		txtElapsedTime.setText(String.valueOf(elapsedTime) + " secondi ");
		txtStepsDone.setText(String.valueOf(steps) );
		txtStepsDone.setText(String.format("%d", steps));
	}

	@Override
	public void onLegActivity(int activity)
	{
		if(Constants.DEBUG_ENABLED)
			steps ++;

		if (loc != null && lastSensorLoc != null && !loc.equals(lastSensorLoc)) {
			gMap.addPolyline(new PolylineOptions()
			.add(new LatLng(lastSensorLoc.getLatitude(), lastSensorLoc.getLongitude()),
					new LatLng(loc.getLatitude(), loc.getLongitude()))
					.width(4)
					.color(Color.WHITE));
			steps++;
			lastSensorLoc = loc;
		}

		updateStats();
	}

	@Override
	public void onProvidereEnabled(String provider) { }

	@Override
	public void onProviderDisabled(String provider) { }
}