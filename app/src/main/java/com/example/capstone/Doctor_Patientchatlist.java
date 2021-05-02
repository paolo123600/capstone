package com.example.capstone;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.mancj.materialsearchbar.MaterialSearchBar;

public class Doctor_Patientchatlist extends AppCompatActivity {
private RecyclerView patientlist;
private FirestoreRecyclerAdapter adapter;
private Button changechat;
MaterialSearchBar materialSearchBar;
FirebaseFirestore db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor__patientchatlist);
            String clinicname = "Clinic2";
        db=FirebaseFirestore.getInstance();
        changechat=(Button) findViewById(R.id.btn_changechat_secretary);
        materialSearchBar = (MaterialSearchBar) findViewById(R.id.searchBar);
        materialSearchBar.setCardViewElevation(10);

        patientlist = (RecyclerView) findViewById(R.id.recyclerViewpat);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Query query = db.collection("Patients").whereEqualTo(clinicname, "true");
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
                            Toast.makeText(Doctor_Patientchatlist.this, model.getUserId(), Toast.LENGTH_SHORT).show();
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