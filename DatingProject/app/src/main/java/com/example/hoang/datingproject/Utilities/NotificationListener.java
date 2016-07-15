package com.example.hoang.datingproject.Utilities;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.app.Service;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.IBinder;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import com.example.hoang.datingproject.Activity.MainActivity;
import com.example.hoang.datingproject.Activity.PersonalInfoActivity;
import com.example.hoang.datingproject.R;
import com.example.hoang.datingproject.Utilities.Const;
import com.example.hoang.datingproject.Utilities.RegisterUserClass;
import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;
import com.firebase.client.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class NotificationListener extends Service {
    public NotificationListener() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Firebase firebase = new Firebase(Const.FIRE_BASE_URL);

        Query queryRef = firebase.limitToLast(1);

        queryRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                String notificationID = PersonalInfoActivity.getDefaults("notificationID", getApplicationContext());

                if (!notificationID.equalsIgnoreCase(dataSnapshot.getKey().toString())) {
                    String msg = dataSnapshot.getValue().toString();

                    //If the value is anything other than none that means a notification has arrived
                    //calling the method to show notification
                    //String msg is containing the msg that has to be shown with the notification
                    showNotification(msg);
                    sendNotification("Dating Notification", msg);
                    PersonalInfoActivity.setDefault("notificationID", dataSnapshot.getKey().toString(), getApplicationContext());
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    private void showNotification(String msg){
        //Creating a notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setSmallIcon(R.drawable.admin);
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
        builder.setContentIntent(pendingIntent);
        builder.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.datingicon));
        builder.setContentTitle("Dating Notification");
        builder.setContentText(msg);
        Notification note = builder.build();

        note.defaults |= Notification.DEFAULT_VIBRATE;
        note.defaults |= Notification.DEFAULT_SOUND;
//        builder.setSound(Uri.parse("uri://sadfasdfasdf.mp3"));

        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(1, builder.build());
    }

    public void sendNotification(final String title, String contents){
        class SendNotification extends AsyncTask<String, Void, String> {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);

                try {
                    JSONObject jsonObject = new JSONObject(s);
                    String result = jsonObject.getString("result");
                    if(result.equalsIgnoreCase("1")){
                        Log.d(Const.LOG_TAG, "send notification success");
                    }else{
                        Log.d(Const.LOG_TAG, "send notification failed");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            protected String doInBackground(String... params) {
                HashMap<String,String> data = new HashMap<>();
                data.put(Const.NOTIFICATION_TITLE,params[0]);
                data.put(Const.NOTIFICATION_CONTENT,params[1]);

                RegisterUserClass ruc = new RegisterUserClass();

                String result = ruc.sendPostRequest(Const.NOTIFICATION_SEND_URL,data);

                Log.d(Const.LOG_TAG, result + "");
                return result;
            }
        }
        SendNotification send = new SendNotification();
        send.execute(title, contents);
    }
}
