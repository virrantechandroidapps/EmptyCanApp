package com.meshyog.emptycan.controller;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.AppConstants;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.meshyog.emptycan.R;
import com.meshyog.emptycan.model.database.CartInfo;
import com.meshyog.emptycan.model.database.EmptycanDataBase;
import com.meshyog.emptycan.model.server.RetrofitAdaptor;
import com.meshyog.emptycan.model.server.WebServiceInterface;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Locale;

import cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

/**
 * Created by varadhan on 17-02-2017.
 */
public class MyOrderDetailsActivity extends AppCompatActivity {

    TableLayout orderDetailList;
    public SweetAlertDialog progressDialog;
    public static long orderKey=0l;
    SweetAlertDialog orderCancleConfirmationDialg =null;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.order_detail);
        try{
            if(getSupportActionBar()!=null) {
                //getSupportActionBar().setTitle("Review Order ");
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            }
            Intent myOrderIntent=  getIntent();
            String orderMetaData=(String) myOrderIntent.getExtras().get(AppConstants.ORDERMETADATA);
            JSONObject orderJsonObj= new JSONObject(orderMetaData);
            if(orderJsonObj!=null){

               String bookingDate= orderJsonObj.getString("booking_date");
                orderKey=orderJsonObj.getLong("order_key");
                String orderId=orderJsonObj.getString("order_id");
                double toatalPayment=orderJsonObj.getDouble("total_amount");
                TextView payableAmntTxtView= (TextView) findViewById(R.id.payableAmnt);
                payableAmntTxtView.setText(String.valueOf(toatalPayment));
                Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
                TextView headerBookingDate=(TextView)findViewById(R.id.bookingDate);
                TextView headerOrderId=(TextView)findViewById(R.id.orderId);
                TextView goBack=(TextView)findViewById(R.id.goBack);
                TextView cancelOrder=(TextView)findViewById(R.id.cancelOrder);
                goBack.setOnClickListener(listener);
                cancelOrder.setOnClickListener(listener);
                headerBookingDate.setText(bookingDate);
                headerOrderId.setText(orderId);

                //toolbar.addView(view);*/
               // setSupportActionBar(toolbar);
            }


            orderDetailList = (TableLayout) findViewById(R.id.tableLayout);
            addListToView(new JSONArray(orderJsonObj.get("cart_list").toString()),orderDetailList );
        }catch(Exception e){
            e.printStackTrace();
        }


    }
    private View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.goBack:
                  onBackPressed();
                    break;

                case R.id.cancelOrder:
                    showConfirmationMessage();
                    break;

            }
        }
    };
    public void showConfirmationMessage(){
        try{
            orderCancleConfirmationDialg =  new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE);
            orderCancleConfirmationDialg.setTitleText("Are you sure?");
            orderCancleConfirmationDialg.setContentText("please choose your  confirmation");
            orderCancleConfirmationDialg.setCancelText("Abort");
            orderCancleConfirmationDialg.setConfirmText("Yes, Cancel the Order !");
            orderCancleConfirmationDialg.showCancelButton(true);
            orderCancleConfirmationDialg.setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                @Override
                public void onClick(SweetAlertDialog sDialog) {
                    // reuse previous dialog instance, keep widget user state, reset them if you need
                    sDialog.setTitleText("Aborted")
                            .setContentText("Order Cancel Process Aborted :)")
                            .setConfirmText("OK")
                            .showCancelButton(false)
                            .setCancelClickListener(null)
                            .setConfirmClickListener(null)
                            .changeAlertType(SweetAlertDialog.ERROR_TYPE);

                    // or you can new a SweetAlertDialog to show
                               /* sDialog.dismiss();
                                new SweetAlertDialog(SampleActivity.this, SweetAlertDialog.ERROR_TYPE)
                                        .setTitleText("Cancelled!")
                                        .setContentText("Your imaginary file is safe :)")
                                        .setConfirmText("OK")
                                        .show();*/
                }
            });
            orderCancleConfirmationDialg.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                @Override
                public void onClick(SweetAlertDialog sDialog) {


                    cancelBookingOrder(orderKey);


                }
            }) .show();
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    public void cancelBookingOrder(long orderId){
        progressDialog = new SweetAlertDialog(this,SweetAlertDialog.PROGRESS_TYPE);
        // progressDialog.setProgressStyle(0);
        //progressDialog.setMessage("Loading...");
        progressDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        progressDialog.setTitleText("Loading");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
        Retrofit retrofit =null;
        WebServiceInterface webServiceInterface=null;
        Call<JsonObject> call=null;
       /* progressDialog = new SweetAlertDialog(this,SweetAlertDialog.PROGRESS_TYPE);

        progressDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        progressDialog.setTitleText("Loading");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();*/
        try{
            retrofit = RetrofitAdaptor.getRetrofit();
            webServiceInterface = retrofit.create(WebServiceInterface.class);
            call = webServiceInterface.cancelOrder(orderId, EmptycanDataBase.ORDER_CANCELLED_BY_CUS);
            call.enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(Response<JsonObject> response, Retrofit retrofit)    {
                    try{
                        if (response.message().equals("OK")) {
                          if(  response.body().getAsJsonObject().get("status").getAsString().equals("success")){
                              progressDialog.dismiss();
                              launchOrderListActivity();
                          }

                        } else {

                        }
                    }catch (Exception e){

                    }finally {
                        progressDialog.dismiss();
                        orderCancleConfirmationDialg.dismiss();
                    }


                }
                @Override
                public void onFailure(Throwable t) {
                    Toast.makeText(getApplicationContext(),""+t.getMessage(),Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                    orderCancleConfirmationDialg.dismiss();

                }
            });
        }catch(Exception e){
            e.printStackTrace();
            progressDialog.dismiss();
            orderCancleConfirmationDialg.dismiss();
        }


    }

    public void launchOrderListActivity(){
    SweetAlertDialog progressDialog = new SweetAlertDialog(MyOrderDetailsActivity.this, SweetAlertDialog.WARNING_TYPE);
        // progressDialog.setProgressStyle(0);
        //progressDialog.setMessage("Loading...");
        progressDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));

        progressDialog.setTitleText("Your order Cancelled Successfully");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
        progressDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(SweetAlertDialog sDialog) {
                sDialog.dismiss();
                launchOrderDetailsActivity();

            }
        });
    }
    public void launchOrderDetailsActivity(){
        Intent orderDetailActivity = new Intent(this,MyOrdersEndlessListActivity.class);
        orderDetailActivity.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(orderDetailActivity);
    }
    public void addListToView(JSONArray cartArray, TableLayout layout){
        try{
            LayoutInflater inflater=LayoutInflater.from(this);
            int cartLength=cartArray.length();
            for(int i=0;i<cartLength;i++){
                JSONObject jsonObject=  cartArray.getJSONObject(i);
                View view=inflater.inflate(R.layout.order_detail_list_item,null);
                TextView name=(TextView)view.findViewById(R.id.order_detail_list_item_name);
                TextView unit=(TextView)view.findViewById(R.id.order_detail_list_item_unit);
                TextView cost=(TextView)view.findViewById(R.id.order_detail_list_item_cost);
                name.setText(jsonObject.getString("cartPrdctName"));
                unit.setText(String.format(Locale.getDefault(),"%d",jsonObject.getInt("cartPrdctUnit")));
                cost.setText(String.valueOf(jsonObject.getDouble("cartPrdctPrice")));
                layout.addView(view,i+1);
            }
        }catch(Exception e){
e.printStackTrace();
        }

    }

    public ArrayList<ItemClass> getList(){
        ArrayList<ItemClass> list=new ArrayList<>();
        ItemClass item1=new ItemClass(5,"Name 1",100);
        ItemClass item2=new ItemClass(1,"Name 1",20);
        ItemClass item3=new ItemClass(3,"Name 1",60);
        ItemClass item4=new ItemClass(2,"Name 1",40);
        list.add(item1);
        list.add(item2);
        list.add(item3);
        list.add(item4);
        return list;
    }


    public class ItemClass{
        private String name;
        private int unit;
        private int cost;

        public ItemClass(int cost, String name, int unit) {
            this.cost = cost;
            this.name = name;
            this.unit = unit;
        }

        public int getCost() {
            return cost;
        }

        public String getName() {
            return name;
        }
        public int getUnit() {
            return unit;
        }
    }
}
