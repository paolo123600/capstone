package com.medicall.capstone;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.firestore.DocumentSnapshot;
import com.medicall.capstone.R;

import com.medicall.capstone.utilities.Constants;
import com.medicall.capstone.utilities.PreferenceManager;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class reschedule_date extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    TextView tvDate;
    EditText etDate;
    private RecyclerView docschedlist;
    private FirestoreRecyclerAdapter adapter;
    DatePickerDialog datePickerDialog ;
    TimePickerDialog timePickerDialog ;
    int Year, Month, Day, Hour, Minute;
    FirebaseFirestore db;
    Calendar calendar ;
    GlobalVariables gv;
    String[] daysofweek = {"Monday" , "Tuesday" , "Wednesday" , "Thursday" , "Friday", "Saturday" , "Sunday"};
    ArrayList<Integer> validdaysofweek = new ArrayList<Integer>();
    String patuid;
    String docid ="";
    private PreferenceManager preferenceManager;
    int Position ;
    String price = "";
    String scheddocu, schedid;

    ImageView back;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reschedule_date);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        docschedlist = (RecyclerView) findViewById(R.id.recycleviewdocsched);
        etDate = findViewById(R.id.et_date);
        gv = (GlobalVariables) getApplicationContext();

        calendar = Calendar.getInstance();
        preferenceManager = new PreferenceManager(getApplicationContext());

        patuid = preferenceManager.getString(Constants.KEY_USER_ID);
        Year = calendar.get(Calendar.YEAR) ;
        Month = calendar.get(Calendar.MONTH);
        Day = calendar.get(Calendar.DAY_OF_MONTH);
        Hour = calendar.get(Calendar.HOUR_OF_DAY);
        Minute = calendar.get(Calendar.MINUTE);

        Position=gv.getPosition();
        docid = gv.getSDDocUid();

        Intent intent = getIntent();
        schedid = intent.getStringExtra("schedid");
        price = intent.getStringExtra("price");
        back = findViewById(R.id.backspace);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), patient_schedule.class);
                startActivity(intent);
            }
        });


        for (String days : daysofweek){
            db.collection("DoctorSchedules").whereEqualTo("DocId", gv.getSDDocUid()).whereEqualTo(days,true).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                    if (!queryDocumentSnapshots.isEmpty() ){
                        switch(days) {
                            case "Monday":
                                validdaysofweek.add(2);
                                break;
                            case "Tuesday":
                                validdaysofweek.add(3);
                                break;
                            case "Wednesday":
                                validdaysofweek.add(4);
                                break;
                            case "Thursday":
                                validdaysofweek.add(5);
                                break;
                            case "Friday":
                                validdaysofweek.add(6);
                                break;
                            case "Saturday":
                                validdaysofweek.add(7);
                                break;
                            case "Sunday":
                                validdaysofweek.add(1);
                                break;
                            default:

                                break;
                        }

                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                }
            });}

        etDate.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                datePickerDialog = DatePickerDialog.newInstance(reschedule_date.this, Year, Month, Day);
                datePickerDialog.setThemeDark(false);
                datePickerDialog.showYearPickerFirst(false);
                datePickerDialog.setTitle("Date Picker");


                // Setting Min Date to today date
                Calendar min_date_c = Calendar.getInstance();
                min_date_c.add(Calendar.DATE,2);
                datePickerDialog.setMinDate(min_date_c);

                // Setting Max Date to next 2 years
                Calendar max_date_c = Calendar.getInstance();
                max_date_c.set(Calendar.YEAR, Year+4);
                datePickerDialog.setMaxDate(max_date_c);

                //Disable all SUNDAYS and SATURDAYS between Min and Max Dates
                for (Calendar loopdate = min_date_c; min_date_c.before(max_date_c); min_date_c.add(Calendar.DATE, 1), loopdate = min_date_c) {
                    int dayOfWeek = loopdate.get(Calendar.DAY_OF_WEEK);
                    if (!validdaysofweek.contains(dayOfWeek)) {
                        Calendar[] disabledDays =  new Calendar[1];
                        disabledDays[0] = loopdate;
                        datePickerDialog.setDisabledDays(disabledDays);
                    }
                }



                datePickerDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {

                    @Override
                    public void onCancel(DialogInterface dialogInterface) {


                    }
                });

                datePickerDialog.show(getSupportFragmentManager(), "DatePickerDialog");
            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), patient_schedule.class);
        startActivity(intent);
    }

    @Override
    public void onDateSet(DatePickerDialog view, int Year, int Month, int Day ) {

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, Year);
        calendar.set(Calendar.MONTH, Month);
        calendar.set(Calendar.DAY_OF_MONTH, Day);
        Date date2 = calendar.getTime();
        String date = Day+"/"+(Month+1)+"/"+Year;
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        SimpleDateFormat format = new SimpleDateFormat("MMMM d ,yyyy");
        SimpleDateFormat simpledateformat = new SimpleDateFormat("EEEE");
        Date date1 = new Date(Year, Month, Day-1);
        String datestring = format.format(date2);
        try {
            date2 = format.parse(datestring);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        String dow = simpledateformat.format(date1);
        etDate.setText(date);


        Query query = db.collection("DoctorSchedules").whereEqualTo("DocId",gv.getSDDocUid()).whereEqualTo(dow,true).whereEqualTo("InActive",true);
        FirestoreRecyclerOptions<DocSchedModel> options = new FirestoreRecyclerOptions.Builder<DocSchedModel>()
                .setQuery(query,DocSchedModel.class)
                .build();

        Date finalDate = date2;
        adapter = new FirestoreRecyclerAdapter<DocSchedModel, reschedule_date.DocReSchedViewHolder>(options) {
            @NonNull
            @Override
            public reschedule_date.DocReSchedViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.selectdate_recyclerview,parent,false);
                return new reschedule_date.DocReSchedViewHolder(view);
            }


            @Override
            protected void onBindViewHolder(@NonNull reschedule_date.DocReSchedViewHolder holder, int position, @NonNull DocSchedModel model) {

                db.collection("Schedules").whereEqualTo("DoctorUId",gv.getSDDocUid()).whereEqualTo("StartTime",model.getStartTime()).whereEqualTo("EndTime", model.getEndTime()).whereEqualTo("Date", finalDate ).whereIn("Status", Arrays.asList("Paid","Pending Approval","Approved")).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {

                            int count = task.getResult().size();
                            int maxbook = Integer.parseInt(model.getMaximumBooking());
                            if (maxbook == count){
                                holder.list_numberbook.setText("No of Books:"+"Full");
                                holder.list_bookbtn.setVisibility(View.GONE);
                            }else {
                                holder.list_numberbook.setText("No of Books:"+count);
                                holder.list_bookbtn.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {

                                        AlertDialog.Builder builder = new AlertDialog.Builder(reschedule_date.this);
                                        builder.setCancelable(true);
                                        builder.setTitle("Reschedule");
                                        builder.setMessage("Do you want to book this schedule?");
                                        builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {

                                                Date currentTime = Calendar.getInstance().getTime();
                                                db.collection("Schedules").document(schedid).update(
                                                        "Status", "Rescheduled",
                                                        "Position",0

                                                ).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        db.collection("Schedules").document(schedid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                            if(task.isSuccessful()){
                                                                DocumentSnapshot documentSnapshot = task.getResult();
                                                                Map<String, Object> Notif = new HashMap<>();
                                                                Notif.put("DoctorUId", documentSnapshot.getString("DoctorUId"));
                                                                Notif.put ("Date", documentSnapshot.getDate("Date"));
                                                                Notif.put("AppointID",schedid);
                                                                Notif.put ("Status", "Rescheduled" );
                                                                Notif.put ("PatientUId", documentSnapshot.getString("PatientUId") );
                                                                Notif.put ("Dnt",currentTime);
                                                                Notif.put ("Seen",false);
                                                                Notif.put("ClinicName",documentSnapshot.getString("ClinicName"));
                                                                db.collection("Notification").document().set(Notif).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                    @Override
                                                                    public void onSuccess(Void aVoid) {
                                                        db.collection("Schedules").whereEqualTo("DoctorUId", gv.getSDDocUid()).whereEqualTo("StartTime",gv.getSDtimestart()).whereEqualTo("EndTime",gv.getSDtimestop()).whereIn("Status", Arrays.asList("Paid","Approved","Pending Approval")).whereEqualTo("Date",gv.getDDate()).get()
                                                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                                    @Override
                                                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                                        if (task.isSuccessful()){

                                                                            for (QueryDocumentSnapshot doc : task.getResult()) {
                                                                                int docposition = doc.getLong("Position").intValue();

                                                                                if (docposition > Position){
                                                                                    String docuid = doc.getId();
                                                                                    db.collection("Schedules").document(docuid).update("Position", docposition-1)
                                                                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                                @Override
                                                                                                public void onSuccess(Void aVoid) {


                                                                                                }
                                                                                            });
                                                                                }
                                                                            }
                                                                            Map<String, Object> PatSched = new HashMap<>();
                                                                            PatSched.put("DoctorUId", docid);
                                                                            PatSched.put("StartTime", model.getStartTime());
                                                                            PatSched.put("EndTime", model.getEndTime());
                                                                            PatSched.put("Position", count+1);
                                                                            PatSched.put("Price", price);
                                                                            PatSched.put ("Date", finalDate);
                                                                            PatSched.put ("Status", "Paid" );
                                                                            PatSched.put ("PatientUId", patuid );
                                                                            PatSched.put ("Dnt",currentTime);
                                                                            PatSched.put("ClinicName",gv.getSDClinic());

                                                                            db.collection("Schedules").document(schedid).update(PatSched);
                                                                            Toast.makeText(reschedule_date.this, "Successfully Rescheduled", Toast.LENGTH_SHORT).show();
                                                                            Intent intent = new Intent(reschedule_date.this, MainActivity.class);
                                                                            startActivity(intent);

                                                                        }
                                                                    }
                                                                });
                                                    }}); };}

                                                });
                                                    }
                                                });
                                            }
                                        });
                                        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                            }
                                        });

                                        AlertDialog dialog = builder.create();
                                        dialog.show();


                                    }
                                });
                            }

                        } else {
                            Log.d("TAG", "Error getting documents: ", task.getException());
                        }

                    }
                });
                holder.list_time.setText("Time:"+model.getStartTime()+" - "+model.getEndTime());
                holder.list_maxbook.setText("Max Booking:"+model.getMaximumBooking());
                holder.list_price.setText(model.getPrice());



            }
        };

        docschedlist.setHasFixedSize(true);
        docschedlist.setLayoutManager(new LinearLayoutManager(reschedule_date.this));
        docschedlist.setAdapter(adapter);
        adapter.startListening();

    }


    private class DocReSchedViewHolder extends RecyclerView.ViewHolder {
        private TextView list_time;
        private TextView list_price;
        private TextView list_maxbook;
        private TextView list_numberbook;
        private Button list_bookbtn;
        public DocReSchedViewHolder(@NonNull View itemView) {
            super(itemView);
            list_bookbtn = itemView.findViewById(R.id.rec_booknow);
            list_time = itemView.findViewById(R.id.rec_time);
            list_price = itemView.findViewById(R.id.rec_price);
            list_maxbook = itemView.findViewById(R.id.rec_maxbooking);
            list_numberbook = itemView.findViewById(R.id.rec_numbooking);
        }
    }
}

