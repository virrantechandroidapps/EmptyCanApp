package com.meshyog.emptycan.references;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.meshyog.emptycan.R;
import com.meshyog.emptycan.model.database.NotificationInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Viswanathan on 26/10/16.
 */
public class NotificationAdapter extends BaseAdapter {

    Context context;
    ArrayList<NotificationInfo> list;

    public NotificationAdapter(Context context, ArrayList<NotificationInfo> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int i) {
        return list.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder=null;
        if (view == null) {

                view = LayoutInflater.from(context).inflate(R.layout.message_from_seller_layout, viewGroup, false);
            viewHolder = new ViewHolder();
            viewHolder.image = (ImageView) view.findViewById(R.id.notification_image);
            viewHolder.title = (TextView) view.findViewById(R.id.item_title);
            viewHolder.message1 = (TextView) view.findViewById(R.id.item_subtitle);
            viewHolder.message2 = (TextView) view.findViewById(R.id.item_subtitle2);
                view.setTag(viewHolder);

        } else {
            viewHolder=(ViewHolder) view.getTag();

        }
        viewHolder.image.setImageResource(list.get(i).getNotificationImgRsrc());
        viewHolder.title.setText(list.get(i).getNotificationTitle());
        viewHolder.message2.setText(list.get(i).getNotificationBody());
        if(list.get(i).getNotificationType()==1){

                viewHolder.message1.setVisibility(View.GONE);
        }else if(list.get(i).getNotificationType()==2){
                viewHolder.message1.setVisibility(View.GONE);
        }else if(list.get(i).getNotificationType()==3){
                viewHolder.title.setText(list.get(i).getNotificationTitle());
                viewHolder.message1.setText(list.get(i).getNotificationBody());
                viewHolder.message1.setVisibility(View.VISIBLE);
                viewHolder.message2.setText(list.get(i).getNotificationBody());
        }

        return view;
    }


    public class ViewHolder {
        TextView title;
        TextView message1;
        TextView message2;
        ImageView image;
    }
}
