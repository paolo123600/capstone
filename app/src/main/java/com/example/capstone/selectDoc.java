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
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.lang.ref.Reference;
import java.util.ArrayList;
import java.util.List;

public class selectDoc extends AppCompatActivity {
    private RecyclerView doctorlist;
    private  FirestoreRecyclerAdapter adapter;

    private String doclastname;

    ImageView back;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_doc);



        // Start
        doctorlist= (RecyclerView) findViewById(R.id.DoctorRF);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Query clinicsRef = db.collection("Clinics").whereEqualTo("Status","Registered");
        Spinner spinner = (Spinner) findViewById(R.id.spinnerclinic);
        List<String> Clinics = new ArrayList<>();
        ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_item, Clinics);
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter1);

        back = findViewById(R.id.backspace);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), PaymentMethod.class);
                startActivity(intent);
            }
        });

        clinicsRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        String subject = document.getString("ClinicName");

                        Clinics.add(subject);
                    }
                    for (String clinics  : Clinics ){
                        db.collection("Doctors").whereEqualTo("ClinicName",clinics).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()){
                                    if (task.getResult().isEmpty()){
                                        Clinics.remove(clinics);
                                    }
                                }
                            }
                        });
                    }
                    adapter1.notifyDataSetChanged();
                }
            }
        });

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {


            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                String Clinicname = spinner.getSelectedItem().toString();
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
                        holder.list_docname.setText("Doc "+model.getLastName());
                        holder.list_docemail.setText(model.getEmail());
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
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });



    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), PaymentMethod.class);
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