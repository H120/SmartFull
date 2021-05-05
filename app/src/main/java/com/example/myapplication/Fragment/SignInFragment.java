package com.example.myapplication.Fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.myapplication.Activity.MainActivity;
import com.example.myapplication.Activity.SignActivity;
import com.example.myapplication.R;

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

public class SignInFragment extends Fragment {
    public SignInFragment() {
        // Required empty public constructor
    }

    String response, username, password, fcm_token, token, jsontext;
    SharedPreferences prefs;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View myView = inflater.inflate(R.layout.fragment_signin, container, false);

        prefs = getContext().getSharedPreferences(
                "", Context.MODE_PRIVATE);

        Button signin_button = myView.findViewById(R.id.signin_button);
        EditText signin_username = myView.findViewById(R.id.signin_username);
        EditText signin_password = myView.findViewById(R.id.signin_password);

        signin_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                username = signin_username.getText().toString();
                password = signin_password.getText().toString();
                fcm_token = "ssss";
                sign_in();
            }
        });

        return myView;

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
            Log.i("TAG", "fetchlogin 2: "+json);

            try {
                JSONObject jsonObj = new JSONObject(json);
                int statuscode = jsonObj.getInt("status");

                if (statuscode==200) {

                    JSONObject data = jsonObj.getJSONObject("result");
                    String jsontoken = data.optString("token");
                    Log.i("TAG", "fetchlogin: "+jsontoken);

                    token=jsontoken;
                    String sharedprefencetoken = "";
                    prefs.edit().putString(sharedprefencetoken, token).apply();

                    getdevice();


                } else {
                    Log.e("message", "failure");
                }

            } catch (final JSONException e) {
                Log.e("TAG", "Json parsing error: " + e.getMessage());
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getActivity(),
                                "Json parsing error: " + e.getMessage(),
                                Toast.LENGTH_LONG).show();
                    }
                });

            }

        } else {
            Log.e("TAG", "Couldn't get json from server.");
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getActivity(),
                            "Couldn't get json from server. Check LogCat for possible errors!",
                            Toast.LENGTH_LONG).show();
                }
            });

        }
        return token;
    }

    private void getdevice(){

        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url("http://smartflow.sensiran.com:8080/api/client/devices")
                .get()
                .addHeader("authorization", "Bearer "+token)
                .addHeader("cache-control", "no-cache")
                .addHeader("postman-token", "072fb14c-4f7d-a183-07ee-c5323addee1c")
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                jsontext=response.body().string();
                if (jsontext != null) {

                    try {
                        JSONObject jsonObj = new JSONObject(jsontext);

                        int statuscode = jsonObj.getInt("status");

                        if (statuscode==200) {

                            Intent intent = new Intent(getActivity(), MainActivity.class);
                            intent.putExtra("jsontext", jsontext);
//                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP );
                            startActivity(intent);
                            getActivity().finish();
                        }
                        else {
                            Log.e("message", "failure");
                        }

                    } catch (final JSONException e) {
                        Log.e("TAG", "Json parsing error: " + e.getMessage());
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getActivity(),
                                        "Json parsing error: " + e.getMessage(),
                                        Toast.LENGTH_LONG).show();
                            }
                        });

                    }

                }

            }

            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                e.printStackTrace();
            }

        });

    }

}