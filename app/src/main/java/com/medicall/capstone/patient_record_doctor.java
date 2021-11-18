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
import android.widget.Button;
import android.widget.TextView;

import com.medicall.capstone.R;

import com.medicall.capstone.utilities.PreferenceManager;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.mancj.materialsearchbar.MaterialSearchBar;

public class patient_record_doctor extends AppCompatActivity {

    private RecyclerView patientrecListdoc;
    private FirebaseFirestore firebaseFirestore;
    MaterialSearchBar materialSearchBar;
    private   String clinicname;
    private String searchtype = "Patients";
    private PreferenceManager preferenceManager;
    private RecyclerView patientlist;
    private FirestoreRecyclerAdapter adapter;

    String txt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_record_doctor);
        preferenceManager = new PreferenceManager(getApplicationContext());

        firebaseFirestore = FirebaseFirestore.getInstance();
        patientrecListdoc = findViewById(R.id.patientrec_doc);
        clinicname = preferenceManager.getString("ClinicName");
        materialSearchBar = (MaterialSearchBar) findViewById(R.id.searchBar);
        materialSearchBar.setCardViewElevation(10);

        getpatient();
        materialSearchBar.addTextChangeListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence text, int start, int before, int count) {
            txt=text.toString();
            if (txt.isEmpty()){
                adapter.stopListening();
                getpatient();
            }else{
                txt = txt.substring(0,1).toUpperCase()+txt.substring(1).toLowerCase();
                startsearchpatient(txt.toString());
            }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });



        //Query
        Query query = firebaseFirestore.collection("Patients");
        //RecyclerOptions
        FirestoreRecyclerOptions<PatientModel> options = new FirestoreRecyclerOptions.Builder<PatientModel>()
                .setQuery(query, PatientModel.class)
                .build();

        adapter = new FirestoreRecyclerAdapter<PatientModel, patient_record_doctor.PatientViewHolder>(options) {
            @NonNull
            @Override
            public patient_record_doctor.PatientViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_patientrec_doc, parent, false);
                return new patient_record_doctor.PatientViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull patient_record_doctor.PatientViewHolder holder, int position, @NonNull PatientModel model) {
                holder.listFirstname.setText(model.getLastName()+ "," +model.getFirstName());
                holder.listemail.setText(model.getEmail());

                String patientID = model.getUserId();

                holder.patientR.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getApplicationContext(), patientrec_doc.class);
                        intent.putExtra("patid", patientID);
                        startActivity(intent);
                    }
                });
            }
        };

        patientrecListdoc.setHasFixedSize(true);
        patientrecListdoc.setLayoutManager(new LinearLayoutManager(this));
        patientrecListdoc.setAdapter(adapter);

    }

    private void startsearchpatient(String text) {
        Query query;
        adapter.stopListening();
        patientlist = (RecyclerView) findViewById(R.id.patientrec_doc);
        FirebaseFirestore db = FirebaseFirestore.getInstance();

            query = db.collection("Patients").whereEqualTo(clinicname, "True").orderBy("LastName").startAt(text).endAt(text+'\uf8ff');

        FirestoreRecyclerOptions<PatientModel> options = new FirestoreRecyclerOptions.Builder<PatientModel>()
                .setQuery(query,PatientModel.class)
                .build();
        adapter = new FirestoreRecyclerAdapter<PatientModel, patient_record_doctor.PatientViewHolder>(options) {

            @NonNull
            @Override
            public patient_record_doctor.PatientViewHolder onCreateViewHolder(@NonNull  ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.patient_list_single,parent,false);
                return new patient_record_doctor.PatientViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull  patient_record_doctor.PatientViewHolder holder, int position, @NonNull PatientModel model) {

                holder.listFirstname.setText(model.getLastName()+", "+model.getFirstName());
                holder.listemail.setText(model.getEmail());
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getApplicationContext(), MessageActivity.class);
                        intent.putExtra("firendid",model.getUserId());

                            intent.putExtra("name", model.getLastName()+", "+model.getFirstName());

                        intent.putExtra("usertype", searchtype);
                        intent.putExtra("type", "Secretary");
                        startActivity(intent);
                    }
                });
            }
        };
        patientlist.setHasFixedSize(true);
        patientlist.setLayoutManager(new LinearLayoutManager(patient_record_doctor.this));
        patientlist.setAdapter(adapter);
        adapter.startListening();
    }

    //search
    private void getpatient() {
        Query query;
        patientlist = (RecyclerView) findViewById(R.id.patientrec_doc);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        if (searchtype == "Patients"){
            query = db.collection(searchtype).whereEqualTo(clinicname, "True");
        } else {
            query = db.collection(searchtype).whereEqualTo("ClinicName", clinicname);
        }
        FirestoreRecyclerOptions<PatientModel> options = new FirestoreRecyclerOptions.Builder<PatientModel>()
                .setQuery(query,PatientModel.class).build();

        adapter = new FirestoreRecyclerAdapter<PatientModel, patient_record_doctor.PatientViewHolder>(options) {

            @NonNull
            @Override
            public patient_record_doctor.PatientViewHolder onCreateViewHolder(@NonNull  ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.patient_list_single,parent,false);
                return new patient_record_doctor.PatientViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull  patient_record_doctor.PatientViewHolder holder, int position, @NonNull PatientModel model) {
            if (searchtype == "Patients"){
                holder.listFirstname.setText(model.getLastName()+", "+model.getFirstName());
            }else {
                holder.listFirstname.setText("Dr."+" "+model.getLastName());
            }
            holder.listemail.setText(model.getEmail());
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getApplicationContext(), MessageActivity.class);
                    intent.putExtra("firendid",model.getUserId());
                    if (searchtype == "Patietns"){
                        intent.putExtra("name", model.getLastName()+", "+model.getFirstName());
                    }else {
                        intent.putExtra("name", "Dr." + " " + model.getLastName());
                    }
                    intent.putExtra("usertype", searchtype);
                    intent.putExtra("type", "Secretary");
                    startActivity(intent);
                }
            });
            }
        };
        patientlist.setHasFixedSize(true);
        patientlist.setLayoutManager(new LinearLayoutManager(patient_record_doctor.this));
        patientlist.setAdapter(adapter);
        adapter.startListening();
        }




    private class PatientViewHolder extends  RecyclerView.ViewHolder{

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