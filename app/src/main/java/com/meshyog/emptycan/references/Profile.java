package com.meshyog.emptycan.references;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.meshyog.emptycan.R;
import com.meshyog.emptycan.controller.BasicProfileEditActivity;
import com.meshyog.emptycan.controller.UserSignInActivity;
import com.meshyog.emptycan.model.database.EmptycanDataBase;

/**
 * Created by Viswanathan on 12/10/16.
 */
public class Profile extends AppCompatActivity {

    ImageView imageView;
    public SharedPreferences sharedPreferences;
    public   String userName;
    public   String dob;
    public   String emailId;
    public   String gender;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile);
        if(getSupportActionBar()!=null){
            getSupportActionBar().setTitle("Profile");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        TextView fullName=  (TextView)findViewById(R.id.consumerFullName);
        TextView consumerGender=  (TextView)findViewById(R.id.consumerGender);
        TextView consumerDob=  (TextView)findViewById(R.id.consumerDob);
        TextView consumerEmailId=  (TextView)findViewById(R.id.consumerEmailId);
        TextView  basicProfileEdit= (TextView)findViewById(R.id.basicProfileEdit);

        this.sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
         userName= sharedPreferences.getString(EmptycanDataBase.USER_NAME,"FirstName LastName");
         dob= sharedPreferences.getString(EmptycanDataBase.USER_DOB,"00-00-0000");
         emailId= sharedPreferences.getString(EmptycanDataBase.USER_EMAIL,"noemail@xxxx.com");
         gender= sharedPreferences.getString(EmptycanDataBase.USER_GENDER,"male/female/transgender");
        fullName.setText(userName);
        consumerGender.setText(gender);
        consumerDob.setText(dob);
        consumerEmailId.setText(emailId);
        basicProfileEdit.setOnClickListener(listener);
        imageView=(ImageView)findViewById(R.id.profile_image);
        Resources resources=getResources();
        BitmapFactory.Options options=new BitmapFactory.Options();
        options.inPreferredConfig= Bitmap.Config.RGB_565;
        Bitmap source= BitmapFactory.decodeResource(resources,R.drawable.dog,options);
        RoundedBitmapDrawable drawable= RoundedBitmapDrawableFactory.create(resources,source);
        drawable.setCornerRadius(Math.max(source.getWidth(),source.getHeight())/2f);
        drawable.setCircular(true);
        imageView.setImageDrawable(drawable);
    }
    private View.OnClickListener listener=new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch(view.getId()){
                case R.id.basicProfileEdit:
                    Intent myIntent = new Intent(getApplicationContext(), BasicProfileEditActivity.class);
                    myIntent.putExtra(EmptycanDataBase.USER_NAME,userName);
                    myIntent.putExtra(EmptycanDataBase.USER_EMAIL,emailId);
                    myIntent.putExtra(EmptycanDataBase.USER_DOB,dob);
                    myIntent.putExtra(EmptycanDataBase.USER_GENDER,gender);
                    startActivity(myIntent);
                    finish();
                    break;

                case R.id.already_registered_user:

                    // onBackPressed();
                    break;

            }
        }
    };
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.profile, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id ==android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

}
