package it.poli.android.scouthisme;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

public class ProvaTimePicker extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_prova_time_picker);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.prova_time_picker, menu);
		return true;
	}

}
