package it.poli.android.scoutthisme.tools;
import android.content.Context;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.example.android.effectivenavigation.R;


/**
 * A fragment that launches the torch application
 */

public class LumusSectionFragment extends Fragment {

	    private static Camera camera;
	    boolean isLighOn = false;

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
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_section_lumus, container, false);
            
            Log.i("info", "sono qui");
            if (camera == null){
            	
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

    		final Parameters p = camera.getParameters();
            
            // Demonstration of a collection-browsing activity.
            rootView.findViewById(R.id.toggleButton1)
                    .setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
            				if (isLighOn) {
            					Log.i("info", "torch is turn off!");
             
            					p.setFlashMode(Parameters.FLASH_MODE_OFF);
            					camera.setParameters(p);
            					camera.stopPreview();
            					isLighOn = false;
            				} else {
            					Log.i("info", "torch is turn on!");
             
            					p.setFlashMode(Parameters.FLASH_MODE_TORCH);
             
            					camera.setParameters(p);
            					camera.startPreview();
            					isLighOn = true;
            				}
        					ToggleButton b = (ToggleButton) view;
        					b.setChecked(isLighOn);
                        }
                    });
            
            return rootView;
        }
    }
    
