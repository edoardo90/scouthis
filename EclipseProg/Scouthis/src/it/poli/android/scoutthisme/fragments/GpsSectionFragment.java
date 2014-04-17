package it.poli.android.scoutthisme.fragments;
import it.poli.android.scouthisme.R;
import it.poli.android.scoutthisme.tools.GpsHandler;
import it.poli.android.scoutthisme.tools.GpsListener;
import it.poli.android.scoutthisme.tools.SensorHandler;
import it.poli.android.scoutthisme.tools.SensorListener;
import android.hardware.SensorEvent;
import android.location.Location;
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
    public class GpsSectionFragment extends Fragment implements SensorListener, GpsListener
    {    	
    	ImageView imgCompass; // Define the display assembly compass picture
    	float currentDegree = 0f; // Record the compass picture angle turned
    	
    	SensorHandler sensorHandler;
    	GpsHandler gpsHandler;
    	TextView txtDegrees;
    	
    	@Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    	{
    		sensorHandler = new SensorHandler(this.getActivity().getBaseContext());
    		gpsHandler = new GpsHandler(this.getActivity().getBaseContext());
    		
            View rootView = inflater.inflate(R.layout.fragment_section_gps, container, false);
    		imgCompass = (ImageView) rootView.findViewById(R.id.imageViewCompass); // Our compass image
    		txtDegrees = (TextView) rootView.findViewById(R.id.txtDegrees); // TextView that will tell the user what degree is he heading
    		
            return rootView;
        }
    	
        @Override
		public void onResume() {
    		super.onResume();
    		
    		sensorHandler.setListener(this);
    		gpsHandler.setListener(this);
    	}

    	@Override
		public void onPause() {
    		super.onPause();
    		
    		sensorHandler.removeListener(this);
    		gpsHandler.removeListener(this);
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
    }