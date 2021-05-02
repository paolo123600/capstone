package com.example.capstone;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.capstone.activities.OutgoingInvitationActivity;
import com.example.capstone.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class doctor_homepage extends AppCompatActivity {
    FirebaseFirestore db;
    private  Button callbtn;
    String gmail ="pao@gmail.com";
    private List<User> users;
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.doctor_home);
        db=FirebaseFirestore.getInstance();



        users = new ArrayList<>();

callbtn= (Button) findViewById(R.id.btn_call);
callbtn.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View view) {
        Query query =  db.collection("Patients").whereEqualTo("Email",gmail);
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    for(QueryDocumentSnapshot document : task.getResult()) {

                            User user = new User();
                            user.token = document.get("fcm_token").toString();
                            user.firstName = document.get("FirstName").toString();
                            user.lastName = document.get("LastName").toString();
                            user.email = document.get("Email").toString();
                            users.add (user);

                            Intent intent = new Intent(getApplicationContext(), OutgoingInvitationActivity.class);
                            intent.putExtra("user", user);
                            intent.putExtra("type", "video");
                            startActivity(intent);


                    }


                }
            }
        });

    }
});





    }
}
