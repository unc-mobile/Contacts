package com.example.contacts;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class EditContactActivity extends AppCompatActivity {
    private EditText mName;
    private EditText mPhoneNumber;
    private EditText mEmail;
    private ImageView mPictureView;
    private Bitmap mPicture;

    private Button mSave;
    private Button mDelete;

    private DbHelper mDatabase;

    private boolean mEditingExisting;
    private long mIdToModify;

    private static final long NOT_EDITING = -1;

    private static final int REQUEST_CODE_PICTURE = 1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.contact);

        mName = findViewById(R.id.name_field);
        mPhoneNumber = findViewById(R.id.phone_number_field);
        mEmail = findViewById(R.id.email_field);
        mPictureView = findViewById(R.id.imageView);

        mSave = findViewById(R.id.save);
        mDelete = findViewById(R.id.delete);

        mDatabase = new DbHelper(this);

        mPicture = null;
        mEditingExisting = false;
        mIdToModify = NOT_EDITING;

        Intent intent = getIntent();
        if (intent != null) {
            String title = intent.getStringExtra(Intent.EXTRA_TITLE);
            setTitle(title);

            String name = intent.getStringExtra(Contract.ContactsTable.COLUMN_NAME_NAME);
            mName.setText(name);

            mIdToModify = intent.getLongExtra(Contract.ContactsTable._ID, NOT_EDITING);
            if (mIdToModify != NOT_EDITING) {
                mEditingExisting = true;
            }

            String phone = intent.getStringExtra(Contract.ContactsTable.COLUMN_NAME_PHONE);
            mPhoneNumber.setText(phone);

            String email = intent.getStringExtra(Contract.ContactsTable.COLUMN_NAME_EMAIL);
            mEmail.setText(email);

            byte[] pictureBlob = intent.getByteArrayExtra(Contract.ContactsTable.COLUMN_NAME_PICTURE);
            if (pictureBlob != null) {
                mPicture = BitmapFactory.decodeByteArray(pictureBlob, 0, pictureBlob.length);
                if (mPicture != null) {
                    mPictureView.setImageBitmap(mPicture);
                }
            }
        }
    }

    private static String getText(EditText editText) {
        Editable editable = editText.getText();
        if (editable == null || editable.length() == 0) {
            return null;
        }
        return editable.toString();
    }

    public void onClick(View view) {
        if (view == mSave) {
            String name = getText(mName);
            if (name == null) {
                Toast.makeText(this, "Name field is required!", Toast.LENGTH_SHORT).show();
                return;
            }

            new Saver(name, getText(mPhoneNumber), getText(mEmail), mPicture).execute();
        } else if (view == mPictureView) {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(intent, REQUEST_CODE_PICTURE);
        } else if (view == mDelete) {
            if (mEditingExisting) {
                new Deleter().execute();
            } else {
                finish();
            }
        }
    }

    private class Saver extends AsyncTask<Void, Void, Boolean> {
        private final String mName;
        private final String mPhone;
        private final String mEmail;
        private final Bitmap mPicture;

        public Saver(String name, String phone, String email, Bitmap picture) {
            mName = name;
            mPhone = phone;
            mEmail = email;
            mPicture = picture;
        }

        @Override
        protected void onPostExecute(Boolean success) {
            if (success) {
                Toast.makeText(EditContactActivity.this, mName + " saved", Toast.LENGTH_SHORT).show();
                setResult(RESULT_OK);
                finish();
            } else {
                Toast.makeText(EditContactActivity.this, "Failed to insert/update " + mName,
                        Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
        }
    }

    private class Deleter extends AsyncTask<Void, Void, Boolean> {
        @Override
        protected void onPostExecute(Boolean success) {
            if (success) {
                Toast.makeText(EditContactActivity.this, "Deleted " + getText(mName),
                        Toast.LENGTH_SHORT).show();
                setResult(RESULT_OK);
            }
            finish();
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode != RESULT_OK) {
            return;
        }

        switch (requestCode) {
            case REQUEST_CODE_PICTURE:
                if (data != null) {
                    Bundle extras = data.getExtras();
                    if (extras != null) {
                        Bitmap picture = (Bitmap) extras.get("data");
                        if (picture != null) {
                            mPicture = picture;
                            mPictureView.setImageBitmap(mPicture);
                        }
                    }
                }
        }
    }
}
