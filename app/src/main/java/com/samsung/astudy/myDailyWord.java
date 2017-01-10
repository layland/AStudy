package com.samsung.astudy;


import android.app.TabActivity;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TabHost.TabContentFactory;
import android.widget.Toast;

import java.util.ArrayList;

public class myDailyWord extends TabActivity {
    myDailyWordDBManager dManager;
    TabHost tabHost;
    ListView wordListView;
    ArrayList<studyMyDailyWordDTO> wordList;
    ArrayList<String> wordDisplay;
    ArrayAdapter<String> arrayAdapter;
    EditText inputWord, inputMean;
    ListView updateListView;
    EditText updateSpel, updateMean;
    ArrayAdapter<String> updateAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initTab();
        initDB();
        SQLiteDatabase db = dManager.getWritableDatabase();
        studyMydailyWordDAO.insertSampleData(db);
        getWordList();

    }
    public void doUpdate(View v) {
        updateListView = (ListView) findViewById(R.id.updateListView);
        updateSpel = (EditText) findViewById(R.id.updateSpel);
        updateMean = (EditText) findViewById(R.id.updateMean);
        updateAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_multiple_choice,
                wordDisplay);
        if (updateListView == null) {
            Toast.makeText(this, "자료가 없습니다.",  Toast.LENGTH_SHORT).show();
        }
        updateListView.setAdapter(updateAdapter);
        updateListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        updateListView.setOnItemClickListener(listener);
    }
    AdapterView.OnItemClickListener listener =
            new AdapterView.OnItemClickListener() {
                public void onItemClick(AdapterView<?> arg0, View arg1,int position, long arg3) {
                    displayUpdate(position);
                }
            };

    int selectedId = -1;
    public void displayUpdate(int index) {
        studyMyDailyWordDTO selectedWord = wordList.get(index);
        updateSpel.setText(selectedWord.getWord());
        updateMean.setText(selectedWord.getMean());
        selectedId = selectedWord.getNum();
    }
    public void commitEvent(View v) {
        if (inputWord == null) {
            inputWord = (EditText) findViewById(R.id.inputWord);
            inputMean = (EditText) findViewById(R.id.inputMean);
        }
        switch (v.getId()) {
            case R.id.saveWord:
                String iWord = inputWord.getText().toString();
                String iMean = inputMean.getText().toString();

                studyMyDailyWordDTO dto = new studyMyDailyWordDTO(0, iWord, iMean);
                studyMydailyWordDAO.insert(dManager.getWritableDatabase(), dto);
                getWordList();
                inputWord.setText("");
                inputMean.setText("");
                Toast.makeText(this, "새로운 단어 저장성공!", Toast.LENGTH_SHORT).show();
                break;
            case R.id.saveBtn:
                String uSpel = updateSpel.getText().toString();
                String uMean = updateMean.getText().toString();
                studyMyDailyWordDTO uWord = new studyMyDailyWordDTO(selectedId, uSpel, uMean);
                studyMydailyWordDAO.update(dManager.getWritableDatabase(), uWord);
                Toast.makeText(this, "수정하였습니다.",  Toast.LENGTH_SHORT).show();
                getWordList();
                updateAdapter.notifyDataSetChanged();
                doUpdate(null);
                break;
            case R.id.updateBtn:
                break;
            case R.id.deleteBtn:
                SparseBooleanArray sba = updateListView.getCheckedItemPositions();
                ArrayList<Integer> selectedIndexs = new ArrayList<Integer>();
                if (sba.size() > 0) {
                    int n = sba.size();
                    for (int i = 0; i < n; i++) {
                        if (sba.get(i)) {
                            selectedIndexs.add(i);
                        }
                    }
                }
                int[] nums = new int[selectedIndexs.size()];

                for (int i = 0; i < selectedIndexs.size(); i++) {
                    int index = selectedIndexs.get(i);
                    studyMyDailyWordDTO word = wordList.get(index);
                    nums[i] = word.getNum();
                }
                studyMydailyWordDAO.deleteAll(dManager.getWritableDatabase(), nums);
                updateListView.clearChoices();
                getWordList();
                doUpdate(null);
                break;
        }
    }
    public void getWordList() {
        SQLiteDatabase db = dManager.getWritableDatabase();
        wordList = studyMydailyWordDAO.getList(db);
        wordDisplay = new ArrayList<String>();
        for (studyMyDailyWordDTO tmp : wordList) {
            String line = tmp.getWord() + " : " + tmp.getMean();
            wordDisplay.add(line);
        }
        arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, wordDisplay); //출력할 모델객체
        wordListView = (ListView) findViewById(R.id.wordListView);
        wordListView.setAdapter(arrayAdapter);
    }

    public void initTab() {
        tabHost = getTabHost();
        Drawable icon1 = getResources().getDrawable(android.R.drawable.ic_menu_search);
        Drawable icon2 = getResources().getDrawable(android.R.drawable.ic_menu_report_image);
        Drawable icon3 = getResources().getDrawable(android.R.drawable.ic_menu_set_as);
        Drawable icon4 = getResources().getDrawable(android.R.drawable.ic_menu_rotate);
        TabFactory factory = new TabFactory();
        tabHost.addTab(tabHost.newTabSpec("1").setIndicator("검색", icon1)
                .setContent(factory));
        tabHost.addTab(tabHost.newTabSpec("2")
                .setIndicator("입력", icon2)
                .setContent(factory));
        tabHost.addTab(tabHost.newTabSpec("3")
                .setIndicator("수정", icon3)
                .setContent(factory));
        tabHost.addTab(tabHost.newTabSpec("4")
                .setIndicator("설정", icon4)
                .setContent(factory));
    }
    public void initDB() {
        dManager = new myDailyWordDBManager(this, "dictionary.db", null, 1);
    }
    @Override
    public void onStart() {
        super.onStart();
    }
    @Override
    public void onStop() {
        super.onStop();
    }
    class TabFactory implements TabContentFactory {
        public View createTabContent(String tag) {
            int pageNum = Integer.parseInt(tag);
            LayoutInflater inflater = (LayoutInflater)
                    getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = null;
            switch (pageNum) {
                case 1:
                    view = inflater.inflate(R.layout.my_daily_word_page1, null);
                    break;
                case 2:
                    view = inflater.inflate(R.layout.my_daily_word_page2, null);
                    break;
                case 3:
                    view = inflater.inflate(R.layout.my_daily_word_page3, null);
                    break;
                case 4:
                    view = inflater.inflate(R.layout.my_daily_word_page4, null);
                    break;
            }
            return view;
        }
    }
}


