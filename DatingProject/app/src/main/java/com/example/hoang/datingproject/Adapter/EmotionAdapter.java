package com.example.hoang.datingproject.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.hoang.datingproject.Fragment.EmotionFragment;
import com.example.hoang.datingproject.Model.ImageItem;
import com.example.hoang.datingproject.R;

import java.util.ArrayList;

/**
 * Created by hoang on 4/11/2016.
 */
public class EmotionAdapter extends RecyclerView.Adapter<EmotionAdapter.EmotionHolder> {
    private Context mContext;
    private ArrayList<ImageItem> imgArr;
    private ImageItem imageItem ;
    private EmotionFragment.ListSelectionListener mListener;

    public void attachInteface(EmotionFragment.ListSelectionListener listener){
        this.mListener = listener;
    }

    public EmotionAdapter(Context context, ArrayList<ImageItem> arr){
        this.mContext = context;
        this.imgArr = arr;
    }

    @Override
    public EmotionHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.emotion_item, parent, false);
        return new EmotionHolder(view);
    }

    @Override
    public void onBindViewHolder(EmotionHolder holder, int position) {
        imageItem = imgArr.get(position);
        Toast.makeText(mContext, imageItem.getImage().toString(), Toast.LENGTH_SHORT);
        holder.imageView.setImageBitmap(imageItem.getImage());
    }

    @Override
    public int getItemCount() {
        return imgArr.size();
    }

    public class EmotionHolder extends RecyclerView.ViewHolder {

        private ImageView imageView;

        public EmotionHolder(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.image);
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.onListSelection(getAdapterPosition(), imgArr);
                }
            });
        }
    }
}
