package com.example.capstone.secretary;

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

import com.example.capstone.Login;
import com.example.capstone.R;
import com.example.capstone.utilities.PreferenceManager;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class Secretary_schedlist extends AppCompatActivity {

    FirebaseAuth fAuth;
    FirebaseFirestore db;
    RecyclerView mFirestoreList;
    Button pastbutton;

    private FirestoreRecyclerAdapter adapter;

    PreferenceManager preferenceManager;

    ImageView back;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_secretary_schedlist);

        mFirestoreList = findViewById(R.id.sec_sched_recview);
        fAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        preferenceManager = new PreferenceManager(getApplicationContext());

        back = findViewById(R.id.backspace);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Login.class);
                startActivity(intent);
            }
        });


        Query query = db.collection("Doctors").whereEqualTo("ClinicName", preferenceManager.getString("ClinicName"));

        FirestoreRecyclerOptions<SecretaryListModel> options = new FirestoreRecyclerOptions.Builder<SecretaryListModel>().setQuery(query, SecretaryListModel.class).build();

        adapter = new FirestoreRecyclerAdapter<SecretaryListModel, SecretarySchedViewHolder>(options) {
            @NonNull
            @Override
            public SecretarySchedViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.sec_schedlist_single, parent, false);
                return new SecretarySchedViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull SecretarySchedViewHolder holder, int position, @NonNull SecretaryListModel model) {
                holder.list_name.setText(model.getLastName() + ", " +  model.getFirstName());
                holder.list_doc.setText(model.getDocType());
                holder.list_clinicname.setText(model.getClinicName());
                String docuid = model.getUserId();
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getApplicationContext(), Secretary_schedlist_patsched.class);
                        intent.putExtra("docuid", docuid);
                        startActivity(intent);
                    }
                });
            }
        };

        mFirestoreList.setHasFixedSize(true);
        mFirestoreList.setLayoutManager(new LinearLayoutManager(this));
        mFirestoreList.setAdapter(adapter);

    }

    private class SecretarySchedViewHolder extends RecyclerView.ViewHolder{

        private TextView list_name, list_doc, list_clinicname;

        public SecretarySchedViewHolder(@NonNull View itemView) {
            super(itemView);

            list_name = itemView.findViewById(R.id.secsched_docname);
            list_doc = itemView.findViewById(R.id.secsched_doctype);
            list_clinicname = itemView.findViewById(R.id.secsched_clinicname);
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