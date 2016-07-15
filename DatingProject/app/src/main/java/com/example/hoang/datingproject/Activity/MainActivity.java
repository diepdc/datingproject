package com.example.hoang.datingproject.Activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTabHost;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TabHost;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.example.hoang.datingproject.Fragment.BaseContainerFragment;
import com.example.hoang.datingproject.Fragment.FeedsFragment;
import com.example.hoang.datingproject.Utilities.NotificationListener;
import com.example.hoang.datingproject.R;
import com.example.hoang.datingproject.Utilities.Const;
import com.example.hoang.datingproject.Utilities.DatingContainer;
import com.example.hoang.datingproject.Utilities.FeedsContainer;
import com.example.hoang.datingproject.Utilities.FontManager;
import com.example.hoang.datingproject.Utilities.FriendsContainer;
import com.example.hoang.datingproject.Utilities.LastFragmentContainer;
import com.example.hoang.datingproject.Utilities.MessagesContainer;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import io.fabric.sdk.android.Fabric;


public class MainActivity extends AppCompatActivity{

    private FragmentTabHost mTabHost;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_main);
        initView();
    }

    public void initView() {
        startService(new Intent(this, NotificationListener.class));
        mTabHost = (FragmentTabHost) findViewById(R.id.tabHost);
        mTabHost.setup(this, getSupportFragmentManager(), R.id.tabContent);

        mTabHost.addTab(setIndicator(MainActivity.this, mTabHost.newTabSpec(Const.TAB_1_TAG), R.drawable.bg_tabs, "ホーム", R.string.feeds_icon), FeedsContainer.class, null);
        mTabHost.addTab(setIndicator(MainActivity.this, mTabHost.newTabSpec(Const.TAB_2_TAG), R.drawable.bg_tabs, "探す", R.string.friends_icon), FriendsContainer.class, null);
        mTabHost.addTab(setIndicator(MainActivity.this, mTabHost.newTabSpec(Const.TAB_3_TAG), R.drawable.bg_tabs, "ト一ク", R.string.messages_icon), MessagesContainer.class, null);
        mTabHost.addTab(setIndicator(MainActivity.this, mTabHost.newTabSpec(Const.TAB_4_TAG), R.drawable.bg_tabs, "通知", R.string.datings_icon), DatingContainer.class, null);
        mTabHost.addTab(setIndicator(MainActivity.this, mTabHost.newTabSpec(Const.TAB_5_TAG), R.drawable.bg_tabs, "プロフ", R.string.emotion_icon), LastFragmentContainer.class, null);

//        mTabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
//
//            @Override
//            public void onTabChanged(String tabId) {
//
//                String currentTabTag = mTabHost.getCurrentTabTag();
//
//                Log.e(Const.LOG_TAG, currentTabTag);
//
//                FragmentManager fragmentManager = MainActivity.this.getSupportFragmentManager();
//                FragmentTransaction transaction = fragmentManager.beginTransaction();
//
//                BaseContainerFragment fragment1 = (BaseContainerFragment) fragmentManager.findFragmentByTag(currentTabTag);
//
//                if (fragment1 != null) {
//                    fragment1.replaceFragment(new FeedsFragment(), true);
////            ((BaseContainerFragment)fragmentManager.findFragmentByTag(fragmentTag)).popFragment();
//                    Log.e(Const.LOG_TAG, "124");
//
//                }
//
//                transaction.addToBackStack(currentTabTag);
//                transaction.commit();
//            }
//        });

        AdView mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
    }

    @Override
    public void onBackPressed() {
        getCurrentTab();
    }

    private void getCurrentTab() {
        boolean isPopFragment = false;
        String currentTabTag = mTabHost.getCurrentTabTag();

        if (currentTabTag.equals(Const.TAB_1_TAG)) {
            isPopFragment = ((BaseContainerFragment)getSupportFragmentManager().findFragmentByTag(Const.TAB_1_TAG)).popFragment();
//
        } else if (currentTabTag.equals(Const.TAB_2_TAG)) {
            isPopFragment = ((BaseContainerFragment)getSupportFragmentManager().findFragmentByTag(Const.TAB_2_TAG)).popFragment();
        }
        else if (currentTabTag.equals(Const.TAB_3_TAG)) {
            isPopFragment = ((BaseContainerFragment)getSupportFragmentManager().findFragmentByTag(Const.TAB_3_TAG)).popFragment();
        }else if (currentTabTag.equals(Const.TAB_4_TAG)) {
            isPopFragment = ((BaseContainerFragment)getSupportFragmentManager().findFragmentByTag(Const.TAB_4_TAG)).popFragment();
        }else if (currentTabTag.equals(Const.TAB_5_TAG)) {
            isPopFragment = ((BaseContainerFragment)getSupportFragmentManager().findFragmentByTag(Const.TAB_5_TAG)).popFragment();
        }

        if (!isPopFragment) {
            finish();
        }
    }

    private TabHost.TabSpec setIndicator(Context ctx, TabHost.TabSpec spec,
                                         int resid, String string, int genresIcon) {
        View v = LayoutInflater.from(ctx).inflate(R.layout.tab_item, null);
        v.setBackgroundResource(resid);
        TextView tv = (TextView)v.findViewById(R.id.txt_tabtxt);
        TextView img = (TextView)v.findViewById(R.id.img_tabtxt);
        Typeface font = FontManager.getTypeface(getApplicationContext(), FontManager.FONTAWESOME);
        img.setText(genresIcon);
        img.setTypeface(font);

        tv.setText(string);
        return spec.setIndicator(v);
    }

}
