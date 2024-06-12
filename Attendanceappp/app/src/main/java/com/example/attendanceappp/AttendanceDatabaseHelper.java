package com.example.attendanceappp;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class AttendanceDatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "attendance.db";
    private static final int DATABASE_VERSION = 1;
    public static final String TABLE_NAME = "subjects";
    public static final String COLUMN_SUBJECT_NAME = "subject_name";
    public static final String COLUMN_ATTENDANCE_DATE = "attendance_date";
    public static final String COLUMN_ATTENDANCE_STATUS = "attendance_status";

    private static final String SQL_CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + " (" +
            COLUMN_SUBJECT_NAME + " TEXT, " +
            COLUMN_ATTENDANCE_DATE + " TEXT PRIMARY KEY, " +
            COLUMN_ATTENDANCE_STATUS + " INTEGER)";

    public AttendanceDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Handle database upgrade
        if (oldVersion < newVersion) {
            // Perform necessary operations such as altering table structure or migrating data
        }
    }
}
