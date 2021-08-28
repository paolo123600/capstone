package com.example.capstone.secretary;

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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.capstone.DoctorModel;
import com.example.capstone.R;
import com.example.capstone.docsched_page2;
import com.example.capstone.docshed_page1;
import com.example.capstone.secretary_homepage;
import com.example.capstone.utilities.PreferenceManager;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.mancj.materialsearchbar.MaterialSearchBar;

import java.util.ArrayList;
import java.util.List;

public class Secretary_schedlist extends AppCompatActivity {

    FirebaseAuth fAuth;
    FirebaseFirestore db;
    RecyclerView mFirestoreList;
    Button pastbutton;

    private FirestoreRecyclerAdapter adapter;

    PreferenceManager preferenceManager;
    ImageView back;
    MaterialSearchBar materialSearchBar;
    String txt;
    private String docn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_secretary_schedlist);


        mFirestoreList = findViewById(R.id.sec_sched_recview);
        fAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        preferenceManager = new PreferenceManager(getApplicationContext());
        docn = preferenceManager.getString("ClinicName");
        back = findViewById(R.id.backspace);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), secretary_homepage.class);
                startActivity(intent);
            }
        });


        materialSearchBar = (MaterialSearchBar) findViewById(R.id.searchBar);
        materialSearchBar.setCardViewElevation(0);
        getdoc();
        materialSearchBar.addTextChangeListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence text, int start, int before, int count) {

                txt = text.toString();
                if (txt.isEmpty()) {
                    adapter.stopListening();
                    getdoc();
                } else {
                    adapter.stopListening();
                    txt = txt.substring(0, 1).toUpperCase() + txt.substring(1).toLowerCase();
                    searchDoc(txt.toString());
                    Toast.makeText(Secretary_schedlist.this, txt.toString(), Toast.LENGTH_SHORT).show();

                }
            }

            @Override
            public void afterTextChanged(Editable s) {


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
        adapter.startListening();

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
    private class DoctorViewHolder extends RecyclerView.ViewHolder{
        private TextView doc_name;



        public DoctorViewHolder(@NonNull View itemView) {
            super(itemView);

            doc_name = itemView.findViewById(R.id.secsched_docname);


        }
    }

    private void getdoc(){
        mFirestoreList = (RecyclerView) findViewById(R.id.sec_sched_recview);
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        List<String> Doc = new ArrayList<>();
        firebaseFirestore.collection("Doctors").whereEqualTo("ClinicName", docn).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull  Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    if(!task.getResult().isEmpty()){

                        for(QueryDocumentSnapshot documnt : task.getResult()) {
                            String docuid = documnt.getString("UserId");
                            Doc.add(docuid);
                        }
                        Query query = firebaseFirestore.collection("Doctors").whereIn("UserId", Doc).orderBy("LastName");

                        FirestoreRecyclerOptions<DoctorModel> options = new FirestoreRecyclerOptions.Builder<DoctorModel>()
                                .setQuery(query, DoctorModel.class)
                                .build();

                        adapter = new FirestoreRecyclerAdapter<DoctorModel, Secretary_schedlist.DoctorViewHolder>(options) {
                            @NonNull
                            @Override
                            public Secretary_schedlist.DoctorViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.sec_schedlist_single,parent,false);
                                return new Secretary_schedlist.DoctorViewHolder(view);
                            }
                            @Override
                            protected void onBindViewHolder(@NonNull  Secretary_schedlist.DoctorViewHolder holder, int position, DoctorModel model) {
                                holder.doc_name.setText("Doctor " + model.getLastName());
                                //holder.doc_email.setText(model.getEmail());
                                holder.itemView.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {

                                        Intent intent = new Intent(getApplicationContext(), Secretary_schedlist_patsched.class);
                                        intent.putExtra("docid", model.getUserId());
                                        intent.putExtra("docname", "Doctor " + model.getFirstName());
                                        startActivity(intent);



                                    }
                                });
                            }





                        };
                        mFirestoreList.setHasFixedSize(true);
                        mFirestoreList.setLayoutManager(new LinearLayoutManager(Secretary_schedlist.this));
                        mFirestoreList.setAdapter(adapter);
                        adapter.startListening();


                    }
                }
            }
        });


    }
    private void searchDoc(String text){
        mFirestoreList = (RecyclerView) findViewById(R.id.sec_sched_recview);
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();

        List<String> Doc = new ArrayList<>();
        firebaseFirestore.collection("Doctors").whereEqualTo("ClinicName", docn).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull  Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    if(!task.getResult().isEmpty()){

                        for(QueryDocumentSnapshot document : task.getResult()) {
                            String docuid = document.getString("UserId");

                            Doc.add(docuid);

                        }
                        Query query = firebaseFirestore.collection("Doctors").whereIn("UserId", Doc).orderBy("LastName").startAt(text).endAt(text + '\uf8ff');

                        FirestoreRecyclerOptions<DoctorModel> options = new FirestoreRecyclerOptions.Builder<DoctorModel>()
                                .setQuery(query, DoctorModel.class)
                                .build();

                        adapter = new FirestoreRecyclerAdapter<DoctorModel, Secretary_schedlist.DoctorViewHolder>(options) {
                            @NonNull
                            @Override
                            public Secretary_schedlist.DoctorViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.sec_schedlist_single,parent,false);
                                return new Secretary_schedlist.DoctorViewHolder(view);
                            }

                            @Override
                            protected void onBindViewHolder(@NonNull  Secretary_schedlist.DoctorViewHolder holder, int position,  DoctorModel model) {
                                holder.doc_name.setText("Doctor " + model.getLastName());
                               // holder.doc_email.setText(model.getEmail());
                                holder.itemView.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {

                                        Intent intent = new Intent(getApplicationContext(), Secretary_schedlist_patsched.class);
                                        intent.putExtra("docid", model.getUserId());
                                        intent.putExtra("docname", "Doctor " + model.getLastName());
                                        startActivity(intent);



                                    }
                                });
                            }

                        };
                        mFirestoreList.setHasFixedSize(true);
                        mFirestoreList.setLayoutManager(new LinearLayoutManager(Secretary_schedlist.this));
                        mFirestoreList.setAdapter(adapter);
                        adapter.startListening();

                    }
                }
            }
        });

    }

}