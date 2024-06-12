package com.example.attendanceappp;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.content.Intent;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class NewActivity extends AppCompatActivity {
    private FloatingActionButton fabAddSubject;
    private List<String> subjectList;
    private LinearLayout subjectLayout;
    private SubjectDatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new);

        fabAddSubject = findViewById(R.id.fabAddSubject);
        subjectLayout = findViewById(R.id.subjectLayout);
        subjectList = new ArrayList<>();
        databaseHelper = new SubjectDatabaseHelper(this);

        fabAddSubject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddSubjectDialog();
            }
        });

        loadSubjectsFromDatabase();
    }

    private void showAddSubjectDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_add_subject, null);
        builder.setView(dialogView);
        builder.setTitle("Add Subject");

        final EditText etSubjectName = dialogView.findViewById(R.id.etSubjectName);

        builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String subjectName = etSubjectName.getText().toString().trim();

                if (TextUtils.isEmpty(subjectName)) {
                    Toast.makeText(NewActivity.this, "Please enter a subject name", Toast.LENGTH_SHORT).show();
                } else {
                    addSubjectToDatabase(subjectName);
                }
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void addSubjectToDatabase(String subjectName) {
        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(SubjectDatabaseHelper.COLUMN_SUBJECT_NAME, subjectName);

        long id = db.insert(SubjectDatabaseHelper.TABLE_NAME, null, values);
        if (id != -1) {
            Toast.makeText(this, "Subject added: " + subjectName, Toast.LENGTH_SHORT).show();
            loadSubjectsFromDatabase(); // Refresh subject list
        } else {
            Toast.makeText(this, "Failed to add subject", Toast.LENGTH_SHORT).show();
        }

        db.close();
    }

    private void loadSubjectsFromDatabase() {
        subjectLayout.removeAllViews();
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        String[] projection = {SubjectDatabaseHelper.COLUMN_SUBJECT_NAME};
        Cursor cursor = db.query(SubjectDatabaseHelper.TABLE_NAME, projection, null, null, null, null, null);

        while (cursor.moveToNext()) {
            String subjectName = cursor.getString(cursor.getColumnIndexOrThrow(SubjectDatabaseHelper.COLUMN_SUBJECT_NAME));
            addSubjectButton(subjectName);
        }

        cursor.close();
        db.close();
    }

    private void addSubjectButton(String subjectName) {
        Button subjectButton = new Button(this);
        subjectButton.setText(subjectName);
        subjectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle subject button click
                Intent intent = new Intent(NewActivity.this, YourNewActivity.class);
                intent.putExtra("subjectName", subjectName);
                startActivity(intent);
            }
        });

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        layoutParams.setMargins(0, 0, 0, getResources().getDimensionPixelSize(R.dimen.subject_button_margin_bottom));
        subjectButton.setLayoutParams(layoutParams);

        subjectLayout.addView(subjectButton);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Close the database connection
        if (databaseHelper != null) {
            databaseHelper.close();
        }
    }
}








