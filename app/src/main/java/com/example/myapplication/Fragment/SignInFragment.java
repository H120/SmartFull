package com.example.myapplication.Fragment;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.example.myapplication.Activity.FirstActivity;
import com.example.myapplication.Activity.MainActivity;
import com.example.myapplication.Activity.SignActivity;
import com.example.myapplication.Class.GetDevice;
import com.example.myapplication.Class.GetSignin;
import com.example.myapplication.Class.GetUser;
import com.example.myapplication.R;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class SignInFragment extends Fragment {
    public SignInFragment() {
        // Required empty public constructor
    }

    String response, username, password, fcm_token, token, jsontext;
    Intent intentgetsignin;
    ProgressDialog progressDialog;
    AlertDialog alertDialog;
    Intent intentgetdevice, intentgetuser;
    int statuscode = 0, statuscodeuser = 0;
    String user_id, user_first_name, user_last_name, user_avatar, user_email, user_mobile, user_created_at, user_updated_at;
    boolean igonrerun = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View myView = inflater.inflate(R.layout.fragment_signin, container, false);

        Button signin_button = myView.findViewById(R.id.signin_button);
        EditText signin_username = myView.findViewById(R.id.signin_username);
        EditText signin_password = myView.findViewById(R.id.signin_password);
        intentgetsignin = new Intent(getActivity(), GetSignin.class);

        intentgetdevice = new Intent(getActivity(), GetDevice.class);
        intentgetuser = new Intent(getActivity(), GetUser.class);

        signin_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                progressDialog = new ProgressDialog(getActivity());
                progressDialog.setTitle("Please Wait");
                progressDialog.setCancelable(false);
                progressDialog.show();

                username = signin_username.getText().toString();
                password = signin_password.getText().toString();
                fcm_token = "ssss";

                intentgetsignin.putExtra("username", username);
                intentgetsignin.putExtra("password", password);
                intentgetsignin.putExtra("receiver", new DataReciverSignin(new Handler()));
                getActivity().startService(intentgetsignin);
            }
        });

        return myView;

    }

    public class DataReciverSignin extends ResultReceiver {

        public DataReciverSignin(Handler handler) {
            super(handler);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {
            super.onReceiveResult(resultCode, resultData);

            if (resultCode == GetSignin.Jsonsresult) {
                jsontext = resultData.getString("jsontext");
                int statuscode = resultData.getInt("statuscode");
                switch (statuscode) {
                    case 200:

                        intentgetdevice.putExtra("receiver", new DataReciverDevice(new Handler()));
                        getActivity().startService(intentgetdevice);

                        intentgetuser.putExtra("receiver", new DataReciverUser(new Handler()));
                        getActivity().startService(intentgetuser);

                        break;
                    case 404:
                        progressDialog.dismiss();
                        alertDialog = new AlertDialog.Builder(getActivity())
                                .setTitle("Result")
                                .setMessage("Wrong User Pass")

                                // Specifying a listener allows you to take an action before dismissing the dialog.
                                // The dialog is automatically dismissed when a dialog button is clicked.
                                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        // Continue with delete operation
                                    }
                                }).setNegativeButton("Forget Password", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        SignActivity.viewPager.setCurrentItem(2);
                                    }
                                })


                                .setIcon(android.R.drawable.ic_dialog_alert)
                                .show();

                        break;
                    case 400:
                        progressDialog.dismiss();
                        Toast.makeText(getActivity(), "Wrong", Toast.LENGTH_LONG).show();
                        break;
                }

            } else if (resultCode == GetDevice.Jsonsresultnull) {

            }

        }
    }

    public class DataReciverDevice extends ResultReceiver {

        public DataReciverDevice(Handler handler) {
            super(handler);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {
            super.onReceiveResult(resultCode, resultData);

            if (resultCode == GetDevice.Jsonsresult) {
                jsontext = resultData.getString("jsontext");
                statuscode = resultData.getInt("statuscode");

                if (statuscode == 401) {

                }
            } else if (resultCode == GetDevice.Jsonsresultnull) {

            }

        }
    }

    public class DataReciverUser extends ResultReceiver {

        public DataReciverUser(Handler handler) {
            super(handler);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {
            super.onReceiveResult(resultCode, resultData);

            if (resultCode == GetUser.Jsonsresult) {
                statuscodeuser = resultData.getInt("statuscodeuser");

                user_id = resultData.getString("user_id");
                user_first_name = resultData.getString("user_first_name");
                user_last_name = resultData.getString("user_last_name");
                user_avatar = resultData.getString("user_avatar");
                user_email = resultData.getString("user_email");
                user_mobile = resultData.getString("user_mobile");
                user_created_at = resultData.getString("user_created_at");
                user_updated_at = resultData.getString("user_updated_at");

                gotomap();

            }

        }
    }

    private void gotomap() {

        if (statuscode == 200 && statuscodeuser == 200 && !igonrerun) {
            igonrerun = true;
            Intent intent = new Intent(getActivity(), MainActivity.class);
            intent.putExtra("jsontext", jsontext);

            try {
                intent.putExtra("user_id", user_id);
                intent.putExtra("user_first_name", user_first_name);
                intent.putExtra("user_last_name", user_last_name);
                intent.putExtra("user_avatar", user_avatar);
                intent.putExtra("user_email", user_email);
                intent.putExtra("user_mobile", user_mobile);
                intent.putExtra("user_created_at", user_created_at);
                intent.putExtra("user_updated_at", user_updated_at);
            } catch (Exception e) {

            }
            startActivity(intent);
            getActivity().finish();

        } else if (statuscode == 401) {
            Intent intent = new Intent(getActivity(), SignActivity.class);
            startActivity(intent);
            getActivity().finish();
        }
    }
}