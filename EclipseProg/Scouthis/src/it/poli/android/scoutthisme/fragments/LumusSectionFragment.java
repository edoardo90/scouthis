package it.poli.android.scoutthisme.fragments;

import it.poli.android.scouthisme.R;
import it.poli.android.scoutthisme.tools.GpsHandler;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

/**
 * A fragment that launches the torch application
 */
public class LumusSectionFragment extends Fragment
{
	Context mContext;
	PackageManager pm;
	
    private Camera camera;
    boolean isLightOn = false;
    
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		mContext = getActivity().getBaseContext();
		pm = mContext.getPackageManager();
	}

	@Override
	public void onStop() {
		super.onStop();
		
		if (camera != null) {
			camera.release();
			camera = null;
			
			Log.i("info", "Rilascio camera");
		}
	}
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.fragment_section_lumus, container, false);
        
        Log.i("info", "sono qui");
        
    	if (!pm.hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
    		Log.e("err", "Device has no camera!");
    		return rootView;
    	}
        
        if (camera == null)
        {	
        	try {
        		camera = Camera.open();
        		Log.i("info", "accesa");
        	} catch(RuntimeException exception) {
        		Context cn = rootView.getContext();
        	    Toast.makeText(cn, "The camera and flashlight are in use by another app.", Toast.LENGTH_LONG).show();
        	    Log.i("info", "errore");
        	    // Exit gracefully
        	}
        }
        
        return rootView;
    }
}