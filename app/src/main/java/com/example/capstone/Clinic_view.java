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
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class Clinic_view extends AppCompatActivity {

    private FirebaseFirestore firebaseFirestore;
    private RecyclerView mClinicList;

    private FirestoreRecyclerAdapter adapter;

    ImageView back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clinic_view);

        firebaseFirestore = FirebaseFirestore.getInstance();
        mClinicList = findViewById(R.id.scheddoc_list);

        back = findViewById(R.id.backspace);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        //Query
        Query query = firebaseFirestore.collection("Secretary");
        //RecyclerOptions
        FirestoreRecyclerOptions<ClinicModel> options = new FirestoreRecyclerOptions.Builder<ClinicModel>()
                .setQuery(query, ClinicModel.class)
                .build();

        adapter = new FirestoreRecyclerAdapter<ClinicModel, ClinicViewHolder>(options) {
            @NonNull
            @Override
            public ClinicViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_clinic, parent, false);
                return new ClinicViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull ClinicViewHolder holder, int position, @NonNull ClinicModel model) {
                holder.clinic_list.setText(model.getClinicName());
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(getApplicationContext(), MessageActivity.class);
                        intent.putExtra("friendid", model.getUserId());
                        intent.putExtra("name", model.getClinicName());
                        intent.putExtra("usertype", "Secretary");
                        intent.putExtra("type", "Patients");
                        startActivity(intent);
                    }
                    });
                }

        };

        mClinicList.setHasFixedSize(true);
        mClinicList.setLayoutManager(new LinearLayoutManager(this));
        mClinicList.setAdapter(adapter);

    }

    private class ClinicViewHolder extends RecyclerView.ViewHolder {

        private TextView clinic_list;

        public ClinicViewHolder(@NonNull View itemView) {
            super(itemView);

            clinic_list = itemView.findViewById(R.id.clinic_name);

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