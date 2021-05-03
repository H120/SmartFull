package com.example.myapplication.Activity;

import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.Class.LocationTrack;
import com.example.myapplication.Fragment.QRScannerFragment;
import com.example.myapplication.R;
import com.google.android.material.textfield.TextInputEditText;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import me.itangqi.waveloadingview.WaveLoadingView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class AddDeviceActivity extends AppCompatActivity implements QRScannerFragment.SendMessage{

    String jsontext,token;
    int statuscode =0 ;
    Button adddevice_add,adddevice_location, qrrefresh;
    QRScannerFragment qrScannerFragment;
    FragmentManager fragmentManager_qrscan;
    TextInputEditText text_adddevice_name,text_adddevice_identity,text_adddevice_latitude,text_adddevice_longitude;

    @Override
    public void onBackPressed() {
        Intent intent=new Intent();
        setResult(1,intent);
        finish();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adddevice);

        SharedPreferences prefs = this.getSharedPreferences(
                "", Context.MODE_PRIVATE);

        String sharedprefencetoken = "";
        token= prefs.getString(sharedprefencetoken, "");

        text_adddevice_identity=(TextInputEditText)findViewById(R.id.text_adddevice_identity);
        text_adddevice_name=(TextInputEditText)findViewById(R.id.text_adddevice_name);
        text_adddevice_latitude=(TextInputEditText)findViewById(R.id.text_adddevice_latitude);
        text_adddevice_longitude=(TextInputEditText)findViewById(R.id.text_adddevice_longitude);
        qrrefresh =(Button)findViewById(R.id.qrrefresh);
        adddevice_add=(Button)findViewById(R.id.adddevice_add);
        adddevice_location=(Button)findViewById(R.id.adddevice_location);

        try {

            qrScannerFragment = new QRScannerFragment();
            fragmentManager_qrscan = getFragmentManager();
            fragmentManager_qrscan.beginTransaction().add(R.id.qrscannerfragment_holder, qrScannerFragment, null).commit();

        }catch (Exception e){
            Toast.makeText(getApplicationContext(),e.toString(),Toast.LENGTH_LONG).show();
            String content = "Add !"+e.toString();
            final Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.setType("text/plain");
            sendIntent.putExtra(Intent.EXTRA_TEXT, content);
            sendIntent.setPackage("com.Slack");
            startActivityForResult(sendIntent, 1092);
        }

        adddevice_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!text_adddevice_identity.getText().toString().isEmpty() && !text_adddevice_name.getText().toString().isEmpty() && !text_adddevice_latitude.getText().toString().isEmpty() && !text_adddevice_longitude.getText().toString().isEmpty()){
                    adddevice();
                }else if(text_adddevice_identity.getText().toString().isEmpty()){
                    Toast.makeText(getApplicationContext(),"Please inset Identity Code",Toast.LENGTH_SHORT).show();
                    text_adddevice_identity.requestFocus();
                }else if(text_adddevice_name.getText().toString().isEmpty()){
                    Toast.makeText(getApplicationContext(),"Please inset Name",Toast.LENGTH_SHORT).show();
                    text_adddevice_name.requestFocus();
                }else if(text_adddevice_latitude.getText().toString().isEmpty()){
                    Toast.makeText(getApplicationContext(),"Please inset Latitude",Toast.LENGTH_SHORT).show();
                    text_adddevice_latitude.requestFocus();
                }else if(text_adddevice_longitude.getText().toString().isEmpty()){
                    Toast.makeText(getApplicationContext(),"Please inset Longitude",Toast.LENGTH_SHORT).show();
                    text_adddevice_longitude.requestFocus();
                }
            }
        });

        adddevice_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(AddDeviceActivity.this, MapActivity.class);
                startActivityForResult(intent,2);

            }
        });

        qrrefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                qrrefresh.setVisibility(View.INVISIBLE);
                text_adddevice_identity.setText(null);
                qrScannerFragment.onResume();
            }
        });

    }

    private void adddevice(){
        MediaType mediaType=MediaType.get("application/json; charset=utf-8");
        OkHttpClient client = new OkHttpClient();
        RequestBody body = RequestBody.create(mediaType, "{\r\n    \"name\": \""+text_adddevice_name.getText().toString()+"\",\r\n    \"identity\": \""+text_adddevice_identity.getText().toString()+
                "\",\r\n    \"lon\": "+text_adddevice_longitude.getText().toString()+",\r\n    \"lat\": "+text_adddevice_latitude.getText().toString()+"\r\n}");

        Request request = new Request.Builder()
                .url("http://smartflow.sensiran.com:8080/api/client/devices")
                .post(body)
                .addHeader("accept", "application/json")
                .addHeader("content-type", "application/json")
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
                        Log.i("TAG", "onResponse: "+statuscode);
                        if (statuscode==200) {
                            getdevice();
                        }


                    } catch (final JSONException e) {
                        Log.e("TAG", "Json parsing error: " + e.getMessage());
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getApplicationContext(),
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

    @Override
    public void sendData(String message) {


        try {

            text_adddevice_identity.setText(message);
            Log.i("TAG", "sendData: "+message);
            qrrefresh.setVisibility(View.VISIBLE);

        }catch (Exception e){
            Toast.makeText(getApplicationContext(),e.toString(),Toast.LENGTH_LONG).show();
            String content = "Get !"+e.toString();
            final Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.setType("text/plain");
            sendIntent.putExtra(Intent.EXTRA_TEXT, content);
            sendIntent.setPackage("com.Slack");
            startActivityForResult(sendIntent, 1092);
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    // Call Back method  to get the Message from other Activity
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        // check if the request code is same as what is passed  here it is 2

        try {
            if (requestCode == 2) {
                String locationresult_lat = data.getStringExtra("locationresult_lat");
                String locationresult_lng = data.getStringExtra("locationresult_lng");
                text_adddevice_latitude.setText(locationresult_lat);
                text_adddevice_longitude.setText(locationresult_lng);
            }
        }catch (Exception e){}
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

                        Intent intent=new Intent();
                        intent.putExtra("jsontext",jsontext);
                        setResult(2,intent);
                        finish();

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
}