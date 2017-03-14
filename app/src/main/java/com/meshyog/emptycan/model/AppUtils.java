package com.meshyog.emptycan.model;


import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.inputmethod.InputMethodManager;

import com.BuildConfig;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import com.google.gson.JsonParser;


import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.InputMismatchException;

/**
 * Created by varadhan on 14-11-2016.
 */
public class AppUtils {
    private Context mContext;
    public static String DEVICE_TYPE;
    public static String androidVersion;
    private static AppUtils instance;
    public String locCord;
    public AppUtils(Context context){
        this.mContext = context;
        this.locCord = BuildConfig.FLAVOR;
    }
    public static AppUtils getInstance(Context ctx) {
        if (instance == null) {
            instance = new AppUtils(ctx);
        }
        return instance;
    }
    private boolean isGooglePlayServicesAvailable(Context context) {
        if (GooglePlayServicesUtil.isGooglePlayServicesAvailable(context) == 0) {
            return true;
        }
        return false;
    }
    public  String getDeviceId() {
        return Settings.Secure.getString(this.mContext.getContentResolver(), "android_id");
    }
    public static String getDeviceInfo() {
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        if (model.startsWith(manufacturer)) {
            return capitalizeFirst(model);
        }
        return capitalizeFirst(manufacturer) + "||" + model;
    }
    public static boolean isValidEmail(String email) {
        return !TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
    public static  String getDeviceSuperInfo() {
        String s = "Debug-infos ";

        try {


            s += " ||OS Version-"      + System.getProperty("os.version")      + "(" + android.os.Build.VERSION.INCREMENTAL + ")";
            s += " ||OS API Level-"    + android.os.Build.VERSION.SDK_INT;
            s += " ||Device-"          + android.os.Build.DEVICE;
            s += " ||Model (and Product)-" + android.os.Build.MODEL            + " ("+ android.os.Build.PRODUCT + ")";

            s += " ||RELEASE-"         + android.os.Build.VERSION.RELEASE;
            s += " ||BRAND-"           + android.os.Build.BRAND;
            s += " ||DISPLAY-"         + android.os.Build.DISPLAY;
            s += " ||CPU_ABI-"         + android.os.Build.CPU_ABI;
            s += " ||CPU_ABI2-"        + android.os.Build.CPU_ABI2;
            s += " ||UNKNOWN-"         + android.os.Build.UNKNOWN;
            s += " ||HARDWARE-"        + android.os.Build.HARDWARE;
            s += " ||Build ID-"        + android.os.Build.ID;
            s += " ||MANUFACTURER-"    + android.os.Build.MANUFACTURER;
            s += "||SERIAL-"          + android.os.Build.SERIAL;
            s += " ||USER-"            + android.os.Build.USER;
            s += " ||HOST-"            + android.os.Build.HOST;




        } catch (Exception e) {

            e.printStackTrace();

        }
return s;
    }
    public static boolean checkAppInstalledOrNot(Context context) {
        try {
            context.getPackageManager().getPackageInfo(GooglePlayServicesUtil.GOOGLE_PLAY_SERVICES_PACKAGE, PackageManager.GET_ACTIVITIES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    public static boolean isServicesConnected(Context context) {
        if (GooglePlayServicesUtil.isGooglePlayServicesAvailable(context) == 0) {
            return true;
        }
        return false;
    }


    public static String returnEmptyStringIfNull(String stringValue) {
        if (stringValue == null) {
            return BuildConfig.FLAVOR;
        }
        if (TextUtils.isEmpty("null") || "null".equalsIgnoreCase(stringValue)) {
            return BuildConfig.FLAVOR;
        }
        return stringValue.trim();
    }

    public static boolean isGPSAvailable(Context context) {
        LocationManager manager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        if (manager == null || !manager.isProviderEnabled("gps")) {
            return false;
        }
        return true;
    }

    public static boolean isNetworkAvailable(Context context) {
        NetworkInfo netInfo = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
        if (netInfo == null || !netInfo.isConnectedOrConnecting()) {
            return false;
        }
        return true;
    }
    public static String capitalizeFirst(String s) {
        if (s == null || s.length() == 0) {
            return BuildConfig.FLAVOR;
        }
        char first = s.charAt(0);
        return !Character.isUpperCase(first) ? Character.toUpperCase(first) + s.substring(1) : s;
    }
    public static String getCurrentDateAsString() {
       String dateFormat="yyyy-MM-dd HH:mm:ss";
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
        return sdf.format(new Date());
    }

    public static java.util.Date convertStringDateToUtil(String sdate, String format) {
        java.util.Date util_sdate = null;
        if ((sdate != null) && !sdate.equals("")) {
            try {
               /* SimpleDateFormat sdf1 = new SimpleDateFormat("dd/mm/yyyy HH:mm:ss");
                Date date = sdf1.parse("sdate");*/
                SimpleDateFormat sdf = new SimpleDateFormat(format);
                util_sdate = sdf.parse(sdate);
            } catch (ParseException pe) {
                pe.printStackTrace();
            }
        }
        return util_sdate;
    }

    public static String removeSybolsFromString(String inputStr){
        return inputStr.replaceAll("[^\\x00-\\x7F]","");
    }
    /*@SuppressWarnings("unchecked")
    public static Object getObjectFromJson(String json, Object object) throws JsonParseException, JsonMappingException,
            IOException {
        return new ObjectMapper().readValue(json, (Class<Object>) object);
    }*/

    public  JsonObject getJSONObject(Object object) {
        JsonObject jObj = null;
        JsonArray jsArr = new JsonArray();
        jsArr.add(getJSONElement(object));
        jObj = new JsonObject();
        jObj.add("rows", jsArr);
        return jObj;
    }
    public JsonElement getJSONElement(Object object) {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.setExclusionStrategies(new GsonExclusionStrategy(null));
        Gson gson = gsonBuilder.serializeNulls().create();
        String json = gson.toJson(object);
        JsonElement jelement = new JsonParser().parse(json);
        return jelement;
    }
    public static void hideSoftKeyboard(Activity activity) {
        ((InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
    }

}
