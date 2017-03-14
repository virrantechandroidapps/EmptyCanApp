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
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Typeface;
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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.AppConstants;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
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
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.wearable.MessageApi;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.meshyog.emptycan.R;
import com.meshyog.emptycan.model.AppConfig;
import com.meshyog.emptycan.model.AppUtils;
import com.meshyog.emptycan.model.database.CartInfo;
import com.meshyog.emptycan.model.database.EmptycanDataBase;
import com.meshyog.emptycan.model.database.ProductInfo;
import com.meshyog.emptycan.model.server.RetrofitAdaptor;
import com.meshyog.emptycan.model.server.WebServiceInterface;
import com.meshyog.emptycan.view.CustomButtonListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

/**
 * Created by varadhan on 20-11-2016.
 */

public class BookCanFormActivity extends AppCompatActivity implements CustomButtonListener, LocationListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    protected static final int REQUEST_CHECK_SETTINGS = 1;
    private static final long FASTEST_INTERVAL = 5000;
    private static final long INTERVAL = 10000;
    private static final int MY_PERMISSIONS_REQUEST_READ_LOCATION=10;
    private static final String TAG = "BookCanFormActivity";
    public ArrayList<Integer> quantity = new ArrayList<Integer>();
    public ArrayList<CartInfo> cartInfoList = null;
    public SharedPreferences sharedPreferences;
    public RelativeLayout placeOrderBtn;
    public SweetAlertDialog progressDialog;
    public TextView orderTotalAmnt;
    public List<ProductInfo> productInfoList = null;
    protected AppConfig appConfig;
    String[] arrItems, prices;
    TypedArray images;
    LocationRequest mLocationRequest;
    Location mCurrentLocation;
    String latLong;
    private ListView listView;
    private BookWaterCanListAdapter listAdapter;
    private GoogleApiClient mGoogleApiClient;
    public static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 1;

    private View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.cartConfrmBrn:
                    removeCartWhereQtyZero(cartInfoList);
                    if (cartInfoList != null && cartInfoList.size() != 0) {

                        moveToOrderDisplayActivity(cartInfoList);
                    } else {
                        Toast.makeText(getApplicationContext(), "please make atleast one order", Toast.LENGTH_SHORT).show();
                    }
                    break;

                case R.id.already_registered_user:
                    onBackPressed();
                    break;

            }
        }
    };

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.meshyog.emptycan.R.layout.book_water_can_activity);
       // this.mRegistrationBroadcastReceiver = new RegistrationBroadCastReceiver();
        setUpBookWaterCanMainPage();
        this.mGoogleApiClient = new GoogleApiClient.Builder(this).addApi(LocationServices.API).addConnectionCallbacks(this).addOnConnectionFailedListener(this).build();
        this.sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        this.appConfig = (AppConfig) this.getApplicationContext();
            createLocationRequest();
            updateUI();
       // setUpBookWaterCanMainPage();
        System.out.print("calling");





    }

    public void setUpBookWaterCanMainPage() {
        try {

            arrItems = getResources().getStringArray(R.array.fruitName);
            images = getResources().obtainTypedArray(R.array.fruitImages);
            prices = getResources().getStringArray(R.array.Price);
            listView = (ListView) findViewById(R.id.customListView);
            orderTotalAmnt = (TextView) findViewById(R.id.ordrAmount);
            orderTotalAmnt.setText("Rs 0.O");
            placeOrderBtn = (RelativeLayout) findViewById(R.id.cartConfrmBrn);
            placeOrderBtn.setOnClickListener(listener);
            Intent mainIntent = getIntent();
            cartInfoList = new ArrayList<CartInfo>();
            if (mainIntent != null) {
                ArrayList<CartInfo> cartInfoLst = (ArrayList<CartInfo>) mainIntent.getSerializableExtra(AppConstants.CARTINFOLIST);
                if (cartInfoLst != null && cartInfoLst.size() > 0) {
                    cartInfoList = cartInfoLst;

                }

            }
            productInfoList = getDefaultProudctList();
            updateCartDetails();
            listAdapter = new BookWaterCanListAdapter(this, productInfoList);
            listView.setAdapter(listAdapter);
            listAdapter.setCustomButtonListener(this);
            listAdapter.notifyDataSetChanged();
        } catch (Exception e) {
            System.out.print(e.getMessage());
            e.printStackTrace();
        }
    }

    public void removeCartWhereQtyZero(ArrayList<CartInfo> cartList) {
        try {
            int size = cartList.size();
            int nativeSize = cartInfoList.size();
            if (size == nativeSize) {
                for (int i = 0; i < size; i++) {
                    if (cartList.get(i).getCartPrdctUnit() == 0) {
                        int index = cartInfoList.indexOf(cartList.get(i));
                        cartInfoList.remove(index);
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    public void moveToOrderDisplayActivity(ArrayList<CartInfo> cartInfoLst) {
        String addressInfo = this.sharedPreferences.getString("defaultAddress", "");
        if (addressInfo != null && !addressInfo.equals("")) {
            Intent orderDetailActivity = new Intent(this, OrderDetailsActivity.class);
            orderDetailActivity.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK);
           // orderDetailActivity.setFlags(orderDetailActivity.FLAG_ACTIVITY_NO_HISTORY);
            orderDetailActivity.putExtra(AppConstants.CARTINFOLIST, cartInfoLst);
            startActivity(orderDetailActivity);
            //finish();
        } else {
            String string = "No Delivery Address Found";
            //string = string.replace("\\\n", System.getProperty("line.separator"));

            progressDialog = new SweetAlertDialog(BookCanFormActivity.this, SweetAlertDialog.WARNING_TYPE);
            // progressDialog.setProgressStyle(0);
            //progressDialog.setMessage("Loading...");
            progressDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));

            progressDialog.setTitleText(string);
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();
            progressDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                @Override
                public void onClick(SweetAlertDialog sDialog) {
                    sDialog.dismiss();
                    launchAddressFormActivity();

                }
            });
        }

        // finish();
    }


    public void launchAddressFormActivity() {
        Intent orderDetailActivity = new Intent(this, AddressFormActivity.class);
        startActivity(orderDetailActivity);
        //finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_myProfile_list) {
            Intent intent = new Intent(this, UserProfileActivity.class);
            startActivity(intent);
        } else if (id == R.id.action_orders_list) {
            Intent intent = new Intent(this, MyOrdersEndlessListActivity.class);
            startActivity(intent);
        } else if (id == R.id.action_notification_list) {
            Intent intent = new Intent(this, NotificationListActivity.class);
            startActivity(intent);
        } else if (id == R.id.action_address_list) {
            Intent intent = new Intent(this, AddressFormActivity.class);
            startActivity(intent);
        } else if (id == R.id.action_sign_out) {
            appConfig.clearAllSharedPrefInfo();
            Intent intent = new Intent(this, UserSignInActivity.class);
            startActivity(intent);
            finish();
        } else if (id == R.id.referralform) {
            Intent intent = new Intent(this, ReferralFormActivity.class);
            startActivity(intent);

        } else if (id == R.id.action_address_list1) {
            Intent intent = new Intent(this, AddressListActivity.class);
            startActivity(intent);

        }


        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private void clearReferences() {
        Activity currActivity = appConfig.getCurrentActivity();
        if (this.equals(currActivity))
            appConfig.setCurrentActivity(null);
    }

    @Override
    protected void onResume() {
        appConfig.setCurrentActivity(this);
        //LocalBroadcastManager.getInstance(this).registerReceiver(this.mRegistrationBroadcastReceiver, new IntentFilter(AppConstants.REGISTRATION_COMPLETE));
        super.onResume();

    }

    @Override
    protected void onPause() {
        clearReferences();
        // LocalBroadcastManager.getInstance(this).unregisterReceiver(this.mRegistrationBroadcastReceiver);
        super.onPause();
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "onStart fired ..............");
        this.mGoogleApiClient.connect();

    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "onStop fired ..............");
        this.mGoogleApiClient.disconnect();
    }

    @Override
    protected void onDestroy() {
        clearReferences();
        super.onDestroy();
    }

    public List<ProductInfo> getDefaultProudctList() {
        List<ProductInfo> productInfoList = new ArrayList<ProductInfo>();

        try {
            if(this.sharedPreferences==null){
                this.sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            }
            String productsString = this.sharedPreferences.getString(EmptycanDataBase.PRODUCT_LIST, "");
            if(!productsString.equals("")){
                JsonParser jsonParser = new JsonParser();
                productInfoList = convertProductsArrayToList(jsonParser.parse(productsString).getAsJsonArray());
            }

           /* ProductInfo productInfo1=new ProductInfo();
            productInfo1.setProductId(545193);
            productInfo1.setProductName("Aqua water Can 1");
            productInfo1.setProductPrice(35);
            productInfo1.setProductQty(0);
            productInfo1.setProductQtyType("LTR");
            productInfo1.setProductImgUrl("http://virranbi.appspot.com/images/rwateracannn.png");
            productInfoList.add(productInfo1);

            ProductInfo productInfo2=new ProductInfo();
            productInfo2.setProductId(545194);
            productInfo2.setProductName("Aqua water can 2 ");
            productInfo2.setProductPrice(65);
            productInfo2.setProductQty(0);
            productInfo2.setProductQtyType("LTR");
            productInfo2.setProductImgUrl("http://virranbi.appspot.com/images/rwateracannn.png");
            productInfoList.add(productInfo2);

            ProductInfo productInfo3=new ProductInfo();
            productInfo3.setProductId(545195);
            productInfo3.setProductName("Aqua water can 3");
            productInfo3.setProductPrice(45);
            productInfo3.setProductQty(0);
            productInfo3.setProductQtyType("LTR");
            productInfo3.setProductImgUrl("http://virranbi.appspot.com/images/rwateracannn.png");

            productInfoList.add(productInfo3);;*/
        } catch (Exception e) {
            e.printStackTrace();
        }
        return productInfoList;
    }

    public void updateCartDetails() {
        if (cartInfoList != null && cartInfoList.size() > 0) {
            int size = productInfoList.size();
            float totalAmntFloat=0.0f;
            for (int i = 0; i < size; i++) {
                ProductInfo procudtInfo = productInfoList.get(i);
                long productId = procudtInfo.getProductId();
                CartInfo cartInfo = new CartInfo(productId);
                int index = cartInfoList.indexOf(cartInfo);
                if (index != -1) {
                    cartInfo = cartInfoList.get(index);
                    procudtInfo.setProductUnits(cartInfo.getCartPrdctUnit());
                    totalAmntFloat += cartInfo.getCartPrdctUnit() * cartInfo.getCartPrdctPrice();
                    productInfoList.remove(i);
                    productInfoList.add(i, procudtInfo);
                }
            }
            updateAmoutnInBtn(String.valueOf(totalAmntFloat));

        }


    }

    @Override
    public void onButtonClickListener(int position, TextView editText, int value) {
        /*
        View view = listView.getChildAt(position);*/
        int quantity = Integer.parseInt(editText.getText().toString());
        quantity = quantity + 1 * value;
        if (quantity < 0)
            quantity = 0;
        editText.setText(quantity + "");
    }

    public ArrayList<ProductInfo> convertProductsArrayToList(JsonArray productArray) {
        ArrayList<ProductInfo> productInfoList = new ArrayList<>();
        try {
            if (productArray != null && productArray.size() > 0) {
                int productListSize = productArray.size();
                for (int i = 0; i < productListSize; i++) {
                    JsonObject productObj = productArray.get(i).getAsJsonObject();
                    JsonObject meta = productObj.get("id").getAsJsonObject();
                    ProductInfo productInfo = new ProductInfo();
                    productInfo.setProductId(meta.get("id").getAsLong());
                    productInfo.setProductPrice(productObj.get("productPrice").getAsFloat());
                    productInfo.setProductName(productObj.get("productName").getAsString());
                    productInfo.setProductQty(productObj.get("productQty").getAsInt());
                    productInfo.setProductUnits(0);
                    productInfo.setProductQtyType(productObj.get("productQtyType")!=null?productObj.get("productQtyType").getAsString():"");
                    productInfo.setProductImgUrl(productObj.get("productImgUrl").getAsString());
                    productInfoList.add(productInfo);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return productInfoList;
    }

    public void updateAmoutnInBtn(String amount) {
        orderTotalAmnt.setText("Rs " + amount);
    }



    @Override
    public void onLocationChanged(Location location) {
        this.mCurrentLocation = location;
        //this.latLong.equals(BuildConfig.FLAVOR)
        if (true) {
            updateUI();
        } else {
            //this.mGoogleApiClient.disconnect();
        }

    }




    public void updateUserLoc(double lat, double lang) {
        Retrofit retrofit = null;
        WebServiceInterface webServiceInterface = null;
        Call<JsonObject> call = null;
        try {
            retrofit = RetrofitAdaptor.getRetrofit();
            webServiceInterface = retrofit.create(WebServiceInterface.class);
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("locLat", String.valueOf(lat));
            jsonObject.addProperty("locLang", String.valueOf(lang));
            jsonObject.addProperty("userId", new EmptycanDataBase(getApplicationContext()).getConsumerKey());
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
                    Toast.makeText(getApplicationContext(), "" + t.getMessage(), Toast.LENGTH_SHORT).show();

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CHECK_SETTINGS:
                switch (resultCode) {
                    case Activity.RESULT_OK://user press ok to on the GPS
                        setUpBookWaterCanMainPage();
                       // startLocationUpdates();
                        break;
                    // }
                    case Activity.RESULT_CANCELED://user press cancel to on the GPS

                        Toast.makeText(this, "We need the permission to get location",
                                Toast.LENGTH_SHORT).show();
                        displayLocationSettingsRequest(getApplicationContext());
                        break;
                    default:
                }
            default:
        }
    }





    protected void createLocationRequest() {
        this.mLocationRequest = new LocationRequest();
        this.mLocationRequest.setInterval(INTERVAL);
        this.mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
        this.mLocationRequest.setPriority(100);

    }
    private void updateUI() {
        Log.d(TAG, "UI update initiated .............");
        if (this.mCurrentLocation != null) {
            this.latLong = String.valueOf(this.mCurrentLocation.getLatitude() + ":" + this.mCurrentLocation.getLongitude());
            //updateUserLoc(this.mCurrentLocation.getLatitude(), this.mCurrentLocation.getLongitude());
        } else if (!AppUtils.isGPSAvailable(this)) {
            displayLocationSettingsRequest(this);
        } else {
            setUpBookWaterCanMainPage();
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
                case LocationSettingsStatusCodes.SUCCESS:
                    Log.i(TAG, "All location settings are satisfied.");
                    setUpBookWaterCanMainPage();
                    break;
                case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                    Log.i("mainActivity", "Location settings are not satisfied. Show the user a dialog to upgrade location settings ");
                    try {
                        status.startResolutionForResult(BookCanFormActivity.this, MainActivity.REQUEST_CHECK_SETTINGS);
                    } catch (IntentSender.SendIntentException e) {
                        Log.i(TAG, "PendingIntent unable to execute request.");
                    }
                    break;
                case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                    Log.i(TAG, "Location settings are inadequate, and cannot be fixed here. Dialog not created.");
                    break;
                default:
            }
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
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        startLocationUpdates();
    }
    @Override
    public void onConnectionSuspended(int i) {

    }
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
    }
    protected void startLocationUpdates() {
        if (ContextCompat.checkSelfPermission(this, "android.permission.ACCESS_FINE_LOCATION") == 0 || ContextCompat.checkSelfPermission(this, "android.permission.ACCESS_COARSE_LOCATION") == 0) {
            PendingResult<Status> pendingResult = LocationServices.FusedLocationApi.requestLocationUpdates(this.mGoogleApiClient, this.mLocationRequest, (LocationListener) this);
           Log.d(TAG, "Location update started .........: ");
        }else{
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION},
                    MY_PERMISSIONS_REQUEST_READ_LOCATION);
           /* LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            lm.requestLocationUpdates(LocationManager.GPS_PROVIDER,1000,10,this);*/
           // getLocation();

        }
    }


    public class BookWaterCanListAdapter extends BaseAdapter {
        //public ArrayList<HashMap<String, String>> listQuantity;
        public ArrayList<Integer> quantity = new ArrayList<Integer>();
        protected AppConfig appConfig;
        TypedArray images;
        ImageLoader imageLoader = AppConfig.getInstance().getImageLoader();
        CustomButtonListener customButtonListener;
        List<ProductInfo> productInfoLst = null;
        private String[] listViewItems, prices;
        private Context context;

        // public  static List<CartInfo> cartInfoList =new ArrayList<>();
        public BookWaterCanListAdapter(Context context, List<ProductInfo> productInfos) {
            this.context = context;
            this.productInfoLst = productInfos;
            // appConfig = (AppConfig)context.getApplicationContext();

       /* this.listViewItems = listViewItems;
        this.images = images;
        this.prices=prices;*/

            for (int i = 0; i < productInfos.size(); i++) {
                quantity.add(0);

                //quantity[i]=0;
            }
        }

        public void setCustomButtonListener(CustomButtonListener customButtonListner) {
            this.customButtonListener = customButtonListner;
        }

        @Override
        public int getCount() {
            return productInfoLst.size();
        }

        @Override
        public Object getItem(int position) {
            return productInfoLst.get(position);
        }


        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            View row;
            final ListViewHolder listViewHolder;
            ProductInfo procudtInfo = productInfoLst.get(position);


            if (imageLoader == null)
                imageLoader = AppConfig.getInstance().getImageLoader();
            if (convertView == null) {
                LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                row = layoutInflater.inflate(R.layout.book_watercan_custom_list, parent, false);
                listViewHolder = new ListViewHolder();
                listViewHolder.productName = (TextView) row.findViewById(R.id.tvFruitName);
                listViewHolder.productImageView = (NetworkImageView) row.findViewById(R.id.ivFruit);
                listViewHolder.productPrice = (TextView) row.findViewById(R.id.tvFruitPrice);
                listViewHolder.btnPlus = (TextView) row.findViewById(R.id.ib_addnew);
                listViewHolder.productQuantity = (TextView) row.findViewById(R.id.editTextQuantity);
                listViewHolder.btnMinus = (TextView) row.findViewById(R.id.ib_remove);
                listViewHolder.itemQuantity=    (TextView) row.findViewById(R.id.itemQuantity);
                row.setTag(listViewHolder);
            } else {
                row = convertView;
                listViewHolder = (ListViewHolder) row.getTag();
            }


        /*listViewHolder.productImageView.setImageResource(images.getResourceId(position, -1));*/


            try {

                listViewHolder.productQuantity.setText(quantity.get(position) + procudtInfo.getProductUnits() + "");
                listViewHolder.productName.setText(procudtInfo.getProductName());
                Typeface create = Typeface.createFromAsset(getAssets(), "fonts/PlutoSansLight.ttf");
                listViewHolder.productName.setTypeface(create);
                listViewHolder.productImageView.setImageUrl(procudtInfo.getProductImgUrl(), imageLoader);
               // listViewHolder.productImageView.setImageUrl("http://virranbi.appspot.com/images/Can.png", imageLoader);
                float price = procudtInfo.getProductPrice();
                listViewHolder.productPrice.setText("\u20B9 " + String.format(Locale.getDefault(), "%.2f", price));
                listViewHolder.productId = procudtInfo.getProductId();
                listViewHolder.itemQuantity.setText(""+procudtInfo.getProductQty()+"L");
            } catch (Exception e) {
                e.printStackTrace();
            }

            listViewHolder.btnPlus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (customButtonListener != null) {
                        customButtonListener.onButtonClickListener(position, listViewHolder.productQuantity, 1);
                        quantity.set(position, quantity.get(position) + 1);
                        addToCartSet(listViewHolder);
                    }

                }
            });
            //listViewHolder.edTextQuantity.setText("0");
            listViewHolder.btnMinus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (customButtonListener != null) {
                        customButtonListener.onButtonClickListener(position, listViewHolder.productQuantity, -1);
                        if (quantity.get(position) > 0)
                            quantity.set(position, quantity.get(position) - 1);
                        addToCartSet(listViewHolder);
                    }
                }
            });

            return row;
        }

        public void addToCartSet(ListViewHolder listViewHolder) {
            CartInfo cartInfo = new CartInfo(listViewHolder.productId);
            String prodctQty = listViewHolder.productQuantity.getText().toString();
            String productPrice = AppUtils.removeSybolsFromString(listViewHolder.productPrice.getText().toString());
            cartInfo.setCartPrdctName(listViewHolder.productName.getText().toString());
            cartInfo.setCartPrdctUnit(Integer.parseInt(prodctQty));
            cartInfo.setCartPrdctPrice(Float.parseFloat(productPrice));
      /*  cartInfo.setCartPrdctOffer();
        cartInfo.setCartPrdctQty();*/


            int indexId = cartInfoList.indexOf(cartInfo);
            if (indexId != -1) {
                cartInfoList.remove(indexId);
                if (!prodctQty.equals("0"))
                    cartInfoList.add(indexId, cartInfo);
            } else
                cartInfoList.add(cartInfo);
            //System.out.print(cartInfoList);
            float totalAmntFloat = 0.0f;
            int size = cartInfoList.size();
            if (size != 0)
                for (int j = 0; j < size; j++) {
                    CartInfo cartInfoooo = cartInfoList.get(j);
                    totalAmntFloat += cartInfoooo.getCartPrdctUnit() * cartInfoooo.getCartPrdctPrice();
                    updateAmoutnInBtn(String.valueOf(totalAmntFloat));
                }
            else
                updateAmoutnInBtn(String.valueOf(0.0));
        }

        public class ListViewHolder {
            public TextView productName;
            public NetworkImageView productImageView;
            public TextView productPrice;
            public TextView btnPlus;
            public TextView btnMinus;
            public long productId;
            public TextView productQuantity;
            public TextView  itemQuantity;

        }

    }

    class RegistrationBroadCastReceiver extends BroadcastReceiver {
        RegistrationBroadCastReceiver() {
        }

        public void onReceive(Context context, Intent intent) {
            Toast.makeText(getApplicationContext(), "Receiver calling", Toast.LENGTH_LONG).show();
        }
    }






}
