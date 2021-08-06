package com.example.capstone;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.capstone.Model.DocHMO;
import com.example.capstone.Model.HMOModel;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class selectDoc_hmo extends AppCompatActivity {

    private RecyclerView doctorlist;
    private FirestoreRecyclerAdapter adapter;
    private String doclastname;

    List <DocHMO> DoctorHMO;
    List <HMOModel> HMOList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_doc_hmo);

        // Start
        doctorlist= (RecyclerView) findViewById(R.id.DoctorRF);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference clinicsRef = db.collection("HMO");
        Spinner spinner = (Spinner) findViewById(R.id.spinnerhmo);
        List<String> hmo = new ArrayList<>();
        ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_item, hmo);
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter1);
        clinicsRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        String subject = document.getString("HMOName");

                        hmo.add(subject);
                    }
                    adapter1.notifyDataSetChanged();
                }
            }
        });

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {


            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                String Clinicname = spinner.getSelectedItem().toString();
                DoctorHMO = new ArrayList<>();
                //query
                Query query = db.collection("Doctors");
                FirestoreRecyclerOptions<DoctorModel> options = new FirestoreRecyclerOptions.Builder<DoctorModel>()
                        .setQuery(query,DoctorModel.class)
                        .build();
                //adapter
                adapter = new FirestoreRecyclerAdapter<DoctorModel, selectDoc_hmo.DoctorsViewHolder>(options) {
                    @NonNull
                    @Override
                    public selectDoc_hmo.DoctorsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.doctor_list_single,parent,false);
                        return new selectDoc_hmo.DoctorsViewHolder(view);
                    }

                    @Override
                    protected void onBindViewHolder(@NonNull selectDoc_hmo.DoctorsViewHolder holder, int position, @NonNull DoctorModel model) {
                        holder.list_docname.setText("Doc "+model.getLastName());
                        holder.list_docemail.setText(model.getEmail());
                        holder.list_docclinic.setText(model.getClinic());
                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent(selectDoc_hmo.this, selectDate_hmo.class);
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
                doctorlist.setLayoutManager(new LinearLayoutManager(selectDoc_hmo.this));
                doctorlist.setAdapter(adapter);
                adapter.startListening();

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

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
