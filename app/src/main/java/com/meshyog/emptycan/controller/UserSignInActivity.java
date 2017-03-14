package com.meshyog.emptycan.controller;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.AppConstants;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.meshyog.emptycan.R;
import com.meshyog.emptycan.model.AppConfig;
import com.meshyog.emptycan.model.AppUtils;
import com.meshyog.emptycan.model.database.EmptycanDataBase;
import com.meshyog.emptycan.model.server.RetrofitAdaptor;
import com.meshyog.emptycan.model.server.WebServiceInterface;
import com.meshyog.emptycan.references.FavouritesList;
import com.meshyog.emptycan.references.Register;

import org.json.JSONObject;

import cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;


/**
 * Created by Viswanathan on 17/10/16.
 */
public class UserSignInActivity extends AppCompatActivity {
    protected AppConfig appConfig;
    public EditText userName;
    public EditText passWord;
    SharedPreferences sharedPreferences;
    public SweetAlertDialog progressDialog;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_signin_activity);
        appConfig = (AppConfig) this.getApplicationContext();
        Button login = (Button) findViewById(com.meshyog.emptycan.R.id.consumerLoginBtn);
        userName = (EditText) findViewById(com.meshyog.emptycan.R.id.cnsumrPhoneNo);
        passWord = (EditText) findViewById(com.meshyog.emptycan.R.id.cnsumrPassword);
        LinearLayout registerHere = (LinearLayout) findViewById(com.meshyog.emptycan.R.id.new_user_register);
        this.sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        login.setOnClickListener(listener);
        registerHere.setOnClickListener(listener);
        userName.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable edt) {
                if (userName.getText().length() > 0) {
                    userName.setError(null);
                }
                if(userName.getText().length() == 10){
                    userName.setError(null);
                }
            }
            public void onTextChanged(CharSequence s, int start, int before, int count){

            }
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after){

            }
        });
        passWord.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable edt) {
                if (passWord.getText().length() > 0) {
                    passWord.setError(null);
                }
            }
            public void onTextChanged(CharSequence s, int start, int before, int count){

            }
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after){

            }
        });
    }

    private View.OnClickListener listener=new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch(view.getId()){
                case com.meshyog.emptycan.R.id.consumerLoginBtn:
                    String userName1= userName.getText().toString();
                    String passWord1= passWord.getText().toString();
                    if( (userName1.equals(""))  ){
                        userName.setError("Field Can't Blank");
                       return;
                    }else if(passWord1.equals("")){
                        passWord.setError("Field Can't Blank");
                        return ;
                    }else if(userName1.length() != 10){
                        userName.setError("Enter 10 digits");
                        return ;
                    }else{
                        if(AppUtils.isNetworkAvailable(getApplicationContext())){
                            loginAction(userName1,passWord1);
                        }else{
                            mAlertDaiog("Check your internet connectivity", false);
                        }

                    }

                   /* Intent intent=new Intent(UserSignInActivity.this,MainActivity.class);
                    startActivity(intent);*/
                    break;

                case com.meshyog.emptycan.R.id.new_user_register:
                    Intent registerIntent=new Intent(UserSignInActivity.this,UserSignUpActivity.class);
                    startActivity(registerIntent);
                    break;

            }
        }
    };
    public void mAlertDaiog(String message, boolean isBack) {
        android.support.v7.app.AlertDialog.Builder alertDialogBuilder = new android.support.v7.app.AlertDialog.Builder(this);
        alertDialogBuilder.setMessage((CharSequence) message);
        alertDialogBuilder.setCancelable(false);
        alertDialogBuilder.setPositiveButton("OK", new AddressFormDialogListner(isBack));
        alertDialogBuilder.create().show();
    }
    class AddressFormDialogListner implements DialogInterface.OnClickListener {
        final /* synthetic */ boolean val$isBack;

        AddressFormDialogListner(boolean z) {
            this.val$isBack = z;
        }

        public void onClick(DialogInterface arg0, int arg1) {
            if (this.val$isBack) {
                UserSignInActivity.this.finish();
            }
        }
    }
    public void loginAction(String userName,String password){
        try{
            progressDialog = new SweetAlertDialog (UserSignInActivity.this,SweetAlertDialog.PROGRESS_TYPE);
            // progressDialog.setProgressStyle(0);
            //progressDialog.setMessage("Loading...");
            progressDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
            progressDialog.setTitleText("Loading");
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();
            Retrofit retrofit =null;
            WebServiceInterface webServiceInterface=null;
            Call<JsonObject> call=null;
            String fcmToken= this.sharedPreferences.getString("fcm_token","No87FCM54Fo69un44d");
            JsonObject consumerLogin=new JsonObject();
            consumerLogin.addProperty("consumer_username",userName);
            consumerLogin.addProperty("consumer_pwd",password);
            consumerLogin.addProperty("fcm_token",fcmToken);
            consumerLogin.addProperty("phone_unique_key", Settings.Secure.getString(getApplicationContext().getContentResolver(), "android_id"));
            retrofit = RetrofitAdaptor.getRetrofit();
            webServiceInterface = retrofit.create(WebServiceInterface.class);
            call = webServiceInterface.consumerLoginAuthentication(consumerLogin);
            call.enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(Response<JsonObject> response, Retrofit retrofit) {
                    if (response.message().equals("OK")) {
                        JsonObject result= response.body().getAsJsonObject();
                        if(result.get("status").getAsString().equals("valid")){
                            try{
                                Toast.makeText(getApplicationContext(),"Valid User Name",Toast.LENGTH_SHORT).show();
                                JSONObject consumerInfo= (JSONObject)new JSONObject(result.get("consumer_info").getAsString()).getJSONArray("rows").get(0);

                                saveUserDataInShredPref(consumerInfo,result.get("products").getAsString(),result.get("default_address").getAsString(),result.get("first_time_booking_status").getAsString());
                            }catch (Exception e){
                                e.printStackTrace();
                            }

                            progressDialog.dismiss();
                        }else{
                            progressDialog.dismiss();
                            Toast.makeText(getApplicationContext(),"Invalid User Name/Password",Toast.LENGTH_SHORT).show();
                        }


                    } else {
                        //request not successful (like 400,401,403 etc)
                        //Handle errors
                        progressDialog.dismiss();
                        Toast.makeText(getApplicationContext(),"Sorry something went wrong.",Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Throwable t) {
                    JsonObject jsonObject = new JsonObject();
                    jsonObject.addProperty("result_type", t.getMessage());
                    jsonObject.addProperty("result", t.getMessage());
                    progressDialog.dismiss();
                    Toast.makeText(getApplicationContext(),"Internal Eroor",Toast.LENGTH_SHORT).show();
                    //((SignupActivity)signUpContext).successNotice(jsonObject);
                    // SignupActivity signupActivityy = new SignupActivity();

                }
            });
        }catch(Exception e){
            e.printStackTrace();
        }

    }
    private void clearReferences(){
        Activity currActivity = appConfig.getCurrentActivity();
        if (this.equals(currActivity))
            appConfig.setCurrentActivity(null);
    }
    protected void onResume() {
        super.onResume();
        appConfig.setCurrentActivity(this);
    }
    protected void onPause() {
        clearReferences();
        super.onPause();
    }
    protected void onDestroy() {
        clearReferences();
        super.onDestroy();
    }
    public void saveUserDataInShredPref(JSONObject jsonObject,String productInfoArrayList,String defaultAddress,String firstBookingStatus){
        try{
            if(defaultAddress.equals("{}")){
                defaultAddress="";
            }

            SharedPreferences.Editor editor= this.sharedPreferences.edit();
            editor.putBoolean(EmptycanDataBase.FIRST_TIME_USER,false);
            editor.putString(EmptycanDataBase.USER_SERVER_KEY,jsonObject.getJSONObject("id").getString("id"));
            editor.putString(EmptycanDataBase.USER_MOBNO,jsonObject.getString("consumerPhoneNumber"));
            // editor.putString(EmptycanDataBase.USER_,jsonObject.get("consumerPassword").getAsString());
            editor.putString(EmptycanDataBase.APP_DEVICEINFO,jsonObject.getString("consumerDeviceName")+jsonObject.getString("consumerDeviceModel"));
            editor.putString(EmptycanDataBase.APP_DEVICEID,jsonObject.getString("consumerDeviceId"));
            editor.putString(EmptycanDataBase.USER_NAME,jsonObject.getString("consumerName"));

            editor.putString(EmptycanDataBase.USER_DOB,jsonObject.getString("consumerDOB").equals("null")?"00-00-0000":jsonObject.getString("consumerDOB"));
            editor.putString(EmptycanDataBase.USER_GENDER,jsonObject.getString("consumerGender").equals("null")?"male/female/transgender":jsonObject.getString("consumerGender"));
            editor.putString(EmptycanDataBase.USER_EMAIL,jsonObject.getString("consumerEmailId").equals("null")?"noemail@xxxx.com":jsonObject.getString("consumerEmailId"));
            editor.putString(EmptycanDataBase.PRODUCT_LIST,productInfoArrayList);
            editor.putString(EmptycanDataBase.USER_PRO_IMG_URL,jsonObject.getString("consumerProfileImageBlobUrl").equals("null")? AppConstants.USER_DEFAULT_AVATHAR:jsonObject.getString("consumerProfileImageBlobUrl"));
            editor.putString(EmptycanDataBase.DEFAULT_ADDRESS,defaultAddress);
            editor.putString(EmptycanDataBase.DEFAULT_DISTRBUTR_ID,String.valueOf(jsonObject.getLong("defaultDistributorId")));
            editor.putString(EmptycanDataBase.IS_COMPLETED_FIRST_BOOKING,firstBookingStatus);
            editor.commit();
            Intent intent_loc = new Intent(this, BookCanFormActivity.class);
            startActivity(intent_loc);
            finish();
            progressDialog.dismiss();
        }catch(Exception e){
            e.printStackTrace();
        }

        //progressDialog.dismiss();
    }

}
