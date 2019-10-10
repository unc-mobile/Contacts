package com.example.contacts;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ContactsAdapter extends CursorAdapter {
    public ContactsAdapter(Context context, Cursor cursor) {
        super(context, cursor, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        return inflater.inflate(R.layout.contact_item, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        int nameIndex = cursor.getColumnIndex(Contract.ContactsTable.COLUMN_NAME_NAME);
        TextView textView = view.findViewById(R.id.name);
        if (nameIndex == -1) {
            MainActivity.log("Missing name from Cursor!");
            textView.setText("");
        } else {
            String name = cursor.getString(nameIndex);
            textView.setText(name);
        }

        int pictureIndex = cursor.getColumnIndex(Contract.ContactsTable.COLUMN_NAME_PICTURE);
        ImageView imageView = view.findViewById(R.id.picture);
        if (pictureIndex == -1) {
            MainActivity.log("Missing picture from Cursor!");
        } else {
            byte[] blob = cursor.getBlob(pictureIndex);
            if (blob != null) {
                Bitmap picture = BitmapFactory.decodeByteArray(blob, 0, blob.length);
                imageView.setImageBitmap(picture);
            } else {
                imageView.setImageResource(R.mipmap.ic_launcher_round);
            }
        }
    }
}
