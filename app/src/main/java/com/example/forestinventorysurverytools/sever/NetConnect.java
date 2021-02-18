package com.example.forestinventorysurverytools.sever;

import android.app.Application;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class NetConnect extends Application {

    private static NetConnect instance;
    private static NetConnect getInstance(){return instance;}

    @Override
    public void onCreate() {
        super.onCreate();
        NetConnect.instance = this;
    }

    private NetService netService;
    public NetService getNetService() {return netService;}

    private String baseUrl;
    public void buildNetworkService(String url){
        synchronized (NetConnect.class){
            if(netService==null){

                Gson gson = new GsonBuilder()
                        .setLenient()
                        .create();

                baseUrl = url;
                Log.d("tag","연결");
                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl(baseUrl)
                        .addConverterFactory(ScalarsConverterFactory.create())
                        .addConverterFactory(GsonConverterFactory.create(gson))
                        .build();
                netService = retrofit.create(NetService.class);
            }
        }
    }

}
