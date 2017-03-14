package com.meshyog.emptycan.model;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;
import android.text.TextUtils;

import com.AppConstants;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.meshyog.emptycan.model.database.EmptycanDataBase;
import com.meshyog.emptycan.view.LruBitmapCache;

/**
 * Created by varadhan on 30-11-2016.
 */
public class AppConfig extends MultiDexApplication {

    public static final String TAG = AppConfig.class.getSimpleName();

    private RequestQueue mRequestQueue;
    private ImageLoader mImageLoader;

    private SharedPreferences sharedPreferences;
    private static AppConfig mInstance;

    public static final String APPLICATION_ID = "com.fyndus.fyndus";
    public static final String BUILD_TYPE = "release";
    public static final boolean DEBUG = false;
    public static final String FLAVOR = "";
    public static final int VERSION_CODE = 10;
    public static final String VERSION_NAME = "1.3.2";
    public static String bookingStatus="";
    //List<CartInfo> cartInfoList =null;

    /*public List<CartInfo> getCartInfoList() {
        return cartInfoList;
    }*/

    /*public void setCartInfoList(List<CartInfo> cartInfoList) {
        this.cartInfoList = cartInfoList;
    }*/

    public void onCreate() {
        super.onCreate();
        mInstance = this;
        this.sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
       // cartInfoList =new ArrayList<CartInfo>();

    }

    private Activity mCurrentActivity = null;



    public static synchronized AppConfig getInstance() {
        return mInstance;
    }

    public Activity getCurrentActivity(){
        return mCurrentActivity;
    }
    public void setCurrentActivity(Activity mCurrentActivity){
        this.mCurrentActivity = mCurrentActivity;
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        }

        return mRequestQueue;
    }

    public ImageLoader getImageLoader() {
        getRequestQueue();
        if (mImageLoader == null) {
            mImageLoader = new ImageLoader(this.mRequestQueue,
                    new LruBitmapCache());
        }
        return this.mImageLoader;
    }

    public <T> void addToRequestQueue(Request<T> req, String tag) {
        // set the default tag if tag is empty
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        getRequestQueue().add(req);
    }

    public <T> void addToRequestQueue(Request<T> req) {
        req.setTag(TAG);
        getRequestQueue().add(req);
    }

    public void cancelPendingRequests(Object tag) {
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(tag);
        }
    }

    public static String  getNotificationUrl(){
       String url="";
        try{
            EmptycanDataBase emptycanDataBase=new EmptycanDataBase(getInstance());
            url=   AppConstants.baseContext+"consumer-notifications/"+String.valueOf(emptycanDataBase.getConsumerKey());

        }catch(Exception e){
            e.printStackTrace();
        }
        return url;
    }

    public void clearAllSharedPrefInfo(){
        SharedPreferences.Editor editor= sharedPreferences.edit();
        editor.putBoolean(EmptycanDataBase.FIRST_TIME_USER,true);
        editor.putString(EmptycanDataBase.USER_SERVER_KEY,"101");
        editor.putString(EmptycanDataBase.USER_MOBNO,"");
        // editor.putString(EmptycanDataBase.USER_,jsonObject.get("consumerPassword").getAsString());
        editor.putString(EmptycanDataBase.APP_DEVICEINFO,"");
        editor.putString(EmptycanDataBase.APP_DEVICEID,"");
        editor.putString(EmptycanDataBase.USER_NAME,"FirstName LastName");
        editor.putString(EmptycanDataBase.USER_DOB,"00-00-0000");
        editor.putString(EmptycanDataBase.USER_GENDER,"male/female/transgender");
        editor.putString(EmptycanDataBase.USER_EMAIL,"noemail@xxxx.com");
        editor.putString(EmptycanDataBase.DEFAULT_DISTRBUTR_ID,"123456");
        editor.putString(EmptycanDataBase.DEFAULT_ADDRESS,"");
        editor.putString(EmptycanDataBase.USER_PRO_IMG_URL,AppConstants.USER_DEFAULT_AVATHAR);
        editor.putString(EmptycanDataBase.IS_COMPLETED_FIRST_BOOKING,"NOT_YET");
        editor.commit();
    }
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(newBase);
        MultiDex.install(this);
    }
}
