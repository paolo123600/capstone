package com.example.capstone;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import com.example.capstone.utilities.Constants;
import com.example.capstone.utilities.PreferenceManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


public class patient_schedule extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {
    TextView docnametv, clinicnametv, statustv, datetv, timetv;
    Button cancelbtn, reschedbtn;
    private PreferenceManager preferenceManager;
    private AlertDialog.Builder dialogbuilder;
    String Patuid ="";
    String datenow;
    Date nowdate= new Date();
    FirebaseFirestore db;
    String docid;
    String scheddocu;

    private Dialog dialog;
    String doclastname;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_schedule);

        docnametv=(TextView) findViewById(R.id.tvdocname);
       clinicnametv=(TextView) findViewById(R.id.tvclinicname);
        statustv=(TextView) findViewById(R.id.tvstatus);
        datetv=(TextView) findViewById(R.id.tvdate);
        timetv=(TextView) findViewById(R.id.tvtime);
        cancelbtn=(Button)findViewById(R.id.btncancel);
        reschedbtn= (Button) findViewById(R.id.btnresched);
        preferenceManager = new PreferenceManager(getApplicationContext());
        Patuid = preferenceManager.getString(Constants.KEY_USER_ID);
        db = FirebaseFirestore.getInstance();
        GlobalVariables gv = (GlobalVariables) getApplicationContext();
        Calendar calendar = Calendar.getInstance();
        datenow = DateFormat.getDateInstance().format(calendar.getTime());

        SimpleDateFormat format = new SimpleDateFormat("MMM d,yyyy");
        format.setLenient(false);
        try {
            nowdate = format.parse(datenow);
        } catch (ParseException e) {
            Toast.makeText(patient_schedule.this, "error1", Toast.LENGTH_SHORT).show();
        }

        db.collection("Schedule").whereEqualTo("PatientUId",Patuid).whereEqualTo("Status","Paid").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()){

                        for (QueryDocumentSnapshot doc : task.getResult()) {
                            String scheddate = doc.getString("SchedDate");
                                Date datesched = new Date();

                            try {
                               datesched  = format.parse(scheddate);
                            } catch (ParseException e) {
                                Toast.makeText(patient_schedule.this, "error1", Toast.LENGTH_SHORT).show();
                            }
                                if(nowdate.before(datesched)){
                                    cancelbtn.setVisibility(View.VISIBLE);
                                    reschedbtn.setVisibility(View.VISIBLE);
                                    scheddocu=doc.getId();
                                    docid=doc.getString("DoctorUId");
                                    db.collection("Doctors").document(docid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                            if (task.isSuccessful()){
                                                DocumentSnapshot document = task.getResult();
                                                if (document.exists()) {
                                                    docnametv.setText("Doc. "+document.getString("LastName"));
                                                    doclastname=document.getString("LastName");
                                                } else {
                                                    Toast.makeText(patient_schedule.this, "does not exits", Toast.LENGTH_SHORT).show();
                                                }

                                            }
                                        }
                                    });
                                    clinicnametv.setText(doc.getString("ClinicName"));
                                    statustv.setText("Status: "+doc.getString("Status"));
                                    timetv.setText("Time :"+doc.getString("TimeStart")+"-"+doc.getString("TimeStart"));
                                    datetv.setText("Date :"+doc.getString("SchedDate"));
                                    gv.setSDDate(doc.getString("SchedDate"));
                                    gv.setSDClinic(doc.getString("ClinicName"));
                                    gv.setSDDocUid(doc.getString("DoctorUId"));

                                }


                            }


                        }


                    }
            });

        cancelbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(patient_schedule.this)
                        .setTitle("Cancel")
                        .setMessage("Are you sure you want to cancel? You will not be granted a refund.")

                        .setPositiveButton("Accept", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                Date currentTime = Calendar.getInstance().getTime();
                                db.collection("Schedule").document(scheddocu).update(
                                        "Status", "Canceled",
                                        "DnT", currentTime

                                ).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(patient_schedule.this, "Successfully Canceled", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(patient_schedule.this, patient_schedule.class);
                                        startActivity(intent);
                                    }
                                });
                            }
                        })

                        .setNegativeButton("Decline", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                               dialogInterface.dismiss();
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();




            }
        });

        reschedbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment datepicker = new DatePickerFragment();
                datepicker.show(getSupportFragmentManager(),"date picker");
            }
        });


    }
    public void createSelectDateDialog(){
        dialogbuilder = new AlertDialog.Builder(this);
        final View selectDateView = getLayoutInflater().inflate(R.layout.popupselecttime,null);
        Button btn930 , btn10, btn1030, btn11, btn1130 , btn1, btn130 , btn2 , btn230, btn3, btn330, btn4,btn430,btn5 ;
        TextView doctv, clinictv , datetv;
        GlobalVariables gv = (GlobalVariables) getApplicationContext();
        //initialize buttons
        btn930 = (Button) selectDateView.findViewById(R.id.btntime930);
        btn10 = (Button) selectDateView.findViewById(R.id.btntime10);
        btn1030 = (Button) selectDateView.findViewById(R.id.btntime1030);
        btn11 = (Button) selectDateView.findViewById(R.id.btntime11);
        btn1130 = (Button) selectDateView.findViewById(R.id.btntime1130);
        btn1 = (Button) selectDateView.findViewById(R.id.btntime1);
        btn130 = (Button)selectDateView.findViewById(R.id.btntime130);
        btn2 = (Button) selectDateView.findViewById(R.id.btntime2);
        btn230 = (Button) selectDateView.findViewById(R.id.btntime230);
        btn3 = (Button) selectDateView.findViewById(R.id.btntime3);
        btn330 = (Button) selectDateView.findViewById(R.id.btntime330);
        btn4 = (Button) selectDateView.findViewById(R.id.btntime4);
        btn430 = (Button) selectDateView.findViewById(R.id.btntime430);
        btn5 = (Button) selectDateView.findViewById(R.id.btntime5);
//initialize textview
        doctv= (TextView)selectDateView.findViewById(R.id.Doctnametv);
        clinictv= (TextView)selectDateView.findViewById(R.id.Clinicnametv);
        datetv= (TextView)selectDateView.findViewById(R.id.textView11);

        doctv.setText("Doc. "+doclastname);
        clinictv.setText(gv.getSDClinic());
        datetv.setText(gv.getSDDate());

        //remove already have sched
        db = FirebaseFirestore.getInstance();

        db.collection("Schedule").whereEqualTo("SchedDate",gv.getSDDate()).whereEqualTo("DoctorUId",gv.getSDDocUid()).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){

                            for (QueryDocumentSnapshot doc : task.getResult()) {

                                String timestart =doc.getString("TimeStart");

                                switch (timestart) {
                                    case "9:30am":
                                        btn930.setVisibility(View.GONE);
                                        break;
                                    case "10:00am":
                                        btn10.setVisibility(View.GONE);
                                        break;
                                    case "10:30am":
                                        btn1030.setVisibility(View.GONE);
                                        break;
                                    case "11:00am":
                                        btn11.setVisibility(View.GONE);
                                        break;
                                    case "11:30am":
                                        btn1130.setVisibility(View.GONE);
                                        break;
                                    case "1:00pm":
                                        btn1.setVisibility(View.GONE);
                                        break;
                                    case "1:30pm":
                                        btn130.setVisibility(View.GONE);
                                        break;
                                    case "2:00pm":
                                        btn2.setVisibility(View.GONE);
                                        break;
                                    case "2:30pm":
                                        btn230.setVisibility(View.GONE);
                                        break;
                                    case "3:00pm":
                                        btn3.setVisibility(View.GONE);
                                        break;
                                    case "3:30pm":
                                        btn330.setVisibility(View.GONE);
                                        break;
                                    case "4:00pm":
                                        btn4.setVisibility(View.GONE);
                                        break;
                                    case "4:30pm":
                                        btn430.setVisibility(View.GONE);
                                        break;
                                    case "5:00pm":
                                        btn5.setVisibility(View.GONE);
                                        break;
                                    default:

                                        break;


                                }


                            }

                        }
                    }
                });

        //calling buttons onclick
        btn930.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gv.setSDtimestart("9:30am");
                gv.setSDtimestop("10:00am");
                dialog.dismiss();
                createconfirmDialog();
            }
        });

        btn10.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gv.setSDtimestart("10:00am");
                gv.setSDtimestop("10:30am");
                dialog.dismiss();
                createconfirmDialog();
            }
        });

        btn1030.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gv.setSDtimestart("10:30am");
                gv.setSDtimestop("11:00am");
                dialog.dismiss();
                createconfirmDialog();
            }
        });

        btn11.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gv.setSDtimestart("11:00am");
                gv.setSDtimestop("11:30am");
                dialog.dismiss();
                createconfirmDialog();
            }
        });

        btn1130.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gv.setSDtimestart("11:30am");
                gv.setSDtimestop("12:00pm");
                dialog.dismiss();
                createconfirmDialog();
            }
        });

        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gv.setSDtimestart("1:00pm");
                gv.setSDtimestop("1:30pm");
                dialog.dismiss();
                createconfirmDialog();
            }
        });

        btn130.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gv.setSDtimestart("1:30pm");
                gv.setSDtimestop("2:00pm");
                dialog.dismiss();
                createconfirmDialog();
            }
        });

        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gv.setSDtimestart("2:00pm");
                gv.setSDtimestop("2:30pm");
                dialog.dismiss();
                createconfirmDialog();
            }
        });

        btn230.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gv.setSDtimestart("2:30pm");
                gv.setSDtimestop("3:00pm");
                dialog.dismiss();
                createconfirmDialog();
            }
        });

        btn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gv.setSDtimestart("3:00pm");
                gv.setSDtimestop("3:30pm");
                dialog.dismiss();
                createconfirmDialog();
            }
        });

        btn330.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gv.setSDtimestart("3:30pm");
                gv.setSDtimestop("4:00pm");
                dialog.dismiss();
                createconfirmDialog();
            }
        });

        btn4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gv.setSDtimestart("4:00pm");
                gv.setSDtimestop("4:30pm");
                dialog.dismiss();
                createconfirmDialog();
            }
        });

        btn430.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gv.setSDtimestart("4:30pm");
                gv.setSDtimestop("5:00pm");
                dialog.dismiss();
                createconfirmDialog();
            }
        });

        btn5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gv.setSDtimestart("5:00pm");
                gv.setSDtimestop("5:30pm");
                dialog.dismiss();
                createconfirmDialog();
            }
        });





        dialogbuilder.setView(selectDateView);
        dialog= dialogbuilder.create();
        dialog.show();


    }

    public void createconfirmDialog(){
        dialogbuilder = new AlertDialog.Builder(this);
        final View confirmView = getLayoutInflater().inflate(R.layout.popupconfirm,null);
        GlobalVariables gv = (GlobalVariables) getApplicationContext();
        TextView docnametv , clinicnmtv , datetv, timetv;
        Button cancelbtn , confirmbtn;
        docnametv=(TextView) confirmView.findViewById(R.id.doctornametv);
        clinicnmtv=(TextView) confirmView.findViewById(R.id.clinicnametv);
        datetv=(TextView) confirmView.findViewById(R.id.datetimetv);
        timetv=(TextView) confirmView.findViewById(R.id.timetv);

        cancelbtn= (Button) confirmView.findViewById(R.id.button);
        confirmbtn= (Button) confirmView.findViewById(R.id.button2);

        docnametv.setText("Doctor: Doc."+doclastname);
        clinicnmtv.setText("Clinic Name: "+gv.getSDClinic());
        datetv.setText("Date: "+gv.getSDDate());
        timetv.setText("Time: "+gv.getSDtimestart()+"-"+gv.getSDtimestop());


        cancelbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();

            }
        });

        confirmbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                preferenceManager = new PreferenceManager(getApplicationContext());
                String patuid = preferenceManager.getString(Constants.KEY_USER_ID);
                GlobalVariables gv =(GlobalVariables) getApplicationContext ();
                Date currentTime = Calendar.getInstance().getTime();


                Map<String,Object> Schedule= new HashMap<>();
                Schedule.put("PatientUId",patuid);
                Schedule.put("DoctorUId",gv.getSDDocUid());
                Schedule.put("ClinicName",gv.getSDClinic());
                Schedule.put("SchedDate",gv.getSDDate());
                Schedule.put("TimeStart",gv.getSDtimestart());
                Schedule.put("TimeStop",gv.getSDtimestop());
                Schedule.put("Note","");
                Schedule.put("Status","Paid");
                Schedule.put("DnT",currentTime);



                db.collection("Schedule").document()
                        .set(Schedule)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {

                                db.collection("Patients").document(patuid).update(gv.getSDClinic(),"True").addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {


                                        Date currentTime = Calendar.getInstance().getTime();
                                        db.collection("Schedule").document(scheddocu).update(
                                                "Status", "Rescheduled",
                                                "DnT", currentTime

                                        ).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {


                                                new android.app.AlertDialog.Builder(patient_schedule.this)
                                                        .setTitle("Successfully Rescheduled")
                                                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                                            @Override
                                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                                Intent intent = new Intent(patient_schedule.this,patient_schedule.class);
                                                                startActivity(intent);
                                                                finish();
                                                            }
                                                        }).show();
                                            }
                                        });
                                    }
                                });

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        dialog.dismiss();
                        Toast.makeText(gv, "Fail addingdata", Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });

        dialogbuilder.setView(confirmView);
        dialog= dialogbuilder.create();
        dialog.show();



    }


    @Override
    public void onDateSet(DatePicker datePicker, int year, int month, int dayOfMonth) {
        Date nowdate = new Date() , currentdate  = new Date()  , currentdateminus2  = new Date();
        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, month);
        c.set(Calendar.DAY_OF_MONTH,dayOfMonth);

        String currentDateString = DateFormat.getDateInstance().format(c.getTime());
        c.add(Calendar.DAY_OF_MONTH,-2);
        String currentDateminus2String = DateFormat.getDateInstance().format(c.getTime());
        Toast.makeText(patient_schedule.this, currentDateString, Toast.LENGTH_SHORT).show();
        GlobalVariables gv = (GlobalVariables) getApplicationContext();
        SimpleDateFormat format = new SimpleDateFormat("MMM d,yyyy");
        format.setLenient(false);
        try {
            nowdate = format.parse(datenow);
        } catch (ParseException e) {
            Toast.makeText(patient_schedule.this, "error1", Toast.LENGTH_SHORT).show();
        }
        try {

            currentdate = format.parse(currentDateString);
        } catch (ParseException e) {
            Toast.makeText(patient_schedule.this, "error", Toast.LENGTH_SHORT).show();
        }
        try {
            currentdateminus2 = format.parse(currentDateminus2String);
        } catch (ParseException e) {
            Toast.makeText(patient_schedule.this, "error", Toast.LENGTH_SHORT).show();
        }


        if (currentdateminus2.before(nowdate)){

            Toast.makeText(patient_schedule.this, "please select a valid date", Toast.LENGTH_SHORT).show();
        }
        else {
            Toast.makeText(patient_schedule.this, datenow, Toast.LENGTH_SHORT).show();
            gv.setSDDate(currentDateString);
            createSelectDateDialog();

        }
    }
}