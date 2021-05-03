package com.example.myapplication.Class;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class GetDevice {
    String sharedprefencetoken = "";
    String jsontext,token;
    int statuscode=0;

    public String getdevice(SharedPreferences sharedPreferences)

    {

        SharedPreferences prefs =sharedPreferences;


        token = prefs.getString(sharedprefencetoken, "");

        if (token != null) {


            OkHttpClient client = new OkHttpClient();

            Request request = new Request.Builder()
                    .url("http://smartflow.sensiran.com:8080/api/client/devices")
                    .get()
                    .addHeader("authorization", "Bearer " + token)
                    .addHeader("cache-control", "no-cache")
                    .addHeader("postman-token", "072fb14c-4f7d-a183-07ee-c5323addee1c")
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws
                        IOException {
                    jsontext = response.body().string();

                    if (jsontext != null) {

                        try {
                            JSONObject jsonObj = new JSONObject(jsontext);

                            statuscode = jsonObj.getInt("status");

                        } catch (final JSONException e) {
                            Log.e("TAG", "Json parsing error: " + e.getMessage());


                        }

                    }

                }

                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    e.printStackTrace();
                }

            });
        }
        return jsontext;

    }

}
