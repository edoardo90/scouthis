package it.poli.android.scoutthisme.tools;

import it.poli.android.scouthisme.R;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class RSSHomeAdapter extends ArrayAdapter<RSSItem>
{
        private final Context context;
        private final ArrayList<RSSItem> itemsList;
 
        public RSSHomeAdapter(Context context, ArrayList<RSSItem> itemsTitlesList)
        {
            super(context, R.layout.newsfeed_list_row, itemsTitlesList);
            this.context = context;
            this.itemsList = itemsTitlesList;
        }
 
        @Override
        public View getView(int position, View convertView, ViewGroup parent)
        {
            // 1. Create inflater
            LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
 
            // 2. Get rowView from inflater
            View rowView = inflater.inflate(R.layout.newsfeed_list_row, parent, false);

            // 3. Get the two text view from the rowView
            TextView labelView = (TextView) rowView.findViewById(R.id.lblRSSItem);
            TextView descView = (TextView) rowView.findViewById(R.id.dscRSSItem);
            //ImageView imageView = (ImageView) rowView.findViewById(R.id.imgRow);
 
            // 4. Set the text for textView
            labelView.setText(itemsList.get(position).getTitle());
            descView.setText(itemsList.get(position).getDescription());
            //imageView.setImageBitmap(itemsList.get(position).getImage());     
 
            // 5. return rowView
            return rowView;
        }
}