package it.poli.android.scoutthisme.fragments;

import it.poli.android.scouthisme.R;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import android.widget.ToggleButton;

/**
 * A fragment that launches the torch application
 */
public class UtilityFragment extends Fragment
{
	PackageManager pm;
    private Camera camera;
    Activity mAct;
	
	@Override
	public void onResume() {
		super.onResume();
    	if (!pm.hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
    		Toast.makeText(mAct, getString(R.string.utility_camera_not_available), Toast.LENGTH_LONG).show();
    	}
	}

	@Override
	public void onPause() {
		super.onPause();
		if (camera != null) {
			camera.release();
			camera = null;
		}
	}
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.fragment_section_utility, container, false);     
        mAct = getActivity();
		pm = mAct.getPackageManager();

        // Demonstration of a collection-browsing activity.
        rootView.findViewById(R.id.tglOnOff)
        .setOnClickListener(new View.OnClickListener() {
        	@Override
        	public void onClick(View view) {
        		ToggleButton btn = (ToggleButton) view;
        		if (btn.isChecked()) {        			
        			if (camera == null) {
        				try {
        					camera = Camera.open();
        				} catch(RuntimeException exception) {
        					Toast.makeText(mAct, getString(R.string.utility_camera_already_in_use), Toast.LENGTH_LONG).show();
        					btn.setChecked(!btn.isChecked());
        					return;
        				}
        			}
        			Parameters p = camera.getParameters();
        			p.setFlashMode(Parameters.FLASH_MODE_TORCH);
        			camera.setParameters(p);
        			camera.startPreview();
        		} else {        			
        			if (camera == null) {
        				try {
        					camera = Camera.open();
        				} catch(RuntimeException exception) {
        					Toast.makeText(mAct, getString(R.string.utility_camera_already_in_use), Toast.LENGTH_LONG).show();
        					btn.setChecked(!btn.isChecked());
        					return;
        				}
        			}
        			Parameters p = camera.getParameters();
        			p.setFlashMode(Parameters.FLASH_MODE_OFF);
        			camera.setParameters(p);
        			camera.stopPreview();
        		}
        	}
        });
        return rootView;
    }
}