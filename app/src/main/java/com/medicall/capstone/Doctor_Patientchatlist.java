package com.medicall.capstone;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.medicall.capstone.R;

import com.medicall.capstone.utilities.PreferenceManager;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.mancj.materialsearchbar.MaterialSearchBar;

import java.util.ArrayList;
import java.util.List;

public class Doctor_Patientchatlist extends AppCompatActivity {
    private   String clinicname;
private RecyclerView patientlist;
private FirestoreRecyclerAdapter adapter;
private RecyclerView.Adapter adapter2;
MaterialSearchBar materialSearchBar;
private PreferenceManager preferenceManager;
FirebaseFirestore db;
String txt;
String clinicid;

ImageView back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor__patientchatlist);
        preferenceManager = new PreferenceManager(getApplicationContext());
            clinicname = preferenceManager.getString("ClinicName");
        db=FirebaseFirestore.getInstance();
        materialSearchBar = (MaterialSearchBar) findViewById(R.id.searchBar);
        materialSearchBar.setCardViewElevation(10);
        back = findViewById(R.id.backspace);

        getpatient();

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), RecentChatDoc.class);
                startActivity(intent);
            }
        });

        materialSearchBar.addTextChangeListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence text, int i, int i1, int i2) {
                txt=text.toString();
                if (txt.isEmpty()) {
                    adapter.stopListening();
                    getpatient();
                }
                else{
                    txt= txt.substring(0,1).toUpperCase()+txt.substring(1).toLowerCase();
                    startsearchpatient(txt.toString());


                }


            }

            @Override
            public void afterTextChanged(Editable editable) {



            }
        });





    }

    private void startsearchpatient(String text) {

        adapter.stopListening();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        patientlist = (RecyclerView) findViewById(R.id.recyclerViewpat);
        List<String> Patients = new ArrayList<>();
        db.collection("Clinics").whereEqualTo("ClinicName",clinicname).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    if (!task.getResult().isEmpty()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            clinicid = document.getId();
                            db.collection("Clinics").document(clinicid).collection("Patients").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful()) {
                                        if (!task.getResult().isEmpty()) {
                                            for (QueryDocumentSnapshot document : task.getResult()) {
                                                String patuid = document.getString("PatUId");

                                                Patients.add(patuid);
                                            }

                                            Query query = db.collection("Patients").whereIn("UserId", Patients).orderBy("LastName").startAt(text).endAt(text + '\uf8ff');
                                            FirestoreRecyclerOptions<PatientModel> options = new FirestoreRecyclerOptions.Builder<PatientModel>()
                                                    .setQuery(query, PatientModel.class)
                                                    .build();

                                            adapter = new FirestoreRecyclerAdapter<PatientModel, PatientsViewHolder>(options) {

                                                @NonNull
                                                @Override
                                                public PatientsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                                                    View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.patient_list_single, parent, false);
                                                    return new PatientsViewHolder(view);
                                                }

                                                @Override
                                                protected void onBindViewHolder(@NonNull PatientsViewHolder holder, int position, @NonNull PatientModel model) {
                                                    holder.list_patname.setText(model.getLastName() + ", " + model.getFirstName());
                                                    holder.list_patemail.setText(model.getEmail());
                                                    holder.itemView.setOnClickListener(new View.OnClickListener() {
                                                        @Override
                                                        public void onClick(View view) {
                                                            Intent intent = new Intent(getApplicationContext(), MessageActivity.class);
                                                            intent.putExtra("friendid", model.getUserId());
                                                            intent.putExtra("name", model.getLastName() + ", " + model.getFirstName());
                                                            intent.putExtra("usertype", "Patients");
                                                            intent.putExtra("type", "Doctors");
                                                            startActivity(intent);
                                                        }
                                                    });
                                                }
                                            };

                                            patientlist.setHasFixedSize(true);
                                            patientlist.setLayoutManager(new LinearLayoutManager(Doctor_Patientchatlist.this));
                                            patientlist.setAdapter(adapter);
                                            adapter.startListening();
                                        }
                                    }
                                }
                            });
                        }
                    }
                }
            }
        });



    }

    private void getpatient() {

        patientlist = (RecyclerView) findViewById(R.id.recyclerViewpat);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        List<String> Patients = new ArrayList<>();
        db.collection("Clinics").whereEqualTo("ClinicName",clinicname).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    if (!task.getResult().isEmpty()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            clinicid = document.getId();
                            db.collection("Clinics").document(clinicid).collection("Patients").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful()) {
                                        if (!task.getResult().isEmpty()) {
                                            for (QueryDocumentSnapshot document : task.getResult()) {
                                                String patuid = document.getString("PatUId");

                                                Patients.add(patuid);
                                            }
                                            Query query = db.collection("Patients").whereIn("UserId", Patients).orderBy("LastName");
                                            FirestoreRecyclerOptions<PatientModel> options = new FirestoreRecyclerOptions.Builder<PatientModel>()
                                                    .setQuery(query, PatientModel.class)
                                                    .build();

                                            adapter = new FirestoreRecyclerAdapter<PatientModel, PatientsViewHolder>(options) {

                                                @NonNull
                                                @Override
                                                public PatientsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                                                    View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.patient_list_single, parent, false);
                                                    return new PatientsViewHolder(view);
                                                }

                                                @Override
                                                protected void onBindViewHolder(@NonNull PatientsViewHolder holder, int position, @NonNull PatientModel model) {
                                                    holder.list_patname.setText(model.getLastName() + ", " + model.getFirstName());
                                                    holder.list_patemail.setText(model.getEmail());
                                                    holder.itemView.setOnClickListener(new View.OnClickListener() {
                                                        @Override
                                                        public void onClick(View view) {
                                                            Intent intent = new Intent(getApplicationContext(), MessageActivity.class);
                                                            intent.putExtra("friendid", model.getUserId());
                                                            intent.putExtra("name", model.getLastName() + ", " + model.getFirstName());
                                                            intent.putExtra("usertype", "Patients");
                                                            intent.putExtra("type", "Doctors");
                                                            startActivity(intent);
                                                        }
                                                    });
                                                }
                                            };

                                            patientlist.setHasFixedSize(true);
                                            patientlist.setLayoutManager(new LinearLayoutManager(Doctor_Patientchatlist.this));
                                            patientlist.setAdapter(adapter);
                                            adapter.startListening();
                                        }
                                    }
                                }
                            });
                        }
                    }
                }
            }
        });


    }

    private class PatientsViewHolder extends RecyclerView.ViewHolder {
        private TextView list_patname;
        private TextView list_patemail;
        public PatientsViewHolder(@NonNull View itemView) {
            super(itemView);
            list_patemail= itemView.findViewById(R.id.list_patemail);
            list_patname= itemView.findViewById(R.id.list_patientname);

        }

    }




}