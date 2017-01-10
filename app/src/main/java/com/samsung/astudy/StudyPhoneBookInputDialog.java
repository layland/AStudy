package com.samsung.astudy;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputFilter;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.samsung.astudy.phoneBookDB.DBContract;
import com.samsung.astudy.phoneBookDB.DBContract.PhoneBook;
import com.samsung.astudy.phoneBookDB.PhoneBookDBHelper;

public class StudyPhoneBookInputDialog extends Dialog implements View.OnClickListener {

    public static final String TAG = "StudyPhoneBookInputDialog";
    private Context mContext;
    private TextView mCancel;
    private TextView mSave;
    private EditText mName;
    //TODO: 스터디네임 스피너로 선택할 수 있게 하기
    private TextView mStudyName;
    private EditText mTel;
    private AlertDialog.Builder mBuilder;
    private PhoneBookDBHelper mHelper;

    public StudyPhoneBookInputDialog(Context context, boolean isStudyPerson) {
        super(context);
        mContext = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.study_phonebook_input_dialog);
        mHelper = new PhoneBookDBHelper(mContext);
        onCreateView();

    }

    private void onCreateView() {
        mCancel = (TextView) findViewById(R.id.new_dialog_cancel);
        mSave = (TextView) findViewById(R.id.new_dialog_save);
        mName = (EditText) findViewById(R.id.new_dialog_input_name);
        mTel = (EditText) findViewById(R.id.new_dialog_input_tel);
        mTel.setFilters(new InputFilter[]{new InputFilter.LengthFilter(11)});

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
        InputMethodManager immhide = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        immhide.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
        switch (v.getId()) {
            case R.id.new_dialog_cancel:
                dismiss();
                break;
            case R.id.new_dialog_save :
                if (checkEmptySpace()) {
                    // 쿼리 날려서 저장하기
                    synchronized (this) {
                        Bundle bundle = new Bundle();
                        bundle.putString(PhoneBook.NAME, mName.getText().toString());
                        bundle.putInt(PhoneBook.TEL, Integer.parseInt( mTel.getText().toString() ));
                        //TODO : 일단 다 기본그룹
                        bundle.putString(PhoneBook.STUDY_NAME, "default");
                        mHelper.insert(mHelper, bundle);
                    }
                    Toast.makeText(mContext, "saved", Toast.LENGTH_LONG).show();
                    dismiss();
                    break;
                }
        }
    }

        private boolean checkEmptySpace() {
        if (mName.getText().toString().equals("")) {
            Toast.makeText(mContext,"Please input the name 'ㅅ'!",Toast.LENGTH_SHORT).show();
            return false;
        }
        if (mTel.getText().toString().equals("")) {
            Toast.makeText(mContext,"Please input the tel 'ㅅ'!",Toast.LENGTH_SHORT).show();
            return false;
        }
            return true;
    }

}
