package com.example.attendanceappp;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class AttendanceAdapter extends RecyclerView.Adapter<AttendanceAdapter.AttendanceViewHolder> {
    private List<String> dates;
    private Context context;

    public AttendanceAdapter(List<String> dates, Context context) {
        this.dates = dates;
        this.context = context;
    }

    @NonNull
    @Override
    public AttendanceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_date, parent, false);
        return new AttendanceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AttendanceViewHolder holder, int position) {
        String date = dates.get(position);
        holder.tvDate.setText(date);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(context, R.array.attendance_options, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        holder.spinner.setAdapter(adapter);

        holder.spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                String selectedOption = parent.getItemAtPosition(pos).toString();
                // Update the attendance status for the date
                updateAttendanceStatus(date, selectedOption);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });
    }

    @Override
    public int getItemCount() {
        return dates.size();
    }

    public static class AttendanceViewHolder extends RecyclerView.ViewHolder {
        TextView tvDate;
        Spinner spinner;

        public AttendanceViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDate = itemView.findViewById(R.id.tvDate);
            spinner = itemView.findViewById(R.id.spinnerAttendance);
        }
    }

    private void updateAttendanceStatus(String date, String status) {
        // Perform any necessary operations to update the attendance status for the date
        // You can store the status in a database, update UI, etc.
    }
}

