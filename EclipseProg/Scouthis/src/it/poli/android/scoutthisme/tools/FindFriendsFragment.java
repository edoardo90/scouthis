package it.poli.android.scoutthisme.tools;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import it.poli.android.scouthisme.R;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;


   /**
     * A fragment that launches other parts of the demo application.
     */
    public  class FindFriendsFragment extends Fragment implements OnClickListener  {
    	
    	private GoogleMap gMap;
    	
        
    	@Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_findfriends, container, false);
          
    		Button btnFnd = (Button) rootView.findViewById(R.id.btnFindFriends);
    		btnFnd.setOnClickListener(this);
    		
            return rootView;
        }

    	
    	@Override
		public void onCreate(Bundle savedInstanceState) {
    		super.onCreate(savedInstanceState);
    		
    	}
    	
		@Override
		public void onClick(View v) {
			this.gMap = ((SupportMapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
	    	Log.i("fnd fr", " button clicked !");
	    	
	        
	        Marker marker = gMap.addMarker(
	        			new MarkerOptions().
	        				position(new LatLng(51.5072, -0.1275)));
	        Marker mark2 = gMap.addMarker(
        			new MarkerOptions().
    				position(new LatLng(51.5172, -0.1275)));
	        
	        gMap.setMyLocationEnabled(true);
	        gMap.moveCamera(CameraUpdateFactory.
	        		newLatLngZoom(marker.getPosition(), 13));
	        
	        mark2.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.pig_no_bg));
	        
	        mark2.setTitle("rufy si trova qui");
	        
	        marker.setTitle("edo si trova qui");
	        marker.showInfoWindow();
	        
	        
	    	
			
		}
    	
        

    }