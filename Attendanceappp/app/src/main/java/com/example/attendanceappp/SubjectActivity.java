package com.example.attendanceappp;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.Locale;

public class SubjectActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private TextView tvSubjectName;
    private Spinner attendanceSpinner;
    private AttendanceDatabaseHelper databaseHelper;

    private static class AttendanceStatus {
        public static final int UNKNOWN = -1;
        public static final int PRESENT = 0;
        public static final int ABSENT = 1;
        public static final int HALF_DAY = 2;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subject);

        // Get subject name from intent
        String subjectName = getIntent().getStringExtra("subjectName");

        tvSubjectName = findViewById(R.id.tvSubjectName);
        attendanceSpinner = findViewById(R.id.attendanceSpinner);

        tvSubjectName.setText(subjectName);

        databaseHelper = new AttendanceDatabaseHelper(this);

        // Set up the spinner adapter
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.attendance_options, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        attendanceSpinner.setAdapter(adapter);
        attendanceSpinner.setOnItemSelectedListener(this);

        // Retrieve and display the initial attendance status for the subject
        int attendanceStatus = getAttendanceStatus(subjectName);
        attendanceSpinner.setSelection(attendanceStatus);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String subjectName = tvSubjectName.getText().toString();
        markAttendance(subjectName, position);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        // Do nothing
    }

    private int getAttendanceStatus(String subjectName) {
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        String[] projection = {AttendanceDatabaseHelper.COLUMN_ATTENDANCE_STATUS};
        String selection = AttendanceDatabaseHelper.COLUMN_SUBJECT_NAME + " = ?";
        String[] selectionArgs = {subjectName};

        Cursor cursor = db.query(
                AttendanceDatabaseHelper.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null
        );

        int attendanceStatus = AttendanceStatus.UNKNOWN;
        if (cursor.moveToFirst()) {
            attendanceStatus = cursor.getInt(cursor.getColumnIndexOrThrow(AttendanceDatabaseHelper.COLUMN_ATTENDANCE_STATUS));
        }

        cursor.close();
        db.close();

        return attendanceStatus;
    }

    private void markAttendance(String subjectName, int attendanceStatus) {
        SQLiteDatabase db = databaseHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(AttendanceDatabaseHelper.COLUMN_SUBJECT_NAME, subjectName);
        values.put(AttendanceDatabaseHelper.COLUMN_ATTENDANCE_STATUS, attendanceStatus);

        db.insertWithOnConflict(AttendanceDatabaseHelper.TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_REPLACE);
        db.close();
    }
}

