package it.poli.android.scoutthisme.tools;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import it.poli.android.scouthisme.R;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;

/**
 * Activity which displays a login screen to the user, offering registration as
 * well.
 */
@SuppressLint("NewApi")
public class FindFriendsActivity extends Activity  {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.friends_act);
		
		
		}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		//getMenuInflater().inflate(R.menu.login, menu);
		return true;
	}

	private GoogleMap gMap;
	
	public void showFriends(View v) {
		
		this.gMap = ((MapFragment) getFragmentManager()
                .findFragmentById(R.id.mapSecond)).getMap();
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
