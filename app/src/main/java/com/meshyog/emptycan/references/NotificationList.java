package com.meshyog.emptycan.references;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import com.meshyog.emptycan.R;
import com.meshyog.emptycan.model.database.EmptycanDataBase;

import java.util.ArrayList;

/**
 * Created by Viswanathan on 21/10/16.
 */
public class NotificationList extends AppCompatActivity {


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notification_list);
        ListView listView=(ListView)findViewById(R.id.listview_notification);
        EmptycanDataBase emptyCanDataBse=new EmptycanDataBase(getApplicationContext());
        NotificationAdapter adapter=new NotificationAdapter(this,emptyCanDataBse.getNotificationList());
        listView.setAdapter(adapter);
        if(getSupportActionBar()!=null) {
            getSupportActionBar().setTitle("Notifications");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

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

    public ArrayList<NotificationObject> getList(){
        ArrayList<NotificationObject> data=new ArrayList<>();
        NotificationObject no1=new NotificationObject();
        no1.setType(1);
        no1.setImageResource(R.mipmap.ic_add_alert_black_24dp);
        no1.setTitle("20% offer");
        no1.setMessage("Huge discount on 75kg cans. Order now.");
        NotificationObject no2=new NotificationObject();
        no2.setType(1);
        no2.setTitle("20% offer");
        no2.setImageResource(R.mipmap.ic_add_alert_black_24dp);
        no2.setMessage("Huge discount on 25kg cans. Order now.");
        NotificationObject no3=new NotificationObject();
        no3.setType(1);
        no3.setTitle("20% offer");
        no3.setImageResource(R.mipmap.ic_add_alert_black_24dp);
        no3.setMessage("Huge discount on 25kg cans above 3 cans. Order now.");
        NotificationObject no4=new NotificationObject();
        no4.setType(2);
        no4.setTitle("Order request");
        no4.setMessage("Order request was sent seller.Your oders are 25 kg *3 no's");
        no4.setImageResource(R.mipmap.ic_person_black_24dp);
        NotificationObject no5=new NotificationObject();
        no5.setType(2);
        no5.setTitle("Order request");
        no5.setImageResource(R.mipmap.ic_person_black_24dp);
        no5.setMessage("Order request was sent seller.Your oders are 25 kg *8 no's");
        NotificationObject no6=new NotificationObject();
        no6.setType(2);
        no6.setTitle("Order request");
        no6.setImageResource(R.mipmap.ic_person_black_24dp);
        no6.setMessage("Order request was sent seller.Your oders are 25 kg *1 no's");
        NotificationObject no7=new NotificationObject();
        no7.setType(3);
        no7.setTitle("Order Confirmation");
        no7.setSubTitle("Delivery date:27/10/2106");
        no7.setImageResource(R.mipmap.ic_check_black_48dp);
        no7.setMessage("Order request was sent seller.Your oders are 25 kg *1 no's");
        NotificationObject no8=new NotificationObject();
        no8.setType(3);
        no8.setTitle("Order Confirmation");
        no8.setSubTitle("Delivery date:28/10/2106");
        no8.setImageResource(R.mipmap.ic_check_black_48dp);
        no8.setMessage("Order request was sent seller.Your oders are 25 kg *1 no's");
        NotificationObject no9=new NotificationObject();
        no9.setType(3);
        no9.setTitle("Order Confirmation");
        no9.setSubTitle("Delivery date:30/10/2106");
        no9.setImageResource(R.mipmap.ic_check_black_48dp);
        no9.setMessage("Order request was sent seller.Your oders are 25 kg *1 no's");
        data.add(no1);
        data.add(no9);
        data.add(no4);
        data.add(no8);
        data.add(no6);
        data.add(no3);
        data.add(no2);
        data.add(no9);
        data.add(no5);
        data.add(no7);
        return data;
    }
}
