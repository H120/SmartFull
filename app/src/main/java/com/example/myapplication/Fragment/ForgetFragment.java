package com.example.myapplication.Fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

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

public class ForgetFragment extends Fragment {
    public ForgetFragment() {
        // Required empty public constructor
    }

    String email;
    static AlertDialog alertDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View myView = inflater.inflate(R.layout.fragment_forget, container, false);

        Button signin_button = myView.findViewById(R.id.signin_button);
        EditText forget_email = myView.findViewById(R.id.forget_email);

        signin_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                email = forget_email.getText().toString();
                forget();
            }
        });

        return myView;

    }

    private void forget()
    {

        OkHttpClient client = new OkHttpClient();

        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(mediaType, "{\r\n    \"email\": \""+email+"\"\r\n}");
        Request request = new Request.Builder()
                .url("http://smartflow.sensiran.com:8080/api/client/authentication/forgot_password")
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

    private void fetchlogin(String json){

        if (json != null) {

            try {
                JSONObject jsonObj = new JSONObject(json);

                int statuscode = jsonObj.getInt("status");
                String message = jsonObj.getString("message");
                Log.i("TAG", "fetchlogin json: "+statuscode);

                switch (statuscode){
                    case 200:
                        backgroundThreadShortAlertdialog(getActivity(),"We Send Email Soon!");
                        break;
                    case 429:
                        backgroundThreadShortAlertdialog(getActivity(),message);
                        break;
                    case 400:
                        backgroundThreadShortAlertdialog(getActivity(),message);
                        break;
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
    }
    public static void backgroundThreadShortAlertdialog(final Context context,
                                                        final String message) {
        if (context != null && message != null) {
            new Handler(Looper.getMainLooper()).post(new Runnable() {

                @Override
                public void run() {
                    alertDialog = new AlertDialog.Builder(context)
                            .setTitle("Result")
                            .setMessage(message)

                            // Specifying a listener allows you to take an action before dismissing the dialog.
                            // The dialog is automatically dismissed when a dialog button is clicked.
                            .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    // Continue with delete operation
                                }
                            }).setIcon(android.R.drawable.ic_dialog_alert)
                            .show();

                }
            });
        }
    }
}