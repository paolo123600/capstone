package com.example.capstone;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.example.capstone.Model.Chatslist;
import com.example.capstone.Model.DocRC;
import com.example.capstone.Model.SecRC;
import com.example.capstone.adapters.DocAdapter;
import com.example.capstone.adapters.SecAdapter;
import com.example.capstone.utilities.Constants;
import com.example.capstone.utilities.PreferenceManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class RecentChats extends AppCompatActivity   {
    List<Chatslist> userlist;

    ImageView back;
    List <DocRC> mDocs;
    List <SecRC> mSecs;
    RecyclerView recyclerView;
    RecyclerView recyclerView1;
    RecyclerView.LayoutManager layoutManager;
    RecyclerView.LayoutManager layoutManager1;
    DocAdapter mAdapter;
    SecAdapter secAdapter;
FirebaseFirestore db;
    private PreferenceManager preferenceManager;
    Button newchat;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recent_chats);

        preferenceManager = new PreferenceManager(getApplicationContext());
        db= FirebaseFirestore.getInstance();
        newchat= (Button) findViewById(R.id.btn_createchat);

        userlist = new ArrayList<>();
        back = findViewById(R.id.backspace);
      recyclerView = (RecyclerView) findViewById(R.id.chat_recyclerview_recentchat4) ;
        recyclerView1=(RecyclerView) findViewById(R.id.chat_recyclerview_recentchat3) ;
        layoutManager = new LinearLayoutManager(this);
        layoutManager1 = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView1.setLayoutManager(layoutManager1);
        recyclerView1.setHasFixedSize(true);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RecentChats.this, MainActivity.class);
                startActivity(intent);
            }
        });

        String User = preferenceManager.getString(Constants.KEY_USER_ID);

        DatabaseReference reference  = FirebaseDatabase.getInstance().getReference("Chatslist")
                .child(User);


        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                userlist.clear();
                for (DataSnapshot ds: snapshot.getChildren()) {
                    Chatslist chatslist = ds.getValue(Chatslist.class);

                    userlist.add(chatslist);


                }

                ChatsListings();
                ChatsListingssec();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



        newchat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), Clinic_view.class);
                startActivity(intent);
            }
        });



    }
    private void ChatsListings() {

        mDocs = new ArrayList<>();

        db.collection("Doctors").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){

                            for (QueryDocumentSnapshot doc : task.getResult()) {
                                DocRC docRC = doc.toObject(DocRC.class);

                                for (Chatslist chatslist: userlist){

                                    if(chatslist.getId().equals(docRC.getUserId())){


                                        mDocs.add(docRC);

                                    }

                                }

                            }
                            mAdapter = new DocAdapter(RecentChats.this, mDocs, true,"Patients");
                            recyclerView.setAdapter(mAdapter);
                        }
                    }
                });




    }

    private void ChatsListingssec() {

        mSecs= new ArrayList<>();

        db.collection("Secretary").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){

                            for (QueryDocumentSnapshot doc : task.getResult()) {
                                SecRC secRC = doc.toObject(SecRC.class);

                                for (Chatslist chatslist: userlist){

                                    if(chatslist.getId().equals(secRC.getUserId())){


                                        mSecs.add(secRC);

                                    }

                                }

                            }
                           secAdapter = new SecAdapter(RecentChats.this, mSecs, true,"Patients");
                            recyclerView1.setAdapter(secAdapter);
                        }
                    }
                });








    }
    }