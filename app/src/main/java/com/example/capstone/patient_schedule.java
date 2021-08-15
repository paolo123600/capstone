 package com.example.capstone;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ActionBar;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
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
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import com.example.capstone.utilities.Constants;
import com.example.capstone.utilities.PreferenceManager;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
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
    String[] time = {"8:00AM","8:30AM","9:00AM","9:30AM","10:00AM","10:30AM","11:00AM","11:30AM","12:00PM","12:30PM","1:00PM","1:30PM","2:00PM","2:30PM","3:00PM","3:30PM","4:00PM","4:30PM","5:00PM","5:30PM","6:00PM","6:30PM","7:00PM","7:30PM","8:00PM","8:30PM"};
    String[] btntext = {"8:00-8:30AM","8:30-9:00AM","9:00-9:30AM","9:30-10:00AM","10:00-10:30AM","10:30-11:00AM","11:00-11:30AM","11:30-12:00PM","12:00-12:30PM","12:30-1:00PM","1:00-1:30PM","1:30-2:00PM","2:00-2:30PM","2:30-3:00PM","3:00-3:30PM","3:30-4:00PM","4:00-4:30PM","4:30-5:00PM","5:00-5:30PM","5:30-6:00PM","6:00-6:30PM","6:30-7:00PM","7:00-7:30PM","7:30-8:00PM"};
    private String start = "";
    private String lstart = "";
    private  String lend = "";
    private String end = "";
    private Dialog dialog;
    String doclastname;
    int Position ;

    ImageView back;




    Spinner spinner_status;
    RecyclerView mFirestorelist;
    FirestoreRecyclerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_schedule);

        spinner_status= (Spinner)findViewById(R.id.statspinner);
        mFirestorelist = (RecyclerView)findViewById(R.id.managesched_recview);




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

        back = findViewById(R.id.backspace);




        ArrayAdapter<CharSequence> arrayAdapter = ArrayAdapter.createFromResource(this,
                R.array.StatusPat, android.R.layout.simple_spinner_item);

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
            Toast.makeText(patient_schedule.this, "error1", Toast.LENGTH_SHORT).show();
        }

        db.collection("Schedules").whereEqualTo("PatientUId",Patuid).whereIn("Status",Arrays.asList("Paid","Pending Approval")).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()){

                        for (QueryDocumentSnapshot doc : task.getResult()) {
                            String scheddate = doc.getString("Date");
                                Date datesched = new Date();

                            try {
                               datesched  = format2.parse(scheddate);
                            } catch (ParseException e) {
                                Toast.makeText(patient_schedule.this, "error1", Toast.LENGTH_SHORT).show();
                            }
                                if(nowdate.before(datesched)||nowdate.equals(datesched)){
                                    if(doc.getString("Status").equals("Paid")){
                                        reschedbtn.setVisibility(View.VISIBLE);}
                                    cancelbtn.setVisibility(View.VISIBLE);
                                    scheddocu =doc.getId();
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
                                    timetv.setText("Time :"+doc.getString("StartTime")+"-"+doc.getString("EndTime"));
                                    datetv.setText("Date :"+doc.getString("Date"));
                                    gv.setSDDate(doc.getString("Date"));
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
                                        "Dnt", currentTime,
                                        "Position",0

                                ).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {

                                        db.collection("Schedules").whereEqualTo("DoctorUId",gv.getSDDocUid()).whereEqualTo("StartTime",gv.getSDtimestart()).whereEqualTo("EndTime",gv.getSDtimestop()).whereEqualTo("Status","Paid").whereEqualTo("Date",gv.getSDDate()).get()
                                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                        if(task.isSuccessful()){

                                                            for (QueryDocumentSnapshot doc : task.getResult()) {

                                                                 int docpostion = doc.getLong("Position").intValue();
                                                                if ( docpostion > Position-1){
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
                startActivity(intent);
            }
        });
        spinner_status.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Query  query1 = db.collection("Schedules").whereEqualTo("PatientUId", Patuid).orderBy("Dnt", Query.Direction.ASCENDING).limit(20);
                String selectedstat = spinner_status.getSelectedItem().toString();
                switch (selectedstat) {
                    case "All":
                        query1 = db.collection("Schedules").whereEqualTo("PatientUId", Patuid).orderBy("Dnt", Query.Direction.ASCENDING).limit(20);
                        break;
                    case "Completed":
                        query1 = db.collection("Schedules").whereEqualTo("Status", "Completed").whereEqualTo("PatientUId", Patuid).orderBy("Dnt", Query.Direction.ASCENDING).limit(20);
                        break;
                    case "Rescheduled":
                        query1 = db.collection("Schedules").whereEqualTo("Status", "Rescheduled").whereEqualTo("PatientUId", Patuid).orderBy("Dnt", Query.Direction.ASCENDING).limit(20);

                        break;
                    case "Cancelled":
                        query1 = db.collection("Schedules").whereEqualTo("Status", "Cancelled").whereEqualTo("PatientUId", Patuid).orderBy("Dnt", Query.Direction.ASCENDING).limit(20);

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
                            String date= model.getDate();
                            String status = model.getStatus();
                            Date bookeddate = model.getDnt();
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
                                                                    holder.tvstatus.setText("Status: "+"Paid");
                                                                    break;
                                                                case "Rescheduled":
                                                                    holder.tvpatname.setText("Patient: "+ patname);
                                                                    holder.tvdocname.setText("Doctor: "+docname);
                                                                    holder.tvdatesched.setText("Date: "+date);
                                                                    holder.tvstatus.setText("Status: "+"Rescheduled");
                                                                    break;
                                                                case "Cancelled":
                                                                    holder.tvpatname.setText("Patient: "+ patname);
                                                                    holder.tvdocname.setText("Doctor: "+docname);
                                                                    holder.tvdatesched.setText("Date: "+date);
                                                                    holder.tvstatus.setText("Status: "+"Cancelled");
                                                                    break;

                                                            }
                                                            SimpleDateFormat simpleDate =  new SimpleDateFormat("MMM d ,yyyy h:ma");
                                                            String bookeddatestring = simpleDate.format(bookeddate);
                                                            holder.tvdate.setText(bookeddatestring);
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
    public void createSelectDateDialog(){
        dialogbuilder = new AlertDialog.Builder(this);
        final View selectDateView = getLayoutInflater().inflate(R.layout.popupselecttime,null);

        TextView doctv, clinictv , datetv;
        GlobalVariables gv = (GlobalVariables) getApplicationContext();
        LinearLayout right , left;

        doctv= (TextView)selectDateView.findViewById(R.id.Doctnametv);
        clinictv= (TextView)selectDateView.findViewById(R.id.Clinicnametv);
        datetv= (TextView)selectDateView.findViewById(R.id.textView11);
        right=(LinearLayout)selectDateView.findViewById(R.id.LLright);
        left=(LinearLayout)selectDateView.findViewById(R.id.LLleft);
        //initialize textview

        doctv.setText("Doc. "+doclastname);
        clinictv.setText(gv.getSDClinic());
        datetv.setText(gv.getSDDate());
        String clinicnaame =  gv.getSDClinic();
        Query clinicsRef = db.collection("Clinics").whereEqualTo("ClinicName",clinicnaame);
        clinicsRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {

                        int a=1;
                        start =document.getString("WorkStart");
                        end =document.getString("WorkEnd");
                        lstart =document.getString("LunchStart");
                        lend =document.getString("LunchEnd");

                        int pos = new ArrayList<String>(Arrays.asList(time)).indexOf(start);
                        int posend = new ArrayList<String>(Arrays.asList(time)).indexOf(end);
                        int lpos = new ArrayList<String>(Arrays.asList(time)).indexOf(lstart);
                        int lposend = new ArrayList<String>(Arrays.asList(time)).indexOf(lend);
                        ArrayList<String> Existing = new ArrayList<String>();




                        db.collection("Schedule").whereEqualTo("SchedDate",gv.getSDDate()).whereEqualTo("DoctorUId",gv.getSDDocUid()).get()
                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        if(task.isSuccessful()){
                                            ArrayList<String> arrayList=new ArrayList<String>();
                                            for (QueryDocumentSnapshot documentSnapshot : task.getResult()){

                                                arrayList.add(documentSnapshot.getString("TimeStart"));
                                            }
                                            int abcd=1;
                                            for (int j = pos; j < lpos; j++) {
                                                String Timestart = time[j];
                                                String Timestop = time[j + 1];
                                                if (arrayList.contains(time[j])){
                                                } else {
                                                    Button btnTag = new Button(patient_schedule.this);
                                                    btnTag.setLayoutParams(new ActionBar.LayoutParams(ActionBar.LayoutParams.WRAP_CONTENT, ActionBar.LayoutParams.WRAP_CONTENT));
                                                    btnTag.setText(btntext[j]);
                                                    btnTag.setWidth(440);
                                                    btnTag.setId(j);
                                                    btnTag.setOnClickListener(new View.OnClickListener() {
                                                        @Override
                                                        public void onClick(View view) {
                                                            gv.setSDtimestart(Timestart);
                                                            gv.setSDtimestop(Timestop);
                                                            dialog.dismiss();
                                                            createconfirmDialog();
                                                        }
                                                    });

                                                    if (abcd <= 10) {
                                                        left.addView(btnTag);
                                                    } else {
                                                        right.addView(btnTag);
                                                    }
                                                    abcd++;

                                                }
                                            }
                                            for (int j = lposend; j < posend; j++) {
                                                String Timestart = time[j];
                                                String Timestop = time[j + 1];
                                                if (arrayList.contains(time[j])){
                                                } else {
                                                    Button btnTag = new Button(patient_schedule.this);
                                                    btnTag.setLayoutParams(new ActionBar.LayoutParams(ActionBar.LayoutParams.WRAP_CONTENT, ActionBar.LayoutParams.WRAP_CONTENT));
                                                    btnTag.setText(btntext[j]);
                                                    btnTag.setWidth(440);
                                                    btnTag.setId(j);
                                                    btnTag.setOnClickListener(new View.OnClickListener() {
                                                        @Override
                                                        public void onClick(View view) {
                                                            gv.setSDtimestart(Timestart);
                                                            gv.setSDtimestop(Timestop);
                                                            dialog.dismiss();
                                                            createconfirmDialog();
                                                        }
                                                    });

                                                    if (abcd <= 10) {
                                                        left.addView(btnTag);
                                                    } else {
                                                        right.addView(btnTag);
                                                    }
                                                    abcd++;

                                                }
                                            }
                                        }
                                        else{
                                            Toast.makeText(patient_schedule.this, "nagloloko", Toast.LENGTH_SHORT).show();
                                        }
                                    }});

                    }
                }
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

    private class Schedholder extends RecyclerView.ViewHolder{
        TextView tvpatname , tvdocname , tvstatus, tvdate, tvdatesched;
        public Schedholder(@NonNull View itemView) {
            super(itemView);
            tvpatname = itemView.findViewById(R.id.notif_patname);
            tvdocname = itemView.findViewById(R.id.notif_docname);
            tvstatus = itemView.findViewById(R.id.notif_patStatus);
            tvdatesched = itemView.findViewById(R.id.notif_datesched);
            tvdate = itemView.findViewById(R.id.notif_date);

        }
    }



}




