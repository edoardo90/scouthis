package it.poli.android.scoutthisme.fragments;
import it.poli.android.scouthisme.R;
import it.poli.android.scoutthisme.tools.GpsHandler;
import it.poli.android.scoutthisme.tools.GpsListener;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
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
    	private GoogleMap gMap;
    	GpsHandler gpsHandler;
    	Location loc;
    	Marker marker;
    	
    	@Override
    	public void onCreate(Bundle savedInstanceState) {
    		super.onCreate(savedInstanceState);
    		//gpsHandler = new GpsHandler(this.getActivity().getBaseContext());
    	}
    	
    	@Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    	{
            View rootView = inflater.inflate(R.layout.fragment_section_findfriends, container, false);
			
            
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
    		//gpsHandler.setListener(this);
    		updateMap();
    	}
    	
    	@Override
    	public void onPause()
    	{
    		super.onPause();
    		//gpsHandler.removeListener(this);
    	    clearMap();
    	}
    
	    public void clearMap() {
    		if (!gMap.equals(null))
    		{
    			gMap.clear();
    		}
	    }
    	
    	public void updateMap() {
   			//clearMap();
    	     super.onResume();
    	     this.gMap = ((SupportMapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();

    	     MarkerOptions mo = new MarkerOptions().position(new LatLng(51.5072, -0.1275));
    	     Marker marker = gMap.addMarker(mo);

    	     gMap.setMyLocationEnabled(true);
    	     gMap.moveCamera(CameraUpdateFactory.
    	     newLatLngZoom(marker.getPosition(), 13));

    	     marker.setTitle("edo si trova qui");
    	     marker.showInfoWindow();
    	}

		@Override
		public void onLocationChanged(Location location) {
			loc = location;			
			//updateMap();
		}
    }