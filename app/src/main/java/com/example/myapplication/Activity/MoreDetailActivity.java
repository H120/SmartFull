package com.example.myapplication.Activity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import com.example.myapplication.R;
import com.timqi.sectorprogressview.ColorfulRingProgressView;

public class MoreDetailActivity extends AppCompatActivity {

    ColorfulRingProgressView colorfulRingProgressView;
    TextView tv_moredetail_percent;
    String current_id, current_name, current_log, current_identity,current_created_at, current_updated_at;
    TextView tv_moredetail_created_at,tv_moredetail_current_identity,tv_moredetail_current_lat,tv_moredetail_current_lon,tv_moredetail_current_name,
            tv_moredetail_current_updated_at;
    double current_lat,current_lon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_moredetail);

        current_created_at = getIntent().getStringExtra("current_created_at");
        current_id = getIntent().getStringExtra("current_id");
        current_identity = getIntent().getStringExtra("current_identity");
        current_lat = getIntent().getDoubleExtra("current_lat",0);
        current_lon = getIntent().getDoubleExtra("current_lon",0);
        current_log = getIntent().getStringExtra("current_log");
        current_name = getIntent().getStringExtra("current_name");
        current_updated_at = getIntent().getStringExtra("current_updated_at");

        tv_moredetail_percent=(TextView) findViewById(R.id.tv_moredetail_percent);
        colorfulRingProgressView=(ColorfulRingProgressView)findViewById(R.id.colorfulRingProgressView);

        tv_moredetail_created_at=(TextView) findViewById(R.id.tv_moredetail_created_at) ;
        tv_moredetail_current_identity=(TextView) findViewById(R.id.tv_moredetail_current_identity) ;
        tv_moredetail_current_lat=(TextView) findViewById(R.id.tv_moredetail_current_lat) ;
        tv_moredetail_current_lon=(TextView) findViewById(R.id.tv_moredetail_current_lon) ;
        tv_moredetail_current_name=(TextView) findViewById(R.id.tv_moredetail_current_name) ;
        tv_moredetail_current_updated_at=(TextView) findViewById(R.id.tv_moredetail_current_updated_at) ;

        tv_moredetail_created_at.setText(current_created_at);
        tv_moredetail_current_identity.setText(current_id);
        tv_moredetail_current_lat.setText(current_identity);
        tv_moredetail_current_lon.setText(current_lat+"");
        tv_moredetail_current_name.setText(current_lon+"");
        tv_moredetail_current_updated_at.setText(current_log);
        tv_moredetail_current_updated_at.setText(current_name);
        tv_moredetail_current_updated_at.setText(current_updated_at);

        colorfulRingProgressView.setPercent(75);
        tv_moredetail_percent.setText("75%\nof Water");
    }
}