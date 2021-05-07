package com.example.capstone;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.capstone.activities.OutgoingInvitationActivity;
import com.example.capstone.models.User;
import com.example.capstone.utilities.Constants;
import com.example.capstone.utilities.PreferenceManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class doctor_homepage extends AppCompatActivity {
    Button btn_dochat;
    FirebaseFirestore db;
    private  Button callbtn;
    String gmail ="";
    String patUid="";
    private List<User> users;
    private PreferenceManager preferenceManager;
    String datenow;
    Date timenow;
    TextView patnametv , schedtimetv;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.doctor_home);
        db=FirebaseFirestore.getInstance();
        preferenceManager = new PreferenceManager(getApplicationContext());
        btn_dochat = (Button) findViewById(R.id.btn_chat_dochome);
        Calendar calendar = Calendar.getInstance();
        datenow = DateFormat.getDateInstance().format(calendar.getTime());

        patnametv=(TextView)findViewById(R.id.tvPatname);
        schedtimetv=(TextView)findViewById(R.id.tvSched);
        SimpleDateFormat dateFormat = new SimpleDateFormat("h:mmaa");

        try{
            Date currentTime = Calendar.getInstance().getTime();
            String timenow1 =dateFormat.format(currentTime);

            timenow = dateFormat.parse(timenow1);

        }

        catch(ParseException e){
            Toast.makeText(doctor_homepage.this, "error getting time", Toast.LENGTH_SHORT).show();
        }



        checkschedcurrent();

        btn_dochat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), RecentChatDoc.class);
                startActivity(intent);
            }
        });

        FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
            @Override
            public void onComplete(@NonNull Task<InstanceIdResult> task) {
                if (task.isSuccessful() && task.getResult() != null) {
                    sendFCMTokenToDatabase(task.getResult().getToken());
                }
            }
        });


        users = new ArrayList<>();

        callbtn= (Button) findViewById(R.id.btn_call);
        callbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Query query =  db.collection("Patients").whereEqualTo("UserId",patUid);
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

    private void checkschedcurrent() {

        //initializing time
        Date d930 = new Date() ,  d10 = new Date() , d1030 = new Date() , d11 = new Date() , d1130 = new Date() , d1 = new Date() , d130 = new Date() , d2 = new Date() , d230 = new Date() , d3 = new Date() , d330 = new Date() ,
                d4 = new Date() , d430 = new Date() ,d5 = new Date() , d530 = new Date(), d12= new Date();
        String timestart ="", timestop="";

        String t930 = "9:30am";
        String t10 ="10:00am";
        String t1030 = "10:30am";
        String t11 = "11:00am";
        String t1130 = "11:30am";
        String t12="12:00pm";
        String t1= "1:00pm";
        String t130 = "1:30pm";
        String t2 = "2:00pm";
        String t230 = "2:30pm";
        String t3 = "3:00pm";
        String t330 = "3:30pm";
        String t4 = "4:00pm";
        String t430 = "4:30pm";
        String t5 = "5:00pm";
        String t530 ="5:30pm";

        SimpleDateFormat dateFormat = new SimpleDateFormat("h:mmaa");
            //converting time
        try{
            d930 = dateFormat.parse(t930);
            d10 = dateFormat.parse(t10);
            d1030 = dateFormat.parse(t1030);
            d11 = dateFormat.parse(t11);
            d1130 = dateFormat.parse(t1130);
            d1 = dateFormat.parse(t1);
            d130 = dateFormat.parse(t130);
            d2 = dateFormat.parse(t2);
            d230 = dateFormat.parse(t230);
            d3 = dateFormat.parse(t3);
            d330 = dateFormat.parse(t330);
            d4 = dateFormat.parse(t4);
            d430 = dateFormat.parse(t430);
            d5 = dateFormat.parse(t5);
            d530 = dateFormat.parse(t530);
        }

        catch(ParseException e){
            Toast.makeText(doctor_homepage.this, "errorsettingtime", Toast.LENGTH_SHORT).show();
        }
//getting the current sched

         if (timenow.after(d5)&&timenow.before(d530)||timenow.equals(d5)){

            timestart=t5;
            timestop=t530;

        }
        else if (timenow.after(d430)||timenow.equals(d430)){

            timestart=t430;
            timestop=t5;

        }
        else if (timenow.after(d4)||timenow.equals(d4)){

            timestart=t4;
            timestop=t430;

        }
        else if (timenow.after(d330)||timenow.equals(d330)){

            timestart=t330;
            timestop=t4;

        }  else if (timenow.after(d3)||timenow.equals(d3)){

             timestart=t3;
             timestop=t330;

         }

        else if (timenow.after(d230)||timenow.equals(d230)){

            timestart=t230;
            timestop=t3;

        }
         else if (timenow.after(d2)||timenow.equals(d2)){

             timestart=t2;
             timestop=t230;

         }
        else if (timenow.after(d130)||timenow.equals(d130)){

            timestart=t130;
            timestop=t2;

        }
        else if (timenow.after(d1)||timenow.equals(d1)){

            timestart=t1;
            timestop=t130;

        }
        else if (timenow.after(d1130)&&timenow.before(d12)||timenow.equals(d1130)){

            timestart=t1130;
            timestop=t12;

        }
        else if (timenow.after(d11)||timenow.equals(d11)){

            timestart=t11;
            timestop=t1130;

        }
        else if (timenow.after(d1030)||timenow.equals(d1030)){

            timestart=t1030;
            timestop=t11;

        }
        else if (timenow.after(d10)||timenow.equals(d10)){

            timestart=t10;
            timestop=t1030;

        }
        else if (timenow.after(d930)||timenow.equals(d930)){

            timestart=t930;
            timestop=t10;

        }

        else{

            Toast.makeText(doctor_homepage.this, "nosched", Toast.LENGTH_SHORT).show();
        }

       db.collection("Schedule").whereEqualTo("SchedDate",datenow).whereEqualTo("TimeStart",timestart).whereEqualTo("DoctorUId", preferenceManager.getString(Constants.KEY_USER_ID)).whereEqualTo("TimeStop",timestop)
               .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
           @Override
           public void onComplete(@NonNull Task<QuerySnapshot> task) {
                   if(task.isSuccessful()){
                       QuerySnapshot querySnapshot = task.getResult();
                       if (querySnapshot.isEmpty()) {
                           Toast.makeText(doctor_homepage.this, "No current", Toast.LENGTH_SHORT).show();
                       } else {
                           for (QueryDocumentSnapshot document : task.getResult()) {
                                patUid=document.getString("PatientUId");
                               Toast.makeText(doctor_homepage.this, patUid, Toast.LENGTH_SHORT).show();
                                db.collection("Patients").document(patUid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        if (task.isSuccessful()){
                                            DocumentSnapshot document1 = task.getResult();{
                                            if (document1.exists())
                                                gmail=document1.getString("Email");
                                                patnametv.setText(document1.getString("LastName")+", "+document1.getString("FirstName"));
                                                schedtimetv.setText("Time: "+document.getString("TimeStart")+" - "+document.getString("TimeStop"));

                                            }

                                        }
                                    }
                                });
                           }
                       }


                   }
                   else {

                   }

               }

       });

    }






    private void sendFCMTokenToDatabase (String token) {
        DocumentReference documentReference = db.collection("Doctors").document(
                preferenceManager.getString(Constants.KEY_USER_ID)
        );
        documentReference.update(Constants.KEY_FCM_TOKEN, token)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(doctor_homepage.this, "Unable to send token: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }


}

