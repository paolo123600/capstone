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
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
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

import java.io.File;
import java.io.IOException;
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

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
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
    Date DDate ;
    String datenow;
    String userId;
    Boolean schedalready = false;
   private String start = "";
    private String lstart = "";
    private  String lend = "";
    private String end = "";
    TextView docname, patienttime, position;
    LinearLayout logo, schedstats;
    private String document_id_reference;

    private StorageReference storageReference;
    private FirebaseStorage storage;
    StorageReference ref;
    String image;
    Bitmap getpic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

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
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_viewer_pat);
        navigationView.setNavigationItemSelectedListener(this);

        updateNavHeader();

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat format = new SimpleDateFormat("MMMM d ,yyyy");
        DDate = calendar.getTime();
        datenow = format.format(DDate);
        try {
            DDate = format.parse(datenow);
        } catch (ParseException e) {
            Toast.makeText(this, "error", Toast.LENGTH_SHORT).show();
        }


        db.collection("Schedules").whereEqualTo("PatientUId", patuid).whereEqualTo("Status","Paid").whereEqualTo("Date", DDate).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Toast.makeText(MainActivity.this, "error listening", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (value.isEmpty()) {
                    schedstats.setVisibility(LinearLayout.GONE);
                    logo.setVisibility(LinearLayout.VISIBLE);
                }
                else {
                    schedstats.setVisibility(LinearLayout.VISIBLE);
                    logo.setVisibility(LinearLayout.GONE);
                    for (QueryDocumentSnapshot doc : value) {
                        db.collection("Doctors").document(doc.getString("DoctorUId"))
                                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if(task.isSuccessful()){
                                    DocumentSnapshot document = task.getResult();
                                    docname.setText(document.getString("LastName"));
                                }
                            }
                        });
                        patienttime.setText(doc.getString("StartTime") + " - " +doc.getString("EndTime"));
                        position.setText(doc.get("Position") + "");

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
                db.collection("Schedules").whereEqualTo("PatientUId",patuid).whereIn("Status", Arrays.asList("Paid","Pending Approval","Approved"))
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

                                        if(doc.getString("Status").equals("Paid")||doc.getString("Status").equals("Approved")){
                                            Toast.makeText(MainActivity.this, "You already have an appointment.", Toast.LENGTH_SHORT).show();
                                        }
                                        else if(doc.getString("Status").equals("Pending Approval")){
                                            Toast.makeText(MainActivity.this, "Your appointment is currently under review.", Toast.LENGTH_SHORT).show();
                                        }
                                        schedalready=true;

                                    }
                                    else if (nowdate.after(datesched)) {  String documentsched =doc.getId();
                                    db.collection("Schedules").document(documentsched).update("Status","Unattended").addOnSuccessListener(new OnSuccessListener<Void>() {
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
            Intent startMain = new Intent(Intent.ACTION_MAIN);
            startMain.addCategory(Intent.CATEGORY_HOME);
            startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(startMain);
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
        CircleImageView profpicture = headerView.findViewById(R.id.profile_picture);



        DocumentReference documentReference = db.collection("Patients").document(userId);
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                navUsernamePat.setText(documentSnapshot.getString("FirstName") + " " + documentSnapshot.getString("LastName"));
                navEmail.setText(documentSnapshot.getString("Email"));
            }

        }




        );

        db.collection("Patients").whereEqualTo("StorageId", userId).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    QuerySnapshot querySnapshot = task.getResult();
                    if (!querySnapshot.isEmpty()) {
                        for (QueryDocumentSnapshot profile : task.getResult()) {
                            image = profile.getString("StorageId");
                            storageReference = FirebaseStorage.getInstance().getReference("PatientPicture/" + image);
                            try {
                                File local = File.createTempFile("myProfilePicture","");
                                storageReference.getFile(local).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                        getpic = BitmapFactory.decodeFile(local.getAbsolutePath());
                                        profpicture.setImageBitmap(getpic);
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
}
