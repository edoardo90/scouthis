package it.poli.android.scoutthisme.stepcounter.utils;

import it.poli.android.scouthisme.R;
import it.poli.android.scoutthisme.Constants;
import it.poli.android.scoutthisme.fragments.StepCounterHistoryFragment;
import it.poli.android.scoutthisme.tools.ImageLoader;
import it.poli.android.scoutthisme.tools.ImageToolz;

import java.util.LinkedList;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class StepCounterLazyAdapter extends BaseAdapter {

	private Activity activity;
	private LinkedList<RunEpisode> data;
	private static LayoutInflater inflater=null;
	public ImageLoader imageLoader; 
	public StepCounterHistoryFragment historyFragment;

	public StepCounterLazyAdapter(Activity a,  
			LinkedList<RunEpisode> runList, StepCounterHistoryFragment historyFragment) {
		this.activity = a;
		this.data=runList;
		StepCounterLazyAdapter.inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.imageLoader=new ImageLoader(activity.getApplicationContext());
		this.historyFragment = historyFragment;
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
			vi = inflater.inflate(R.layout.stepcounter_list_row, null);

		TextView distance = (TextView)vi.findViewById(R.id.lst_step_distance); 
		TextView speed = (TextView)vi.findViewById(R.id.lst_step_speed); 
		ImageView thumb_image=(ImageView)vi.findViewById(R.id.lst_step_image);
		ImageView trash_image = (ImageView)vi.findViewById(R.id.lst_step_trash);
		TextView runDate = (TextView)vi.findViewById(R.id.lst_step_date);

		RunEpisode runEspisode;
		if(data.size() > 0)
		{
			runEspisode = data.get(position);

			String fotoname = runEspisode.getFotoName();
			distance.setText(Html.fromHtml("<b>" + runEspisode.getStringDistance() + "</b>" + " m"));
			speed.setText( Html.fromHtml("<b>" + runEspisode.getStringSpeed() + "</b>"+ " km/h"));
			runDate.setText(runEspisode.getDateReadable());
			
			Bitmap bmp = ImageToolz.loadBitmapFromDisk(Constants.SD_IMAGE_DIR, fotoname);
			thumb_image.setImageBitmap(bmp);
			
		}
		
		trash_image.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				historyFragment.deleteRunEpisode(v);
				
			}
		});
		
		return vi;
	}

	
}