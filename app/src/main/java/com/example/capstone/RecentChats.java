package com.example.capstone;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.capstone.Model.Chatslist;
import com.example.capstone.Model.Users;
import com.example.capstone.adapters.UserAdapter;
import com.example.capstone.utilities.Constants;
import com.example.capstone.utilities.PreferenceManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import org.w3c.dom.Document;

import java.util.ArrayList;
import java.util.List;

public class RecentChats extends AppCompatActivity   {
    List<Chatslist> userlist;
    List<Users> mUsers;
    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    UserAdapter mAdapter;
FirebaseFirestore db;
    private PreferenceManager preferenceManager;
    Button newchat;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recent_chats);

        preferenceManager = new PreferenceManager(getApplicationContext());
        db= FirebaseFirestore.getInstance();
        newchat.findViewById(R.id.btn_recentchat);

        userlist = new ArrayList<>();

      recyclerView = (RecyclerView) findViewById(R.id.chat_recyclerview_recentchat) ;
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);


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

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });




        return view;







        newchat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), Clinic_view.class);
                startActivity(intent);
            }
        });



    }
    private void ChatsListings() {

        mUsers = new ArrayList<>();
        for (Chatslist chatslist: userlist) {
        db.collection("Patients").document(chatslist.getId())
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        DocumentSnapshot document = task.getResult();
                        mUsers.add(document);
                    }
                });



        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users");


        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                mUsers.clear();

                for (DataSnapshot ds: snapshot.getChildren()) {

                    Users users = ds.getValue(Users.class);

                    for (Chatslist chatslist: userlist) {


                        if (users.getId().equals(chatslist.getId())) {


                            mUsers.add(users);




                        }




                    }


                }

                mAdapter = new UserAdapter(getContext(), mUsers, true);
                recyclerView.setAdapter(mAdapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



    }
}