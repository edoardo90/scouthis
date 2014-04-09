package com.example.androidhive;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class LazyAdapter extends BaseAdapter {
    
    private Activity activity;
    private ArrayList<HashMap<String, String>> data;
    private static LayoutInflater inflater=null;
    public ImageLoader imageLoader; 
    
    public LazyAdapter(Activity a, ArrayList<HashMap<String, String>> d) {
        activity = a;
        data=d;
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
        if(convertView==null)
            vi = inflater.inflate(R.layout.list_row, null);

        TextView days = (TextView)vi.findViewById(R.id.title); // title
        TextView hour = (TextView)vi.findViewById(R.id.artist); // artist name
        ImageView thumb_image=(ImageView)vi.findViewById(R.id.list_image); // thumb image
        
        HashMap<String, String> song = new HashMap<String, String>();
        song = data.get(position);
        
        // Setting all values in listview
        days.setText(song.get(CustomizedListView.KEY_DAYS));
        hour.setText(song.get(CustomizedListView.KEY_HOUR));
        String bird = song.get(CustomizedListView.KEY_BIRD);
        
        bird = bird.replaceAll("\\s","");
        Log.i(" lazy ad ", "bird: " + bird);
        
        int resID = activity.getResources().getIdentifier(bird, "drawable", activity.getPackageName());
        thumb_image.setImageResource(resID);
        
        return vi;
    }
    
    
}