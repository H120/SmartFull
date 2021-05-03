package com.example.myapplication.Class;


import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;

import com.example.myapplication.Item.JsonItem;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

public class JsonReader {

    ProgressDialog progress;
    Context context;
    List id,name,title,duration;
    String response;
    private List<JsonItem> markerlist = new ArrayList<JsonItem>();

    public List getJson(Context context , String addressurl) {
        this.context=context;
        progress = new ProgressDialog(context);
        progress.setTitle("Loading");
        progress.setMessage("Please Wait ...");
        progress.setCancelable(true); // disable dismiss by tapping outside of the dialog
//        progress.show();



        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        client.get(addressurl, params, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int i, cz.msebera.android.httpclient.Header[] headers, byte[] bytes) {
                response = new String(bytes);

                id = new ArrayList();
                name = new ArrayList();
                title = new ArrayList();
                duration = new ArrayList();

                try {

                    JSONObject jObj = new JSONObject(response);
                    boolean success = jObj.getBoolean("success");
                    if (success) {
                        JSONArray jArray = jObj.getJSONArray("data");
                        for (int j = 0; j < jArray.length(); j++) {
                            JSONObject object = jArray.getJSONObject(j);
                            System.out.println(response);

                            id.add(object.getString("id"));
                            name.add(object.getString("userId"));
                            title.add(object.getString("title"));
                            duration.add(object.getString("completed"));

                            Log.i("TAG", "onSuccess1: ");
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(int i, cz.msebera.android.httpclient.Header[] headers, byte[] bytes, Throwable throwable) {

            }
        });

//        for (int i = 0; i < id.size(); i++) {
//            try {
//
//                JsonItem jsonItem = new JsonItem();
//                jsonItem.id = Integer.parseInt(id.get(i).toString());
//                jsonItem.title = title.get(i).toString();
//                jsonItem.duration = duration.get(i).toString();
//                markerlist.add(jsonItem);
//
//            } catch (Exception e) {
//            }
//        }

        return markerlist;
    }

}