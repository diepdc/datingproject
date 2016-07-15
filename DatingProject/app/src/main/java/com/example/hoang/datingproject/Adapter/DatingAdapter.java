package com.example.hoang.datingproject.Adapter;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.hoang.datingproject.Model.MessageModel;
import com.example.hoang.datingproject.Model.NotificationModel;
import com.example.hoang.datingproject.R;
import com.example.hoang.datingproject.Utilities.Const;
import com.example.hoang.datingproject.Utilities.OnLoadMoreListener;

import java.util.ArrayList;

/**
 * Created by hoang on 4/21/2016.
 */
public class DatingAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context mContext;
    private NotificationModel notificationModel;
    private ArrayList<NotificationModel> arr = new ArrayList<NotificationModel>();

    private OnLoadMoreListener mOnLoadMoreListener;

    private boolean isLoading;
    private int visibleThreshold = 3;
    private int lastVisibleItem, totalItemCount;

    public DatingAdapter(Context mcontext, ArrayList<NotificationModel> arr, RecyclerView recyclerView) {
        this.mContext = mcontext;
        this.arr = arr;

        if (recyclerView.getLayoutManager() instanceof LinearLayoutManager) {

            final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView
                    .getLayoutManager();

            recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);

                    totalItemCount = linearLayoutManager.getItemCount();
                    lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();

                    if (!isLoading && totalItemCount <= (lastVisibleItem + visibleThreshold) && totalItemCount >=5) {
                        if (mOnLoadMoreListener != null) {
                            Log.d(Const.LOG_TAG, totalItemCount + "size" + "-" + lastVisibleItem + "position" + visibleThreshold);
                            mOnLoadMoreListener.onLoadMore();
                        }
                        isLoading = true;
                    }
                }
            });
        }
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
            View view = inflater.inflate(R.layout.dating_item, parent, false);
            return new DatingHolder(view);
        } else if (viewType == Const.VIEW_TYPE_LOADING) {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            View view = inflater.inflate(R.layout.load_more_item, parent, false);
            return new LoadingViewHolder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof DatingHolder) {
            notificationModel = arr.get(position);
            DatingHolder datingHolder = (DatingHolder) holder;
            datingHolder.messageTitle.setText(notificationModel.getTitle());
            datingHolder.messageDescription.setText(notificationModel.getContent());
            datingHolder.posttime.setText(notificationModel.getPosttime());

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


    public class DatingHolder extends RecyclerView.ViewHolder{

        private TextView messageTitle;
        private TextView messageDescription;
        private TextView posttime;

        public DatingHolder(View itemView) {

            super(itemView);
            messageTitle = (TextView) itemView.findViewById(R.id.message_title);
            messageDescription = (TextView) itemView.findViewById(R.id.message_content);
            posttime = (TextView) itemView.findViewById(R.id.posttime);
        }
    }
}
