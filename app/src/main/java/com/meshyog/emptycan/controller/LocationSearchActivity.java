package com.meshyog.emptycan.controller;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.Toast;

import com.google.android.gms.location.places.Place;
import com.meshyog.emptycan.R;
import com.meshyog.emptycan.model.AppConfig;
import com.meshyog.emptycan.model.AppUtils;
import com.meshyog.emptycan.model.ContactInterface;
import com.meshyog.emptycan.model.GeocodingLocation;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by varadhan on 16-12-2016.
 */
public class LocationSearchActivity extends AppCompatActivity  implements AdapterView.OnItemClickListener {
    private static final String OUT_JSON = "/json";
    private static final String PLACES_API_BASE = "https://maps.googleapis.com/maps/api/place";
    protected static final int REQUEST_CHECK_SETTINGS = 1;
    private static final String TAG = "MainActivity";
    private static final String TYPE_AUTOCOMPLETE = "/autocomplete";
    private String spinner_location;
    private AutoCompleteTextView streetAutoCompletedTxtView;
    public HashMap<String,String> addressMap;
    public  static ContactInterface contactInterface;
    public SharedPreferences sharedPreferences;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.location_search);
        this.streetAutoCompletedTxtView = (AutoCompleteTextView)findViewById(R.id.street_search);
        this.streetAutoCompletedTxtView.setAdapter(new GooglePlacesAutocompleteAdapter(this, getApplicationContext(), R.layout.places_list));
        if(streetAutoCompletedTxtView !=null)
        streetAutoCompletedTxtView.setOnItemClickListener(this);
        this.spinner_location= AppConfig.FLAVOR;
        if(getSupportActionBar()!=null) {
            getSupportActionBar().setTitle("Location Search");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        Intent addressFrmMap=getIntent();
        this.sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        if(addressFrmMap.getExtras().get("address_map") != null)
         addressMap=( HashMap<String,String> )addressFrmMap.getExtras().get("address_map");

    }
    public void saveLocationMetaData(String locationAddress){
        try{
            this.sharedPreferences.edit().putString("location_meta_data",locationAddress);
            this.sharedPreferences.edit().commit();
        }catch (Exception e){
            e.printStackTrace();
        }

    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) // Press Back Icon
        { onBackPressed();
            return true;
            //finish();
        }

        return super.onOptionsItemSelected(item);
    }
    private class GeocoderHandler extends Handler {
        private GeocoderHandler() {
        }

        public void handleMessage(Message message) {
            String locationAddress;
            switch (message.what) {
                case LocationSearchActivity.REQUEST_CHECK_SETTINGS /*1*/:
                    locationAddress = message.getData().getString("address");
                       contactInterface.result(locationAddress);

                    break;
                default:
                    locationAddress = null;
                    break;
            }

        }
    }

    class GooglePlacesAutocompleteAdapter extends ArrayAdapter<String> implements Filterable {
        final /* synthetic */ LocationSearchActivity locationSearchBarActiviy;
        private ArrayList<String> resultStreetList;
        Context context;
        public GooglePlacesAutocompleteAdapter(LocationSearchActivity locationActivityNew, Context context, int i) {
            super(context, i);
            this.locationSearchBarActiviy = locationActivityNew;
            this.context=context;
        }
        /*public View getView(int position, View convertView, ViewGroup parent)
        {
            View v = convertView;
            try{

                if (v == null) {
                    LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    v = vi.inflate(R.layout.places_list, null);

                }
                String locationName = resultStreetList.get(position);
                if (locationName != null ) {
                    TextView tt = (TextView) v.findViewById(R.id.txtName);

                    if (tt != null) {
                        tt.setText(locationName);
                    } else {
                        System.out.println("Not getting textview..");
                    }

                }
            }catch(Exception e){
                e.printStackTrace();
            }

            return v;
        }*/

        /* renamed from: com.fyndus.fyndus.controller.LocationActivityNew.b.1 */
        class StreetSearchFilter extends Filter {
            final /* synthetic */ GooglePlacesAutocompleteAdapter autoListAdapter;

            StreetSearchFilter(GooglePlacesAutocompleteAdapter c0885b) {
                this.autoListAdapter = c0885b;
            }

            protected FilterResults performFiltering(CharSequence charSequence) {
                FilterResults filterResults = new FilterResults();
                try{
                    if (charSequence != null) {
                        this.autoListAdapter.resultStreetList = LocationSearchActivity.getApproximateStreetList(charSequence.toString());
                        if(this.autoListAdapter.resultStreetList == null)
                            Toast.makeText(getApplicationContext()," street values return null",Toast.LENGTH_SHORT).show();
                        if(this.autoListAdapter.resultStreetList!=null){
                            filterResults.values = this.autoListAdapter.resultStreetList;
                            filterResults.count = this.autoListAdapter.resultStreetList.size();
                        }else{
                            this.autoListAdapter.resultStreetList.add("No results");
                            filterResults.values = this.autoListAdapter.resultStreetList;
                            filterResults.count = this.autoListAdapter.resultStreetList.size();
                        }

                    }
                }catch(Exception e){
                    e.printStackTrace();
                }


                return filterResults;
            }

            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                try{
                    if (filterResults == null || filterResults.count <= 0) {
                        this.autoListAdapter.notifyDataSetInvalidated();
                    } else {
                        this.autoListAdapter.notifyDataSetChanged();

                    }
                }catch(Exception e){
                    e.printStackTrace();
                }

            }
        }



        public String getLocationName(int i) {
            return (String) this.resultStreetList.get(i);
        }

        public int getCount() {
            return this.resultStreetList.size();
        }

        public Filter getFilter() {
            return new StreetSearchFilter(this);
        }

        public  String getItem(int i) {
            return getLocationName(i);
        }

    }



    public static ArrayList<String> getApproximateStreetList(String str) {
        Throwable e;
        Throwable th;
        Throwable e2;
        HttpURLConnection httpURLConnection;
        HttpURLConnection httpURLConnection2 = null;
        StringBuilder stringBuilder = new StringBuilder();
        try {
            StringBuilder stringBuilder2 = new StringBuilder("https://maps.googleapis.com/maps/api/place/autocomplete/json");
            stringBuilder2.append("?key=AIzaSyB_AIE_HgpsORuYAOE0I0z-bjAbdfpIW6s");
            stringBuilder2.append("&input=" + URLEncoder.encode(str, "utf8"));
            URL url = new URL(stringBuilder2.toString());
            System.out.println("URL: " + url);
            HttpURLConnection httpURLConnection3 = (HttpURLConnection) url.openConnection();
            try {
                InputStreamReader inputStreamReader = new InputStreamReader(httpURLConnection3.getInputStream());
                char[] cArr = new char[Place.TYPE_SUBLOCALITY_LEVEL_2];
                while (true) {
                    int read = inputStreamReader.read(cArr);
                    if (read == -1) {
                        break;
                    }
                    stringBuilder.append(cArr, 0, read);
                }
                if (httpURLConnection3 != null) {
                    httpURLConnection3.disconnect();
                }
                ArrayList<String> arrayList;
                try {
                    JSONObject jSONObject = new JSONObject(stringBuilder.toString());
                    JSONArray jSONArray = jSONObject.getJSONArray("predictions");
                   // C1080a.m1721c("Google:" + jSONObject.toString());
                    arrayList = new ArrayList(jSONArray.length());
                    int i = 0;
                    while (i < jSONArray.length()) {
                        try {
                            //System.out.println(jSONArray.getJSONObject(i).getString(PlusShare.KEY_CONTENT_DEEP_LINK_METADATA_DESCRIPTION));
                            System.out.println("============================================================");
                            arrayList.add(jSONArray.getJSONObject(i).getString("description"));
                            i++;
                        } catch (JSONException e3) {
                            e = e3;
                        }
                    }
                    return arrayList;
                } catch (Throwable e22) {
                    e22.printStackTrace();
                    th = e22;
                    arrayList = null;
                    e = th;
                    //C1080a.m1709a("Cannot process JSON results", e);
                    return arrayList;
                }
            } catch (Throwable e4) {
                e4.printStackTrace();
                th = e4;
                httpURLConnection = httpURLConnection3;
               // e22 = th;
                try {
                   // C1080a.m1709a("Error processing Places API URL", e22);
                    if (httpURLConnection != null) {
                        httpURLConnection.disconnect();
                    }
                    return null;
                } catch (Throwable th2) {
                  //  e22 = th2;
                    th2.printStackTrace();
                    httpURLConnection2 = httpURLConnection;
                    if (httpURLConnection2 != null) {
                        httpURLConnection2.disconnect();
                    }
                    //throw e22;
                }
            } /*catch (Throwable e42) {
                th = e42;
                httpURLConnection = httpURLConnection3;
              //  e22 = th;
               // C1080a.m1709a("Error connecting to Places API", e22);
                if (httpURLConnection != null) {
                    httpURLConnection.disconnect();
                }
                return null;
            }*/ /*catch (Throwable e5) {
                th = e5;
                httpURLConnection2 = httpURLConnection3;
               // e22 = th;
                if (httpURLConnection2 != null) {
                    httpURLConnection2.disconnect();
                }
               // throw e22;
            }*/
        } catch (MalformedURLException e6) {
            e6.printStackTrace();
           // e22 = e6;
            httpURLConnection = null;
            //C1080a.m1709a("Error processing Places API URL", e22);
            if (httpURLConnection != null) {
                httpURLConnection.disconnect();
            }
            return null;
        } catch (IOException e7) {
            e7.printStackTrace();
           // e22 = e7;
            httpURLConnection = null;
           // C1080a.m1709a("Error connecting to Places API", e22);
            if (httpURLConnection != null) {
                httpURLConnection.disconnect();
            }
            return null;
        } catch (Throwable th3) {
           // e22 = th3;
            th3.printStackTrace();
            if (httpURLConnection2 != null) {
                httpURLConnection2.disconnect();
            }
           // throw e22;
        }
        return   null;
    }
   public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        AppUtils.hideSoftKeyboard(this);
        this.spinner_location = (String) adapterView.getItemAtPosition(position);
       // this.f22g.manualLocation = this.spinner_location;
        GeocodingLocation.getLatLangFromAddress(this.spinner_location, this, new GeocoderHandler());
       Intent addressIntent=new Intent(this,AddressFormActivity.class);
       addressIntent.putExtra("street_name",spinner_location);
       if(addressMap!=null)
       addressMap.put("locationName",this.spinner_location);
       addressIntent.putExtra("address_map",addressMap);
       startActivity(addressIntent);
       finish();
    }
    public  static void setContact( ContactInterface listener){
       contactInterface = listener;
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        //startActivity(new Intent(NotificationListActivity.this, BookCanFormActivity.class));
        Intent addressIntent = new Intent(this,AddressFormActivity.class);
        addressIntent.putExtra("street_name",spinner_location);
        if(addressMap!=null)
            addressMap.put("locationName",this.spinner_location);
        addressIntent.putExtra("address_map",addressMap);
        //startMain.addCategory(Intent.CATEGORY_HOME);
        // startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(addressIntent);
        finish();
    }
}
