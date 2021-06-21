package com.example.capstone;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.CalendarView;
import android.widget.DatePicker;
import android.widget.TextView;

import java.util.Calendar;
import java.util.Date;

public class datepicker extends AppCompatActivity {

    TextView disablePastDate, disableFutureDate;
    CalendarView calendar1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_datepicker);

        disablePastDate = findViewById(R.id.pastdate);
        disableFutureDate = findViewById(R.id.futuredate);
        calendar1 = findViewById(R.id.calendarview1);

        calendar1.setFirstDayOfWeek(Calendar.MONDAY);

        Calendar calendar = Calendar.getInstance();

        int yeear = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);



        disablePastDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        datepicker.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        String sDate = dayOfMonth + "/" + month + "/" + year;
                        disablePastDate.setText(sDate);
                    }
                },yeear,month,day
                );
                datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() -1000);
                datePickerDialog.show();
            }
        });

        disableFutureDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        datepicker.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        String sDate = dayOfMonth + "/" + month + "/" + year;
                        disableFutureDate.setText(sDate);
                    }
                },yeear,month,day
                );
                datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
                datePickerDialog.show();
            }
        });
    }
}