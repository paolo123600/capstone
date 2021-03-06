package com.medicall.capstone.secretary;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
import com.medicall.capstone.PastSchedstatus;
import com.medicall.capstone.Patient_HMOList;
import com.medicall.capstone.R;
import com.medicall.capstone.UpcomingSchedStatus;
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
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

public class Secretary_schedlist_patsched extends AppCompatActivity {

    FirebaseAuth fAuth;
    FirebaseFirestore db;
    RecyclerView mFirestoreList;
    Button pastsched;

    String docuid;

    private FirestoreRecyclerAdapter adapter;

    PreferenceManager preferenceManager;

    ImageView back;
    TextView None;

    private StorageReference storageReference;
    private FirebaseStorage storage;
    Bitmap getpic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_secretary_schedlist_patsched);

        pastsched = findViewById(R.id.sec_past_sched);
        None = findViewById(R.id.sec_patsched_none);

        mFirestoreList = findViewById(R.id.sec_sched_recview);
        fAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        preferenceManager = new PreferenceManager(getApplicationContext());

        Intent intent = getIntent();
        docuid = intent.getStringExtra("docuid");

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        back = findViewById(R.id.backspace);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Secretary_schedlist.class);
                startActivity(intent);
            }
        });

        Date currentTime = Calendar.getInstance().getTime();
        Query query = db.collection("Schedules").whereEqualTo("DoctorUId", docuid).whereEqualTo("ClinicName", preferenceManager.getString("ClinicName")).whereIn("Status", Arrays.asList("Paid","Approved")).whereGreaterThanOrEqualTo("Date",currentTime).orderBy("Date", Query.Direction.ASCENDING).limit(20);

        FirestoreRecyclerOptions<SecretaryPatschedModel> options = new FirestoreRecyclerOptions.Builder<SecretaryPatschedModel>().setQuery(query, SecretaryPatschedModel.class).build();

        pastsched.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Secretary_schedlist_patsched_past.class);
                intent.putExtra("docuid", docuid);
                startActivity(intent);
            }
        });

        adapter = new FirestoreRecyclerAdapter<SecretaryPatschedModel, Secretary_schedlist_patsched.SecretaryPatSchedViewHolder>(options) {
            @NonNull
            @Override
            public Secretary_schedlist_patsched.SecretaryPatSchedViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.sec_schedlist_patsched_single, parent, false);
                return new Secretary_schedlist_patsched.SecretaryPatSchedViewHolder(view);
            }


            @Override
            protected void onBindViewHolder(@NonNull Secretary_schedlist_patsched.SecretaryPatSchedViewHolder holder, int position, @NonNull SecretaryPatschedModel model) {
                Date datesched =model.getDate();
                String patientid = model.getPatientUId();
                SimpleDateFormat format = new SimpleDateFormat("MMMM d ,yyyy");
                String date=  format.format(datesched);
                holder.list_datesched.setText(date);
                holder.list_timesched.setText(model.getStartTime()+" - "+model.getEndTime());
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
                                            holder.itemView.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    String documentId = getSnapshots().getSnapshot(position).getId();
                                                    Intent intent1 = new Intent(getApplicationContext(), UpcomingSchedStatus.class);
                                                    intent1.putExtra("documentid",documentId);
                                                    startActivity(intent1);
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
        Date currentTime1 = Calendar.getInstance().getTime();
        db.collection("Schedules").whereEqualTo("DoctorUId", docuid).whereEqualTo("ClinicName", preferenceManager.getString("ClinicName")).whereEqualTo("Status", "Paid").whereGreaterThanOrEqualTo("Date",currentTime1).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Toast.makeText(Secretary_schedlist_patsched.this, "Error listening", Toast.LENGTH_SHORT).show();
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

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), Secretary_schedlist.class);
        startActivity(intent);
    }

    private class SecretaryPatSchedViewHolder extends RecyclerView.ViewHolder{

        private TextView list_name, list_datesched ,list_timesched;
        private ImageView profilepic;

        public SecretaryPatSchedViewHolder(@NonNull View itemView) {
            super(itemView);

            list_name = itemView.findViewById(R.id.secsched_patname);
            list_datesched = itemView.findViewById(R.id.secsched_datesched);
            profilepic = itemView.findViewById(R.id.patient_profilepic);
            list_timesched=itemView.findViewById(R.id.secsched_timesched);

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