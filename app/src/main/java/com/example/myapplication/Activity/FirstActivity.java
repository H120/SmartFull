package com.example.myapplication.Activity;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.R;
import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.util.ArrayList;

import me.itangqi.waveloadingview.WaveLoadingView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.CAMERA;

public class FirstActivity extends AppCompatActivity {

    String jsontext,userjsontext,token;
    private int mInterval = 200;
    private Handler mHandler;
    WaveLoadingView waveView;
    int waterlevelratio=100;
    int statuscode=0, statuscodeuser=0, runed =0;
    private ArrayList<String> permissionsToRequest;
    private ArrayList<String> permissionsRejected = new ArrayList<>();
    private final static int ALL_PERMISSIONS_RESULT = 101;
    private ArrayList<String> permissionslist = new ArrayList<>();
    TextView button_retry;
    String user_id ,user_first_name ,user_last_name ,user_avatar ,user_email,user_mobile,user_created_at,user_updated_at;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first);

        permissionslist.add(ACCESS_FINE_LOCATION);
        permissionslist.add(ACCESS_COARSE_LOCATION);
        permissionslist.add(CAMERA);

        permissionsToRequest = findUnAskedPermissions(permissionslist);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (permissionsToRequest.size() > 0)
                requestPermissions(permissionsToRequest.toArray(new String[permissionsToRequest.size()]), ALL_PERMISSIONS_RESULT);
        }


        waveView=(WaveLoadingView)findViewById(R.id.wave_view);
        button_retry=(TextView)findViewById(R.id.button_retry);

        SharedPreferences prefs = this.getSharedPreferences(
                "", Context.MODE_PRIVATE);

        String sharedprefencetoken = "";
        token= prefs.getString(sharedprefencetoken, "");



        if (!checkinternet()) {
            button_retry.setVisibility(View.VISIBLE);
            waveView.setProgressValue(80);
            Toast.makeText(this, "Please Connect to The Internet and Try Agarin !", Toast.LENGTH_LONG).show();

        }else {
            button_retry.setVisibility(View.GONE);

            if (token!=null){
                getdevice();
            }
            mHandler = new Handler();
            mStatusChecker.run();
        }



        button_retry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (checkinternet()){
                    if (token!=null){
                        getdevice();
                    }
                    mHandler = new Handler();
                    mStatusChecker.run();
                    button_retry.setVisibility(View.GONE);

                }else{
                    button_retry.setVisibility(View.VISIBLE);
                    waveView.setProgressValue(80);
                    Toast.makeText(getApplicationContext(),"Please Connect to The Internet and Try Agarin !", Toast.LENGTH_LONG).show();
                }


            }
        });



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

                        statuscode = jsonObj.getInt("status");
                        getuser();
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

    private void getuser(){

        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url("http://smartflow.sensiran.com:8080/api/client/user")
                .get()
                .addHeader("authorization", "Bearer "+token)
                .addHeader("cache-control", "no-cache")
                .addHeader("postman-token", "072fb14c-4f7d-a183-07ee-c5323addee1c")
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                userjsontext=response.body().string();
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


                    } catch (final JSONException e) {
                        Log.e("TAG", "Json parsing error: " + e.getMessage());
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
//                                Toast.makeText(getApplicationContext(),
//                                        "Json parsing error: " + e.getMessage(),
//                                        Toast.LENGTH_LONG).show();
                            }
                        });

                    }

                } else {
                    Log.e("TAG", "Couldn't get json from server.");
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
//                            Toast.makeText(getApplicationContext(),
//                                    "Couldn't get json from server. Check LogCat for possible errors!",
//                                    Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }

            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                e.printStackTrace();
            }

        });

    }

    Runnable mStatusChecker = new Runnable() {
        @Override
        public void run() {

            try {
                if (waterlevelratio>-10) {
                    waterlevelratio = waterlevelratio - 10;
                    waveView.setProgressValue(waterlevelratio);
                }
                else if(waterlevelratio<=-10 && runed==0){
                    runed=1;
                    gotomap();
                }
            } finally {
                // 100% guarantee that this always happens, even if
                // your update method throws an exception
                mHandler.postDelayed(mStatusChecker, mInterval);
            }

        }
    };

    private void gotomap(){
        Log.i("TAG", "gotomap: "+statuscode);
        if (statuscode==200 && statuscodeuser==200) {
            Intent intent = new Intent(FirstActivity.this, MainActivity.class);
            intent.putExtra("jsontext", jsontext);
            intent.putExtra("user_id", user_id);
            intent.putExtra("user_first_name", user_first_name);
            intent.putExtra("user_last_name", user_last_name);
            intent.putExtra("user_avatar", user_avatar);
            intent.putExtra("user_email", user_email);
            intent.putExtra("user_mobile", user_mobile);
            intent.putExtra("user_created_at", user_created_at);
            intent.putExtra("user_updated_at", user_updated_at);
            startActivity(intent);
            finish();

        }else if(statuscode==401){
            Intent intent = new Intent(FirstActivity.this, SignActivity.class);
            startActivity(intent);
            finish();
        }
        else {
            Log.e("message", "failure");
        }
    }


    private boolean hasPermission(Object permission) {
        if (canMakeSmores()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                return (checkSelfPermission((String) permission) == PackageManager.PERMISSION_GRANTED);
            }
        }
        return true;
    }

    private boolean canMakeSmores() {
        return (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1);
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        switch (requestCode) {

            case ALL_PERMISSIONS_RESULT:
                for (String perms : permissionsToRequest) {
                    if (!hasPermission(perms)) {
                        permissionsRejected.add(perms);
                        AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(this);
                        dlgAlert.setMessage("For Run This Program We Need Permission\nPlease Grant in Setting");
                        dlgAlert.setTitle("Permission");
                        dlgAlert.setCancelable(false);
                        dlgAlert.setPositiveButton("Ok",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {

                                        finish();
                                    }
                                });
                        dlgAlert.create().show();

                    }
                }

                if (permissionsRejected.size() > 0) {


                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (shouldShowRequestPermissionRationale(permissionsRejected.get(0))) {
                            showMessageOKCancel("These permissions are mandatory for the application. Please allow access.",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {

                                        }
                                    });
                            return;
                        }
                    }

                }

                break;
        }

    }
    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
//        new AlertDialog.Builder(FirstActivity.this)
//                .setMessage(message)
//                .setPositiveButton("OK", okListener)
//                .setNegativeButton("Cancel", null)
//                .create()
//                .show();
    }

    private ArrayList findUnAskedPermissions(ArrayList wanted) {
        ArrayList result = new ArrayList();

        for (Object perm : wanted) {
            if (!hasPermission(perm)) {
                result.add(perm);
            }
        }

        return result;
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
    }

    private boolean checkinternet(){
        ConnectivityManager connectivityManager = (ConnectivityManager) this.getSystemService(this.CONNECTIVITY_SERVICE);
        if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            return true;
        }else {
            return false;
        }
    }

}