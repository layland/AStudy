package com.samsung.astudy.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.samsung.astudy.R;
import com.samsung.astudy.phoneBookDB.DBContract;
import com.samsung.astudy.phoneBookDB.PhoneBookDBHelper;

public class StudyInputDialog extends Dialog implements View.OnClickListener {

    public static final String TAG = "StudyInputDialog";
    private String STUDY_UPDATE_ACTION = "com.samsung.astudy.study_update_action";

    private Context mContext;
    private TextView mCancel;
    private TextView mSave;
    private EditText mStudyName;
    private PhoneBookDBHelper mHelper;

    public StudyInputDialog(Context context) {
        super(context);
        mContext = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.study_phonebook_study_input_dialog);
        mHelper = new PhoneBookDBHelper(mContext);
        onCreateView();
    }

    private void onCreateView() {
        mCancel = (TextView) findViewById(R.id.new_dialog_cancel);
        mSave = (TextView) findViewById(R.id.new_dialog_save);
        mStudyName = (EditText) findViewById(R.id.new_dialog_input_study_name);

        //click
        mCancel.setOnClickListener(this);
        mSave.setOnClickListener(this);

        mStudyName.requestFocus();
        InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
    }

    @Override
    public void onClick(View v) {
        InputMethodManager immhide = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        immhide.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
        switch (v.getId()) {
            case R.id.new_dialog_cancel:
                dismiss();
                break;
            case R.id.new_dialog_save :
                String study_name =  mStudyName.getText().toString();
                if (checkEmptySpace() && checkDuplicatedName(study_name)) {
                    // 쿼리 날려서 저장하기
                    synchronized (this) {
                        Bundle bundle = new Bundle();
                        bundle.putString(DBContract.PhoneBook.STUDY_NAME, study_name);
                        mHelper.studyNameInsert(mHelper, bundle);
                        Intent updateBR = new Intent();
                        updateBR.setAction(STUDY_UPDATE_ACTION);
                        mContext.sendBroadcast(updateBR);

                    }
                    Toast.makeText(mContext, "saved", Toast.LENGTH_LONG).show();
                    dismiss();
                    break;
                }
        }
    }

    private boolean checkEmptySpace() {
        if (mStudyName.getText().toString().equals("")) {
            Toast.makeText(mContext,"Please input the study name 'ㅅ'!",Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private boolean checkDuplicatedName(String studyName) {
        if(mHelper.ifStudyExist(mHelper, studyName)) {
            Toast.makeText(mContext,"Already exist 'ㅅ'!",Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
}
