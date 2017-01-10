package com.samsung.astudy;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class StudyPhoneBookInputDialog extends Dialog implements View.OnClickListener {

    private Context mContext;
    private TextView mCancel;
    private TextView mSave;
    private EditText mName;
    private EditText mTel;
    private AlertDialog.Builder mBuilder;

    public StudyPhoneBookInputDialog(Context context, boolean isStudyPerson) {
        super(context);
        mContext = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.study_phonebook_input_dialog);
        onCreateView();

    }

    private void onCreateView() {
        mCancel = (TextView) findViewById(R.id.new_dialog_cancel);
        mSave = (TextView) findViewById(R.id.new_dialog_save);
        mName = (EditText) findViewById(R.id.new_dialog_input_name);
        mTel = (EditText) findViewById(R.id.new_dialog_input_tel);

        //click
        mCancel.setOnClickListener(this);
        mSave.setOnClickListener(this);

        // 여러분이 원하시던 키보드 튀어나오미 'ㅅ'
        mName.requestFocus();
        InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.new_dialog_cancel:
                dismiss();
                break;
            case R.id.new_dialog_save :
                // 쿼리 날려서 저장하기
                Toast.makeText(mContext,"saved",Toast.LENGTH_LONG).show();
                dismiss();
                break;
        }
    }
}
