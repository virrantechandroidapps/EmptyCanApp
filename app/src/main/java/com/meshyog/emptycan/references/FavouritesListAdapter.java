package com.meshyog.emptycan.references;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.meshyog.emptycan.R;

import java.util.ArrayList;

/**
 * Created by Viswanathan on 12/10/16.
 */
public class FavouritesListAdapter extends BaseAdapter {

    Context context;
    ArrayList<String> list;

    public FavouritesListAdapter(Context context, ArrayList<String> list) {
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
        ViewHolder viewHolder;
        if(view==null){
            view= LayoutInflater.from(context).inflate(R.layout.favouriteslist_item,viewGroup,false);
            viewHolder=new ViewHolder();
            viewHolder.date=(TextView)view.findViewById(R.id.favouriteslist_item_name);
            view.setTag(viewHolder);
        }else{
            viewHolder=(ViewHolder)view.getTag();
        }
        viewHolder.date.setText(list.get(i));
        return view;
    }

    private class ViewHolder{
        TextView date;
    }
}
