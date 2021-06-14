package com.example.capstone;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import org.w3c.dom.Text;

public class docshed_page1 extends AppCompatActivity {

    private RecyclerView mFirestoreList;
    private FirebaseFirestore db;
    FirestoreRecyclerAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_docshed_page1);

        db = FirebaseFirestore.getInstance();
        mFirestoreList = findViewById(R.id.docsched_list);

        Query query = db.collection("Doctors");

        FirestoreRecyclerOptions<DoctorModel> options = new FirestoreRecyclerOptions.Builder<DoctorModel>()
                .setQuery(query,DoctorModel.class)
                .build();

        adapter = new FirestoreRecyclerAdapter<DoctorModel, DoctorViewHolder>(options) {
            @NonNull
            @Override
            public DoctorViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_doc_page1, parent , false);
                return new DoctorViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull DoctorViewHolder holder, int position, @NonNull DoctorModel model) {
                holder.doc_name.setText("Doctor " + model.getLastName());
                holder.doc_email.setText(model.getEmail());
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        Intent intent = new Intent(getApplicationContext(), docsched_page2.class);
                        intent.putExtra("docid", model.getUserId());
                        intent.putExtra("docname", "Doctor " + model.getLastName());
                        startActivity(intent);



                    }
                });
            }
        };

        mFirestoreList.setHasFixedSize(true);
        mFirestoreList.setLayoutManager(new LinearLayoutManager(this));
        mFirestoreList.setAdapter(adapter);
        adapter.startListening();
    }

    private class DoctorViewHolder extends RecyclerView.ViewHolder{
        private TextView doc_name;
        private TextView doc_email;

        public DoctorViewHolder(@NonNull View itemView) {
            super(itemView);

            doc_name = itemView.findViewById(R.id.list_docname);
            doc_email = itemView.findViewById(R.id.list_docemail);
        }
    }
}