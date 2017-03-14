package com.meshyog.emptycan.controller;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.AppConstants;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.JsonObject;
import com.meshyog.emptycan.R;
import com.meshyog.emptycan.model.AppConfig;
import com.meshyog.emptycan.model.AppUtils;
import com.meshyog.emptycan.model.database.EmptycanDataBase;
import com.meshyog.emptycan.model.server.RetrofitAdaptor;
import com.meshyog.emptycan.model.server.WebServiceInterface;

import java.util.regex.Pattern;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

/**
 * Created by Viswanathan on 17/10/16.
 */
public class UserSignUpActivity extends AppCompatActivity  {
   // @InjectView(R.id.cnsumrUserName)
    public EditText cnsumrUserName;
   // @InjectView(R.id.cnsumrPhoneNo)
    public EditText cnsumrPhoneNo;
   // @InjectView(R.id.cnsumrPassword)
    public EditText cnsmrPassword;
    public EditText defaultDistributor;
    public JsonObject consumerJson=new JsonObject();
    SharedPreferences sharedPreferences;
    public ProgressDialog progressDialog;
    protected AppConfig appConfig;
    public String defaultDistId;
    private GoogleApiClient mGoogleApiClient;
    private Activity  mActivity;
    private static final long FASTEST_INTERVAL = 5000;
    private static final long INTERVAL = 10000;
    protected static final int REQUEST_CHECK_SETTINGS = 1;
    private static final String TAG = "MainActivity";
    private BroadcastReceiver mRegistrationBroadcastReceiver;
    LocationRequest mLocationRequest;
    Location mCurrentLocation;
    String latLong;
    //public JsonObject ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try{
            super.onCreate(savedInstanceState);
            setContentView(R.layout.user_signup_activity);
            appConfig = (AppConfig)this.getApplicationContext();
            this.sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            Button register=(Button)findViewById(R.id.cnsumrsignupbtnrpl);
            LinearLayout alreadyRegUserBtn=(LinearLayout) findViewById(R.id.already_registered_user);
            //ButterKnife.inject(this);
            System.out.println("VARAD");
            cnsumrUserName=(EditText)findViewById(R.id.cnsumrUserName);
            cnsumrPhoneNo=(EditText)findViewById(R.id.cnsumrPhoneNo);
            cnsmrPassword=(EditText)findViewById(R.id.cnsumrPassword);
            defaultDistributor=(EditText)findViewById(R.id.defaultDistributor);
            register.setOnClickListener(listener);
            alreadyRegUserBtn.setOnClickListener(listener);


            //setupLocationSettingDialog();
        }catch(Exception e){
            e.printStackTrace();
        }

    //Retrofit    retrofit = RetrofitAdaptor.getRetrofit();
        //System.out.println(retrofit);
        //login.setOnClickListener(listener);

    }
    private View.OnClickListener listener=new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch(view.getId()){
                case R.id.cnsumrsignupbtnrpl:
                    userSignUpDataHandler();
                    break;

                case R.id.already_registered_user:
                    Intent myIntent = new Intent(getApplicationContext(), UserSignInActivity.class);
                    startActivity(myIntent);
                    finish();
                   // onBackPressed();
                    break;

            }
        }
    };

    public void userSignUpDataHandler(){

            String uName = cnsumrUserName.getText().toString();
            String pNo = cnsumrPhoneNo.getText().toString();
            String cPwrd = cnsmrPassword.getText().toString();
        defaultDistId= defaultDistributor.getText().toString();
        //defaultDistId="123456";
        if(defaultDistId==null || defaultDistId.isEmpty())
            defaultDistId="123456";
        long distributorId = Long.parseLong(defaultDistId);
         boolean isValidPhone=validateMobNo(pNo);
        boolean isValidPass=validatePass(cPwrd);
        boolean isValidUname=validatUserName(uName);
            if(!isValidPhone || !isValidPass || !isValidUname){
                return ;
            }
            if (AppUtils.isNetworkAvailable(this)) {
                //consumerJson=new JsonObject();
               try{
                   consumerJson.addProperty("consumerPhoneNumber",pNo);
                   consumerJson.addProperty("consumerPassword",cPwrd);
                   consumerJson.addProperty("consumerName",uName);
                   consumerJson.addProperty("defaultDistributorId",distributorId);

                   String[] deviceInfo= AppUtils.getDeviceInfo().split(Pattern.quote(("||")));
                   if(deviceInfo.length==1 || deviceInfo.length==2)
                   consumerJson.addProperty("consumerDeviceName",deviceInfo[0] + AppUtils.getDeviceSuperInfo());
                   if(deviceInfo.length==2)
                   consumerJson.addProperty("consumerDeviceModel",deviceInfo[1]);
                   else
                       consumerJson.addProperty("consumerDeviceModel",deviceInfo[0]);
                   consumerJson.addProperty("deviceSuperInfo",AppUtils.getDeviceSuperInfo());
                   consumerJson.addProperty("consumerDeviceId",new AppUtils(getApplicationContext()).getDeviceId());
                   progressDialog = new ProgressDialog(UserSignUpActivity.this);
                   progressDialog.setProgressStyle(0);
                   progressDialog.setMessage("Loading...");
                   progressDialog.setCanceledOnTouchOutside(false);
                   progressDialog.show();
                   checkPhoneNoExists(pNo);
                   //saveUserDatas();
               }catch(Exception e){
                   e.printStackTrace();
                   Toast.makeText(getApplicationContext(),"Error saving data",Toast.LENGTH_SHORT).show();

               }


            } else {
                mAlertDaiog("Check your internet connectivity", false);
            }

    }

