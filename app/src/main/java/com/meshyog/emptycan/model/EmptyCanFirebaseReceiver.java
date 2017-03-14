package com.meshyog.emptycan.model;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.Toast;

import com.AppConstants;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.meshyog.emptycan.R;
import com.meshyog.emptycan.controller.MainActivity;
import com.meshyog.emptycan.controller.NotificationListActivity;
import com.meshyog.emptycan.model.database.EmptycanDataBase;
import com.meshyog.emptycan.model.database.NotificationInfo;
import com.meshyog.emptycan.model.server.RetrofitAdaptor;
import com.meshyog.emptycan.model.server.WebServiceInterface;
import com.meshyog.emptycan.references.NotificationList;


import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

/**
 * Created by varadhan on 27-08-2016.
 */
public class EmptyCanFirebaseReceiver extends FirebaseMessagingService {
    public static final int notifyID = 9001;
    NotificationCompat.Builder builder;
    int numMessages=0;
    private String title="";
   // public List<NotificationInfo> notificaitonInfoList=new ArrayList<NotificationInfo>();
    private SharedPreferences sharedPreferences;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        NotificationInfo notificationInfo=new NotificationInfo();
        try{
           JSONObject notificationData= new JSONObject(remoteMessage.getData());
            notificationInfo.setNotificationTitle( notificationData.has("title")?notificationData.getString("title"):"");
            notificationInfo.setNotificationBody(notificationData.has("body")?notificationData.getString("body"):"");
            notificationInfo.setNotificationData(notificationData.has("data")?notificationData.getString("data"):"");
            notificationInfo.setNotificationSound(notificationData.has("sound")?notificationData.getString("sound"):"");
            //notificationInfo.setNotificationImage(notificationData.getString("noti_img_url"));
            notificationInfo.setNotificationClickAction(notificationData.has("click_action")?notificationData.getString("click_action"):"NOTIFICATION_BOOTH");
            notificationInfo.setNotificationRcvdDate(AppUtils.getCurrentDateAsString());
            notificationInfo.setNotificationMsgId(remoteMessage.getMessageId());
          //  notificaitonInfoList.add(notificationInfo);
            if(this.sharedPreferences==null){
                this.sharedPreferences= PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            }
           /* Activity currentActivity = ((AppConfig)getApplicationContext()).getCurrentActivity();
                if(currentActivity!=null && currentActivity.getClass().getName().equals("com.meshyog.emptycan.controller.BookCanFormActivity")){

                }*/

            if(NotificationUtills.isAppIsInBackground(getApplicationContext())){
                sendNotificationToTray(notificationInfo);
            }else {
                sendNotificationToTray(notificationInfo);
            }
            if(notificationInfo.getNotificationTitle().equals("Products Updation Alert")){
               String productUpdateData=  notificationInfo.getNotificationData();
                SharedPreferences.Editor editor = this.sharedPreferences.edit();
                editor.putString(EmptycanDataBase.PRODUCT_LIST, productUpdateData);
                editor.commit();
            }
           /* if(notificationType.equals(ApplicationConstants.DISTRIBUTOR_REQUEST_ACCEPTED_NOTIFICATN)){

            }else if(notificationType.equals(ApplicationConstants.DISTRIBUTOR_REQUEST_ACCEPTED_NOTIFICATN)){

            }*/


        }catch (Exception e)
        {
            e.printStackTrace();
        }


    }
    public void sendNotificationToTray(NotificationInfo notificationInfo){
        try{
            //Class classs = Class.forName("com.meshyog.emptycan.controller"+notificationInfo.getNotificationClickAction());
            Intent resultIntent = new Intent(this,MainActivity.class);
            resultIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            NotificationCompat.BigPictureStyle notiStyle = new NotificationCompat.BigPictureStyle();
            notiStyle.setSummaryText(notificationInfo.getNotificationBody());
           // notiStyle.bigPicture(picture);
            TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
            stackBuilder.addParentStack(MainActivity.class);
            stackBuilder.addNextIntent(resultIntent);

            PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0,
                    PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_ONE_SHOT);
    String notificationTtl=notificationInfo.getNotificationTitle();
         /*   PendingIntent resultPendingIntent = PendingIntent.getActivity(this, 0,
                    resultIntent, 0);*/
            NotificationCompat.Builder mNotifyBuilder;
            NotificationManager mNotificationManager;
            mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            int defaults = 0;
            defaults = defaults | Notification.DEFAULT_LIGHTS;
            defaults = defaults | Notification.DEFAULT_VIBRATE;
            defaults = defaults | Notification.DEFAULT_SOUND;
            mNotifyBuilder = new NotificationCompat.Builder(this)
                    .setContentTitle(notificationTtl)
                    .setContentText(notificationInfo.getNotificationBody())
                    .setStyle(new NotificationCompat.BigTextStyle().bigText(notificationInfo.getNotificationBody()))
                   // .setStyle(notiStyle)
                    .setSmallIcon(R.mipmap.bootleicon).setTicker(notificationTtl+" !!" + " EmptyCan");
            mNotifyBuilder.setDefaults(defaults);
            mNotifyBuilder.setAutoCancel(false);
            mNotifyBuilder.setContentIntent(resultPendingIntent);
            mNotifyBuilder.setNumber(numMessages++);
            int notificationId=  NotificationUtills.createRandomInteger(1,999999999);
            String notificationArrStr= this.sharedPreferences.getString(EmptycanDataBase.RECENT_NOTIFICATION_IDS,"");

            if(notificationArrStr.equals("")){
                SharedPreferences.Editor editor= this.sharedPreferences.edit();
                editor.putString(EmptycanDataBase.RECENT_NOTIFICATION_IDS,  new JSONArray().put(notificationId).toString());
                editor.commit();
            }else{
               JSONArray recentNotificationArray= new JSONArray(notificationArrStr);
                recentNotificationArray.put(notificationId);
                SharedPreferences.Editor editor= this.sharedPreferences.edit();
                editor.putString(EmptycanDataBase.RECENT_NOTIFICATION_IDS,recentNotificationArray.toString());
                editor.commit();
            }

           mNotificationManager.notify(notificationId, mNotifyBuilder.build());


            EmptycanDataBase emptycanDataBase=new EmptycanDataBase(getApplicationContext());
            updateStatus(emptycanDataBase.getConsumerKey(),notificationInfo.getNotificationMsgId());
          //  emptycanDataBase.insertNotification(notificationInfo);
        }catch(Exception e){
e.printStackTrace();
        }
    }
  /*  private void sendNotification(JSONObject customer_information)throws Exception {

        Intent resultIntent = new Intent(this,MainActivity.class);
        //resultIntent.putExtra("msg", msg);
        resultIntent.setAction("com.meshyog.emptycan.activity.MainActivity");
        resultIntent.putExtra("NotificationType", "NewCustomerRequest");
        PendingIntent resultPendingIntent = PendingIntent.getActivity(this, 0,
                resultIntent, 0);

        resultIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);

        //JSONObject customer_information=new JSONObject(msg);
        String customer_Name=customer_information.getString("consumerUserName");
        NotificationCompat.Builder mNotifyBuilder;
        NotificationManager mNotificationManager;

        mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        mNotifyBuilder = new NotificationCompat.Builder(this)
                .setContentTitle("TEST Title")
                .setContentText("New Customer Requst")
                .setSmallIcon(R.drawable.notification_image).setTicker(this.title);
        // Set pending intent
        mNotifyBuilder.setContentIntent(resultPendingIntent);
        mNotifyBuilder.setNumber(numMessages++);

        // Set Vibrate, Sound and Light
        int defaults = 0;
        defaults = defaults | Notification.DEFAULT_LIGHTS;
        defaults = defaults | Notification.DEFAULT_VIBRATE;
        defaults = defaults | Notification.DEFAULT_SOUND;

        mNotifyBuilder.setDefaults(defaults);
        // Set the content for Notification
        //mNotifyBuilder.setContentText("New message from Server");
        // Set autocancel
        mNotifyBuilder.setAutoCancel(true);
        //mNotificationManager.setLatestEventInfo(getApplicationContext(), "", "", resultPendingIntent);

        // Post a notification
        mNotificationManager.notify(generateIds(), mNotifyBuilder.build());
    }*/
   /* private void sendNotification(String messageBody) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                //.setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Firebase Push Notification")
                .setContentText(messageBody)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0, notificationBuilder.build());
    }*/
    public static int  generateIds(){
        Random random = new Random();
        int m = random.nextInt(9999 - 1000) + 1000;
        return m;
    }

    public void updateStatus(long consumerId,String messageId){
        Retrofit retrofit =null;
        WebServiceInterface webServiceInterface=null;
        Call<JsonObject> call=null;
        try{
            retrofit = RetrofitAdaptor.getRetrofit();
            webServiceInterface = retrofit.create(WebServiceInterface.class);

            JsonObject jsonObject=new JsonObject();
            jsonObject.addProperty("consumerId",consumerId);
            jsonObject.addProperty("messageId",messageId);
            call= webServiceInterface.updateNotificationStatus(jsonObject);
            call.enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(Response<JsonObject> response, Retrofit retrofit) {
                    if (response.message().equals("OK")) {


                    } else {
                        //request not successful (like 400,401,403 etc)
                        //Handle errors

                    }

                }

                @Override
                public void onFailure(Throwable t) {

                    //((SignupActivity)signUpContext).successNotice(jsonObject);
                    // SignupActivity signupActivityy = new SignupActivity();

                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
