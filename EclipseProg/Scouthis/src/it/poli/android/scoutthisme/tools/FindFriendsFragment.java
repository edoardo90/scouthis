package it.poli.android.scoutthisme.tools;
import it.poli.android.scouthisme.R;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

   /**
     * A fragment that launches other parts of the demo application.
     */
    public  class FindFriendsFragment extends Fragment implements LocationListener  {
    	
    	private GoogleMap gMap;
    	
    	@Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    	{
            View rootView = inflater.inflate(R.layout.fragment_section_findfriends, container, false);
            return rootView;
        }
    	
    	@Override
    	public void onResume()
    	{
    		super.onResume();
			this.gMap = ((SupportMapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
	        
	        Marker marker = gMap.addMarker(
	        			new MarkerOptions().
	        				position(new LatLng(51.5072, -0.1275)));
	        
	        gMap.setMyLocationEnabled(true);
	        gMap.moveCamera(CameraUpdateFactory.
	        		newLatLngZoom(marker.getPosition(), 13));
	        
	        marker.setTitle("edo si trova qui");
	        marker.showInfoWindow();
    	}
    	
    	@Override
    	public void onPause()
    	{
    		super.onPause();
    	    
    	    SupportMapFragment mapFragment = ((SupportMapFragment) getFragmentManager().findFragmentById(R.id.map));
		    if(mapFragment != null) {
		        FragmentManager fM = getFragmentManager();
		        fM.beginTransaction().remove(mapFragment).commit();
		    }
    	}

		@Override
		public void onLocationChanged(Location location) {
			// TODO Auto-generated method stub
		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
			// TODO Auto-generated method stub
		}

		@Override
		public void onProviderEnabled(String provider) {
			// TODO Auto-generated method stub
		}

		@Override
		public void onProviderDisabled(String provider) {
			// TODO Auto-generated method stub
		} 
    }