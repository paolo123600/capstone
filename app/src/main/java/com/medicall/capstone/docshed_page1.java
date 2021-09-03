package com.medicall.capstone;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.medicall.capstone.R;

import com.medicall.capstone.utilities.PreferenceManager;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.mancj.materialsearchbar.MaterialSearchBar;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class docshed_page1 extends AppCompatActivity {


    private RecyclerView mFirestoreList;
    private RecyclerView test;
    private FirebaseFirestore db;
    FirestoreRecyclerAdapter adapter;
    private PreferenceManager preferenceManager;
    MaterialSearchBar materialSearchBar;
    String txt;

    private String docn;

    private StorageReference storageReference;
    private FirebaseStorage storage;
    String image;
    Bitmap getpic;

    ImageView back;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_docshed_page1);
        preferenceManager = new PreferenceManager(getApplicationContext());
        docn = preferenceManager.getString("ClinicName");
        getdoc();

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        back = findViewById(R.id.backspace);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Login.class);
                startActivity(intent);
            }
        });

        materialSearchBar = (MaterialSearchBar) findViewById(R.id.searchBar);
        materialSearchBar.setCardViewElevation(0);
        materialSearchBar.addTextChangeListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence text, int start, int before, int count) {
                txt = text.toString();
                if (txt.isEmpty()) {
                    adapter.stopListening();
                    getdoc();
                } else {
                    adapter.stopListening();
                    txt = txt.substring(0, 1).toUpperCase() + txt.substring(1).toLowerCase();
                    searchDoc(txt.toString());

                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        db = FirebaseFirestore.getInstance();
        mFirestoreList = findViewById(R.id.docsched_list);
        test = findViewById(R.id.docsched_list);
        preferenceManager = new PreferenceManager(getApplicationContext());
        Query query = db.collection("Doctors").whereEqualTo("ClinicName",preferenceManager.getString("ClinicName"));



        FirestoreRecyclerOptions<DoctorModel> options = new FirestoreRecyclerOptions.Builder<DoctorModel>()
                .setQuery(query,DoctorModel.class)
                .build();

        adapter = new FirestoreRecyclerAdapter<DoctorModel, DoctorViewHolder>(options) {
            @NonNull
            @Override
            public DoctorViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_doc_page1, parent , false);
                return new DoctorViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull DoctorViewHolder holder, int position, @NonNull DoctorModel model) {
                holder.doc_name.setText("Doctor " + model.getLastName());
                holder.doc_type.setText(model.getDocType());
                String DocUid = model.getUserId();
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        Intent intent = new Intent(getApplicationContext(), docsched_page2.class);
                        intent.putExtra("docid", model.getUserId());
                        intent.putExtra("docname", "Doctor " + model.getLastName());
                        startActivity(intent);

                    }
                });

                db.collection("Doctors").whereEqualTo("StorageId", DocUid).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            QuerySnapshot querySnapshot = task.getResult();
                            if (!querySnapshot.isEmpty()) {
                                for (QueryDocumentSnapshot profile : task.getResult()) {
                                    image = profile.getString("StorageId");
                                    storageReference = FirebaseStorage.getInstance().getReference("DoctorPicture/" + image);
                                    try {
                                        File local = File.createTempFile("myProfilePicture","");
                                        storageReference.getFile(local).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                                            @Override
                                            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                                getpic = BitmapFactory.decodeFile(local.getAbsolutePath());
                                                holder.profpicturedoc.setImageBitmap(getpic);
                                            }
                                        });
                                    } catch (IOException e) {
                                        e.printStackTrace();
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
        adapter.startListening();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), Login.class);
        startActivity(intent);
    }

    private class DoctorViewHolder extends RecyclerView.ViewHolder{
        private TextView doc_name;
        private TextView doc_email;
        private TextView doc_type;
        private TextView listFirstname;
        private TextView listemail;
        private CircleImageView profpicturedoc;
        public DoctorViewHolder(@NonNull View itemView) {
            super(itemView);

            profpicturedoc = itemView.findViewById(R.id.doc_dp_schedlist);
            doc_name = itemView.findViewById(R.id.list_docname);
            doc_type = itemView.findViewById(R.id.page1_doctype);

        }
    }



    private void getdoc(){
        mFirestoreList = (RecyclerView) findViewById(R.id.docsched_list);
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();

        List<String> Doc = new ArrayList<>();
        firebaseFirestore.collection("Doctors").whereEqualTo("ClinicName", docn).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull  Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    if(!task.getResult().isEmpty()){

                        for(QueryDocumentSnapshot documnt : task.getResult()) {
                            String docuid = documnt.getString("UserId");
                            Doc.add(docuid);
                        }
                        Query query = firebaseFirestore.collection("Doctors").whereIn("UserId", Doc).orderBy("LastName");

                        FirestoreRecyclerOptions<DoctorModel> options = new FirestoreRecyclerOptions.Builder<DoctorModel>()
                                .setQuery(query, DoctorModel.class)
                                .build();

                        adapter = new FirestoreRecyclerAdapter<DoctorModel, docshed_page1.DoctorViewHolder>(options) {
                            @NonNull
                            @Override
                            public docshed_page1.DoctorViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_doc_page1,parent,false);
                                return new docshed_page1.DoctorViewHolder(view);
                            }

                            @Override
                            protected void onBindViewHolder(@NonNull docshed_page1.DoctorViewHolder holder, int position,  DoctorModel model) {
                                holder.doc_name.setText("Doctor " + model.getLastName());
                                holder.doc_type.setText(model.getDocType());
                                String DocUid = model.getUserId();
                                holder.itemView.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {

                                        Intent intent = new Intent(getApplicationContext(), docsched_page2.class);
                                        intent.putExtra("docid", model.getUserId());
                                        intent.putExtra("docname", "Doctor " + model.getLastName());
                                        startActivity(intent);



                                    }
                                });

                                db.collection("Doctors").whereEqualTo("StorageId", DocUid).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        if (task.isSuccessful()) {
                                            QuerySnapshot querySnapshot = task.getResult();
                                            if (!querySnapshot.isEmpty()) {
                                                for (QueryDocumentSnapshot profile : task.getResult()) {
                                                    image = profile.getString("StorageId");
                                                    storageReference = FirebaseStorage.getInstance().getReference("DoctorPicture/" + image);
                                                    try {
                                                        File local = File.createTempFile("myProfilePicture","");
                                                        storageReference.getFile(local).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                                                            @Override
                                                            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                                                getpic = BitmapFactory.decodeFile(local.getAbsolutePath());
                                                                holder.profpicturedoc.setImageBitmap(getpic);
                                                            }
                                                        });
                                                    } catch (IOException e) {
                                                        e.printStackTrace();
                                                    }
                                                }
                                            }
                                        }
                                    }
                                });

                            }


                        };
                        mFirestoreList.setHasFixedSize(true);
                        mFirestoreList.setLayoutManager(new LinearLayoutManager(docshed_page1.this));
                        mFirestoreList.setAdapter(adapter);
                        adapter.startListening();


                    }
                }
            }
        });

    }

    private void searchDoc(String text){
        mFirestoreList = (RecyclerView) findViewById(R.id.docsched_list);
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();

        List<String> Doc = new ArrayList<>();
        firebaseFirestore.collection("Doctors").whereEqualTo("ClinicName", docn).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull  Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    if(!task.getResult().isEmpty()){

                        for(QueryDocumentSnapshot document : task.getResult()) {
                            String docuid = document.getString("UserId");

                            Doc.add(docuid);

                        }
                        Query query = firebaseFirestore.collection("Doctors").whereIn("UserId", Doc).orderBy("LastName").startAt(text).endAt(text + '\uf8ff');

                        FirestoreRecyclerOptions<DoctorModel> options = new FirestoreRecyclerOptions.Builder<DoctorModel>()
                                .setQuery(query, DoctorModel.class)
                                .build();

                        adapter = new FirestoreRecyclerAdapter<DoctorModel, docshed_page1.DoctorViewHolder>(options) {
                            @NonNull
                            @Override
                            public docshed_page1.DoctorViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_doc_page1,parent,false);
                                return new docshed_page1.DoctorViewHolder(view);
                            }

                            @Override
                            protected void onBindViewHolder(@NonNull docshed_page1.DoctorViewHolder holder, int position,  DoctorModel model) {
                                holder.doc_name.setText("Doctor " + model.getLastName());
                                holder.doc_type.setText(model.getDocType());
                                String DocUid = model.getUserId();
                                holder.itemView.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {

                                        Intent intent = new Intent(getApplicationContext(), docsched_page2.class);
                                        intent.putExtra("docid", model.getUserId());
                                        intent.putExtra("docname", "Doctor " + model.getLastName());
                                        startActivity(intent);



                                    }
                                });

                                db.collection("Doctors").whereEqualTo("StorageId", DocUid).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        if (task.isSuccessful()) {
                                            QuerySnapshot querySnapshot = task.getResult();
                                            if (!querySnapshot.isEmpty()) {
                                                for (QueryDocumentSnapshot profile : task.getResult()) {
                                                    image = profile.getString("StorageId");
                                                    storageReference = FirebaseStorage.getInstance().getReference("DoctorPicture/" + image);
                                                    try {
                                                        File local = File.createTempFile("myProfilePicture","");
                                                        storageReference.getFile(local).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                                                            @Override
                                                            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                                                getpic = BitmapFactory.decodeFile(local.getAbsolutePath());
                                                                holder.profpicturedoc.setImageBitmap(getpic);
                                                            }
                                                        });
                                                    } catch (IOException e) {
                                                        e.printStackTrace();
                                                    }
                                                }
                                            }
                                        }
                                    }
                                });
                            }


                        };
                        mFirestoreList.setHasFixedSize(true);
                        mFirestoreList.setLayoutManager(new LinearLayoutManager(docshed_page1.this));
                        mFirestoreList.setAdapter(adapter);
                        adapter.startListening();

                    }
                }
            }
        });

    }
}