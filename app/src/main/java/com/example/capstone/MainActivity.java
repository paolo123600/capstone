package com.example.capstone;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DatePickerDialog;
import android.app.Dialog;
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
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.capstone.Model.Chatslist;
import com.example.capstone.Model.PatRC;
import com.example.capstone.adapters.UserAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.example.capstone.activities.VideoCall_Main;
import com.example.capstone.utilities.Constants;
import com.example.capstone.utilities.PreferenceManager;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;


import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, DatePickerDialog.OnDateSetListener {
    private Button buttonbook;
    private Button buttonsched;
    Button chat;
    private DrawerLayout drawer;
    private AlertDialog.Builder dialogbuilder;
    private Dialog dialog;
    private RecyclerView doctorlist;
    private  FirestoreRecyclerAdapter adapter;
    private PreferenceManager preferenceManager;
    FirebaseFirestore db;
    FirebaseAuth fAuth;
    private String doclastname;
    String datenow;
    String userId;
    Boolean schedalready = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = FirebaseFirestore.getInstance();

        preferenceManager = new PreferenceManager(getApplicationContext());

        String patuid = preferenceManager.getString(Constants.KEY_USER_ID);
        chat = findViewById(R.id.chat);
        buttonbook = (Button) findViewById(R.id.booknow);
        buttonsched = (Button) findViewById(R.id.schedules);

        fAuth = FirebaseAuth.getInstance();
        userId = fAuth.getCurrentUser().getUid();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_viewer_pat);
        navigationView.setNavigationItemSelectedListener(this);

        updateNavHeader();


        chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

        Intent intent = new Intent(MainActivity.this, RecentChats.class);
        startActivity(intent);




            }
        });
                buttonbook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                datenow = DateFormat.getDateInstance().format(calendar.getTime());
                db.collection("Schedule").whereEqualTo("PatientUId",patuid).whereEqualTo("Status","Paid")
                        .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            QuerySnapshot querySnapshot = task.getResult();
                            if (querySnapshot.isEmpty()) {

                                createSelectDoctorDialog();
                            }
                            else{
                                Date nowdate= new Date();
                                SimpleDateFormat format = new SimpleDateFormat("MMM d,yyyy");
                                format.setLenient(false);
                                try {
                                    nowdate = format.parse(datenow);
                                } catch (ParseException e) {
                                    Toast.makeText(MainActivity.this, "error1", Toast.LENGTH_SHORT).show();
                                }
                                for (QueryDocumentSnapshot doc : task.getResult()) {

                                    String scheddate = doc.getString("SchedDate");
                                    Date datesched = new Date();
                                    try {
                                        datesched  = format.parse(scheddate);
                                    } catch (ParseException e) {
                                        Toast.makeText(MainActivity.this, "error1", Toast.LENGTH_SHORT).show();
                                    }
                                    if(nowdate.before(datesched)||nowdate.equals(datesched)){

                                        Toast.makeText(MainActivity.this, "You already have an appointment.", Toast.LENGTH_SHORT).show();
                                        schedalready=true;

                                    }
                                    else if (nowdate.after(datesched)) {  String documentsched =doc.getId();
                                    db.collection("Schedule").document(documentsched).update("Status","Completed").addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {

                                        }
                                    });


                                    }

                        }  if (schedalready==false){
                                    createSelectDoctorDialog();
                                }  }


                        }
                    }
                });


            }
        });

        buttonsched.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, patient_schedule.class);
                startActivity(intent);
            }
        });

        FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
            @Override
            public void onComplete(@NonNull Task<InstanceIdResult> task) {
                if (task.isSuccessful() && task.getResult() != null) {
                    sendFCMTokenToDatabase(task.getResult().getToken());
                }
            }
        });


        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();



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
        Toast.makeText(MainActivity.this, currentDateString, Toast.LENGTH_SHORT).show();
        GlobalVariables gv = (GlobalVariables) getApplicationContext();
        SimpleDateFormat format = new SimpleDateFormat("MMM d,yyyy");
        format.setLenient(false);
        try {
             nowdate = format.parse(datenow);
        } catch (ParseException e) {
            Toast.makeText(MainActivity.this, "error1", Toast.LENGTH_SHORT).show();
        }
        try {

            currentdate = format.parse(currentDateString);
        } catch (ParseException e) {
            Toast.makeText(MainActivity.this, "error", Toast.LENGTH_SHORT).show();
        }
        try {
            currentdateminus2 = format.parse(currentDateminus2String);
        } catch (ParseException e) {
            Toast.makeText(MainActivity.this, "error", Toast.LENGTH_SHORT).show();
        }


        if (currentdateminus2.before(nowdate)){

            Toast.makeText(MainActivity.this, "please select a valid date", Toast.LENGTH_SHORT).show();
        }
        else {
            Toast.makeText(MainActivity.this, datenow, Toast.LENGTH_SHORT).show();
            gv.setSDDate(currentDateString);
            createSelectDateDialog();

        }

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.nav_about:
                Intent intent1 = new Intent(MainActivity.this,about.class);
                startActivity(intent1);
                break;
            case R.id.nav_profile:
                Intent intent = new Intent(MainActivity.this, ProfileFragment.class);
                startActivity(intent);
                break;
            case R.id.nav_logout:
                Toast.makeText(this, "Signing Out...", Toast.LENGTH_SHORT).show();
                FirebaseFirestore database = FirebaseFirestore.getInstance();
                DocumentReference documentReference =
                        db.collection(Constants.KEY_COLLECTION_USERS).document(
                                preferenceManager.getString(Constants.KEY_USER_ID)
                        );
                HashMap<String, Object> updates = new HashMap<>();
                updates.put(Constants.KEY_FCM_TOKEN, FieldValue.delete());
                documentReference.update(updates)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                preferenceManager.clearPreferences();
                                startActivity(new Intent(getApplicationContext(), Login.class));
                                finish();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(MainActivity.this, "Unable to sign out", Toast.LENGTH_SHORT).show();
                            }
                        });
                break;
            case R.id.medical_records:
                Intent intent2 = new Intent(MainActivity.this, medical_records.class);
                startActivity(intent2);
                break;
        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)){
            drawer.closeDrawer(GravityCompat.START);
        }else {
            super.onBackPressed();
        }

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
                                        new android.app.AlertDialog.Builder(MainActivity.this)
                                                .setTitle("Account Successfully Created")
                                                .setMessage("You have successfully created an account!! You can now login in the login page.")
                                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialogInterface, int i) {
                                                        dialog.dismiss();
                                                    }
                                                }).show();
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

    public void createSelectDoctorDialog(){
        dialogbuilder = new AlertDialog.Builder(this);
        final View selectDoctorView = getLayoutInflater().inflate(R.layout.popupselectdoctor,null);
        // Start
        doctorlist= (RecyclerView) selectDoctorView.findViewById(R.id.DoctorRF);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference clinicsRef = db.collection("Clinics");
        Spinner spinner = (Spinner) selectDoctorView.findViewById(R.id.spinnerclinic);
        List<String> Clinics = new ArrayList<>();
        ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_item, Clinics);
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter1);
        clinicsRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        String subject = document.getString("ClinicName");
                        Clinics.add(subject);
                    }
                    adapter1.notifyDataSetChanged();
                }
            }
        });

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {


            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                String Clinicname = spinner.getSelectedItem().toString();
                //query
                Query query = db.collection("Doctors").whereEqualTo("ClinicName",Clinicname);
                FirestoreRecyclerOptions<DoctorModel> options = new FirestoreRecyclerOptions.Builder<DoctorModel>()
                        .setQuery(query,DoctorModel.class)
                        .build();
                //adapter
                 adapter = new FirestoreRecyclerAdapter<DoctorModel, DoctorsViewHolder>(options) {
                    @NonNull
                    @Override
                    public DoctorsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.doctor_list_single,parent,false);
                        return new DoctorsViewHolder(view);
                    }

                    @Override
                    protected void onBindViewHolder(@NonNull DoctorsViewHolder holder, int position, @NonNull DoctorModel model) {
                        holder.list_docname.setText("Doc "+model.getLastName());
                        holder.list_docemail.setText(model.getEmail());
                        holder.list_docclinic.setText(model.getClinic());
                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                GlobalVariables gv =(GlobalVariables) getApplicationContext ();
                                gv.setSDDocemail(model.getEmail());
                                gv.setSDDocUid(model.getUserId());
                                doclastname=model.getLastName();
                                gv.setSDClinic(Clinicname);
                                dialog.dismiss();
                                DialogFragment datepicker = new DatePickerFragment();
                                datepicker.show(getSupportFragmentManager(),"date picker");



                            }
                        });

                    }
                };

                doctorlist.setHasFixedSize(true);
                doctorlist.setLayoutManager(new LinearLayoutManager(MainActivity.this));
                doctorlist.setAdapter(adapter);
                adapter.startListening();

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        dialogbuilder.setView(selectDoctorView);
        dialog= dialogbuilder.create();
        dialog.show();





    }

    private class DoctorsViewHolder extends RecyclerView.ViewHolder{
       private TextView list_docname;
        private TextView list_docemail;
        private TextView list_docclinic;


        public DoctorsViewHolder(@NonNull View itemView) {
            super(itemView);
                list_docname = itemView.findViewById(R.id.list_patientname);
            list_docemail = itemView.findViewById(R.id.list_patemail);
            list_docclinic= itemView.findViewById(R.id.list_docclinic);

        }

    }




    private void sendFCMTokenToDatabase (String token) {
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        DocumentReference documentReference = db.collection(Constants.KEY_COLLECTION_USERS).document(
                preferenceManager.getString(Constants.KEY_USER_ID)
        );
        documentReference.update(Constants.KEY_FCM_TOKEN, token)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(MainActivity.this, "Unable to send token: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void updateNavHeader () {
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_viewer_pat);
        View headerView = navigationView.getHeaderView(0);
        TextView navUsernamePat = headerView.findViewById(R.id.nav_header_name);
        TextView navEmail = headerView.findViewById(R.id.nav_header_email);

        DocumentReference documentReference = db.collection("Patients").document(userId);
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                navUsernamePat.setText(documentSnapshot.getString("FirstName") + " " + documentSnapshot.getString("LastName"));
                navEmail.setText(documentSnapshot.getString("Email"));
            }
        });


    }
}
