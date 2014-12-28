package com.wordpress.priyankvex.skiffle;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by priyank on 24/12/14.
 */
public class NavigationListAdapter extends BaseAdapter{
    private Context context;
    ArrayList<String> navDrawerItems;

    public NavigationListAdapter(Context context, ArrayList<String> navDrawerItems){
        this.context = context;
        this.navDrawerItems = navDrawerItems;
    }

    @Override
    public int getCount() {
        return navDrawerItems.size();
    }

    @Override
    public Object getItem(int position) {
        return navDrawerItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater mInflater = (LayoutInflater)
                    context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            convertView = mInflater.inflate(R.layout.drawer_list_item, null);
        }

        ImageView imgIcon = (ImageView) convertView.findViewById(R.id.icon);
        TextView txtTitle = (TextView) convertView.findViewById(R.id.title);

        if (position == 0){
            imgIcon.setImageResource(R.drawable.ic_menu_home);
        }
        else if (position == 1){
            imgIcon.setImageResource(R.drawable.ic_menu_search_holo_light);
        }
        else if (position == 4){
            imgIcon.setImageResource(R.drawable.btn_rating_star_off_normal_holo_light);
        }
        else if (position == 5){
            imgIcon.setImageResource(R.drawable.ic_settings);
        }
        else{
            imgIcon.setImageResource(R.drawable.ic_media_play);
        }
        txtTitle.setText(navDrawerItems.get(position));

        return convertView;
    }
}
