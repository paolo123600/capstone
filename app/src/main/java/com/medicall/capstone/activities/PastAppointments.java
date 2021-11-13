package com.medicall.capstone.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.medicall.capstone.PastSchedstatus;
import com.medicall.capstone.R;
import com.medicall.capstone.secretary.SecretaryPatschedModel;
import com.medicall.capstone.secretary.Secretary_schedlist_patsched_past;
import com.medicall.capstone.utilities.PreferenceManager;

import org.w3c.dom.Text;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class PastAppointments extends AppCompatActivity {

    FirebaseAuth fAuth;
    FirebaseFirestore db;
    RecyclerView mFirestoreList;
    TextView history;

    String patuid;

    private FirestoreRecyclerAdapter adapter;

    PreferenceManager preferenceManager;

    ImageView back;

    private StorageReference storageReference;
    private FirebaseStorage storage;
    Bitmap getpic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_past_appointments);

        mFirestoreList = findViewById(R.id.past_appointment_secsched);
        fAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        preferenceManager = new PreferenceManager(getApplicationContext());

        history = findViewById(R.id.noHistory);

        Intent intent = getIntent();
        patuid = intent.getStringExtra("patuid");

        back = findViewById(R.id.backspace);
        getHistory();

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        db.collection("Schedules").whereEqualTo("PatientUId", patuid).whereEqualTo("Status","Completed")
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    QuerySnapshot querySnapshot = task.getResult();
                    if(!querySnapshot.isEmpty()){
                        for(QueryDocumentSnapshot nohistory : task.getResult()){
                            history.setVisibility(TextView.GONE);
                        }
                    }
                    else{
                        history.setVisibility(TextView.VISIBLE);
                    }
                }
            }
        });
    }

    public void getHistory(){
        Query query = db.collection("Schedules").whereEqualTo("PatientUId", patuid).whereEqualTo("Status", "Completed").orderBy("Date", Query.Direction.DESCENDING).limit(20);

        FirestoreRecyclerOptions<SecretaryPatschedModel> options = new FirestoreRecyclerOptions.Builder<SecretaryPatschedModel>().setQuery(query, SecretaryPatschedModel.class).build();

        adapter = new FirestoreRecyclerAdapter<SecretaryPatschedModel, PastAppointments.SecretaryPatSchedViewHolder>(options) {
            @NonNull
            @Override
            public PastAppointments.SecretaryPatSchedViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.sec_schedlist_patsched_single, parent, false);
                return new PastAppointments.SecretaryPatSchedViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull PastAppointments.SecretaryPatSchedViewHolder holder, int position, @NonNull SecretaryPatschedModel model) {
                Date datesched =model.getDate();
                String patientid = model.getPatientUId();
                SimpleDateFormat format = new SimpleDateFormat("MMMM d ,yyyy");
                String date=  format.format(datesched);
                holder.list_timesched.setVisibility(View.GONE);
                holder.list_datesched.setText(date);
                db.collection("Patients").document(model.getPatientUId()).get()
                        .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                documentSnapshot.getData();
                                String patname = documentSnapshot.getString("LastName") + ", " +documentSnapshot.getString("FirstName");
                                holder.list_name.setText(patname);
                            }
                        });

                db.collection("Patients").whereEqualTo("StorageId", patientid).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            QuerySnapshot querySnapshot = task.getResult();
                            if(!querySnapshot.isEmpty()){
                                for(QueryDocumentSnapshot pat : task.getResult()){
                                    String storageid = pat.getString("StorageId");
                                    if(storageid.equals("None")){
                                        holder.profilepic.setBackgroundResource(R.drawable.circlebackground);
                                    }
                                    else{
                                        storageReference = FirebaseStorage.getInstance().getReference("PatientPicture/" + storageid);
                                        try{
                                            File local = File.createTempFile("patDP", "");
                                            storageReference.getFile(local).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                                                @Override
                                                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                                    getpic = BitmapFactory.decodeFile(local.getAbsolutePath());
                                                    holder.profilepic.setImageBitmap(getpic);
                                                    holder.itemView.setOnClickListener(new View.OnClickListener() {
                                                        @Override
                                                        public void onClick(View v) {
                                                            String documentId = getSnapshots().getSnapshot(position).getId();
                                                            Intent intent1 = new Intent(getApplicationContext(), PastSchedstatus.class);
                                                            intent1.putExtra("documentid",documentId);
                                                            startActivity(intent1);
                                                        }
                                                    });
                                                }
                                            });
                                        }
                                        catch(IOException e){
                                            e.printStackTrace();
                                        }
                                    }
                                }
                            }
                        }
                    }
                });
            }
        };

        mFirestoreList.setHasFixedSize(true);
        mFirestoreList.setLayoutManager(new LinearLayoutManager(PastAppointments.this));
        mFirestoreList.setAdapter(adapter);
    }

    private class SecretaryPatSchedViewHolder extends RecyclerView.ViewHolder{

        private TextView list_name, list_datesched, list_timesched;
        private ImageView profilepic;


        public SecretaryPatSchedViewHolder(@NonNull View itemView) {
            super(itemView);

            list_name = itemView.findViewById(R.id.secsched_patname);
            list_datesched = itemView.findViewById(R.id.secsched_datesched);
            profilepic = itemView.findViewById(R.id.patient_profilepic);
            list_timesched = itemView.findViewById(R.id.secsched_timesched);
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