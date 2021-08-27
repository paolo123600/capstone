package com.example.capstone;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.LauncherActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class docsched_page2 extends AppCompatActivity {

    private RecyclerView mFirestoreList;
    private FirebaseFirestore db;
    FirestoreRecyclerAdapter adapter;
    TextView docnameTV;
    String docname , docid;
    String realDocID;
    Button addbtn;
    int row_index;
    ImageView back;

    boolean asd = true, qwe = false;


    String userId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_docsched_page2);
        docnameTV= (TextView) findViewById(R.id.docnameTV);
        addbtn  = (Button) findViewById(R.id.docsched_add);


        userId = "DocumentId";

        Intent intent = getIntent();
        docname = intent.getStringExtra("docname");
        docid = intent.getStringExtra("docid");
        docnameTV.setText(docname);

        db = FirebaseFirestore.getInstance();
        mFirestoreList = findViewById(R.id.docsched_schedule);

        Query query = db.collection("DoctorSchedules").whereEqualTo("DocId", docid);
        back = findViewById(R.id.backspace);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), docshed_page1.class);
                startActivity(intent);
            }
        });


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


                if (model.getInActive() == true ){
                    holder.switchbtn.setChecked(true);
                    holder.maxBook.setTextColor(Color.GRAY);
                    holder.daysTV.setTextColor(Color.GRAY);
                    holder.Pricedocshed.setTextColor(Color.GRAY);
                    holder.daysTV.setTextColor(Color.GRAY);
                    holder.timeTV.setTextColor(Color.GRAY);
                    holder.docsched_linearlayout.setBackgroundColor(Color.WHITE);
                    holder.docsched_cardview.setBackgroundColor(Color.WHITE);
                } else if (model.getInActive() == false) {
                    holder.switchbtn.setChecked(false);
                    holder.maxBook.setTextColor(Color.WHITE);
                    holder.daysTV.setTextColor(Color.WHITE);
                    holder.Pricedocshed.setTextColor(Color.WHITE);
                    holder.daysTV.setTextColor(Color.WHITE);
                    holder.timeTV.setTextColor(Color.WHITE);
                    holder.docsched_linearlayout.setBackgroundColor(Color.GRAY);
                    holder.docsched_cardview.setBackgroundColor(Color.GRAY);
                }
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
                        intent.putExtra("docname", docname);
                        intent.putExtra("type", "Update");
                        intent.putExtra("Documentid",documentid );
                        intent.putExtra("Monday",model.getMonday());
                        intent.putExtra("Tuesday",model.getTuesday());
                        intent.putExtra("Wednesday",model.getWednesday());
                        intent.putExtra("Thursday",model.getThursday());
                        intent.putExtra("Friday",model.getFriday());
                        intent.putExtra("Saturday",model.getSaturday());
                        intent.putExtra("Sunday",model.getSunday());

                        intent.putExtra("StartTime", model.getStartTime());
                        intent.putExtra("EndTime", model.getEndTime());
                        intent.putExtra("MaximumBooking", model.getMaximumBooking());
                        intent.putExtra("Price", model.getPrice());

                        startActivity(intent);
                    }
                });

                holder.switchbtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String documentid = getSnapshots().getSnapshot(position).getId();
                        if (holder.switchbtn.isChecked()) {
                            Map<String, Object> DocSched = new HashMap<>();
                            DocSched.put("InActive", true);
                            Toast.makeText(docsched_page2.this, "ON", Toast.LENGTH_SHORT).show();
                            db.collection("DoctorSchedules").document(documentid).update(DocSched);
                        } else {
                            Map<String, Object> DocSched = new HashMap<>();
                            DocSched.put("InActive", false);
                            Toast.makeText(docsched_page2.this, "OFF", Toast.LENGTH_SHORT).show();
                            db.collection("DoctorSchedules").document(documentid).update(DocSched);

                        }
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
                intent.putExtra("docname", docname);
                intent.putExtra("type", "Add");
                startActivity(intent);
            }
        });
    }

    private class DocSchedViewHolder extends RecyclerView.ViewHolder {

        LinearLayout docsched_linearlayout;
        CardView docsched_cardview;

        TextView daysTV;
        TextView timeTV;
        TextView maxBook;
        TextView Pricedocshed;
        Button editbtn;
        Button deletebtn;
        Switch switchbtn;

        public DocSchedViewHolder(@NonNull View itemView) {
            super(itemView);
            docsched_linearlayout = itemView.findViewById(R.id.docsched_linearlayout);
            docsched_cardview = itemView.findViewById(R.id.docsched_cardview);
            daysTV = itemView.findViewById(R.id.list_docdays);
            timeTV = itemView.findViewById(R.id.list_doctime);
            editbtn = itemView.findViewById(R.id.list_docedit);
            deletebtn = itemView.findViewById(R.id.list_docdelete);
            switchbtn = itemView.findViewById(R.id.btnswitch);

            maxBook = itemView.findViewById(R.id.docsched_maxbooking);
            Pricedocshed = itemView.findViewById(R.id.docsched_price);


        }
    }
}