package com.medicall.capstone;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.medicall.capstone.R;

import java.util.ArrayList;
import java.util.List;

public class selectDoc extends AppCompatActivity {
    private RecyclerView doctorlist;
    private  FirestoreRecyclerAdapter adapter;

    private String doclastname;
    GlobalVariables gv;
    ImageView back;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_doc);



        // Start
        doctorlist= (RecyclerView) findViewById(R.id.DoctorRF);
        FirebaseFirestore db = FirebaseFirestore.getInstance();


        gv = (GlobalVariables) getApplicationContext();
        back = findViewById(R.id.backspace);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), selectClinic.class);
                startActivity(intent);
            }
        });







                String Clinicname = gv.getSDClinic();


                //query
                Query query = db.collection("Doctors").whereEqualTo("ClinicName",Clinicname);
                FirestoreRecyclerOptions<DoctorModel> options = new FirestoreRecyclerOptions.Builder<DoctorModel>()
                        .setQuery(query,DoctorModel.class)
                        .build();
                //adapter
                adapter = new FirestoreRecyclerAdapter<DoctorModel, DoctorsViewHolder>(options) {
                    @NonNull
                    @Override
                    public DoctorsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.doctor_list_single,parent,false);
                        return new selectDoc.DoctorsViewHolder(view);
                    }

                    @Override
                    protected void onBindViewHolder(@NonNull DoctorsViewHolder holder, int position, @NonNull DoctorModel model) {
                        holder.list_docname.setText("Dr. "+model.getLastName());
                        holder.list_docemail.setText(model.getDocType());
                        holder.list_docclinic.setText(model.getClinic());
                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent(selectDoc.this, selectDate.class);
                                GlobalVariables gv =(GlobalVariables) getApplicationContext ();
                                gv.setSDDocemail(model.getEmail());
                                gv.setSDDocUid(model.getUserId());
                                gv.setSDDocLastName(model.getLastName());
                                gv.setSDClinic(Clinicname);
                                startActivity(intent);

                            }
                        });

                        holder.view_info.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(getApplicationContext(), View_doctor_info.class);
                                GlobalVariables gv =(GlobalVariables) getApplicationContext ();
                                gv.setSDDocUid(model.getUserId());
                                startActivity(intent);
                            }
                        });

                    }
                };

                doctorlist.setHasFixedSize(true);
                doctorlist.setLayoutManager(new LinearLayoutManager(selectDoc.this));
                doctorlist.setAdapter(adapter);
                adapter.startListening();

            }


    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), selectClinic.class);
        startActivity(intent);
    }

    private class DoctorsViewHolder extends RecyclerView.ViewHolder{
        private TextView list_docname;
        private TextView list_docemail;
        private TextView list_docclinic;
        private Button view_info;
        public DoctorsViewHolder(@NonNull View itemView){
            super(itemView);
            view_info = itemView.findViewById(R.id.viewdocinfo);
            list_docname = itemView.findViewById(R.id.list_patientname);
            list_docemail = itemView.findViewById(R.id.list_patemail);
            list_docclinic= itemView.findViewById(R.id.list_docclinic);
        }
    }
}