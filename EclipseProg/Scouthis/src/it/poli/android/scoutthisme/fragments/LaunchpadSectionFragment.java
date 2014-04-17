package it.poli.android.scoutthisme.fragments;

import it.poli.android.scouthisme.R;
import it.poli.android.scoutthisme.FacebookActivity;
import it.poli.android.scoutthisme.tools.DownloadService;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class LaunchpadSectionFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) 
    {
        View rootView = inflater.inflate(R.layout.fragment_section_launchpad, container, false);

        // Demonstration of a collection-browsing activity.
	    rootView.findViewById(R.id.demo_collection_button)
            .setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getActivity(), FacebookActivity.class);
                    startActivity(intent);
                }
            });

	    // Demonstration of navigating to external activities.
	    rootView.findViewById(R.id.demo_external_activity)
            .setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // Create an intent that asks the user to pick a photo, but using
                    // FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET, ensures that relaunching
                    // the application from the device home screen does not return
                    // to the external activity.
                    Intent externalActivityIntent = new Intent(Intent.ACTION_PICK);
                    externalActivityIntent.setType("image/*");
                    externalActivityIntent.addFlags(
                            Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
                    startActivity(externalActivityIntent);
                }
        });
    
		Log.i(" launch pad - on create", " onCR ora parto il thread, contaci");
		 
		Intent intent = new Intent(getActivity(), DownloadService.class);
		// add infos for the service which file to download and where to store
		intent.putExtra(DownloadService.FILENAME, "index.html");
		intent.putExtra(DownloadService.URL,
		    "http://superheroexperiments.com/wp-content/uploads/2013/04/elephant-painting-itself1.jpg");


		getActivity().startService(intent);
		
		    return rootView;
	}

//	private BroadcastReceiver receiver = new BroadcastReceiver()
//	{
//	    @Override
//	    public void onReceive(Context context, Intent intent) {
//			Log.i("onReceive", " i finished ");
//			Bundle bundle = intent.getExtras();
//			if (bundle != null) {
//			  String string = bundle.getString(DownloadService.FILEPATH);
//			  int resultCode = bundle.getInt(DownloadService.RESULT);
//			  
//			  if (resultCode == RESULT_OK) {
//			    Log.i(" launchpad ", " ");
//			  } else {
//				  Log.i("  launch ", "Download failed");
//			  }
//			}
//	    }
//	};        
}