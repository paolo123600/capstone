package com.medicall.capstone;

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
import android.widget.TextView;
import android.widget.Toast;

import com.medicall.capstone.R;

import com.medicall.capstone.utilities.PreferenceManager;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.mancj.materialsearchbar.MaterialSearchBar;

public class Doctor_Patientchatlist extends AppCompatActivity {
    private   String clinicname;
private RecyclerView patientlist;
private FirestoreRecyclerAdapter adapter;
private RecyclerView.Adapter adapter2;
MaterialSearchBar materialSearchBar;
private PreferenceManager preferenceManager;
FirebaseFirestore db;
String txt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor__patientchatlist);
        preferenceManager = new PreferenceManager(getApplicationContext());
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
                    Toast.makeText(Doctor_Patientchatlist.this, txt.toString(), Toast.LENGTH_SHORT).show();

                }


            }

            @Override
            public void afterTextChanged(Editable editable) {



            }
        });





    }

    private void startsearchpatient(String text) {
        adapter.stopListening();
        patientlist = (RecyclerView) findViewById(R.id.recyclerViewpat);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Query query = db.collection("Patients").whereEqualTo(clinicname, "True").orderBy("LastName").startAt(text).endAt(text+'\uf8ff');
        FirestoreRecyclerOptions<PatientModel> options = new FirestoreRecyclerOptions.Builder<PatientModel>()
                .setQuery(query,PatientModel.class)
                .build();

        adapter = new FirestoreRecyclerAdapter<PatientModel, PatientsViewHolder>(options) {

            @NonNull
            @Override
            public PatientsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.patient_list_single,parent,false);
                return new PatientsViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull PatientsViewHolder holder, int position, @NonNull PatientModel model) {
                holder.list_patname.setText(model.getLastName()+", "+model.getFirstName());
                holder.list_patemail.setText(model.getEmail());
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(getApplicationContext(), MessageActivity.class);
                        intent.putExtra("friendid", model.getUserId());
                        intent.putExtra("name", model.getLastName()+", "+model.getFirstName());
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

    private void getpatient() {

        patientlist = (RecyclerView) findViewById(R.id.recyclerViewpat);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Query query = db.collection("Patients").whereEqualTo(clinicname, "True");
        FirestoreRecyclerOptions<PatientModel> options = new FirestoreRecyclerOptions.Builder<PatientModel>()
                .setQuery(query,PatientModel.class)
                .build();

        adapter = new FirestoreRecyclerAdapter<PatientModel, PatientsViewHolder>(options) {

            @NonNull
            @Override
            public PatientsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.patient_list_single,parent,false);
                return new PatientsViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull PatientsViewHolder holder, int position, @NonNull PatientModel model) {
                holder.list_patname.setText(model.getLastName()+", "+model.getFirstName());
                holder.list_patemail.setText(model.getEmail());
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(getApplicationContext(), MessageActivity.class);
                        intent.putExtra("friendid", model.getUserId());
                        intent.putExtra("name", model.getLastName()+", "+model.getFirstName());
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