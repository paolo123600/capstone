package com.medicall.capstone.doctor;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.firestore.paging.FirestorePagingAdapter;
import com.firebase.ui.firestore.paging.FirestorePagingOptions;
import com.firebase.ui.firestore.paging.LoadingState;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.medicall.capstone.BPModel;
import com.medicall.capstone.R;
import com.medicall.capstone.patient_record_clinic;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class Doctor_patient_bp extends AppCompatActivity {

    TextView bpdate;
    DatePickerDialog.OnDateSetListener listener;

    FirebaseAuth fAuth;
    FirebaseFirestore db;
    String userId;
    RecyclerView mFirestoreList;
    ImageView back;
    TextView none;






    private FirestoreRecyclerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_patient_bp);
        none = findViewById(R.id.None);


        mFirestoreList = findViewById(R.id.Doctor_patBP);
        fAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();


        back = findViewById(R.id.backspace);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               Intent intent = new Intent(getApplicationContext(), patient_record_clinic.class);
               startActivity(intent);
            }
        });

        Intent intent = getIntent();
        userId = intent.getStringExtra("patid");





        Query query = db.collection("Patients").document(userId).collection("BP").orderBy("Dnt", Query.Direction.DESCENDING).limit(20);


        FirestoreRecyclerOptions<BPModel> options = new FirestoreRecyclerOptions.Builder<BPModel>().setQuery(query, BPModel.class).build();


        adapter = new FirestoreRecyclerAdapter<BPModel, BPViewHolder>(options) {
            @NonNull
            @Override
            public Doctor_patient_bp.BPViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.bp_list, parent, false);
                return new Doctor_patient_bp.BPViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull Doctor_patient_bp.BPViewHolder holder, int position, @NonNull BPModel model) {
                holder.list_bpressure.setText("BP: " + model.getUpper() + "/" + model.getLower());
                holder.temperi.setText("Temperature: " + model.getTemperature());
                holder.pulse.setText("Pulse: " + model.getPulse());
                holder.respi.setText("Respiration: " + model.getRespiratory());
                holder.list_dnt.setText(model.getDnt() + "");
            }
        };

        mFirestoreList.setHasFixedSize(true);
        mFirestoreList.setLayoutManager(new LinearLayoutManager(this));
        mFirestoreList.setAdapter(adapter);


        db.collection("Patients").document(userId).collection("BP").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable  QuerySnapshot value, @Nullable  FirebaseFirestoreException error) {
                if(error != null){
                    Toast.makeText(Doctor_patient_bp.this, "Error Loading", Toast.LENGTH_SHORT).show();

                }
                if (value.isEmpty()){
                    none.setVisibility(View.VISIBLE);
                    mFirestoreList.setVisibility(View.GONE);
                } else {
                    none.setVisibility(View.GONE);
                    mFirestoreList.setVisibility(View.VISIBLE);
                }

            }
        });




    }


    private class BPViewHolder extends RecyclerView.ViewHolder {

        private TextView list_bpressure, list_date, list_dnt, temperi, pulse, respi;

        public BPViewHolder(@NonNull View itemView) {
            super(itemView);

            list_bpressure = itemView.findViewById(R.id.BPlist_bloodp);
            list_dnt = itemView.findViewById(R.id.BPlist_dnt);
            temperi = itemView.findViewById(R.id.Temperi);
            pulse = itemView.findViewById(R.id.Pulse);
            respi = itemView.findViewById(R.id.respir);
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