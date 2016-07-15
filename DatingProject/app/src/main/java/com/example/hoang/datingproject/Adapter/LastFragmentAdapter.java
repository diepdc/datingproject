package com.example.hoang.datingproject.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hoang.datingproject.Model.FeedModel;
import com.example.hoang.datingproject.R;
import com.example.hoang.datingproject.Utilities.Const;
import com.example.hoang.datingproject.Utilities.FontManager;
import com.example.hoang.datingproject.Utilities.OnLoadMoreListener;

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
 * Created by hoang on 4/5/2016.
 */
public class LastFragmentAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private Context mContext;
    private FeedModel feedModel;
    private ArrayList<FeedModel> arr = new ArrayList<FeedModel>();

    private OnLoadMoreListener mOnLoadMoreListener;

    private boolean isLoading;
    private int visibleThreshold = 1;
    private int lastVisibleItem, totalItemCount;


    public LastFragmentAdapter(Context mcontext, ArrayList<FeedModel> arr, RecyclerView recyclerView) {
        this.mContext = mcontext;
        this.arr = arr;

        notifyDataSetChanged();
        final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                if (linearLayoutManager != null) {
                    totalItemCount = linearLayoutManager.getItemCount();
                    lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();
                }

                if (!isLoading && totalItemCount <= (lastVisibleItem + visibleThreshold) && totalItemCount >=5) {
                    if (mOnLoadMoreListener != null) {
                        mOnLoadMoreListener.onLoadMore();
                    }
                    isLoading = true;
                }
            }
        });
    }

    public void setOnLoadMoreListener(OnLoadMoreListener mOnLoadMoreListener) {
        this.mOnLoadMoreListener = mOnLoadMoreListener;
    }

    @Override
    public int getItemViewType(int position) {
        return arr.get(position) == null ? Const.VIEW_TYPE_LOADING : Const.VIEW_TYPE_ITEM;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == Const.VIEW_TYPE_ITEM) {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            View view = inflater.inflate(R.layout.last_item, parent, false);
            return new LastFragmentHolder(view);
        } else if (viewType == Const.VIEW_TYPE_LOADING) {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            View view = inflater.inflate(R.layout.load_more_item, parent, false);
            return new LoadingViewHolder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof LastFragmentHolder) {
            feedModel = arr.get(position);
            LastFragmentHolder lastFragmentHolder = (LastFragmentHolder) holder;
            Typeface font = FontManager.getTypeface(mContext, FontManager.FONTAWESOME);
            byte[] decodedString = Base64.decode(feedModel.getFeedIcon(), Base64.DEFAULT);
            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            lastFragmentHolder.feedIcon.setImageBitmap(decodedByte);
            lastFragmentHolder.feedTitle.setText(feedModel.getFeedTitle());
            lastFragmentHolder.feedDescription.setText(feedModel.getFeedDescription());
            lastFragmentHolder.btn1_icon.setText(R.string.btn1_icon);
            lastFragmentHolder.btn1_icon.setTypeface(font);
            lastFragmentHolder.btn3_icon.setText(R.string.delete_post_icon);
            lastFragmentHolder.btn3_icon.setTypeface(font);
        } else if (holder instanceof LoadingViewHolder) {
            LoadingViewHolder loadingViewHolder = (LoadingViewHolder) holder;
            loadingViewHolder.progressBar.setIndeterminate(true);
            loadingViewHolder.textLoadMore.setText("Loading...");
        }

    }

    @Override
    public int getItemCount() {
        return arr == null ? 0 : arr.size();
    }

    public void setLoaded() {
        isLoading = false;
    }

    public class LoadingViewHolder extends RecyclerView.ViewHolder {
        public ProgressBar progressBar;
        private TextView textLoadMore;

        public LoadingViewHolder(View itemView) {
            super(itemView);
            progressBar = (ProgressBar) itemView.findViewById(R.id.progressBar1);
            textLoadMore = (TextView) itemView.findViewById(R.id.text_load_more);
        }
    }

    public class LastFragmentHolder extends RecyclerView.ViewHolder{

        private ImageView feedIcon;
        private TextView feedTitle;
        private TextView feedDescription;
        private RelativeLayout btn3;
        private RelativeLayout btn1;
        private TextView btn1_icon, btn3_icon;

        public LastFragmentHolder(View itemView) {
            super(itemView);
            feedIcon = (ImageView) itemView.findViewById(R.id.feed_image);
            feedTitle = (TextView) itemView.findViewById(R.id.feed_title);
            feedDescription = (TextView) itemView.findViewById(R.id.feed_description);

            btn1 = (RelativeLayout) itemView.findViewById(R.id.feed_btn1);
            btn3 = (RelativeLayout) itemView.findViewById(R.id.feed_btn3);

            btn1_icon = (TextView) itemView.findViewById(R.id.btn1_icon);
            btn3_icon = (TextView) itemView.findViewById(R.id.btn3_icon);

            btn1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(mContext, "Button1 is clicked", Toast.LENGTH_SHORT).show();
                }
            });
            btn3.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                    builder.setTitle("Delete Item");
                    builder.setMessage("do you want to remove this item?");
                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            new DeleteItem().execute(Const.DELETE_TIMELINE_URL + arr.get(getAdapterPosition()).getId());
                        }
                    });

                    builder.setNegativeButton("Oh No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });

                    builder.show();
                }
            });
        }
    }

    private class DeleteItem extends AsyncTask<String, Void, String> {

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
                    Log.d(Const.LOG_TAG, "delete item successful!");
                    Toast.makeText(mContext, "delete item successful!", Toast.LENGTH_SHORT).show();
                } else {
                    Log.d(Const.LOG_TAG, "delete item failed!");
                    Toast.makeText(mContext, "delete item failed!", Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
