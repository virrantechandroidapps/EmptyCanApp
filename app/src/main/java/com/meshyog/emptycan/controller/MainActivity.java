package com.meshyog.emptycan.controller;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.BuildConfig;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.LocationRequest;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.meshyog.emptycan.model.AppConfig;
import com.meshyog.emptycan.model.AppUtils;
import com.meshyog.emptycan.model.database.EmptycanDataBase;
import com.meshyog.emptycan.model.server.RetrofitAdaptor;
import com.meshyog.emptycan.model.server.WebServiceInterface;

import cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

/**
 * Created by varadhan on 14-11-2016.
 */
public class MainActivity extends AppCompatActivity {
    boolean isFirstTime;
    boolean isLoaded;
    boolean isOutdate;
    String latLong;
    private static final long FASTEST_INTERVAL = 5000;
    private static final long INTERVAL = 10000;
    protected static final int REQUEST_CHECK_SETTINGS = 1;
    Location mCurrentLocation;
    AppUtils appUtilInstance;
    LocationRequest mLocationRequest;
    public SweetAlertDialog progressDialog;
    boolean isCanceled;
    private static final String TAG = "MainActivity";
    SharedPreferences sharedPreferences;
    protected AppConfig appConfig;
    private EmptycanDataBase db;
    private static final int PERMISSIONS_REQUEST_ACCESS_LOCATION = 0;



    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        resetAll();
        //requestUserSendCrashReport();
        appConfig = (AppConfig) this.getApplicationContext();
        //requestWindowFeature(REQUEST_CHECK_SETTINGS);
        //setContentView(com.meshyog.emptycan.R.layout.login_user);
        this.appUtilInstance = AppUtils.getInstance(this);
        Intent intent_loc;

