package com.samsung.astudy.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.samsung.astudy.R;
import com.samsung.astudy.phoneBookDB.DBContract.PhoneBook;
import com.samsung.astudy.phoneBookDB.PhoneBookDBHelper;

import java.util.ArrayList;

public class PersonInputDialog extends Dialog implements View.OnClickListener {

    public static final String TAG = "PersonInputDialog";
    private String PERSON_UPDATE_ACTION = "com.samsung.astudy.person_update_action";
    private Context mContext;
    private TextView mCancel;
    private TextView mSave;
    private EditText mName;
    private Spinner mStudyNameSpinner;
    private String mStudyName;
    private EditText mTel;
    private PhoneBookDBHelper mHelper;
    private ArrayList<String> mStudyNames;

    public PersonInputDialog(Context context) {
        super(context);
        mContext = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.study_phonebook_person_input_dialog);
        mHelper = new PhoneBookDBHelper(mContext);
        onCreateView();

    }

    private void onCreateView() {
        mCancel = (TextView) findViewById(R.id.new_dialog_cancel);
        mSave = (TextView) findViewById(R.id.new_dialog_save);
        mStudyNameSpinner = (Spinner) findViewById(R.id.study_name_spinner);
        mName = (EditText) findViewById(R.id.new_dialog_input_name);
        mTel = (EditText) findViewById(R.id.new_dialog_input_tel);
        mTel.setFilters(new InputFilter[]{new InputFilter.LengthFilter(11)});

        setStudyNameToSpinner();

        //click
        mCancel.setOnClickListener(this);
        mSave.setOnClickListener(this);

        mName.requestFocus();
        InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
    }

    private void setStudyNameToSpinner() {
        mStudyNames = new ArrayList();
        mStudyNames.add("DEFAULT");
        mStudyNames = mHelper.studyNamequery(mHelper);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(mContext, android.R.layout.simple_spinner_dropdown_item, mStudyNames);
        mStudyNameSpinner.setAdapter(adapter);
        mStudyNameSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mStudyName = mStudyNames.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
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
                        bundle.putString(mStudyName, "DEFAULT");
                        mHelper.insert(mHelper, bundle);
                        Intent updateBR = new Intent();
                        updateBR.setAction(PERSON_UPDATE_ACTION);
                        mContext.sendBroadcast(updateBR);
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
