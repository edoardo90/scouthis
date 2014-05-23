package it.poli.android.scoutthisme.social;

import it.poli.android.scoutthisme.alarm.utils.RoundedImageView;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Marker;

public class ImageLoader extends AsyncTask<String, Integer, Bitmap>{

	Activity activity;
	Marker marker;
	public ImageLoader(Activity activity, Marker marker)
	{
		this.activity = activity;
		this.marker = marker;
	}
	@Override
	protected Bitmap doInBackground(String... urls) {

		URL imgUrl = null;
		InputStream in = null;
		try {
			imgUrl = new URL(urls[0].replace("http", "https"));
			
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		try {
			in = (InputStream) imgUrl.getContent();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		Bitmap  bitmap = BitmapFactory.decodeStream(in);
		
		try {
			if(in!=null)
				in.close();
				
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return bitmap;
	
	}

	@Override
	protected void onPostExecute(Bitmap bitmap)
	{
		RoundedImageView rv = new RoundedImageView(this.activity.getApplicationContext());
		bitmap = rv.getCroppedBitmap(bitmap, 80);
		marker.setIcon(BitmapDescriptorFactory.fromBitmap(bitmap));
		marker.setAnchor(0.5f, 1);
		
	}
	
	
}
