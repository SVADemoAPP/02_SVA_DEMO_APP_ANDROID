package com.hck.imagemap;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

import com.hck.imagemap.Constant.MapConstant;
import com.hck.imagemap.config.GlobalConfig;
import com.hck.imagemap.view.CheckSwitchButton;

/**
 * 通用设置类
 * 
 */

public class GeneralSettingActivity extends Activity
{

    private CheckSwitchButton mCheckSwithcButton, animation, cs_biaochi,
            floorSwitch, vipShow, wifiSwitch;
    private Boolean autoLogin, bAnimation, biaochi, bFloorSwitch, bVipShow,
            bWifiSwitch;
    private SharedPreferences myPreferences;
    private CheckSwitchButton follow;
    private boolean bFollow;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_general_setting);
        setViews();
        setListeners();
    }

    /**
     * 初始化
     */
    private void setViews()
    {
        myPreferences = getSharedPreferences("setting", Activity.MODE_PRIVATE);
        mCheckSwithcButton = (CheckSwitchButton) findViewById(R.id.mCheckSwithcButton);
        autoLogin = myPreferences.getBoolean("autoPush", true);
        mCheckSwithcButton.setChecked(autoLogin);

        animation = (CheckSwitchButton) findViewById(R.id.manimation);
        bAnimation = myPreferences.getBoolean("bAnimation", true);
        animation.setChecked(bAnimation);

        cs_biaochi = (CheckSwitchButton) findViewById(R.id.cs_biaochi);
        biaochi = myPreferences.getBoolean("biaochi", true);
        cs_biaochi.setChecked(biaochi);

        floorSwitch = (CheckSwitchButton) findViewById(R.id.floorSwitch);
        bFloorSwitch = myPreferences.getBoolean("autoSwitch", true);
        floorSwitch.setChecked(bFloorSwitch);

        wifiSwitch = (CheckSwitchButton) findViewById(R.id.wifiSwitch);
        MapConstant.wifiSwitch = myPreferences.getBoolean("wifiSwitch", false);
        wifiSwitch.setChecked(MapConstant.wifiSwitch);

        vipShow = (CheckSwitchButton) findViewById(R.id.vipshow);
        bVipShow = myPreferences.getBoolean("vipShow", true);
        vipShow.setChecked(bVipShow);

        follow = (CheckSwitchButton) findViewById(R.id.follow);
        bFollow = myPreferences.getBoolean("follow", false);
        follow.setChecked(bFollow);
    }

    /**
     * 监听事件
     */
    private void setListeners()
    {
        mCheckSwithcButton
                .setOnCheckedChangeListener(new OnCheckedChangeListener()
                {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView,
                            boolean isChecked)
                    {
                        GlobalConfig.setAutoPush(isChecked);
                        SharedPreferences preferences = getSharedPreferences(
                                "setting", Activity.MODE_PRIVATE);
                        // 获得SharedPreferences.Editor
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putBoolean("autoPush", isChecked);
                        editor.commit();
                    }
                });

        animation.setOnCheckedChangeListener(new OnCheckedChangeListener()
        {

            @Override
            public void onCheckedChanged(CompoundButton arg0, boolean arg1)
            {
                GlobalConfig.setAnimation(arg1);
                SharedPreferences preferences = getSharedPreferences("setting",
                        Activity.MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putBoolean("bAnimation", arg1);
                editor.commit();
            }
        });

        cs_biaochi.setOnCheckedChangeListener(new OnCheckedChangeListener()
        {

            @Override
            public void onCheckedChanged(CompoundButton arg0, boolean arg1)
            {
                GlobalConfig.setAnimation(arg1);
                SharedPreferences preferences = getSharedPreferences("setting",
                        Activity.MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putBoolean("biaochi", arg1);
                editor.commit();
            }
        });

        floorSwitch.setOnCheckedChangeListener(new OnCheckedChangeListener()
        {

            @Override
            public void onCheckedChanged(CompoundButton arg0, boolean arg1)
            {
                SharedPreferences preferences = getSharedPreferences("setting",
                        Activity.MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putBoolean("autoSwitch", arg1);
                editor.commit();
            }
        });

        wifiSwitch.setOnCheckedChangeListener(new OnCheckedChangeListener()
        {

            @Override
            public void onCheckedChanged(CompoundButton arg0, boolean arg1)
            {
                SharedPreferences preferences = getSharedPreferences("setting",
                        Activity.MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putBoolean("wifiSwitch", arg1);
                editor.commit();
            }
        });

        vipShow.setOnCheckedChangeListener(new OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton arg0, boolean arg1)
            {
                SharedPreferences preferences = getSharedPreferences("setting",
                        Activity.MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putBoolean("vipShow", arg1);
                editor.commit();
            }
        });

        follow.setOnCheckedChangeListener(new OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton arg0, boolean arg1)
            {
                SharedPreferences preferences = getSharedPreferences("setting",
                        Activity.MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putBoolean("follow", arg1);
                editor.commit();
            }
        });
    }

    public void setting_back(View v)
    {
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.general_setting, menu);
        return true;
    }

}
