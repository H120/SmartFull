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

public class GetUser extends IntentService {

    String userjsontext,token;
    int statuscodeuser=0;
    public static final int Jsonsresult = 2;
    String user_id ,user_first_name ,user_last_name ,user_avatar ,user_email,user_mobile,user_created_at,user_updated_at;

    public GetUser() {
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
                    .url("http://smartflow.sensiran.com:8080/api/client/user")
                    .get()
                    .addHeader("authorization", "Bearer " + token)
                    .addHeader("cache-control", "no-cache")
                    .addHeader("postman-token", "072fb14c-4f7d-a183-07ee-c5323addee1c")
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    userjsontext = response.body().string();
                    if (userjsontext != null) {
                        try {

                            JSONObject jsonObj = new JSONObject(userjsontext);

                            statuscodeuser = jsonObj.getInt("status");
                            String userdata = jsonObj.getString("result");

                            JSONObject jsonObj1 = new JSONObject(userdata);

                            user_id = jsonObj1.getString("id");
                            user_first_name = jsonObj1.getString("first_name");
                            user_last_name = jsonObj1.getString("last_name");
                            user_avatar = jsonObj1.getString("avatar");
                            user_email = jsonObj1.getString("email");
                            user_mobile = jsonObj1.getString("mobile");
                            user_created_at = jsonObj1.getString("created_at");
                            user_updated_at = jsonObj1.getString("updated_at");


                            Bundle resultData = new Bundle();
                            try {
                                resultData.putInt("statuscodeuser", statuscodeuser);
                                resultData.putString("user_id", user_id);
                                resultData.putString("user_first_name", user_first_name);
                                resultData.putString("user_last_name", user_last_name);
                                resultData.putString("user_avatar", user_avatar);
                                resultData.putString("user_email", user_email);
                                resultData.putString("user_mobile", user_mobile);
                                resultData.putString("user_created_at", user_created_at);
                                resultData.putString("user_updated_at", user_updated_at);
                            }catch (Exception e){
                                Log.e("TAG", "onResponse: ",e );
                            }
                            receiver.send(Jsonsresult, resultData);

                        } catch (final JSONException e) {
                            Log.e("TAG", "Json parsing error: " + e.getMessage());

                        }

                    } else {
                        Log.e("TAG", "Couldn't get json from server.");

                    }
                }

                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    e.printStackTrace();
                }

            });
        }
    }


}
