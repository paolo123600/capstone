package com.medicall.capstone;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ActionBar;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.app.Dialog;

import com.medicall.capstone.R;

import com.medicall.capstone.utilities.Constants;
import com.medicall.capstone.utilities.PreferenceManager;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


public class patient_schedule extends AppCompatActivity  {
    TextView docnametv, clinicnametv, statustv, datetv, timetv;
    Button cancelbtn, reschedbtn;
    private PreferenceManager preferenceManager;
    private AlertDialog.Builder dialogbuilder;
    String Patuid ="";
    String datenow;
    Date nowdate= new Date();
    FirebaseFirestore db;
    Date now= new Date();
    String docid;
    String scheddocu;
    private String start = "";
    private String lstart = "";
    private  String lend = "";
    private String end = "";
    private Dialog dialog;
    private String price = "";
    String doclastname;
    int Position ;

    ImageView back;

    Spinner spinner_status;
    RecyclerView mFirestorelist;
    FirestoreRecyclerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_schedule);

        spinner_status= (Spinner)findViewById(R.id.statspinner);
        mFirestorelist = (RecyclerView)findViewById(R.id.managesched_recview);

        now = Calendar.getInstance().getTime();
        SimpleDateFormat format3 = new SimpleDateFormat("MMMM dd,yyyy");
        datenow =format3.format(now);





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
        back = findViewById(R.id.backspace);


        ArrayAdapter<CharSequence> arrayAdapter = ArrayAdapter.createFromResource(this,
                R.array.StatusPat, R.layout.custom_spinner);

        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner_status.setAdapter(arrayAdapter);


        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Login.class);
                startActivity(intent);
            }
        });

        SimpleDateFormat format = new SimpleDateFormat("MMMM dd,yyyy");
        SimpleDateFormat format2 = new SimpleDateFormat("MMMM d ,yyyy");
        format.setLenient(false);
        try {
            nowdate = format.parse(datenow);
        } catch (ParseException e) {
            Toast.makeText(patient_schedule.this, "error2", Toast.LENGTH_SHORT).show();
        }

        db.collection("Schedules").whereEqualTo("PatientUId",Patuid).whereIn("Status",Arrays.asList("Paid","Pending Approval","Declined","Approved")).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){

                    for (QueryDocumentSnapshot doc : task.getResult()) {
                        Date datesched = doc.getDate("Date");
                        SimpleDateFormat format = new SimpleDateFormat("MMMM d ,yyyy");
                        String scheddate =format.format(datesched);


                        try {
                            datesched  = format2.parse(scheddate);
                        } catch (ParseException e) {
                            Toast.makeText(patient_schedule.this, "error1", Toast.LENGTH_SHORT).show();
                        }
                        if(nowdate.before(datesched)||nowdate.equals(datesched)){
                            if(doc.getString("Status").equals("Paid")){
                                cancelbtn.setVisibility(View.VISIBLE);
                                reschedbtn.setVisibility(View.VISIBLE);}
                            if(doc.getString("Status").equals("Pending Approval")||doc.getString("Status").equals("Approved")){
                                cancelbtn.setVisibility(View.VISIBLE);}

                            scheddocu =doc.getId();
                            gv.setSDid(scheddocu);
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
                            Date datescheds =doc.getDate("Date");

                            String date1=  format.format(datesched);
                            clinicnametv.setText(doc.getString("ClinicName"));
                            statustv.setText("Status: "+doc.getString("Status"));
                            timetv.setText("Time :"+doc.getString("StartTime")+"-"+doc.getString("EndTime"));
                            datetv.setText("Date :"+date1);
                            price = doc.getString("Price");

                            gv.setDDate(datesched);
                            Position = doc.getLong("Position").intValue();
                            gv.setPosition(doc.getLong("Position").intValue());
                            gv.setSDClinic(doc.getString("ClinicName"));
                            gv.setSDDocUid(doc.getString("DoctorUId"));
                            gv.setSDtimestart(doc.getString("StartTime"));
                            gv.setSDtimestop(doc.getString("EndTime"));

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
                                db.collection("Schedules").document(scheddocu).update(
                                        "Status", "Cancelled",
                                        "Position",0

                                ).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        db.collection("Schedules").document(scheddocu).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                if(task.isSuccessful()){
                                                    DocumentSnapshot documentSnapshot = task.getResult();
                                                    Map<String, Object> Notif = new HashMap<>();
                                                    Notif.put("DoctorUId", documentSnapshot.getString("DoctorUId"));
                                                    Notif.put ("Date", documentSnapshot.getDate("Date"));
                                                    Notif.put("AppointID",scheddocu);
                                                    Notif.put ("Status", "Cancelled" );
                                                    Notif.put ("PatientUId", documentSnapshot.getString("PatientUId") );
                                                    Notif.put ("Dnt",currentTime);
                                                    Notif.put ("Seen",false);
                                                    Notif.put("ClinicName",documentSnapshot.getString("ClinicName"));
                                                    db.collection("Notification").document().set(Notif).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {
                                                            db.collection("Schedules").whereEqualTo("DoctorUId",gv.getSDDocUid()).whereEqualTo("StartTime",gv.getSDtimestart()).whereEqualTo("EndTime",gv.getSDtimestop()).whereIn("Status",Arrays.asList("Paid","Pending Approval","Approved")).whereEqualTo("Date",gv.getDDate()).get()
                                                                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                                        @Override
                                                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                                            if(task.isSuccessful()){

                                                                                for (QueryDocumentSnapshot doc : task.getResult()) {

                                                                                    int docpostion = doc.getLong("Position").intValue();
                                                                                    if ( docpostion > Position){
                                                                                        String docuid = doc.getId();
                                                                                        db.collection("Schedules").document(docuid).update(
                                                                                                "Position",docpostion-1

                                                                                        ).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                            @Override
                                                                                            public void onSuccess(Void aVoid) {

                                                                                            }
                                                                                        });

                                                                                    }


                                                                                }
                                                                            }
                                                                        }
                                                                    });
                                                            Toast.makeText(patient_schedule.this, "Successfully Cancelled", Toast.LENGTH_SHORT).show();
                                                            Intent intent = new Intent(patient_schedule.this, patient_schedule.class);
                                                            startActivity(intent);
                                                        }
                                                    });
                                                }
                                            }
                                        });


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
                Intent intent = new Intent(getApplicationContext(), reschedule_date.class);
                intent.putExtra("schedid", scheddocu);
                intent.putExtra("price", price);
                startActivity(intent);
            }
        });
        spinner_status.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Query  query1 = db.collection("Notification").whereEqualTo("PatientUId", Patuid).orderBy("Dnt", Query.Direction.DESCENDING).limit(20);
                String selectedstat = spinner_status.getSelectedItem().toString();
                switch (selectedstat) {
                    case "All":
                        query1 = db.collection("Notification").whereEqualTo("PatientUId", Patuid).whereIn("Status", Arrays.asList("Completed" ,"Rescheduled", "Cancelled","Declined","Approved","Unattended")).orderBy("Dnt", Query.Direction.DESCENDING).limit(20);
                        break;
                    case "Declined":
                        query1 = db.collection("Notification").whereEqualTo("Status", "Declined").whereEqualTo("PatientUId", Patuid).orderBy("Dnt", Query.Direction.DESCENDING).limit(20);
                        break;
                    case "Completed":
                        query1 = db.collection("Notification").whereEqualTo("Status", "Completed").whereEqualTo("PatientUId", Patuid).orderBy("Dnt", Query.Direction.DESCENDING).limit(20);
                        break;
                    case "Approved":
                        query1 = db.collection("Notification").whereEqualTo("Status", "Approved").whereEqualTo("PatientUId", Patuid).orderBy("Dnt", Query.Direction.DESCENDING).limit(20);
                        break;
                    case "Rescheduled":
                        query1 = db.collection("Notification").whereEqualTo("Status", "Rescheduled").whereEqualTo("PatientUId", Patuid).orderBy("Dnt", Query.Direction.DESCENDING).limit(20);

                        break;
                    case "Cancelled":
                        query1 = db.collection("Notification").whereEqualTo("Status", "Cancelled").whereEqualTo("PatientUId", Patuid).orderBy("Dnt", Query.Direction.DESCENDING).limit(20);

                        break;
                    case "Unattended":
                        query1 = db.collection("Notification").whereEqualTo("Status", "Unattended").whereEqualTo("PatientUId", Patuid).orderBy("Dnt", Query.Direction.DESCENDING).limit(20);

                        break;

                }
                FirestoreRecyclerOptions<DocTodaySchedModel> options = new FirestoreRecyclerOptions.Builder<DocTodaySchedModel>()
                        .setQuery(query1, DocTodaySchedModel.class)
                        .build();
                adapter = new FirestoreRecyclerAdapter<DocTodaySchedModel, patient_schedule.Schedholder>(options) {
                    @NonNull
                    @Override
                    public patient_schedule.Schedholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.notif_single_patient,parent,false);
                        return new patient_schedule.Schedholder(view);
                    }

                    @Override
                    protected void onBindViewHolder(@NonNull patient_schedule.Schedholder holder, int position, @NonNull DocTodaySchedModel model) {
                        Date datesched =model.getDate();
                        SimpleDateFormat format = new SimpleDateFormat("MMMM d ,yyyy");
                        String date=  format.format(datesched);
                        String status = model.getStatus();
                        Date bookeddate = model.getDnt();
                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                String documentId = model.AppointID;
                                Intent intent = new Intent(getApplicationContext(), Patient_sched_status.class);
                                intent.putExtra("schedid", documentId);
                                startActivity(intent);
                            }
                        });


                        db.collection("Patients").document(model.getPatientUId()).get()
                                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                    @Override
                                    public void onSuccess(DocumentSnapshot documentSnapshot1) {
                                        documentSnapshot1.getData();
                                        String patname = documentSnapshot1.getString("LastName")+", "+documentSnapshot1.getString("FirstName");
                                        db.collection("Doctors").document(model.getDoctorUId()).get()
                                                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                                    @Override
                                                    public void onSuccess(DocumentSnapshot documentSnapshot2) {
                                                        documentSnapshot2.getData();
                                                        String docname= "Doc. "+documentSnapshot2.getString("LastName");
                                                        switch (status){
                                                            case "Completed":
                                                                holder.tvpatname.setText("Patient: "+ patname);
                                                                holder.tvdocname.setText("Doctor: "+docname);
                                                                holder.tvdatesched.setText("Date: "+date);
                                                                holder.tvstatus.setText(Html.fromHtml("Status: "+"<font color='#2FD845'>COMPLETED</font>"));
                                                                break;
                                                            case "Rescheduled":
                                                                holder.tvpatname.setText("Patient: "+ patname);
                                                                holder.tvdocname.setText("Doctor: "+docname);
                                                                holder.tvdatesched.setText("Date: "+date);
                                                                holder.tvstatus.setText(Html.fromHtml("Status: "+"<font color='#33D2DA'>RESCHEDULED</font>"));
                                                                break;
                                                            case "Cancelled":
                                                                holder.tvpatname.setText("Patient: "+ patname);
                                                                holder.tvdocname.setText("Doctor: "+docname);
                                                                holder.tvdatesched.setText("Date: "+date);
                                                                holder.tvstatus.setText(Html.fromHtml("Status: "+"<font color='#FF871D'>CANCELLED</font>"));
                                                                break;

                                                            case "Declined":
                                                                holder.tvpatname.setText("Patient: "+ patname);
                                                                holder.tvdocname.setText("Doctor: "+docname);
                                                                holder.tvdatesched.setText("Date: "+date);
                                                                holder.tvstatus.setText(Html.fromHtml("Status: "+"<font color='#DA3333'>DECLINED</font>"));
                                                                break;
                                                            case "Approved":
                                                                holder.tvpatname.setText("Patient: "+ patname);
                                                                holder.tvdocname.setText("Doctor: "+docname);
                                                                holder.tvdatesched.setText("Date: "+date);
                                                                holder.tvstatus.setText(Html.fromHtml("Status: "+"<font color='#2FD845'>APPROVED</font>"));
                                                                break;

                                                            case "Unattended":
                                                                holder.tvpatname.setText("Patient: "+ patname);
                                                                holder.tvdocname.setText("Doctor: "+docname);
                                                                holder.tvdatesched.setText("Date: "+date);
                                                                holder.tvstatus.setText(Html.fromHtml("Status: "+"<font color='#DA3333'>UNATTENDED</font>"));
                                                                break;

                                                        }
                                                        SimpleDateFormat simpleDate =  new SimpleDateFormat("MMM d ,yyyy h:ma");
                                                        String bookeddatestring = simpleDate.format(bookeddate);
                                                    }
                                                }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(patient_schedule.this, "error showing patient", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(patient_schedule.this, "error showing patient", Toast.LENGTH_SHORT).show();
                            }
                        });




                    }
                };
                mFirestorelist.setHasFixedSize(true);
                mFirestorelist.setLayoutManager(new LinearLayoutManager(patient_schedule.this));
                mFirestorelist.setAdapter(adapter);
                adapter.startListening();


            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), Login.class);
        startActivity(intent);
    }







    private class Schedholder extends RecyclerView.ViewHolder{
        TextView tvpatname , tvdocname , tvstatus, tvdate, tvdatesched;
        Button pat_prescription;
        public Schedholder(@NonNull View itemView) {
            super(itemView);
            tvpatname = itemView.findViewById(R.id.notif_patname);
            tvdocname = itemView.findViewById(R.id.notif_docname);
            tvstatus = itemView.findViewById(R.id.notif_patStatus);
            tvdatesched = itemView.findViewById(R.id.notif_datesched);

        }
    }



}




