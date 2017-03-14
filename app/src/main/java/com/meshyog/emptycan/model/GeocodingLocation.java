package com.meshyog.emptycan.model;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.google.gson.JsonObject;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 * Created by varadhan on 18-12-2016.
 */
public class GeocodingLocation {

 static class GeoCodeLocThread extends Thread{
     final  Context threadContext;
       final  String locationString;
       final Handler messageHandler;

    GeoCodeLocThread(Context context, String str, Handler handler) {
        this.threadContext = context;
        this.locationString = str;
        this.messageHandler = handler;
    }
     public void run(){
         Geocoder geocoder = new Geocoder(threadContext, Locale.getDefault());
         String result = null;
         JsonObject jsonObject=null;
         try {
             List addressList = geocoder.getFromLocationName(locationString, 1);
             if (addressList != null && addressList.size() > 0) {
                 Address address = (Address) addressList.get(0);

                 jsonObject=new JsonObject();

                 jsonObject.addProperty("Latitude", address.getLatitude() != 0.0 ? address.getLatitude():0.0 );
                 jsonObject.addProperty("Longitude",address.getLongitude()!= 0.0 ? address.getLongitude():0.0 );

                 jsonObject.addProperty("locationName",locationString!=null?locationString:"");
                 jsonObject.addProperty("SubArea",address.getSubLocality()!=null?address.getSubLocality():"");
                 jsonObject.addProperty("District",address.getSubAdminArea()!=null?address.getSubAdminArea():"");
                 jsonObject.addProperty("State",address.getAdminArea()!=null?address.getAdminArea():"");
                 jsonObject.addProperty("CountryName",address.getCountryName()!=null?address.getCountryName():"");
                 jsonObject.addProperty("PostalCode",address.getPostalCode()!=null?address.getPostalCode():"");
                 jsonObject.addProperty("CountryCode",address.getCountryCode()!=null?address.getCountryCode():"");
                 jsonObject.addProperty("Phone",address.getPhone()!=null?address.getPhone():"");
                 jsonObject.addProperty("Locality",address.getLocality()!=null?address.getLocality():"");
                 jsonObject.addProperty("Premises",address.getPremises()!=null?address.getPremises():"");






                // Toast.makeText(threadContext, result.toString(), Toast.LENGTH_SHORT).show();
             }
         } catch (IOException e) {
            // Log.e(TAG, "Unable to connect to Geocoder", e);
             e.printStackTrace();
         } finally {
                   Message message = Message.obtain();
                   message.setTarget(messageHandler);
                   if (jsonObject != null ) {
                       message.what = 1;
                        Bundle bundle = new Bundle();

                       bundle.putString("address", jsonObject.toString());
                        message.setData(bundle);
                   } else {                        message.what = 1;
                        Bundle bundle = new Bundle();
                       jsonObject=new JsonObject();
                       jsonObject.addProperty("status","fail");
                       jsonObject.addProperty("locationName",locationString);
                       bundle.putString("address", jsonObject.toString());
                       message.setData(bundle);
                    }
                   message.sendToTarget();
         }
     }

}

    public  static void getLatLangFromAddress(String str, Context context, Handler handler){
    new GeoCodeLocThread( context,  str,  handler).start();
    }

}
