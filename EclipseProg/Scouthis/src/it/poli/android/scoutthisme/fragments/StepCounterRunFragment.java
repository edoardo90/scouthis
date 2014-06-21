package it.poli.android.scoutthisme.fragments;

import it.poli.android.scouthisme.R;
import it.poli.android.scoutthisme.Constants;
import it.poli.android.scoutthisme.gps.utils.GpsHandler;
import it.poli.android.scoutthisme.gps.utils.GpsListener;
import it.poli.android.scoutthisme.stepcounter.utils.LegMovementDetector;
import it.poli.android.scoutthisme.stepcounter.utils.RunEpisode;
import it.poli.android.scoutthisme.stepreload.stats.PedometerSettings;
import it.poli.android.scoutthisme.stepreload.stats.StepService;
import it.poli.android.scoutthisme.stepreload.stats.Utils;
import it.poli.android.scoutthisme.tools.CircleButton;
import it.poli.android.scoutthisme.tools.ImageToolz;
import it.poli.android.scoutthisme.tools.TextFilesUtils;

import java.util.Calendar;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.hardware.SensorManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.SnapshotReadyCallback;
import com.google.android.gms.maps.LocationSource.OnLocationChangedListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

/**
 * Step Counter fragment:
 * Fragment used to display user position on the google map,
 * used to count user's steps.
 * The user path is displayed on the google map as a spline.
 */
public  class StepCounterRunFragment extends StepCounterFragmentArchetype 
		implements GpsListener
{
	public static final String strLatitudeExtra = "it.poli.latitude";
	public static final String strLongitudeExtra = "it.poli.longitude";
	private GoogleMap gMap;

	Activity mAct;

	GpsHandler gpsHandler;
	SensorManager mSensorManager;
	
	Location loc, lastSensorLoc;
	Marker marker;
	boolean needDefaultZoom;
	final int defaultZoom = 13;

	/* Stepcounter's path variables */
	int steps;
	int secondsAtTheBeginning = 0;
	int secondsAtTheEnd = 0;
	float stepSize = 0;
	float userHeight = 1.7f;
	private float speed = 0;
	private float distance = 0;
	private int elapsedTime = 0;
	


	private int id;

	/** new vars **/
	private static final String TAG = "Pedometer";
    private SharedPreferences mSettings;
    private PedometerSettings mPedometerSettings;
    private Utils mUtils;
    
    private TextView txtPassi;
    private TextView txtElapsedTime;
    private TextView txtDistance;
    private TextView txtAverageSpeed;
    private TextView mCaloriesValueView;
    private int mStepValue;
    private int mPaceValue;
    private float mDistanceValue;
    private float mSpeedValue;
    private int mCaloriesValue;
    private float mDesiredPaceOrSpeed;
    private int mMaintain;
    private boolean mQuitting = false;
    private long startRunMilliSeconds = 0;

    private String unitaDiMisura  = "";
    
    /**
     * True, when service is running.
     */
    private boolean mIsRunning;

	
	
	/** fine news vars **/
	
	
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
		gpsHandler.setListener(this);

		
		mAct = getActivity();

		mSensorManager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
		
		needDefaultZoom = true;
		steps = 0;

		secondsAtTheBeginning = Calendar.getInstance().get(Calendar.SECOND);
		
		gpsHandler.setListener(this);
		
		
		/** new things **/
		
        mStepValue = 0;
        mPaceValue = 0;
        
        mUtils = Utils.getInstance();

        mSettings = PreferenceManager.getDefaultSharedPreferences(this.getActivity());
        mPedometerSettings = new PedometerSettings(mSettings);
        
        mUtils.setSpeak(mSettings.getBoolean("speak", false));
        
        // Read from preferences if the service was running on the last onPause
        mIsRunning = mPedometerSettings.isServiceRunning();
        
        // Start the service if this is considered to be an application start (last onPause was long ago)
        if (!mIsRunning && mPedometerSettings.isNewStart()) {
            startStepService();
            bindStepService();
        }
        else if (mIsRunning) {
            bindStepService();
        }
        
        this.startRunMilliSeconds = Utils.currentTimeInMillis();
        
        mPedometerSettings.clearServiceRunning();

        /** end things with components **/
        
        displayDesiredPaceOrSpeed();
    }
	    
    
        
        
        
	

