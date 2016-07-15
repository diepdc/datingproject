package com.example.hoang.datingproject.Fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.example.hoang.datingproject.Activity.PersonalInfoActivity;
import com.example.hoang.datingproject.R;
import com.example.hoang.datingproject.Utilities.Const;
import com.example.hoang.datingproject.Utilities.FontManager;
import com.example.hoang.datingproject.Utilities.RegisterUserClass;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by hoang on 4/14/2016.
 */
public class WriteNoteDialog extends DialogFragment {

    String itemID;
    Dialog dialog;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.write_note_dialog, null);
        builder.setView(view);

        dialog = builder.create();

        itemID = getArguments().getString("itemID");
        Log.d(Const.LOG_TAG, itemID);

        TextView exit_button = (TextView) view.findViewById(R.id.exit_button);
        TextView post_button = (TextView) view.findViewById(R.id.post);
        final EditText reply_item = (EditText) view.findViewById(R.id.reply_item);

        Typeface font = FontManager.getTypeface(getActivity(), FontManager.FONTAWESOME);
        exit_button.setTypeface(font);
        post_button.setTypeface(font);

        exit_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        post_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendReply(itemID, reply_item.getText().toString(), PersonalInfoActivity.getDefaults("id", getActivity()));
            }
        });

        return dialog;
    }

    public void sendReply(final String itemID, String contents, String writeUserID){
        class SendReply extends AsyncTask<String, Void, String> {
            ProgressDialog progressDialog;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                progressDialog = new ProgressDialog(getActivity(),
                        R.style.AppTheme_Dark_Dialog);
                progressDialog.setIndeterminate(true);
                progressDialog.setMessage("Loading...");
                progressDialog.show();
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);

                try {
                    JSONObject jsonObject = new JSONObject(s);
                    String result = jsonObject.getString("result");
                    if(result.equalsIgnoreCase("1")){
                        Log.d(Const.LOG_TAG, "send reply success");
                    }else{
                        Log.d(Const.LOG_TAG, "send reply failed");
                    }
                    dialog.dismiss();
                    progressDialog.dismiss();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            protected String doInBackground(String... params) {
                HashMap<String,String> data = new HashMap<>();
                data.put(Const.ITEMID,params[0]);
                data.put(Const.REPLY_CONTENT,params[1]);
                data.put(Const.WRITE_USER,params[2]);

                RegisterUserClass ruc = new RegisterUserClass();

                String result = ruc.sendPostRequest(Const.REPLY_TIMELINE_URL,data);

                Log.d(Const.LOG_TAG, result + "");
                return result;
            }
        }
        SendReply send = new SendReply();
        send.execute(itemID, contents, writeUserID);
    }
}
