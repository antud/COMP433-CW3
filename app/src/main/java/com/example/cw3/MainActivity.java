package com.example.cw3;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.icu.text.SimpleDateFormat;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    SQLiteDatabase db;
    EditText tagField;
    EditText searchField;
    TextView textInfoOne;
    TextView textInfoTwo;
    TextView textInfoThree;
    private ImageView imageViewOne;
    private ImageView imageViewTwo;
    private ImageView imageViewThree;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = this.openOrCreateDatabase("images", Context.MODE_PRIVATE, null);
        db.execSQL("CREATE TABLE IF NOT EXISTS IMAGES (IMAGE BLOB, DATE DATETIME, TAGS TEXT)");

        imageViewOne = findViewById(R.id.top_small_image);
        textInfoOne = findViewById(R.id.top_small_image_text);

        imageViewTwo = findViewById(R.id.mid_small_image);
        textInfoTwo = findViewById(R.id.mid_small_image_text);

        imageViewThree = findViewById(R.id.bot_small_image);
        textInfoThree  = findViewById(R.id.bot_small_image_text);

        tagField = findViewById(R.id.tag_edit_text_box);
        searchField = findViewById(R.id.tag_search_edit_box);
        Cursor c = db.rawQuery("SELECT * FROM IMAGES", null);
        showLatestImages(c);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void saveDrawing(View view) {
        MyDrawingArea mda = findViewById(R.id.drawing_area);

        String tagStrings = tagField.getText().toString();

        LocalDateTime currentDateTime =LocalDateTime.now();

        Bitmap b = mda.getBitmap();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        b.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] ba = stream.toByteArray();
        ContentValues cv = new ContentValues();
        cv.put("IMAGE", ba);
        cv.put("DATE", String.valueOf(currentDateTime));
        cv.put("TAGS", tagStrings);
        db.insert("IMAGES", null, cv);
    }

    public void searchTags(View view) {
        MyDrawingArea mda = findViewById(R.id.drawing_area);
        Bitmap bbb = mda.getBitmap();
        Cursor c;
        String tagText = searchField.getText().toString();
        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS");
        SimpleDateFormat outputFormat = new SimpleDateFormat("MMM d, yyyy h a");

        if (tagText.equals("")) {
            c = db.rawQuery("SELECT * FROM IMAGES", null);
            showLatestImages(c);
        } else {
            try {
                c = db.rawQuery("SELECT * FROM IMAGES WHERE TAGS = '" + tagText + "'", null);
                if (!c.moveToFirst()) {
                    imageViewOne.setImageBitmap(bbb);
                    textInfoOne.setText("unavailable");
                }
                c.moveToFirst();
                byte[] ba = c.getBlob(0);
                String date = c.getString(1);
                String tags = c.getString(2);

                imageViewOne.setImageBitmap((BitmapFactory.decodeByteArray(ba, 0, ba.length)));
                textInfoOne.setText(tags + "\n" + date);

                if (!c.moveToNext()) {
                    imageViewTwo.setImageBitmap(bbb);
                    textInfoTwo.setText("unavailable");
                }
                c.moveToNext();
                ba = c.getBlob(0);
                date = c.getString(1);

                tags = c.getString(2);
                imageViewTwo.setImageBitmap((BitmapFactory.decodeByteArray(ba, 0, ba.length)));
                textInfoTwo.setText(tags + "\n" + date);

                if (!c.moveToNext()) {
                    imageViewThree.setImageBitmap(bbb);
                    textInfoThree.setText("unavailable");
                }
                c.moveToNext();
                ba = c.getBlob(0);
                date = c.getString(1);
                tags = c.getString(2);
                imageViewThree.setImageBitmap((BitmapFactory.decodeByteArray(ba, 0, ba.length)));
                textInfoThree.setText(tags + "\n" + date);
            } catch (CursorIndexOutOfBoundsException e) {
                imageViewTwo.setImageBitmap(bbb);
                textInfoTwo.setText("unavailable");
                imageViewThree.setImageBitmap(bbb);
                textInfoThree.setText("unavailable");
            }
        }
    }

    public void formatDates(String date, SimpleDateFormat input, SimpleDateFormat output) {
        SimpleDateFormat inputFormat = new SimpleDateFormat(date);

        // Create a SimpleDateFormat object for formatting the output datetime
        SimpleDateFormat outputFormat = new SimpleDateFormat("MMM d, yyyy h a");
    }

    public void showLatestImages(Cursor c) {
        c.moveToLast();
        byte[] ba = c.getBlob(0);
        String date = c.getString(1);
        String tags = c.getString(2);

        imageViewOne.setImageBitmap((BitmapFactory.decodeByteArray(ba, 0, ba.length)));
        textInfoOne.setText(tags + "\n" + date);

        c.moveToPrevious();
        ba = c.getBlob(0);
        date = c.getString(1);
        tags = c.getString(2);
        imageViewTwo.setImageBitmap((BitmapFactory.decodeByteArray(ba, 0, ba.length)));
        textInfoTwo.setText(tags + "\n" + date);

        c.moveToPrevious();
        ba = c.getBlob(0);
        date = c.getString(1);
        tags = c.getString(2);
        imageViewThree.setImageBitmap((BitmapFactory.decodeByteArray(ba, 0, ba.length)));
        textInfoThree.setText(tags + "\n" + date);
    }

    public void onClear(View view) {
        MyDrawingArea mda = findViewById(R.id.drawing_area);
        tagField.setText("");
        mda.clear();
    }
}