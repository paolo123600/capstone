package com.medicall.capstone;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.medicall.capstone.Model.ChatlistModel;
import com.medicall.capstone.Model.Chats;
import com.medicall.capstone.Model.Chatslist;
import com.medicall.capstone.Model.DocRC;
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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
    FirestoreRecyclerAdapter mAdapter;
    FirestoreRecyclerAdapter docAdapter;
    FirebaseFirestore db;
    String thelastmessage;
    FirebaseUser firebaseUser;
    String mainuserid;
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
        mainuserid = preferenceManager.getString(Constants.KEY_USER_ID);
        recyclerView=(RecyclerView)findViewById(R.id.chat_recyclerview_recentchat2) ;
        recyclerView1=(RecyclerView)findViewById(R.id.chat_recyclerview_recentchatSec) ;
        layoutManager = new LinearLayoutManager(this);
        layoutManager1 = new LinearLayoutManager(this);
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
                ChatsListingsdoc();
                ChatsListings();

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

        Query query = db.collection("Secretary").document(mainuserid).collection("ChatList").whereEqualTo("UserType","Doctors").orderBy("DateAndTime", Query.Direction.DESCENDING);
        FirestoreRecyclerOptions<ChatlistModel> options = new FirestoreRecyclerOptions.Builder<ChatlistModel>()
                .setQuery(query,ChatlistModel.class)
                .build();

        docAdapter = new FirestoreRecyclerAdapter<ChatlistModel, DocRecentViewHolder>(options) {
            @NonNull
            @Override
            public DocRecentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layoutofusers,parent,false);
                return new DocRecentViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull DocRecentViewHolder holder, int position, @NonNull ChatlistModel model) {
                Date dnt = new Date();
                SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd hh:mm aa");
                String datentime="";
                String docid = model.getUserId();
                db.collection("Doctors").document(docid).get()
                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if(task.isSuccessful()){
                                    DocumentSnapshot documentSnapshot = task.getResult();
                                    String docname = "Dr. "+ documentSnapshot.getString("LastName");
                                    holder.tvname.setText(docname);

                                    holder.itemView.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {

                                            Intent intent = new Intent(RecentChatSecretary.this, MessageActivity.class);
                                            intent.putExtra("friendid", docid);
                                            intent.putExtra("name", docname);
                                            intent.putExtra("usertype", "Doctors");
                                            intent.putExtra("type", "Secretary");
                                            startActivity(intent);
                                        }
                                    });
                                }
                            }

                        });
                LastMessage(docid,holder.tvmessage);
                dnt = model.getDateAndTime();
                datentime = dateFormat.format(dnt);
                holder.tvtime.setText(datentime);

            }
        };

        recyclerView1.setHasFixedSize(true);
        recyclerView1.setLayoutManager(new LinearLayoutManager(this));
        recyclerView1.setAdapter(docAdapter);
        docAdapter.startListening();


    }

    private void ChatsListings() {

        Query query = db.collection("Secretary").document(mainuserid).collection("ChatList").whereEqualTo("UserType","Patients").orderBy("DateAndTime", Query.Direction.DESCENDING);
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
                                    String patname = documentSnapshot.getString("FirstName")+ documentSnapshot.getString("LastName");
                                    holder.tvname.setText(patname);

                                    holder.itemView.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {

                                            Intent intent = new Intent(RecentChatSecretary.this, MessageActivity.class);
                                            intent.putExtra("friendid", patid);
                                            intent.putExtra("name", patname);
                                            intent.putExtra("usertype", "Patients");
                                            intent.putExtra("type", "Secretary");
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

    }

    private class DocRecentViewHolder extends RecyclerView.ViewHolder {
        TextView tvname;
        TextView tvmessage;
        TextView tvtime;
        public DocRecentViewHolder(@NonNull View itemView) {
            super(itemView);

            tvname = itemView.findViewById(R.id.username_userfrag);
            tvmessage = itemView.findViewById(R.id.lastMessage);
            tvtime = itemView.findViewById(R.id.tvtime);
        }
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
}