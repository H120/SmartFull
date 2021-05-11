package com.example.myapplication.Class;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.util.Log;

import androidx.annotation.Nullable;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class DeleteDevice extends IntentService {

    String deletejsontext,token;
    public static final int Jsonsresult = 4;
    String deletedeviceid;
    SharedPreferences prefs;
    ResultReceiver receiver;
    public DeleteDevice() {
        super("deletedevice");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        receiver = (ResultReceiver) intent.getParcelableExtra("receiver");
        deletedeviceid = (String) intent.getStringExtra("deletedeviceid");
        Log.i("TAG", "fetchelete: 33");

        prefs = getApplicationContext().getSharedPreferences(
                "", Context.MODE_PRIVATE);
        SharedPreferences prefs = this.getSharedPreferences(
                "", Context.MODE_PRIVATE);

        String sharedprefencetoken = "";
        token= prefs.getString(sharedprefencetoken, "");

        delete_device();
    }

    private void delete_device()
    {

        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url("http://smartflow.sensiran.com:8080/api/client/devices/"+deletedeviceid)
                .delete()
                .addHeader("acce1pt", "application/json")
                .addHeader("content-type", "application/json")
                .addHeader("authorization", "Bearer " + token)
                .addHeader("cache-control", "no-cache")
                .addHeader("postman-token", "8deec5e0-6522-1d78-7577-98f0168925b7")
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String s=response.body().string();
                fetchelete(s);
            }

            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                e.printStackTrace();

            }
        });
    }


    private String fetchelete(String json){

        if (json != null) {

            try {
                JSONObject jsonObj = new JSONObject(json);
                int statuscode = jsonObj.getInt("status");
                if (statuscode==200) {
                    Log.i("TAG", "fetchelete: "+statuscode);

                    Bundle resultData = new Bundle();
                    resultData.putString("deletejsontext" ,deletejsontext);
                    resultData.putInt("statuscode" ,statuscode);
                    receiver.send(Jsonsresult, resultData);



                } else{
                    Bundle resultData = new Bundle();
                    resultData.putString("jsontext" ,deletejsontext);
                    resultData.putInt("statuscode" ,statuscode);
                    receiver.send(Jsonsresult, resultData);

                    Log.e("message", "failure");
                }

            } catch (final JSONException e) {
                Log.e("TAG", "Json parsing error: " + e.getMessage());
            }

        } else {
            Log.e("TAG", "Couldn't get json from server.");

        }
        return token;
    }
}