package com.example.hoang.datingproject.Activity;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ImageSpan;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hoang.datingproject.Fragment.EmotionFragment;
import com.example.hoang.datingproject.Fragment.FeedsFragment;
import com.example.hoang.datingproject.Model.ImageItem;
import com.example.hoang.datingproject.R;
import com.example.hoang.datingproject.Utilities.Const;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class WrittingNoteActivity extends AppCompatActivity implements EmotionFragment.ListSelectionListener, View.OnClickListener {

    private ImageView btn_bold, btn_italic, btn_link, btn_email, btn_capture, btn_emotion;
    private EditText content;
    private FragmentManager mFragmentManager;
    private FrameLayout mEmotionsFrameLayout;
    private EmotionFragment emotionFragment = new EmotionFragment();
    private static int count;
    private FragmentTransaction fragmentTransaction;
    private Bitmap img = null;
    private ArrayList<Integer> imgId = new ArrayList<Integer>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_writting_note);
        getControls();
    }

    private void getControls(){
        btn_bold = (ImageView) findViewById(R.id.boldButton);
        btn_italic = (ImageView) findViewById(R.id.italicButton);
        btn_link = (ImageView) findViewById(R.id.linkButton);
        btn_email = (ImageView) findViewById(R.id.mailButton);
        btn_capture = (ImageView) findViewById(R.id.captureButton);
        btn_emotion = (ImageView) findViewById(R.id.emotionButton);
        content = (EditText) findViewById(R.id.content);

        registerForContextMenu(btn_capture);

        btn_bold.setOnClickListener(this);
        btn_italic.setOnClickListener(this);
        btn_link.setOnClickListener(this);
        btn_email.setOnClickListener(this);
        btn_capture.setOnClickListener(this);
        btn_emotion.setOnClickListener(this);

        count = 0;

        mEmotionsFrameLayout = (FrameLayout) findViewById(R.id.emotion_fragment_container);
        // Get a reference to the FragmentManager
        mFragmentManager = getSupportFragmentManager();
    }

    @Override
    public void onListSelection(int index, ArrayList<ImageItem> arr) {
        imgId.add(index);
//        content.setText(content.getText() + "(" + arr.get(index).getTitle() + ")");
        addImageBetweenText(arr.get(index).getImage());
//        Toast.makeText(this, arr.get(index).getImage().toString(), Toast.LENGTH_LONG ).show();
//        content.setSelection(content.length());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.boldButton:
                content.setText(content.getText() + "<strong></strong>");
                content.setSelection(content.length());
                break;
            case R.id.italicButton:
                content.setText(content.getText() + "<em></em>");
                content.setSelection(content.length());
                break;
            case R.id.linkButton:
//                showDialog();
                break;
            case R.id.mailButton:
//                showDialog();
                break;
            case R.id.captureButton:

                break;
            case R.id.emotionButton:
                if (count % 2 == 0){
                    RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 300);
                    layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);

                    mEmotionsFrameLayout.setLayoutParams(layoutParams);
//                    mQuotesFrameLayout.setBackgroundResource(R.color.gray);
                    fragmentTransaction = mFragmentManager
                            .beginTransaction();
                    fragmentTransaction.add(R.id.emotion_fragment_container,
                            emotionFragment, "emotion");
                    fragmentTransaction.addToBackStack(null);

                    // Commit the FragmentTransaction
                    fragmentTransaction.commit();

                } else {
                    getSupportFragmentManager().beginTransaction().
                            remove(getSupportFragmentManager().findFragmentByTag("emotion")).commit();
                    RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0);
                    layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);

                    mEmotionsFrameLayout.setLayoutParams(layoutParams);
                }
                count++;
                break;
        }
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, Const.REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    public void onBackPressed() {
        if(count % 2 !=0){
            getSupportFragmentManager().beginTransaction().
                    remove(getSupportFragmentManager().findFragmentByTag("emotion")).commit();
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0);
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);

            mEmotionsFrameLayout.setLayoutParams(layoutParams);
            count++;
        }else {
            FeedsFragment tab2Fragment = new FeedsFragment();
            Bundle data = new Bundle();
            data.putString("content", content.getText().toString());
            tab2Fragment.setArguments(data);
            finish();
        }
    }

    private void addImageBetweenText(Bitmap bitmap){

        Drawable drawable = new BitmapDrawable(getResources(), bitmap);

        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());

        int selectionCursor = content.getSelectionStart();
        content.getText().insert(selectionCursor, ".");
        selectionCursor = content.getSelectionStart();

        SpannableStringBuilder builder = new SpannableStringBuilder(content.getText());
        builder.setSpan(new ImageSpan(drawable), selectionCursor - ".".length(), selectionCursor, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        content.setText(builder, TextView.BufferType.SPANNABLE);
        content.setSelection(selectionCursor);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Const.REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            if (data != null) {
                Bundle extras = data.getExtras();
                Bitmap imageBitmap = (Bitmap) extras.get("data");
                addImageBetweenText(imageBitmap);
            }
        } else if (requestCode == Const.PICTURE_CHOOSE && resultCode == RESULT_OK) {
            if (data != null) {
                Cursor cursor = getContentResolver().query(data.getData(), null, null, null, null);
                cursor.moveToFirst();
                int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                String fileSrc = cursor.getString(idx);

                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = true;
                img = BitmapFactory.decodeFile(fileSrc, options);

                //scale size to read
                options.inSampleSize = Math.max(1, (int) Math.ceil(Math.max((double) options.outWidth / 1024f, (double) options.outHeight / 1024f)));
                options.inJustDecodeBounds = false;
                img = BitmapFactory.decodeFile(fileSrc, options);

                addImageBetweenText(img);
            }
        }
    }

    private File createImageFile() throws IOException {

        String mCurrentPhotoPath;

        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = "file:" + image.getAbsolutePath();
        Toast.makeText(WrittingNoteActivity.this, mCurrentPhotoPath, Toast.LENGTH_LONG).show();
        return image;
    }

    private void getImageFromGallery() {
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, Const.PICTURE_CHOOSE);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        getMenuInflater().inflate(R.menu.my_context_menu, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch(item.getItemId())
        {
            case R.id.capture:
                dispatchTakePictureIntent();
                break;
            case R.id.gallery:
                getImageFromGallery();
                break;
        }
        return super.onContextItemSelected(item);
    }
}