private void checkPhoneNoExists(String pNo){
    try{
        Retrofit retrofit =null;
        WebServiceInterface webServiceInterface=null;
        Call<JsonObject> call=null;
        retrofit = RetrofitAdaptor.getRetrofit();
        webServiceInterface = retrofit.create(WebServiceInterface.class);
        call = webServiceInterface.isPhoneNoExists(pNo);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Response<JsonObject> response, Retrofit retrofit) {
                if (response.message().equals("OK")) {
                    JsonObject result= response.body().getAsJsonObject();
                    if(result.get("status").getAsString().equals("success")){

                        if(result.get("phone_exists").getAsBoolean()){
                            Toast.makeText(getApplicationContext(),"Sorry ! Phone Number already registred. ",Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                            return ;
                        }else{
                            saveUserDatas();
                        }

                    }


                } else {
                    Toast.makeText(getApplicationContext(),"Server Error while phone number validation ",Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                    //request not successful (like 400,401,403 etc)
                    //Handle errors
                }
            }

            @Override
            public void onFailure(Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(getApplicationContext(),"Internal server Error",Toast.LENGTH_SHORT).show();
                //((SignupActivity)signUpContext).successNotice(jsonObject);
                // SignupActivity signupActivityy = new SignupActivity();

            }
        });
    }catch(Exception e){
        e.printStackTrace();
    }
}
    private boolean validateMobNo(String phoneNumber) {
        if (phoneNumber.trim().isEmpty()) {
            this.cnsumrPhoneNo.setError("Enter Valid number");
            requestFocus(this.cnsumrPhoneNo);
            return false;
        }
       // this.cnsumrPhoneNo.setErrorEnabled(false);
        return true;
    }

    private boolean validatePass(String passWrd) {
        if (passWrd.trim().isEmpty()) {
            cnsmrPassword.setError("Enter Password");
            requestFocus(this.cnsmrPassword);
            return false;
        }
      //  this.inputLayPass.setErrorEnabled(false);
        return true;
    }
    private boolean validatUserName(String userName){
        if(userName.length() < 4 || userName.equals("")){
            cnsumrUserName.setError("User Name Atleast 4 chracters");
            requestFocus(this.cnsumrUserName);
            return false;
        }

        return true;
    }



    public void mAlertDaiog(String message, boolean isBack) {
        android.support.v7.app.AlertDialog.Builder alertDialogBuilder = new android.support.v7.app.AlertDialog.Builder(this);
        alertDialogBuilder.setMessage((CharSequence) message);
        alertDialogBuilder.setCancelable(false);
        alertDialogBuilder.setPositiveButton("OK", new UserSignUpDialogOnclicListner(isBack));
        alertDialogBuilder.create().show();
    }
  /*  private boolean validateRePass() {
        if (cnsmrPassword.getText().toString().trim().equals(cnsmrPassword.getText().toString().trim())) {
           // this.cnsmrPassword.setErrorEnabled(false);
            return true;
        }
        this.cnsmrPassword.setError("Password not match");
        requestFocus(this.cnsmrPassword);
        return false;
    }*/
    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(5);
        }
    }

    class UserSignUpDialogOnclicListner implements DialogInterface.OnClickListener {
        final /* synthetic */ boolean val$isBack;

        UserSignUpDialogOnclicListner(boolean z) {
            this.val$isBack = z;
        }

        public void onClick(DialogInterface arg0, int arg1) {
            if (this.val$isBack) {
                UserSignUpActivity.this.finish();
            }
        }
    }

    public void saveUserDatas() {

        new AsyncTask<Void, Void, String>() {

            Retrofit retrofit =null;
            WebServiceInterface webServiceInterface=null;
            Call<JsonObject> call=null;
            protected void onPreExecute() {
                super.onPreExecute();

               // UserSignUpActivity.this.resp_msg = SignUpActivity.this.getResources().getString(C0609R.string.went_wrong);
            }
            @Override
            protected String doInBackground(Void... params) {
                String msg = "";
                String registrationId;
                Log.d("Register GCM", "started");
                try {
                    registrationId= FirebaseInstanceId.getInstance().getToken();


                    msg ="Device registered, registration ID=" + registrationId;
                    Log.e("msg",msg);
                    //.register(registrationId).execute();

                } catch (Exception ex) {
                    return "Error :" + ex.getMessage();
                }

                //registrationId = "not yet registred";
                return registrationId;
            }


            protected void onPostExecute(String consumerDeviceId) {
                try{
                    //consumerProfileEntity.setConsumerMobileHardWareId(getPhoneHardwareId());
                    // msgg=consumerDeviceId;

                   consumerJson.addProperty("consumerDeviceNotificationId",consumerDeviceId);
                    // Log.e("Detailsddada",   getMobileHardWareKey());
                     retrofit = RetrofitAdaptor.getRetrofit();
                     webServiceInterface = retrofit.create(WebServiceInterface.class);
                     call = webServiceInterface.saveNewConsumer(consumerJson);
                    call.enqueue(new Callback<JsonObject>() {
                        @Override
                        public void onResponse(Response<JsonObject> response, Retrofit retrofit) {
                            if (response.message().equals("OK")) {
                                JsonObject result= response.body().getAsJsonObject();
                                if(result.get("status").getAsString().equals("success")){
                                    consumerJson.addProperty("consumer_server_key",result.get("consumer_key").getAsString());
                                    String productsArray= result.get("products").getAsString();
                                    String refernceStatus= result.get("referencestatus").getAsString();
                                    saveUserDataInShredPref(consumerJson,productsArray,refernceStatus);
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
        }.execute();
    }
    public void saveUserDataInShredPref(JsonObject jsonObject, String productInfoArrayList,String refStatus){   SharedPreferences.Editor editor= this.sharedPreferences.edit();
try{
    editor.putBoolean(EmptycanDataBase.FIRST_TIME_USER,false);
    editor.putString(EmptycanDataBase.USER_SERVER_KEY,jsonObject.get("consumer_server_key").getAsString());
    editor.putString(EmptycanDataBase.USER_MOBNO,jsonObject.get("consumerPhoneNumber").getAsString());
    // editor.putString(EmptycanDataBase.USER_,jsonObject.get("consumerPassword").getAsString());
    editor.putString(EmptycanDataBase.APP_DEVICEINFO,jsonObject.get("consumerDeviceName").getAsString()+jsonObject.get("consumerDeviceModel").getAsString());
    editor.putString(EmptycanDataBase.APP_DEVICEID,jsonObject.get("consumerDeviceId").getAsString());
    editor.putString(EmptycanDataBase.USER_NAME,jsonObject.get("consumerName").getAsString());
    editor.putString(EmptycanDataBase.DEFAULT_DISTRBUTR_ID,defaultDistId);
    editor.putString(EmptycanDataBase.PRODUCT_LIST,productInfoArrayList);
    editor.putString(EmptycanDataBase.IS_COMPLETED_FIRST_BOOKING,"NOT_YET");
    editor.putString(EmptycanDataBase.REF_STATUS,refStatus);
    editor.putString(EmptycanDataBase.USER_PRO_IMG_URL, AppConstants.USER_DEFAULT_AVATHAR);
    editor.commit();
    Intent intent_loc = new Intent(this, BookCanFormActivity.class);
    startActivity(intent_loc);
    finish();
    progressDialog.dismiss();
}catch (Exception e){
    e.printStackTrace();
}

    }
    @Override
    public void finish() {
        if(progressDialog != null) {
            progressDialog.dismiss();
        }
        super.finish();
    }
  /*  private View.OnClickListener listener=new View.OnClickListener() {
        @Override
        public void onClick(View view) {
           switch(view.getId()){
               case com.meshyog.emptycan.R.id.register_register_user:
                   onBackPressed();
                   break;

               case com.meshyog.emptycan.R.id.already_registered_user:
                   onBackPressed();
                   break;

            }
        }
    };*/
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
    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }



}
