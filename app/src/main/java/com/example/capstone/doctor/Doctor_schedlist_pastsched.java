package com.example.capstone.doctor;

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

import com.example.capstone.R;
import com.example.capstone.utilities.PreferenceManager;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Doctor_schedlist_pastsched extends AppCompatActivity {

    FirebaseAuth fAuth;
    FirebaseFirestore db;
    RecyclerView mFirestoreList;

    String docuid;

    private FirestoreRecyclerAdapter adapter;

    PreferenceManager preferenceManager;

    String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_schedlist_pastsched);


        mFirestoreList = findViewById(R.id.Doc_past_sched_recview);
        fAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        preferenceManager = new PreferenceManager(getApplicationContext());

        Intent intent = getIntent();
        docuid = intent.getStringExtra("docuid");

        userId = fAuth.getCurrentUser().getUid();


        Query query = db.collection("Schedules").whereEqualTo("DoctorUId", userId).whereEqualTo("Status", "Completed").orderBy("Date", Query.Direction.DESCENDING).limit(20);

        FirestoreRecyclerOptions<DoctorUpcomingModel> options = new FirestoreRecyclerOptions.Builder<DoctorUpcomingModel>().setQuery(query, DoctorUpcomingModel.class).build();

        adapter = new FirestoreRecyclerAdapter<DoctorUpcomingModel, Doctor_schedlist_pastsched.DoctorUpcomingViewHolder>(options) {
            @NonNull
            @Override
            public Doctor_schedlist_pastsched.DoctorUpcomingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.doc_upcoming_schedlist_single, parent, false);
                return new Doctor_schedlist_pastsched.DoctorUpcomingViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull Doctor_schedlist_pastsched.DoctorUpcomingViewHolder holder, int position, @NonNull DoctorUpcomingModel model) {
                Date datesched =model.getDate();
                SimpleDateFormat format = new SimpleDateFormat("MMMM d ,yyyy");
                String date=  format.format(datesched);
                holder.list_datesched.setText(date);
                db.collection("Patients").document(model.getPatientUId()).get()
                        .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                documentSnapshot.getData();
                                String patname = documentSnapshot.getString("LastName") + ", " +documentSnapshot.getString("FirstName");
                                holder.list_name.setText(patname);
                            }
                        });

            }
        };

        mFirestoreList.setHasFixedSize(true);
        mFirestoreList.setLayoutManager(new LinearLayoutManager(this));
        mFirestoreList.setAdapter(adapter);

    }

    private class DoctorUpcomingViewHolder extends RecyclerView.ViewHolder{

        private TextView list_name, list_datesched;

        public DoctorUpcomingViewHolder(@NonNull View itemView) {
            super(itemView);

            list_name = itemView.findViewById(R.id.doc_upcomingsched_patname);
            list_datesched = itemView.findViewById(R.id.doc_upcomingsched_datesched);
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