package com.samsung.astudy.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.samsung.astudy.R;
import com.samsung.astudy.phoneBookDB.PhoneBookDBHelper;

public class PersonDetailDialog  extends Dialog implements View.OnClickListener {

    private Context mContext;
    private TextView mCancel;
    private TextView mModify;
    private String mStudyName;
    private String mPersonName;
    private String mTelephone;
    private Bundle mDataBundle;

    public PersonDetailDialog(Context context, Bundle bundle) {
        super(context);
        Log.d("hi","hi11");
        mContext = context;
        mDataBundle = bundle;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("hi", "bye11");
        if(mDataBundle == null) dismiss();
        setContentView(R.layout.study_phonebook_person_detail_dialog);
        onCreateView();
    }

    private void onCreateView() {
        mStudyName = mDataBundle.getString("studyName", "DEFAULT");
        mPersonName = mDataBundle.getString("personName", "no name T_T");
        mTelephone = mDataBundle.getString("telNumber", "000000");

        TextView studyNameTv = (TextView) findViewById(R.id.detail_study_name);
        TextView personNameTv = (TextView) findViewById(R.id.detail_dialog_input_name);
        TextView telephoneTv = (TextView) findViewById(R.id.detail_dialog_input_tel);
        studyNameTv.setText(mStudyName);
        personNameTv.setText(mPersonName);
        telephoneTv.setText(mTelephone);

        mCancel = (TextView) findViewById(R.id.detail_dialog_cancel);
        mModify = (TextView) findViewById(R.id.detail_dialog_modify);
        mCancel.setOnClickListener(this);
        mModify.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
       switch (v.getId()) {
           case R.id.detail_dialog_cancel:
               dismiss();
               break;
           case R.id.detail_dialog_modify:
               Intent callInputDialog = new Intent();
               callInputDialog.putExtra("studyName", mStudyName);
               callInputDialog.putExtra("personName", mPersonName);
               callInputDialog.putExtra("telephone", mTelephone);

               //TODO : inputdialog로 보내기, 거기서는 이거 handling하기
               dismiss();
               break;
           default:
               break;
       }
    }
}
