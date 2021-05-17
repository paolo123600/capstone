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
import com.example.capstone.Model.PatRC;
import com.example.capstone.adapters.DocAdapter;
import com.example.capstone.adapters.UserAdapter;
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

public class RecentChatSecretary extends AppCompatActivity {
    List<Chatslist> userlist;
    Button createchatsec;
    List <DocRC> mDocs;
    List<PatRC> mPats;
    RecyclerView recyclerView;
    RecyclerView recyclerView1;
    RecyclerView.LayoutManager layoutManager;
    RecyclerView.LayoutManager layoutManager1;
    UserAdapter mAdapter;
    DocAdapter docAdapter;
    FirebaseFirestore db;
    ImageView back;
    private PreferenceManager preferenceManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recent_chat_secretary);
        preferenceManager = new PreferenceManager(getApplicationContext());
        db= FirebaseFirestore.getInstance();
        createchatsec= (Button) findViewById(R.id.btn_recentchatsec);
        userlist = new ArrayList<>();
        recyclerView=(RecyclerView)findViewById(R.id.chat_recyclerview_recentchatSec) ;
        recyclerView1=(RecyclerView)findViewById(R.id.chat_recyclerview_recentchat2) ;
        layoutManager = new LinearLayoutManager(this);
        layoutManager1 = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView1.setLayoutManager(layoutManager1);
        recyclerView1.setHasFixedSize(true);
        String User = preferenceManager.getString(Constants.KEY_USER_ID);
        back = findViewById(R.id.backspace);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RecentChatSecretary.this, secretary_homepage.class);
                startActivity(intent);
            }
        });

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
                ChatsListingsdoc();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });






        createchatsec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RecentChatSecretary.this,Sec_patient_list.class);
                startActivity(intent);
            }
        });

    }

    private void ChatsListingsdoc() {
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
                            docAdapter = new DocAdapter(RecentChatSecretary.this, mDocs, true,"Secretary");
                            recyclerView1.setAdapter(docAdapter);
                        }
                    }
                });

    }

    private void ChatsListings() {
        mPats = new ArrayList<>();

        db.collection("Patients").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){

                            for (QueryDocumentSnapshot doc : task.getResult()) {
                                PatRC pats = doc.toObject(PatRC.class);

                                for (Chatslist chatslist: userlist){

                                    if(chatslist.getId().equals(pats.getUserId())){


                                        mPats.add(pats);

                                    }

                                }

                            }
                            mAdapter = new UserAdapter(RecentChatSecretary.this, mPats, true,"Secretary");
                            recyclerView.setAdapter(mAdapter);
                        }
                    }
                });
    }
}