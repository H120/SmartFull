package com.example.myapplication.Fragment;

import android.os.Bundle;
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

public class SignUpFragment extends Fragment {
    public SignUpFragment() {
        // Required empty public constructor
    }

    String response, firstname, lastname, email, password, password_confirmation,mobile;
    EditText signup_firstname,signup_lastnam,signup_password,signup_repassword,signup_email,signup_mobile;
    Button signup_button;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View myView = inflater.inflate(R.layout.fragment_signup, container, false);

        Button signup_button = myView.findViewById(R.id.signup_button);
        EditText signup_firstname = myView.findViewById(R.id.signup_firstname);
        EditText signup_lastname = myView.findViewById(R.id.signup_lastname);
        EditText signup_email = myView.findViewById(R.id.signup_email);
        EditText signup_password = myView.findViewById(R.id.signup_password);
        EditText signup_repassword = myView.findViewById(R.id.signup_repassword);
        EditText signup_mobile = myView.findViewById(R.id.signup_mobile);

        signup_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firstname = signup_firstname.getText().toString();
                lastname = signup_lastname.getText().toString();
                email = signup_email.getText().toString();
                password = signup_password.getText().toString();
                password_confirmation = signup_repassword.getText().toString();
                mobile = signup_mobile.getText().toString();

                sign_up();
            }
        });

        return myView;
    }

    private void sign_up()
    {

        OkHttpClient client = new OkHttpClient();

        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(mediaType, "{\r\n    \"first_name\": \""+firstname+"\",\r\n    \"last_name\": \""+lastname+"\",\r\n    " +
                "\"email\": \""+email+"\",\r\n    \"password\": \""+password+"\",\r\n    \"password_confirmation\": \""+password_confirmation+"\",\r\n    " +
                "\"mobile\": \""+mobile+"\"\r\n}");
        Request request = new Request.Builder()
                .url("http://smartflow.sensiran.com:8585/api/client/authentication/register")
                .post(body)
                .addHeader("accept", "application/json")
                .addHeader("content-type", "application/json")
                .addHeader("cache-control", "no-cache")
                .addHeader("postman-token", "71a983db-0a1f-be2f-5695-effc0be6a56d")
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
            Log.i("TAG", "fetchlogin 2: "+json);

            try {
                JSONObject jsonObj = new JSONObject(json);

//                JSONArray contacts = jsonObj.getJSONArray("status");
                int statuscode = jsonObj.getInt("status");

                if (statuscode==200) {

                    JSONObject data = jsonObj.getJSONObject("result");
                    String jsontoken = data.optString("token");
                    Log.i("TAG", "fetchlogin: "+jsontoken);



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
    }
}