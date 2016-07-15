package com.example.hoang.datingproject.Fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.hoang.datingproject.R;
import com.example.hoang.datingproject.Utilities.FontManager;
import com.yahoo.mobile.client.android.util.rangeseekbar.RangeSeekBar;

/**
 * Created by hoang on 4/17/2016.
 */
public class SearchAccountDialogFragment extends DialogFragment implements View.OnClickListener{

    private Button bt1, bt2, bt3, search;
    private Dialog dialog;
    private RangeSeekBar<Integer> rangeSeekBar;
    private int minAge, maxAge;
    private String gender = "male";

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.search_account_dialog, null);
        builder.setView(view);

        dialog = builder.create();

        TextView exit_button = (TextView) view.findViewById(R.id.exit_button);
        Typeface font = FontManager.getTypeface(getActivity(), FontManager.FONTAWESOME);
        exit_button.setTypeface(font);

        bt1 = (Button) view.findViewById(R.id.personal_btn1);
        bt2 = (Button) view.findViewById(R.id.personal_btn2);
        bt3 = (Button) view.findViewById(R.id.personal_btn3);
        search = (Button) view.findViewById(R.id.searchPeople);

        rangeSeekBar = (RangeSeekBar<Integer>) view.findViewById(R.id.rangeseekbar);
        minAge = 15;
        maxAge = 100;

        bt1.setOnClickListener(this);
        bt2.setOnClickListener(this);
        bt3.setOnClickListener(this);
        search.setOnClickListener(this);
        exit_button.setOnClickListener(this);

        bt1.setSelected(true);
        bt1.setPressed(true);

        rangeSeekBar.setOnRangeSeekBarChangeListener(new RangeSeekBar.OnRangeSeekBarChangeListener<Integer>() {
            @Override
            public void onRangeSeekBarValuesChanged(RangeSeekBar<?> bar, Integer minValue, Integer maxValue) {
                minAge = minValue;
                maxAge = maxValue;
            }
        });

        return dialog;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.personal_btn1:
                bt1.setSelected(true);
                bt1.setPressed(true);
                bt2.setSelected(false);
                bt2.setPressed(false);
                bt3.setSelected(false);
                bt3.setPressed(false);
                gender = "male";
                break;
            case R.id.personal_btn2:
                bt1.setSelected(false);
                bt1.setPressed(false);
                bt2.setSelected(true);
                bt2.setPressed(true);
                bt3.setSelected(false);
                bt3.setPressed(false);
                gender = "female";
                break;
            case R.id.personal_btn3:
                bt1.setSelected(false);
                bt1.setPressed(false);
                bt2.setSelected(false);
                bt2.setPressed(false);
                bt3.setSelected(true);
                bt3.setPressed(true);
                gender = "gay/les";
                break;
            case R.id.searchPeople:
                Intent intent = new Intent();
                intent.putExtra("minAge", minAge);
                intent.putExtra("maxAge", maxAge);
                intent.putExtra("gender", gender);
                getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, intent);
                dialog.dismiss();
                break;
            case R.id.exit_button:
                dialog.dismiss();
                break;
        }
    }
}
