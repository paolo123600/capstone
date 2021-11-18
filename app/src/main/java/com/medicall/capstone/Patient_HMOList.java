package com.medicall.capstone;

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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.medicall.capstone.Model.PatientHMOModel;

import com.medicall.capstone.R;

import com.medicall.capstone.utilities.PreferenceManager;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class Patient_HMOList extends AppCompatActivity {

    Button Edit, Delete, Add;
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
    public void onBackPressed() {

        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_hmo_list);
        db = FirebaseFirestore.getInstance();
        gv = (GlobalVariables) getApplicationContext();
        firebaseAuth = FirebaseAuth.getInstance();
        preferenceManager = new PreferenceManager(getApplicationContext());

        userID = firebaseAuth.getCurrentUser().getUid();
        hmoList = (RecyclerView) findViewById(R.id.HMOList);
        Add = (Button) findViewById(R.id.hmo_add);
        back = findViewById(R.id.backspace);
        None = findViewById(R.id.list_none);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }
        });

        Add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent addhmo = new Intent(Patient_HMOList.this, Patient_HMOAdd.class);
                startActivity(addhmo);
            }
        });

        Query query = db.collection("Patients").document(userID).collection("HMO");
        FirestoreRecyclerOptions<PatientHMOModel> options = new FirestoreRecyclerOptions.Builder<PatientHMOModel>()
                .setQuery(query, PatientHMOModel.class)
                .build();





        adapter = new FirestoreRecyclerAdapter<PatientHMOModel, PatientHMOModelView>(options) {
            @NonNull
            @Override
            public PatientHMOModelView onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.hmo_list_recyclerview, parent, false);
                return new Patient_HMOList.PatientHMOModelView(view);



            }



            @Override
            protected void onBindViewHolder(@NonNull PatientHMOModelView holder, int position, @NonNull PatientHMOModel model){

                holder.nameHMO.setText("HMO: " + model.getHMOName());
                holder.cardHMO.setText("Card Number: " + model.getCardNumber());
                holder.expiryHMO.setText("Expiry Date: " + model.getExpiryDate());
                holder.contactHMO.setText("Provider Contact: " + model.getHMOCNumber());
                String hmoAddress = model.getHMOAddress();
                holder.editHMO.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(Patient_HMOList.this, Patient_HMOEdit.class);
                        gv.setEditHMO_cardNumber(model.getCardNumber());
                        gv.setEditHMO_hmoName(model.getHMOName());
                        gv.setEditHMO_expirydate(model.getExpiryDate());
                        gv.setEditHMO_contact(model.getHMOCNumber());
                        gv.setEditHMO_Address(hmoAddress);
                        startActivity(intent);
                    }
                });

                holder.deleteHMO.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(Patient_HMOList.this);
                        builder.setCancelable(true);
                        builder.setTitle("Delete Health Insurance");
                        builder.setMessage("Do you want to delete this HMO?" +
                                "HMO Name: " + model.getHMOName());
                        builder.setPositiveButton("Confirm",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        db.collection("Patients").document(userID).collection("HMO").document(model.getHMOName())
                                                .delete()
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        Toast.makeText(Patient_HMOList.this, "Delete Successful!", Toast.LENGTH_SHORT).show();
                                                    }
                                                })
                                                .addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {

                                                    }
                                                });
                                    }
                                });
                        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });
                        AlertDialog dialog = builder.create();
                        dialog.show();

                    }
                });
            }
        };
        hmoList.setHasFixedSize(true);
        hmoList.setLayoutManager(new LinearLayoutManager(this));
        hmoList.setAdapter(adapter);
        adapter.startListening();


        db.collection("Patients").document(userID).collection("HMO").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Toast.makeText(Patient_HMOList.this, "Error listening", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (value.isEmpty()) {
                    None.setVisibility(View.VISIBLE);
                    hmoList.setVisibility(View.GONE);
                } else {
                    None.setVisibility(View.GONE);
                    hmoList.setVisibility(View.VISIBLE);
                }
            }
        });


    }



    private class PatientHMOModelView extends RecyclerView.ViewHolder{
        private TextView nameHMO;
        private Button editHMO;
        private Button deleteHMO;
        private TextView cardHMO;
        private TextView expiryHMO;
        private TextView contactHMO;
        public PatientHMOModelView(@NonNull View itemView){
            super(itemView);
            cardHMO = itemView.findViewById(R.id.card_number);
            nameHMO = itemView.findViewById(R.id.hmo_name);
            expiryHMO = itemView.findViewById(R.id.hmo_expiry);
            editHMO = itemView.findViewById(R.id.hmo_edit);
            deleteHMO = itemView.findViewById(R.id.hmo_delete);
            contactHMO = itemView.findViewById(R.id.hmo_contact);
        }
    }
}
