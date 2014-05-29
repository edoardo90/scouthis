package it.poli.android.scouthisme;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;

public class DialogFloatActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_dialog_float);
		
	}

	public void loadMainTab(View v)
	{
		finish();
		
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.dialog_float, menu);
		return true;
	}

}
