package com.meshyog.emptycan.model.database;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.preference.PreferenceManager;
import android.util.Base64;
import android.util.Log;

import com.meshyog.emptycan.R;
import com.meshyog.emptycan.model.AppUtils;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

/**
 * Created by varadhan on 14-11-2016.
 */
public class EmptycanDataBase extends SQLiteOpenHelper {
    public static final String APP_DEVICEID = "emptycan_app_device_Id";
    public static final String APP_DEVICEINFO = "emptycan_app_device_Info";
    public static final String APP_DEVICETOKEN = "emptycan_app_device_token";
    public static final String APP_LOCATION = "emptycan_user_location";
    public static final String APP_USERID = "emptycan_app_app_Id";
    public static final String TABLE_ADPROPER = "ad_property";
    public static final String TABLE_JSON = "json";
    public static final String USER_AUTHTYPE = "emptycan_user_auth_method";
    public static final String USER_DOB = "emptycan_user_dob";
    public static final String USER_EMAIL = "emptycan_user_email";
    public static final String USER_FIRSTNAME = "emptycan_user_first_name";
    public static final String USER_FLYERID = "emptycan_user_flyer_user_id";
    public static final String USER_GENDER = "emptycan_user_gender";
    public static final String USER_IS_USED_REFERRAL = "emptycan_user_is_referral_used";
    public static final String USER_LASTNAME = "emptycan_user_last_name";
    public static final String USER_MOBNO = "emptycan_user_mobile_no";
    public static final String USER_PROFILEPIC = "emptycan_user_profile_image";
    public static final String USER_REFERRAL_CODE = "emptycan_user_referral_code";
    public static final String USER_REFERRAL_NOTE = "emptycan_user_referral_note";
    public static final String USER_USED_WALLET_BAL = "emptycan_user_wallet_balance";
    public static final String USER_SERVER_KEY = "user_server_key";
    public static final String DB_FILE_PATH="/data/data/com.meshyog.emptycan/databases/";
    public static final String USER_NAME = "user_name";
    public static final String DEFAULT_DISTRBUTR_ID = "default_distributor_id";
    public static final String PRODUCT_LIST = "product_list";
    public static final String DATABASE_NAME = "emptycan.sqlite";
    public static final String FIRST_TIME_USER="first_time_user";
    public static final String DEFAULT_ADDRESS="defaultAddress";
    public static final String RECENT_NOTIFICATION_IDS="recent_notification_ids";
    public static final String IS_COMPLETED_FIRST_BOOKING="is_he_completed_first_time_booking";
    public static final String REF_STATUS="ref_status";
    public static final String USER_PRO_IMG_URL="emptycan_user_image";
    public static final String USER_LOC_LAT_LANG = "user_location_info";
    public final static String ORDER_INITIATED="INITIATED";
    public final static String ORDER_SCHEDULED="SCHEDULED";
    public final static String ORDER_CANCELLED_BY_CUS="CANCELLED_BY_CUS";
    public final static String ORDER_CANCELLED_BY_DELVIRYBOY="CANCELLED_BY_TEAM";
    public final static String ORDER_DELIVERED="DELIVERED";
    public final static String ADDRESS_JSON="address_json";
    private final String DATABASE_PATH;
    public static Integer DATABASE_VERSION = 1;
    private final Context mContext;
    private String createNotificatTbl="CREATE TABLE  `notification_tbl`( \t`n_id`\tINTEGER PRIMARY KEY AUTOINCREMENT, \t`n_title`\tTEXT, \t`n_body`\tTEXT, \t`n_status`\tTEXT, \t`n_image`\tTEXT, \t`n_data`\tTEXT, \t`n_received_date`\tTEXT )";
    AppUtils appUtills;
    SharedPreferences sharedPreferences;
    
