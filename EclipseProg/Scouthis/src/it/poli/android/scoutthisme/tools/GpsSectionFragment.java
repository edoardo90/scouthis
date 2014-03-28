package it.poli.android.scoutthisme.tools;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.effectivenavigation.R;

   /**
     * A fragment that launches other parts of the demo application.
     */
    public  class GpsSectionFragment extends Fragment implements SensorEventListener {

    	// define the display assembly compass picture
    	private ImageView image;

    	// record the compass picture angle turned
    	private float currentDegree = 0f;

    	// device sensor manager
    	private SensorManager mSensorManager;

    	TextView tvHeading;
    	
        
    	@Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_section_gps, container, false);
            
            if(rootView == null)
            	Log.i("create view", "root view is null!");
            else
            	Log.i("create view", "root view is not null! :) ");
  		  
    		// our compass image
    		image = (ImageView)   rootView.findViewById(R.id.imageViewCompass);

    		if(image == null)
    			Log.i("on create  ", " image � null dopo la sua creazione");
    		
    		
    		// TextView that will tell the user what degree is he heading
    		tvHeading = (TextView) rootView.findViewById(R.id.tvHeading1);
    		
    		if(tvHeading == null)
    			Log.i("on create  ", " tvHeading � null dopo la sua creazione");
    		
    		// initialize your android device sensor capabilities
    		mSensorManager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);

    		if(mSensorManager == null)
    			Log.i("on create  ", " mSensor MGR � null dopo la sua creazione");

    		
    		
            return rootView;
        }

    	
    	@Override
		public void onCreate(Bundle savedInstanceState) {
    		super.onCreate(savedInstanceState);
    		
    
	    	
    	}
    	
        @Override
		public void onResume() {
    		super.onResume();
    		
    		// for the system's orientation sensor registered listeners
    		if(mSensorManager == null)
    			Log.i("on resume ", " mSensor Mgr � null!!");
    		mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION),
    				SensorManager.SENSOR_DELAY_GAME);
    	}

    	@Override
		public void onPause() {
    		super.onPause();
    		
    		// to stop the listener and save battery
    		mSensorManager.unregisterListener(this);
    	}

    	@Override
    	public void onSensorChanged(SensorEvent event) {

    		// get the angle around the z-axis rotated
    		float degree = Math.round(event.values[0]);
    		
    		if(tvHeading == null)
    			Log.i("sensor changed ", "  tvHead is null!!");
    		
    		tvHeading.setText("Heading: " + Float.toString(degree) + " degrees");

    		// create a rotation animation (reverse turn degree degrees)
    		RotateAnimation ra = new RotateAnimation(
    				currentDegree, 
    				-degree,
    				Animation.RELATIVE_TO_SELF, 0.5f, 
    				Animation.RELATIVE_TO_SELF,
    				0.5f);

    		// how long the animation will take place
    		ra.setDuration(210);

    		// set the animation after the end of the reservation status
    		ra.setFillAfter(true);

    		// Start the animation
    		image.startAnimation(ra);
    		currentDegree = -degree;

    	}

    	@Override
    	public void onAccuracyChanged(Sensor sensor, int accuracy) {
    		// not in use
    	}
    }