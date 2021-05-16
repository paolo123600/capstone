package com.example.capstone;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.capstone.activities.OutgoingInvitationActivity;
import com.example.capstone.models.User;
import com.example.capstone.utilities.Constants;
import com.example.capstone.utilities.PreferenceManager;
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

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class  doctor_homepage extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    Button btn_dochat;
    FirebaseFirestore db;
    private Button callbtn;
    private Button  pat_record;
    String gmail = "";
    String patUid = "";
    private List<User> users;
    private PreferenceManager preferenceManager;
    String datenow;
    Date timenow;
    TextView patnametv, schedtimetv;

    private DrawerLayout drawer;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    String userId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.doctor_home);
        db = FirebaseFirestore.getInstance();
        preferenceManager = new PreferenceManager(getApplicationContext());
        btn_dochat = (Button) findViewById(R.id.btn_chat_dochome);
        Calendar calendar = Calendar.getInstance();
        datenow = DateFormat.getDateInstance().format(calendar.getTime());

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

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        updateNavHeader();


        patnametv = (TextView) findViewById(R.id.tvPatname);
        schedtimetv = (TextView) findViewById(R.id.tvSched);
        SimpleDateFormat dateFormat = new SimpleDateFormat("h:mmaa");

        try {
            Date currentTime = Calendar.getInstance().getTime();
//            String timenow1 =dateFormat.format(currentTime);

            String timenow1 = "4:50PM";
            timenow = dateFormat.parse(timenow1);

        } catch (ParseException e) {
            Toast.makeText(doctor_homepage.this, "error getting time", Toast.LENGTH_SHORT).show();
        }


        checkschedcurrent();

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

    private void checkschedcurrent() {


        String[] time = {"8:00AM","8:30AM","9:00AM","9:30AM","10:00AM","10:30AM","11:00AM","11:30AM","12:00PM","12:30PM","1:00PM","1:30PM","2:00PM","2:30PM","3:00PM","3:30PM","4:00PM","4:30PM","5:00PM","5:30PM","6:00PM","6:30PM","7:00PM","7:30PM","8:00PM","8:30PM"};
        String[] btntext = {"8:00-8:30AM","8:30-9:00AM","9:00-9:30AM","9:30-10:00AM","10:00-10:30AM","10:30-11:00AM","11:00-11:30AM","11:30-12:00PM","12:00-12:30PM","12:30-1:00PM","1:00-1:30PM","1:30-2:00PM","2:00-2:30PM","2:30-3:00PM","3:00-3:30PM","3:30-4:00PM","4:00-4:30PM","4:30-5:00PM","5:00-5:30PM","5:30-6:00PM","6:00-6:30PM","6:30-7:00PM","7:00-7:30PM","7:30-8:00PM"};
        //initializing time
        Date d1= new Date(), d2= new Date();
        String timestart = "", timestop = "";
        SimpleDateFormat dateFormat = new SimpleDateFormat("h:mmaa");
        //converting time
        for (int j=0 ; j<time.length-1 ; j++){
            try {
                d1 = dateFormat.parse(time[j]);
                d2= dateFormat.parse(time[j+1]);
            } catch (ParseException e) {
                Toast.makeText(doctor_homepage.this, "errorsettingtime", Toast.LENGTH_SHORT).show();

            }
            if (timenow.after(d1) && timenow.before(d2) || timenow.equals(d2)) {
                timestart=time[j];
                timestop=time[j+1];
            }
        }

        Toast.makeText(this, "timestart: "+timestart+"timestop: "+timestop, Toast.LENGTH_SHORT).show();


        db.collection("Schedule").whereEqualTo("SchedDate", datenow).whereEqualTo("TimeStart", timestart).whereEqualTo("DoctorUId", preferenceManager.getString(Constants.KEY_USER_ID)).whereEqualTo("TimeStop", timestop).whereIn("Status", Arrays.asList("Paid", "Completed"))
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    QuerySnapshot querySnapshot = task.getResult();
                    if (querySnapshot.isEmpty()) {
                        Toast.makeText(doctor_homepage.this, "No current", Toast.LENGTH_SHORT).show();
                    } else {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            patUid = document.getString("PatientUId");

                            db.collection("Patients").document(patUid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if (task.isSuccessful()) {
                                        DocumentSnapshot document1 = task.getResult();
                                        {
                                            if (document1.exists()) {
                                                gmail = document1.getString("Email");
                                                patnametv.setText(document1.getString("LastName") + ", " + document1.getString("FirstName"));
                                                schedtimetv.setText("Time: " + document.getString("TimeStart") + " - " + document.getString("TimeStop"));
                                                callbtn.setVisibility(View.VISIBLE);
                                                pat_record.setVisibility(View.VISIBLE);
                                                GlobalVariables gv = (GlobalVariables) getApplicationContext();
                                                gv.setSDtimestart(document.getString("TimeStart"));
                                                gv.setSDDate(datenow);
                                            }

                                        }

                                    }
                                }
                            });
                        }
                    }


                } else {

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
            case R.id.btn_appointment:

                break;
            case R.id.btn_patientrecord:
                Intent intent = new Intent(getApplicationContext(), patientrec_sec.class);
                startActivity(intent);
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

        DocumentReference documentReference = fStore.collection("Doctors").document(userId);
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                navUsername.setText(documentSnapshot.getString("FirstName") + " " + documentSnapshot.getString("LastName"));
                navEmail.setText(documentSnapshot.getString("Email"));
            }
        });


    }
}