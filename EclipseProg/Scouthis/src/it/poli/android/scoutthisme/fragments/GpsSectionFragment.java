package it.poli.android.scoutthisme.fragments;
import it.poli.android.scouthisme.R;
import it.poli.android.scoutthisme.tools.GpsHandler;
import it.poli.android.scoutthisme.tools.GpsListener;
import it.poli.android.scoutthisme.tools.SensorHandler;
import it.poli.android.scoutthisme.tools.SensorListener;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;
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
    	
		String[] latlongOrient = {"N", "N-NE", "NE", "E-NE", "E", "E-SE", "SE", "S-SE", "S", "S-SO", "SO", "O-SO", "O", "O-NO", "NO", "N-NO"};
    	
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
    		Sensor mySensor = event.sensor;
    	    if (mySensor.getType() == Sensor.TYPE_ACCELEROMETER) {
    	        float x = event.values[0];
    	        float y = event.values[1];
    	        float z = event.values[2];
    	        
    	        double accel = Math.sqrt((x*x + y*y + z*z) / (SensorManager.GRAVITY_EARTH * SensorManager.GRAVITY_EARTH)) - 1;
    	        
    	        TextView txtAcl = (TextView) getView().findViewById(R.id.txtAccel);
    	        if (accel > 0)
    	        	txtAcl.setText(String.format("%.2f", accel));
    	        else
    	        	txtAcl.setText(String.format("%.2f", 0.00));
        		
    	    } else {
        		float degree = Math.round(event.values[0]); // Get the angle around the z-axis rotated
        		txtDegrees.setText(String.format("%.0f", degree)+"°");

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
        		
        		int orient = (int)(Math.round((degree)/22.5)%16);
        		
        		TextView txtOrientation = (TextView) getView().findViewById(R.id.txtOrient);
        		txtOrientation.setText(latlongOrient[orient]);  
    	    }
    	}

    	@Override
    	public void onLocationChanged(Location location) {
    		View rootView = getView();
    		    		
    	    double latitude = location.getLatitude();
    	    double longitude = location.getLongitude();
    	    double altitude = location.getAltitude();
    	    double accuracy = location.getAccuracy();
    	    double bearing = location.getBearing();
    	    double speed = location.getSpeed()*3.6;

    	    Log.i("Geo_Location", "Latitude: " + latitude + ", Longitude: " + longitude);
    	    
    	    int locDegrees = (int)latitude;
    	    double locMinutes = ((latitude - locDegrees)*60);
    	    String locOrient = latitude >= 0 ? "N" : "S";
    	    
    	    int lonDegrees = (int)longitude;
    	    double lonMinutes = ((longitude - lonDegrees)*60);
    	    String lonOrient = longitude >= 0 ? "E" : "O";
    	    
    	    int orient = (int)(Math.round((bearing)/22.5)%16);
    	    
    	    TextView txtLat = (TextView) rootView.findViewById(R.id.txtLatitude);
    	    TextView txtLong = (TextView) rootView.findViewById(R.id.txtLongitude);
    	    TextView txtAlt = (TextView) rootView.findViewById(R.id.txtAltitude);
    	    TextView txtSpd = (TextView) rootView.findViewById(R.id.txtSpeed);    
    	    TextView txtPrc = (TextView) rootView.findViewById(R.id.txtPrecision);    
    	    TextView txtBea = (TextView) rootView.findViewById(R.id.txtBearing);    
    	    
    	    txtLat.setText(String.format("%d", Math.abs(locDegrees))+"°"+String.format("%.2f", Math.abs(locMinutes))+"'"+locOrient);
    	    txtLong.setText(String.format("%d", Math.abs(lonDegrees))+"°"+String.format("%.2f", Math.abs(lonMinutes))+"'"+lonOrient);
    	    txtAlt.setText(String.format("%.0f", altitude)+"m (WGS)");
    	    txtSpd.setText(String.format("%.0f", speed)+"km/h");
    	    txtPrc.setText(String.format("%.0f", accuracy)+"m");
    	    txtBea.setText(String.format("%.0f", bearing)+"°"+" "+latlongOrient[orient]);
    	}
    }