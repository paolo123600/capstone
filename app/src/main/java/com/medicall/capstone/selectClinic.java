package com.medicall.capstone;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.DialogInterface;
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

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.mancj.materialsearchbar.MaterialSearchBar;
import com.medicall.capstone.Model.PatientHMOModel;
import com.medicall.capstone.secretary.SecretaryListModel;
import com.medicall.capstone.utilities.PreferenceManager;

import java.util.ArrayList;
import java.util.List;

import kotlin.text.UStringsKt;

public class selectClinic extends AppCompatActivity {

    FirebaseFirestore db;
    GlobalVariables gv;
    private FirestoreRecyclerAdapter adapter;
    FirebaseAuth firebaseAuth;
    String userID;
    RecyclerView hmoList;
    PreferenceManager preferenceManager;
    MaterialSearchBar searchClinic;
    String results;

    ImageView back;
    TextView None;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_clinic);
        db = FirebaseFirestore.getInstance();
        gv = (GlobalVariables) getApplicationContext();
        firebaseAuth = FirebaseAuth.getInstance();
        preferenceManager = new PreferenceManager(getApplicationContext());

        searchClinic = (MaterialSearchBar) findViewById(R.id.clinicSearchBar);
        searchClinic.setCardViewElevation(0);

        hmoList = (RecyclerView) findViewById(R.id.HMOList);
        back = findViewById(R.id.backspace);
        None = findViewById(R.id.list_none);


        getClinic();

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }
        });

        searchClinic.addTextChangeListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence result, int start, int before, int count) {
                results = result.toString();
                if(results.isEmpty()){
                    adapter.stopListening();
                    getClinic();
                }
                else{
                    adapter.stopListening();
                    results = results.substring(0,1).toUpperCase() + results.substring(1).toLowerCase();
                    SearchClinic(results.toString());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), PaymentMethod.class);
        startActivity(intent);
    }

    private void getClinic(){
        Query query = db.collection("Clinics");
        FirestoreRecyclerOptions<SecretaryListModel> options = new FirestoreRecyclerOptions.Builder<SecretaryListModel>()
                .setQuery(query, SecretaryListModel.class)
                .build();

        adapter = new FirestoreRecyclerAdapter<SecretaryListModel, selectClinic.SecretaryListModelView>(options) {
            @NonNull
            @Override
            public selectClinic.SecretaryListModelView onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.select_sec_list_single, parent, false);
                return new selectClinic.SecretaryListModelView(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull selectClinic.SecretaryListModelView holder, int position, @NonNull SecretaryListModel model){

                holder.nameCLINIC.setText(model.getClinicName());
                holder.viewclinfo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        DocumentSnapshot snapshot = getSnapshots().getSnapshot(holder.getAdapterPosition());
                        final String clinicid = snapshot.getId();
                        Intent intent = new Intent(getApplicationContext(), selectClinic_info.class);
                        intent.putExtra("ClinicUid", clinicid);
                        startActivity(intent);
                    }
                });
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getApplicationContext(), selectDoc.class);
                        gv.setSDClinic(model.getClinicName());
                        startActivity(intent);
                    }
                });
            }
        };
        hmoList.setHasFixedSize(true);
        hmoList.setLayoutManager(new LinearLayoutManager(this));
        hmoList.setAdapter(adapter);
        adapter.startListening();
    }

    private void SearchClinic(String result){
        List<String> SearchedClinic = new ArrayList<>();
        db.collection("Clinics").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    if(!task.getResult().isEmpty()){
                        for(QueryDocumentSnapshot document : task.getResult()){
                            Query query = db.collection("Clinics").orderBy("ClinicName").startAt(result).endAt(result + '\uf8ff');
                            FirestoreRecyclerOptions<SecretaryListModel> options = new FirestoreRecyclerOptions.Builder<SecretaryListModel>()
                                    .setQuery(query, SecretaryListModel.class)
                                    .build();

                            adapter = new FirestoreRecyclerAdapter<SecretaryListModel, selectClinic.SecretaryListModelView>(options) {
                                @NonNull
                                @Override
                                public selectClinic.SecretaryListModelView onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
                                    View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.select_sec_list_single, parent, false);
                                    return new selectClinic.SecretaryListModelView(view);
                                }

                                @Override
                                protected void onBindViewHolder(@NonNull selectClinic.SecretaryListModelView holder, int position, @NonNull SecretaryListModel model){

                                    holder.nameCLINIC.setText(model.getClinicName());
                                    holder.itemView.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            Intent intent = new Intent(getApplicationContext(), selectDoc.class);
                                            gv.setSDClinic(model.getClinicName());
                                            startActivity(intent);
                                        }
                                    });
                                }
                            };
                            hmoList.setHasFixedSize(true);
                            hmoList.setLayoutManager(new LinearLayoutManager(selectClinic.this));
                            hmoList.setAdapter(adapter);
                            adapter.startListening();
                        }
                    }
                }
            }
        });
    }



    private class SecretaryListModelView extends RecyclerView.ViewHolder{
        private TextView nameCLINIC;
        private Button viewclinfo;
        public SecretaryListModelView(@NonNull View itemView){
            super(itemView);
            nameCLINIC = itemView.findViewById(R.id.seclist_name);
            viewclinfo = itemView.findViewById(R.id.cl_info);
        }
    }
}