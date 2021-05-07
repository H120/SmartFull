package com.example.myapplication.Activity;

import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.example.myapplication.Fragment.SignFragmentAdapter;
import com.example.myapplication.R;
import com.google.android.material.tabs.TabLayout;

import java.util.List;

import me.itangqi.waveloadingview.WaveLoadingView;

public class SignActivity extends AppCompatActivity {

    Button signin_button, signin_button2;
    EditText signin_username, signin_password;
    List id, name, title, duration;
    WaveLoadingView waveView;
    TabLayout tabLayout;
    public static ViewPager viewPager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign);

        waveView = (WaveLoadingView) findViewById(R.id.wave_view);
        tabLayout = findViewById(R.id.tabLayout);
        viewPager = findViewById(R.id.viewPager);
        tabLayout.addTab(tabLayout.newTab().setText("Sign In"));
        tabLayout.addTab(tabLayout.newTab().setText("Sign Up"));
        tabLayout.addTab(tabLayout.newTab().setText("Forget Password"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        final SignFragmentAdapter adapter = new SignFragmentAdapter(this, getSupportFragmentManager(),
                tabLayout.getTabCount());
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());

                if (tab.getPosition()==0){
                    WaveLoadingView layout = findViewById(R.id.wave_view);
                    ViewGroup.LayoutParams params = layout.getLayoutParams();
                    params.height =(int) (250 * (getResources().getDisplayMetrics().density));
                    layout.setLayoutParams(params);
                }else if(tab.getPosition()==1){
                    WaveLoadingView layout = findViewById(R.id.wave_view);
                    ViewGroup.LayoutParams params = layout.getLayoutParams();
                    params.height =(int) (150 * (getResources().getDisplayMetrics().density));
                    layout.setLayoutParams(params);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });

    }

}