private View findViewById(int desiredPaceLabel) {
		return this.getActivity()
				.findViewById(desiredPaceLabel);
	}







private void displayDesiredPaceOrSpeed() {
//    if (mMaintain == PedometerSettings.M_PACE) {
//        mDesiredPaceView.setText("" + (int)mDesiredPaceOrSpeed);
//    }
//    else {
//        mDesiredPaceView.setText("" + mDesiredPaceOrSpeed);
//    }
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

		////// gpsHandler.setViewActive(false);  /**  SERVE? E' DANNOSO?  **/
		
		new Thread(new Runnable() 
		{
			public void run() 
			{
				id = TextFilesUtils.getLastIdFromXml(getActivity(), Constants.XML_PATH_STEPCOUNTER);
			}
		}).start();

		txtPassi     = (TextView) findViewById(R.id.txtPassi);
        txtElapsedTime     = (TextView) findViewById(R.id.txtElapsedTime);
        txtDistance = (TextView) findViewById(R.id.txtDistance);
        txtAverageSpeed    = (TextView) findViewById(R.id.txtAverageSpeed);
        
        mCaloriesValueView = (TextView) findViewById(R.id.txtNull);
        
        CircleButton btnEndRun = (CircleButton)this.getActivity().findViewById(R.id.step_btn_end_run);
		btnEndRun.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
		        resetValues(true);
				Log.i(TAG, "STOPPED SERVICE!");
				saveRunEpisodeOnFile();	
				transitionTowars(new StepCounterFragment());
			}
		});
		
		Log.i(TAG, "on resume, clear?");
		resetValues(true);  //l'alternativa � salvare i secondi trascorsi 
		
		
		updateMap();
	}

    @Override
	public void onPause() {
        Log.i(TAG, "[ACTIVITY] onPause");
        
        ///// gpsHandler.setViewActive(false); /** serve?  E' DANNOSO? **/
        mPedometerSettings.saveServiceRunningWithNullTimestamp(mIsRunning);
        
        /*
        if (mIsRunning) {
            unbindStepService();
        }
        if (mQuitting) {
            mPedometerSettings.saveServiceRunningWithNullTimestamp(mIsRunning);
        }
        else {
            mPedometerSettings.saveServiceRunningWithTimestamp(mIsRunning);
        }
		*/

        super.onPause();
        savePaceSetting();
    }
	
	@Override
	public void onDestroy()
	{
		super.onDestroy();
		
		gpsHandler.removeListener();
		
	}
	
	protected void onRestart() {
	        Log.i(TAG, "[ACTIVITY] onRestart");
	        super.onDestroy();
	}
	

	private void saveRunEpisodeOnFile()
	{	
		this.id++;
		this.saveMapAsImage();
		RunEpisode rep = new RunEpisode(this.id, this.distance, this.steps, this.elapsedTime, this.speed);
		TextFilesUtils.appendXmlElement(getActivity(),
				Constants.XML_PATH_STEPCOUNTER,
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
		if (loc == null) {
			gMap.moveCamera(CameraUpdateFactory.
					newLatLngZoom(new LatLng(41.29246, 12.5736108), 5));
		}
		else if (needDefaultZoom) {
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
		
		Log.i("PEDOMETER", "LOCATION CHANGED: " + location.getLatitude() + ", " + location.getLongitude());
		
		Toast.makeText(this.getActivity().getApplicationContext(),
				"" + location.getLatitude() + ", " + location.getLongitude()
				, Toast.LENGTH_SHORT).show();
		
		if (lastSensorLoc == null) {
			this.lastSensorLoc = location;
		}
		updateMap();
	}

	
    private void savePaceSetting() {
        mPedometerSettings.savePaceOrSpeedSetting(mMaintain, mDesiredPaceOrSpeed);
    }

    private StepService mService;
    
    private ServiceConnection mConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            mService = ((StepService.StepBinder)service).getService();

            mService.registerCallback(mCallback);
            mService.reloadSettings();
            
        }

        public void onServiceDisconnected(ComponentName className) {
            mService = null;
        }
    };
    

    private void startStepService() {
        if (! mIsRunning) {
            Log.i(TAG, "[SERVICE] Start");
            mIsRunning = true;
            this.getActivity().getApplicationContext()
            .startService(new Intent(this.getActivity(),     StepService.class));
        }
    }
    
    private void bindStepService() {
        Log.i(TAG, "[SERVICE] Bind");
        this.getActivity()
        .getApplicationContext()
        .bindService(new Intent(this.getActivity(), 
                StepService.class), mConnection, Context.BIND_AUTO_CREATE + Context.BIND_DEBUG_UNBIND);
    }

    private void unbindStepService() {
        Log.i(TAG, "[SERVICE] Unbind");
        this.getActivity().getApplicationContext().
        unbindService(mConnection);
    }
    
    private void stopStepService() {
        Log.i(TAG, "[SERVICE] Stop");
        if (mService != null) {
            Log.i(TAG, "[SERVICE] stopService");
            this.getActivity().getApplicationContext().
            stopService(new Intent(this.getActivity(),
                  StepService.class));
        }
        mIsRunning = false;
    }
    
    private void resetValues(boolean updateDisplay) {
        if (mService != null && mIsRunning) {
            mService.resetValues();                    
        }
        else {
            txtPassi.setText("0");
            txtElapsedTime.setText("0");
            txtDistance.setText("0");
            txtAverageSpeed.setText("0");
            mCaloriesValueView.setText("0");
            SharedPreferences state = this.getActivity().getApplicationContext() 
            		.getSharedPreferences("state", 0);
            SharedPreferences.Editor stateEditor = state.edit();
            if (updateDisplay) {
                stateEditor.putInt("steps", 0);
                stateEditor.putInt("pace", 0);
                stateEditor.putFloat("distance", 0);
                stateEditor.putFloat("speed", 0);
                stateEditor.putFloat("calories", 0);
                stateEditor.commit();
            }
        }
    }

    
    private static final int MENU_QUIT     = 9;

    private static final int MENU_PAUSE = 1;
    private static final int MENU_RESUME = 2;
    private static final int MENU_RESET = 3;
    
    /* Creates the menu items */
    public void onPrepareOptionsMenu(Menu menu) {

    	/***  NOT NOW... ***/
    	//        menu.clear();
//        if (mIsRunning) {
//            menu.add(0, MENU_PAUSE, 0, R.string.pause)
//            .setIcon(android.R.drawable.ic_media_pause)
//            .setShortcut('1', 'p');
//        }
//        else {
//            menu.add(0, MENU_RESUME, 0, R.string.resume)
//            .setIcon(android.R.drawable.ic_media_play)
//            .setShortcut('1', 'p');
//        }
//        
//        menu.add(0, MENU_RESET, 0, R.string.reset)
//        .setIcon(android.R.drawable.ic_menu_close_clear_cancel)
//        .setShortcut('2', 'r');
//        menu.add(0, MENU_SETTINGS, 0, R.string.settings)
//        .setIcon(android.R.drawable.ic_menu_preferences)
//        .setShortcut('8', 's')
//        .setIntent(new Intent(this.getActivity()
//        		, Settings.class));
//        menu.add(0, MENU_QUIT, 0, R.string.quit)
//        .setIcon(android.R.drawable.ic_lock_power_off)
//        .setShortcut('9', 'q');
//      
//        return;
    }

    /* Handles item selections */
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case MENU_PAUSE:
                unbindStepService();
                stopStepService();
                return true;
            case MENU_RESUME:
                startStepService();
                bindStepService();
                return true;
            case MENU_RESET:
                resetValues(true);
                return true;
            case MENU_QUIT:
                resetValues(false);
                unbindStepService();
                stopStepService();
                mQuitting = true;
                this.getActivity()
                .finish();
                return true;
        }
        return false;
    }
 
    // TODO: unite all into 1 type of message
    private StepService.ICallback mCallback = new StepService.ICallback() {
        public void stepsChanged(int value) {
            mHandler.sendMessage(mHandler.obtainMessage(STEPS_MSG, value, 0));
        }
        public void paceChanged(int value) {
            mHandler.sendMessage(mHandler.obtainMessage(PACE_MSG, value, 0));
        }
        public void distanceChanged(float value) {
            mHandler.sendMessage(mHandler.obtainMessage(DISTANCE_MSG, (int)(value*1000), 0));
        }
        public void speedChanged(float value) {
            mHandler.sendMessage(mHandler.obtainMessage(SPEED_MSG, (int)(value*1000), 0));
        }
        public void caloriesChanged(float value) {
            mHandler.sendMessage(mHandler.obtainMessage(CALORIES_MSG, (int)(value), 0));
        }
    };
    
    private static final int STEPS_MSG = 1;
    private static final int PACE_MSG = 2;
    private static final int DISTANCE_MSG = 3;
    private static final int SPEED_MSG = 4;
    private static final int CALORIES_MSG = 5;
    
    @SuppressLint("HandlerLeak")
	private Handler mHandler = new Handler() {
        

		@Override public void handleMessage(Message msg) {
            switch (msg.what) {
                case STEPS_MSG:
                    mStepValue = (int)msg.arg1;
                    
                    steps = mStepValue;
                    
                    txtPassi.setText("" + mStepValue);
                    
                    break;
                case PACE_MSG:
                    mPaceValue = msg.arg1;
                    if (mPaceValue <= 0) { 
                        txtElapsedTime.setText("0");
                    }
                    else {
                        txtElapsedTime.setText("" + (int)mPaceValue);
                    }
                    break;
                case DISTANCE_MSG:
                    
                	mDistanceValue = ((int)msg.arg1)/1000f;
                    
                  
                    mDistanceValue = convertMilesToKm(mDistanceValue);
                    distance = mDistanceValue;
                    
                    float multiplier = 1;
                    
                    if (mDistanceValue < 1) 
                    {	
                    	unitaDiMisura = "m";
                    	multiplier = 1000;
                    }
                    else
                    {
                    	unitaDiMisura = "km";
                    	multiplier = 1;
                    }
                    
                    mDistanceValue *= multiplier;
                    
                    distance = mDistanceValue;
                    
                    if (mDistanceValue <= 0) { 
                        txtDistance.setText("0");
                    }
                    else {
                        txtDistance.setText(
                                ("" + (mDistanceValue + 0.000001f)).substring(0, 5)
                                    +  " " + unitaDiMisura
                        );
                        
                    }
                    break;
                case SPEED_MSG:
                    mSpeedValue = ((int)msg.arg1)/1000f;
                    
                    Log.i("Stepper", "speed [miles/hour] : " + mSpeedValue);
                    
                    mSpeedValue = convertMilesToKm(mSpeedValue);
                    speed = mSpeedValue;
                    
                    float time_sec = Utils.currentTimeInMillis() - startRunMilliSeconds;
                    time_sec = time_sec / 1000;
                    
                    elapsedTime = (int) time_sec;
                    
                    unitaDiMisura = "s";
                    if (time_sec > 60)
                    {
                    	unitaDiMisura = "min";
                    	time_sec = time_sec / 60f;
                    }
                    if (time_sec > 60)
                    {
                    	unitaDiMisura = "ore";
                    	time_sec = time_sec / 60f;
                    }
                    
                    elapsedTime =(int) time_sec;
                    
                    txtElapsedTime.setText("" + elapsedTime +  " "+ unitaDiMisura);
                    
                    if (mSpeedValue <= 0) { 
                        txtAverageSpeed.setText("0");
                    }
                    else {
                        txtAverageSpeed.setText(
                                ("" + (mSpeedValue + 0.000001f)).substring(0, 4)
                                    +  " " +  "km/h"
                        );
                    }
                    break;
                case CALORIES_MSG:
                    mCaloriesValue = msg.arg1;
                    
                    if (mCaloriesValue <= 0) { 	
                        mCaloriesValueView.setText("0");
                    }
                    else {
                        mCaloriesValueView.setText("" + (int)mCaloriesValue);
                    }
                    break;
                default:
                    super.handleMessage(msg);
            }
        }
        
    };



	protected float convertMilesToKm(float sizeInMiles) {
		return sizeInMiles * 1.609344f;
	}
   
	
	
	
	
	
	
	
	
}