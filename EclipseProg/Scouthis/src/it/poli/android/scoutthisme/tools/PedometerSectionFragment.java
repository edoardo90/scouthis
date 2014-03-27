package it.poli.android.scoutthisme.tools;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.android.effectivenavigation.R;

    /**
     * A fragment that launches other parts of the demo application.
     */
    public  class PedometerSectionFragment extends Fragment {

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_section_pedometer, container, false);

            return rootView;
        }
    }