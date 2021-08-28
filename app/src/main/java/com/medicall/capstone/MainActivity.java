package com.medicall.capstone;

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

import android.app.ActionBar;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.medicall.capstone.R;

import com.medicall.capstone.utilities.Constants;
import com.medicall.capstone.utilities.PreferenceManager;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.MetadataChanges;
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
import java.util.List;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;


import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, DatePickerDialog.OnDateSetListener {
    private LinearLayout buttonbook;
    private LinearLayout buttonsched;
    LinearLayout chat;
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
    String[] time = {"8:00AM","8:30AM","9:00AM","9:30AM","10:00AM","10:30AM","11:00AM","11:30AM","12:00PM","12:30PM","1:00PM","1:30PM","2:00PM","2:30PM","3:00PM","3:30PM","4:00PM","4:30PM","5:00PM","5:30PM","6:00PM","6:30PM","7:00PM","7:30PM","8:00PM","8:30PM"};
    String[] btntext = {"8:00-8:30AM","8:30-9:00AM","9:00-9:30AM","9:30-10:00AM","10:00-10:30AM","10:30-11:00AM","11:00-11:30AM","11:30-12:00PM","12:00-12:30PM","12:30-1:00PM","1:00-1:30PM","1:30-2:00PM","2:00-2:30PM","2:30-3:00PM","3:00-3:30PM","3:30-4:00PM","4:00-4:30PM","4:30-5:00PM","5:00-5:30PM","5:30-6:00PM","6:00-6:30PM","6:30-7:00PM","7:00-7:30PM","7:30-8:00PM"};
   private String start = "";
    private String lstart = "";
    private  String lend = "";
    private String end = "";
    TextView docname, patienttime, position;
    LinearLayout logo, schedstats;
    private String document_id_reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        docname = findViewById(R.id.doctor_name);
        patienttime = findViewById(R.id.est_time);
        position = findViewById(R.id.position_queue);

        logo = findViewById(R.id.imagelogo);
        schedstats = findViewById(R.id.linearLayout1);
        schedstats.setVisibility(LinearLayout.GONE);

        db = FirebaseFirestore.getInstance();

        preferenceManager = new PreferenceManager(getApplicationContext());

        String patuid = preferenceManager.getString(Constants.KEY_USER_ID);
        chat = findViewById(R.id.chat);
        buttonbook = findViewById(R.id.booknow);
        buttonsched =  findViewById(R.id.schedules);

        fAuth = FirebaseAuth.getInstance();
        userId = fAuth.getCurrentUser().getUid();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_viewer_pat);
        navigationView.setNavigationItemSelectedListener(this);

        updateNavHeader();




        db.collection("Schedules").whereEqualTo("PatientUId", patuid).whereEqualTo("Status","Paid")
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    QuerySnapshot querySnapshot = task.getResult();
                    if(!querySnapshot.isEmpty()){
                        logo.setVisibility(LinearLayout.GONE);
                        schedstats.setVisibility(LinearLayout.VISIBLE);

                        for (QueryDocumentSnapshot patient : task.getResult()){
                            document_id_reference = patient.getId();
                                    db.collection("Doctors").document(patient.getString("DoctorUId"))
                                            .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                            if(task.isSuccessful()){
                                                DocumentSnapshot document = task.getResult();
                                                docname.setText(document.getString("LastName"));
                                            }
                                        }
                                    });
                                    patienttime.setText(patient.getString("StartTime") + " - " +patient.getString("EndTime"));
                                    position.setText(patient.get("Position") + "");

                            DocumentReference docRef = db.collection("Schedules").document(document_id_reference);
                            docRef.addSnapshotListener(MetadataChanges.INCLUDE, new EventListener<DocumentSnapshot>() {
                                @Override
                                public void onEvent(@Nullable DocumentSnapshot snapshot,
                                                    @Nullable FirebaseFirestoreException e) {
                                    if(!snapshot.getString("Status").equals("Paid")){
                                        schedstats.setVisibility(LinearLayout.GONE);
                                        logo.setVisibility(LinearLayout.VISIBLE);
                                    }
                                    else{
                                        position.setText(snapshot.get("Position") + "");
                                    }
                                }
                            });

                        }
                    }
                    else{

                    }
                }
            }
        });

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
                db.collection("Schedules").whereEqualTo("PatientUId",patuid).whereIn("Status", Arrays.asList("Paid","Pending Approval"))
                        .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            QuerySnapshot querySnapshot = task.getResult();
                            if (querySnapshot.isEmpty()) {
                                Intent intent = new Intent(MainActivity.this, PaymentMethod.class);
                                startActivity(intent);
                            }
                            else{
                                Date nowdate= new Date();
                                SimpleDateFormat format = new SimpleDateFormat("MMMM d,yyyy");

                                SimpleDateFormat format2 = new SimpleDateFormat("MMMM d ,yyyy");
                                format.setLenient(false);
                                try {
                                    nowdate = format.parse(datenow);
                                } catch (ParseException e) {
                                    Toast.makeText(MainActivity.this, "error1", Toast.LENGTH_SHORT).show();
                                }
                                for (QueryDocumentSnapshot doc : task.getResult()) {


                                    Date datesched = doc.getDate("Date");

                                    if(nowdate.before(datesched)||nowdate.equals(datesched)){

                                        if(doc.getString("Status").equals("Paid")){
                                            Toast.makeText(MainActivity.this, "You already have an appointment.", Toast.LENGTH_SHORT).show();
                                        }
                                        else if(doc.getString("Status").equals("Pending Approval")){
                                            Toast.makeText(MainActivity.this, "Your appointment is currently under review.", Toast.LENGTH_SHORT).show();
                                        }
                                        schedalready=true;

                                    }
                                    else if (nowdate.after(datesched)) {  String documentsched =doc.getId();
                                    db.collection("Schedules").document(documentsched).update("Status","Completed").addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {

                                        }
                                    });
                                    }

                        }  if (schedalready==false){
                                    Intent intent = new Intent(MainActivity.this, selectDoc.class);
                                    startActivity(intent);
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
        GlobalVariables gv = (GlobalVariables) getApplicationContext();
        SimpleDateFormat format = new SimpleDateFormat("MMM d,yyyy");
        format.setLenient(false);
        try {
             nowdate = format.parse(datenow);
        } catch (ParseException e) {
            Toast.makeText(MainActivity.this, "error5", Toast.LENGTH_SHORT).show();
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
            gv.setSDDate(currentDateString);
            createSelectDateDialog();

        }

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.nav_about:
                Intent intent1 = new Intent(MainActivity.this, about.class);
                startActivity(intent1);
                break;
            case R.id.blood_p:
                Intent intent3 = new Intent(MainActivity.this, pat_blood_pressure.class);
                startActivity(intent3);
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
            case R.id.nav_listHMO:
                Intent intent5 = new Intent(MainActivity.this, Patient_HMOList.class);
                startActivity(intent5);
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
           finish();
        }

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
                                                    Button btnTag = new Button(MainActivity.this);
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
                                                    Button btnTag = new Button(MainActivity.this);
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
                                            Toast.makeText(MainActivity.this, "nagloloko", Toast.LENGTH_SHORT).show();
                                        }
                                    }});

                    }
                }
            }
        });






        dialogbuilder.setView(selectDateView);
        dialog= dialogbuilder.create();
        Window window = dialog.getWindow();
        window.setLayout(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);
        window.setGravity(Gravity.CENTER);
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
                                                .setTitle("Successfully Booked an appointment")
                                                .setPositiveButton("Okay", new DialogInterface.OnClickListener() {
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