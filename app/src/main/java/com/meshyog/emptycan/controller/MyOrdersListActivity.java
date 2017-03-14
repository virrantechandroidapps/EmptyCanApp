package com.meshyog.emptycan.controller;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ListView;

import com.google.gson.JsonArray;
import com.meshyog.emptycan.model.EndlessListener;
import com.meshyog.emptycan.view.MyOrdersListAdapter;

import java.util.ArrayList;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * Created by varadhan on 19-02-2017.
 */
public class MyOrdersListActivity  extends ListView  implements AbsListView.OnScrollListener {

    public SweetAlertDialog progressDialog;
    private View footer;
    private boolean isLoading;
    private EndlessListener listener;
    private MyOrdersListAdapter adapter;
    public MyOrdersListActivity(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.setOnScrollListener(this);
    }

    public MyOrdersListActivity(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.setOnScrollListener(this);
    }

    public MyOrdersListActivity(Context context) {
        super(context);
        this.setOnScrollListener(this);
    }
    public void setListener(EndlessListener listener) {
        this.listener = listener;
    }
    @Override
    public void onScrollStateChanged(AbsListView absListView, int i) {

    }
    public EndlessListener setListener() {
        return listener;
    }
    public void addNewData(List<String> data) {

        this.removeFooterView(footer);

        adapter.addAll(new JsonArray());
        adapter.notifyDataSetChanged();
        isLoading = false;
    }
    @Override
    public void onScroll(AbsListView absListView, int i, int i1, int i2) {

    }
    public void setLoadingView(int resId) {
        LayoutInflater inflater = (LayoutInflater) super.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        footer = (View) inflater.inflate(resId, null);
        this.addFooterView(footer);

    }
    public void setAdapter(MyOrdersListAdapter adapter) {
        // super.setAdapter(adapter);
        this.adapter = adapter;
        this.removeFooterView(footer);
    }
  /*  protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.myorderlist);
        if(getSupportActionBar()!=null) {
            getSupportActionBar().setTitle("My Orders");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            progressDialog = new SweetAlertDialog(MyOrdersListActivity.this,SweetAlertDialog.PROGRESS_TYPE);
            // progressDialog.setProgressStyle(0);
            //progressDialog.setMessage("Loading...");
            progressDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
            progressDialog.setTitleText("Loading");
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();
            retriveOrderList();
        }



    }*/

    /*public void retriveOrderList(){
        Retrofit retrofit =null;
        WebServiceInterface webServiceInterface=null;
        Call<JsonArray> call=null;
        EmptycanDataBase emptycanDataBase =new EmptycanDataBase(getContext());
        try {
            retrofit = RetrofitAdaptor.getRetrofit();
            webServiceInterface = retrofit.create(WebServiceInterface.class);
            call=webServiceInterface.getMyOrders(emptycanDataBase.getConsumerKey());
            call.enqueue(new Callback<JsonArray>() {
                @Override
                public void onResponse(Response<JsonArray> response, Retrofit retrofit) {
                    if (response.message().equals("OK")) {
                        if(response.body()!=null){
                            JsonArray result = response.body().getAsJsonArray();
                            MyOrdersListAdapter adapter=new MyOrdersListAdapter(getContext(),result);
                            ListView listView=(ListView)findViewById(R.id.orderslist_list);
                            listView.setAdapter(adapter);
                            adapter.notifyDataSetChanged();
                            System.out.println(result);
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
                    Toast.makeText(getContext(),"Internal server Error",Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();

                    //((SignupActivity)signUpContext).successNotice(jsonObject);
                    // SignupActivity signupActivityy = new SignupActivity();

                }
            });

        }catch(Exception e){
                e.printStackTrace();
            }
    }*/
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

   /* @Override
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
    }*/






}
