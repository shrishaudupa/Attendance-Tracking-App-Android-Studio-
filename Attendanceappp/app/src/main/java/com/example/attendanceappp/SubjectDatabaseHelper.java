package com.example.attendanceappp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class SubjectDatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "subject.db";
    private static final int DATABASE_VERSION = 1;

    public static final String TABLE_NAME = "subjects";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_SUBJECT_NAME = "subject_name";

    private static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_SUBJECT_NAME + " TEXT)";

    public SubjectDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Handle database upgrades if needed
    }

    public long insertSubject(String subjectName) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_SUBJECT_NAME, subjectName);

        long id = db.insert(TABLE_NAME, null, values);
        db.close();

        return id;
    }

    public List<String> getAllSubjects() {
        List<String> subjects = new ArrayList<>();

        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLE_NAME, null, null, null, null, null, null);

        if (cursor != null) {
            while (cursor.moveToNext()) {
                String subjectName = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_SUBJECT_NAME));
                subjects.add(subjectName);
            }
            cursor.close();
        }

        db.close();

        return subjects;
    }

    public void deleteSubject(String subjectName) {
        SQLiteDatabase db = getWritableDatabase();

        String selection = COLUMN_SUBJECT_NAME + " = ?";
        String[] selectionArgs = {subjectName};

        db.delete(TABLE_NAME, selection, selectionArgs);
        db.close();
    }
}







