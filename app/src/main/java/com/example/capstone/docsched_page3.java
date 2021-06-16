package com.example.capstone;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class docsched_page3 extends AppCompatActivity {
    TextView starttime, endtime  ;
    EditText maxbookingtv, pricetv;
    int t1Hour, t1Minute, t2Hour, t2Minute;
    Button monbtn, tuebtn, wedbtn, thubtn , fribtn, satbtn, sunbtn , cancelbtn, savebtn;
    boolean monstat = false, tuestat = false, wedstat = false, thustat = false, fristat = false, satstat = false, sunstat = false;
    private FirebaseFirestore db;
    String docname, docid , type , documentid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doc_sched_page3);


        db = FirebaseFirestore.getInstance();
        starttime = findViewById(R.id.docsched_starttime);
        endtime = findViewById(R.id.docsched_endtime);
        maxbookingtv = (EditText) findViewById(R.id.docsched_maxbooking);
        pricetv = (EditText) findViewById(R.id.docsched_price);
        monbtn = (Button) findViewById(R.id.docsched_monday);
        tuebtn = (Button) findViewById(R.id.docsched_tuesday);
        wedbtn = (Button) findViewById(R.id.docsched_wednesday);
        thubtn = (Button) findViewById(R.id.docsched_thursday);
        fribtn = (Button) findViewById(R.id.docsched_friday);
        satbtn = (Button) findViewById(R.id.docsched_saturday);
        sunbtn = (Button) findViewById(R.id.docsched_sunday);
        cancelbtn = (Button) findViewById(R.id.docsched_cancel);
        savebtn = (Button) findViewById(R.id.docsched_save);

        Intent intent = getIntent();
        docname = intent.getStringExtra("docname");
        docid = intent.getStringExtra("docid");
        type = intent.getStringExtra("type");
        documentid = intent.getStringExtra("Documentid");


        monbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!monstat) {
                    monbtn.setBackgroundResource(R.drawable.daybg);
                    monstat = true ;
                }else  {
                    monbtn.setBackgroundResource(R.drawable.daybg2);
                    monstat = false;
                }
            }
        });
        tuebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!tuestat) {
                    tuebtn.setBackgroundResource(R.drawable.daybg);
                    tuestat = true ;
                }else  {
                    tuebtn.setBackgroundResource(R.drawable.daybg2);
                    tuestat = false;
                }
            }
        });
        wedbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!wedstat) {
                    wedbtn.setBackgroundResource(R.drawable.daybg);
                    wedstat = true ;
                }else  {
                    wedbtn.setBackgroundResource(R.drawable.daybg2);
                    wedstat = false;
                }
            }
        });
        thubtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!thustat) {
                    thubtn.setBackgroundResource(R.drawable.daybg);
                    thustat = true ;
                }else  {
                    thubtn.setBackgroundResource(R.drawable.daybg2);
                    thustat = false;
                }
            }
        });
        fribtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!fristat) {
                    fribtn.setBackgroundResource(R.drawable.daybg);
                    fristat = true ;
                }else  {
                    fribtn.setBackgroundResource(R.drawable.daybg2);
                    fristat = false;
                }
            }
        });
        satbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!satstat) {
                    satbtn.setBackgroundResource(R.drawable.daybg);
                    satstat = true ;
                }else  {
                    satbtn.setBackgroundResource(R.drawable.daybg2);
                    satstat = false;
                }
            }
        });
        sunbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!sunstat) {
                    sunbtn.setBackgroundResource(R.drawable.daybg);
                    sunstat = true ;
                }else  {
                    sunbtn.setBackgroundResource(R.drawable.daybg2);
                    sunstat = false;
                }
            }
        });

        savebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            if (type.equals("Add")){
                String maxbooking = maxbookingtv.getText().toString();
                String price = pricetv.getText().toString();

                Map<String, Object> DocSched = new HashMap<>();
                DocSched.put("DocId", docid);
                DocSched.put("StartTime", starttime.getText());
                DocSched.put("EndTime", endtime.getText());
                DocSched.put("MaximumBooking", maxbooking );
                DocSched.put("Price", price);
                DocSched.put("Monday", monstat);
                DocSched.put("Tuesday", tuestat);
                DocSched.put("Wednesday", wedstat);
                DocSched.put("Thursday", thustat);
                DocSched.put("Friday", fristat);
                DocSched.put("Saturday", satstat);
                DocSched.put("Sunday", sunstat);

                db.collection("DoctorSchedules").document().set(DocSched)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d("TAG", "DocumentSnapshot successfully written!");
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w("TAG", "Error writing document", e);
                            }
                        });
            }

            else {
                String maxbooking = maxbookingtv.getText().toString();
                String price = pricetv.getText().toString();

                Map<String, Object> DocSched = new HashMap<>();
                DocSched.put("DocId", docid);
                DocSched.put("StartTime", starttime.getText());
                DocSched.put("EndTime", endtime.getText());
                DocSched.put("MaximumBooking", maxbooking );
                DocSched.put("Price", price);
                DocSched.put("Monday", monstat);
                DocSched.put("Tuesday", tuestat);
                DocSched.put("Wednesday", wedstat);
                DocSched.put("Thursday", thustat);
                DocSched.put("Friday", fristat);
                DocSched.put("Saturday", satstat);
                DocSched.put("Sunday", sunstat);

                db.collection("DoctorSchedules").document(documentid).update(DocSched)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d("TAG", "DocumentSnapshot successfully written!");
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w("TAG", "Error writing document", e);
                            }
                        });


            }

            }
        });


        starttime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog timePickerDialog = new TimePickerDialog(
                        docsched_page3.this,
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                t1Hour = hourOfDay;
                                t1Minute = minute;

                                String time = t1Hour + ":" + t1Minute;
                                SimpleDateFormat f24hours = new SimpleDateFormat(
                                        "HH:mm"
                                );
                                try {
                                    Date date = f24hours.parse(time);
                                    SimpleDateFormat f12hours = new SimpleDateFormat(
                                            "hh:mm aa"
                                    );
                                    starttime.setText(f12hours.format(date));
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                            }
                        },12,0,false
                );
                timePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                timePickerDialog.updateTime(t1Hour,t1Minute);
                timePickerDialog.show();
            }
        });

        endtime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog timePickerDialog = new TimePickerDialog(
                        docsched_page3.this,
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                t2Hour = hourOfDay;
                                t2Minute = minute;

                                String time = t2Hour + ":" + t2Minute;
                                SimpleDateFormat f24hours = new SimpleDateFormat(
                                        "HH:mm"
                                );
                                try {
                                    Date date = f24hours.parse(time);
                                    SimpleDateFormat f12hours = new SimpleDateFormat(
                                            "hh:mm aa"
                                    );
                                    endtime.setText(f12hours.format(date));
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                            }
                        },12,0,false
                );
                timePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                timePickerDialog.updateTime(t2Hour,t2Minute);
                timePickerDialog.show();
            }
        });

    }
}