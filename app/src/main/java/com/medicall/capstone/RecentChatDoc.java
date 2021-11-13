package com.medicall.capstone;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.medicall.capstone.Model.ChatlistModel;
import com.medicall.capstone.Model.Chats;
import com.medicall.capstone.Model.Chatslist;
import com.medicall.capstone.Model.PatRC;

import com.medicall.capstone.R;

import com.medicall.capstone.utilities.Constants;
import com.medicall.capstone.utilities.PreferenceManager;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class RecentChatDoc extends AppCompatActivity {
    List<Chatslist> userlist;
    Button recentchat;
    List<PatRC> mPats;
    RecyclerView recyclerView;
    ImageView back;
    RecyclerView.LayoutManager layoutManager;
    FirestoreRecyclerAdapter mAdapter;
    FirebaseFirestore db;
    FirebaseAuth fAuth;
    LinearLayout secbtn;
    String userId;
    String secname;
    String thelastmessage;
    String mainuserid;
    FirebaseUser firebaseUser;
    String secid;
    private PreferenceManager preferenceManager;
    TextView none;

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
        mainuserid = preferenceManager.getString(Constants.KEY_USER_ID);
        clinicname = findViewById(R.id.clinicnamedoc);
        fAuth = FirebaseAuth.getInstance();
        userId = fAuth.getCurrentUser().getUid();
        secbtn= (LinearLayout) findViewById(R.id.doc_chat_sec);
        none = findViewById(R.id.None);

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

        Query query = db.collection("Doctors").document(mainuserid).collection("ChatList").whereEqualTo("UserType","Patients").orderBy("DateAndTime", Query.Direction.DESCENDING);
        FirestoreRecyclerOptions<ChatlistModel> options = new FirestoreRecyclerOptions.Builder<ChatlistModel>()
                .setQuery(query,ChatlistModel.class)
                .build();

        mAdapter = new FirestoreRecyclerAdapter<ChatlistModel, DocRecentViewHolder>(options) {
            @NonNull
            @Override
            public DocRecentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layoutofusers,parent,false);
                return new DocRecentViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull DocRecentViewHolder holder, int position, @NonNull ChatlistModel model) {
                Date dnt = new Date();
                SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM dd hh:mm aa");
                String datentime="";
                String patid = model.getUserId();
                db.collection("Patients").document(patid).get()
                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if(task.isSuccessful()){
                                    DocumentSnapshot documentSnapshot = task.getResult();
                                    String patname = documentSnapshot.getString("FirstName")+" "+ documentSnapshot.getString("LastName");
                                    holder.tvname.setText(patname);


                                    holder.itemView.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {

                                            Intent intent = new Intent(RecentChatDoc.this, MessageActivity.class);
                                            intent.putExtra("friendid", patid);
                                            intent.putExtra("name", patname);
                                            intent.putExtra("usertype", "Patients");
                                            intent.putExtra("type", "Doctors");
                                            startActivity(intent);
                                        }
                                    });
                                }
                            }

                        });
                LastMessage(patid,holder.tvmessage);
                dnt = model.getDateAndTime();
                datentime = dateFormat.format(dnt);
                holder.tvtime.setText(datentime);



            }
        };

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(mAdapter);
        mAdapter.startListening();

        db.collection("Doctors").document(mainuserid).collection("ChatList").whereEqualTo("UserType","Patients").orderBy("DateAndTime", Query.Direction.DESCENDING).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable  QuerySnapshot value, @Nullable  FirebaseFirestoreException error) {
                if(error != null){
                    Toast.makeText(RecentChatDoc.this, "Error Loading",Toast.LENGTH_SHORT).show();
                }
                if(value.isEmpty()){
                    none.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                } else {
                    none.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                }
            }
        });



    }
    private void LastMessage(String friendid, final TextView last_msg) {

        thelastmessage = "default";

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Chats");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot ds: snapshot.getChildren()) {

                    Chats chats = ds.getValue(Chats.class);

                    if (firebaseUser!=null &&  chats!=null) {


                        if (chats.getSender().equals(friendid) && chats.getReciever().equals(firebaseUser.getUid()) ||
                                chats.getSender().equals(firebaseUser.getUid()) && chats.getReciever().equals(friendid)) {


                            thelastmessage = chats.getMessage();
                        }




                    }

                }


                switch (thelastmessage) {

                    case "default":
                        last_msg.setText("No message");
                        break;

                    default:
                        last_msg.setText(thelastmessage);

                }


                thelastmessage = "default";


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }
    private class DocRecentViewHolder extends RecyclerView.ViewHolder {
        TextView tvname;
        TextView tvmessage;
        TextView chatcount;
        CardView chatcontainer;
        TextView tvtime;
        public DocRecentViewHolder(@NonNull View itemView) {
            super(itemView);


            tvname = itemView.findViewById(R.id.username_userfrag);
            tvmessage = itemView.findViewById(R.id.lastMessage);
            tvtime = itemView.findViewById(R.id.tvtime);
        }
    }
}