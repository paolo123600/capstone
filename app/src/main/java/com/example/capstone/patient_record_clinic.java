package com.example.capstone;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.capstone.utilities.PreferenceManager;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.mancj.materialsearchbar.MaterialSearchBar;

import java.util.ArrayList;
import java.util.List;

public class patient_record_clinic extends AppCompatActivity {

    private RecyclerView patientrecList;
    private FirebaseFirestore firebaseFirestore;
    //
    private FirestoreRecyclerAdapter adapter;
    private String clinicname;
    private  String clinicid;


    private PreferenceManager preferenceManager;

    MaterialSearchBar materialSearchBar;
    String txt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_record_clinic);
        preferenceManager = new PreferenceManager(getApplicationContext());
        clinicname = preferenceManager.getString("ClinicName");

        firebaseFirestore = FirebaseFirestore.getInstance();

        materialSearchBar = (MaterialSearchBar) findViewById(R.id.searchBar);
        materialSearchBar.setCardViewElevation(0);

        getpatient();

        materialSearchBar.addTextChangeListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence text, int start, int before, int count) {
                txt = text.toString();
                if (txt.isEmpty()) {
                    adapter.stopListening();
                    getpatient();
                } else {
                    adapter.stopListening();
                    txt = txt.substring(0, 1).toUpperCase() + txt.substring(1).toLowerCase();
                    startsearchpatient(txt.toString());
                    Toast.makeText(patient_record_clinic.this, txt.toString(), Toast.LENGTH_SHORT).show();

                }


            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    private void startsearchpatient(String text) {

        patientrecList = (RecyclerView) findViewById(R.id.patientrec_sec);
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();

        List<String> Patients = new ArrayList<>();
        firebaseFirestore.collection("Clinics").whereEqualTo("ClinicName", clinicname).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    if (!task.getResult().isEmpty()) {
                        Toast.makeText(patient_record_clinic.this, "hhere", Toast.LENGTH_SHORT).show();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            clinicid = document.getId();
                            firebaseFirestore.collection("Clinics").document(clinicid).collection("Patients").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful()) {
                                        if (!task.getResult().isEmpty()) {
                                            for (QueryDocumentSnapshot document : task.getResult()) {
                                                String patuid = document.getString("PatUId");
                                                Patients.add(patuid);

                                            }

                                                Query query = firebaseFirestore.collection("Patients").whereIn("UserId", Patients).orderBy("LastName").startAt(text).endAt(text + '\uf8ff');
                                                //RecyclerOptions
                                                FirestoreRecyclerOptions<PatientModel> options = new FirestoreRecyclerOptions.Builder<PatientModel>()
                                                        .setQuery(query, PatientModel.class)
                                                        .build();

                                                adapter = new FirestoreRecyclerAdapter<PatientModel, patient_record_clinic.PatientViewHolder>(options) {
                                                    @NonNull
                                                    @Override
                                                    public patient_record_clinic.PatientViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                                                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_patientrec_sec, parent, false);
                                                        return new patient_record_clinic.PatientViewHolder(view);
                                                    }

                                                    @Override
                                                    protected void onBindViewHolder(@NonNull patient_record_clinic.PatientViewHolder holder, int position, @NonNull PatientModel model) {
                                                        holder.listFirstname.setText(model.getLastName() + "," + model.getFirstName());
                                                        holder.listemail.setText(model.getEmail());

                                                        String patientID = model.getUserId();

                                                        holder.patientR.setOnClickListener(new View.OnClickListener() {
                                                            @Override
                                                            public void onClick(View v) {
                                                                Intent intent = new Intent(getApplicationContext(), patientrec_sec.class);
                                                                intent.putExtra("patid", patientID);
                                                                startActivity(intent);
                                                            }
                                                        });
                                                    }
                                                };

                                                patientrecList.setHasFixedSize(true);
                                                patientrecList.setLayoutManager(new LinearLayoutManager(patient_record_clinic.this));
                                                patientrecList.setAdapter(adapter);
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

        patientrecList = (RecyclerView) findViewById(R.id.patientrec_sec);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        List<String> Patients = new ArrayList<>();
        db.collection("Clinics").whereEqualTo("ClinicName",clinicname).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    if (!task.getResult().isEmpty()){
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            clinicid = document.getId();
                            db.collection("Clinics").document(clinicid).collection("Patients").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful()){
                                        if (!task.getResult().isEmpty()){
                                            for (QueryDocumentSnapshot document : task.getResult()) {
                                                String patuid = document.getString("PatUId");

                                                Patients.add(patuid);
                                            }
                                            Query query = db.collection("Patients").whereIn("UserId",Patients).orderBy("LastName");
                                            FirestoreRecyclerOptions<PatientModel> options = new FirestoreRecyclerOptions.Builder<PatientModel>()
                                                    .setQuery(query, PatientModel.class)
                                                    .build();

                                            adapter = new FirestoreRecyclerAdapter<PatientModel, patient_record_clinic.PatientViewHolder>(options) {

                                                @NonNull
                                                @Override
                                                public patient_record_clinic.PatientViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                                                    View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_patientrec_sec, parent, false);
                                                    return new patient_record_clinic.PatientViewHolder(view);
                                                }

                                                @Override
                                                protected void onBindViewHolder(@NonNull patient_record_clinic.PatientViewHolder holder, int position, @NonNull PatientModel model) {
                                                    holder.listFirstname.setText(model.getLastName() + "," + model.getFirstName());
                                                    holder.listemail.setText(model.getEmail());

                                                    String patientID = model.getUserId();

                                                    holder.patientR.setOnClickListener(new View.OnClickListener() {
                                                        @Override
                                                        public void onClick(View v) {
                                                            Intent intent = new Intent(getApplicationContext(), patientrec_sec.class);
                                                            intent.putExtra("patid", patientID);
                                                            startActivity(intent);
                                                        }
                                                    });
                                                }
                                            };

                                            patientrecList.setHasFixedSize(true);
                                            patientrecList.setLayoutManager(new LinearLayoutManager(patient_record_clinic.this));
                                            patientrecList.setAdapter(adapter);
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

    private class PatientViewHolder extends RecyclerView.ViewHolder {

        private TextView listFirstname;
        private TextView listemail;
        private Button patientR;

        public PatientViewHolder(@NonNull View itemView) {
            super(itemView);

            listFirstname = itemView.findViewById(R.id.patientrec_firstname);
            listemail = itemView.findViewById(R.id.patientrec_email);
            patientR = itemView.findViewById(R.id.patientrec_btn);

        }
    }

}