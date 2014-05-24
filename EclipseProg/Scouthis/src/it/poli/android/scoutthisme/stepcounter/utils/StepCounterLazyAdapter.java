package it.poli.android.scoutthisme.stepcounter.utils;

import it.poli.android.scouthisme.R;
import it.poli.android.scoutthisme.tools.ImageLoader;
import it.poli.android.scoutthisme.tools.ImageToolz;

import java.util.LinkedList;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class StepCounterLazyAdapter extends BaseAdapter {

	private Activity activity;
	private LinkedList<RunEpisode> data;
	private static LayoutInflater inflater=null;
	public ImageLoader imageLoader; 

	public StepCounterLazyAdapter(Activity a,  
			LinkedList<RunEpisode> runList) {
		activity = a;
		data=runList;
		inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		imageLoader=new ImageLoader(activity.getApplicationContext());
	}

	public int getCount() {
		return data.size();
	}

	public Object getItem(int position) {
		return position;
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		View vi=convertView;
		if(convertView == null)
			vi = inflater.inflate(R.layout.alarms_list_row, null);

		TextView distance = (TextView)vi.findViewById(R.id.days); 
		TextView speed = (TextView)vi.findViewById(R.id.hour); 
		ImageView thumb_image=(ImageView)vi.findViewById(R.id.list_image); 
		

		RunEpisode runEspisode;
		if(data.size() > 0)
		{
			runEspisode = data.get(position);

			String fotoname = runEspisode.getFotoName();
			distance.setText(runEspisode.getStringDistance());
			speed.setText( runEspisode.getStringSpeed() );
			
			Bitmap bmp = ImageToolz.loadBitmapFromDisk(fotoname);
			thumb_image.setImageBitmap(bmp);

		}
		return vi;
	}

	
}