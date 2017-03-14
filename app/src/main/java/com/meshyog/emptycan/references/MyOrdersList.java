package com.meshyog.emptycan.references;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import com.meshyog.emptycan.R;
import com.meshyog.emptycan.view.MyOrdersListAdapter;

import java.util.ArrayList;

import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * Created by Viswanathan on 12/10/16.
 */
public class MyOrdersList extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.myorderlist);
        if(getSupportActionBar()!=null) {
            getSupportActionBar().setTitle("My Orders");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        }

        /*MyOrdersListAdapter adapter=new MyOrdersListAdapter(this,getDatas());
        ListView listView=(ListView)findViewById(R.id.orderslist_list);
        listView.setAdapter(adapter);*/

    }

    public ArrayList<String> getDatas(){
        ArrayList<String> list=new ArrayList<>();
        list.add("Mon,Feb 13,08:24 PM");
        list.add("Mon,Feb 13,08:24 PM");
        list.add("Mon,Feb 13,08:24 PM");
        list.add("Mon,Feb 13,08:24 PM");
        list.add("Mon,Feb 13,08:24 PM");
        list.add("Mon,Feb 13,08:24 PM");
        return list;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.my_orders_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id ==android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

}
