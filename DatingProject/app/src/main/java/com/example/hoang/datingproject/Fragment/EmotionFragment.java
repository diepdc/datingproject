package com.example.hoang.datingproject.Fragment;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.hoang.datingproject.Adapter.EmotionAdapter;
import com.example.hoang.datingproject.Model.ImageItem;
import com.example.hoang.datingproject.R;

import java.util.ArrayList;

/**
 * Created by hoang on 4/11/2016.
 */
public class EmotionFragment extends Fragment{

    private RecyclerView recyclerView;
    private EmotionAdapter adapter;
    private ListSelectionListener mListener = null;
    private ArrayList<ImageItem> imageItems;

    public interface ListSelectionListener {
        public void onListSelection(int index, ArrayList<ImageItem> arr);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.emotion_fragment, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getControls();
    }

    private void getControls() {
        recyclerView = (RecyclerView) getActivity().findViewById(R.id.recyclerView);
        adapter = new EmotionAdapter(getActivity(), getData());
        adapter.attachInteface(mListener);
//        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(),4, LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setAdapter(adapter);
        recyclerView.setHasFixedSize(true);
    }

    private ArrayList<ImageItem> getData() {
        imageItems = new ArrayList<>();
        int []arr = {R.drawable.fortune_1,R.drawable.fortune_1,R.drawable.fortune_1,R.drawable.fortune_1,
                R.drawable.fortune_1,R.drawable.fortune_1,R.drawable.fortune_1,R.drawable.fortune_1,
                R.drawable.fortune_2,R.drawable.fortune_2,R.drawable.fortune_2,R.drawable.fortune_2,
                R.drawable.fortune_3,R.drawable.fortune_3,R.drawable.fortune_3,R.drawable.fortune_3,
                R.drawable.fortune_4,R.drawable.fortune_4,R.drawable.fortune_4,R.drawable.fortune_4,
                R.drawable.fortune_5,R.drawable.fortune_5,R.drawable.fortune_5,R.drawable.fortune_5,
                R.drawable.fortune_5,R.drawable.fortune_5,R.drawable.fortune_5,R.drawable.fortune_5,
                R.drawable.fortune_6,R.drawable.fortune_3,R.drawable.fortune_5,R.drawable.fortune_3,
                R.drawable.fortune_6,R.drawable.fortune_3,R.drawable.fortune_5,R.drawable.fortune_3,
                R.drawable.fortune_6,R.drawable.fortune_3,R.drawable.fortune_5,R.drawable.fortune_3,
                R.drawable.fortune_6,R.drawable.fortune_3,R.drawable.fortune_5,R.drawable.fortune_3,
                R.drawable.fortune_5,R.drawable.fortune_5,R.drawable.fortune_5,R.drawable.fortune_5,
                R.drawable.fortune_5,R.drawable.fortune_5,R.drawable.fortune_5,R.drawable.fortune_5,
                R.drawable.fortune_5,R.drawable.fortune_5,R.drawable.fortune_5,R.drawable.fortune_5,
                R.drawable.fortune_5,R.drawable.fortune_5,R.drawable.fortune_5,R.drawable.fortune_5,
                R.drawable.fortune_6,R.drawable.fortune_3,R.drawable.fortune_5,R.drawable.fortune_3,
                R.drawable.fortune_6,R.drawable.fortune_3,R.drawable.fortune_5,R.drawable.fortune_3,
                R.drawable.fortune_6,R.drawable.fortune_3,R.drawable.fortune_5,R.drawable.fortune_3,
                R.drawable.fortune_6,R.drawable.fortune_3,R.drawable.fortune_5,R.drawable.fortune_3,
                R.drawable.fortune_6,R.drawable.fortune_3,R.drawable.fortune_5,R.drawable.fortune_3,
                R.drawable.fortune_3,R.drawable.fortune_2,R.drawable.fortune_3,R.drawable.avatar};

        for (int i = 0; i < arr.length; i++) {
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), arr[i]);
            imageItems.add(new ImageItem(bitmap, "Image#" + i));
        }
        return imageItems;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {

            // Set the ListSelectionListener for communicating with the QuoteViewerActivity
            mListener = (ListSelectionListener) activity;

        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnArticleSelectedListener");
        }
    }
}
