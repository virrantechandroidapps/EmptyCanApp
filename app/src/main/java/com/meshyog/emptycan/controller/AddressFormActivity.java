package com.meshyog.emptycan.controller;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.annotations.JsonAdapter;
import com.meshyog.emptycan.R;
import com.meshyog.emptycan.model.AppConfig;
import com.meshyog.emptycan.model.AppUtils;
import com.meshyog.emptycan.model.ContactInterface;
import com.meshyog.emptycan.model.database.EmptycanDataBase;
import com.meshyog.emptycan.model.server.RetrofitAdaptor;
import com.meshyog.emptycan.model.server.WebServiceInterface;
import com.meshyog.emptycan.references.SpinnerAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Pattern;

import cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

/**
 * Created by varadhan on 28-11-2016.
 */
public class AddressFormActivity  extends AppCompatActivity implements ContactInterface {

    public Spinner deliveryTypeSpinner;
    public EditText  consumerFullName;
    public EditText  apartmentName;
    public EditText apartmentFloor ;
    public EditText locationName;
    public EditText streetName;
    public EditText officeName;
    public EditText officeFloor;
    public EditText hotelName;
    public EditText doorNumber;
    public EditText contactNumber;
    public Button addressButton;
    public String selectedDelvryType;
    protected AppConfig appConfig;
    public Intent locationSearchIntent;
    private static final long FASTEST_INTERVAL = 5000;
    private static final long INTERVAL = 10000;
    private static final String OUT_JSON = "/json";
    private static final String PLACES_API_BASE = "https://maps.googleapis.com/maps/api/place";
    protected static final int REQUEST_CHECK_SETTINGS = 1;
    private static final String TAG = "MainActivity";
    private static final String TYPE_AUTOCOMPLETE = "/autocomplete";
    Dialog locationDialog=null;
    public SharedPreferences sharedPreferences;
    public HashMap<String,String> addressMap;
    public String consumerLocationData;
    public SweetAlertDialog  progressDialog;
    public String defaultAddress="NO";
    protected void onCreate(Bundle savedInstanceState) {

        try{
            super.onCreate(savedInstanceState);
            setContentView(R.layout.address_form_activity);
            appConfig = (AppConfig)this.getApplicationContext();

            if(getSupportActionBar()!=null){
                getSupportActionBar().setTitle("Delivery Address");
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            }
           if( AppUtils.isNetworkAvailable(getApplicationContext())){
               locationDialog= new Dialog(this);
               locationDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
               locationSearchIntent=  new Intent(this,LocationSearchActivity.class);
               deliveryTypeSpinner = (Spinner) findViewById(R.id.livingType);
               apartmentName= (EditText) findViewById(R.id.apartment_name);
               apartmentFloor=(EditText)  findViewById(R.id.apartment_floor);
               locationName = (EditText) findViewById(R.id.location_name);
               officeName=  (EditText)  findViewById(R.id.office_name);
               officeFloor=(EditText)  findViewById(R.id.office_floor);
               hotelName=(EditText)  findViewById(R.id.hotel_name);
               doorNumber=(EditText)  findViewById(R.id.door_number);
               addressButton=(Button) findViewById(R.id.addAddressBtn);
               consumerFullName=(EditText)findViewById(R.id.consumerFullName) ;
               contactNumber=(EditText)findViewById(R.id.contactNumber) ;
               streetName=(EditText)findViewById(R.id.street_name) ;
               ArrayList<String> type=new ArrayList<String>();
               type.add("HOME");
               type.add("APARTMENT");
               type.add("OFFICE");
               type.add("HOTEL");
               SpinnerAdapter spinnerAdapter=new SpinnerAdapter(getApplicationContext(),type);
               deliveryTypeSpinner.setAdapter(spinnerAdapter);
               this.sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
               addressButton.setOnClickListener(listener);
               locationName.setOnClickListener(listener);
               Intent addressFrmMap=getIntent();
               if(addressFrmMap.getExtras()!=null && addressFrmMap.getExtras().get("address_map") != null){
                   addressMap=( HashMap<String,String> )addressFrmMap.getExtras().get("address_map");
                   consumerFullName.setText((String) addressMap.get("consumerFullName"));
                   apartmentName.setText((String) addressMap.get("apartmentName"));
                   apartmentFloor.setText((String) addressMap.get("apartmentFloor"));
                   locationName.setText((String) addressMap.get("locationName"));
                   officeName.setText((String) addressMap.get("officeName"));
                   officeFloor.setText((String) addressMap.get("officeFloor"));
                   hotelName.setText((String) addressMap.get("hotelName"));
                   doorNumber.setText((String) addressMap.get("doorNumber"));
                   contactNumber.setText((String) addressMap.get("contactNumber"));
                   streetName.setText((String) addressMap.get("streetName"));
                   deliveryTypeSpinner.setSelection(getIndex(deliveryTypeSpinner,(String)addressMap.get("selectedDelvryType")));
               }else{
                   addressMap=new HashMap<String,String>();
               }




               PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment)
                       getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);

               autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
                   @Override
                   public void onPlaceSelected(Place place) {
                       // TODO: Get info about the selected place.
                       System.out.println("Place: " + place.getName());
                   }

                   @Override
                   public void onError(Status status) {
                       // TODO: Handle the error.
                       System.out.println("An error occurred: " + status);
                   }
               });


               deliveryTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                   public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                       selectedDelvryType= (String) parent.getItemAtPosition(pos);
                       if(selectedDelvryType.equalsIgnoreCase("HOME")){
                           doorNumber.setVisibility(View.VISIBLE);

                           apartmentName.setVisibility(View.GONE);
                           apartmentFloor.setVisibility(View.GONE);
                           officeName.setVisibility(View.GONE);
                           officeFloor.setVisibility(View.GONE);
                           hotelName.setVisibility(View.GONE);
                       }else if(selectedDelvryType.equalsIgnoreCase("APARTMENT")){
                           apartmentName.setVisibility(View.VISIBLE);
                           apartmentFloor.setVisibility(View.VISIBLE);

                           doorNumber.setVisibility(View.GONE);
                           officeName.setVisibility(View.GONE);
                           officeFloor.setVisibility(View.GONE);
                           hotelName.setVisibility(View.GONE);
                       }else if(selectedDelvryType.equalsIgnoreCase("OFFICE")){
                           officeName.setVisibility(View.VISIBLE);
                           officeFloor.setVisibility(View.VISIBLE);

                           apartmentName.setVisibility(View.GONE);
                           apartmentFloor.setVisibility(View.GONE);
                           doorNumber.setVisibility(View.GONE);
                           hotelName.setVisibility(View.GONE);
                       }else if(selectedDelvryType.equalsIgnoreCase("HOTEL")){
                           hotelName.setVisibility(View.VISIBLE);

                           officeName.setVisibility(View.GONE);
                           officeFloor.setVisibility(View.GONE);
                           apartmentName.setVisibility(View.GONE);
                           apartmentFloor.setVisibility(View.GONE);
                           doorNumber.setVisibility(View.GONE);
                       }
                       //Toast.makeText(getApplicationContext(),"Position ->"+parent.getItemAtPosition(pos),Toast.LENGTH_LONG).show();
                   }
                   public void onNothingSelected(AdapterView<?> parent) {
                   }
               });
           }else{
               mAlertDaiog("Check your internet connectivity", true);
           }

        }catch(Exception e){
            e.printStackTrace();
        }


    }
    private int getIndex(Spinner spinner, String myString)
    {
        int index = 0;

        for (int i=0;i<spinner.getCount();i++){
            if (spinner.getItemAtPosition(i).toString().equalsIgnoreCase(myString)){
                index = i;
                break;
            }
        }
        return index;
    }
    private View.OnClickListener listener=new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch(view.getId()){
                case R.id.addAddressBtn:
                    boolean isValidCName=validateContactName();
                    boolean isValidPname=validateMobNo();
                    boolean isValidLocName=validateLocation();
                    boolean isValidAddressInfo=validateBasicAddress(selectedDelvryType);
                    boolean  isValidStreetNme=validateStreetnme();
                    if(!isValidCName || !isValidPname || !isValidLocName ||!isValidAddressInfo ||!isValidStreetNme){
                        return ;
                    }else{
                        showDefaultAddressConfirmationDialog();
                    }


                    break;

                case R.id.already_registered_user:
                    onBackPressed();
                    break;
                case R.id.location_name:
                    int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1;

                    try {
                        addressMap=new HashMap<String,String>();
                        addressMap.put("consumerFullName",consumerFullName.getText().toString());
                        addressMap.put("apartmentName",apartmentName.getText().toString());
                        addressMap.put("apartmentFloor",apartmentFloor.getText().toString());
                        addressMap.put("locationName", locationName.getText().toString());
                        addressMap.put("officeName",officeName.getText().toString());
                        addressMap.put("officeFloor",officeFloor.getText().toString());
                        addressMap.put("hotelName",hotelName.getText().toString());
                        addressMap.put("doorNumber",doorNumber.getText().toString());
                        addressMap.put("contactNumber",contactNumber.getText().toString());
                        addressMap.put("streetName",streetName.getText().toString());
                        addressMap.put("selectedDelvryType",selectedDelvryType);
                        locationSearchIntent.putExtra("address_map",addressMap);
                        setUpContact();

                        startActivity(locationSearchIntent);
                        finish();
                       // startActivityForResult(locationSearchIntent, PLACE_AUTOCOMPLETE_REQUEST_CODE);
                    } catch (Exception e) {
                        // TODO: Handle the error.
                        e.printStackTrace();
                    }
                    break;


            }
        }
    };
    public boolean validateBasicAddress(String livingType){
        //consumerFullName
        try{
            if(livingType.equals("HOME")){
              String doorNo=  doorNumber.getText().toString();
                if(doorNo.isEmpty()){
                    doorNumber.setError("Please enter door No");
                    requestFocus(this.doorNumber);
                    return false;
                }
            }else if(livingType.equalsIgnoreCase("APARTMENT")){
                String apartName=apartmentName.getText().toString();
                String apartFlr=apartmentFloor.getText().toString();


                if(apartName.isEmpty()){
                    apartmentName.setError("Please enter Apartment Name");
                    requestFocus(this.apartmentName);
                    return false;
                }
                if(apartFlr.isEmpty()){
                    apartmentFloor.setError("Please enter Apartment Floor No");
                    requestFocus(this.apartmentFloor);
                    return false;
                }
            }else if(livingType.equalsIgnoreCase("HOTEL")){
                String hotelNme=hotelName.getText().toString();



                if(hotelNme.isEmpty()){
                    hotelName.setError("Please enter Hotel Name");
                    requestFocus(this.hotelName);
                    return false;
                }

            }else if(livingType.equalsIgnoreCase("OFFICE")){
                String officeNme=officeName.getText().toString();
                String officeFlr=officeFloor.getText().toString();

                if(officeNme.isEmpty()){
                    officeName.setError("Please enter Office Name");
                    requestFocus(this.officeName);
                    return false;
                }
                if(officeFlr.isEmpty()){
                    officeFloor.setError("Please enter Office Floor No");
                    requestFocus(this.officeFloor);
                    return false;
                }
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return true;
    }
    private boolean validateContactName(){
        String consumerContactName=  consumerFullName.getText().toString();
        if(consumerContactName.length() < 4 || consumerContactName.equals("")){
            consumerFullName.setError("Contact Name Atleast 4 chracters");
            requestFocus(this.consumerFullName);
            return false;
        }

        return true;
    }
    private boolean validateMobNo() {
        String phoneNumber=  contactNumber.getText().toString();
        if (phoneNumber.trim().isEmpty()) {
            this.contactNumber.setError("Enter Valid number");
            requestFocus(this.contactNumber);
            return false;
        }
        // this.cnsumrPhoneNo.setErrorEnabled(false);
        return true;
    }
    private boolean validateStreetnme() {
        String streetNme=  streetName.getText().toString();
        if (streetNme.trim().isEmpty()) {
            this.streetName.setError("Enter Street Name");
            requestFocus(this.streetName);
            return false;
        }
        // this.cnsumrPhoneNo.setErrorEnabled(false);
        return true;
    }
    private boolean validateLocation() {
        String locationValue=  locationName.getText().toString();
        String locaitonMetaData= this.sharedPreferences.getString("location_meta_data","no meta data");
        locaitonMetaData.split(Pattern.quote("||"));
        if (locationValue.trim().isEmpty()) {
            this.locationName.setError("Enter Valid Location");
            requestFocus(this.locationName);
            return false;
        }
        // this.cnsumrPhoneNo.setErrorEnabled(false);
        return true;
    }
    public void setUpContact(){
        //LocationSearchActivity locationSearchActionBarActivity=new LocationSearchActivity();
        LocationSearchActivity.setContact(this);
    }
    public void saveUserAddress(final String isDefault){
        progressDialog = new SweetAlertDialog (AddressFormActivity.this,SweetAlertDialog.PROGRESS_TYPE);
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
                //JsonObject addConsumerAddress=new JsonObject()
                String cFullName = consumerFullName.getText().toString();
                String liviingType = (String)deliveryTypeSpinner.getSelectedItem();
                String  apartName = (String)apartmentName.getText().toString();
                String apartFloor= apartmentFloor.getText().toString();
                String strtName= streetName.getText().toString();
                String oficeName= officeName.getText().toString();
                String oficeFloor= officeFloor.getText().toString();
                String hotlName= hotelName.getText().toString();
                String dorNumber= doorNumber.getText().toString();
                //String contactNumber=contactNumber.get
                String cNumber=  contactNumber.getText().toString();
                String addressJson="";
                JsonObject addConsumerAddress=new JsonObject();
                consumerLocationData=   this.sharedPreferences.getString("location_meta_data","no meta data");
                JsonParser jsonParser=new JsonParser();
                JsonObject addressObj= jsonParser.parse(consumerLocationData).getAsJsonObject();
                if (addressObj.has("status")) {
                    addConsumerAddress.addProperty("locationName",addressObj.get("locationName").getAsString());
                }else{
                    addConsumerAddress.addProperty("locationName",addressObj.get("locationName").getAsString());
                    addConsumerAddress.addProperty("consumerLocLat",addressObj.get("Latitude").getAsString());
                    addConsumerAddress.addProperty("consumerLocLang",addressObj.get("Longitude").getAsString());
                    addConsumerAddress.addProperty("consumerAreaName",addressObj.get("SubArea").getAsString());
                    addConsumerAddress.addProperty("consumerCity",addressObj.get("District").getAsString());
                    addConsumerAddress.addProperty("state",addressObj.get("State").getAsString());
                    addConsumerAddress.addProperty("countryCode",addressObj.get("CountryCode").getAsString());
                    addConsumerAddress.addProperty("premises",addressObj.get("Premises").getAsString());
                    addConsumerAddress.addProperty("countryCode",addressObj.get("CountryCode").getAsString());
                    addConsumerAddress.addProperty("district",addressObj.get("District").getAsString());
                    addConsumerAddress.addProperty("postalCode",addressObj.get("PostalCode").getAsString());
                    addConsumerAddress.addProperty("subArea",addressObj.get("SubArea").getAsString());

                }
                if(selectedDelvryType.equalsIgnoreCase("HOME")){
                   // addressJson="{\"door_number\":\" "+dorNumber+" \", \"location_name\":\" "+strtName+" \",\"location_meta\":\" "+consumerLocationData+" \"}";
                    addConsumerAddress.addProperty("doorNumber",dorNumber);
                }else if(selectedDelvryType.equalsIgnoreCase("APARTMENT")){
                   // addressJson="{\"apartment_name\":\" "+apartName+" \", \"apartment_floor\":\" "+apartFloor+" \",\"location_name\":\" "+strtName+" \",\"location_meta\":\" "+consumerLocationData+" \"}";
                    addConsumerAddress.addProperty("apartmentName",apartName);
                    addConsumerAddress.addProperty("apartmentFloor",apartFloor);
                }else if(selectedDelvryType.equalsIgnoreCase("OFFICE")){
                   // addressJson="{\"office_name\":\" "+oficeName+" \", \"office_floor\":\" "+oficeFloor+" \",\"location_name\":\" "+strtName+" \",\"location_meta\":\" "+consumerLocationData+" \"}";
                    addConsumerAddress.addProperty("officeName",oficeName);
                    addConsumerAddress.addProperty("officeFloor",oficeFloor);
                }else if(selectedDelvryType.equalsIgnoreCase("HOTEL")){
                    //addressJson="{\"hotel_name\":\" "+hotlName+" \",\"location_name\":\" "+strtName+" \",\"location_meta\":\" "+consumerLocationData+" \"}";
                    addConsumerAddress.addProperty("hotelName",hotlName);

                }

                //addConsumerAddress.addProperty("addressHeader"]="TEST HEADER";
                addConsumerAddress.addProperty("consumerFullName",cFullName);
                addConsumerAddress.addProperty("consumerHouseNo",dorNumber);
                addConsumerAddress.addProperty("livingType",selectedDelvryType);
                //addConsumerAddress.addProperty("addressAsJson",addressJson);
                addConsumerAddress.addProperty("consumerProfileId",new EmptycanDataBase(getApplicationContext()).getConsumerKey());
                addConsumerAddress.addProperty("defaultAddress",defaultAddress);
                addConsumerAddress.addProperty("consumerStreetName",strtName);
                addConsumerAddress.addProperty("contactNumber",cNumber);

                Retrofit retrofit =null;
                WebServiceInterface webServiceInterface=null;
                Call<JsonObject> call=null;

                retrofit = RetrofitAdaptor.getRetrofit();
                webServiceInterface = retrofit.create(WebServiceInterface.class);
                call = webServiceInterface.addConsumerAddress(addConsumerAddress);
                call.enqueue(new Callback<JsonObject>() {
                    @Override
                    public void onResponse(Response<JsonObject> response, Retrofit retrofit) {
                        if (response.message().equals("OK")) {
                            JsonObject result= response.body().getAsJsonObject();
                            if(result.get("status").getAsString().equals("success")){
                                 long serverKey=result.get("server_address_key").getAsLong();
                                SharedPreferences.Editor editor= sharedPreferences.edit();
                                if(isDefault.equals("YES")){
                                    JsonObject defaultAddressMap=new JsonObject();
                                    defaultAddressMap.addProperty("fullName",consumerFullName.getText().toString());
                                    defaultAddressMap.addProperty("locationName",locationName.getText().toString());
                                    defaultAddressMap.addProperty("contactNumber",contactNumber.getText().toString());
                                    defaultAddressMap.addProperty("addressServerKey",serverKey);
                                    editor.putString(EmptycanDataBase.DEFAULT_ADDRESS,defaultAddressMap.toString());
                                    editor.commit();
                                }else{
                                    JsonObject defaultAddressMap=new JsonObject();
                                    defaultAddressMap.addProperty("fullName",consumerFullName.getText().toString());
                                    defaultAddressMap.addProperty("locationName",locationName.getText().toString());
                                    defaultAddressMap.addProperty("contactNumber",contactNumber.getText().toString());
                                    defaultAddressMap.addProperty("addressServerKey",serverKey);
                                    editor.putString("nodefaultAddress",defaultAddressMap.toString());
                                    editor.commit();
                                }


                                Toast.makeText(getApplicationContext(),"Address Saved successfully",Toast.LENGTH_SHORT).show();

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


   @Override
    public void result(String locationAddress) {
      //  Toast.makeText(getApplicationContext()," data from value"+data,Toast.LENGTH_SHORT).show();
       SharedPreferences.Editor editor= this.sharedPreferences.edit();
       editor.putString("location_meta_data",locationAddress);
       editor.commit();
        consumerLocationData=locationAddress;
    }
    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(5);
        }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) // Press Back Icon
        {
            onBackPressed();
        }

        return super.onOptionsItemSelected(item);
    }

    class AddressFormDialogListner implements DialogInterface.OnClickListener {
        final /* synthetic */ boolean val$isBack;

        AddressFormDialogListner(boolean z) {
            this.val$isBack = z;
        }

        public void onClick(DialogInterface arg0, int arg1) {
            if (this.val$isBack) {
                AddressFormActivity.this.finish();
            }
        }
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
        startActivity(addressListIntent);
        finish();
    }
   /* @Override
    public void onBackPressed() {
        super.onBackPressed();
        //startActivity(new Intent(NotificationListActivity.this, BookCanFormActivity.class));
        Intent startMain = new Intent(this,UserProfileActivity.class);
        //startMain.addCategory(Intent.CATEGORY_HOME);
        // startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(startMain);
        finish();
    }*/

    public void showDefaultAddressConfirmationDialog(){
        new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                .setTitleText("Set it as Default Address?")
                .setContentText("Your Orders  Delivered in Default Address Only ")
                .setCancelText("No")
                .setConfirmText("Yes,Make it as Default")
                .showCancelButton(true)
                .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        // reuse previous dialog instance, keep widget user state, reset them if you need
                        sDialog.setTitleText("This Address is not your default one")
                                .setContentText("")
                                .setConfirmText("OK")
                                .showCancelButton(false)
                                .setCancelClickListener(null)
                                .setConfirmClickListener(null)
                                .changeAlertType(SweetAlertDialog.ERROR_TYPE);
                        defaultAddress="NO";
                        sDialog.dismiss();
                        saveUserAddress("NO");
                        // or you can new a SweetAlertDialog to show
                               /* sDialog.dismiss();
                                new SweetAlertDialog(SampleActivity.this, SweetAlertDialog.ERROR_TYPE)
                                        .setTitleText("Cancelled!")
                                        .setContentText("Your imaginary file is safe :)")
                                        .setConfirmText("OK")
                                        .show();*/
                    }
                })
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                      ///  bookWaterCan(cartInfoLst);
                        sDialog.setTitleText("Address Saved successfully")
                                .setContentText("")
                                .setConfirmText("OK")
                                .showCancelButton(false)
                                .setCancelClickListener(null)
                                .setConfirmClickListener(null)
                                .changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
                        sDialog.dismiss();
                        defaultAddress="YES";
                        saveUserAddress("YES");

                    }
                })
                .show();
    }

}
