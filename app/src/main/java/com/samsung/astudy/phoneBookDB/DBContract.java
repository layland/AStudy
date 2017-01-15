package com.samsung.astudy.phoneBookDB;

// DB 스트링 모으미

import android.provider.BaseColumns;

public final class DBContract {

    public static final String TABLE_NAME = "AStudy_PhoneBook";

    public static final class PhoneBook implements BaseColumns {
        public static final String NAME = "name";
        public static final String TEL = "telephone";
        public static final String STUDY_NAME = "study_name";
        public static final String FM = "fm";
        public static final String LATITUDE = "latitude";
        public static final String LONGITUDE = "longitude";
    }

}
