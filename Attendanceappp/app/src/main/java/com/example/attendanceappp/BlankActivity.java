package com.example.attendanceappp;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class BlankActivity extends AppCompatActivity {

    private Button btnNext;
    private Button btnDelete;
    private LinearLayout buttonContainer;
    private SharedPreferences sharedPreferences;
    private List<String> savedButtons;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blank);

        btnNext = findViewById(R.id.btnNext);
        btnDelete = findViewById(R.id.btnDelete);
        buttonContainer = findViewById(R.id.buttonContainer);
        sharedPreferences = getSharedPreferences("MyButtons", MODE_PRIVATE);

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddStudentDialog();
            }
        });

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDeleteStudentDialog();
            }
        });

        loadSavedButtons();
    }

    private void showAddStudentDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_add_student, null);
        final EditText editTextStudentName = dialogView.findViewById(R.id.editTextStudentName);

        builder.setView(dialogView)
                .setTitle("Add Student")
                .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String studentName = editTextStudentName.getText().toString().trim();
                        addStudentButton(studentName);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void addStudentButton(String studentName) {
        // Create a unique database name for each student
        String dbName = "student_" + studentName + "_db";

        // Create the database helper for the student
        SubjectDatabaseHelper databaseHelper = new SubjectDatabaseHelper(this, dbName);

        // Start NewActivity and pass the database name
        Intent intent = new Intent(BlankActivity.this, NewActivity.class);
        intent.putExtra("dbName", dbName);
        startActivity(intent);
    }

    private void showDeleteStudentDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Delete Student")
                .setItems(savedButtons.toArray(new String[0]), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String studentName = savedButtons.get(which);
                        deleteStudentButton(studentName);
                    }
                });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void deleteStudentButton(String studentName) {
        // Remove the button from the layout
        for (int i = 0; i < buttonContainer.getChildCount(); i++) {
            View childView = buttonContainer.getChildAt(i);
            if (childView instanceof Button) {
                Button button = (Button) childView;
                if (button.getText().toString().equals(studentName)) {
                    buttonContainer.removeViewAt(i);
                    break;
                }
            }
        }

        // Save the updated button list
        savedButtons.remove(studentName);
        saveButtonList(savedButtons);
    }

    private void saveButtonList(List<String> buttonList) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Set<String> buttonSet = new HashSet<>(buttonList);
        editor.putStringSet("buttons", buttonSet);
        editor.apply();
    }

    private void loadSavedButtons() {
        savedButtons = new ArrayList<>(sharedPreferences.getStringSet("buttons", new HashSet<>()));

        for (String studentName : savedButtons) {
            addStudentButton(studentName);
        }
    }
}

