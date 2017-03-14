package com.meshyog.emptycan.view;


import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.meshyog.emptycan.R;
import com.meshyog.emptycan.model.AppConfig;
import com.meshyog.emptycan.model.database.NotificationInfo;

/**
 * Created by varadhan on 02-12-2016.
 */
public class NotificationCustomListAdapter extends BaseAdapter {
    private Activity activity;
    private LayoutInflater inflater;
    private List<NotificationInfo> notificationListItems;
    ImageLoader imageLoader = AppConfig.getInstance().getImageLoader();

    public NotificationCustomListAdapter(Activity activity, List<NotificationInfo> notificationMovies) {
        this.activity = activity;
        this.notificationListItems = notificationMovies;
    }

    @Override
    public int getCount() {
        return notificationListItems.size();
    }

    @Override
    public Object getItem(int location) {
        return notificationListItems.get(location);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (inflater == null)
            inflater = (LayoutInflater) activity
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null)
            convertView = inflater.inflate(R.layout.notification_row_layout, null);

        if (imageLoader == null)
            imageLoader = AppConfig.getInstance().getImageLoader();
        NetworkImageView thumbNail = (NetworkImageView) convertView
                .findViewById(R.id.thumbnail);
        TextView title = (TextView) convertView.findViewById(R.id.title);
        TextView rating = (TextView) convertView.findViewById(R.id.rating);
        TextView genre = (TextView) convertView.findViewById(R.id.genre);
        TextView year = (TextView) convertView.findViewById(R.id.releaseYear);

        // getting movie data for the row
        NotificationInfo notificationInfo = notificationListItems.get(position);

        // thumbnail image
        thumbNail.setImageUrl(notificationInfo.getNotificationImage(), imageLoader);
        if(!notificationInfo.isRead())
            convertView.setBackgroundColor(activity.getResources().getColor(R.color.unreadColor));
        else
            convertView.setBackgroundColor(activity.getResources().getColor(R.color.readColor));
       // #e9ebee
        // title
        title.setText(notificationInfo.getNotificationTitle());

        // rating
        rating.setText(notificationInfo.getNotificationBody());

        // genre
        String genreStr = "Water Can";
        /*for (String str : notificationInfo.getGenre()) {
            genreStr += str + ", ";
        }*/
        genreStr = genreStr.length() > 0 ? genreStr.substring(0,
                genreStr.length() - 2) : genreStr;
        //genre.setText(genreStr);

        // release year
        year.setText(String.valueOf(notificationInfo.getNotificationRcvdDate()));

        return convertView;
    }

}