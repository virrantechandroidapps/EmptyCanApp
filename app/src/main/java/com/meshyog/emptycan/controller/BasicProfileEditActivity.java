package com.meshyog.emptycan.controller;

import android.app.Activity;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.AppConstants;
import com.google.gson.JsonObject;
import com.meshyog.emptycan.R;
import com.meshyog.emptycan.model.AppUtils;
import com.meshyog.emptycan.model.database.EmptycanDataBase;
import com.meshyog.emptycan.model.server.RetrofitAdaptor;
import com.meshyog.emptycan.model.server.WebServiceInterface;
import com.meshyog.emptycan.references.Profile;
import com.meshyog.emptycan.references.SpinnerAdapter;
import com.meshyog.emptycan.view.DatePickerFragment;

import java.util.ArrayList;
import java.util.Calendar;

import cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

/**
 * Created by varadhan on 21-12-2016.
 */
public class BasicProfileEditActivity extends AppCompatActivity {

    public Spinner consumerGender;
    public EditText  consumerFullName;
    public static EditText consumerDob;
    public EditText consumerEmailId;
    public   String userName;
    public   String dob;
    public   String emailId;
    public   String gender;
    public String selectedGender;
    public EmptycanDataBase  emptycanDataBase;
    public Button updateBtn;
    SweetAlertDialog pDialog = null;
    public SharedPreferences sharedPreferences;
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.basic_profile_edit);
       if( AppUtils.isNetworkAvailable(getApplicationContext())){
            try{

                if(getSupportActionBar()!=null){
                    getSupportActionBar().setTitle("Basic Profile Edit");
                    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                }
                ArrayList<String> type=new ArrayList<String>();
                type.add("MALE");
                type.add("FEMALE");
                type.add("TRANSGENDER");
                this.sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                pDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
                consumerGender = (Spinner) findViewById(R.id.cnsumerGender);
                consumerFullName = (EditText) findViewById(R.id.cnsumrFullName);
                consumerDob = (EditText) findViewById(R.id.cnsumerDob);
                consumerEmailId = (EditText) findViewById(R.id.cnsumrEmailId);
                updateBtn=(Button) findViewById(R.id.basicProfileEditBtn);
                updateBtn.setOnClickListener(listener);
                SpinnerAdapter spinnerAdapter=new SpinnerAdapter(getApplicationContext(),type);
                consumerGender.setAdapter(spinnerAdapter);
                consumerGender.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                        selectedGender= (String) parent.getItemAtPosition(pos);

                    } public void onNothingSelected(AdapterView<?> parent) {
                    }});
                Intent intent= getIntent();
                userName= intent.getExtras().get(EmptycanDataBase.USER_NAME).toString();
                emailId= intent.getExtras().get(EmptycanDataBase.USER_EMAIL).toString();
                dob=intent.getExtras().get(EmptycanDataBase.USER_DOB).toString();
                gender=intent.getExtras().get(EmptycanDataBase.USER_GENDER).toString();
                int position=0;
                if(gender.equals("MALE"))
                    position=0;
                else if(gender.equals("FEMALE"))
                    position=1;
                else if(gender.equals("TRANSGENDER"))
                    position=2;
                consumerFullName.setText(userName);
                consumerGender.setSelection(position);
                consumerDob.setText(dob);
                consumerEmailId.setText(emailId);
                emptycanDataBase=new EmptycanDataBase(getApplicationContext());

            }catch(Exception e){
                e.printStackTrace();
            }
        }else{
            mAlertDaiog("Check your internet connectivity", true);
        }


    }
    public void mAlertDaiog(String message, boolean isBack) {
        android.support.v7.app.AlertDialog.Builder alertDialogBuilder = new android.support.v7.app.AlertDialog.Builder(this);
        alertDialogBuilder.setMessage((CharSequence) message);
        alertDialogBuilder.setCancelable(false);
        alertDialogBuilder.setPositiveButton("OK", new BasicProfileDialogListner(isBack));
        alertDialogBuilder.create().show();
    }
    class BasicProfileDialogListner implements DialogInterface.OnClickListener {
        final /* synthetic */ boolean val$isBack;

        BasicProfileDialogListner(boolean z) {
            this.val$isBack = z;
        }

        public void onClick(DialogInterface arg0, int arg1) {
            if (this.val$isBack) {
                BasicProfileEditActivity.this.finish();
            }
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.profile, menu);
        return true;
    }
    private View.OnClickListener listener=new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch(view.getId()){
                case R.id.basicProfileEditBtn:
                    try{
                        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
                        pDialog.setTitleText("Loading");
                        pDialog.setCancelable(false);
                        pDialog.show();
                        JsonObject profileData=new JsonObject();
                        profileData.addProperty("consumerName",consumerFullName.getText().toString());
                        profileData.addProperty("consumerDOB",consumerDob.getText().toString());
                        profileData.addProperty("consumerEmailId",consumerEmailId.getText().toString());
                        profileData.addProperty("consumerGender",selectedGender);
                        profileData.addProperty("consumerProfileId", emptycanDataBase.getConsumerKey() );

                    /*    consumerGender = (Spinner) findViewById(R.id.cnsumerGender);
                        consumerFullName = (EditText) findViewById(R.id.cnsumrFullName);
                        consumerDob = (EditText) findViewById(R.id.cnsumerDob);
                        consumerEmailId = (EditText) findViewById(R.id.cnsumrEmailId);*/
                        Retrofit retrofit =null;
                        WebServiceInterface webServiceInterface=null;
                        Call<JsonObject> call=null;
                        retrofit = RetrofitAdaptor.getRetrofit();
                        webServiceInterface = retrofit.create(WebServiceInterface.class);
                        call = webServiceInterface.consumerAddBasicProfile(profileData);
                        call.enqueue(new Callback<JsonObject>() {
                            @Override
                            public void onResponse(Response<JsonObject> response, Retrofit retrofit) {
                                if (response.message().equals("OK")) {
                                    JsonObject result= response.body().getAsJsonObject();
                                    if(result.get("status").getAsString().equals("success")){
                                        updateUserProfileData();
                                    }


                                } else {
                                    //request not successful (like 400,401,403 etc)
                                    //Handle errors

                                }
                                pDialog.dismiss();
                            }

                            @Override
                            public void onFailure(Throwable t) {
                                JsonObject jsonObject = new JsonObject();
                                jsonObject.addProperty("result_type", t.getMessage());
                                jsonObject.addProperty("result", t.getMessage());
                                pDialog.dismiss();
                                //((SignupActivity)signUpContext).successNotice(jsonObject);
                                // SignupActivity signupActivityy = new SignupActivity();

                            }
                        });
                    }catch(Exception e){
                        e.printStackTrace();
                        pDialog.dismiss();
                    }


                    break;



            }
        }
    };
    public void updateUserProfileData(){
       /* userName= intent.getExtras().get().toString();
        emailId= intent.getExtras().get(EmptycanDataBase.USER_EMAIL).toString();
        dob=intent.getExtras().get(EmptycanDataBase.USER_DOB).toString();
        gender=intent.getExtras().get(EmptycanDataBase.USER_GENDER).toString();*/
        SharedPreferences.Editor editor= this.sharedPreferences.edit();
        editor.putString(EmptycanDataBase.USER_NAME,consumerFullName.getText().toString());
        editor.putString(EmptycanDataBase.USER_DOB,consumerDob.getText().toString());
        editor.putString(EmptycanDataBase.USER_GENDER,selectedGender);
        editor.putString(EmptycanDataBase.USER_EMAIL,consumerEmailId.getText().toString());
        editor.commit();
        Intent orderDetailActivity = new Intent(this,UserProfileActivity.class);
        startActivity(orderDetailActivity);
        finish();
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id ==android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent orderDetailActivity = new Intent(this,UserProfileActivity.class);
        startActivity(orderDetailActivity);
        finish();
    }

    public class SpinnerAdapter extends BaseAdapter implements android.widget.SpinnerAdapter {

        Context context;
        ArrayList<String> list;


        public SpinnerAdapter(Context context, ArrayList<String> list) {
            this.context = context;
            this.list = list;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {

            TextView txt = new TextView(context);
            txt.setGravity(Gravity.CENTER);
            txt.setPadding(16, 16, 16, 16);
            txt.setTextSize(16);
          //  txt.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.mipmap.deliveryicon, 0);
            txt.setText(list.get(i));
            txt.setTextColor(Color.parseColor("#000000"));
            return  txt;
        }

        @Override
        public long getItemId(int i) {
            return (long)i;
        }

        @Override
        public Object getItem(int i) {
            return list.get(i);
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent) {
            TextView txt = new TextView(context);
            txt.setPadding(16, 16, 16, 16);
            txt.setTextSize(18);
            txt.setGravity(Gravity.CENTER_VERTICAL);
            txt.setText(list.get(position));
            txt.setTextColor(Color.parseColor("#000000"));
            return  txt;
        }
    }
    public void showTimePickerDialog(View v) {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

    public static class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            // Do something with the date chosen by the user
            setUpDate(year,month,day);

        }
    }
    public static void setUpDate(int year, int month, int day){
        consumerDob.setText(day+"-"+month+"-"+year);
    }
}
