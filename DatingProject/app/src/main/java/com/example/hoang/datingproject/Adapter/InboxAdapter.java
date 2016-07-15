package com.example.hoang.datingproject.Adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hoang.datingproject.Activity.ImageViewActivity;
import com.example.hoang.datingproject.Activity.PersonalInfoActivity;
import com.example.hoang.datingproject.Activity.UserProfileActivity;
import com.example.hoang.datingproject.Model.InboxModel;
import com.example.hoang.datingproject.Model.PersonModel;
import com.example.hoang.datingproject.R;
import com.example.hoang.datingproject.Utilities.Const;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

/**
 * Created by hoang on 4/14/2016.
 */
public class InboxAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context mContext;
    private InboxModel inboxModel;
    private ArrayList<InboxModel> arr = new ArrayList<InboxModel>();

    public InboxAdapter(Context mcontext, ArrayList<InboxModel> arr) {
        this.mContext = mcontext;
        this.arr = arr;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == Const.SENDER_MESSAGE) {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            View view = inflater.inflate(R.layout.inbox_item, parent, false);
            return new InboxHolder(view);
        }else if (viewType == Const.SENDER_IMAGE) {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            View view = inflater.inflate(R.layout.inbox_item_image, parent, false);
            return new InboxImageHolder(view);
        }else if (viewType == Const.RECEIVER_MEASSAGE) {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            View view = inflater.inflate(R.layout.friend_inbox_item, parent, false);
            return new InboxHolder(view);
        }else if (viewType == Const.RECEIVER_IMAGE) {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            View view = inflater.inflate(R.layout.frien_inbox_item_image, parent, false);
            return new InboxImageHolder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof InboxHolder) {
            inboxModel = arr.get(position);
            InboxHolder inboxHolder = (InboxHolder) holder;

            inboxHolder.inbox_time.setText(inboxModel.getTime());

            byte[] decodedString = Base64.decode(inboxModel.getProfileImage(), Base64.DEFAULT);

            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            if (decodedByte == null) {
                Log.e(Const.LOG_TAG, "null");
            }
            inboxHolder.avatar.setImageBitmap(decodedByte);
            inboxHolder.setMessage(inboxModel.getContent());
        } else if (holder instanceof InboxImageHolder) {
            inboxModel = arr.get(position);
            InboxImageHolder inboxImageHolder = (InboxImageHolder) holder;

            inboxImageHolder.inbox_time.setText(inboxModel.getTime());

            byte[] decodedString = Base64.decode(inboxModel.getProfileImage(), Base64.DEFAULT);

            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            if (decodedByte == null) {
                Log.e(Const.LOG_TAG, "null");
            }
            inboxImageHolder.avatar.setImageBitmap(decodedByte);
            inboxImageHolder.setImage(inboxModel.getImage());
        }
    }

    @Override
    public int getItemViewType(int position) {
//        String sendUser = PersonalInfoActivity.getDefaults("uid", mContext);
        int type = -1;
        InboxModel model = arr.get(position);
        if (model.getSender().equalsIgnoreCase(PersonalInfoActivity.getDefaults("id", mContext)) && model.getContent() != null) {
            type = Const.SENDER_MESSAGE;
        } else if (model.getSender().equalsIgnoreCase(PersonalInfoActivity.getDefaults("id", mContext)) && model.getImage() != null) {
            type = Const.SENDER_IMAGE;
        } else if (!model.getSender().equalsIgnoreCase(PersonalInfoActivity.getDefaults("id", mContext)) && model.getContent() != null) {
            type = Const.RECEIVER_MEASSAGE;
        } else if (!model.getSender().equalsIgnoreCase(PersonalInfoActivity.getDefaults("id", mContext)) && model.getImage() != null) {
            type = Const.RECEIVER_IMAGE;
        }
        return type;
//        return arr.get(position).getSender().equalsIgnoreCase(PersonalInfoActivity.getDefaults("id", mContext)) ? Const.SENDER : Const.RECEIVER;
    }

    @Override
    public int getItemCount() {
        return arr.size();
    }

    public class InboxHolder extends RecyclerView.ViewHolder{
        private TextView inbox_time;
        private TextView inbox_content;
        private ImageView avatar;

        public InboxHolder(View itemView) {
            super(itemView);
            inbox_time = (TextView) itemView.findViewById(R.id.inbox_time);
            inbox_content = (TextView) itemView.findViewById(R.id.inbox_content);
            avatar = (ImageView) itemView.findViewById(R.id.avatar_item);

            avatar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, UserProfileActivity.class);
                    mContext.startActivity(intent);
                }
            });
        }

        public void setMessage(String message) {
            if (null == inbox_content) return;
            if(null == message) return;
            inbox_content.setText(message);
        }

    }

    public class InboxImageHolder extends RecyclerView.ViewHolder{
        private TextView inbox_time;
        private ImageView inbox_image;
        private ImageView avatar;

        public InboxImageHolder(View itemView) {
            super(itemView);
            inbox_time = (TextView) itemView.findViewById(R.id.inbox_time);
            inbox_image = (ImageView) itemView.findViewById(R.id.inbox_image);
            avatar = (ImageView) itemView.findViewById(R.id.avatar_item);

            inbox_image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();

                    Bitmap mImage = arr.get(getAdapterPosition()).getImage();
                    mImage.compress(Bitmap.CompressFormat.PNG, 100, stream);
                    byte[] byteArray = stream.toByteArray();

                    Intent intent = new Intent(mContext, ImageViewActivity.class);
                    intent.putExtra("image", byteArray);
                    mContext.startActivity(intent);
                }
            });
        }

        public void setImage(Bitmap bmp){
            if(null == inbox_image) return;
            if(null == bmp) return;
            inbox_image.setImageBitmap(bmp);
        }
    }
}
