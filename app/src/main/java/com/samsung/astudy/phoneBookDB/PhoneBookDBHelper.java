package com.samsung.astudy.phoneBookDB;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.util.Log;

import com.samsung.astudy.PersonData;

import java.util.ArrayList;

public class PhoneBookDBHelper extends SQLiteOpenHelper {

    public static final String TAG = "PhoneBookDBHelper";
    public static final String DB_NAME = "phonebook.db";
    public static final int VERSION = 6;
    private SQLiteDatabase mDB;

    public PhoneBookDBHelper(Context context) {
        super(context, DB_NAME, null, VERSION);
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        mDB = db;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        createPhoneBookDB(db);
        createStudyNameDB(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + DBContract.PhoneBook.TABLE_NAME);
        createPhoneBookDB(db);
        db.execSQL("DROP TABLE IF EXISTS " + DBContract.Study.TABLE_NAME);
        createStudyNameDB(db);
    }




    /*     Create     */
    private void createPhoneBookDB(SQLiteDatabase db) {
        String sql_create = "CREATE TABLE IF NOT EXISTS " + DBContract.PhoneBook.TABLE_NAME + " ("
                + DBContract.PhoneBook.NAME + " TEXT NOT NULL, "
                + DBContract.PhoneBook.TEL + " TEXT NOT NULL, "
                + DBContract.PhoneBook.STUDY_NAME + " TEXT NOT NULL,"
                + DBContract.PhoneBook.FM + " INTEGER NOT NULL, "
                + DBContract.PhoneBook.LATITUDE + " LONG NOT NULL,"
                + DBContract.PhoneBook.LONGITUDE + " LONG NOT NULL);";
        db.execSQL(sql_create);
    }

    private void createStudyNameDB(SQLiteDatabase db) {
        String sql_create = "CREATE TABLE IF NOT EXISTS " + DBContract.Study.TABLE_NAME + " ("
                + DBContract.Study.STUDY_NAME + " TEXT NOT NULL);";
        db.execSQL(sql_create);
    }



    /*     Insert     */
    public void insert(PhoneBookDBHelper helper, Bundle bundle) {
        helper.getWritableDatabase();
        String studyName = bundle.getString(DBContract.PhoneBook.STUDY_NAME, "DEFAULT");
        String personName = bundle.getString(DBContract.PhoneBook.NAME);
        String telephoneNumber = bundle.getString(DBContract.PhoneBook.TEL);
        int fm = bundle.getInt(DBContract.PhoneBook.FM, 0);
        long latitude = bundle.getInt(DBContract.PhoneBook.LATITUDE, 0);
        long longitude = bundle.getInt(DBContract.PhoneBook.LONGITUDE, 0);
        String sql_insert = "INSERT INTO " + DBContract.PhoneBook.TABLE_NAME + " VALUES ('"
                + personName + "', '" + telephoneNumber + "', '" + studyName + "', '"
                + fm + "', '" + latitude + "', '" + longitude + "');";
        mDB.execSQL(sql_insert);
        helper.close();
    }

    public void studyNameInsert(PhoneBookDBHelper helper, Bundle bundle) {
        helper.getWritableDatabase();
        String studyName = bundle.getString(DBContract.Study.STUDY_NAME, "DEFAULT");
        String sql_insert = "INSERT INTO " + DBContract.Study.TABLE_NAME + " VALUES ('"
                + studyName + "');";
        mDB.execSQL(sql_insert);
        helper.close();
    }




    /*     Update     */
    public void update(PhoneBookDBHelper helper, Bundle bundle) {

    }

    public void studyNameUpdate(PhoneBookDBHelper helper, Bundle bundle) {

    }


    /*     Delete     */
    public void delete(PhoneBookDBHelper helper, Bundle bundle) {

    }
    public void studyNameDelete(PhoneBookDBHelper helper, Bundle bundle) {

    }



    /*     Query     */
    public ArrayList<PersonData> query(PhoneBookDBHelper helper) {
        // query to get all person
        ArrayList<PersonData> persons = new ArrayList<>();
        helper.getReadableDatabase();
        String sql_query = "SELECT " + DBContract.PhoneBook.NAME +", "
                + DBContract.PhoneBook.TEL + ", "
                + DBContract.PhoneBook.STUDY_NAME + ", "
                +DBContract.PhoneBook.FM
                + " FROM " + DBContract.PhoneBook.TABLE_NAME+";";
        Cursor result = mDB.rawQuery(sql_query, null);
        while(result.moveToNext()) {
            //Log.d(TAG, "0 : "+result.getString(0)+" 2:" + result.getString(2));
            String person_name = result.getString(0);
            String telephone = result.getString(1);
            String study_name = result.getString(2);
            int fm = result.getInt(3);
            persons.add(new PersonData(fm == 0, study_name, person_name, String.valueOf(telephone)));
        }
        result.close();
        helper.close();
        return persons;
    }

    public ArrayList<String> studyNamequery(PhoneBookDBHelper helper) {
        // query to get all study names
        helper.getReadableDatabase();
        ArrayList<String> studies = new ArrayList<>();
        studies.add("DEFAULT");
        String sql_query = "SELECT " + DBContract.Study.STUDY_NAME
                + " FROM " + DBContract.Study.TABLE_NAME+";";
        Cursor result = mDB.rawQuery(sql_query, null);
        while(result.moveToNext()) {
            String studyName = result.getString(0);
            studies.add(studyName);
        }
        result.close();
        helper.close();
        return studies;
    }

    public boolean ifStudyExist(PhoneBookDBHelper helper, String studyName) {
        // query to check study is existed
        helper.getReadableDatabase();
        String sql_query = "SELECT " + DBContract.Study.STUDY_NAME
                + " FROM " + DBContract.Study.TABLE_NAME
                + " WHERE " + DBContract.Study.STUDY_NAME + "='" + studyName+"';" ;
        Cursor result = mDB.rawQuery(sql_query, null);
        if(result.getCount() <= 0){
            result.close();
            helper.close();
            return false;
        }
        result.close();
        helper.close();
        return true;
    }

    public void cleanUp(PhoneBookDBHelper helper) {
        // to Drop the table
        helper.getWritableDatabase();
        mDB.execSQL("DROP TABLE IF EXISTS " + DBContract.PhoneBook.TABLE_NAME);
        createPhoneBookDB(mDB);
        mDB.execSQL("DROP TABLE IF EXISTS " + DBContract.Study.TABLE_NAME);
        createStudyNameDB(mDB);
    }
}
