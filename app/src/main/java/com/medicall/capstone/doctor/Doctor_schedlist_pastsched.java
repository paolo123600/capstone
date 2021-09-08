package com.medicall.capstone.doctor;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.medicall.capstone.Login;
import com.medicall.capstone.R;
import com.medicall.capstone.secretary.Secretary_schedlist_patsched;
import com.medicall.capstone.utilities.PreferenceManager;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Doctor_schedlist_pastsched extends AppCompatActivity {

    FirebaseAuth fAuth;
    FirebaseFirestore db;
    RecyclerView mFirestoreList;

    String docuid;

    private FirestoreRecyclerAdapter adapter;

    PreferenceManager preferenceManager;

    String userId;
    private StorageReference storageReference;
    ImageView back;
    Bitmap getpic;

    TextView None;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_schedlist_pastsched);


        mFirestoreList = findViewById(R.id.Doc_past_sched_recview);
        fAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        preferenceManager = new PreferenceManager(getApplicationContext());

        Intent intent = getIntent();
        docuid = intent.getStringExtra("docuid");

        userId = fAuth.getCurrentUser().getUid();

        back = findViewById(R.id.backspace);
        None = findViewById(R.id.doc_past_sched_none);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Login.class);
                startActivity(intent);
            }
        });

        Query query = db.collection("Schedules").whereEqualTo("DoctorUId", userId).whereEqualTo("Status", "Completed").orderBy("Date", Query.Direction.DESCENDING).limit(20);

        FirestoreRecyclerOptions<DoctorUpcomingModel> options = new FirestoreRecyclerOptions.Builder<DoctorUpcomingModel>().setQuery(query, DoctorUpcomingModel.class).build();

        adapter = new FirestoreRecyclerAdapter<DoctorUpcomingModel, Doctor_schedlist_pastsched.DoctorUpcomingViewHolder>(options) {
            @NonNull
            @Override
            public Doctor_schedlist_pastsched.DoctorUpcomingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.doc_upcoming_schedlist_single, parent, false);
                return new Doctor_schedlist_pastsched.DoctorUpcomingViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull Doctor_schedlist_pastsched.DoctorUpcomingViewHolder holder, int position, @NonNull DoctorUpcomingModel model) {
                Date datesched =model.getDate();
                String patientid = model.getPatientUId();
                SimpleDateFormat format = new SimpleDateFormat("MMMM d ,yyyy");
                String date=  format.format(datesched);
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
        mFirestoreList.setLayoutManager(new LinearLayoutManager(this));
        mFirestoreList.setAdapter(adapter);

        db.collection("Schedules").whereEqualTo("DoctorUId", userId).whereEqualTo("Status", "Completed").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Toast.makeText(Doctor_schedlist_pastsched.this, "Error listening", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (value.isEmpty()) {
                    None.setVisibility(View.VISIBLE);
                    mFirestoreList.setVisibility(View.GONE);
                } else {
                    None.setVisibility(View.GONE);
                    mFirestoreList.setVisibility(View.VISIBLE);
                }
            }
        });

    }

    private class DoctorUpcomingViewHolder extends RecyclerView.ViewHolder{

        private TextView list_name, list_datesched;
        private ImageView profilepic;

        public DoctorUpcomingViewHolder(@NonNull View itemView) {
            super(itemView);

            list_name = itemView.findViewById(R.id.doc_upcomingsched_patname);
            list_datesched = itemView.findViewById(R.id.doc_upcomingsched_datesched);
            profilepic = itemView.findViewById(R.id.patient_profilepic);
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