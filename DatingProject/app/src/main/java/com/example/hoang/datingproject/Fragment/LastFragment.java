package com.example.hoang.datingproject.Fragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.hoang.datingproject.Activity.PersonalInfoActivity;
import com.example.hoang.datingproject.Activity.SettingsActivity;
import com.example.hoang.datingproject.Adapter.FeedsAdapter;
import com.example.hoang.datingproject.Adapter.LastFragmentAdapter;
import com.example.hoang.datingproject.Model.FeedModel;
import com.example.hoang.datingproject.Model.FootPrintModel;
import com.example.hoang.datingproject.Model.MessageModel;
import com.example.hoang.datingproject.Model.PersonModel;
import com.example.hoang.datingproject.R;
import com.example.hoang.datingproject.Utilities.AsyncRespond;
import com.example.hoang.datingproject.Utilities.Const;
import com.example.hoang.datingproject.Utilities.FontManager;
import com.example.hoang.datingproject.Utilities.OnLoadMoreListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by hoang on 4/5/2016.
 */
public class LastFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener{

    private RecyclerView recyclerView;
    private LastFragmentAdapter adapter;
    private ArrayList<FeedModel> arr, temp_arr;
    private SwipeRefreshLayout swipeRefreshLayout;

    private TextView profile_name, profile_address, profile_description;
    private ImageView profile_image;

    private String address1;
    private String profileImage1;
    private String nickname;
    private String description1;

    private static int LIMIT = 5;
    private static int OFFSET = 1;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        arr = new ArrayList<FeedModel>();
        arr.add(null);
        temp_arr = new ArrayList<FeedModel>();

        nickname = "フン";
        address1 = "Oversea, 27";
        description1 = "はじめまして、ベトナムです。";

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Bitmap icon = BitmapFactory.decodeResource(this.getResources(),
                R.drawable.avatar);
        icon.compress(Bitmap.CompressFormat.JPEG, 100, baos);

        byte[] b = baos.toByteArray();
        profileImage1 = Base64.encodeToString(b, Base64.DEFAULT);

