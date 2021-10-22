package com.medicall.capstone;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.medicall.capstone.R;

import com.medicall.capstone.activities.OutgoingInvitationActivity;
import com.medicall.capstone.doctor.Doctor_schedlist_pastsched;
import com.medicall.capstone.doctor.Doctor_schedlist_upcoming;
import com.medicall.capstone.models.User;
import com.medicall.capstone.utilities.Constants;
import com.medicall.capstone.utilities.PreferenceManager;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class  doctor_homepage extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    Button btn_dochat;
    FirebaseFirestore db;
    private Button callbtn;
    private Button  pat_record;
    String gmail = "";
    TextView noofappoints;
    String patUid = "";
    String currentday= "";
    private List<User> users;
    private PreferenceManager preferenceManager;
    String datenow;
    Date timenow;
    TextView patnametv, schedtimetv;
    Boolean condition=false;
    private DrawerLayout drawer;
    FirebaseAuth fAuth;
    FirestoreRecyclerAdapter adapter;
    FirebaseFirestore fStore;
    String SchedTimeStart;
    Date DDate ;
    String SchedTimeEnd;
    RecyclerView mFirestorelist;
    String userId;
    ImageView btncomplete , btnnext;
    private BroadcastReceiver minuteUpdateReceiver;

    private StorageReference storageReference;
    private FirebaseStorage storage;
    StorageReference ref;
    String image;
    Bitmap getpic;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.doctor_home);

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        db = FirebaseFirestore.getInstance();
        preferenceManager = new PreferenceManager(getApplicationContext());
        btn_dochat = (Button) findViewById(R.id.btn_chat_dochome);
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat format = new SimpleDateFormat("MMMM d ,yyyy");
        DDate = calendar.getTime();
        datenow = format.format(DDate);
        try {
            DDate = format.parse(datenow);
        } catch (ParseException e) {
            Toast.makeText(this, "error", Toast.LENGTH_SHORT).show();
        }

        mFirestorelist = (RecyclerView)findViewById(R.id.scheddoc_list);
        noofappoints = (TextView) findViewById(R.id.position);
        btncomplete= (ImageView) findViewById(R.id.button_complete);
        btnnext = (ImageView) findViewById(R.id.button_next);
        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        userId = fAuth.getCurrentUser().getUid();
        pat_record= (Button) findViewById(R.id.btn_patient);
        callbtn = (Button) findViewById(R.id.btn_call);
        drawer = findViewById(R.id.drawer_layout_doc);
        NavigationView navigationView = findViewById(R.id.nav_viewer_doc);
        navigationView.setNavigationItemSelectedListener(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        updateNavHeader();


        patnametv = (TextView) findViewById(R.id.tvPatname);
        schedtimetv = (TextView) findViewById(R.id.tvSched);
        SimpleDateFormat dateFormat = new SimpleDateFormat("h:mmaa");

        try {

            Calendar calendar2 = Calendar.getInstance();
            int day = calendar2.get(Calendar.DAY_OF_WEEK);

            switch (day) {
                case Calendar.SUNDAY:
                    currentday = "Sunday";
                    break;
                case Calendar.MONDAY:
                    currentday = "Monday";
                    break;
                case Calendar.TUESDAY:
                    currentday = "Tuesday";
                    break;
                case Calendar.WEDNESDAY:
                    currentday = "Wednesday";
                    break;
                case Calendar.THURSDAY:
                    currentday = "Thursday";
                    break;
                case Calendar.FRIDAY:
                    currentday = "Friday";
                    break;
                case Calendar.SATURDAY:
                    currentday = "Saturday";
                    break;
            }

            Date currentTime = Calendar.getInstance().getTime();

//            String timenow1 =dateFormat.format(currentTime);


          String timenow1 ="3:40PM";
            timenow = dateFormat.parse(timenow1);

        } catch (ParseException e) {
            Toast.makeText(doctor_homepage.this, "error getting time", Toast.LENGTH_SHORT).show();
        }


        checkschedcurrent();




        GlobalVariables gv = (GlobalVariables) getApplicationContext();
        pat_record.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(doctor_homepage.this,patientrec_sec.class);
                intent.putExtra("patid",patUid);
                startActivity(intent);

            }
        });
        btn_dochat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), RecentChatDoc.class);
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


        users = new ArrayList<>();


        callbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Query query = db.collection("Patients").whereEqualTo("UserId", patUid);
                query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {

                                User user = new User();
                                GlobalVariables gv = (GlobalVariables) getApplicationContext();
                                gv.setSDPatUId(document.getString("UserId"));
                                user.token = document.getString("fcm_token");
                                user.firstName = document.get("FirstName").toString();
                                user.lastName = document.get("LastName").toString();
                                user.email = document.get("Email").toString();
                                users.add(user);

                                if (user.token == null || user.token.trim().isEmpty()) {

                                    Toast.makeText(doctor_homepage.this, "The user is offline", Toast.LENGTH_SHORT).show();

                                } else {
                                    Intent intent = new Intent(getApplicationContext(), OutgoingInvitationActivity.class);
                                    intent.putExtra("user", user);
                                    intent.putExtra("type", "video");
                                    startActivity(intent);
                                }

                            }


                        }
                    }
                });

            }
        });


    }
    public void startMinuteUpdater(){
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_TIME_TICK);
        minuteUpdateReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                SimpleDateFormat dateFormat = new SimpleDateFormat("h:mmaa");

                try {

                    Calendar calendar2 = Calendar.getInstance();
                    int day = calendar2.get(Calendar.DAY_OF_WEEK);

                    switch (day) {
                        case Calendar.SUNDAY:
                            currentday = "Sunday";
                            break;
                        case Calendar.MONDAY:
                            currentday = "Monday";
                            break;
                        case Calendar.TUESDAY:
                            currentday = "Tuesday";
                            break;
                        case Calendar.WEDNESDAY:
                            currentday = "Wednesday";
                            break;
                        case Calendar.THURSDAY:
                            currentday = "Thursday";
                            break;
                        case Calendar.FRIDAY:
                            currentday = "Friday";
                            break;
                        case Calendar.SATURDAY:
                            currentday = "Saturday";
                            break;
                    }

                    Date currentTime = Calendar.getInstance().getTime();

//                          String timenow1 =dateFormat.format(currentTime);


                    String timenow1 ="4:01PM";
                    timenow = dateFormat.parse(timenow1);

                } catch (ParseException e) {
                    Toast.makeText(doctor_homepage.this, "error getting time", Toast.LENGTH_SHORT).show();
                }


                checkschedcurrent();


            }
        };
        registerReceiver(minuteUpdateReceiver, intentFilter);

    }
    private void checkschedcurrent() {


        db.collection("DoctorSchedules").whereEqualTo(currentday,true).whereEqualTo("DocId", preferenceManager.getString(Constants.KEY_USER_ID)).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    if (!task.getResult().isEmpty()){
                        String timestart = "none", timestop = "none";
                        for (QueryDocumentSnapshot document : task.getResult()) {

                            String time1 = document.getString("StartTime");
                            String time2 = document.getString("EndTime");
                            Date d1= new Date(), d2= new Date();
                            SimpleDateFormat dateFormat = new SimpleDateFormat("h:mm aa");

                            try {
                                d1 = dateFormat.parse(time1);
                                d2= dateFormat.parse(time2);
                            } catch (ParseException e) {
                                Toast.makeText(doctor_homepage.this, "errorsettingtime", Toast.LENGTH_SHORT).show();

                            }
                            if (timenow.after(d1) && timenow.before(d2) || timenow.equals(d2)) {
                                timestart=time1;
                                timestop=time2;

                            }

                        }
                        String finalTimestart1 = timestart;
                        String finalTimestop1 = timestop;
                        String finalTimestart2 = timestart;
                        String finalTimestop2 = timestop;


                        db.collection("Schedules").whereEqualTo("Date", DDate).whereEqualTo("StartTime", timestart).whereEqualTo("DoctorUId", preferenceManager.getString(Constants.KEY_USER_ID)).whereEqualTo("EndTime", timestop).whereIn("Status", Arrays.asList("Paid","Approved"))
                                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                                    @Override
                                    public void onEvent(@Nullable QuerySnapshot value,
                                                        @Nullable FirebaseFirestoreException e) {
                                        if (e != null) {
                                            Toast.makeText(doctor_homepage.this, "error listening", Toast.LENGTH_SHORT).show();
                                            return;
                                        }


                                        if (!value.isEmpty()){
                                            int count;
                                            count = value.size();
                                            noofappoints.setText("Total Appointments: "+count);
                                        }
                                        else {
                                            noofappoints.setText("Total Appointments: None");
                                            }
                                    }
                                });
                        db.collection("Schedules").whereEqualTo("DoctorUId",preferenceManager.getString(Constants.KEY_USER_ID)).whereIn("Status", Arrays.asList("Paid","Approved")).whereEqualTo("Date",DDate).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()){
                                    QuerySnapshot querySnapshot = task.getResult();
                                    if (!querySnapshot.isEmpty()){
                                        for (QueryDocumentSnapshot document : task.getResult()) {
                                            SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm aa");
                                            Date times = new Date();
                                            try {
                                                String endtime = document.getString("EndTime");
                                                times = dateFormat.parse(endtime);
                                                if (timenow.after(times)){
                                                    String documentsched =document.getId();
                                                    Date currentTime = Calendar.getInstance().getTime();
                                                    db.collection("Schedules").document(documentsched).update("Status","Unattended", "Dnt", currentTime).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {

                                                        }
                                                    });
                                                }
                                            } catch (ParseException e) {
                                                Toast.makeText(doctor_homepage.this, "error time", Toast.LENGTH_SHORT).show();
                                            }

                                        }
                                    }
                                }  else {

                                }
                            }
                        });
                        db.collection("Schedules").whereEqualTo("DoctorUId",preferenceManager.getString(Constants.KEY_USER_ID)).whereLessThan("Date",DDate).whereIn("Status", Arrays.asList("Paid","Approved")).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()){
                                    QuerySnapshot querySnapshot = task.getResult();
                                    if (!querySnapshot.isEmpty()){
                                        for (QueryDocumentSnapshot document : task.getResult()) {
                                            String documentsched =document.getId();
                                            Date currentTime = Calendar.getInstance().getTime();
                                            db.collection("Schedules").document(documentsched).update("Status","Unattended","Dnt", currentTime).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {

                                                }
                                            });

                                        }
                                    }
                                }
                            else {

                                }}
                        });
                        db.collection("Schedules").whereEqualTo("Date", DDate).whereEqualTo("StartTime", timestart).whereEqualTo("DoctorUId", preferenceManager.getString(Constants.KEY_USER_ID)).whereEqualTo("Status", "Pending Approval").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()){
                                    QuerySnapshot querySnapshot = task.getResult();
                                if (!querySnapshot.isEmpty()) {

                                        for (QueryDocumentSnapshot doc : task.getResult()){
                                            int position = doc.getLong("Position").intValue();
                                            Date currentTime = Calendar.getInstance().getTime();

                                            db.collection("Schedules").document(doc.getId()).update("Status","Declined","Dnt",currentTime,"Position",0).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {

                                                    db.collection("Schedules").whereEqualTo("Date", DDate).whereEqualTo("StartTime", finalTimestart2).whereEqualTo("DoctorUId", preferenceManager.getString(Constants.KEY_USER_ID)).whereEqualTo("EndTime", finalTimestop2).whereIn("Status", Arrays.asList("Paid","Approved")).get()
                                                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                                    if (task.isSuccessful()) {
                                                                        QuerySnapshot querySnapshot = task.getResult();
                                                                        if (!querySnapshot.isEmpty()) {

                                                                            for (QueryDocumentSnapshot doc : task.getResult()) {
                                                                                int docposition = doc.getLong("Position").intValue();
                                                                                if (docposition > 1 && docposition >position) {
                                                                                    String docuid = doc.getId();
                                                                                    db.collection("Schedules").document(docuid).update("Position", docposition - 1)
                                                                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                                @Override
                                                                                                public void onSuccess(Void aVoid) {


                                                                                                }
                                                                                            });
                                                                                }
                                                                            }
                                                                        }   }
                                                                }
                                                            });
                                                }
                                            });
                                        }
                                    }
                                }
                            }
                        });
                        Query query =  db.collection("Schedules").whereEqualTo("Date", DDate).whereEqualTo("StartTime", timestart).whereEqualTo("DoctorUId", preferenceManager.getString(Constants.KEY_USER_ID)).whereIn("Status", Arrays.asList("Paid","Approved")).whereNotEqualTo("Position", 1  ).orderBy("Position", Query.Direction.ASCENDING);
                        FirestoreRecyclerOptions<DocTodaySchedModel> options = new FirestoreRecyclerOptions.Builder<DocTodaySchedModel>()
                                .setQuery(query, DocTodaySchedModel.class)
                                .build();

                        adapter = new FirestoreRecyclerAdapter<DocTodaySchedModel, SchedHolder>(options) {
                            @NonNull
                            @Override
                            public SchedHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.doctor_singlesched,parent,false);
                                return new SchedHolder(view);
                            }

                            @Override
                            protected void onBindViewHolder(@NonNull SchedHolder holder, int position, @NonNull DocTodaySchedModel model) {
                                db.collection("Patients").document(model.PatientUId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        if (task.isSuccessful()) {
                                            DocumentSnapshot document = task.getResult();
                                            if (document.exists()) {
                                                holder.tv_patientnamelist.setText(document.getString("LastName")+", "+document.getString("FirstName"));
                                                holder.tv_positionlist.setText("Position: " + model.Position+"");
                                                holder.btnViewRecord.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View view) {
                                                        Intent intent = new Intent(doctor_homepage.this,patientrec_sec.class);
                                                        intent.putExtra("patid",model.getPatientUId());
                                                        startActivity(intent);
                                                    }
                                                });
                                            } else {
                                                Toast.makeText(doctor_homepage.this, "document does not exist", Toast.LENGTH_SHORT).show();
                                            }
                                        } else {

                                        }
                                    }
                                });

                            }
                        };

                        mFirestorelist.setHasFixedSize(true);
                        mFirestorelist.setLayoutManager(new LinearLayoutManager(doctor_homepage.this));
                        mFirestorelist.setAdapter(adapter);
                        adapter.stopListening();
                        adapter.startListening();


                        String finalTimestart = timestart;
                        String finalTimestop = timestop;


                        db.collection("Schedules").whereEqualTo("Date", DDate).whereEqualTo("StartTime", timestart).whereEqualTo("DoctorUId", preferenceManager.getString(Constants.KEY_USER_ID)).whereEqualTo("EndTime", timestop).whereIn("Status", Arrays.asList("Paid", "Completed","Approved")).whereEqualTo("Position",1)
                                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                                    @Override
                                    public void onEvent(@Nullable QuerySnapshot value,
                                                        @Nullable FirebaseFirestoreException e) {
                                        if (e != null) {
                                            Toast.makeText(doctor_homepage.this, "error listening", Toast.LENGTH_SHORT).show();
                                            return;
                                        }

                                        List<String> pats = new ArrayList<>();
                                        if (value.isEmpty()){
                                            patnametv.setText("You have no current appointments");
                                            schedtimetv.setText("");
                                            callbtn.setVisibility(View.INVISIBLE);
                                            pat_record.setVisibility(View.INVISIBLE);
                                            btncomplete.setVisibility(View.INVISIBLE);
                                            btnnext.setVisibility(View.INVISIBLE);
                                        }
                                        else {
                                            for (QueryDocumentSnapshot doc : value) {
                                                String docid = doc.getId();
                                                patUid = doc.getString("PatientUId");

                                                db.collection("Patients").document(patUid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                        if (task.isSuccessful()) {
                                                            DocumentSnapshot document1 = task.getResult();
                                                            {
                                                                if (document1.exists()) {
                                                                    gmail = document1.getString("Email");
                                                                    patnametv.setText(document1.getString("LastName") + ", " + document1.getString("FirstName"));
                                                                    schedtimetv.setText("Time: " + doc.getString("StartTime") + " - " + doc.getString("EndTime"));
                                                                    callbtn.setVisibility(View.VISIBLE);
                                                                    pat_record.setVisibility(View.VISIBLE);
                                                                    GlobalVariables gv = (GlobalVariables) getApplicationContext();
                                                                    gv.setSDtimestart(doc.getString("TimeStart"));
                                                                    gv.setSDDate(datenow);
                                                                    gv.setSDid(doc.getId());
                                                                    btncomplete.setVisibility(View.VISIBLE);
                                                                    btnnext.setVisibility(View.VISIBLE);

                                                                    btncomplete.setOnClickListener(new View.OnClickListener() {
                                                                        @Override
                                                                        public void onClick(View view) {

                                                                            new AlertDialog.Builder(doctor_homepage.this).setMessage("Are you sure the appointment is completed?").setPositiveButton("Accept", new DialogInterface.OnClickListener() {
                                                                                @Override
                                                                                public void onClick(DialogInterface dialog, int which) {
                                                                                    Date currentTime = Calendar.getInstance().getTime();
                                                                                    db.collection("Schedules").document(docid).update(
                                                                                            "Status", "Completed",
                                                                                            "Dnt", currentTime,
                                                                                            "Position",0

                                                                                    ).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                        @Override
                                                                                        public void onSuccess(Void aVoid) {
                                                                                            db.collection("Schedules").whereEqualTo("DoctorUId",preferenceManager.getString(Constants.KEY_USER_ID)).whereEqualTo("StartTime", finalTimestart).whereEqualTo("EndTime", finalTimestop).whereIn("Status",Arrays.asList("Paid","Approved")).whereEqualTo("Date", DDate).get()
                                                                                                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                                                                        @Override
                                                                                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                                                                            if (task.isSuccessful()) {

                                                                                                                for (QueryDocumentSnapshot doc : task.getResult()) {
                                                                                                                    int docposition = doc.getLong("Position").intValue();
                                                                                                                    if (docposition > 1) {
                                                                                                                        String docuid = doc.getId();
                                                                                                                        db.collection("Schedules").document(docuid).update("Position", docposition - 1)
                                                                                                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                                                                    @Override
                                                                                                                                    public void onSuccess(Void aVoid) {


                                                                                                                                    }
                                                                                                                                });
                                                                                                                    }
                                                                                                                }
                                                                                                            }
                                                                                                        }
                                                                                                    });
                                                                                        }
                                                                                    });
                                                                                }
                                                                            }).setNegativeButton("Decline", new DialogInterface.OnClickListener() {
                                                                                @Override
                                                                                public void onClick(DialogInterface dialog, int which) {

                                                                                }
                                                                            }).show();


                                                                        }
                                                                    });
                                                                    btnnext.setOnClickListener(new View.OnClickListener() {
                                                                        @Override
                                                                        public void onClick(View view) {
                                                                            new AlertDialog.Builder(doctor_homepage.this).setMessage("Are you sure you would like to move this patient?").setPositiveButton("Accept", new DialogInterface.OnClickListener() {
                                                                                @Override
                                                                                public void onClick(DialogInterface dialog, int which) {
                                                                                    db.collection("Schedules").whereEqualTo("DoctorUId",preferenceManager.getString(Constants.KEY_USER_ID)).whereEqualTo("StartTime", finalTimestart).whereEqualTo("EndTime", finalTimestop).whereIn("Status",Arrays.asList("Paid","Approved")).whereEqualTo("Date", DDate).get()
                                                                                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                                                                @Override
                                                                                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                                                                    if (task.isSuccessful()) {

                                                                                                        for (QueryDocumentSnapshot doc : task.getResult()) {
                                                                                                            int docposition = doc.getLong("Position").intValue();
                                                                                                            if (docposition > 1) {
                                                                                                                String docuid = doc.getId();
                                                                                                                db.collection("Schedules").document(docuid).update("Position", docposition - 1)
                                                                                                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                                                            @Override
                                                                                                                            public void onSuccess(Void aVoid) {
                                                                                                                                db.collection("Schedules").whereEqualTo("DoctorUId",preferenceManager.getString(Constants.KEY_USER_ID)).whereEqualTo("StartTime", finalTimestart).whereEqualTo("EndTime", finalTimestop).whereIn("Status", Arrays.asList("Paid","Completed","Approved")).whereEqualTo("Date", DDate).get()
                                                                                                                                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                                                                                                            @Override
                                                                                                                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                                                                                                                if (task.isSuccessful()) {

                                                                                                                                                    int count = task.getResult().size();
                                                                                                                                                    db.collection("Schedules").document(docid).update(
                                                                                                                                                            "Position",count

                                                                                                                                                    ).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                                                                                        @Override
                                                                                                                                                        public void onSuccess(Void aVoid) {

                                                                                                                                                        }
                                                                                                                                                    });
                                                                                                                                                }
                                                                                                                                            }
                                                                                                                                        });


                                                                                                                            }
                                                                                                                        });
                                                                                                            }
                                                                                                        }
                                                                                                    }
                                                                                                }
                                                                                            });
                                                                                }
                                                                            }).setNegativeButton("Decline", new DialogInterface.OnClickListener() {
                                                                                @Override
                                                                                public void onClick(DialogInterface dialog, int which) {

                                                                                }
                                                                            }).show();


                                                                        }


                                                                    });


                                                                }

                                                            }

                                                        }
                                                    }
                                                });
                                            }}
                                    }
                                });



                    }
                    else {

                        patnametv.setText("You don't have a schedule.");
                        schedtimetv.setText("");
                        callbtn.setVisibility(View.INVISIBLE);
                        pat_record.setVisibility(View.INVISIBLE);
                        btncomplete.setVisibility(View.INVISIBLE);
                        btnnext.setVisibility(View.INVISIBLE);

                    }

                }

            }

        });




    }


    private void sendFCMTokenToDatabase(String token) {
        DocumentReference documentReference = db.collection("Doctors").document(
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
                        Toast.makeText(doctor_homepage.this, "Unable to send token: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_logout_doc:
                Toast.makeText(this, "Signing Out...", Toast.LENGTH_SHORT).show();
                FirebaseFirestore database = FirebaseFirestore.getInstance();
                DocumentReference documentReference =
                        db.collection(Constants.KEY_COLLECTION_DOCTOR).document(
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
                                Toast.makeText(doctor_homepage.this, "Unable to sign out", Toast.LENGTH_SHORT).show();
                            }
                        });
                break;
            case R.id.btn_patientrecord:
                Intent intent = new Intent(getApplicationContext(), patient_record_clinic.class);
                startActivity(intent);

                break;

            case R.id.nav_profile_dodc:

                Intent intent1 = new Intent(getApplicationContext(), doctor_viewporfile.class);
                startActivity(intent1);

                break;

            case R.id.btn_upcoming:
                Intent intent2 = new Intent(getApplicationContext(), Doctor_schedlist_upcoming.class);
                startActivity(intent2);

                break;
            case R.id.btn_appointment:
                Intent intent3 = new Intent(getApplicationContext(), Doctor_schedlist_pastsched.class);
                startActivity(intent3);

                break;
        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void updateNavHeader () {
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_viewer_doc);
        View headerView = navigationView.getHeaderView(0);
        TextView navUsername = headerView.findViewById(R.id.nav_header_name);
        TextView navEmail = headerView.findViewById(R.id.nav_header_email);
        CircleImageView profpicturedoc = headerView.findViewById(R.id.profile_picture);

        DocumentReference documentReference = fStore.collection("Doctors").document(userId);
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                navUsername.setText(documentSnapshot.getString("FirstName") + " " + documentSnapshot.getString("LastName"));
                navEmail.setText(documentSnapshot.getString("Email"));
            }
        });

        db.collection("Doctors").whereEqualTo("StorageId", userId).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    QuerySnapshot querySnapshot = task.getResult();
                    if (!querySnapshot.isEmpty()) {
                        for (QueryDocumentSnapshot profile : task.getResult()) {
                            image = profile.getString("StorageId");
                            storageReference = FirebaseStorage.getInstance().getReference("DoctorPicture/" + image);
                            try {
                                File local = File.createTempFile("myProfilePicture","");
                                storageReference.getFile(local).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                        getpic = BitmapFactory.decodeFile(local.getAbsolutePath());
                                        profpicturedoc.setImageBitmap(getpic);
                                    }
                                });
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
        });


    }


    private class SchedHolder extends  RecyclerView.ViewHolder {

        private TextView tv_patientnamelist, tv_positionlist;
        private CircleImageView btnViewRecord;
        public SchedHolder(@NonNull View itemView) {
            super(itemView);
            tv_patientnamelist = (TextView) itemView.findViewById(R.id.notif_pat);
            tv_positionlist = (TextView) itemView.findViewById(R.id.notif_date);
            btnViewRecord = (CircleImageView) itemView.findViewById(R.id.view_patInfo);


        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        startMinuteUpdater();
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(minuteUpdateReceiver);
    }
}