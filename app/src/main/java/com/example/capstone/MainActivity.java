package com.example.capstone;

import androidx.annotation.NonNull;
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
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
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
    String datenow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = FirebaseFirestore.getInstance();

        preferenceManager = new PreferenceManager(getApplicationContext());

        chat = findViewById(R.id.chat);
        buttonbook = (Button) findViewById(R.id.booknow);
        buttonsched = (Button) findViewById(R.id.schedules);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_viewer);

        navigationView.setNavigationItemSelectedListener(this);


        chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

        Intent intent = new Intent(MainActivity.this, Clinic_view.class);
        startActivity(intent);
//                Intent intent = new Intent(MainActivity.this, RecentChats.class);
//                startActivity(intent);
            }
        });

        buttonbook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(MainActivity.this,Doctor_Patientchatlist.class);
                startActivity(intent);
//                Calendar calendar = Calendar.getInstance();
//                datenow = DateFormat.getDateInstance().format(calendar.getTime());
//                Toast.makeText(MainActivity.this, datenow, Toast.LENGTH_SHORT).show();
//               createSelectDoctorDialog();
            }
        });

        buttonsched.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, VideoCall_Main.class);
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
                R.string.nagiation_drawer_open, R.string.nagiation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();


        navigationView.setCheckedItem(R.id.nav_patienthome);
    }

    @Override
    public void onDateSet(DatePicker datePicker, int year, int month, int dayOfMonth) {
        Date nowdate = new Date() , currentdate  = new Date() ;
        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, month);
        c.set(Calendar.DAY_OF_MONTH,dayOfMonth);
        String currentDateString = DateFormat.getDateInstance().format(c.getTime());
        Toast.makeText(MainActivity.this, currentDateString, Toast.LENGTH_SHORT).show();
        GlobalVariables gv = (GlobalVariables) getApplicationContext();
        gv.setSDDate(currentDateString);
        SimpleDateFormat format = new SimpleDateFormat("MMMMM d,yyyy");
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


        if (currentdate.before(nowdate)){
            Toast.makeText(MainActivity.this, "please select a valid date", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
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
        final View selectDateView = getLayoutInflater().inflate(R.layout.popupselectdate,null);







        dialogbuilder.setView(selectDateView);
        dialog= dialogbuilder.create();
        dialog.show();
        dialog.getWindow().setLayout(1200, 2000);

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
                Query query = db.collection("Doctors").whereEqualTo("Clinic",Clinicname);
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
                                gv.setSDClinic(Clinicname);
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
        dialog.getWindow().setLayout(1200, 2000);




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


}