        methodThatSynProfileInfor();
        methodThatStartsTheAsyncTask();
//        new GetData((AsynData) getActivity()).execute(Const.LIST_TIMELINE_URL + Const.LIMIT + "=" + LIMIT + "&" + Const.OFFSET + "=" + OFFSET);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.last_fragment, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        getControls();
    }

    private void getControls() {

        swipeRefreshLayout = (SwipeRefreshLayout) getActivity().findViewById(R.id.swipe2refresh);

        swipeRefreshLayout.setOnRefreshListener(this);

        profile_name = (TextView) getActivity().findViewById(R.id.profile_name);
        profile_address = (TextView) getActivity().findViewById(R.id.profile_address);
        profile_description = (TextView) getActivity().findViewById(R.id.profile_description);
        profile_image = (ImageView) getActivity().findViewById(R.id.profile_icon);

        profile_name.setText(nickname);
        profile_description.setText(description1);
        profile_address.setText(address1);

        byte[] decodedString = Base64.decode(profileImage1, Base64.DEFAULT);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

        profile_image.setImageBitmap(decodedByte);

        recyclerView = (RecyclerView) getActivity().findViewById(R.id.recyclerView);

        adapter = new LastFragmentAdapter(getActivity(), arr, recyclerView);
        adapter.notifyItemInserted(arr.size() - 1);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
//        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(),4);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(adapter);
        recyclerView.setHasFixedSize(true);

        adapter.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                arr.add(null);
                adapter.notifyItemInserted(arr.size() - 1);
                Log.d(Const.LOG_TAG, "vao day ha");
                LIMIT += 5;
                methodThatStartsTheAsyncTask();
            }
        });

        Typeface font = FontManager.getTypeface(getActivity(), FontManager.FONTAWESOME);
        TextView settings = (TextView) getActivity().findViewById(R.id.setting_icon);
        settings.setTypeface(font);
        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), SettingsActivity.class);
                getActivity().startActivity(intent);
            }
        });
    }

    private void methodThatSynProfileInfor() {
        GetProfileData getProfileData = new GetProfileData(new AsyncRespond() {
            @Override
            public void processFinish(ArrayList<FeedModel> arr) {

            }

            @Override
            public void processFriendFinish(ArrayList<PersonModel> arr) {

            }

            @Override
            public void processMessageFinish(ArrayList<MessageModel> arr) {

            }

            @Override
            public void processDatingFinish(ArrayList<FootPrintModel> arr) {

            }

            @Override
            public void processLastFinish(ArrayList<FeedModel> arr) {

            }

            @Override
            public void processProfileFinish(String name, String address, String description, String profileImage) {

                nickname = name;
                address1 = address;
                description1 = description;
                profileImage1 = profileImage;

                profile_name.setText(nickname);
                profile_description.setText(description1);
                profile_address.setText(address1);

                byte[] decodedString = Base64.decode(profileImage1, Base64.DEFAULT);
                Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

                profile_image.setImageBitmap(decodedByte);

                Log.d(Const.LOG_TAG, address + " - " + description + " - " + nickname);
                if (swipeRefreshLayout != null) {
                    swipeRefreshLayout.setRefreshing(false);
                }
            }
        });
        getProfileData.execute(Const.USER_PROFILE_URL + PersonalInfoActivity.getDefaults("id", getActivity()));
    }

    private void methodThatStartsTheAsyncTask() {

        GetData testAsyncTask = new GetData(new AsyncRespond() {

            @Override
            public void processFinish(ArrayList<FeedModel> arr) {

            }

            @Override
            public void processFriendFinish(ArrayList<PersonModel> arr) {

            }

            @Override
            public void processMessageFinish(ArrayList<MessageModel> arr) {

            }

            @Override
            public void processDatingFinish(ArrayList<FootPrintModel> arr) {

            }

            @Override
            public void processLastFinish(ArrayList<FeedModel> arr) {
                methodThatDoesSomethingWhenTaskIsDone(arr);
            }

            @Override
            public void processProfileFinish(String name, String address, String description, String profileImage) {

            }
        });

        testAsyncTask.execute(Const.PROFILE_TIMELINE_URL + PersonalInfoActivity.getDefaults("id", getActivity()) + "&" +  Const.LIMIT + "=" + LIMIT + "&" + Const.OFFSET + "=" + OFFSET);
    }

    private void methodThatDoesSomethingWhenTaskIsDone(ArrayList<FeedModel> temp_arr) {
        /* Magic! */
        arr.clear();
        arr.addAll(temp_arr);

        if (adapter != null) {
            adapter.notifyDataSetChanged();
            swipeRefreshLayout.setRefreshing(false);
            adapter.setLoaded();
            Log.d(Const.LOG_TAG, "refresh");
        }
    }

    @Override
    public void onRefresh() {
        methodThatSynProfileInfor();
        methodThatStartsTheAsyncTask();
        LIMIT = 5;
    }

    private class GetData extends AsyncTask<String, Void, String> {

        private AsyncRespond listener;

        public GetData(AsyncRespond listener){
            this.listener=listener;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
        @Override
        protected String doInBackground(String... params) {
            ArrayList<Double> resultList = null;
            HttpURLConnection connection = null;
            StringBuilder jsonResults = new StringBuilder();
            String result = "";

            try {
                StringBuilder sb = new StringBuilder(params[0]);

                URL url = new URL(sb.toString());
                Log.d(Const.LOG_TAG, url.toString());
                connection = (HttpURLConnection) url.openConnection();
                InputStreamReader in = new InputStreamReader(connection.getInputStream());

                int read;
                char[] buff = new char[1024];
                while ((read = in.read(buff)) != -1) {
                    jsonResults.append(buff, 0, read);
                }
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
            }
            result = jsonResults.toString();
            return result;
        }

        @Override
        protected void onPostExecute(String doubles) {

            try{

                JSONObject jsonObject = new JSONObject(doubles);
                Log.d(Const.LOG_TAG, jsonObject.toString());
                String result = jsonObject.getString("result");
                if (result.equalsIgnoreCase("1")) {
                    JSONArray data = jsonObject.getJSONArray("data");
                    ArrayList<FeedModel> arrayList = new ArrayList<FeedModel>();
                    for (int i = 0; i < data.length(); i++) {
                        JSONObject item = data.getJSONObject(i);
                        String content = item.getString(Const.POST_CONTENT);
                        String profileImage = item.getString(Const.PROFILE_IMAGE);
                        String nickname = item.getString(Const.NICK_NAME);
                        String itemID = item.getString(Const.ITEMID);

                        Log.d(Const.LOG_TAG, content + " - " + profileImage + " - " + nickname);

                        FeedModel model = new FeedModel(profileImage, nickname, content, itemID);

                        arrayList.add(model);
                    }
                    temp_arr.clear();
                    temp_arr.addAll(arrayList);

                    listener.processLastFinish(temp_arr);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private class GetProfileData extends AsyncTask<String, Void, String> {

        private AsyncRespond listener;

        public GetProfileData(AsyncRespond listener){
            this.listener=listener;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
        @Override
        protected String doInBackground(String... params) {
            ArrayList<Double> resultList = null;
            HttpURLConnection connection = null;
            StringBuilder jsonResults = new StringBuilder();
            String result = "";

            try {
                StringBuilder sb = new StringBuilder(params[0]);

                URL url = new URL(sb.toString());
                Log.d(Const.LOG_TAG, url.toString());
                connection = (HttpURLConnection) url.openConnection();
                InputStreamReader in = new InputStreamReader(connection.getInputStream());

                int read;
                char[] buff = new char[1024];
                while ((read = in.read(buff)) != -1) {
                    jsonResults.append(buff, 0, read);
                }
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
            }
            result = jsonResults.toString();
            return result;
        }

        @Override
        protected void onPostExecute(String doubles) {
            try{

                JSONObject jsonObject = new JSONObject(doubles);
                Log.d(Const.LOG_TAG, jsonObject.toString());
                String result = jsonObject.getString("result");
                if (result.equalsIgnoreCase("1")) {
                    JSONObject data = jsonObject.getJSONObject("data");
                    String address = data.getString(Const.ADDRESS);
                    String profileImage = data.getString(Const.PROFILE_IMAGE);
                    String nickname = data.getString(Const.NICK_NAME);
                    String description = data.getString(Const.DESCRIPTION);

                    listener.processProfileFinish(nickname, address, description, profileImage);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
