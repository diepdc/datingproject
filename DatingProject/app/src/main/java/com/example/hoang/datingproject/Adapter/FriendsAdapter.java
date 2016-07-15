package com.example.hoang.datingproject.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hoang.datingproject.Activity.MessagesActivity;
import com.example.hoang.datingproject.Activity.PersonalInfoActivity;
import com.example.hoang.datingproject.Activity.UserProfileActivity;
import com.example.hoang.datingproject.Model.PersonModel;

import java.util.ArrayList;
import java.util.List;
import com.example.hoang.datingproject.R;
import com.example.hoang.datingproject.Utilities.Const;
import com.example.hoang.datingproject.Utilities.OnLoadMoreListener;

/**
 * Created by hoang on 4/4/2016.
 */
public class FriendsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private PersonModel personModel;
    private ArrayList<PersonModel> arr = new ArrayList<PersonModel>();

    private OnLoadMoreListener mOnLoadMoreListener;

    private boolean isLoading;
    private int visibleThreshold = 5;
    private int lastVisibleItem, totalItemCount;

    public FriendsAdapter(Context mcontext, ArrayList<PersonModel> arr , RecyclerView recyclerView) {
        this.mContext = mcontext;
        this.arr = arr;

        if (recyclerView.getLayoutManager() instanceof GridLayoutManager) {

            final GridLayoutManager linearLayoutManager = (GridLayoutManager) recyclerView
                    .getLayoutManager();

            recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);

                    totalItemCount = linearLayoutManager.getItemCount();
                    lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();

                    if (!isLoading && totalItemCount <= (lastVisibleItem + visibleThreshold) && totalItemCount >=20) {
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
            View view = inflater.inflate(R.layout.friend_item, parent, false);
            return new FriendsHolder(view);
        } else if (viewType == Const.VIEW_TYPE_LOADING) {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            View view = inflater.inflate(R.layout.friend_load_more_item, parent, false);
            return new LoadingViewHolder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof FriendsHolder) {
            personModel = arr.get(position);
            FriendsHolder friendsHolder = (FriendsHolder) holder;
            if (personModel.getImage() == null) {
                Log.e(Const.LOG_TAG, "loi gi k biet");
            }else {
                byte[] decodedString = Base64.decode(personModel.getImage(), Base64.DEFAULT);

                Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                if (decodedByte == null) {
                    Log.e(Const.LOG_TAG, "null");
                }
                friendsHolder.avatarItem.setImageBitmap(decodedByte);
            }

        } else if (holder instanceof LoadingViewHolder) {
            LoadingViewHolder loadingViewHolder = (LoadingViewHolder) holder;
            loadingViewHolder.progressBar.setIndeterminate(true);
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

        public LoadingViewHolder(View itemView) {
            super(itemView);
            progressBar = (ProgressBar) itemView.findViewById(R.id.load_more);
        }
    }

    public class FriendsHolder extends RecyclerView.ViewHolder{
        private ImageView avatarItem;

        public FriendsHolder(View itemView) {
            super(itemView);
            avatarItem = (ImageView) itemView.findViewById(R.id.avatar_item);
            avatarItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (PersonalInfoActivity.getDefaults("id", mContext).equalsIgnoreCase("123")){
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
                    } else {
                        Toast.makeText(mContext, "is clicked", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(mContext, MessagesActivity.class);
                        intent.putExtra("from", Const.FROM_FRIEND_ADAPTER);
                        intent.putExtra("model", arr.get(getAdapterPosition()));
                        mContext.startActivity(intent);
                    }
                }
            });
        }
    }
}
