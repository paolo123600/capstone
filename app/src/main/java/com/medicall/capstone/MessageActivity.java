package com.medicall.capstone;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.medicall.capstone.R;

import com.medicall.capstone.adapters.MessageAdapter;

import com.medicall.capstone.Model.Chats;
import com.medicall.capstone.utilities.Constants;
import com.medicall.capstone.utilities.PreferenceManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MessageActivity extends AppCompatActivity {


    String friendid, message, myid;

    String type;
    LinearLayout chat_act;
    String usertype;
    TextView usernameonToolbar;
    Toolbar toolbar;
    FirebaseUser firebaseUser;
    FirebaseFirestore db;
    EditText et_message;
    Button send;

    String FromCall = "false";
    private PreferenceManager preferenceManager;


    ImageView back;

    DatabaseReference reference;

    List<Chats> chatsList;
    MessageAdapter messageAdapter;
    RecyclerView recyclerView;
    ValueEventListener seenlistener;


    private static final int  GALLERY_CODE = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
        preferenceManager = new PreferenceManager(getApplicationContext());

        //send pic
        chat_act = (LinearLayout) findViewById(R.id.chat_act);
        friendid = getIntent().getStringExtra("friendid"); // retreive the friendid when we click on the item
        usertype = getIntent().getStringExtra("usertype");
        type = getIntent().getStringExtra("type");
        back = findViewById(R.id.profile_image_toolbar_message);

        FromCall = getIntent().getStringExtra("FromCall");

        if (usertype.equals("Doctors") && type.equals("Patients")) {
            chat_act.setVisibility(View.INVISIBLE);
        }else {

        }

        toolbar = findViewById(R.id.toolbar_message);


        usernameonToolbar = findViewById(R.id.username_ontoolbar_message);

        recyclerView = findViewById(R.id.recyclerview_messages);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(layoutManager);

        send = findViewById(R.id.send_messsage_btn);
        et_message = findViewById(R.id.edit_message_text);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (FromCall== null) {
                    onBackPressed();
                }else {

                    Intent intent = new Intent(getApplicationContext(), Login.class);
                    startActivity(intent);
                }
            }
        });



        myid = preferenceManager.getString(Constants.KEY_USER_ID);


        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(friendid);




        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

//                Users users = snapshot.getValue(Users.class);
//
                usernameonToolbar.setText(getIntent().getStringExtra("name")); // set the text of the user on textivew in toolbar


                readMessages(myid, friendid);



            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });





        seenMessage(friendid);





        et_message.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                if (s.toString().length() > 0) {

                    send.setEnabled(true);

                } else {

                    send.setEnabled(false);


                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                String text = et_message.getText().toString();

                if (!text.startsWith(" ")) {
                    et_message.getText().insert(0, " ");

                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });




        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                message = et_message.getText().toString();

                sendMessage(myid, friendid, message);

                et_message.setText(" ");


            }
        });




    }



    private void seenMessage(final String friendid) {

        reference = FirebaseDatabase.getInstance().getReference("Chats");


        seenlistener = reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot ds: snapshot.getChildren()) {

                    Chats chats = ds.getValue(Chats.class);

                    if (chats.getReciever().equals(myid) && chats.getSender().equals(friendid)) {

                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("isseen", true);
                        ds.getRef().updateChildren(hashMap);

                    }




                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });






    }

    private void readMessages(final String myid, final String friendid) {

        chatsList = new ArrayList<>();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Chats");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                chatsList.clear();

                for (DataSnapshot ds: snapshot.getChildren()) {

                    Chats chats = ds.getValue(Chats.class);

                    if (chats.getSender().equals(myid) && chats.getReciever().equals(friendid) ||
                            chats.getSender().equals(friendid) && chats.getReciever().equals(myid)) {

                        chatsList.add(chats);
                    }

                    messageAdapter = new MessageAdapter(MessageActivity.this, chatsList);
                    recyclerView.setAdapter(messageAdapter);

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }


    private void sendMessage(final String myid, final String friendid, final String message) {


        final DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("sender", myid);
        hashMap.put("reciever", friendid);
        hashMap.put("message", message);
        hashMap.put("isseen", false);

        reference.child("Chats").push().setValue(hashMap);

        // ChatList
        DocumentReference query = null;
        DocumentReference query2 = null;
        Date currentTime = Calendar.getInstance().getTime();
        if (type.equals("Doctors")){
            query = db.collection("Doctors").document(myid).collection("ChatList").document(friendid);
        } else  if (type.equals("Patients")){
            query = db.collection("Patients").document(myid).collection("ChatList").document(friendid);
        } else  if (type.equals("Secretary")){
            query = db.collection("Secretary").document(myid).collection("ChatList").document(friendid);
        }
        Map<String, Object> Chat = new HashMap<>();
        Chat.put("UserId", friendid);
        Chat.put("DateAndTime", currentTime );
        Chat.put ("UserType",usertype);
        query.set(Chat);

        if (usertype.equals("Doctors")){
            query2 = db.collection("Doctors").document(friendid).collection("ChatList").document(myid);
        } else  if (usertype.equals("Patients")){
            query2 = db.collection("Patients").document(friendid).collection("ChatList").document(myid);
        } else  if (usertype.equals("Secretary")){
            query2 = db.collection("Secretary").document(friendid).collection("ChatList").document(myid);
        }
        else {
            Toast.makeText(this, "sdaf", Toast.LENGTH_SHORT).show();
        }
        Map<String, Object> Chat2 = new HashMap<>();
        Chat2.put("UserId", myid);
        Chat2.put("DateAndTime", currentTime );
        Chat2.put ("UserType",type);
        query2.set(Chat2);

//        final DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference("Chatslist").child(myid).child(friendid);
//
//        reference1.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//
//
//                if (!snapshot.exists()) {
//
//
//                    reference1.child("id").setValue(friendid);
//                }
//
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });
//
//            final DatabaseReference reference2 = FirebaseDatabase.getInstance().getReference("Chatslist").child(friendid).child(myid);
//
//        reference2.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//
//
//                if (!snapshot.exists()) {
//
//
//                    reference2.child("id").setValue(myid);
//                }
//
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });

    }





}