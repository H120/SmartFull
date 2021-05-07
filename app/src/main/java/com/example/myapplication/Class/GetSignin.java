package com.example.myapplication.Class;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.util.Log;
import androidx.annotation.Nullable;
import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class GetSignin extends IntentService {

    String signinjsontext,token;
    public static final int Jsonsresult = 3;
    String username, password;
    SharedPreferences prefs;
    ResultReceiver receiver;
    public GetSignin() {
        super("getsignin");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        receiver = (ResultReceiver) intent.getParcelableExtra("receiver");
        username = (String) intent.getStringExtra("username");
        password = (String) intent.getStringExtra("password");

        prefs = getApplicationContext().getSharedPreferences(
                "", Context.MODE_PRIVATE);
        SharedPreferences prefs = this.getSharedPreferences(
                "", Context.MODE_PRIVATE);

        String sharedprefencetoken = "";
        token= prefs.getString(sharedprefencetoken, "");

        sign_in();
    }

    private void sign_in()
    {

        OkHttpClient client = new OkHttpClient();

        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(mediaType, "{\r\n    \"email\": \""+username+"\",\r\n    \"password\": \""+password+"\",\r\n    \"fcm_token\": \"ssss\"\r\n}");
        Request request = new Request.Builder()
                .url("http://smartflow.sensiran.com:8080/api/client/authentication/login")
                .post(body)
                .addHeader("accept", "application/json")
                .addHeader("content-type", "application/json")
                .addHeader("authorization", "{{client_token}}")
                .addHeader("cache-control", "no-cache")
                .addHeader("postman-token", "8deec5e0-6522-1d78-7577-98f0168925b7")
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String s=response.body().string();
                fetchlogin(s);
            }

            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                e.printStackTrace();

            }
        });
    }


    private String fetchlogin(String json){

        if (json != null) {

            try {
                JSONObject jsonObj = new JSONObject(json);
                int statuscode = jsonObj.getInt("status");

                if (statuscode==200) {

                    JSONObject data = jsonObj.getJSONObject("result");
                    String jsontoken = data.optString("token");
                    token=jsontoken;
                    String sharedprefencetoken = "";
                    prefs.edit().putString(sharedprefencetoken, token).apply();
                    Bundle resultData = new Bundle();
                    resultData.putString("jsontext" ,signinjsontext);
                    resultData.putInt("statuscode" ,statuscode);
                    receiver.send(Jsonsresult, resultData);



                } else{
                    Bundle resultData = new Bundle();
                    resultData.putString("jsontext" ,signinjsontext);
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