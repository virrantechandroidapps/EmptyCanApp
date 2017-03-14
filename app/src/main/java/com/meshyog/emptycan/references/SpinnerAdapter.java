package com.meshyog.emptycan.references;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.meshyog.emptycan.R;

import java.util.ArrayList;

/**
 * Created by Viswanathan on 21/10/16.
 */
public class SpinnerAdapter extends BaseAdapter implements android.widget.SpinnerAdapter {

    Context context;
    ArrayList<String> list;


    public SpinnerAdapter(Context context, ArrayList<String> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        TextView txt = new TextView(context);
        txt.setGravity(Gravity.CENTER);
        txt.setPadding(16, 16, 16, 16);
        txt.setTextSize(16);
        txt.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.mipmap.deliveryicon, 0);
        txt.setText(list.get(i));
        txt.setTextColor(Color.parseColor("#000000"));
        return  txt;
    }

    @Override
    public long getItemId(int i) {
        return (long)i;
    }

    @Override
    public Object getItem(int i) {
        return list.get(i);
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        TextView txt = new TextView(context);
        txt.setPadding(16, 16, 16, 16);
        txt.setTextSize(18);
        txt.setGravity(Gravity.CENTER_VERTICAL);
        txt.setText(list.get(position));
        txt.setTextColor(Color.parseColor("#000000"));
        return  txt;
    }
}
