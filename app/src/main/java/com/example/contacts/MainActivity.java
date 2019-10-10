package com.example.contacts;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements ListView.OnItemClickListener {

    public static void log(String msg) {
        Log.d("SCROGGO", msg);
    }

    // Request codes for activities started by this one.
    private static final int REQUEST_CODE_NEW_CONTACT = 1;
    private static final int REQUEST_CODE_EDIT_CONTACT = 2;


    private ListView mContactsList;
    private ContactsAdapter mContactsAdapter = null;

    private Button mNewContactButton;

    private DbHelper mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mDatabase = new DbHelper(this);

        mContactsList = findViewById(R.id.contacts_list);
        new RefreshContactsList().execute();
        mContactsList.setOnItemClickListener(this);

        mNewContactButton = findViewById(R.id.new_contact);
    }

    public void onClick(View view) {
        if (view == mNewContactButton) {
            Intent intent = new Intent(this, EditContactActivity.class);
            intent.putExtra(Intent.EXTRA_TITLE, "New contact");
            startActivityForResult(intent, REQUEST_CODE_NEW_CONTACT);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        new GetContactInfo().execute(id);
    }

    private class GetContactInfo extends AsyncTask<Long, Void, Cursor> {
        @Override
        protected void onPostExecute(Cursor cursor) {
            if (cursor == null || cursor.getCount() == 0) {
                Toast.makeText(MainActivity.this, "Could not find contact!",
                        Toast.LENGTH_SHORT).show();
                return;
            }
            if (cursor.getCount() > 1) {
                Toast.makeText(MainActivity.this, "Found too many contacts!",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            if (!cursor.moveToFirst()) {
                Toast.makeText(MainActivity.this, "Failed to access contact!",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            Intent intent = new Intent(MainActivity.this, EditContactActivity.class);
            intent.putExtra(Intent.EXTRA_TITLE, "New contact");

            for (String key : new String[] {
                    Contract.ContactsTable.COLUMN_NAME_NAME,
                    Contract.ContactsTable.COLUMN_NAME_PHONE,
                    Contract.ContactsTable.COLUMN_NAME_EMAIL
            }) {
                int columnIndex = cursor.getColumnIndex(key);
                if (columnIndex == -1) {
                    Toast.makeText(MainActivity.this, "Missing column " + key,
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                intent.putExtra(key, cursor.getString(columnIndex));
            }

            int pictureIndex = cursor.getColumnIndex(Contract.ContactsTable.COLUMN_NAME_PICTURE);
            if (pictureIndex == -1) {
                Toast.makeText(MainActivity.this,
                        "Missing column " + Contract.ContactsTable.COLUMN_NAME_PICTURE,
                        Toast.LENGTH_SHORT).show();
                return;
            }

            byte[] pictureBlob = cursor.getBlob(pictureIndex);
            intent.putExtra(Contract.ContactsTable.COLUMN_NAME_PICTURE, pictureBlob);

            int idIndex = cursor.getColumnIndex(Contract.ContactsTable._ID);
            if (idIndex == -1) {
                Toast.makeText(MainActivity.this, "Missing ID", Toast.LENGTH_SHORT).show();
                return;
            }
            long id = cursor.getLong(idIndex);
            intent.putExtra(Contract.ContactsTable._ID, id);

            startActivityForResult(intent, REQUEST_CODE_EDIT_CONTACT);
        }

        @Override
        protected Cursor doInBackground(Long... longs) {
            long id = longs[0];
            return mDatabase.getReadableDatabase().query(Contract.ContactsTable.TABLE_NAME, null,
                    Contract.ContactsTable._ID + " = ?", new String[] { String.valueOf(id)},
                    null, null, null);
        }
    }

    private class RefreshContactsList extends AsyncTask<Void, Void, Cursor> {
        @Override
        protected void onPostExecute(Cursor cursor) {
            if (cursor == null || cursor.getCount() == 0) {
                log("no contacts!");
                mContactsList.setAdapter(null);
            } else {
                if (mContactsAdapter == null) {
                    mContactsAdapter = new ContactsAdapter(MainActivity.this, cursor);
                } else {
                    mContactsAdapter.changeCursor(cursor);
                }
                mContactsList.setAdapter(mContactsAdapter);
            }
        }

        @Override
        protected Cursor doInBackground(Void... voids) {
            String[] projection = new String[] {
                    Contract.ContactsTable._ID,
                    Contract.ContactsTable.COLUMN_NAME_NAME,
                    Contract.ContactsTable.COLUMN_NAME_PICTURE
            };

            return mDatabase.getReadableDatabase().query(Contract.ContactsTable.TABLE_NAME,
                    projection, null, null, null, null,
                    null);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode != RESULT_OK) {
            return;
        }

        switch (requestCode) {
            case REQUEST_CODE_NEW_CONTACT:
            case REQUEST_CODE_EDIT_CONTACT:
                new RefreshContactsList().execute();
                break;
        }
    }
}
