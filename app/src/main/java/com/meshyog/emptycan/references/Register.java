package com.meshyog.emptycan.references;

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
public class Register extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_user);
        Button register=(Button)findViewById(R.id.register_register_user);
        LinearLayout login=(LinearLayout)findViewById(R.id.already_registered_user);

        register.setOnClickListener(listener);
        login.setOnClickListener(listener);
    }

    private View.OnClickListener listener=new View.OnClickListener() {
        @Override
        public void onClick(View view) {
           switch(view.getId()){
               case R.id.register_register_user:
                   onBackPressed();
                   break;

               case R.id.already_registered_user:
                   onBackPressed();
                   break;

            }
        }
    };
}
