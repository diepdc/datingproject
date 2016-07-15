package com.example.hoang.datingproject.Utilities;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.hoang.datingproject.Fragment.BaseContainerFragment;
import com.example.hoang.datingproject.Fragment.LastFragment;
import com.example.hoang.datingproject.R;

/**
 * Created by hoang on 4/5/2016.
 */
public class LastFragmentContainer extends BaseContainerFragment {
    boolean isInitialView = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.container_framelayout, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (!isInitialView){
            initView();
        }
    }

    public void initView(){
        replaceFragment(new LastFragment(), false);
    }
}
