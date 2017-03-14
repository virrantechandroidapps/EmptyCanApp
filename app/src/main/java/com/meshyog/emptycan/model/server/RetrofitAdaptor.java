package com.meshyog.emptycan.model.server;

import android.widget.EditText;

import com.AppConstants;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.squareup.okhttp.OkHttpClient;


import java.util.concurrent.TimeUnit;

import retrofit.GsonConverterFactory;
import retrofit.Retrofit;
import retrofit.RxJavaCallAdapterFactory;

/**
 * Created by Aniruthan on 12/13/2015.
 */
public class RetrofitAdaptor {

    private static Retrofit retrofit = null;

    public static Retrofit getRetrofit() {
        if (retrofit == null) {
            OkHttpClient okHttpClient = new OkHttpClient();
            okHttpClient.setReadTimeout(60, TimeUnit.SECONDS);
            okHttpClient.setConnectTimeout(60, TimeUnit.SECONDS);
            okHttpClient.setWriteTimeout(60, TimeUnit.SECONDS);
                    /*.connectTimeout(60, TimeUnit.SECONDS)
                    .readTimeout(60, TimeUnit.SECONDS)
                    .writeTimeout(60, TimeUnit.SECONDS)*/
                    //.build();
            Gson gson = new GsonBuilder().setLenient().create();
            retrofit =
                    new Retrofit.Builder()
                            .addConverterFactory(GsonConverterFactory.create(gson))
                            .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                            .baseUrl(AppConstants.baseContext).client(okHttpClient).build();
//http://162.211.109.215:13030/emptycan/ --mongodb realtime server --http://192.168.0.118:8080/fcds/
            //http://172.21.5.80:8080/fcds
            //http://172.21.5.80:8080/fcds/
            //http://192.168.0.118:8080/fcds
        }
        return retrofit;
    }

}
