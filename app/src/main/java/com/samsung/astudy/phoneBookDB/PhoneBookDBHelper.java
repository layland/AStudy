package com.samsung.astudy.phoneBookDB;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;

public class PhoneBookDBHelper extends SQLiteOpenHelper {

    public static final String TAG = "PhoneBookDBHelper";
    public static final String DB_NAME = "phonebook.db";
    public static final int VERSION = 4;
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
        db.execSQL("DROP TABLE IF EXISTS " + DBContract.TABLE_NAME);
        createPhoneBookDB(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + DBContract.TABLE_NAME);
        createPhoneBookDB(db);
    }

    private void createPhoneBookDB(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS " + DBContract.TABLE_NAME+" ("
                + DBContract.PhoneBook.NAME + " TEXT NOT NULL, "
                + DBContract.PhoneBook.TEL + " INTEGER NOT NULL, "
                + DBContract.PhoneBook.STUDY_NAME + " TEXT NOT NULL,"
                + DBContract.PhoneBook.FM + " INTEGER NOT NULL, "
                + DBContract.PhoneBook.LATITUDE + " LONG NOT NULL,"
                + DBContract.PhoneBook.LONGITUDE  + " LONG NOT NULL);");
    }

    public void insert(PhoneBookDBHelper helper, Bundle bundle) {
        helper.getWritableDatabase();
        String studyName = bundle.getString(DBContract.PhoneBook.STUDY_NAME, "default");
        String personName = bundle.getString(DBContract.PhoneBook.NAME);
        int telephoneNumber = bundle.getInt(DBContract.PhoneBook.TEL);
        int fm = bundle.getInt(DBContract.PhoneBook.FM, 0);
        long latitude = bundle.getInt(DBContract.PhoneBook.LATITUDE, 0);
        long longitude = bundle.getInt(DBContract.PhoneBook.LONGITUDE, 0);

        String sql_insert = "INSERT INTO " + DBContract.TABLE_NAME + " VALUES ('"
                + personName + "', '" + telephoneNumber + "', '" + studyName + "', '"
                + fm + "', '" + latitude + "', '" + longitude + "');";

        mDB.execSQL(sql_insert);
        helper.close();
    }
}
