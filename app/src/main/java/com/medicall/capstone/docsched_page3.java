package com.medicall.capstone;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.medicall.capstone.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class docsched_page3 extends AppCompatActivity {
    TextView starttime, endtime  ;
    EditText maxbookingtv;
    Spinner pricetv;
    int t1Hour, t1Minute, t2Hour, t2Minute;
    Button monbtn, tuebtn, wedbtn, thubtn , fribtn, satbtn, sunbtn , cancelbtn, savebtn;
    boolean monstat = false, tuestat = false, wedstat = false, thustat = false, fristat = false, satstat = false, sunstat = false;
    private FirebaseFirestore db;
    String docname, docid , type , documentid;
    Context mContext = this;
    Boolean conflict = false;
    ImageView back;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doc_sched_page3);

        db = FirebaseFirestore.getInstance();
        starttime = findViewById(R.id.docsched_starttime);
        endtime = findViewById(R.id.docsched_endtime);
        maxbookingtv = (EditText) findViewById(R.id.docsched_maxbooking);
        pricetv = (Spinner) findViewById(R.id.docsched_price);
        monbtn = (Button) findViewById(R.id.docsched_monday);
        tuebtn = (Button) findViewById(R.id.docsched_tuesday);
        wedbtn = (Button) findViewById(R.id.docsched_wednesday);
        thubtn = (Button) findViewById(R.id.docsched_thursday);
        fribtn = (Button) findViewById(R.id.docsched_friday);
        satbtn = (Button) findViewById(R.id.docsched_saturday);
        sunbtn = (Button) findViewById(R.id.docsched_sunday);
        cancelbtn = (Button) findViewById(R.id.docsched_cancel);
        savebtn = (Button) findViewById(R.id.docsched_save);

        back = findViewById(R.id.backspace);

        ArrayAdapter<CharSequence> arrayAdapter = ArrayAdapter.createFromResource(this,
                R.array.Price, android.R.layout.simple_spinner_item);

        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        pricetv.setAdapter(arrayAdapter);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), docshed_page1.class);
                startActivity(intent);
            }
        });

        Intent intent = getIntent();
        docname = intent.getStringExtra("docname");
        docid = intent.getStringExtra("docid");
        type = intent.getStringExtra("type");
        documentid = intent.getStringExtra("Documentid");
        starttime.setText(intent.getStringExtra("StartTime"));
        endtime.setText(intent.getStringExtra("EndTime"));
        maxbookingtv.setText(intent.getStringExtra("MaximumBooking"));



        if (type.equals("Update")) {
            Intent intent1 = getIntent();



if (type.equals("Update")){
    boolean monday = intent1.getBooleanExtra("Monday", false);
    boolean tuesday = intent1.getBooleanExtra("Tuesday", false);
    boolean wednesday = intent1.getBooleanExtra("Wednesday", false);
    boolean thursday = intent1.getBooleanExtra("Thursday", false);
    boolean friday = intent1.getBooleanExtra("Friday", false);
    boolean saturday = intent1.getBooleanExtra("Saturday", false);
    boolean sunday = intent1.getBooleanExtra("Sunday", false);

if (monday){
    monbtn.setBackgroundResource(R.drawable.daybg);
    monstat = true ;
} if (tuesday) {
    tuebtn.setBackgroundResource(R.drawable.daybg);
    tuestat = true ;
} if (wednesday) {
    wedbtn.setBackgroundResource(R.drawable.daybg);
    wedstat = true ;
} if (thursday) {
    thubtn.setBackgroundResource(R.drawable.daybg);
    thustat = true ;
} if (friday) {
    fribtn.setBackgroundResource(R.drawable.daybg);
    fristat = true ;
} if (saturday) {
    satbtn.setBackgroundResource(R.drawable.daybg);
    satstat = true ;
} if (sunday) {
    sunbtn.setBackgroundResource(R.drawable.daybg);
    sunstat = true ;
            }
        }}

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

        cancelbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), docsched_page2.class);
                intent.putExtra("docid", docid);
                intent.putExtra("docname", docname);
                startActivity(intent);
            }
        });

        savebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                conflict =false;
                String stringnewstart;
                String stringnewend;
                Date datenewstart = new Date();
                Date datenewend = new Date();
                stringnewstart = starttime.getText().toString();
                stringnewend = endtime.getText().toString();

                if (monstat == false && tuestat == false && wedstat == false && thustat == false && fristat == false && satstat == false && sunstat == false) {
                    Toast.makeText(docsched_page3.this, "Please select day/s of week", Toast.LENGTH_SHORT).show();
                } else if(maxbookingtv.getText().toString().equals("") || pricetv.getSelectedItem().toString().equals("") || stringnewend.equals("")|| stringnewend.equals("")){
                    Toast.makeText(docsched_page3.this, "Please fill all necessary fields", Toast.LENGTH_SHORT).show();
                }
                    else {


                    SimpleDateFormat f24hours = new SimpleDateFormat("hh:mm aa");
                    try {
                        datenewstart = f24hours.parse(stringnewstart);
                        datenewend = f24hours.parse(stringnewend);
                    } catch (ParseException e) {
                        Toast.makeText(docsched_page3.this, "Error converting new time ", Toast.LENGTH_SHORT).show();
                    }
                    if (datenewstart.after(datenewend)) {
                        Toast.makeText(docsched_page3.this, "The End Time should not be before the Start Time", Toast.LENGTH_SHORT).show();
                    } else {

                        if (type.equals("Add")) {
                            Date finalDatenewstart = datenewstart;
                            Date finalDatenewend = datenewend;
                            Date finalDatenewend1 = datenewend;
                            db.collection("DoctorSchedules").whereEqualTo("DocId", docid).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful()) {
                                        if (task.getResult().isEmpty()) {

                                            addsched(conflict);
                                        } else {
                                            for (QueryDocumentSnapshot doc : task.getResult()) {
                                                String stringstart;
                                                String stringend;
                                                Boolean same = false;
                                                if (monstat == true && doc.getBoolean("Monday") == true) {
                                                    same = true;
                                                }
                                                if (tuestat == true && doc.getBoolean("Tuesday") == true) {
                                                    same = true;
                                                }
                                                if (wedstat == true && doc.getBoolean("Wednesday") == true) {
                                                    same = true;
                                                }
                                                if (thustat == true && doc.getBoolean("Thursday") == true) {
                                                    same = true;
                                                }
                                                if (fristat == true && doc.getBoolean("Friday") == true) {
                                                    same = true;
                                                }
                                                if (satstat == true && doc.getBoolean("Saturday") == true) {
                                                    same = true;
                                                }
                                                if (sunstat == true && doc.getBoolean("Sunday") == true) {
                                                    same = true;
                                                }
                                                if (same) {
                                                    Date dateendtime = new Date();
                                                    Date datestarttime = new Date();


                                                    stringstart = doc.getString("StartTime");
                                                    stringend = doc.getString("EndTime");


                                                    SimpleDateFormat f24hours = new SimpleDateFormat("hh:mm aa");
                                                    try {
                                                        dateendtime = f24hours.parse(stringend);
                                                        datestarttime = f24hours.parse(stringstart);
                                                    } catch (ParseException e) {
                                                        Toast.makeText(docsched_page3.this, "Error converting time", Toast.LENGTH_SHORT).show();
                                                    }

                                                    if (finalDatenewstart.after(datestarttime) && finalDatenewstart.before(dateendtime) || finalDatenewstart.equals(datestarttime) || finalDatenewstart.equals(dateendtime)) {
                                                        conflict = true;

                                                    } else {
                                                        if (finalDatenewend.after(datestarttime) && finalDatenewend.before(dateendtime) || finalDatenewend.equals(datestarttime) || finalDatenewend.equals(dateendtime)) {
                                                            conflict = true;

                                                        } else {
                                                            if (finalDatenewend.after(datestarttime) && finalDatenewstart.before(datestarttime)){
                                                                conflict = true;
                                                            }

                                                        }

                                                    }
                                                } else {

                                                }
                                            }
                                            addsched(conflict);

                                        }
                                    }
                                }

                            });


                        } else {
                            Date finalDatenewstart = datenewstart;
                            Date finalDatenewend = datenewend;
                            Date finalDatenewend1 = datenewend;
                            db.collection("DoctorSchedules").whereEqualTo("DocId", docid).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful()) {
                                        if (task.getResult().isEmpty()) {
                                            updatesched(conflict);
                                        } else {
                                            for (QueryDocumentSnapshot doc : task.getResult()) {
                                                if (!documentid.equals(doc.getId())){
                                                String stringstart;
                                                String stringend;
                                                Boolean same = false;
                                                if (monstat == true && doc.getBoolean("Monday") == true) {
                                                    same = true;
                                                }
                                                if (tuestat == true && doc.getBoolean("Tuesday") == true) {
                                                    same = true;
                                                }
                                                if (wedstat == true && doc.getBoolean("Wednesday") == true) {
                                                    same = true;
                                                }
                                                if (thustat == true && doc.getBoolean("Thursday") == true) {
                                                    same = true;
                                                }
                                                if (fristat == true && doc.getBoolean("Friday") == true) {
                                                    same = true;
                                                }
                                                if (satstat == true && doc.getBoolean("Saturday") == true) {
                                                    same = true;
                                                }
                                                if (sunstat == true && doc.getBoolean("Sunday") == true) {
                                                    same = true;
                                                }
                                                if (same) {
                                                    Date dateendtime = new Date();
                                                    Date datestarttime = new Date();


                                                    stringstart = doc.getString("StartTime");
                                                    stringend = doc.getString("EndTime");


                                                    SimpleDateFormat f24hours = new SimpleDateFormat("hh:mm aa");
                                                    try {
                                                        dateendtime = f24hours.parse(stringend);
                                                        datestarttime = f24hours.parse(stringstart);
                                                    } catch (ParseException e) {
                                                        Toast.makeText(docsched_page3.this, "Error converting time", Toast.LENGTH_SHORT).show();
                                                    }

                                                    if (finalDatenewstart.after(datestarttime) && finalDatenewstart.before(dateendtime) || finalDatenewstart.equals(datestarttime) || finalDatenewstart.equals(dateendtime)) {
                                                        conflict = true;

                                                    } else {
                                                        if (finalDatenewend.after(datestarttime) && finalDatenewend.before(dateendtime) || finalDatenewend.equals(datestarttime) || finalDatenewend.equals(dateendtime)) {
                                                            conflict = true;

                                                        } else {
                                                            if (finalDatenewend.after(datestarttime) && finalDatenewstart.before(datestarttime)){
                                                            conflict = true;
                                                        }

                                                        }

                                                    }
                                                } else {

                                                }
                                            }}
                                            updatesched(conflict);

                                        }
                                    }
                                }

                            });



                        }

                    }
                }
            }   });


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

    public void addsched (Boolean conflict){

        if (conflict ){
            Toast.makeText(this, "There has been a conflict between schedules. Please check and try again.", Toast.LENGTH_SHORT).show();
        }else {
            String maxbooking = maxbookingtv.getText().toString();
            String price = pricetv.getSelectedItem().toString();
            Intent intent = getIntent();

            if (price.equals("₱150")) {
               price = "appointment_150";
            } else if (price.equals("₱200")) {
                price = "appointment_200";
            } else if (price.equals("₱250")) {
                price = "appointment_250";
            } else if (price.equals("₱300")) {
                price = "appointment_300";
            }



            Map<String, Object> DocSched = new HashMap<>();
            DocSched.put("DocId", docid);
            DocSched.put("StartTime", starttime.getText());
            DocSched.put("EndTime", endtime.getText());
            DocSched.put("MaximumBooking", maxbooking);
            DocSched.put("Price", price);
            DocSched.put("Monday", monstat);
            DocSched.put("Tuesday", tuestat);
            DocSched.put("Wednesday", wedstat);
            DocSched.put("Thursday", thustat);
            DocSched.put("Friday", fristat);
            DocSched.put("Saturday", satstat);
            DocSched.put("Sunday", sunstat);
            DocSched.put("InActive", true);

            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
            builder.setCancelable(true);
            builder.setTitle("Add to Schedule");
            builder.setMessage("Do you want to add this schedule?");
            builder.setPositiveButton("Confirm",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

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
                            Intent intent = new Intent(getApplicationContext(), docsched_page2.class);
                            intent.putExtra("docid", docid);
                            intent.putExtra("docname", docname);
                            startActivity(intent);
                        }

                    });
            builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                }
            });

            AlertDialog dialog = builder.create();
            dialog.show();
        }
    }

    public void updatesched (Boolean conflict){

        if (conflict ){
            Toast.makeText(this, "There has been a conflict between schedules. Please check and try again.", Toast.LENGTH_SHORT).show();
        }else {
            String maxbooking = maxbookingtv.getText().toString();
            String price = pricetv.getSelectedItem().toString();

            Map<String, Object> DocSched = new HashMap<>();
            DocSched.put("DocId", docid);
            DocSched.put("StartTime", starttime.getText());
            DocSched.put("EndTime", endtime.getText());
            DocSched.put("MaximumBooking", maxbooking);
            DocSched.put("Price", price);
            DocSched.put("Monday", monstat);
            DocSched.put("Tuesday", tuestat);
            DocSched.put("Wednesday", wedstat);
            DocSched.put("Thursday", thustat);
            DocSched.put("Friday", fristat);
            DocSched.put("Saturday", satstat);
            DocSched.put("Sunday", sunstat);

            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
            builder.setCancelable(true);
            builder.setTitle("Update Schedule");
            builder.setMessage("Confirm update of schedule?");
            builder.setPositiveButton("Confirm",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

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

                            Intent intent = new Intent(getApplicationContext(), docsched_page2.class);
                            intent.putExtra("docid", docid);
                            intent.putExtra("docname", docname);
                            startActivity(intent);
                        }

                    });
            builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                }
            });

            AlertDialog dialog = builder.create();
            dialog.show();
        }
    }
}