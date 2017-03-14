package com.meshyog.emptycan.controller;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.AppConstants;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.meshyog.emptycan.R;
import com.meshyog.emptycan.model.AppUtils;
import com.meshyog.emptycan.model.database.CartInfo;
import com.meshyog.emptycan.model.database.EmptycanDataBase;
import com.meshyog.emptycan.model.server.RetrofitAdaptor;
import com.meshyog.emptycan.model.server.WebServiceInterface;
import com.meshyog.emptycan.view.AddressListAdapter;

import java.util.ArrayList;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

/**
 * Created by varadhan on 16-12-2016.
 */
public class AddressListActivity extends AppCompatActivity {
    ListView consumerAdrslstView=null;
    private ProgressDialog pDialog;
    public  JsonArray myAddresses=null;
    private LayoutInflater inflater;
    public AddressListAdapter addressListAdapter=null;
    public Activity addressLstActvty=null;
    public FloatingActionButton addAddressBtn;
    public String intentFrom;
    public  ArrayList<CartInfo>  cartInfoLst;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.consumer_address_list_activity);
        if( AppUtils.isNetworkAvailable(getApplicationContext())){
            if(getSupportActionBar()!=null) {
                getSupportActionBar().setTitle("My Addresses");
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            }
            Intent intent= getIntent();

            if(intent.getExtras()!=null){
                intentFrom= intent.getExtras().getString("intentfrom");
                cartInfoLst= (ArrayList<CartInfo>)intent.getSerializableExtra(AppConstants.CARTINFOLIST);
            }


            addressLstActvty=this;
            consumerAdrslstView=  (ListView) findViewById(R.id.consumerAddressLst);
            addAddressBtn= (FloatingActionButton)findViewById(R.id.addAddressBtn);
            addAddressBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent=new Intent(getApplicationContext(),AddressFormActivity.class);
                    startActivity(intent);
                }
            });
            myAddresses=new JsonArray();
           // addressListAdapter=new AddressListAdapter(this,myAddresses,intentFrom,cartInfoLst);
            //consumerAdrslstView.setAdapter(addressListAdapter);
            EmptycanDataBase emptycanDataBase =new EmptycanDataBase(getApplicationContext());

            pDialog = new ProgressDialog(this);
            // Showing progress dialog before making http request
            pDialog.setMessage("Loading...");
            pDialog.show();
            try{
                //JsonObject addConsumerAddress=new JsonObject()

                Retrofit retrofit =null;
                WebServiceInterface webServiceInterface=null;
                Call<JsonArray> call=null;

                retrofit = RetrofitAdaptor.getRetrofit();
                webServiceInterface = retrofit.create(WebServiceInterface.class);
                call = webServiceInterface.getConsumerAddress(emptycanDataBase.getConsumerKey());
                call.enqueue(new Callback<JsonArray>() {
                    @Override
                    public void onResponse(Response<JsonArray> response, Retrofit retrofit) {
                        if (response.message().equals("OK")) {
                            myAddresses= response.body().getAsJsonArray();
                            //myAddresses=result;
                            // Toast.makeText(getApplicationContext(),"Address Saved successfully",Toast.LENGTH_SHORT).show();

                            //   consumerAdrslstView.setAdapter(addressListAdapter);
                            if(intentFrom==null)
                                intentFrom="";
                            addressListAdapter=new AddressListAdapter(addressLstActvty,myAddresses,intentFrom,cartInfoLst);
                            consumerAdrslstView.setAdapter(addressListAdapter);
                            addressListAdapter.notifyDataSetChanged();




                        } else {
                            //request not successful (like 400,401,403 etc)
                            //Handle errors
                        }
                        hidePDialog();
                    }

                    @Override
                    public void onFailure(Throwable t) {
                        JsonObject jsonObject = new JsonObject();
                        jsonObject.addProperty("result_type", t.getMessage());
                        jsonObject.addProperty("result", t.getMessage());
                        Toast.makeText(getApplicationContext(),"Server Error..",Toast.LENGTH_SHORT).show();
                        hidePDialog();
                        //((SignupActivity)signUpContext).successNotice(jsonObject);
                        // SignupActivity signupActivityy = new SignupActivity();

                    }
                });
            }catch(Exception e){
                e.printStackTrace();
            }
        }else{
            mAlertDaiog("Check your internet connectivity", true);
        }

    }

    public void mAlertDaiog(String message, boolean isBack) {
        android.support.v7.app.AlertDialog.Builder alertDialogBuilder = new android.support.v7.app.AlertDialog.Builder(this);
        alertDialogBuilder.setMessage((CharSequence) message);
        alertDialogBuilder.setCancelable(false);
        alertDialogBuilder.setPositiveButton("OK", new BasicProfileDialogListner(isBack));
        alertDialogBuilder.create().show();
    }
    class BasicProfileDialogListner implements DialogInterface.OnClickListener {
        final /* synthetic */ boolean val$isBack;

        BasicProfileDialogListner(boolean z) {
            this.val$isBack = z;
        }

        public void onClick(DialogInterface arg0, int arg1) {
            if (this.val$isBack) {
                AddressListActivity.this.finish();
            }
        }
    }


    private void hidePDialog() {
        if (pDialog != null) {
            pDialog.dismiss();
            pDialog = null;
        }
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
        Intent startMain = new Intent(this,UserProfileActivity.class);
        //startMain.addCategory(Intent.CATEGORY_HOME);
        // startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(startMain);
        finish();
    }

}
