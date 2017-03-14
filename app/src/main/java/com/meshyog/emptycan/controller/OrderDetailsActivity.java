package com.meshyog.emptycan.controller;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.AppConstants;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.wearable.MessageApi;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.meshyog.emptycan.R;
import com.meshyog.emptycan.model.AppConfig;
import com.meshyog.emptycan.model.AppUtils;
import com.meshyog.emptycan.model.database.CartInfo;
import com.meshyog.emptycan.model.database.EmptycanDataBase;
import com.meshyog.emptycan.model.database.OrderInfo;
import com.meshyog.emptycan.model.server.RetrofitAdaptor;
import com.meshyog.emptycan.model.server.WebServiceInterface;
import com.meshyog.emptycan.view.AddressListAdapter;
import com.meshyog.emptycan.view.OrderInfoCardView;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;
import it.gmariotti.cardslib.library.view.CardViewNative;
import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

/**
 * Created by varadhan on 10-12-2016.
 */

public class OrderDetailsActivity extends AppCompatActivity implements LocationListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,android.location.LocationListener  {
    public OrderInfoCardView orderInfoCardView =null;
    public Button placeOdrBtn;
    public  ArrayList<CartInfo>  cartInfoLst;
    public SharedPreferences sharedPreferences;
    public TextView addressTitle;
    public TextView streetAddress;
    public TextView addressPart1;
    public ImageView changeAddressImage;
    public long addressServerKey;
    public SweetAlertDialog progressDialog;
    protected AppConfig appConfig;
    public String firstTimeBooking="";
    public String userFcmId="";
    public String refStatus="";
    public long defaultDistributorId=0l;
    SweetAlertDialog bookingConfirmationDlg=null;
    Location mCurrentLocation;
    private GoogleApiClient mGoogleApiClient;
    LocationRequest mLocationRequest;
    private static final long FASTEST_INTERVAL = 5000;
    private static final long INTERVAL = 10000;
    protected static final int REQUEST_CHECK_SETTINGS = 1;
    private static final String TAG = "OrderDetailsActivity";
    private BroadcastReceiver mRegistrationBroadcastReceiver;
    public static JsonObject locDetails=new JsonObject();
    public static String locLang;
    private static final int MY_PERMISSIONS_REQUEST_READ_LOCATION=10;
    public static  boolean isLocReqCalled=false;
    protected void onCreate(Bundle savedInstanceState) {
        try{
            super.onCreate(savedInstanceState);
            //Thread.setDefaultUncaughtExceptionHandler(new TopExceptionHandler(this));
            appConfig = (AppConfig)this.getApplicationContext();

            if( AppUtils.isNetworkAvailable(getApplicationContext())){
               // appConfig = (AppConfig)this.getApplicationContext();
                setContentView(R.layout.orders_details_activity);
                Intent bookCanFormActvtyIntnt= getIntent();
                cartInfoLst= (ArrayList<CartInfo>)bookCanFormActvtyIntnt.getSerializableExtra(AppConstants.CARTINFOLIST);
                String newAddress= bookCanFormActvtyIntnt.getExtras().getString("changedAddress");
                this.sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                orderInfoCardView=new OrderInfoCardView(getApplicationContext(),cartInfoLst,this);
                orderInfoCardView.init();
                ((CardViewNative) findViewById(R.id.carddemo)).setCard(this.orderInfoCardView);
                placeOdrBtn= (Button) findViewById(R.id.placeOrderBtn);
                placeOdrBtn.setOnClickListener(listener);
                addressTitle=(TextView) findViewById(R.id.addressHeader);
                streetAddress=(TextView)  findViewById(R.id.addressPart1);
                addressPart1=(TextView)  findViewById(R.id.addressPart2);
                changeAddressImage=(ImageView) findViewById(R.id.changeAddressImg);
                changeAddressImage.setOnClickListener(listener);
                if(getSupportActionBar()!=null) {
                    getSupportActionBar().setTitle("Review Order ");
                    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                }
                this.mRegistrationBroadcastReceiver = new RegistrationBroadCastReceiver();
                this.mGoogleApiClient = new GoogleApiClient.Builder(this).addApi(LocationServices.API).addConnectionCallbacks(this).addOnConnectionFailedListener(this).build();
                if(!isLocReqCalled){
                    createLocationRequest();
                    updateUI();
                }



            /*defaultAddressMap.addProperty("fullName",consumerFullName.getText().toString());
            defaultAddressMap.addProperty("locationName",locationName.getText().toString());
            defaultAddressMap.addProperty("contactNumber",contactNumber.getText().toString());
            defaultAddressMap.addProperty("addressServerKey",contactNumber.getText().toString());
            editor.putString("defaultAddress",defaultAddressMap.toString());*/
                String addressInfo=  this.sharedPreferences.getString(EmptycanDataBase.DEFAULT_ADDRESS,"");
                firstTimeBooking=  this.sharedPreferences.getString(EmptycanDataBase.IS_COMPLETED_FIRST_BOOKING,"");
                defaultDistributorId=Long.parseLong(this.sharedPreferences.getString(EmptycanDataBase.DEFAULT_DISTRBUTR_ID,"123456"));
                refStatus=  this.sharedPreferences.getString(EmptycanDataBase.REF_STATUS,"");
                userFcmId=  this.sharedPreferences.getString("fcm_token","NOT_FOUND");
                if(!addressInfo.equals("") && newAddress==null){
                    JSONObject jsonObject=new JSONObject(addressInfo);
                    String fullName=  jsonObject.getString("fullName");
                    String locationName=  jsonObject.getString("locationName");
                    String contactNo=  jsonObject.getString("contactNumber");
                    addressServerKey=jsonObject.getLong("addressServerKey");
                    addressTitle.setText(fullName);
                    streetAddress.setText(locationName);
                    addressPart1.setText("Contact number +91 "+contactNo);
                }else if(newAddress!=null && !newAddress.equals("")){
                    JSONObject jsonObject=new JSONObject(newAddress);
                    String fullName=  jsonObject.getString("consumerFullName");
                    String locationName=  jsonObject.getString("locationName");
                    String contactNo=  jsonObject.getString("contactNumber");
                    addressServerKey=jsonObject.getJSONObject("id").getLong("id");
                    addressTitle.setText(fullName);
                    streetAddress.setText(locationName);
                    addressPart1.setText("Contact number +91 "+contactNo);


                }else{
                    addressTitle.setText("No Header");
                    streetAddress.setText("No Location");
                    addressPart1.setText("Contact number +91 "+0000000000);
                }
            }else{
                mAlertDaiog("Check your internet connectivity", true);
            }


        }catch (Exception e){
            e.printStackTrace();
        }


    }
    protected void createLocationRequest() {
        this.mLocationRequest = new LocationRequest();
        this.mLocationRequest.setInterval(INTERVAL);
        this.mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
        this.mLocationRequest.setPriority(100);
    }
    private void displayLocationSettingsRequest(Context context) {
        isLocReqCalled=true;
        GoogleApiClient googleApiClient = new GoogleApiClient.Builder(context).addApi(LocationServices.API).build();
        googleApiClient.connect();
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(100);
        locationRequest.setInterval(INTERVAL);
        locationRequest.setFastestInterval(FASTEST_INTERVAL);
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);
        builder.setAlwaysShow(true);
        LocationServices.SettingsApi.checkLocationSettings(googleApiClient, builder.build()).setResultCallback(new LocationResultCalBack());

    }
    class LocationResultCalBack implements ResultCallback<LocationSettingsResult> {
        LocationResultCalBack() {
        }

        public void onResult(@NonNull LocationSettingsResult result) {
            Status status = result.getStatus();
            switch (status.getStatusCode()) {
                case  MessageApi.FILTER_LITERAL :
                    Log.i(TAG, "All location settings are satisfied.");
                 //   setUpBookWaterCanMainPage();
                    break;
                case 6  :
                    Log.i("OrderDetails Activity", "Location settings are not satisfied. Show the user a dialog to upgrade location settings ");
                    try {
                        status.startResolutionForResult(OrderDetailsActivity.this, OrderDetailsActivity.REQUEST_CHECK_SETTINGS);
                    } catch (IntentSender.SendIntentException e) {
                        Log.i(TAG, "PendingIntent unable to execute request.");
                    }
                    break;
                case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE :
                    Log.i(TAG, "Location settings are inadequate, and cannot be fixed here. Dialog not created.");
                    break;
                default:
            }
        }

    }
    public void mAlertDaiog(String message, boolean isBack) {
        android.support.v7.app.AlertDialog.Builder alertDialogBuilder = new android.support.v7.app.AlertDialog.Builder(this);
        alertDialogBuilder.setMessage((CharSequence) message);
        alertDialogBuilder.setCancelable(false);
        alertDialogBuilder.setPositiveButton("OK", new AddressFormDialogListner(isBack));
        alertDialogBuilder.create().show();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        startLocationUpdates();

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onLocationChanged(Location location) {
        this.mCurrentLocation = location;
        //this.latLong.equals(BuildConfig.FLAVOR)
        if ( true) {
            if(!isLocReqCalled){
                updateUI();
            }

        } else {
           // this.mGoogleApiClient.disconnect();
        }

    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {
        if(LocationManager.GPS_PROVIDER.equals(s)){
            Toast.makeText(this,"GPS on",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onProviderDisabled(String s) {
        if(LocationManager.GPS_PROVIDER.equals(s)){
            Toast.makeText(this,"GPS off",Toast.LENGTH_SHORT).show();
            this.mCurrentLocation=null;
            this.mGoogleApiClient = new GoogleApiClient.Builder(this).addApi(LocationServices.API).addConnectionCallbacks(this).addOnConnectionFailedListener(this).build();

            if(!isLocReqCalled){
                createLocationRequest();
                updateUI();
            }

        }
    }

    private void updateUI() {
        Log.d(TAG, "UI update initiated .............");
        if (this.mCurrentLocation != null) {
            locDetails.addProperty("latitude",String.valueOf(this.mCurrentLocation.getLatitude()));
            locDetails.addProperty("longitude",String.valueOf(this.mCurrentLocation.getLongitude()));
            locDetails.addProperty("altitude",String.valueOf(this.mCurrentLocation.getAltitude()));
            locDetails.addProperty("accuracy",String.valueOf(this.mCurrentLocation.getAccuracy()));
            locDetails.addProperty("bearing",String.valueOf(this.mCurrentLocation.getBearing()));
            locDetails.addProperty("provider",String.valueOf(this.mCurrentLocation.getProvider()));
            locDetails.addProperty("speed",String.valueOf(this.mCurrentLocation.getSpeed()));
            locDetails.addProperty("time",String.valueOf(this.mCurrentLocation.getTime()));
            locDetails.addProperty("describeContents",String.valueOf(this.mCurrentLocation.describeContents()));
            this.sharedPreferences.edit().putString(EmptycanDataBase.USER_LOC_LAT_LANG,locDetails.toString());
            this.sharedPreferences.edit().commit();

            //updateUserLoc(this.mCurrentLocation.getLatitude(),this.mCurrentLocation.getLongitude());
            //Toast.makeText(getApplicationContext(),"Current Position "+this.mCurrentLocation.getLongitude() ,Toast.LENGTH_SHORT).show();

        } else if (!AppUtils.isGPSAvailable(this)) {
            displayLocationSettingsRequest(this);
        }else {

        }
    }
    public void updateUserLoc(double lat,double lang){
        Retrofit retrofit =null;
        WebServiceInterface webServiceInterface=null;
        Call<JsonObject> call=null;
        try{
            retrofit = RetrofitAdaptor.getRetrofit();
            webServiceInterface = retrofit.create(WebServiceInterface.class);
            JsonObject jsonObject=new JsonObject();
            jsonObject.addProperty("locLat",String.valueOf(lat));
            jsonObject.addProperty("locLang",String.valueOf(lang));
            jsonObject.addProperty("userId",new EmptycanDataBase(getApplicationContext()).getConsumerKey());
            call = webServiceInterface.saveUserLocation(jsonObject);
            call.enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(Response<JsonObject> response, Retrofit retrofit) {
                    if (response.message().equals("OK")) {
                    } else {

                    }
                }
                @Override
                public void onFailure(Throwable t) {
                    Toast.makeText(getApplicationContext(),""+t.getMessage(),Toast.LENGTH_SHORT).show();

                }
            });
        }catch(Exception e){
            e.printStackTrace();
        }

    }
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    class AddressFormDialogListner implements DialogInterface.OnClickListener {
        final /* synthetic */ boolean val$isBack;

        AddressFormDialogListner(boolean z) {
            this.val$isBack = z;
        }

        public void onClick(DialogInterface arg0, int arg1) {
            if (this.val$isBack) {
                OrderDetailsActivity.this.finish();
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
    private View.OnClickListener listener=new View.OnClickListener() {

        @Override
        public void onClick(View view) {
            switch(view.getId()){
                case R.id.placeOrderBtn:
                    if(AppUtils.isNetworkAvailable(getApplicationContext()))
                    showBookingConfirmationDialog();
                    else
                        mAlertDaiog("Check your internet connectivity", true);

                break;
                case R.id.changeAddressImg:
                    PopupMenu popup = new PopupMenu(OrderDetailsActivity.this, view);
                    // Inflate the menu from xml
                    popup.getMenuInflater().inflate(R.menu.address_change_menu, popup.getMenu());
                    // Setup menu item selection
                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        public boolean onMenuItemClick(MenuItem item) {
                            switch (item.getItemId()) {
                                case R.id.menu_change:
                                    Intent lauchAddressListIntent=new Intent(getApplicationContext(), AddressListActivity.class);
                                    lauchAddressListIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK);
                                    lauchAddressListIntent.putExtra(AppConstants.CARTINFOLIST, cartInfoLst);
                                    lauchAddressListIntent.putExtra("intentfrom","orderdetails");
                                    startActivity(lauchAddressListIntent);

                                default:
                                    return false;
                            }
                        }
                    });
                    popup.show();
                    break;



            }
        }
    };
    class RegistrationBroadCastReceiver extends BroadcastReceiver {
        RegistrationBroadCastReceiver() {
        }

        public void onReceive(Context context, Intent intent) {
            Toast.makeText(getApplicationContext(),"Receiver calling",Toast.LENGTH_LONG).show();
        }
    }
    public void bookWaterCan(List<CartInfo> cartInfoList,final SweetAlertDialog sDialog){
        try
        {
            String locaitonDetails= this.sharedPreferences.getString(EmptycanDataBase.USER_LOC_LAT_LANG,locDetails.toString());
            JsonParser jsonParser=new JsonParser();
             JsonObject bookingLocation= jsonParser.parse(locaitonDetails).getAsJsonObject();
          //  bookingLocation.remove("time");

            Retrofit retrofit =null;
            WebServiceInterface webServiceInterface=null;
            Call<JsonObject> call=null;
            retrofit = RetrofitAdaptor.getRetrofit();
            webServiceInterface = retrofit.create(WebServiceInterface.class);
            OrderInfo orderInfo =new OrderInfo();
            orderInfo.setOrderAddressId(addressServerKey);
            orderInfo.setCartInfoList(cartInfoList);
            orderInfo.setConsumerKey(new EmptycanDataBase(getApplicationContext()).getConsumerKey());
            orderInfo.setPaymentMode(AppConstants.PAYMENT_CASH_MODE);
            orderInfo.setTotalPayment(Float.parseFloat(String.valueOf(orderInfoCardView.payableAmout)));
            orderInfo.setIsFirstTimeBooking(firstTimeBooking);
            orderInfo.setRefStatus(refStatus);
            orderInfo.setUserFcmId(userFcmId);
            orderInfo.setOrderStatus(EmptycanDataBase.ORDER_INITIATED);
                if(bookingLocation.entrySet().size()!=0){
                    orderInfo.setLatitude(bookingLocation.get("latitude").getAsString());
                    orderInfo.setLongitude(bookingLocation.get("longitude").getAsString());
                    orderInfo.setAltitude(bookingLocation.get("altitude").getAsString());
                    orderInfo.setAccuracy(bookingLocation.get("accuracy").getAsString());
                    orderInfo.setBearing(bookingLocation.get("bearing").getAsString());

                   orderInfo.setGpsProvider(bookingLocation.get("provider").getAsString());

                    orderInfo.setSpeed(bookingLocation.get("speed").getAsString());
                    orderInfo.setTime(bookingLocation.get("time").getAsString());
                    orderInfo.setDescribeContents(bookingLocation.get("describeContents").getAsString());
                }

            // JsonObject orderInfoJsonObject= new  AppUtils(getApplicationContext()).getJSONObject(orderInfo);
            ObjectMapper mapper = new ObjectMapper();
            String orderInfoStrObject= mapper.writeValueAsString(orderInfo);
            JsonParser parser = new JsonParser();
            JsonObject orderInfoJsonObject=  parser.parse(orderInfoStrObject).getAsJsonObject();
            orderInfoJsonObject.addProperty("distibutorId",defaultDistributorId);
            call = webServiceInterface.consumerNewOrder(orderInfoJsonObject);
            call.enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(Response<JsonObject> response, Retrofit retrofit) {
                    if (response.message().equals("OK")) {
                        //launchBookCanFormActivity();
                        JsonObject result= response.body().getAsJsonObject();
                        if(result.get("status").getAsString().equals("success")){
                           /* sDialog.setTitleText("Order Placed!")
                                    .setContentText("Your order successfully placed.")
                                    .setConfirmText("OK")
                                    .showCancelButton(false)
                                    .setCancelClickListener(null)
                                    .setConfirmClickListener(null)
                                    .changeAlertType(SweetAlertDialog.SUCCESS_TYPE);*/
                            lauchBookCanFormActivity("Your Booking Successfull");
                            //launchBookCanFormActivity("NOHISTROY");
                            //launchBookCanFormActivity();
                        }


                    } else {
                        //launchBookCanFormActivity();
                        //request not successful (like 400,401,403 etc)
                        //Handle errors
                        lauchBookCanFormActivity("Your Booking failled");
                    }
                    progressDialog.dismiss();
                    bookingConfirmationDlg.dismiss();
                }

                @Override
                public void onFailure(Throwable t) {
                    JsonObject jsonObject = new JsonObject();
                    jsonObject.addProperty("result_type", t.getMessage());
                    jsonObject.addProperty("result", t.getMessage());
                    progressDialog.dismiss();
                    bookingConfirmationDlg.dismiss();
                    //launchBookCanFormActivity();
                    //((SignupActivity)signUpContext).successNotice(jsonObject);
                    // SignupActivity signupActivityy = new SignupActivity();

                }
            });
        }catch(Exception e){
            progressDialog.dismiss();
            bookingConfirmationDlg.dismiss();
            e.printStackTrace();
        }

    }
    public void lauchBookCanFormActivity(String status){
        SweetAlertDialog progressDialog = new SweetAlertDialog(OrderDetailsActivity.this, SweetAlertDialog.WARNING_TYPE);
        // progressDialog.setProgressStyle(0);
        //progressDialog.setMessage("Loading...");
        progressDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));

        progressDialog.setTitleText(status);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
        progressDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(SweetAlertDialog sDialog) {
                sDialog.dismiss();
                launchBookCanFormActivity("NOHISTROY");

            }
        });
    }
    public void launchBookCanFormActivity(String flag){
        if(firstTimeBooking.equals("NOT_YET")){
            SharedPreferences.Editor editor= this.sharedPreferences.edit();
            editor.putString(EmptycanDataBase.IS_COMPLETED_FIRST_BOOKING,"BOOKED");
            editor.commit();
        }
        bookingConfirmationDlg.dismiss();
        Intent orderDetailActivity = new Intent(this,BookCanFormActivity.class);
        cartInfoLst=new ArrayList<CartInfo>();
        orderDetailActivity.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK);
        if(flag.equals("NOHISTROY"))
        orderDetailActivity.putExtra(AppConstants.CARTINFOLIST, cartInfoLst);
        else
         orderDetailActivity.putExtra(AppConstants.CARTINFOLIST, cartInfoLst);
        startActivity(orderDetailActivity);
        //finish();
    }
 public void showBookingConfirmationDialog(){
     bookingConfirmationDlg=  new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE);
     bookingConfirmationDlg .setTitleText("Are you sure?");
     bookingConfirmationDlg .setContentText("please choose your  confirmation");
     bookingConfirmationDlg .setCancelText("No,Cancel Order!");
     bookingConfirmationDlg.setConfirmText("Yes,Place the Order !");
     bookingConfirmationDlg.showCancelButton(true);
     bookingConfirmationDlg .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                 @Override
                 public void onClick(SweetAlertDialog sDialog) {
                    // bookingConfirmationDlg.dismiss();
                     // reuse previous dialog instance, keep widget user state, reset them if you need
                     sDialog.setTitleText("Order Cancelled!")
                             .setContentText("Your Order Cancelled :)")
                             .setConfirmText("OK")
                             .showCancelButton(false)
                             .setCancelClickListener(null)
                             .setConfirmClickListener(null)
                             .changeAlertType(SweetAlertDialog.ERROR_TYPE);
                     launchBookCanFormActivity("NOHISTROY");
                     // or you can new a SweetAlertDialog to show
                               /* sDialog.dismiss();
                                new SweetAlertDialog(SampleActivity.this, SweetAlertDialog.ERROR_TYPE)
                                        .setTitleText("Cancelled!")
                                        .setContentText("Your imaginary file is safe :)")
                                        .setConfirmText("OK")
                                        .show();*/
                 }
             });
     bookingConfirmationDlg.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                 @Override
                 public void onClick(SweetAlertDialog sDialog) {
                     bookingConfirmationDlg.dismiss();
                     progressDialog = new SweetAlertDialog(OrderDetailsActivity.this,SweetAlertDialog.PROGRESS_TYPE);
                     // progressDialog.setProgressStyle(0);
                     //progressDialog.setMessage("Loading...");
                     progressDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
                     progressDialog.setTitleText("Loading");
                     progressDialog.setCanceledOnTouchOutside(false);
                     progressDialog.show();
                     bookWaterCan(cartInfoLst,sDialog);

                 }
             }) .show();
 }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        //startActivity(new Intent(NotificationListActivity.this, BookCanFormActivity.class));
        Intent orderDetailActivity = new Intent(this,BookCanFormActivity.class);
        orderDetailActivity.putExtra(AppConstants.CARTINFOLIST, cartInfoLst);
        orderDetailActivity.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK);
      /*  addressIntent.putExtra("street_name",spinner_location);

        addressIntent.putExtra("address_map",addressMap);*/
        //startMain.addCategory(Intent.CATEGORY_HOME);
        // startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(orderDetailActivity);
        //finish();
    }
    @Override
    protected void onDestroy() {
        clearReferences();
        super.onDestroy();
    }
    private void clearReferences(){
        Activity currActivity = appConfig.getCurrentActivity();
        if (this.equals(currActivity))
            appConfig.setCurrentActivity(null);
    }
    @Override
    protected void onResume() {
        if(appConfig!=null)
        appConfig.setCurrentActivity(this);
        LocalBroadcastManager.getInstance(this).registerReceiver(this.mRegistrationBroadcastReceiver, new IntentFilter(AppConstants.REGISTRATION_COMPLETE));
        super.onResume();

    }
    @Override
    public void onStart() {
        super.onStart();
        if(this.mGoogleApiClient!=null)
        this.mGoogleApiClient.connect();
        Log.d(TAG, "onStart fired ..............");

    }

    @Override
    public void onStop() {
        super.onStop();
        if(this.mGoogleApiClient!=null)
        this.mGoogleApiClient.disconnect();
        Log.d(TAG, "onStop fired ..............");
    }
    @Override
    protected void onPause() {
        clearReferences();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(this.mRegistrationBroadcastReceiver);
        super.onPause();
    }
    protected void startLocationUpdates() {
        if (ContextCompat.checkSelfPermission(this, "android.permission.ACCESS_FINE_LOCATION") == 0 || ContextCompat.checkSelfPermission(this, "android.permission.ACCESS_COARSE_LOCATION") == 0) {
            if(!this.mGoogleApiClient.isConnected()){
                this.mGoogleApiClient = new GoogleApiClient.Builder(this).addApi(LocationServices.API).addConnectionCallbacks(this).addOnConnectionFailedListener(this).build();
            }else{
                if(this.mLocationRequest==null){
                    createLocationRequest();
                    isLocReqCalled=false;
                }
                PendingResult<Status> pendingResult = LocationServices.FusedLocationApi.requestLocationUpdates(this.mGoogleApiClient, this.mLocationRequest, (LocationListener) this);
                LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                lm.requestLocationUpdates(LocationManager.GPS_PROVIDER,1000,10,this);
                Log.d(TAG, "Location update started ..............: ");
            }

        }else{
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION},
                    MY_PERMISSIONS_REQUEST_READ_LOCATION);
           // getLocation();

        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CHECK_SETTINGS:
                switch (resultCode) {
                    case Activity.RESULT_OK://user press ok to on the GPS
                        // setUpBookWaterCanMainPage();
                        startLocationUpdates();
                        break;
                    // }
                    case Activity.RESULT_CANCELED://user press cancel to on the GPS
                        Toast.makeText(this, "We need the permission to get location",
                                Toast.LENGTH_SHORT).show();
                        displayLocationSettingsRequest(getApplicationContext());
                       // finish();
                        break;
                    default:
                }
            default:
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        Log.d(TAG, "Permission callback called-------");
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_LOCATION: {

                Map<String, Integer> perms = new HashMap<>();
                // Initialize the map with both permissions
                perms.put(Manifest.permission.ACCESS_COARSE_LOCATION, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.ACCESS_FINE_LOCATION, PackageManager.PERMISSION_GRANTED);
                // Fill with actual results from user
                if (grantResults.length > 0) {
                    for (int i = 0; i < permissions.length; i++)
                        perms.put(permissions[i], grantResults[i]);
                    // Check for both permissions
                    if (perms.get(Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
                            && perms.get(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        Log.d(TAG, "slocation services permission granted");
                        // process the normal flow
                        //else any one or both the permissions are not granted
                    } else {
                        Log.d(TAG, "Some permissions are not granted ask again ");
                        //permission is denied (this is the first time, when "never ask again" is not checked) so ask again explaining the usage of permission
//                        // shouldShowRequestPermissionRationale will return true
                        //show the dialog or snackbar saying its necessary and try again otherwise proceed with setup.
                        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_COARSE_LOCATION) || ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                            showDialogOK("SMS and Location Services Permission required for this app",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            switch (which) {
                                                case DialogInterface.BUTTON_POSITIVE:
                                                    startLocationUpdates();
                                                    break;
                                                case DialogInterface.BUTTON_NEGATIVE:
                                                    // proceed with logic by disabling the related features or quit the app.
                                                    Intent intent = new Intent(Intent.ACTION_MAIN);
                                                    intent.addCategory(Intent.CATEGORY_HOME);
                                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                    startActivity(intent);
                                                    break;
                                            }
                                        }
                                    });
                        }
                        //permission is denied (and never ask again is  checked)
                        //shouldShowRequestPermissionRationale will return false
                        else {
                            Toast.makeText(this, "Go to settings and enable permissions", Toast.LENGTH_LONG)
                                    .show();
                            //                            //proceed with logic by disabling the related features or quit the app.
                        }
                    }
                }
            }
        }

    }
    private void showDialogOK(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", okListener)
                .create()
                .show();
    }

}
