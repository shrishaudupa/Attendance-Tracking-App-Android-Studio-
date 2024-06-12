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
import android.widget.CalendarView;
import android.widget.Spinner;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class YourNewActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private CalendarView calendarView;
    private TextView tvSelectedDate;
    private TextView tvSelectedDateStatus;
    private TextView tvPresentPercentage;
    private TextView tvNumDaysPresent;
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
        setContentView(R.layout.activity_your_new);

        calendarView = findViewById(R.id.calendarView);
        tvSelectedDate = findViewById(R.id.tvSelectedDate);
        tvSelectedDateStatus = findViewById(R.id.tvSelectedDateStatus);
        tvPresentPercentage = findViewById(R.id.tvPresentPercentage);
        tvNumDaysPresent = findViewById(R.id.tvNumDaysPresent);
        attendanceSpinner = findViewById(R.id.attendanceSpinner);

        databaseHelper = new AttendanceDatabaseHelper(this);

        // Set up the spinner adapter
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.attendance_options, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        attendanceSpinner.setAdapter(adapter);
        attendanceSpinner.setOnItemSelectedListener(this);

        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {
                String selectedDate = formatDate(year, month, dayOfMonth);
                tvSelectedDate.setText("Selected Date: " + selectedDate);

                Calendar selectedCalendar = Calendar.getInstance();
                selectedCalendar.set(year, month, dayOfMonth);

                int dayOfWeek = selectedCalendar.get(Calendar.DAY_OF_WEEK);
                boolean isSunday = (dayOfWeek == Calendar.SUNDAY);

                if (isSunday) {
                    tvSelectedDateStatus.setText("Status: Holiday");
                    tvSelectedDateStatus.setTextColor(Color.parseColor("#FFA500")); // Orange color
                    attendanceSpinner.setVisibility(View.GONE);
                } else {
                    int attendanceStatus = getAttendanceStatus(selectedDate);
                    tvSelectedDateStatus.setText("Status: " + getAttendanceStatusText(attendanceStatus));
                    tvSelectedDateStatus.setTextColor(getColorForStatus(attendanceStatus));
                    attendanceSpinner.setVisibility(View.VISIBLE);
                    attendanceSpinner.setSelection(attendanceStatus);
                }

                updatePresentPercentage();
            }
        });

        updatePresentPercentage();
    }

    private void updatePresentPercentage() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);

        // Get the number of days in the month
        int numDaysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);

        // Get the number of absent days in the month
        int numAbsentDays = 0;
        String[] projection = {AttendanceDatabaseHelper.COLUMN_ATTENDANCE_STATUS};
        String selection = AttendanceDatabaseHelper.COLUMN_ATTENDANCE_STATUS + " = ?";
        String[] selectionArgs = {String.valueOf(AttendanceStatus.ABSENT)};

        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        Cursor cursor = db.query(
                AttendanceDatabaseHelper.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null
        );

        numAbsentDays = cursor.getCount();
        cursor.close();
        db.close();

        // Calculate the number of days present excluding absent days
        int numDaysPresent = numDaysInMonth - numAbsentDays;

        // Calculate the present percentage
        double presentPercentage = 0;
        if (numDaysInMonth > 0) {
            presentPercentage = (double) numDaysPresent / numDaysInMonth * 100;
        }

        tvPresentPercentage.setText("Present Percentage: " + String.format(Locale.getDefault(), "%.2f%%", presentPercentage));
        tvNumDaysPresent.setText("Number of Days Present: " + numDaysPresent);
    }


    private int getColorForStatus(int attendanceStatus) {
        switch (attendanceStatus) {
            case AttendanceStatus.PRESENT:
                return Color.GREEN;
            case AttendanceStatus.ABSENT:
                return Color.RED;
            case AttendanceStatus.HALF_DAY:
                return Color.parseColor("#FFA500"); // Orange color
            default:
                return Color.BLACK;
        }
    }

    private String getAttendanceStatusText(int attendanceStatus) {
        switch (attendanceStatus) {
            case AttendanceStatus.PRESENT:
                return "Present";
            case AttendanceStatus.ABSENT:
                return "Absent";
            case AttendanceStatus.HALF_DAY:
                return "Half-day";
            default:
                return "Unknown";
        }
    }

    private int getAttendanceStatus(String date) {
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        String[] projection = {AttendanceDatabaseHelper.COLUMN_ATTENDANCE_STATUS};
        String selection = AttendanceDatabaseHelper.COLUMN_ATTENDANCE_DATE + " = ?";
        String[] selectionArgs = {date};

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

    private void markDate(String date, int attendanceStatus) {
        SQLiteDatabase db = databaseHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(AttendanceDatabaseHelper.COLUMN_ATTENDANCE_DATE, date);
        values.put(AttendanceDatabaseHelper.COLUMN_ATTENDANCE_STATUS, attendanceStatus);

        db.insertWithOnConflict(AttendanceDatabaseHelper.TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_REPLACE);
        db.close();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String selectedDate = tvSelectedDate.getText().toString().substring(15);
        markDate(selectedDate, position);
        tvSelectedDateStatus.setText("Status: " + getAttendanceStatusText(position));
        tvSelectedDateStatus.setTextColor(getColorForStatus(position));
        updatePresentPercentage();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        // Do nothing
    }

    private String formatDate(int year, int month, int dayOfMonth) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, dayOfMonth);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return sdf.format(calendar.getTime());
    }
}






