        if (!isGooglePlayServicesAvailable()) {
            finish();
        }
        //setupLocationSettingDialog();
        //this.mGoogleApiClient = new GoogleApiClient.Builder(this).addApi(LocationServices.API).addConnectionCallbacks(this).addOnConnectionFailedListener(this).build();
        try {
            this.db.createDataBase();
            if (isFirstTimeUser()) {

                intent_loc = new Intent(this, UserSignUpActivity.class);
                startActivity(intent_loc);
                finish();
            } else {
                this.isFirstTime = false;
                if (AppUtils.isNetworkAvailable(getApplicationContext())) {
                    launchBookCanFormActiviy();
                    //getLatestProductsFromServer();
                } else {
                    intent_loc = new Intent(this, BookCanFormActivity.class);
                    startActivity(intent_loc);
                    finish();
                }

            }


                System.out.println("ddd");

            System.out.println("");
            // ATTENTION: This was auto-generated to implement the App Indexing API.
            // See https://g.co/AppIndexing/AndroidStudio for more information.
            //client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
        } catch (Exception e) {
            e.printStackTrace();
        }


    }
    class RegistrationBroadCastReceiver extends BroadcastReceiver {
        RegistrationBroadCastReceiver() {
        }

        public void onReceive(Context context, Intent intent) {
            Toast.makeText(getApplicationContext(),"Receiver calling",Toast.LENGTH_LONG).show();
        }
    }
    public void getLatestProductsFromServer() {
        Retrofit retrofit = null;
        WebServiceInterface webServiceInterface = null;
        Call<JsonArray> call = null;
        try {
            progressDialog = new SweetAlertDialog(MainActivity.this, SweetAlertDialog.PROGRESS_TYPE);
            // progressDialog.setProgressStyle(0);
            //progressDialog.setMessage("Loading...");
            progressDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
            progressDialog.setTitleText("Loading");
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();
            retrofit = RetrofitAdaptor.getRetrofit();
            webServiceInterface = retrofit.create(WebServiceInterface.class);
            long defaultDistributorId = Long.parseLong(this.sharedPreferences.getString(EmptycanDataBase.DEFAULT_DISTRBUTR_ID, "123456"));
            call = webServiceInterface.getProducts(defaultDistributorId);
            call.enqueue(new Callback<JsonArray>() {
                @Override
                public void onResponse(Response<JsonArray> response, Retrofit retrofit) {
                    if (response.message().equals("OK")) {
                        JsonArray result = response.body().getAsJsonArray();
                        saveProductInShredPref(result);
                    } else {
                        //request not successful (like 400,401,403 etc)
                        //Handle errors
                        launchBookCanFormActiviy();
                    }
                    progressDialog.dismiss();
                }

                @Override
                public void onFailure(Throwable t) {
                    JsonObject jsonObject = new JsonObject();
                    jsonObject.addProperty("result_type", t.getMessage());
                    jsonObject.addProperty("result", t.getMessage());
                    Toast.makeText(getApplicationContext(), "Internal server Error", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                    launchBookCanFormActiviy();
                    //((SignupActivity)signUpContext).successNotice(jsonObject);
                    // SignupActivity signupActivityy = new SignupActivity();

                }
            });
        } catch (Exception e) {
            progressDialog.dismiss();
            launchBookCanFormActiviy();
            e.printStackTrace();
        }
    }

    public void resetAll() {
        this.isFirstTime = true;
        this.isOutdate = false;
        this.isLoaded = false;
        this.latLong = BuildConfig.FLAVOR;
        this.sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        this.db = new EmptycanDataBase(this);
        //this.locCord = BuildConfig.FLAVOR;
    }






    private boolean isGooglePlayServicesAvailable() {
        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (status == 0) {
            return true;
        }
        GooglePlayServicesUtil.getErrorDialog(status, this, 0).show();
        return false;
    }

    public boolean isFirstTimeUser() {
        boolean result = false;
        try {
            if (this.sharedPreferences.getBoolean(EmptycanDataBase.FIRST_TIME_USER, true)) {
                return true;
            } else {

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
    }

    protected void createLocationRequest() {
        this.mLocationRequest = new LocationRequest();
        this.mLocationRequest.setInterval(INTERVAL);
        this.mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
        this.mLocationRequest.setPriority(100);
    }


    public  void saveProductInShredPref(JsonArray productArray) {
        try {
            if(this.sharedPreferences==null){
                this.sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            }
            SharedPreferences.Editor editor = this.sharedPreferences.edit();
            editor.putString(EmptycanDataBase.PRODUCT_LIST, productArray.toString());
            editor.commit();
            launchBookCanFormActiviy();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void launchBookCanFormActiviy() {
        Intent intent_loc = new Intent(this, BookCanFormActivity.class);
        startActivity(intent_loc);
        finish();
    }

    @Override
    protected void onResume() {
        clearReferences();
        super.onResume();

    }

    @Override
    protected void onPause() {
        clearReferences();
        super.onPause();
    }

    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public void onStop() {
        super.onStop();

    }

    @Override
    public void onDestroy() {
        clearReferences();
        super.onDestroy();
        Log.d(TAG, "onDestroy fired ..............");

    }

     /*public void onActivityResult(int requestCode, int resultCode, Intent data) {
         switch (requestCode) {
             case REQUEST_CHECK_SETTINGS :
                switch (resultCode) {
                    case MessageApi.UNKNOWN_REQUEST_ID:
                       // createLocationRequest();
                        if (this.isLoaded) {
                            Intent intent_loc = new Intent(this, UserProfileActivity.class);
                            intent_loc.putExtra("intentpos", REQUEST_CHECK_SETTINGS);
                            startActivity(intent_loc);
                            finish();
                        }
                    case MessageApi.FILTER_LITERAL:
                        this.isCanceled = true;
                        if (this.isLoaded) {
                            startActivity(new Intent(this, UserProfileActivity.class));
                            finish();
                        }
                    default:
                }
            default:
        }
    }*/
    private void clearReferences() {
        Activity currActivity = appConfig.getCurrentActivity();
        if (this.equals(currActivity))
            appConfig.setCurrentActivity(null);
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }





}
// for splash screen http://stackoverflow.com/questions/17357226/add-the-loading-screen-in-starting-of-the-android-application