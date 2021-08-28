package com.medicall.capstone.secretary;

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
import android.widget.ImageView;
import android.widget.TextView;

import com.medicall.capstone.R;
import com.medicall.capstone.utilities.PreferenceManager;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Secretary_schedlist_patsched extends AppCompatActivity {

    FirebaseAuth fAuth;
    FirebaseFirestore db;
    RecyclerView mFirestoreList;
    Button pastsched;

    String docuid;

    private FirestoreRecyclerAdapter adapter;

    PreferenceManager preferenceManager;

    ImageView back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_secretary_schedlist_patsched);

        pastsched = findViewById(R.id.sec_past_sched);

        mFirestoreList = findViewById(R.id.sec_sched_recview);
        fAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        preferenceManager = new PreferenceManager(getApplicationContext());

        Intent intent = getIntent();
        docuid = intent.getStringExtra("docuid");

        back = findViewById(R.id.backspace);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Secretary_schedlist.class);
                startActivity(intent);
            }
        });


        Query query = db.collection("Schedules").whereEqualTo("DoctorUId", docuid).whereEqualTo("Status", "Paid").orderBy("Date", Query.Direction.ASCENDING).limit(20);

        FirestoreRecyclerOptions<SecretaryPatschedModel> options = new FirestoreRecyclerOptions.Builder<SecretaryPatschedModel>().setQuery(query, SecretaryPatschedModel.class).build();

        pastsched.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Secretary_schedlist_patsched_past.class);
                intent.putExtra("docuid", docuid);
                startActivity(intent);
            }
        });

        adapter = new FirestoreRecyclerAdapter<SecretaryPatschedModel, Secretary_schedlist_patsched.SecretaryPatSchedViewHolder>(options) {
            @NonNull
            @Override
            public Secretary_schedlist_patsched.SecretaryPatSchedViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.sec_schedlist_patsched_single, parent, false);
                return new Secretary_schedlist_patsched.SecretaryPatSchedViewHolder(view);
            }


            @Override
            protected void onBindViewHolder(@NonNull Secretary_schedlist_patsched.SecretaryPatSchedViewHolder holder, int position, @NonNull SecretaryPatschedModel model) {
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

    private class SecretaryPatSchedViewHolder extends RecyclerView.ViewHolder{

        private TextView list_name, list_datesched;

        public SecretaryPatSchedViewHolder(@NonNull View itemView) {
            super(itemView);

            list_name = itemView.findViewById(R.id.secsched_patname);
            list_datesched = itemView.findViewById(R.id.secsched_datesched);
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