    public EmptycanDataBase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION.intValue());
        this.DATABASE_PATH = "/data/data/com.meshyog.emptycan/databases/";
        this.mContext = context;
        this.appUtills = AppUtils.getInstance(this.mContext);
        this.sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public final void createDataBase() throws IOException {
        boolean dbExist = checkDataBase();
        checkOflyerDataBase();
        if (!dbExist) {
            getReadableDatabase().close();
            try {
                //copyDatabase();
                tableCreation(createNotificatTbl,"notification_tbl");
                System.out.print("ddfdf");
            } catch (Exception e) {
                e.printStackTrace();
                //throw new Error("Error copying database");
            }
        }else{
            tableCreation(createNotificatTbl,"notification_tbl");
        }
    }

    private boolean checkDataBase() {
        return new File("/data/data/com.meshyog.emptycan/databases/emptycan.sqlite").exists();
    }

    private void checkOflyerDataBase() {
        File dbFile = new File("/data/data/com.meshyog.emptycan/databases/elyerz.sqlite");
        if (dbFile.exists()) {
            dbFile.delete();
        }
    }

   /* private void copyDatabase() throws IOException {
        close();
        InputStream input = this.mContext.getAssets().open(DB_FILE_PATH+DATABASE_NAME);
        String outFileName = "/data/data/com.meshyog.emptycan/databases/emptycan.sqlite";
        OutputStream output = new FileOutputStream("/data/data/com.meshyog.emptycan/databases/emptycan.sqlite");
        byte[] buffer = new byte[Barcode.UPC_E];
        while (true) {
            int length = input.read(buffer);
            if (length > 0) {
                output.write(buffer, 0, length);
            } else {
                output.flush();
                output.close();
                input.close();
                getWritableDatabase().close();
                return;
            }
        }
    }*/

    public  Long getConsumerKey(){
        Long key=0L;
        try{
            key= Long.parseLong(this.sharedPreferences.getString(EmptycanDataBase.USER_SERVER_KEY,"101"));
        }catch(Exception e){
            e.printStackTrace();
        }
        return key;
    }
    public void onCreate(SQLiteDatabase db) {
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    public void deleteTableData(String tableName){
        SQLiteDatabase db=null;
        try{
            db = getWritableDatabase();
                db.execSQL("delete table from "+tableName+"");

        }catch(Exception e){
            e.printStackTrace();
        }finally {
            db.close();
        }
    }
    public void tableCreation(String createTblQry,String tableName){
        SQLiteDatabase db=null;
        try{

             db = getWritableDatabase();
             if(!isTableExists(db,tableName)){
                 db.execSQL(createTblQry);
             }

        }catch(Exception e){
            e.printStackTrace();
        }finally {
            db.close();
        }
    }

            public void insertNotification(NotificationInfo notificationInfo) {
                SQLiteDatabase db =null;
                try {
                    String query="insert into notification_tbl(`n_title`,`n_body`,`n_status`,`n_image`,`n_data`,`n_received_date`) values " +
                            "( " +
                            " '"+notificationInfo.getNotificationTitle()+"'," +
                            " '"+notificationInfo.getNotificationBody()+"'," +
                            " '"+notificationInfo.isRead()+"'," +
                            " '"+notificationInfo.getNotificationImage()+"'," +
                            " '"+notificationInfo.getNotificationData()+"'," +
                            " '"+notificationInfo.getNotificationRcvdDate()+"' )";
                    db = getWritableDatabase();
                    db.execSQL(query);
                    System.out.print(""+query);
                    Log.i("mainActivity", query);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    db.close();
                }
            }
    public void deleteOldNotifications(){
       String sql="DELETE FROM notification_tbl WHERE n_id NOT IN (   SELECT n_id   FROM (     SELECT n_id     FROM  notification_tbl    ORDER BY datetime(n_received_date) DESC     LIMIT 10   ) foo );";
        SQLiteDatabase db =null;
        try{
            db = getWritableDatabase();
            db.execSQL(sql);
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    public ArrayList<NotificationInfo> getNotificationList(){
        ArrayList<NotificationInfo> notificationList=null;
        SQLiteDatabase db =null;
        try{
            String query="select * from notification_tbl order by  n_received_date";
            db = getWritableDatabase();
            Cursor c = db.rawQuery(query,null);
            if (c != null ) {
                notificationList=new ArrayList<>();
                if  (c.moveToFirst()) {
                    do {
                        NotificationInfo notificationInfo=new NotificationInfo();
                        notificationInfo.setNotificationTitle(  c.getString(c.getColumnIndex("n_title")));
                        notificationInfo.setNotificationBody(  c.getString(c.getColumnIndex("n_body")));
                        notificationInfo.setRead(  c.getInt (c.getColumnIndex("n_status")) > 0);
                        notificationInfo.setNotificationImage(  c.getString(c.getColumnIndex("n_image")));
                        notificationInfo.setNotificationData(  c.getString(c.getColumnIndex("n_data")));
                        notificationInfo.setNotificationRcvdDate(  c.getString(c.getColumnIndex("n_received_date")));
                        notificationInfo.setNotificationImgRsrc(R.mipmap.ic_add_alert_black_24dp);
                        notificationInfo.setNotificationType(1);
                        notificationList.add(notificationInfo);


                    }while (c.moveToNext());
                }
            }
        }catch(Exception e)
        {
            e.printStackTrace();
        }
        return notificationList;
    }
    public String encrypt(String value) {
        byte[] data = new byte[0];
        try {
            data = value.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return Base64.encodeToString(data, 0);
    }
   public boolean isTableExists(SQLiteDatabase db, String tableName)
    {
        if (tableName == null || db == null || !db.isOpen())
        {
            return false;
        }
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM sqlite_master WHERE type = ? AND name = ?", new String[] {"table", tableName});
        if (!cursor.moveToFirst())
        {
            cursor.close();
            return false;
        }
        int count = cursor.getInt(0);
        cursor.close();
        return count > 0;
    }
}
