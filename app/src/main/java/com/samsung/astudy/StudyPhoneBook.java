package com.samsung.astudy;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.samsung.astudy.dialog.PersonInputDialog;
import com.samsung.astudy.dialog.StudyInputDialog;
import com.samsung.astudy.phoneBookDB.PhoneBookDBHelper;

import java.util.ArrayList;

public class StudyPhoneBook extends Activity implements View.OnClickListener {

    public static final String TAG = "StudyPhoneBook";
    private String PERSON_UPDATE_ACTION = "com.samsung.astudy.person_update_action";
    private String STUDY_UPDATE_ACTION = "com.samsung.astudy.study_update_action";
    private Context mContext;
    private ListView mListView;
    private StudyPhoneBookAdapter mAdapter;
    private LayoutInflater mInflater;
    private View mHeaderView;
    private View mFooterView;
    private ImageView mAddBtn;

    // HeaderView
    private ImageView mBack;
    private ImageView mNext;
    private TextView mStudyName;

    private BroadcastReceiver mBR;
    private PhoneBookDBHelper mHelper;
    private int mStudyNamePosition;
    private ArrayList<String> mStudyNames = new ArrayList<>();
    private ArrayList<PersonData> mStudyPersons = new ArrayList<>();

    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.study_phonebook);

        mContext = getApplicationContext();
        mHelper = new PhoneBookDBHelper(mContext);
        mStudyPersons = mHelper.query(mHelper);

        mStudyNamePosition = 0;
        setBR();
        createView();
    }

    private void setBR() {
        mBR = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if(intent.getAction().equals(PERSON_UPDATE_ACTION)) {
                    Log.d(TAG, "got Person update");
                    mStudyPersons = mHelper.query(mHelper);
                    setDataFromDB();
                } else if (intent.getAction().equals(STUDY_UPDATE_ACTION)) {
                    Log.d(TAG, "got Study name update");
                    mStudyNames = mHelper.studyNamequery(mHelper);
                }
            }
        };
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(PERSON_UPDATE_ACTION);
        intentFilter.addAction(STUDY_UPDATE_ACTION);
        registerReceiver(mBR, intentFilter);
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(mBR);
        super.onDestroy();
    }

    @SuppressLint("NewApi")
    private void createView() {
        mListView = (ListView) findViewById(R.id.phonebook_list);
        mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        mHeaderView = mInflater.inflate(R.layout.study_phonebook_header, null, false);
        mStudyName = (TextView) mHeaderView.findViewById(R.id.phonebook_study_name);
        mBack = (ImageView) mHeaderView.findViewById(R.id.phonebook_back);
        mNext = (ImageView) mHeaderView.findViewById(R.id.phonebook_next);
        mBack.setOnClickListener(this);
        mNext.setOnClickListener(this);

        mFooterView = mInflater.inflate(R.layout.study_phonebook_footer, null, false);

        mAdapter = new StudyPhoneBookAdapter(mContext);
        mListView.setAdapter(mAdapter);
        mListView.addHeaderView(mHeaderView);
        mListView.addFooterView(mFooterView);

        RelativeLayout addPersonLayout = (RelativeLayout) mFooterView.findViewById(R.id.phonebook_add_person_btn);
        addPersonLayout.setOnClickListener(this);

        int addBtnColor = mContext.getResources().getColor(R.color.phonebook_add_icon_bgcolor, null);
        mAddBtn = (ImageView) findViewById(R.id.phonebook_add_btn);
        mAddBtn.getDrawable().setTint(addBtnColor);
        mAddBtn.setOnClickListener(this);
        mStudyNames = mHelper.studyNamequery(mHelper);
        setDataFromDB();
    }

    private void setDataFromDB() {
        mAdapter.clear();
        String currentStudyName = mStudyNames.get(mStudyNamePosition);
        mBack.setVisibility(mStudyNamePosition != 0 ? View.VISIBLE : View.INVISIBLE);
        mNext.setVisibility(mStudyNamePosition != mStudyNames.size()-1 ? View.VISIBLE : View.INVISIBLE);
        mStudyName.setText(currentStudyName);
        for(PersonData p : mStudyPersons) {
            if (p.getmStudyName().equals(currentStudyName)) {
                mAdapter.addList(p);
            }
        }
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.phonebook_add_person_btn:
                // footer. add person
                PersonInputDialog personinputDialog = new PersonInputDialog(this);
                if (personinputDialog != null) {
                    personinputDialog.show();
                }
                break;
            case R.id.phonebook_add_btn:
                // floating. add study
                StudyInputDialog studyinputDialog = new StudyInputDialog(this);
                if (studyinputDialog != null) {
                    studyinputDialog.show();
                }
                break;
            case R.id.phonebook_back:
                mStudyNamePosition --;
                setDataFromDB();
                break;
            case R.id.phonebook_next:
                mStudyNamePosition ++;
                setDataFromDB();
                break;
            default:
                break;
        }

    }
}
