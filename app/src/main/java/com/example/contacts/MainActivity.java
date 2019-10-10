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

            startActivityForResult(intent, REQUEST_CODE_EDIT_CONTACT);
        }

        @Override
        protected Cursor doInBackground(Long... longs) {
            long id = longs[0];
            return null;
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
            return null;
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
