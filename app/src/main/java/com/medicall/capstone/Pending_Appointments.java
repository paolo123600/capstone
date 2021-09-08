package com.medicall.capstone;

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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.medicall.capstone.R;

import com.medicall.capstone.utilities.PreferenceManager;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Pending_Appointments extends AppCompatActivity {

    private RecyclerView pendinglist;
    private FirestoreRecyclerAdapter adapter;
    private PreferenceManager preferenceManager;
    GlobalVariables gv;
    FirebaseFirestore db;
    ImageView back;
    TextView noList;



    String patientname, schedule, docname, hmoname;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pending_appointment);
        back = findViewById(R.id.backspace);
        preferenceManager = new PreferenceManager(getApplicationContext());
        pendinglist = (RecyclerView) findViewById(R.id.PendingHMO);
        noList = findViewById(R.id.noPatient);

        gv = (GlobalVariables) getApplicationContext();
        SimpleDateFormat format = new SimpleDateFormat("MMMM d ,yyyy");
        db = FirebaseFirestore.getInstance();

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        db.collection("Schedules").whereEqualTo("ClinicName",preferenceManager.getString("ClinicName")).whereEqualTo("Status","Pending Approval")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if(error != null){
                            Toast.makeText(Pending_Appointments.this, "error listening", Toast.LENGTH_SHORT).show();
                        }
                        if(value.isEmpty()){
                            noList.setVisibility(View.VISIBLE);
                            pendinglist.setVisibility(View.GONE);
                        }
                        else{
                            noList.setVisibility(View.GONE);
                            pendinglist.setVisibility(View.VISIBLE);
                            Query query = db.collection("Schedules").whereEqualTo("ClinicName", preferenceManager.getString("ClinicName")).whereEqualTo("Status","Pending Approval");
                            FirestoreRecyclerOptions<PendingModel> options = new FirestoreRecyclerOptions.Builder<PendingModel>()
                                    .setQuery(query, PendingModel.class)
                                    .build();

                            adapter = new FirestoreRecyclerAdapter<PendingModel, PendingViewHolder>(options){
                                @NonNull
                                @Override
                                public PendingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                                    View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.pending_appointment_recyclerview, parent, false);
                                    return new Pending_Appointments.PendingViewHolder(view);
                                }

                                @Override
                                protected void onBindViewHolder(@NonNull PendingViewHolder holder, int position, @NonNull PendingModel model) {
                                    String datestring ;
                                    Date date = new Date();
                                    date = model.getDate();
                                    datestring= format.format(date);
                                    db.collection("Doctors").document(model.getDoctorUId())
                                            .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                            if(task.isSuccessful()){

                                                DocumentSnapshot doctor = task.getResult();
                                                docname = doctor.getString("FirstName") + " " + doctor.getString("LastName");
                                                holder.docName.setText("Doctor Name: " + docname);

                                                db.collection("Patients").document(model.getPatientUId())
                                                        .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                        if(task.isSuccessful()){
                                                            DocumentSnapshot patient = task.getResult();
                                                            patientname = patient.getString("FirstName") + " " + patient.getString("LastName");
                                                            holder.patName.setText("Patient Name: " + patientname);
                                                        }
                                                    }
                                                });
                                            }
                                        }
                                    });

                                    holder.schedtime.setText("Schedule: " + datestring);
                                    holder.itemView.setOnClickListener(new View.OnClickListener(){
                                        @Override
                                        public void onClick(View v) {

                                        }
                                    });

                                    holder.view_confirm.setOnClickListener(new View.OnClickListener(){
                                        @Override
                                        public void onClick(View v) {
                                            Intent intent = new Intent(Pending_Appointments.this, Pending_Confirmation.class);
                                            gv.setPending_docUid(model.getDoctorUId());
                                            gv.setPending_patUid(model.getPatientUId());
                                            gv.setPending_sched(datestring + " (" + model.getStartTime() + " - " + model.getEndTime() + ")" );
                                            gv.setPending_hmo(model.getHMOName());
                                            gv.setPending_sched(datestring + " (" + model.getStartTime() + " - " + model.getEndTime() + ")" );
                                            gv.setPending_cardNumber(model.getCardNumber());
                                            startActivity(intent);
                                        }
                                    });

                                }
                            };
                            pendinglist.setHasFixedSize(true);
                            pendinglist.setLayoutManager(new LinearLayoutManager(Pending_Appointments.this));
                            pendinglist.setAdapter(adapter);
                            adapter.startListening();
                        }
                    }
                });

    }

    private class PendingViewHolder extends RecyclerView.ViewHolder{
        private TextView patName;
        private TextView docName;
        private TextView schedtime;
        private Button view_confirm;
        public PendingViewHolder(@NonNull View itemView){
            super(itemView);
            patName = itemView.findViewById(R.id.pending_patientname);
            schedtime = itemView.findViewById(R.id.pending_sched);
            docName = itemView.findViewById(R.id.pending_doctorname);
            view_confirm = itemView.findViewById(R.id.pending_info);
        }
    }
}
