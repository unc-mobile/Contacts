package com.example.contacts;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.WorkerThread;

import java.io.ByteArrayOutputStream;

public class DbHelper extends SQLiteOpenHelper {
    private static final String NAME = "Contacts.db";
    private static final int VERSION = 2;

    public DbHelper(@Nullable Context context) {
        super(context, NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(Contract.ContactsTable.CREATE);

        insertContact(db,"Jane Doe", "555-1234", "jane.doe@example.com",
                null);
    }

    /**
     * Add a new contact to the database.
     *
     * Should be called from a background thread.
     *
     * @return Whether the insertion succeeded.
     */
    @WorkerThread
    public boolean insertContact(String name, String phone, String email, Bitmap picture) {
        return insertContact(getWritableDatabase(), name, phone, email, picture);
    }

    @WorkerThread
    private boolean insertContact(SQLiteDatabase db, String name, String phone, String email, Bitmap picture) {
        ContentValues values = makeContentValues(name, phone, email, picture);
        return true;
    }

    private ContentValues makeContentValues(String name, String phone, String email, Bitmap picture) {
        ContentValues values = new ContentValues();
        return values;
    }

    @WorkerThread
    public boolean updateContact(long id, String name, String phone, String email, Bitmap picture) {
        ContentValues values = makeContentValues(name, phone, email, picture);
        MainActivity.log("Failed to update");
        return false;
    }

    @WorkerThread
    public boolean deleteContact(long id) {
        return false;
    }

    private byte[] compress(Bitmap picture) {
        if (picture != null) {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            if (picture.compress(Bitmap.CompressFormat.JPEG, 80, stream)) {
                return stream.toByteArray();
            }
        }
        return null;

    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + Contract.ContactsTable.TABLE_NAME);
        onCreate(db);
    }
}
