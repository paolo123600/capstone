package com.example.capstone;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.capstone.Model.Chatslist;
import com.example.capstone.Model.PatRC;
import com.example.capstone.adapters.DocAdapter;
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
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class RecentChatDoc extends AppCompatActivity {
    List<Chatslist> userlist;
    Button recentchat;
    List<PatRC> mPats;
    RecyclerView recyclerView;
    ImageView back;
    RecyclerView.LayoutManager layoutManager;
    UserAdapter mAdapter;
    FirebaseFirestore db;
    FirebaseAuth fAuth;
    LinearLayout secbtn;
    String userId;
    String secname;
    String secid;
    private PreferenceManager preferenceManager;

    TextView clinicname;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recent_chat_doc);
        recentchat= (Button) findViewById(R.id.btn_recentchatDoc);
        db= FirebaseFirestore.getInstance();
        preferenceManager = new PreferenceManager(getApplicationContext());
        userlist = new ArrayList<>();
        recyclerView = (RecyclerView) findViewById(R.id.chat_recyclerview_recentchatDoc) ;
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        String User = preferenceManager.getString(Constants.KEY_USER_ID);
        back = findViewById(R.id.backspace);

        clinicname = findViewById(R.id.clinicnamedoc);
        fAuth = FirebaseAuth.getInstance();
        userId = fAuth.getCurrentUser().getUid();
        secbtn= (LinearLayout) findViewById(R.id.doc_chat_sec);

        DocumentReference documentReference = db.collection("Doctors").document(userId);
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                secname=documentSnapshot.getString("ClinicName");
                clinicname.setText(secname);
            }
        });

        secbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                db.collection("Secretary").whereEqualTo("ClinicName",secname).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            for (QueryDocumentSnapshot queryDocumentSnapshot: task.getResult()){
                                secid=queryDocumentSnapshot.getId();
                                Intent intent = new Intent(RecentChatDoc.this, MessageActivity.class);
                                intent.putExtra("friendid", secid);
                                intent.putExtra("name", secname);
                                intent.putExtra("usertype", "Secretary");
                                intent.putExtra("type", "Doctors");
                                startActivity(intent);
                            }

                        }
                    }
                });

            }
        });
        DatabaseReference reference  = FirebaseDatabase.getInstance().getReference("Chatslist")
                .child(User);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RecentChatDoc.this, doctor_homepage.class);
                startActivity(intent);
            }
        });
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

            recentchat.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getApplicationContext(), Doctor_Patientchatlist.class);
                    startActivity(intent);
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
                    mAdapter = new UserAdapter(RecentChatDoc.this, mPats, true,"Doctors");
                    recyclerView.setAdapter(mAdapter);
                            }
                        }
                    });




        }
}