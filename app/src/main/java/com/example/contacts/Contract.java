package com.example.contacts;

import android.provider.BaseColumns;

public class Contract {
    public static class ContactsTable implements BaseColumns {
        public static final String TABLE_NAME = "contacts";
        public static final String COLUMN_NAME_NAME = "name";
        public static final String COLUMN_NAME_PHONE = "phone";
        public static final String COLUMN_NAME_EMAIL = "email";
        public static final String COLUMN_NAME_PICTURE = "picture";

        public static final String CREATE = "CREATE TABLE " + TABLE_NAME + " ( "
                + _ID + " INTEGER PRIMARY KEY, "
                + COLUMN_NAME_NAME + " TEXT, "
                + COLUMN_NAME_PHONE + " TEXT, "
                + COLUMN_NAME_EMAIL + " TEXT, "
                + COLUMN_NAME_PICTURE + " BLOB)";
    }
}
