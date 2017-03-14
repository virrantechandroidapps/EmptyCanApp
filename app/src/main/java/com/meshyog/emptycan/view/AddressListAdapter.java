package com.meshyog.emptycan.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v7.widget.PopupMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.AppConstants;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.meshyog.emptycan.R;
import com.meshyog.emptycan.controller.AddressFormActivity;
import com.meshyog.emptycan.controller.AddressListActivity;
import com.meshyog.emptycan.controller.BookCanFormActivity;
import com.meshyog.emptycan.controller.OrderDetailsActivity;
import com.meshyog.emptycan.model.database.CartInfo;
import com.meshyog.emptycan.model.database.EmptycanDataBase;
import com.meshyog.emptycan.model.server.RetrofitAdaptor;
import com.meshyog.emptycan.model.server.WebServiceInterface;

import org.json.JSONObject;

import java.util.ArrayList;

import cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

/**
 * Created by varadhan on 16-12-2016.
 */
public class AddressListAdapter extends BaseAdapter {
    //public Context context;
    private JsonArray consumerAddressArray;
    private Activity activity;
    private LayoutInflater inflater;
    public  ViewHolder viewHolderForAction=null;
    public ViewHolder viewHolder=null;
    SweetAlertDialog bookingConfirmationDlg=null;
    SweetAlertDialog progressDialog=null;
    public SharedPreferences sharedPreferences;
    public static String intentFrom="";
    ArrayList<CartInfo> cartInfoLst;
    public AddressListAdapter (Activity contextParam, JsonArray consumerAddressParam,String intentFrom,ArrayList<CartInfo> cartInfoLstParam){
        this.activity=contextParam;
        this.consumerAddressArray=consumerAddressParam;
        this.sharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity);
        this.intentFrom=intentFrom;
        this.cartInfoLst=cartInfoLstParam;
    }


    @Override
    public int getCount() {
        return consumerAddressArray.size();
        //return 3;

    }

    @Override
    public Object getItem(int position) {
        return consumerAddressArray.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

try{
    ImageView addressActionView=null;
    TextView addressHeader=null;
    TextView phoneNo=null;
    TextView addressPart1=null;
    TextView addressPart2=null;


    JsonObject addressJson=  consumerAddressArray.get(position).getAsJsonObject();
    if (inflater == null)
        inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    if (convertView == null){
        convertView = inflater.inflate(R.layout.consumer_address_list_item, null);
        viewHolder = new ViewHolder();
        addressHeader = (TextView) convertView.findViewById(R.id.addressHeader);
        phoneNo = (TextView) convertView.findViewById(R.id.phoneNo);
        addressPart1 = (TextView) convertView.findViewById(R.id.addressPart1);
        addressPart2 = (TextView) convertView.findViewById(R.id.addressPart2);
        addressActionView = (ImageView) convertView.findViewById(R.id.address_action);
        viewHolder.consumerKey = addressJson.get("id").getAsJsonObject().get("parentKey").getAsJsonObject().get("id").getAsLong();
        viewHolder.addressKey = addressJson.get("id").getAsJsonObject().get("id").getAsLong();
        viewHolder.position = position;
        convertView.setTag(viewHolder);
    }
    else{
        viewHolder=(ViewHolder) convertView.getTag();
    }

    String conusmerFullName=addressJson.get("consumerFullName")!=null?addressJson.get("consumerFullName").getAsString():"";
    String consumerStreetName=addressJson.get("consumerStreetName")!=null?addressJson.get("consumerStreetName").getAsString():"";
    String consumerHouseNo=addressJson.get("consumerHouseNo")!=null?addressJson.get("consumerHouseNo").getAsString():"";
    String consumerSubArea=addressJson.get("consumerAreaName")!=null?addressJson.get("consumerAreaName").getAsString():"";
    String consumerCity=addressJson.get("consumerCity")!=null?addressJson.get("consumerCity").getAsString():"";
    String postalCde=addressJson.get("postalCode")!=null?addressJson.get("postalCode").getAsString():"";
    String cotactNo=addressJson.get("contactNumber")!=null?addressJson.get("contactNumber").getAsString():"";
    String locationName=addressJson.get("locationName")!=null?addressJson.get("locationName").getAsString():"";

    if(intentFrom.equals("orderdetails")){
        if(convertView!=null){
            convertView.setOnClickListener(new View.OnClickListener(){

                @Override
                public void onClick(View view) {

                    if(cartInfoLst!=null){

                        Intent orderDetailActivity = new Intent(activity, OrderDetailsActivity.class);
                        orderDetailActivity.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK);
                        // orderDetailActivity.setFlags(orderDetailActivity.FLAG_ACTIVITY_NO_HISTORY);
                        orderDetailActivity.putExtra(AppConstants.CARTINFOLIST, cartInfoLst);
                        JsonObject addressObj= editAddresss(  ((ViewHolder)view.getTag()).consumerKey,  ((ViewHolder)view.getTag()).addressKey);
                        if(addressObj!=null)
                        orderDetailActivity.putExtra("changedAddress", addressObj.toString());
                        activity.startActivity(orderDetailActivity);
                    }

                }
            });
        }

    }

    //addressJson
    addressHeader.setText(conusmerFullName);
    phoneNo.setText(""+cotactNo+"");
    if(!consumerHouseNo.equals(""))
    addressPart1.setText(consumerHouseNo+"," +consumerStreetName+ "," +locationName);
    else
        addressPart1.setText(consumerStreetName+ "," +locationName);
    if(!postalCde.equals(""))
    addressPart2.setText(" "+consumerSubArea+" ,"+consumerCity+" - "+postalCde+"");
    else
        addressPart2.setText(" "+consumerSubArea+" ,"+consumerCity);
    if(!intentFrom.equals("orderdetails")){
        addressActionView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view.setTag(viewHolder);
                showFilterPopup(view);
            }
        });
    }

}catch(Exception e){
    e.printStackTrace();
}


        return convertView;
    }
    // Display anchored popup menu based on view selected
    private void showFilterPopup(View v) {
        PopupMenu popup = new PopupMenu(activity, v);
        viewHolderForAction= (ViewHolder)v.getTag();

        // Inflate the menu from xml
        popup.getMenuInflater().inflate(R.menu.address_actions_menu, popup.getMenu());
        // Setup menu item selection
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    /*case R.id.menu_edit:
                        Toast.makeText(activity, "Edit!", Toast.LENGTH_SHORT).show();
                        if(viewHolder!=null)
                        //editAddresss(viewHolder.consumerKey,viewHolder.consumerKey);
                        return true;*/
                    case R.id.menu_delete:
                       // Toast.makeText(activity, "Change!", Toast.LENGTH_SHORT).show();
                        showBookingConfirmationDialog();

                        return true;
                    default:
                        return false;
                }
            }
        });
        // Handle dismissal with: popup.setOnDismissListener(...);
        // Show the menu
        popup.show();
    }
    public class ViewHolder {
     long consumerKey;
     long addressKey;
        int position;
    }
    public JsonObject editAddresss(long consumerKey,long addressKey){
        JsonObject addressJson=null;
            try{
                    System.out.println(consumerKey +""+addressKey);
               int size = consumerAddressArray.size();
                for(int m=0;m<size;m++){
                    addressJson=  consumerAddressArray.get(m).getAsJsonObject();
                   if(addressKey==addressJson.get("id").getAsJsonObject().get("id").getAsLong()){
                       return  consumerAddressArray.get(m).getAsJsonObject();

                    }
                }

            }catch (Exception e){
                e.printStackTrace();
            }
        return addressJson;
    }
    public void deleteAddress(long consumerKey,final long addressKey,int pos){
        Retrofit retrofit =null;
        WebServiceInterface webServiceInterface=null;
        Call<JsonObject> call=null;
        try {
            JsonObject deleteAddressKeys=new JsonObject();
            deleteAddressKeys.addProperty("consumer_id",consumerKey);
            deleteAddressKeys.addProperty("consumer_address_id",addressKey);

            retrofit = RetrofitAdaptor.getRetrofit();
            webServiceInterface = retrofit.create(WebServiceInterface.class);
            call = webServiceInterface.deleteAddress(deleteAddressKeys);
            call.enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(Response<JsonObject> response, Retrofit retrofit)    {
                    try{
                        if (response.message().equals("OK")) {
                            if(  response.body().getAsJsonObject().get("status").getAsString().equals("success")){

                                String addressInfo=  sharedPreferences.getString(EmptycanDataBase.DEFAULT_ADDRESS,"");
                                JSONObject jsonObject=new JSONObject(addressInfo);
                                long addressServerKey=jsonObject.getLong("addressServerKey");
                                if(addressServerKey==addressKey){
                                    SharedPreferences.Editor editor= sharedPreferences.edit();
                                    editor.putString(EmptycanDataBase.DEFAULT_ADDRESS,"");
                                    editor.commit();
                                    editor.commit();
                                }
                                showFinlaConfirmation("Address delted successfully");
                            }else
                                showFinlaConfirmation("Failled to delted");
                        } else {
                            showFinlaConfirmation("Failled to delted");
                        }
                        progressDialog.dismiss();

                    }catch (Exception e){
                        showFinlaConfirmation("Failled to delted");
                        progressDialog.dismiss();
                    }finally {

                    }


                }
                @Override
                public void onFailure(Throwable t) {



                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void lauchAddressListActivity(int pos){
        try{

        }catch(Exception e){

        }
    }
    public void showBookingConfirmationDialog(){
        bookingConfirmationDlg=  new SweetAlertDialog(activity, SweetAlertDialog.WARNING_TYPE);
        bookingConfirmationDlg .setTitleText("Are you sure?");
        bookingConfirmationDlg .setContentText("please choose your  confirmation");
        bookingConfirmationDlg .setCancelText("Don't Delete Address !");
        bookingConfirmationDlg.setConfirmText("Yes,Delete Address!");
        bookingConfirmationDlg.showCancelButton(true);
        bookingConfirmationDlg .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(SweetAlertDialog sDialog) {
                // bookingConfirmationDlg.dismiss();
                // reuse previous dialog instance, keep widget user state, reset them if you need
                sDialog.setTitleText(" Aborted!")
                        .setContentText("Process Aborted:)")
                        .setConfirmText("OK")
                        .showCancelButton(false)
                        .setCancelClickListener(null)
                        .setConfirmClickListener(null)
                        .changeAlertType(SweetAlertDialog.ERROR_TYPE);
                //launchBookCanFormActivity("NOHISTROY");
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
                progressDialog = new SweetAlertDialog(activity,SweetAlertDialog.PROGRESS_TYPE);
                // progressDialog.setProgressStyle(0);
                //progressDialog.setMessage("Loading...");
                progressDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
                progressDialog.setTitleText("Loading");
                progressDialog.setCanceledOnTouchOutside(false);
                progressDialog.show();
                deleteAddress(viewHolderForAction.consumerKey,viewHolderForAction.addressKey,viewHolderForAction.position);
                //bookWaterCan(cartInfoLst,sDialog);

            }
        }) .show();
    }

    public void showFinlaConfirmation(String status){
        SweetAlertDialog progressDialog = new SweetAlertDialog(this.activity, SweetAlertDialog.WARNING_TYPE);
        // progressDialog.setProgressStyle(0);
        //progressDialog.setMessage("Loading...");
        progressDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));

        progressDialog.setTitleText(status);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
        progressDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(SweetAlertDialog sDialog) {


                if (Build.VERSION.SDK_INT >= 11) {
                    activity.recreate();
                } else {
                    Intent intent = activity.getIntent();
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK);
                    activity.finish();
                    activity.overridePendingTransition(0, 0);

                    activity.startActivity(intent);
                    activity.overridePendingTransition(0, 0);
                }
                sDialog.dismiss();
            }
        });
    }
}

