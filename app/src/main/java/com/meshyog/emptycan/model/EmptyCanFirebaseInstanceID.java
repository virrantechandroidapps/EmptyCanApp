package com.meshyog.emptycan.model;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.google.gson.JsonObject;
import com.meshyog.emptycan.model.server.RetrofitAdaptor;
import com.meshyog.emptycan.model.server.WebServiceInterface;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

/**
 * Created by varadhan on 05-12-2016.
 */
public class EmptyCanFirebaseInstanceID extends FirebaseInstanceIdService {


    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d("", "Refreshed token: " + refreshedToken);
        saveTokenToPrefs(refreshedToken);
    }


    private void saveTokenToPrefs(final String _token)
    {
        Retrofit retrofit =null;
        WebServiceInterface webServiceInterface=null;
        Call<JsonObject> call=null;
        try{
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("fcm_registration_id", _token);
            editor.commit();
            JsonObject fcmInfo=new JsonObject();
            fcmInfo.addProperty("fcmRegistrationId",_token);
            fcmInfo.addProperty("phoneUniqueKey", Settings.Secure.getString(getApplicationContext().getContentResolver(), "android_id"));
            retrofit = RetrofitAdaptor.getRetrofit();
            webServiceInterface = retrofit.create(WebServiceInterface.class);
            call = webServiceInterface.sendFCMToken(fcmInfo);
            call.enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(Response<JsonObject> response, Retrofit retrofit) {
                    if (response.message().equals("OK")) {
                        JsonObject result= response.body().getAsJsonObject();
                        if(result.get("status").getAsString().equals("success")){
                            String serverKey=  result.get("fcm_server_key").getAsString();
                            String  status=serverKey;
                            statusOfFCMKey(status,_token);
                            Toast.makeText(getApplicationContext(),"saved fcm details",Toast.LENGTH_LONG).show();
                        }else{
                            String  status="00000";
                            statusOfFCMKey(status,_token);
                            Toast.makeText(getApplicationContext(),"faillure saving",Toast.LENGTH_LONG).show();
                        }


                    } else {
                        //request not successful (like 400,401,403 etc)
                        //Handle errors
                        Toast.makeText(getApplicationContext(),"network not reachable saving fcm info",Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(Throwable t) {
                    JsonObject jsonObject = new JsonObject();
                    jsonObject.addProperty("result_type", t.getMessage());
                    jsonObject.addProperty("result", t.getMessage());
                    //((SignupActivity)signUpContext).successNotice(jsonObject);
                    // SignupActivity signupActivityy = new SignupActivity();

                }
            });
        }catch(Exception e){
            e.printStackTrace();
        }



    }

    public void statusOfFCMKey(String s,String fcmToken){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("fcm_status", s);
        editor.putString("fcm_token", fcmToken);
        editor.commit();
    }
}