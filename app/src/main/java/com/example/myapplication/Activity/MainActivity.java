package com.example.myapplication.Activity;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.AlertDialog;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.budiyev.android.codescanner.CodeScanner;
import com.example.myapplication.Class.DeleteDevice;
import com.example.myapplication.Class.GetDevice;
import com.example.myapplication.Class.GetUser;
import com.example.myapplication.Item.JsonItem;
import com.example.myapplication.Class.LocationTrack;
import com.example.myapplication.Fragment.QRScannerFragment;
import com.example.myapplication.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.navigation.NavigationView;
import com.squareup.picasso.Picasso;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback ,GoogleMap.OnMarkerClickListener{

    private GoogleMap mMap;
    LocationTrack locationTrack;

    private Button getlocation,qrreader;
    private CodeScanner mCodeScanner;
    String jsontext;
    ArrayList<JsonItem> markerList;
    SharedPreferences prefs;
    private Marker markeritem;
    private DrawerLayout mDrawer;
    private Toolbar toolbar;
    private NavigationView nvDrawer;
    private ActionBarDrawerToggle drawerToggle;
    RelativeLayout info_relativelayout;
    TextView tv_detail_detail, tv_date_detail, tv_name_detail, tv_degree_detail,
            tv_speed_detail, tv_online_detail, tv_door_detail;
    int open_info_speed,currentmarkerselected,statuscode;
    RelativeLayout rl_info_mainactivitybottom;
    boolean info_layout_is_open = false;
    ImageView iv_close_info_mainactivitybottom,iv_more_info_mainactivitybottom;
    String current_id, current_name, current_log, current_identity,current_created_at, current_updated_at;
    double current_lat,current_lon;
    private long pressedTime;
    String user_id ,user_first_name ,user_last_name ,user_avatar ,user_email,user_mobile,user_created_at,user_updated_at;
    TextView textview_user_id ,textview_user_first_name ,textview_user_last_name ,
            textview_user_email,textview_user_mobile;

    ImageView imageview_user_avatar;
    LinearLayout ll_delete_device;
    Intent intentdeletedevice;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        jsontext = getIntent().getStringExtra("jsontext");
        user_id = getIntent().getStringExtra("user_id");
        user_first_name = getIntent().getStringExtra("user_first_name");
        user_last_name = getIntent().getStringExtra("user_last_name");
        user_avatar = getIntent().getStringExtra("user_avatar");
        user_email = getIntent().getStringExtra("user_email");
        user_mobile = getIntent().getStringExtra("user_mobile");
        user_created_at = getIntent().getStringExtra("user_created_at");
        user_updated_at = getIntent().getStringExtra("user_updated_at");

        nvDrawer = (NavigationView) findViewById(R.id.nvView);
        // Setup drawer view
        setupDrawerContent(nvDrawer);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Find our drawer view
        mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerToggle = setupDrawerToggle();

        // Setup toggle to display hamburger icon with nice animation
        drawerToggle.setDrawerIndicatorEnabled(true);
        drawerToggle.syncState();

        // Tie DrawerLayout events to the ActionBarToggle
        mDrawer.addDrawerListener(drawerToggle);

        // Set a Toolbar to replace the ActionBar.
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // This will display an Up icon (<-), we will replace it with hamburger later
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        // Find our drawer view
        mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        getlocation =(Button)findViewById(R.id.getlocation);
        qrreader =(Button)findViewById(R.id.qrreader);
        info_relativelayout = (RelativeLayout) findViewById(R.id.inforelativelayout);
        tv_date_detail = (TextView) findViewById(R.id.tv_date_detail);
        tv_name_detail = (TextView) findViewById(R.id.tv_name_detail);
        tv_detail_detail = (TextView) findViewById(R.id.tv_detail_detail);
        rl_info_mainactivitybottom = (RelativeLayout) findViewById(R.id.rl_info_mainactivitybottom);
        iv_close_info_mainactivitybottom = (ImageView) findViewById(R.id.iv_close_info_mainactivitybottom);
        iv_more_info_mainactivitybottom = (ImageView) findViewById(R.id.iv_more_info_mainactivitybottom);
        ll_delete_device = (LinearLayout) findViewById(R.id.ll_delete_device);

        setuserdata();

        iv_close_info_mainactivitybottom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                if (info_layout_is_open)
                {
                    Closeinfobottomwithanimation(true, true);
                    info_layout_is_open = false;
                    rl_info_mainactivitybottom.setClickable(false);
                }
            }
        });

        iv_more_info_mainactivitybottom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {

            }
        });

        qrreader.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                FragmentManager fragmentManager = getFragmentManager();
                FrameLayout frameLayout= (FrameLayout)findViewById(R.id.qrscannerfragment_holder);
                frameLayout.setVisibility(View.VISIBLE);
                fragmentManager.beginTransaction().add(R.id.qrscannerfragment_holder, new QRScannerFragment(),null).commit();

            }
        });

        getlocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                locationTrack = new LocationTrack(MainActivity.this);

                if (locationTrack.canGetLocation()) {

                    double longitude = locationTrack.getLongitude();
                    double latitude = locationTrack.getLatitude();

                    Log.i("Location", "onClick: "+"Longitude:" + Double.toString(longitude) + "\nLatitude:" + Double.toString(latitude));
                    if(longitude!=0 && latitude!=0) {
                        Toast.makeText(getApplicationContext(), "Longitude:" + Double.toString(longitude) + "\nLatitude:" + Double.toString(latitude), Toast.LENGTH_SHORT).show();
                    }
                } else {

                    locationTrack.showSettingsAlert();
                }
            }
        });

        ll_delete_device.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder1 = new AlertDialog.Builder(MainActivity.this);
                builder1.setMessage("You sure ?\n \" "+ current_name+" \" ");
                builder1.setCancelable(true);
                builder1.setPositiveButton("Yes",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                //put your code that needed to be executed when okay is clicked
                                delete_device();

                                dialog.cancel();

                            }
                        });
                builder1.setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

                AlertDialog alert11 = builder1.create();
                alert11.show();

            }
        });
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnMarkerClickListener(this);
        mMap.getUiSettings().setMapToolbarEnabled(false);
        mMap.getUiSettings().setZoomControlsEnabled(true);

        fetchdevices(jsontext);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            locationTrack.stopListener();

        }catch (Exception e){}
    }

    private void setmarkers(){

        ConnectivityManager connectivityManager = (ConnectivityManager) this.getSystemService(this.CONNECTIVITY_SERVICE);
        if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {

            if (markerList.size() > 0) {
                for (int i = 0; i < markerList.size(); i++) {
                    LatLng marker = new LatLng(markerList.get(i).lon, markerList.get(i).lat);
                    markeritem = mMap.addMarker(new MarkerOptions().position(marker).title(null));
                    markeritem.setTag(i);
                }
                LatLng lastmarker = new LatLng(markerList.get(markerList.size() - 1).lon, markerList.get(markerList.size() - 1).lat);
                mMap.animateCamera(CameraUpdateFactory.newLatLng(lastmarker));
                addMenuItemInNavMenuDrawer();

            } else {
                Toast.makeText(this, "Please Connect to The Internet", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void fetchdevices(String json){

        if (markerList!=null){
            markerList.clear();
        }
        if (json != null) {
            try {
                markerList=new ArrayList<>();

                JSONObject jsonObj = new JSONObject(json);

                int status = jsonObj.getInt("status");

                JSONArray contacts = jsonObj.getJSONArray("result");

                // looping through All Contacts
                for (int i = 0; i < contacts.length(); i++) {
                    JSONObject c = contacts.getJSONObject(i);
                    String id = c.getString("id");
                    String name = c.getString("name");
                    String identity = c.getString("identity");
                    String geometry = c.getString("geometry");
                    String created_at = c.getString("created_at");
                    String updated_at = c.getString("updated_at");
                    String log = c.getString("log");

                    // tmp hash map for single contact
                    JsonItem marker = new JsonItem();

                    // adding each child node to HashMap key => value
                    marker.id=id;
                    marker.name=name;
                    marker.identity=identity;
                    marker.lat=Double.parseDouble(geometry.substring(1,geometry.indexOf(",")));
                    marker.lon=Double.parseDouble(geometry.substring(geometry.indexOf(",")+1,geometry.length()-2));
                    marker.created_at=created_at;
                    marker.updated_at=updated_at;
                    marker.log=log;


                    Log.i("TAG", "fetchdevices lat: "+geometry.substring(1,geometry.indexOf(",")));
                    Log.i("TAG", "fetchdevices id: "+id+"|"+identity);
                    try {
                        Log.i("TAG", "fetchdevices lon: "+geometry.substring(geometry.indexOf(",")+1,geometry.length()-2)+"");

                    }catch (Exception e){}

                    // adding contact to contact list
                    markerList.add(marker);
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
            Log.i("TAG", "markerlist size: "+markerList.size());
            setmarkers();

        } else {
            Log.e("TAG", "Couldn't get json from server.");
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getApplicationContext(),
                            "Couldn't get json from server. Check LogCat for possible errors!",
                            Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        selectDrawerItem(menuItem);
                        return true;
                    }
                });
    }

    public void selectDrawerItem(MenuItem menuItem) {

        switch(menuItem.getItemId()) {

            case R.id.add_device:
                adddevice();
                break;
            case R.id.sign_out:
                signout();
                break;

        }

        for (int i=0;i<markerList.size();i++) {
            if (menuItem.getItemId() == i) {
                Log.i("TAG", "selectDrawerItem: "+markerList.get(menuItem.getItemId()).name);

                current_id = markerList.get(menuItem.getItemId()).id;
                current_name = markerList.get(menuItem.getItemId()).name;
                current_log=markerList.get(menuItem.getItemId()).log;
                current_lat=markerList.get(menuItem.getItemId()).lat;
                current_lon=markerList.get(menuItem.getItemId()).lon;
                current_created_at=markerList.get(menuItem.getItemId()).created_at;
                current_updated_at=markerList.get(menuItem.getItemId()).updated_at;
                if (info_layout_is_open)
                {
                    Closeinfobottomwithanimation(false, false);
                } else
                {
                    Openinfobottomwithanimation();
                }
                LatLng lastmarker = new LatLng(markerList.get(menuItem.getItemId()).lon,markerList.get(menuItem.getItemId()).lat);
                mMap.animateCamera(CameraUpdateFactory.newLatLng(lastmarker));
            }
        }
        // Highlight the selected item has been done by NavigationView
        menuItem.setChecked(false);
        // Set action bar title
        setTitle(menuItem.getTitle());
        // Close the navigation drawer
        mDrawer.closeDrawers();
    }

    private void signout (){

        String sharedprefencetoken = "";
        prefs = this.getSharedPreferences(
                "", Context.MODE_PRIVATE);
        prefs.edit().putString(sharedprefencetoken, null).apply();

        Intent intent=new Intent(MainActivity.this,FirstActivity.class);
        startActivity(intent);
        finish();

    }

    private ActionBarDrawerToggle setupDrawerToggle() {
        // NOTE: Make sure you pass in a valid toolbar reference.  ActionBarDrawToggle() does not require it
        // and will not render the hamburger icon without it.
        return new ActionBarDrawerToggle(this, mDrawer, toolbar, R.string.drawer_open,  R.string.drawer_close);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggles
        drawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    void Openinfobottomwithanimation()
    {

        try
        {
            tv_date_detail.setText(current_id);
            tv_name_detail.setText(current_name);
            tv_detail_detail.setText(current_updated_at);
        } catch (Exception e)
        {
        }
        info_relativelayout.setVisibility(View.VISIBLE);

        TranslateAnimation translateAnimation = new TranslateAnimation(0, 0, info_relativelayout.getHeight(), 0);
        translateAnimation.setDuration(open_info_speed);
        translateAnimation.setFillAfter(true);
        info_relativelayout.startAnimation(translateAnimation);
        translateAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation)
            {

            }

            @Override
            public void onAnimationEnd(Animation animation)
            {

                rl_info_mainactivitybottom.setClickable(true);
                info_layout_is_open = true;
            }

            @Override
            public void onAnimationRepeat(Animation animation)
            {

            }
        });

    }

    void Closeinfobottomwithanimation(final boolean closemanualy, final boolean isfirst)
    {

        TranslateAnimation translateAnimation = new TranslateAnimation(0, 0, 0, info_relativelayout.getHeight());
        translateAnimation.setDuration(open_info_speed);
        translateAnimation.setFillAfter(true);
        info_relativelayout.startAnimation(translateAnimation);

        translateAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation)
            {

            }

            @Override
            public void onAnimationEnd(Animation animation)

            {
                info_layout_is_open = false;
                if (!isfirst && !closemanualy)
                {
                    Openinfobottomwithanimation();
                } else
                {
                    info_relativelayout.setVisibility(View.GONE);
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation)
            {

            }
        });

    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        // Retrieve the data from the marker.
        Integer currentmarker = (Integer) marker.getTag();
        if (currentmarker != null) {
            current_id = markerList.get(currentmarker).id;
            current_name = markerList.get(currentmarker).name;
            current_log=markerList.get(currentmarker).log;
            current_lat=markerList.get(currentmarker).lat;
            current_lon=markerList.get(currentmarker).lon;
            current_created_at=markerList.get(currentmarker).created_at;
            current_updated_at=markerList.get(currentmarker).updated_at;
            currentmarkerselected =currentmarker;

            if (info_layout_is_open)
            {
                Closeinfobottomwithanimation(false, false);
            } else
            {
                Openinfobottomwithanimation();
            }
            Log.i("TAG", "onMarkerClick: "+currentmarker);
        }
        return false;
    }

    private void adddevice(){
        Intent intent=new Intent(MainActivity.this,AddDeviceActivity.class);
        startActivityForResult(intent,2);

    }

    // Call Back method  to get the Message from other Activity
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        try {


            if (resultCode==2) {
                jsontext = data.getStringExtra("jsontext");
//            String locationresult_lng = data.getStringExtra("locationresult_lng");
                fetchdevices(jsontext);

            }
            else if(resultCode==1){
            }
        }catch (Exception e){}

    }

    @Override
    public void onBackPressed() {
        if (pressedTime + 2000 > System.currentTimeMillis()) {
            super.onBackPressed();
            finish();
        } else {
            Toast.makeText(getBaseContext(), "Press back again to exit", Toast.LENGTH_SHORT).show();
        }
        pressedTime = System.currentTimeMillis();
    }
    private void addMenuItemInNavMenuDrawer() {

        Menu menu = nvDrawer.getMenu();
        Menu submenu = menu.addSubMenu("Devices");
        for (int i = 0; i < markerList.size(); i++) {
            submenu.add(1,i,i,markerList.get(i).name);
        }
        nvDrawer.invalidate();
    }

    private void setuserdata(){
        View headerView = nvDrawer.getHeaderView(0);

        textview_user_id = (TextView) headerView.findViewById(R.id.textview_user_id);
        textview_user_first_name = (TextView) headerView.findViewById(R.id.textview_user_first_name);
        textview_user_last_name = (TextView) headerView.findViewById(R.id.textview_user_last_name);
        textview_user_email = (TextView) headerView.findViewById(R.id.textview_user_email);
        textview_user_mobile = (TextView) headerView.findViewById(R.id.textview_user_mobile);
        imageview_user_avatar = (ImageView) headerView.findViewById(R.id.imageview_user_avatar);

        if (user_id !=null)
            textview_user_id.setText(user_id);

        if (user_first_name !=null)
            textview_user_first_name.setText(user_first_name);

        if (user_last_name !=null)
            textview_user_last_name.setText(user_last_name);

        if (user_email !=null)
            textview_user_email.setText(user_email);

        if (user_mobile !=null)
            textview_user_mobile.setText(user_mobile);

        if (user_avatar !=null) {
            Picasso.get().load(user_avatar).placeholder(R.drawable.avatar_placeholder_icon).into(imageview_user_avatar);
        }

    }

    private void delete_device(){

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please Wait");
        progressDialog.setCancelable(false);
        progressDialog.show();

        intentdeletedevice = new Intent(this, DeleteDevice.class);
        intentdeletedevice.putExtra("receiver", new DataReciverDelete(new Handler()));
        intentdeletedevice.putExtra("deletedeviceid", current_id);
        startService(intentdeletedevice);

    }

    public class DataReciverDelete extends ResultReceiver {

        public DataReciverDelete(Handler handler) {
            super(handler);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {
            super.onReceiveResult(resultCode, resultData);
            if (resultCode == DeleteDevice.Jsonsresult) {
                statuscode=resultData.getInt("statuscode");
                Log.i("TAG", "onReceiveResult: "+statuscode);
                if (statuscode==200){
                    Closeinfobottomwithanimation(true, true);
                    markerList.remove(currentmarkerselected);
                    mMap.clear();
                    setmarkers();
                    progressDialog.dismiss();

                }
            }
        }
    }
}