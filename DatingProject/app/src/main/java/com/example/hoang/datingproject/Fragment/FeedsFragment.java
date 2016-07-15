package com.example.hoang.datingproject.Fragment;

import android.app.Activity;
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
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hoang.datingproject.Activity.NewNoteActivity;
import com.example.hoang.datingproject.Activity.PersonalInfoActivity;
import com.example.hoang.datingproject.Activity.WrittingNoteActivity;
import com.example.hoang.datingproject.Adapter.DatingAdapter;
import com.example.hoang.datingproject.Adapter.FeedsAdapter;
import com.example.hoang.datingproject.Model.FeedModel;
import com.example.hoang.datingproject.Model.FootPrintModel;
import com.example.hoang.datingproject.Model.MessageModel;
import com.example.hoang.datingproject.Model.PersonModel;
import com.example.hoang.datingproject.R;
import com.example.hoang.datingproject.Utilities.AsyncRespond;
import com.example.hoang.datingproject.Utilities.Const;
import com.example.hoang.datingproject.Utilities.FontManager;
import com.example.hoang.datingproject.Utilities.OnLoadMoreListener;
import com.example.hoang.datingproject.Utilities.RegisterUserClass;

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
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by hoang on 4/4/2016.
 */
public class FeedsFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener{

    private RecyclerView recyclerView;
    private FeedsAdapter adapter;
    private ArrayList<FeedModel> arr, temp_arr;
    private TextView new_note;
    private SwipeRefreshLayout swipeRefreshLayout;
    private int LIMIT = 5;
    private int OFFSET = 1;
    private View rootView = null;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        arr = new ArrayList<FeedModel>();
        arr.add(null);
        temp_arr = new ArrayList<FeedModel>();

        methodThatStartsTheAsyncTask();
//        new GetData((AsynData) getActivity()).execute(Const.LIST_TIMELINE_URL + Const.LIMIT + "=" + LIMIT + "&" + Const.OFFSET + "=" + OFFSET);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.feed_fragment, container, false);
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getControls();
    }

    private void getControls() {
        swipeRefreshLayout = (SwipeRefreshLayout) getActivity().findViewById(R.id.swipe2refresh);

        swipeRefreshLayout.setOnRefreshListener(this);
        new_note = (TextView) getActivity().findViewById(R.id.edit_icon);
        recyclerView = (RecyclerView) getActivity().findViewById(R.id.recyclerView);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
//        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(),4);
        recyclerView.setLayoutManager(linearLayoutManager);

        adapter = new FeedsAdapter(getActivity(), arr, recyclerView);
        adapter.notifyItemInserted(arr.size() - 1);
        recyclerView.setAdapter(adapter);
        recyclerView.setHasFixedSize(true);

        adapter.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                arr.add(null);
                adapter.notifyItemInserted(arr.size() - 1);
                LIMIT += 5;
                methodThatStartsTheAsyncTask();
//                new GetData((AsyncRespond) getActivity()).execute(Const.LIST_TIMELINE_URL + Const.LIMIT + "=" + LIMIT + "&" + Const.OFFSET + "=" + OFFSET);
//                new AddData().execute(Const.LIST_TIMELINE_URL);
//                adapter.setLoaded();
            }
        });
        Typeface font = FontManager.getTypeface(getActivity(), FontManager.FONTAWESOME);
        TextView edit = (TextView) getActivity().findViewById(R.id.edit_icon);
        edit.setTypeface(font);

        new_note.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), NewNoteActivity.class);
                getActivity().startActivity(intent);
//                showDialog();
            }
        });
    }

    private void methodThatStartsTheAsyncTask() {
        GetData testAsyncTask = new GetData(new AsyncRespond() {


            @Override
            public void processFinish(ArrayList<FeedModel> arr) {
                methodThatDoesSomethingWhenTaskIsDone(arr);
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

            }
        });

        testAsyncTask.execute(Const.LIST_TIMELINE_URL + Const.LIMIT + "=" + LIMIT + "&" + Const.OFFSET + "=" + OFFSET);
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
        LIMIT = 5;
        methodThatStartsTheAsyncTask();
    }

    public void showDialog(){
        WritePostDialog myDialog = new WritePostDialog();
        myDialog.show(getFragmentManager(), "My Dialog");
    }

    private Bitmap convertStringtoBitmap(String image) {
        byte[] decodedString = Base64.decode(image, Base64.DEFAULT);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

        return decodedByte;
    }

    public class GetData extends AsyncTask<String, Void, String> {

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

//            Log.d(Const.LOG_TAG, encodedImage);

            try{

                JSONObject jsonObject = new JSONObject(doubles);
                Log.d(Const.LOG_TAG, jsonObject.toString());
                String result = jsonObject.getString("result");
                if (result.equalsIgnoreCase("1")) {
                    ArrayList<FeedModel> arrayList = new ArrayList<FeedModel>();
                    JSONArray data = jsonObject.getJSONArray("data");

                    for (int i = 0,n=data.length(); i < n; i++) {
                        JSONObject item = data.getJSONObject(i);
                        String content = item.getString(Const.POST_CONTENT);
                        String profileImage = item.getString(Const.PROFILE_IMAGE);
                        String nickname = item.getString(Const.NICK_NAME);
                        String itemID = item.getString(Const.ITEMID);
                        String image = item.getString(Const.IMAGE);
                        String birthday = item.getString(Const.BIRTHDAY);

                        JSONObject replyTo = item.getJSONObject(Const.REPLY_TO);

                        String userID = replyTo.getString("userID");

                        Log.d(Const.LOG_TAG, content + " - " + profileImage + " - " + nickname);

                        FeedModel model = new FeedModel(profileImage, nickname, content, convertStringtoBitmap(image),itemID, userID, birthday);

                        arrayList.add(model);

                    }
                    temp_arr.clear();
                    temp_arr.addAll(arrayList);

                    listener.processFinish(temp_arr);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
