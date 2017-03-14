package com.meshyog.emptycan.model;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.AppConstants;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.wearable.MessageApi;


/**
 * Created by varadhan on 03-03-2017.
 */
public class GPS implements  GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener  {
    private IGPSActivity main;

    // Helper for GPS-Position
   // private LocationListener mlocListener;
    private LocationManager mlocManager;
    private static final long FASTEST_INTERVAL = 5000;
    private static final long INTERVAL = 10000;
    protected static final int REQUEST_CHECK_SETTINGS = 1;
    private boolean isRunning;
    private BroadcastReceiver mRegistrationBroadcastReceiver;
    LocationRequest mLocationRequest;
    Location mCurrentLocation;
    private GoogleApiClient mGoogleApiClient;
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
public Activity myActivity;
    public GPS(IGPSActivity main, Activity activity) {
        this.main = main;
        this.myActivity = activity;
        this.mRegistrationBroadcastReceiver = new RegistrationBroadCastReceiver();
        this.mGoogleApiClient = new GoogleApiClient.Builder(myActivity).addApi(LocationServices.API).addConnectionCallbacks(this).addOnConnectionFailedListener(this).build();
        createLocationRequest();
        // GPS Position
       // mlocManager = (LocationManager) ((Activity) this.main).getSystemService(Context.LOCATION_SERVICE);
        //mlocListener = new MyLocationListener();
        //displayLocationSettingsRequest(this.myActivity.getApplicationContext());

    }
    protected void createLocationRequest() {
        this.mLocationRequest = new LocationRequest();
        this.mLocationRequest.setInterval(INTERVAL);
        this.mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
        this.mLocationRequest.setPriority(100);
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    class RegistrationBroadCastReceiver extends BroadcastReceiver {
        RegistrationBroadCastReceiver() {
        }

        public void onReceive(Context context, Intent intent) {
           // Toast.makeText(getApplicationContext(),"Receiver calling",Toast.LENGTH_LONG).show();
        }
    }
    public boolean checkSelfPermission(){
        if (ContextCompat.checkSelfPermission(this.myActivity,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this.myActivity,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

                //Prompt the user once explanation has been shown
                ActivityCompat.requestPermissions(this.myActivity,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this.myActivity,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        } else {
            return true;
        }
    }



    private void displayLocationSettingsRequest(Context context) {
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
                   // Log.i(TAG, "All location settings are satisfied.");
                   // setUpBookWaterCanMainPage();
                    break;
                case 6  :
                    Log.i("mainActivity", "Location settings are not satisfied. Show the user a dialog to upgrade location settings ");
                    try {
                        status.startResolutionForResult(myActivity, REQUEST_CHECK_SETTINGS);
                    } catch (IntentSender.SendIntentException e) {
                       // Log.i(TAG, "PendingIntent unable to execute request.");
                    }
                    break;
                case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE :
                    //Log.i(TAG, "Location settings are inadequate, and cannot be fixed here. Dialog not created.");
                    break;
                default:
            }
        }

    }
    public void stopGPS() {
        if(isRunning) {
            //mlocManager.removeUpdates(mlocListener);
            this.mGoogleApiClient.disconnect();

            this.isRunning = false;
        }
    }

    public void onPause(){
        LocalBroadcastManager.getInstance(this.myActivity).unregisterReceiver(this.mRegistrationBroadcastReceiver);
    }
    public void resumeGPS() {
       // mlocManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, mlocListener);
        LocalBroadcastManager.getInstance(this.myActivity).registerReceiver(this.mRegistrationBroadcastReceiver, new IntentFilter(AppConstants.REGISTRATION_COMPLETE));
        this.isRunning = true;

    }
    public void onStart(){
        this.mGoogleApiClient.connect();
    }

    public boolean isRunning() {
        return this.isRunning;
    }

    public class MyLocationListener implements LocationListener {

        private final String TAG = MyLocationListener.class.getSimpleName();

        @Override
        public void onLocationChanged(Location loc) {
            GPS.this.main.locationChanged(loc.getLongitude(), loc.getLatitude());
        }

        @Override
        public void onProviderDisabled(String provider) {
            GPS.this.main.displayGPSSettingsDialog();
        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

    }

}
