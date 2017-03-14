package com.meshyog.emptycan.controller;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.AppConstants;
import com.google.gson.JsonObject;
import com.meshyog.emptycan.R;
import com.meshyog.emptycan.model.AppConfig;
import com.meshyog.emptycan.model.AppUtils;
import com.meshyog.emptycan.model.database.EmptycanDataBase;
import com.meshyog.emptycan.model.server.RetrofitAdaptor;
import com.meshyog.emptycan.model.server.WebServiceInterface;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.RequestBody;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;

import cn.pedant.SweetAlert.SweetAlertDialog;


import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

/**
 * Created by varadhan on 28-11-2016.
 */
public class ReferralFormActivity extends AppCompatActivity  {


    protected AppConfig appConfig;

    public SweetAlertDialog  progressDialog;
    public Button saveRefferalBtn;
    public EditText refferalCode;

    protected void onCreate(Bundle savedInstanceState) {

        try{
            super.onCreate(savedInstanceState);
            setContentView(R.layout.referral_form_activity);
            appConfig = (AppConfig)this.getApplicationContext();
            if(getSupportActionBar()!=null){
                getSupportActionBar().setTitle("Referral and Earn");
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            }


            refferalCode=(EditText)  findViewById(R.id.referralCode);
            saveRefferalBtn=(Button) findViewById(R.id.refferalBtn);
            saveRefferalBtn.setOnClickListener(listener);
        }catch(Exception e){
            e.printStackTrace();
        }


    }

    private View.OnClickListener listener=new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch(view.getId()){
                case R.id.refferalBtn:
                    saveRefferalDetails(refferalCode.getText().toString());
                    //uploadImage("test");
                    break;

                case R.id.already_registered_user:
                    onBackPressed();
                    break;
                case R.id.location_name:

                    break;


            }
        }
    };



    public void saveRefferalDetails(String refferalCode){
        progressDialog = new SweetAlertDialog (ReferralFormActivity.this,SweetAlertDialog.PROGRESS_TYPE);
       // progressDialog.setProgressStyle(0);
        //progressDialog.setMessage("Loading...");
        progressDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        progressDialog.setTitleText("Loading");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
       /* if(!validateMobNo()||!validatePass()){
            return ;
        }*/
        if (AppUtils.isNetworkAvailable(this)) {
            //consumerJson=new JsonObject();
            try{
                JsonObject referralDetails=new JsonObject();
                referralDetails.addProperty("refererId",new EmptycanDataBase(getApplicationContext()).getConsumerKey());
                referralDetails.addProperty("referralCode",Long.valueOf(refferalCode));
                referralDetails.addProperty("refererName","");
                referralDetails.addProperty("status","PENDING");

                Retrofit retrofit =null;
                WebServiceInterface webServiceInterface=null;
                Call<JsonObject> call=null;

                retrofit = RetrofitAdaptor.getRetrofit();
                webServiceInterface = retrofit.create(WebServiceInterface.class);
                call = webServiceInterface.addReferalDetails(referralDetails);
                call.enqueue(new Callback<JsonObject>() {
                    @Override
                    public void onResponse(Response<JsonObject> response, Retrofit retrofit) {
                        if (response.message().equals("OK")) {
                            JsonObject result= response.body().getAsJsonObject();
                            if(result.get("status").getAsString().equals("success")){
                                long serverKey=result.get("refferer_server_key").getAsLong();
                                Toast.makeText(getApplicationContext(),"serverKey:"+serverKey,Toast.LENGTH_SHORT).show();

                            }


                        } else {
                            Toast.makeText(getApplicationContext(),"Server Internal Error.Please try again.",Toast.LENGTH_SHORT).show();
                            //request not successful (like 400,401,403 etc)
                            //Handle errors
                        }
                        progressDialog.dismiss();
                        callUserProfiletActivity();
                    }

                    @Override
                    public void onFailure(Throwable t) {
                        JsonObject jsonObject = new JsonObject();
                        jsonObject.addProperty("result_type", t.getMessage());
                        jsonObject.addProperty("result", t.getMessage());
                        progressDialog.dismiss();
                        Toast.makeText(getApplicationContext(),"Error occured.",Toast.LENGTH_SHORT).show();
                        //((SignupActivity)signUpContext).successNotice(jsonObject);
                        // SignupActivity signupActivityy = new SignupActivity();

                    }
                });
            }catch(Exception e){
            e.printStackTrace();
                progressDialog.dismiss();
            }


        } else {
            mAlertDaiog("Check your internet connectivity", false);
        }

    }
    public void mAlertDaiog(String message, boolean isBack) {
        android.support.v7.app.AlertDialog.Builder alertDialogBuilder = new android.support.v7.app.AlertDialog.Builder(this);
        alertDialogBuilder.setMessage((CharSequence) message);
        alertDialogBuilder.setCancelable(false);
        alertDialogBuilder.setPositiveButton("OK", new AddressFormDialogListner(isBack));
        alertDialogBuilder.create().show();
    }



    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(5);
        }
    }


    class AddressFormDialogListner implements DialogInterface.OnClickListener {
        final /* synthetic */ boolean val$isBack;

        AddressFormDialogListner(boolean z) {
            this.val$isBack = z;
        }

        public void onClick(DialogInterface arg0, int arg1) {
            if (this.val$isBack) {
                ReferralFormActivity.this.finish();
            }
        }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) // Press Back Icon
        {
            onBackPressed();
            return true;
            //finish();
        }

        return super.onOptionsItemSelected(item);
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

    public void callUserProfiletActivity(){
        Intent addressListIntent=new Intent(this,BookCanFormActivity.class);
        addressListIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(addressListIntent);
        finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        callUserProfiletActivity();
        //finish();
    }


}
