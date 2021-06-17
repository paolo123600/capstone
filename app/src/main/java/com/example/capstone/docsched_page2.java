package com.example.capstone;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.LauncherActivityInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.Calendar;
import java.util.Date;

public class docsched_page2 extends AppCompatActivity {

    private RecyclerView mFirestoreList;
    private FirebaseFirestore db;
    FirestoreRecyclerAdapter adapter;
    TextView docnameTV;
    String docname , docid;
    Button addbtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_docsched_page2);
        docnameTV= (TextView) findViewById(R.id.docnameTV);
        addbtn  = (Button) findViewById(R.id.docsched_add);

        Intent intent = getIntent();
        docname = intent.getStringExtra("docname");
        docid = intent.getStringExtra("docid");
        docnameTV.setText(docname);

        db = FirebaseFirestore.getInstance();
        mFirestoreList = findViewById(R.id.docsched_schedule);

        Query query = db.collection("DoctorSchedules").whereEqualTo("DocId", docid);

        FirestoreRecyclerOptions<DocSchedModel> options = new FirestoreRecyclerOptions.Builder<DocSchedModel>()
                .setQuery(query,DocSchedModel.class)
                .build();

        adapter = new FirestoreRecyclerAdapter<DocSchedModel, DocSchedViewHolder>(options) {
            @NonNull
            @Override
            public DocSchedViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_doc_page2 , parent , false);
                return new DocSchedViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull DocSchedViewHolder holder, int position, @NonNull DocSchedModel model) {
                String days= "";
                if (model.getMonday() == true){
                    days += "M";
                }
                if (model.getTuesday() == true){
                    days += " T";
                }
                if (model.getWednesday() == true){
                    days += " W";
                }
                if (model.getThursday() == true){
                    days += " TH";
                }
                if (model.getFriday() == true){
                    days += " F";
                }
                if (model.getSaturday() == true){
                    days += " S";
                }
                if (model.getSunday() == true){
                    days += " SU";
                }
                holder.daysTV.setText(days);
                holder.timeTV.setText(model.getStartTime()+" to "+model.getEndTime());
                holder.maxBook.setText("Max Booking: " + model.getMaximumBooking());
                holder.Pricedocshed.setText("Price: " + model.getPrice());
                holder.editbtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String documentid = getSnapshots().getSnapshot(position).getId();
                        Intent intent = new Intent(getApplicationContext(), docsched_page3.class);
                        intent.putExtra("docid", docid);
                        intent.putExtra("docname", "Doctor " + docname);
                        intent.putExtra("type", "Update");
                        intent.putExtra("Documentid",documentid );
                        startActivity(intent);
                    }
                });

                holder.deletebtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        new AlertDialog.Builder(docsched_page2.this)
                                .setTitle("Cancel")
                                .setMessage("Are you sure you want to delete the schedule?")

                                .setPositiveButton("Accept", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        String documentid = getSnapshots().getSnapshot(position).getId();

                                        db.collection("DoctorSchedules").document(documentid).delete()
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        Toast.makeText(docsched_page2.this, "Successfully Deleted", Toast.LENGTH_SHORT).show();
                                                    }
                                                }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {

                                            }
                                        });
                                    }
                                })

                                .setNegativeButton("Decline", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        dialogInterface.dismiss();
                                    }
                                })
                                .setIcon(android.R.drawable.ic_dialog_alert)
                                .show();


                    }
                });
            }
        };

        mFirestoreList.setHasFixedSize(true);
        mFirestoreList.setLayoutManager(new LinearLayoutManager(this));
        mFirestoreList.setAdapter(adapter);
        adapter.startListening();




        addbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), docsched_page3.class);
                intent.putExtra("docid", docid);
                intent.putExtra("docname", "Doctor " + docname);
                intent.putExtra("type", "Add");
                startActivity(intent);
            }
        });

    }

    private class DocSchedViewHolder extends  RecyclerView.ViewHolder {

        TextView daysTV;
        TextView timeTV;
        TextView maxBook;
        TextView Pricedocshed;
        Button editbtn;
        Button deletebtn;

        public DocSchedViewHolder(@NonNull View itemView) {
            super(itemView);

            daysTV = itemView.findViewById(R.id.list_docdays);
            timeTV = itemView.findViewById(R.id.list_doctime);
            editbtn = itemView.findViewById(R.id.list_docedit);
            deletebtn = itemView.findViewById(R.id.list_docdelete);

            maxBook = itemView.findViewById(R.id.docsched_maxbooking);
            Pricedocshed = itemView.findViewById(R.id.docsched_price);


        }
    }
}