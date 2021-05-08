package com.example.capstone;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class patient_record_clinic extends AppCompatActivity {

    private RecyclerView patientrecList;
    private FirebaseFirestore firebaseFirestore;

    private FirestoreRecyclerAdapter adapter;

    Button patientrec;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_record_clinic);

        firebaseFirestore = FirebaseFirestore.getInstance();
        patientrecList = findViewById(R.id.patientrec_sec);


        //Query
        Query query = firebaseFirestore.collection("Patients");
        //RecyclerOptions
        FirestoreRecyclerOptions<PatientModel> options = new FirestoreRecyclerOptions.Builder<PatientModel>()
                .setQuery(query, PatientModel.class)
                .build();

        adapter = new FirestoreRecyclerAdapter<PatientModel, PatientViewHolder>(options) {
            @NonNull
            @Override
            public PatientViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_patientrec_sec, parent, false);
                return new PatientViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull PatientViewHolder holder, int position, @NonNull PatientModel model) {
                holder.listFirstname.setText(model.getLastName()+ "," +model.getFirstName());
                holder.listemail.setText(model.getEmail());

                String patientID = model.getUserId();

                holder.patientR.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getApplicationContext(), ProfileFragment.class);
                        intent.putExtra("patid", patientID);
                        startActivity(intent);
                    }
                });
            }
        };

        patientrecList.setHasFixedSize(true);
        patientrecList.setLayoutManager(new LinearLayoutManager(this));
        patientrecList.setAdapter(adapter);

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

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }
}
