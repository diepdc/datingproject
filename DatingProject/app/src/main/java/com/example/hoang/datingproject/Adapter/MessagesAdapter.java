package com.example.hoang.datingproject.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.support.v7.widget.GridLayoutManager;
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
import android.widget.TextView;
import android.widget.Toast;

import com.example.hoang.datingproject.Activity.MessagesActivity;
import com.example.hoang.datingproject.Activity.PersonalInfoActivity;
import com.example.hoang.datingproject.Model.FeedModel;
import com.example.hoang.datingproject.Model.MessageModel;
import com.example.hoang.datingproject.Model.PersonModel;
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
public class MessagesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    private Context mContext;
    private MessageModel messageModel;
    private ArrayList<MessageModel> arr = new ArrayList<MessageModel>();

    private OnLoadMoreListener mOnLoadMoreListener;

    private boolean isLoading;
    private int visibleThreshold = 5;
    private int lastVisibleItem, totalItemCount;

    public MessagesAdapter(Context mcontext, ArrayList<MessageModel> arr, RecyclerView recyclerView) {
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
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == Const.VIEW_TYPE_ITEM) {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            View view = inflater.inflate(R.layout.message_item, parent, false);
            return new MessageHolder(view);

        } else if (viewType == Const.VIEW_TYPE_LOADING) {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            View view = inflater.inflate(R.layout.load_more_item, parent, false);
            return new LoadingViewHolder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof MessageHolder) {
            messageModel = arr.get(position);
            MessageHolder messageHolder = (MessageHolder) holder;
            byte[] decodedString = Base64.decode(messageModel.getMessage_icon(), Base64.DEFAULT);

            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            messageHolder.messageIcon.setImageBitmap(decodedByte);
            messageHolder.nickname.setText(messageModel.getNickname());
            messageHolder.messageContent.setText(messageModel.getMessage_content());
            messageHolder.messagePosttime.setText(messageModel.getMessage_pottime());

        } else if (holder instanceof LoadingViewHolder) {
            LoadingViewHolder loadingViewHolder = (LoadingViewHolder) holder;
            loadingViewHolder.progressBar.setIndeterminate(true);
            loadingViewHolder.textLoadMore.setText("Loading...");
        }
    }

    @Override
    public int getItemViewType(int position) {
        return arr.get(position) == null ? Const.VIEW_TYPE_LOADING : Const.VIEW_TYPE_ITEM;
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

    private void createDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle("Update Account");
        builder.setMessage("You need to update your personal account to perform this action?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(mContext, PersonalInfoActivity.class);
                mContext.startActivity(intent);
            }
        });

        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builder.show();
    }

    public class MessageHolder extends RecyclerView.ViewHolder{

        private ImageView messageIcon;
        private TextView nickname;
        private TextView messageContent;
        private TextView messagePosttime;
        private LinearLayout message_container;

        public MessageHolder(View itemView) {

            super(itemView);

            messageIcon = (ImageView) itemView.findViewById(R.id.message_icon);
            nickname = (TextView) itemView.findViewById(R.id.message_title);
            messageContent = (TextView) itemView.findViewById(R.id.message_content);
            messagePosttime = (TextView) itemView.findViewById(R.id.message_posttime);
            message_container = (LinearLayout) itemView.findViewById(R.id.message_container);

            message_container.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (PersonalInfoActivity.getDefaults("id", mContext).equalsIgnoreCase("123")){
                        createDialog();
                    } else {
                        Toast.makeText(mContext, "is clicked", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(mContext, MessagesActivity.class);
                        intent.putExtra("from", Const.FROM_MESSAGE_ADAPTER);
                        intent.putExtra("model", arr.get(getAdapterPosition()));
                        mContext.startActivity(intent);
                    }
                }
            });
        }
    }
}
