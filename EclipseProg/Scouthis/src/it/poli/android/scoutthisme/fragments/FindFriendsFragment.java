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
import android.widget.TextView;

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
	boolean needDefaultZoom;
	final int defaultZoom = 13;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		gpsHandler = new GpsHandler(this.getActivity().getBaseContext());
	}
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
        View rootView = inflater.inflate(R.layout.fragment_section_findfriends, container, false);
        this.gMap = ((SupportMapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
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
		needDefaultZoom = true;
		gpsHandler.setListener(this);
	}
	
	@Override
	public void onPause()
	{
		super.onPause();
		gpsHandler.removeListener(this);
	    clearMap();
	}

    public void clearMap() {
		gMap.clear();
    }
	
	public void updateMap() {
		super.onResume();
		
		clearMap();
		
		MarkerOptions mo = new MarkerOptions().position(new LatLng(loc.getLatitude(), loc.getLongitude()));
		Marker marker = gMap.addMarker(mo);
		
		gMap.setMyLocationEnabled(true);
		
		if (needDefaultZoom) {
			needDefaultZoom = false;
			gMap.moveCamera(CameraUpdateFactory.
					newLatLngZoom(marker.getPosition(), defaultZoom));
		}
		else {
			gMap.moveCamera(CameraUpdateFactory.
					newLatLngZoom(marker.getPosition(), gMap.getCameraPosition().zoom));
		}
		
		marker.setTitle("Tu sei qui");
		marker.showInfoWindow();
		
		View rootView = getView();
		
	    TextView txtLat = (TextView) rootView.findViewById(R.id.txtLat); // Our compass image
	    TextView txtLong = (TextView) rootView.findViewById(R.id.txtLong); // TextView that will tell the user what degree is he heading
		
	    txtLat.setText(String.valueOf(loc.getLatitude()));
	    txtLong.setText(String.valueOf(loc.getLongitude()));
	}

	@Override
	public void onLocationChanged(Location location) {
		this.loc = location;			
		updateMap();
	}
}