package com.meshyog.emptycan.references;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.meshyog.emptycan.R;

/**
 * Created by Viswanathan on 17/10/16.
 */
public class Login extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_user);
        Button login=(Button)findViewById(R.id.signin_user_login);
        LinearLayout registerHere=(LinearLayout)findViewById(R.id.new_user_register);
        login.setOnClickListener(listener);
        registerHere.setOnClickListener(listener);
    }

    private View.OnClickListener listener=new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch(view.getId()){
                case R.id.signin_user_login:
                    Intent intent=new Intent(Login.this,FavouritesList.class);
                    startActivity(intent);
                    break;

                case R.id.new_user_register:
                    Intent registerIntent=new Intent(Login.this,Register.class);
                    startActivity(registerIntent);
                    break;

            }
        }
    };
}
