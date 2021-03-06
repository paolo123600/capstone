package com.medicall.capstone;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.medicall.capstone.R;
import com.medicall.capstone.utilities.PreferenceManager;

import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;


public class notification extends AppCompatActivity {
Spinner spinner_status;
RecyclerView mFirestorelist;
FirebaseFirestore db;
FirestoreRecyclerAdapter adapter;
PreferenceManager preferenceManager;
TextView none;
    ImageView back;
String clinicname;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        spinner_status= (Spinner)findViewById(R.id.statspinner);
        mFirestorelist = (RecyclerView)findViewById(R.id.recycleview_notif);
        db= FirebaseFirestore.getInstance();
        back = findViewById(R.id.backspace);
        none = findViewById(R.id.None);
        ArrayAdapter<CharSequence> arrayAdapter = ArrayAdapter.createFromResource(this,
                R.array.Status, R.layout.custom_spinner);
        preferenceManager = new PreferenceManager(getApplicationContext());
        clinicname = preferenceManager.getString("ClinicName");
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner_status.setAdapter(arrayAdapter);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


        Query query = db.collection("Notification").whereEqualTo("ClinicName",clinicname).orderBy("Dnt", Query.Direction.DESCENDING).limit(50);

        Shownotif(query);



        spinner_status.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Query query;
                String selectedstat = spinner_status.getSelectedItem().toString();
                switch (selectedstat) {
                    case "All":
                        query = db.collection("Notification").whereEqualTo("ClinicName",clinicname).whereIn("Status", Arrays.asList("Paid","Pending Approval","Reschedule","Cancelled","Declined","Approved", "Unattended","Rescheduled")).orderBy("Dnt", Query.Direction.DESCENDING).limit(20);
                        Shownotif(query);
                        break;
                    case "Booked":
                         query = db.collection("Notification").whereEqualTo("ClinicName",clinicname).whereEqualTo("Status","Paid").orderBy("Dnt", Query.Direction.DESCENDING).limit(20);
                        Shownotif(query);
                        break;
                    case "Pending":
                         query = db.collection("Notification").whereEqualTo("ClinicName",clinicname).whereEqualTo("Status","Pending Approval").orderBy("Dnt", Query.Direction.DESCENDING).limit(20);
                        Shownotif(query);
                        break;
                    case "Rescheduled":
                         query = db.collection("Notification").whereEqualTo("ClinicName",clinicname).whereEqualTo("Status","Rescheduled").orderBy("Dnt", Query.Direction.DESCENDING).limit(20);
                        Shownotif(query);
                        break;
                    case "Cancelled":
                         query = db.collection("Notification").whereEqualTo("ClinicName",clinicname).whereEqualTo("Status","Cancelled").orderBy("Dnt", Query.Direction.DESCENDING).limit(20);
                        Shownotif(query);
                        break;
                    case "Declined":
                        query = db.collection("Notification").whereEqualTo("ClinicName",clinicname).whereEqualTo("Status","Declined").orderBy("Dnt", Query.Direction.DESCENDING).limit(20);
                        Shownotif(query);
                        break;
                    case "Approved":
                        query = db.collection("Notification").whereEqualTo("ClinicName",clinicname).whereEqualTo("Status","Approved").orderBy("Dnt", Query.Direction.DESCENDING).limit(20);
                        Shownotif(query);
                        break;

                    case "Unattended":
                        query = db.collection("Notification").whereEqualTo("ClinicName",clinicname).whereEqualTo("Status","Unattended").orderBy("Dnt", Query.Direction.DESCENDING).limit(20);
                        Shownotif(query);
                        break;
                }
            ;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        db.collection("Schedules").whereEqualTo("ClinicName",clinicname).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable  FirebaseFirestoreException error) {
                if(error != null){
                    Toast.makeText(notification.this, "Error Loading",Toast.LENGTH_SHORT).show();
                }
                if (value.isEmpty()){
                    none.setVisibility(View.VISIBLE);
                    mFirestorelist.setVisibility(View.GONE);
                }else{
                    none.setVisibility(View.GONE);
                    mFirestorelist.setVisibility(View.VISIBLE);
                }
            }
        });


    }

    private class Schedholder extends RecyclerView.ViewHolder{
        TextView tvpatname , tvdocname , tvstatus, tvdate;
        public Schedholder(@NonNull View itemView) {
            super(itemView);
            tvpatname = itemView.findViewById(R.id.notif_pat);
            tvdate = itemView.findViewById(R.id.notif_date);

        }
    }

     public void Shownotif(Query query){

         FirestoreRecyclerOptions<DocTodaySchedModel> options = new FirestoreRecyclerOptions.Builder<DocTodaySchedModel>()
                 .setQuery(query, DocTodaySchedModel.class)
                 .build();
         adapter = new FirestoreRecyclerAdapter<DocTodaySchedModel, Schedholder>(options) {
             @NonNull
             @Override
             public Schedholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                 View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.notif_single,parent,false);

                 return new Schedholder(view);
             }

             @Override
             protected void onBindViewHolder(@NonNull Schedholder holder, int position, @NonNull DocTodaySchedModel model) {
                 Date datesched =model.getDate();
                 SimpleDateFormat format = new SimpleDateFormat("MMMM d ,yyyy");
                 String date=  format.format(datesched);
                 String status = model.getStatus();
                 Date bookeddate = model.getDnt();
                 db.collection("Patients").document(model.getPatientUId()).get()
                         .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                             @Override
                             public void onSuccess(DocumentSnapshot documentSnapshot1) {
                                 documentSnapshot1.getData();
                                 String patname = documentSnapshot1.getString("LastName")+", "+documentSnapshot1.getString("FirstName");
                                 db.collection("Doctors").document(model.getDoctorUId()).get()
                                         .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                             @Override
                                             public void onSuccess(DocumentSnapshot documentSnapshot2) {
                                                 documentSnapshot2.getData();
                                                 String docname= "Dr. "+documentSnapshot2.getString("LastName");

                                                 switch (status){
                                                     case "Paid":
                                                         holder.tvpatname.setText(Html.fromHtml(patname+ " has " + "<font color='#2FD845'>BOOKED</font>" + " an appointment with " +docname+ " dated " + date));
                                                         break;
                                                     case "Pending Approval":
                                                         holder.tvpatname.setText(Html.fromHtml(patname+" has booked an appointment with "+docname+" that is currently " + "<font color='#F0EF45'>PENDING</font>" +" for "+date));
                                                         break;
                                                     case "Rescheduled":
                                                         holder.tvpatname.setText(Html.fromHtml(patname+" has " + "<font color='#33D2DA'>RESCHEDULED</font>" + " an appointment with "+docname+" dated "+date));
                                                         break;
                                                     case "Cancelled":
                                                         holder.tvpatname.setText(Html.fromHtml(patname+" has " + "<font color='#FF871D'>CANCELLED</font>" + " an appointment with "+docname+" dated "+date));
                                                         break;
                                                     case "Declined":
                                                         holder.tvpatname.setText(Html.fromHtml(patname+"'s appointment has been " + "<font color='#DA3333'>DECLINED</font>" + " dated "+date));
                                                         break;
                                                     case "Approved":
                                                         holder.tvpatname.setText(Html.fromHtml(patname+"'s appointment has been " + "<font color='#2FD845'>APPROVED</font>" + " dated "+date));
                                                         break;

                                                     case "Unattended":
                                                         holder.tvpatname.setText(Html.fromHtml(patname+"'s appointment was " + "<font color='#DA3333'>UNATTENDED</font>" + " dated "+date));
                                                         break;

                                                 }
                                                 SimpleDateFormat simpleDate =  new SimpleDateFormat("MMM d ,yyyy h:ma");
                                                 String bookeddatestring = simpleDate.format(bookeddate);
                                                 holder.tvdate.setText(bookeddatestring);
                                             }
                                         }).addOnFailureListener(new OnFailureListener() {
                                     @Override
                                     public void onFailure(@NonNull Exception e) {
                                         Toast.makeText(notification.this, "error showing patient", Toast.LENGTH_SHORT).show();
                                     }
                                 });
                             }
                         }).addOnFailureListener(new OnFailureListener() {
                     @Override
                     public void onFailure(@NonNull Exception e) {
                         Toast.makeText(notification.this, "error showing patient", Toast.LENGTH_SHORT).show();
                     }
                 });


                 String documentId = getSnapshots().getSnapshot(position).getId();
                if (!model.Seen){
                    db.collection("Notification").document(documentId).update("Seen" , true);
                }
             }
         };
         mFirestorelist.setHasFixedSize(true);
         mFirestorelist.setLayoutManager(new LinearLayoutManager(this));
         mFirestorelist.setAdapter(adapter);
         adapter.startListening();

    }
}