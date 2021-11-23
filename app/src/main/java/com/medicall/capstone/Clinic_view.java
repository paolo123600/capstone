package com.medicall.capstone;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
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
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.medicall.capstone.R;

import java.io.File;
import java.io.IOException;

import de.hdodenhof.circleimageview.CircleImageView;

public class Clinic_view extends AppCompatActivity {

    private FirebaseFirestore firebaseFirestore;
    private RecyclerView mClinicList;

    private FirestoreRecyclerAdapter adapter;

    ImageView back;
    private StorageReference storageReference;
    private FirebaseStorage storage;
    StorageReference ref;
    Bitmap getpic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clinic_view);

        firebaseFirestore = FirebaseFirestore.getInstance();
        mClinicList = findViewById(R.id.scheddoc_list);

        back = findViewById(R.id.backspace);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        //Query
        Query query = firebaseFirestore.collection("Secretary");
        //RecyclerOptions
        FirestoreRecyclerOptions<ClinicModel> options = new FirestoreRecyclerOptions.Builder<ClinicModel>()
                .setQuery(query, ClinicModel.class)
                .build();

        adapter = new FirestoreRecyclerAdapter<ClinicModel, ClinicViewHolder>(options) {
            @NonNull
            @Override
            public ClinicViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_clinic, parent, false);
                return new ClinicViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull ClinicViewHolder holder, int position, @NonNull ClinicModel model) {

                firebaseFirestore.collection("Clinics").whereEqualTo("ClinicName",model.getClinicName()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            QuerySnapshot querySnapshot = task.getResult();
                            if(!querySnapshot.isEmpty()){
                                for(QueryDocumentSnapshot picture : task.getResult()){
                                    if(picture.getString("StorageId").isEmpty()){

                                    }
                                    else{
                                        storageReference = FirebaseStorage.getInstance().getReference("ClinicPicture/" + picture.getString("StorageId"));
                                        try {
                                            File local = File.createTempFile("myProfilePicture","");
                                            storageReference.getFile(local).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                                                @Override
                                                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                                    getpic = BitmapFactory.decodeFile(local.getAbsolutePath());
                                                    holder.clinicPicture.setImageBitmap(getpic);
                                                }
                                            });
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                    }

                                }
                            }
                        }
                    }
                });

                holder.clinic_list.setText(model.getClinicName());
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(getApplicationContext(), MessageActivity.class);
                        intent.putExtra("friendid", model.getUserId());
                        intent.putExtra("name", model.getClinicName());
                        intent.putExtra("usertype", "Secretary");
                        intent.putExtra("type", "Patients");
                        startActivity(intent);
                    }
                    });
                }

        };

        mClinicList.setHasFixedSize(true);
        mClinicList.setLayoutManager(new LinearLayoutManager(this));
        mClinicList.setAdapter(adapter);

    }

    private class ClinicViewHolder extends RecyclerView.ViewHolder {

        private TextView clinic_list;
        private CircleImageView clinicPicture;

        public ClinicViewHolder(@NonNull View itemView) {
            super(itemView);
            clinicPicture = itemView.findViewById(R.id.clinicPicture);
            clinic_list = itemView.findViewById(R.id.clinic_name);

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