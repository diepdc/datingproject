package com.example.hoang.datingproject.Fragment;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TabHost;

import com.example.hoang.datingproject.Activity.PersonalInfoActivity;
import com.example.hoang.datingproject.Adapter.DatingAdapter;
import com.example.hoang.datingproject.Adapter.FootPrintAdapter;
import com.example.hoang.datingproject.Model.FeedModel;
import com.example.hoang.datingproject.Model.FootPrintModel;
import com.example.hoang.datingproject.Model.MessageModel;
import com.example.hoang.datingproject.Model.NotificationModel;
import com.example.hoang.datingproject.Model.PersonModel;
import com.example.hoang.datingproject.R;
import com.example.hoang.datingproject.Utilities.AsyncRespond;
import com.example.hoang.datingproject.Utilities.Const;
import com.example.hoang.datingproject.Utilities.OnLoadMoreListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by hoang on 4/4/2016.
 */
public class DatingFragment extends Fragment implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener{

    private Button btn1, btn2;
    private TabHost tabHost;
    private RecyclerView recyclerView1, recyclerView2;
    private DatingAdapter adapter;
    private FootPrintAdapter adapter1;
    private int LIMIT = 5;
    private int LIMIT1 = 5;
    private int OFFSET = 1;
    private ArrayList<NotificationModel> arr = new ArrayList<NotificationModel>();
    private ArrayList<FootPrintModel> arr1, temp_arr;

    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        arr1 = new ArrayList<FootPrintModel>();
        arr1.add(null);
        temp_arr = new ArrayList<FootPrintModel>();
        methodThatStartsTheAsyncTask();
//        new GetData((AsynData) getActivity()).execute(Const.LIST_TIMELINE_URL + Const.LIMIT + "=" + LIMIT + "&" + Const.OFFSET + "=" + OFFSET);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dating_fragment, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getControls();
        loadTabs();
    }

    public void getControls() {

        swipeRefreshLayout = (SwipeRefreshLayout) getActivity().findViewById(R.id.swipe2refresh);

        swipeRefreshLayout.setOnRefreshListener(this);

        btn1 = (Button) getActivity().findViewById(R.id.dating_btn1);
        btn2 = (Button) getActivity().findViewById(R.id.dating_btn2);

        btn1.setOnClickListener(this);
        btn2.setOnClickListener(this);

        recyclerView1 = (RecyclerView) getActivity().findViewById(R.id.listFriend);
        recyclerView2 = (RecyclerView) getActivity().findViewById(R.id.listFriend2);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
//        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(),4);
        recyclerView2.setLayoutManager(linearLayoutManager);
        arr.add(null);
        adapter = new DatingAdapter(getActivity(), arr, recyclerView2);
        adapter.notifyItemInserted(arr.size() - 1);
        recyclerView2.setAdapter(adapter);

        recyclerView2.setHasFixedSize(true);

        LinearLayoutManager linearLayoutManager1 = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
//        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(),4);
        recyclerView1.setLayoutManager(linearLayoutManager1);

        adapter1 = new FootPrintAdapter(getActivity(), arr1, recyclerView1);
        adapter1.notifyItemInserted(arr.size() - 1);
        recyclerView1.setAdapter(adapter1);

        recyclerView1.setHasFixedSize(true);

        adapter.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                arr.add(null);
                adapter.notifyItemInserted(arr.size() - 1);

                //Load more data for reyclerview
                LIMIT1 += 5;
                new GetData().execute(Const.LIST_NOTIFICATION_URL + Const.LIMIT + "=" + LIMIT1 + "&" + Const.OFFSET + "=" + OFFSET);
            }
        });

        adapter1.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                arr1.add(null);
                adapter1.notifyItemInserted(arr.size() - 1);

                //Load more data for reyclerview
                LIMIT += 5;
                methodThatStartsTheAsyncTask();
            }
        });
        methodThatStartsTheAsyncTask();
        new GetData().execute(Const.LIST_NOTIFICATION_URL + Const.LIMIT + "=" + LIMIT1 + "&" + Const.OFFSET + "=" + OFFSET);
    }

    public class GetFootPrints extends AsyncTask<String, Void, String> {
        private AsyncRespond listener;

        public GetFootPrints(AsyncRespond listener){
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
                    ArrayList<FootPrintModel> arrayList = new ArrayList<FootPrintModel>();
                    JSONArray data = jsonObject.getJSONArray("data");

                    for (int i = 0; i < data.length(); i++) {
                        JSONObject item = data.getJSONObject(i);
                        String nickname = item.getString(Const.NICK_NAME);
                        String address = item.getString(Const.ADDRESS);
                        String posttime = item.getString(Const.POSTTIME);
                        String profileImage = item.getString(Const.PROFILE_IMAGE);
                        String userID = item.getString(Const.USERID);

                        Log.d(Const.LOG_TAG, nickname + " - " + address + " - " + posttime);

                        FootPrintModel model = new FootPrintModel(nickname, userID, address, profileImage, posttime);

                        arrayList.add(model);

                    }
                    temp_arr.clear();
                    temp_arr.addAll(arrayList);

                    listener.processDatingFinish(temp_arr);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void loadTabs(){
        tabHost = (TabHost) getActivity().findViewById(android.R.id.tabhost);
        tabHost.setup();
        TabHost.TabSpec spec;
        spec = tabHost.newTabSpec("listfriend1");
        spec.setContent(R.id.tab1);
        spec.setIndicator("FootPrints");
        tabHost.addTab(spec);

        spec = tabHost.newTabSpec("listfriend2");
        spec.setContent(R.id.tab2);
        spec.setIndicator("Notifications");
        tabHost.addTab(spec);

        tabHost.setCurrentTab(0);
        btn1.setPressed(true);
        btn1.setSelected(true);

//        tabHost.getTabWidget().setBackgroundResource(R.drawable.bg_toolbar);
        tabHost.getTabWidget().setDividerDrawable(null);

//        for(int i=0;i<tabHost.getTabWidget().getChildCount();i++) {
//            tabHost.getTabWidget().getChildAt(i).setBackgroundResource(R.drawable.bg_tab2);//unselected
////            tabHost.getTabWidget().getChildAt(i).setPadding(20, 20, 10 ,0);
//        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.dating_btn1:
                tabHost.setCurrentTab(0);
                btn1.setSelected(true);
                btn1.setPressed(true);
                btn2.setSelected(false);
                btn2.setPressed(false);
                break;
            case R.id.dating_btn2:
                tabHost.setCurrentTab(1);
                btn1.setSelected(false);
                btn1.setPressed(false);
                btn2.setSelected(true);
                btn2.setPressed(true);
                break;
        }
    }

    private void methodThatStartsTheAsyncTask() {
        GetFootPrints testAsyncTask = new GetFootPrints(new AsyncRespond() {


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
                methodThatDoesSomethingWhenTaskIsDone(arr);
            }

            @Override
            public void processLastFinish(ArrayList<FeedModel> arr) {

            }

            @Override
            public void processProfileFinish(String name, String address, String description, String profileImage) {

            }
        });

        testAsyncTask.execute(Const.FOOT_PRINT_URL + Const.LIMIT + "=" + LIMIT + "&" + Const.OFFSET + "=" + OFFSET);
    }

    private void methodThatDoesSomethingWhenTaskIsDone(ArrayList<FootPrintModel> temp_arr) {
        /* Magic! */
        arr1.clear();
        arr1.addAll(temp_arr);

        if (adapter1 != null) {
            adapter1.notifyDataSetChanged();
            swipeRefreshLayout.setRefreshing(false);
            adapter1.setLoaded();
            Log.d(Const.LOG_TAG, "refresh3");
        }
    }

    @Override
    public void onRefresh() {
        LIMIT = 5;
        LIMIT1 = 5;
        new GetData().execute(Const.LIST_NOTIFICATION_URL + Const.LIMIT + "=" + LIMIT1 + "&" + Const.OFFSET + "=" + OFFSET);
        methodThatStartsTheAsyncTask();
    }

    public class GetData extends AsyncTask<String, Void, String> {
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
//            progressDialog = new ProgressDialog(getActivity(),
//                    R.style.AppTheme_Dark_Dialog);
//            progressDialog.setIndeterminate(true);
//            progressDialog.setMessage("Loading...");
//            progressDialog.show();
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

//            Log.d(Const.LOG_TAG, encodedImage);

            try{

                JSONObject jsonObject = new JSONObject(doubles);
                Log.d(Const.LOG_TAG, jsonObject.toString());
                String result = jsonObject.getString("result");
                if (result.equalsIgnoreCase("1")) {
                    ArrayList<NotificationModel> arrayList = new ArrayList<NotificationModel>();
                    JSONArray data = jsonObject.getJSONArray("data");

                    for (int i = 0; i < data.length(); i++) {
                        JSONObject item = data.getJSONObject(i);
                        String title = item.getString(Const.NOTIFICATION_TITLE);
                        String content = item.getString(Const.NOTIFICATION_CONTENT);
                        String posttime = item.getString(Const.NOTIFICATION_POSTTIME);

                        Log.d(Const.LOG_TAG, content + " - " + title + " - " + posttime);

                        NotificationModel model = new NotificationModel(title, content, posttime);

                        arrayList.add(model);

                    }
                    arr.clear();
                    arr.addAll(arrayList);
                    adapter.notifyDataSetChanged();
//                    progressDialog.dismiss();
                    swipeRefreshLayout.setRefreshing(false);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
