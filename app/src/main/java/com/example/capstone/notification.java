package com.example.capstone;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.text.SimpleDateFormat;
import java.util.Date;


public class notification extends AppCompatActivity {
Spinner spinner_status;
RecyclerView mFirestorelist;
FirebaseFirestore db;
FirestoreRecyclerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        spinner_status= (Spinner)findViewById(R.id.statspinner);
        mFirestorelist = (RecyclerView)findViewById(R.id.recycleview_notif);
        db= FirebaseFirestore.getInstance();

        ArrayAdapter<CharSequence> arrayAdapter = ArrayAdapter.createFromResource(this,
                R.array.Status, android.R.layout.simple_spinner_item);

        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner_status.setAdapter(arrayAdapter);

        Query query = db.collection("Schedules").orderBy("Dnt", Query.Direction.ASCENDING).limit(20);

        Shownotif(query);

        spinner_status.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Query query;
                String selectedstat = spinner_status.getSelectedItem().toString();
                switch (selectedstat) {
                    case "All":
                        query = db.collection("Schedules").orderBy("Dnt", Query.Direction.ASCENDING).limit(20);
                        Shownotif(query);
                        break;
                    case "Booked":
                         query = db.collection("Schedules").whereEqualTo("Status","Paid").orderBy("Dnt", Query.Direction.ASCENDING).limit(20);
                        Shownotif(query);
                        break;
                    case "Pending":
                         query = db.collection("Schedules").whereEqualTo("Status","Pending Approval").orderBy("Dnt", Query.Direction.ASCENDING).limit(20);
                        Shownotif(query);
                        break;
                    case "Rescheduled":
                         query = db.collection("Schedules").whereEqualTo("Status","Rescheduled").orderBy("Dnt", Query.Direction.ASCENDING).limit(20);
                        Shownotif(query);
                        break;
                    case "Cancelled":
                         query = db.collection("Schedules").whereEqualTo("Status","Cancelled").orderBy("Dnt", Query.Direction.ASCENDING).limit(20);
                        Shownotif(query);
                        break;
                }
            ;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

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
                 String date= model.getDate();
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
                                                 String docname= "Doc. "+documentSnapshot2.getString("LastName");
                                                 switch (status){
                                                     case "Paid":
                                                         holder.tvpatname.setText(patname+" has booked an appointment with "+docname+" for "+date);
                                                         break;
                                                     case "Pending Approval":
                                                         holder.tvpatname.setText(patname+" has booked an appointment with "+docname+" that is currently pending for "+date);
                                                         break;
                                                     case "Rescheduled":
                                                         holder.tvpatname.setText(patname+" has rescheduled an appointment with "+docname+" dated "+date);
                                                         break;
                                                     case "Cancelled":
                                                         holder.tvpatname.setText(patname+" has cancelled an appointment with "+docname+" dated "+date);
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




             }
         };
         mFirestorelist.setHasFixedSize(true);
         mFirestorelist.setLayoutManager(new LinearLayoutManager(this));
         mFirestorelist.setAdapter(adapter);
         adapter.startListening();

    }
}