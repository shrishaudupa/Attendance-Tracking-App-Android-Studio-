package com.example.attendanceappp;

import androidx.appcompat.app.AppCompatActivity;

import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

public class StudentActivity extends AppCompatActivity {
    private static final String COLUMN_NAME = "student_name";

    private LinearLayout studentContainer;
    private Button addButton;
    private NewStudentData studentData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student);

        studentData = new NewStudentData(this);

        studentContainer = findViewById(R.id.studentContainer);
        addButton = findViewById(R.id.addButton);

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle button click event
                createNewStudentButton();
            }
        });

        loadStudentButtons();
    }

    private void createNewStudentButton() {
        Button newStudentButton = new Button(this);
        newStudentButton.setText("New Student");

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        layoutParams.setMargins(0, 0, 0, 16); // Set margin between buttons

        newStudentButton.setLayoutParams(layoutParams);
        newStudentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle student button click event
                Toast.makeText(StudentActivity.this, "Student button clicked", Toast.LENGTH_SHORT).show();
            }
        });

        studentContainer.addView(newStudentButton);

        // Store the new student in the database
        String studentName = "New Student";
        studentData.insertStudent(studentName);
    }

    private void loadStudentButtons() {
        Cursor cursor = studentData.getAllStudents();
        while (cursor.moveToNext()) {
            String studentName = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME));
            Button studentButton = new Button(this);
            studentButton.setText(studentName);

            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            layoutParams.setMargins(0, 0, 0, 16); // Set margin between buttons

            studentButton.setLayoutParams(layoutParams);
            studentButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Handle student button click event
                    Toast.makeText(StudentActivity.this, "Student button clicked: " + studentName, Toast.LENGTH_SHORT).show();
                }
            });

            studentContainer.addView(studentButton);
        }
        cursor.close();
    }
}









