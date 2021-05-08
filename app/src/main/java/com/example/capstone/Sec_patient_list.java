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

import com.example.capstone.utilities.Constants;
import com.example.capstone.utilities.PreferenceManager;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.mancj.materialsearchbar.MaterialSearchBar;

public class Sec_patient_list extends AppCompatActivity {

    private String searchtype = "Patients";
    TextView changeTV;
    Button btnchange;
    private   String clinicname;
    private RecyclerView patientlist;
    private FirestoreRecyclerAdapter adapter;
    private RecyclerView.Adapter adapter2;
    MaterialSearchBar materialSearchBar;
    FirebaseFirestore db;
    String txt;
    private PreferenceManager preferenceManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sec_patient_list);
        preferenceManager = new PreferenceManager(getApplicationContext());
        changeTV = findViewById(R.id.changeTV);
        btnchange = findViewById(R.id.button_doctor_view);
        clinicname = preferenceManager.getString("ClinicName");
        db=FirebaseFirestore.getInstance();
        materialSearchBar = (MaterialSearchBar) findViewById(R.id.searchBar);
        materialSearchBar.setCardViewElevation(10);

        getpatient();

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


btnchange.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        if (searchtype == "Patients") {
            searchtype = "Doctors";
            changeTV.setText("Doctors: ");
            btnchange.setText("Patient");
        }else {
            searchtype = "Patients";
            changeTV.setText("Patients: ");
            btnchange.setText("Doctor");
        }
        getpatient();
    }
});


    }

    private void startsearchpatient(String text) {
        Query query;
        adapter.stopListening();
        patientlist = (RecyclerView) findViewById(R.id.recyclerViewpat);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        if (searchtype == "Patients") {
            query = db.collection(searchtype).whereEqualTo(clinicname, "true").orderBy("LastName").startAt(text).endAt(text+'\uf8ff');
        }else {
            query = db.collection(searchtype).whereEqualTo("ClinicName", clinicname).orderBy("LastName").startAt(text).endAt(text+'\uf8ff');
        }
        FirestoreRecyclerOptions<PatientModel> options = new FirestoreRecyclerOptions.Builder<PatientModel>()
                .setQuery(query,PatientModel.class)
                .build();

        adapter = new FirestoreRecyclerAdapter<PatientModel, Sec_patient_list.PatientsViewHolder>(options) {

            @NonNull
            @Override
            public Sec_patient_list.PatientsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.patient_list_single,parent,false);
                return new Sec_patient_list.PatientsViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull Sec_patient_list.PatientsViewHolder holder, int position, @NonNull PatientModel model) {
                if (searchtype == "Patients") {
                    holder.list_patname.setText(model.getLastName()+", "+model.getFirstName());
                }else {
                    holder.list_patname.setText("Dr."+" "+model.getLastName());
                }
                holder.list_patemail.setText(model.getEmail());
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(getApplicationContext(), MessageActivity.class);
                        intent.putExtra("friendid", model.getUserId());
                        if (searchtype == "Patients") {
                            intent.putExtra("name", model.getLastName()+", "+model.getFirstName());
                        }else {
                            intent.putExtra("name", "Dr."+" "+model.getLastName());
                        }
                        intent.putExtra("usertype", searchtype);
                        intent.putExtra("type", "Secretary");
                        startActivity(intent);
                    }
                });
            }
        };

        patientlist.setHasFixedSize(true);
        patientlist.setLayoutManager(new LinearLayoutManager(Sec_patient_list.this));
        patientlist.setAdapter(adapter);
        adapter.startListening();




    }

    private void getpatient() {
        Query query;
        patientlist = (RecyclerView) findViewById(R.id.recyclerViewpat);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        if (searchtype == "Patients") {
            query = db.collection(searchtype).whereEqualTo(clinicname, "true");
        }else {
            query = db.collection(searchtype).whereEqualTo("ClinicName", clinicname);
        }
        FirestoreRecyclerOptions<PatientModel> options = new FirestoreRecyclerOptions.Builder<PatientModel>()
                .setQuery(query,PatientModel.class)
                .build();

        adapter = new FirestoreRecyclerAdapter<PatientModel, Sec_patient_list.PatientsViewHolder>(options) {

            @NonNull
            @Override
            public Sec_patient_list.PatientsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.patient_list_single,parent,false);
                return new Sec_patient_list.PatientsViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull Sec_patient_list.PatientsViewHolder holder, int position, @NonNull PatientModel model) {
                if (searchtype == "Patients") {
                    holder.list_patname.setText(model.getLastName()+", "+model.getFirstName());
                }else {
                    holder.list_patname.setText("Dr."+" "+model.getLastName());
                }
                holder.list_patemail.setText(model.getEmail());
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(getApplicationContext(), MessageActivity.class);
                        intent.putExtra("friendid", model.getUserId());
                        if (searchtype == "Patients") {
                            intent.putExtra("name", model.getLastName()+", "+model.getFirstName());
                        }else {
                            intent.putExtra("name", "Dr."+" "+model.getLastName());
                        }
                        intent.putExtra("usertype", searchtype);
                        intent.putExtra("type", "Secretary");
                        startActivity(intent);
                    }
                });
            }
        };

        patientlist.setHasFixedSize(true);
        patientlist.setLayoutManager(new LinearLayoutManager(Sec_patient_list.this));
        patientlist.setAdapter(adapter);
        adapter.startListening();

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