package com.meshyog.emptycan.controller;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Movie;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import com.AppConstants;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.meshyog.emptycan.R;
import com.meshyog.emptycan.model.AppConfig;
import com.meshyog.emptycan.model.AppUtils;
import com.meshyog.emptycan.model.NotificationUtills;
import com.meshyog.emptycan.model.database.EmptycanDataBase;
import com.meshyog.emptycan.model.database.NotificationInfo;
import com.meshyog.emptycan.references.NotificationAdapter;
import com.meshyog.emptycan.references.NotificationList;
import com.meshyog.emptycan.references.NotificationObject;
import com.meshyog.emptycan.view.NotificationCustomListAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by varadhan on 29-11-2016.
 */
public class NotificationListActivity  extends AppCompatActivity{
    protected AppConfig appConfig;
    private static final String TAG = MainActivity.class.getSimpleName();
    private SharedPreferences sharedPreferences;
    // Movies json url
  //  private static final String url = "http://"+AppConstants.baseContext+"/rest/consumer-notifications/5260063627280384";
    private ProgressDialog pDialog;
    private List<NotificationInfo> notificationInfoList = new ArrayList<NotificationInfo>();
    private ListView listView;
    private NotificationCustomListAdapter adapter;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notification_list_duplicate);
        try{
            if(getSupportActionBar()!=null) {
                getSupportActionBar().setTitle("Notifications");
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            }
            sharedPreferences= PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            appConfig = (AppConfig)this.getApplicationContext();
            ListView listView=(ListView)findViewById(R.id.list);
            EmptycanDataBase emptyCanDataBse=new EmptycanDataBase(getApplicationContext());
            //notificationInfoList= emptyCanDataBse.getNotificationList();
            if(notificationInfoList==null)
                notificationInfoList=new ArrayList<NotificationInfo>();
            adapter = new NotificationCustomListAdapter(this, notificationInfoList);
            listView.setAdapter(adapter);
            pDialog = new ProgressDialog(this);
            // Showing progress dialog before making http request
            pDialog.setMessage("Loading...");
            pDialog.show();
            //hidePDialog();
            String notificationArrStr= this.sharedPreferences.getString(EmptycanDataBase.RECENT_NOTIFICATION_IDS,"");

            if(!notificationArrStr.equals("")){
                JSONArray allNotifications=new JSONArray(notificationArrStr);
                int size=allNotifications.length();
                for(int i=0;i<size;i++){
                 NotificationUtills.cancelNotification(getApplicationContext(),(int)  allNotifications.get(i));
                }
                if(size!=0){
                    SharedPreferences.Editor editor= this.sharedPreferences.edit();
                    editor.putString(EmptycanDataBase.RECENT_NOTIFICATION_IDS,"");
                    editor.commit();
                }
            }
            JsonArrayRequest notificationReq = new JsonArrayRequest(appConfig.getNotificationUrl(),
                    new Response.Listener<JSONArray>() {
                        @Override
                        public void onResponse(JSONArray response) {
                            Log.d(TAG, response.toString());
                            hidePDialog();

                            // Parsing json
                            for (int i = 0; i < response.length(); i++) {
                                try {

                                    JSONObject obj = response.getJSONObject(i);

                                    NotificationInfo notification = new NotificationInfo();
                                    notification.setNotificationTitle(obj.getString("title"));
                                    notification.setNotificationBody(obj.getString("body"));
                                    notification.setNotificationImage(obj.getString("notificationImageUrl"));
                                    String date=obj.getString("notificationSentDateTime");
                                    notification.setNotificationRcvdDate(date);
                                    notification.setNotificationSendDate(AppUtils.convertStringDateToUtil(date,"yyyy-MM-dd HH:mm:ss"));
                                    String readStats=obj.getString("notificationReadStatus");
                                    if(readStats.equals("unread"))
                                        notification.setRead(false);
                                    else if(readStats.equals("read"))
                                        notification.setRead(true);
                                    notificationInfoList.add(notification);
                                    Collections.sort(notificationInfoList,Collections.reverseOrder());

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                            }

                            // notifying list adapter about data changes
                            // so that it renders the list view with updated data
                            adapter.notifyDataSetChanged();
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    VolleyLog.d(TAG, "Error: " + error.getMessage());
                    hidePDialog();

                }
            });
            AppConfig.getInstance().addToRequestQueue(notificationReq);
        }catch(Exception e){
            e.printStackTrace();

        }

     /*   getActionBar().setBackgroundDrawable(
                new ColorDrawable(Color.parseColor("#1b1b1b")));*/
        //emptyCanDataBse.deleteOldNotifications();
       /* NotificationAdapter adapter=new NotificationAdapter(this,emptyCanDataBse.getNotificationList());
        listView.setAdapter(adapter);
        if(getSupportActionBar()!=null) {
            getSupportActionBar().setTitle("Notifications");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }*/
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
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        //startActivity(new Intent(NotificationListActivity.this, BookCanFormActivity.class));
        Intent startMain = new Intent(this,BookCanFormActivity.class);
        //startMain.addCategory(Intent.CATEGORY_HOME);
       // startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(startMain);
        finish();
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
    private void clearReferences(){
        Activity currActivity = appConfig.getCurrentActivity();
        if (this.equals(currActivity))
            appConfig.setCurrentActivity(null);
    }
    @Override
    protected void onResume() {
        appConfig.setCurrentActivity(this);
        super.onResume();

    }
    @Override
    protected void onPause() {
        clearReferences();
        super.onPause();
    }
    @Override
    protected void onDestroy() {
        clearReferences();
        super.onDestroy();
    }
    private void hidePDialog() {
        if (pDialog != null) {
            pDialog.dismiss();
            pDialog = null;
        }
    }
}
