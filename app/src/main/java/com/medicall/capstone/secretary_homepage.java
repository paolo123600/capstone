package com.medicall.capstone;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.medicall.capstone.R;

import com.medicall.capstone.secretary.Secretary_schedlist;
import com.medicall.capstone.utilities.Constants;
import com.medicall.capstone.utilities.PreferenceManager;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class  secretary_homepage extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    private LinearLayout Patrec_button;
    private ImageView notifbtn;
    private LinearLayout chatbtn;
    LinearLayout managesched;
    Button bottomchat;
    CardView notifcontainer;
    LinearLayout appointment_btn;
    TextView notifcount;
    TextView pendingcount;
    CardView pendingcontainer;

    private PreferenceManager preferenceManager;
    FirebaseFirestore db;

    private DrawerLayout drawer;



    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    String userId;
    FirebaseUser currentuser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_secretary_homepage);

        notifbtn = (ImageView) findViewById(R.id.notif);
        chatbtn = findViewById(R.id.secretary_chat_btn);
        managesched = findViewById(R.id.manage_schedule_button);
        bottomchat = findViewById(R.id.sec_bottom_chat);
        notifcount = (TextView) findViewById(R.id.bellcount);
        notifcontainer = (CardView) findViewById(R.id.notificationNumberContainer);
        pendingcount = (TextView) findViewById(R.id.pendingcount);
        pendingcontainer = (CardView) findViewById(R.id.PendingContainer);


        appointment_btn = findViewById(R.id.appointment_button);

        db = FirebaseFirestore.getInstance();

        preferenceManager = new PreferenceManager(getApplicationContext());

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        currentuser = fAuth.getCurrentUser();

        userId = fAuth.getCurrentUser().getUid();

        ////////////



        ///////////
        Patrec_button = findViewById(R.id.notification_button);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        drawer = findViewById(R.id.drawer_layout_sec);
        NavigationView navigationView = findViewById(R.id.nav_viewer_sec);
        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        updateNavHeader();

        appointment_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(secretary_homepage.this, Pending_Appointments.class);
                startActivity(intent);
            }
        });

        managesched.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), docshed_page1.class);
                startActivity(intent);
            }
        });

        notifbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(secretary_homepage.this,notification.class );
                startActivity(intent);
            }
        });
        chatbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(secretary_homepage.this, Secretary_schedlist.class);
                startActivity(intent);
            }
        });

        bottomchat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(secretary_homepage.this, RecentChatSecretary.class);
                startActivity(intent);
            }
        });


        Patrec_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), patient_record_clinic.class);
                startActivity(intent);

            }
        });

        DocumentReference documentReference = fStore.collection("Secretary").document(userId);
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
            String clinicname=  documentSnapshot.getString("ClinicName");

                db.collection("Notification").whereEqualTo("ClinicName", clinicname).whereEqualTo("Seen",false)
                        .addSnapshotListener(new EventListener<QuerySnapshot>() {
                            @Override
                            public void onEvent(@Nullable QuerySnapshot value,
                                                @Nullable FirebaseFirestoreException e) {
                                if (e != null) {
                                    Toast.makeText(secretary_homepage.this, "error listening", Toast.LENGTH_SHORT).show();
                                    return;
                                }


                                if (!value.isEmpty()){
                                    int count;
                                    count = value.size();

                                    notifcount.setVisibility(View.VISIBLE);
                                    notifcontainer.setVisibility(View.VISIBLE);
                                    if (value.size() > 99){
                                        notifcount.setText("99");
                                    }
                                    else {  notifcount.setText(""+count);}

                                }

                                else {
                                    notifcount.setVisibility(View.GONE);
                                    notifcontainer.setVisibility(View.GONE);
                                }
                            }
                        });

                db.collection("Schedules").whereEqualTo("ClinicName", clinicname).whereEqualTo("Status","Pending Approval")
                        .addSnapshotListener(new EventListener<QuerySnapshot>() {
                            @Override
                            public void onEvent(@Nullable QuerySnapshot value,
                                                @Nullable FirebaseFirestoreException e) {
                                if (e != null) {
                                    Toast.makeText(secretary_homepage.this, "error listening", Toast.LENGTH_SHORT).show();
                                    return;
                                }


                                if (!value.isEmpty()){
                                    int count;
                                    count = value.size();

                                   pendingcount.setVisibility(View.VISIBLE);
                                    pendingcontainer.setVisibility(View.VISIBLE);
                                    if (value.size() > 99){
                                        pendingcount.setText("99");
                                    }
                                    else {  pendingcount.setText(""+count);}

                                }

                                else {
                                    pendingcount.setVisibility(View.GONE);
                                    pendingcontainer.setVisibility(View.GONE);
                                }
                            }
                        });


            }
        });

    }
    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)){
            drawer.closeDrawer(GravityCompat.START);
        }else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){

            case R.id.nav_logout_sec:
                Toast.makeText(this, "Signing Out...", Toast.LENGTH_SHORT).show();
                FirebaseFirestore database = FirebaseFirestore.getInstance();
                DocumentReference documentReference =
                        db.collection(Constants.KEY_COLLECTION_SECRETARY).document(
                                preferenceManager.getString(Constants.KEY_USER_ID)
                        );
                HashMap<String, Object> updates = new HashMap<>();
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
                                Toast.makeText(secretary_homepage.this, "Unable to sign out", Toast.LENGTH_SHORT).show();
                            }
                        });
                break;

            case  R.id.nav_change_email_sec:
                Intent intent = new Intent(getApplicationContext(), changeEmail_Secretary.class);
                startActivity(intent);
                break;

            case  R.id.nav_change_picture_sec:
                Intent intent2 = new Intent(getApplicationContext(), Secretary_ChangePicture.class);
                startActivity(intent2);
                break;
        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void updateNavHeader () {
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_viewer_sec);
        View headerView = navigationView.getHeaderView(0);
        TextView navUsername = headerView.findViewById(R.id.nav_header_name);
        TextView navEmail = headerView.findViewById(R.id.nav_header_email);

       DocumentReference documentReference = fStore.collection("Secretary").document(userId);
       documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
           @Override
           public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
               navUsername.setText(documentSnapshot.getString("ClinicName"));
               navEmail.setText(documentSnapshot.getString("Email"));



           }
       });


    }
}