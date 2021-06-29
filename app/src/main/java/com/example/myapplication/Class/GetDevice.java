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

public class GetDevice extends IntentService {
    String jsontext,token;
    int statuscode=0;
    public static final int Jsonsresult = 1;
    public static final int Jsonsresultnull = 10;


    public GetDevice() {
        super("getdevice");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        ResultReceiver receiver = (ResultReceiver) intent.getParcelableExtra("receiver");


        SharedPreferences prefs = this.getSharedPreferences(
                "", Context.MODE_PRIVATE);

        String sharedprefencetoken = "";
        token= prefs.getString(sharedprefencetoken, "");

        if (token != null) {


            OkHttpClient client = new OkHttpClient();

            Request request = new Request.Builder()
                    .url("http://smartflow.sensiran.com:8585/api/client/devices")
                    .get()
                    .addHeader("authorization", "Bearer " + token)
                    .addHeader("cache-control", "no-cache")
                    .addHeader("postman-token", "072fb14c-4f7d-a183-07ee-c5323addee1c")
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws
                        IOException {
                    jsontext=response.body().string();

                    if (jsontext != null) {
                        try {
                            JSONObject jsonObj = new JSONObject(jsontext);

                            statuscode = jsonObj.getInt("status");

                            Bundle resultData = new Bundle();
                            resultData.putString("jsontext" ,jsontext);
                            resultData.putInt("statuscode" ,statuscode);
                            receiver.send(Jsonsresult, resultData);


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
            Bundle resultData = new Bundle();
            resultData.putInt("progress" ,100);
        }
        else if(token==null){
            Bundle resultData = new Bundle();
            resultData.putInt("statuscode" ,0);
            receiver.send(Jsonsresultnull, resultData);
        }

    }


}
