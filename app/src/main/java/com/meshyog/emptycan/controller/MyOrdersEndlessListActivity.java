package com.meshyog.emptycan.controller;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.HeaderViewListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.meshyog.emptycan.R;
import com.meshyog.emptycan.model.database.EmptycanDataBase;
import com.meshyog.emptycan.model.server.RetrofitAdaptor;
import com.meshyog.emptycan.model.server.WebServiceInterface;
import com.meshyog.emptycan.view.MyOrderEndlessListView;
import com.meshyog.emptycan.view.MyOrdersListAdapter;

import cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

/**
 * Created by varadhan on 22-02-2017.
 */
public class MyOrdersEndlessListActivity extends AppCompatActivity implements  MyOrderEndlessListView.EndlessListener {
    public SweetAlertDialog progressDialog;
    public MyOrderEndlessListView myOrderEndlessListView;
    private final static int ITEM_PER_REQUEST = 10;
    private  static int ITEM_PER_RANGE = 0;
    public  JsonArray orderListJsonArray=new JsonArray();
    public MyOrdersListAdapter myOrdersListAdapter=null;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {super.onCreate(savedInstanceState);
        setContentView(R.layout.myorderlist);
        if(getSupportActionBar()!=null) {
            getSupportActionBar().setTitle("My Orders");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            myOrderEndlessListView=(MyOrderEndlessListView)findViewById(R.id.orderslist_list);
             myOrdersListAdapter=new MyOrdersListAdapter(this, orderListJsonArray,R.layout.settingslistview_item);
           /* myOrderEndlessListView.setLoadingView();
            myOrderEndlessListView.setAdapter();
            myOrderEndlessListView.setOnClickListener();*/
            progressDialog = new SweetAlertDialog(MyOrdersEndlessListActivity.this,SweetAlertDialog.PROGRESS_TYPE);
            // progressDialog.setProgressStyle(0);
            //progressDialog.setMessage("Loading...");
            progressDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
            progressDialog.setTitleText("Loading");
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();
            ITEM_PER_RANGE=0;
            retriveOrderList("YES",ITEM_PER_RANGE,ITEM_PER_REQUEST);
        }



    }
    public void retriveOrderList(final String init,int range1,int range2){
        Retrofit retrofit =null;
        WebServiceInterface webServiceInterface=null;
        Call<JsonArray> call=null;
        EmptycanDataBase emptycanDataBase =new EmptycanDataBase(getApplicationContext());
        try {
            retrofit = RetrofitAdaptor.getRetrofit();
            webServiceInterface = retrofit.create(WebServiceInterface.class);
            JsonObject jsonObject=new JsonObject();
            jsonObject.addProperty("consumerKey",emptycanDataBase.getConsumerKey());
            jsonObject.addProperty("range_from",range1);
            jsonObject.addProperty("range_to",range2);
            call=webServiceInterface.getMyOrders(jsonObject);
            call.enqueue(new Callback<JsonArray>() {
                @Override
                public void onResponse(Response<JsonArray> response, Retrofit retrofit) {
                    if (response.message().equals("OK")) {
                        if(response.body()!=null){
                            JsonArray result = response.body().getAsJsonArray();
                            ITEM_PER_RANGE+=result.size();
                            if(init.equals("YES"))
                            initOrderList(result,"firsttime");
                            else{
                                //orderListJsonArray=result;
                                myOrderEndlessListView.addNewData(orderListJsonArray);
                                initOrderList(result,"regular");

                            }

                            //System.out.println(result);
                        }


                    } else {
                        //request not successful (like 400,401,403 etc)
                        //Handle errors

                    }
                    progressDialog.dismiss();
                }

                @Override
                public void onFailure(Throwable t) {
                    JsonObject jsonObject = new JsonObject();
                    jsonObject.addProperty("result_type", t.getMessage());
                    jsonObject.addProperty("result", t.getMessage());
                    Toast.makeText(getApplicationContext(),"Internal server Error",Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();

                    //((SignupActivity)signUpContext).successNotice(jsonObject);
                    // SignupActivity signupActivityy = new SignupActivity();

                }
            });

        }catch(Exception e){
            e.printStackTrace();
        }
    }
    @Override
    public void loadData(int start) {
        retriveOrderList("USUAL",start,start+ITEM_PER_REQUEST);
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
    public void initOrderList(JsonArray result,String status){
        int size=result.size();
        for(int m=0;m<size;m++){
            orderListJsonArray.add(  result.get(m).getAsJsonObject());
        }
      //  this.orderListJsonArray=result;
        if(status.equals("firsttime")){
            myOrdersListAdapter=new MyOrdersListAdapter(this, orderListJsonArray,R.layout.settingslistview_item);
            myOrdersListAdapter.setNotifyOnChange(true);
            myOrderEndlessListView.setLoadingView(R.layout.orders_loading_layout);
            myOrderEndlessListView.setAdapter(myOrdersListAdapter);
            myOrderEndlessListView.setListener(this);
        }else if(status.equals("regular")){
            myOrdersListAdapter=new MyOrdersListAdapter(this, orderListJsonArray,R.layout.settingslistview_item);
            myOrderEndlessListView.setAdapter(myOrdersListAdapter);
            myOrdersListAdapter.notifyDataSetChanged();
          //  myOrderEndlessListView.smoothScrollToPosition(orderListJsonArray.size());
            //((MyOrdersListAdapter)((HeaderViewListAdapter)myOrderEndlessListView.getAdapter()).getWrappedAdapter()).notifyDataSetChanged();

        }

        ;

    }
}
