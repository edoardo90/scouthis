package it.poli.android.scoutthisme.tools;
import it.poli.android.scouthisme.R;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
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

   /**
     * A fragment that launches other parts of the demo application.
     */
    public class GpsSectionFragment extends Fragment implements SensorEventListener, LocationListener
    {    	
    	ImageView imgCompass; // Define the display assembly compass picture
    	float currentDegree = 0f; // Record the compass picture angle turned
    	SensorManager mSensorManager; // Device sensor manager
    	LocationManager locationManager;
    	TextView txtDegrees;
    	
    	@Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    	{
            View rootView = inflater.inflate(R.layout.fragment_section_gps, container, false);

    		imgCompass = (ImageView) rootView.findViewById(R.id.imageViewCompass); // Our compass image
    		txtDegrees = (TextView) rootView.findViewById(R.id.txtDegrees); // TextView that will tell the user what degree is he heading
    		
    		mSensorManager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE); // Initialize your android device sensor capabilities
    	    locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
    		
            return rootView;
        }
    	
        @Override
		public void onResume() {
    		super.onResume();
    		
    		// For the system's orientation sensor registered listeners
    		mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION),
    				SensorManager.SENSOR_DELAY_GAME);
    	    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
    	}

    	@Override
		public void onPause() {
    		super.onPause();
    		
    		mSensorManager.unregisterListener(this); // To stop the listener and save battery
    		locationManager.removeUpdates(this);
    	}

    	@Override
    	public void onSensorChanged(SensorEvent event)
    	{
    		float degree = Math.round(event.values[0]); // Get the angle around the z-axis rotated
    		txtDegrees.setText("Heading: " + Float.toString(degree) + " degrees");

    		// Create a rotation animation (reverse turn degree degrees)
    		RotateAnimation ra = new RotateAnimation(
    				currentDegree, 
    				-degree,
    				Animation.RELATIVE_TO_SELF, 0.5f, 
    				Animation.RELATIVE_TO_SELF,
    				0.5f);

    		ra.setDuration(210); // How long the animation will take place
    		ra.setFillAfter(true); // Set the animation after the end of the reservation status

    		imgCompass.startAnimation(ra); // Start the animation
    		currentDegree = -degree;
    	}

    	@Override
    	public void onAccuracyChanged(Sensor sensor, int accuracy) {
    		// Not in use
    	}

    	@Override
    	public void onLocationChanged(Location location) {
    		View rootView = getView();
    		
    	    double latitude = location.getLatitude();
    	    double longitude = location.getLongitude();

    	    Log.i("Geo_Location", "Latitude: " + latitude + ", Longitude: " + longitude);
    	    
    	    TextView txtLat = (TextView) rootView.findViewById(R.id.txtLat); // Our compass image
    	    TextView txtLong = (TextView) rootView.findViewById(R.id.txtLong); // TextView that will tell the user what degree is he heading
    		
    	    txtLat.setText(String.valueOf(latitude));
    	    txtLong.setText(String.valueOf(longitude));
    	}

    	@Override
    	public void onProviderDisabled(String provider) {
    		// Not in use
    	}

    	@Override
    	public void onProviderEnabled(String provider) {
    		// Not in use
    	}

    	@Override
    	public void onStatusChanged(String provider, int status, Bundle extras) {
    		// Not in use
    	}
    }