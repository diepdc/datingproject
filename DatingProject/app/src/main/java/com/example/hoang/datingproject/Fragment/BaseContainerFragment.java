package com.example.hoang.datingproject.Fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.hoang.datingproject.R;
import com.example.hoang.datingproject.Utilities.Const;

/**
 * Created by hoang on 4/3/2016.
 */
public class BaseContainerFragment extends Fragment {

    public void replaceFragment(Fragment fragment, boolean addToBackStack){
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        String fragmentTag = fragment.getClass().getName();
        Fragment fragment1 = fragmentManager.findFragmentByTag(fragmentTag);

        if (fragment1 != null) {
            transaction.replace(R.id.container_framelayout, fragment1, fragmentTag);
            Log.e(Const.LOG_TAG, "123");

        } else {
            transaction.replace(R.id.container_framelayout, fragment, fragmentTag);
        }

        transaction.addToBackStack(fragmentTag);
        transaction.commit();
        getChildFragmentManager().executePendingTransactions();

    }

    public boolean popFragment() {
        boolean isPop = false;
        if (getChildFragmentManager().getBackStackEntryCount() > 0) {
            isPop = true;
            getChildFragmentManager().popBackStack();
        }
        return isPop;
    }
}
