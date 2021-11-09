package com.medicall.capstone;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.medicall.capstone.R;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class pat_blood_pressure extends AppCompatActivity {

    TextView bpdate;
    DatePickerDialog.OnDateSetListener listener;
    EditText bpupper;
    EditText bplower;
    EditText temp;
    EditText prate;
    EditText res;
    Button submitbp;

    FirebaseAuth fAuth;
    FirebaseFirestore db;
    String userId;
    String collectionBpDate, edittxtupper, edittxtlower, temps, pulse, respira;
    RecyclerView mFirestoreList;
    ImageView back;
    TextView none;

    private FirestoreRecyclerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pat_blood_pressure);
        none = findViewById(R.id.None);


        submitbp = findViewById(R.id.submit_bp);



        mFirestoreList = findViewById(R.id.recview_bp);
        fAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();



        userId = fAuth.getCurrentUser().getUid();
        back = findViewById(R.id.backspace);

        Calendar calendar = Calendar.getInstance();
        final int year = calendar.get(Calendar.YEAR);
        final int month = calendar.get(Calendar.MONTH);
        final int day = calendar.get(Calendar.DAY_OF_MONTH);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });




        Query query = db.collection("Patients").document(userId).collection("BP").orderBy("Dnt", Query.Direction.DESCENDING).limit(20);;

        FirestoreRecyclerOptions<BPModel> options = new FirestoreRecyclerOptions.Builder<BPModel>().setQuery(query, BPModel.class).build();


        adapter = new FirestoreRecyclerAdapter<BPModel, BPViewHolder>(options) {
            @NonNull
            @Override
            public BPViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.bp_list, parent, false);
                return new BPViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull BPViewHolder holder, int position, @NonNull BPModel model) {
                holder.list_bpressure.setText("BP: " + model.getUpper() + "/" + model.getLower());
                holder.list_dnt.setText(model.getDnt() + "");
                holder.Temperi.setText("Temperature: " + model.getTemperature());
                holder.Pulser.setText("Pulse Rate: " + model.getPulse());
               holder.respi.setText("Respiratory Rate: " + model.getRespiratory());

            }
        };

        mFirestoreList.setHasFixedSize(true);
        mFirestoreList.setLayoutManager(new LinearLayoutManager(this));
        mFirestoreList.setAdapter(adapter);




        listener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                month = month + 1;
                String date = dayOfMonth + "-" + month + "-" + year;
                bpdate.setText(date);


            }
        };





        submitbp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getApplicationContext(),addvitals.class);
                startActivity(intent);

            }
        });

        db.collection("Patients").document(userId).collection("BP").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable  QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if(error != null){
                    Toast.makeText(pat_blood_pressure.this, "Error Loading",Toast.LENGTH_SHORT).show();
                }
                if(value.isEmpty()){
                    none.setVisibility(View.VISIBLE);
                    mFirestoreList.setVisibility(View.GONE);
                } else {
                    none.setVisibility(View.GONE);
                    mFirestoreList.setVisibility(View.VISIBLE);
                }

            }
        });





    }

    private String makeDateString(int day, int month, int year){
        return  day + "-" +  month + "-" + year;
    }

    private String getTodaysDate() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        month = month + 1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        return makeDateString(day, month, year);
    }

    private class BPViewHolder extends RecyclerView.ViewHolder {

        private TextView list_bpressure, list_date, list_dnt, Temperi, Pulser, respi;

        public BPViewHolder(@NonNull View itemView) {
            super(itemView);

            list_bpressure = itemView.findViewById(R.id.BPlist_bloodp);
            list_dnt = itemView.findViewById(R.id.BPlist_dnt);
            Temperi = itemView.findViewById(R.id.Temperi);
            Pulser = itemView.findViewById(R.id.Pulse);
            respi = itemView.findViewById(R.id.respir);


        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }


    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }
}