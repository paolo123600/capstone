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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.medicall.capstone.Model.PatientHMOModel;
import com.medicall.capstone.secretary.SecretaryListModel;
import com.medicall.capstone.utilities.PreferenceManager;

public class selectClinic extends AppCompatActivity {

    FirebaseFirestore db;
    GlobalVariables gv;
    private FirestoreRecyclerAdapter adapter;
    FirebaseAuth firebaseAuth;
    String userID;
    RecyclerView hmoList;
    PreferenceManager preferenceManager;



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


        hmoList = (RecyclerView) findViewById(R.id.HMOList);
        back = findViewById(R.id.backspace);
        None = findViewById(R.id.list_none);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }
        });



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



    private class SecretaryListModelView extends RecyclerView.ViewHolder{
        private TextView nameCLINIC;
        public SecretaryListModelView(@NonNull View itemView){
            super(itemView);
            nameCLINIC = itemView.findViewById(R.id.seclist_name);
        }
    }
}