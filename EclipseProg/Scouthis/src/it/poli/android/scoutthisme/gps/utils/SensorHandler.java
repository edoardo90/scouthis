package it.poli.android.scoutthisme.gps.utils;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v4.app.Fragment;

public class SensorHandler implements SensorEventListener
{	
	Activity mActivity;
	SensorListener listener;
	SensorManager mSensorManager;
	Sensor senAccelerometer;
    
    public SensorHandler (Fragment f) {
    	this.mActivity = f.getActivity();
		mSensorManager = (SensorManager) mActivity.getSystemService(Context.SENSOR_SERVICE); // Initialize your android device sensor capabilities
		senAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
    }

    @SuppressWarnings("deprecation")
	public void setListener(SensorListener l) {
    	this.listener = l;
		mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION),
				SensorManager.SENSOR_DELAY_GAME);
		mSensorManager.registerListener(this, senAccelerometer , SensorManager.SENSOR_DELAY_UI);
    }
    
    public void removeListener(GpsListener l) {
    	mSensorManager.unregisterListener(this);
    	this.listener = null;
    }
	
	@Override
	public void onSensorChanged(SensorEvent event) {
		if (listener != null) {
			listener.onSensorChanged(event);
		}
	}
	
	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) { }
}