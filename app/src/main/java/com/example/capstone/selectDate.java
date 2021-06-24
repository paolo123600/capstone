package com.example.capstone;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.date.MonthAdapter;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class selectDate extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {
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
    String docid ="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selectdate);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        docschedlist = (RecyclerView) findViewById(R.id.recycleviewdocsched);
        etDate = findViewById(R.id.et_date);
        gv = (GlobalVariables) getApplicationContext();

        calendar = Calendar.getInstance();

        Year = calendar.get(Calendar.YEAR) ;
        Month = calendar.get(Calendar.MONTH);
        Day = calendar.get(Calendar.DAY_OF_MONTH);
        Hour = calendar.get(Calendar.HOUR_OF_DAY);
        Minute = calendar.get(Calendar.MINUTE);

        docid = gv.getSDDocUid();
        //checking of schedule days of week

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
                datePickerDialog = DatePickerDialog.newInstance(selectDate.this, Year, Month, Day);
                datePickerDialog.setThemeDark(false);
                datePickerDialog.showYearPickerFirst(false);
                datePickerDialog.setTitle("Date Picker");


                // Setting Min Date to today date
                Calendar min_date_c = Calendar.getInstance();
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

                        Toast.makeText(selectDate.this, "Datepicker Canceled", Toast.LENGTH_SHORT).show();
                    }
                });

                datePickerDialog.show(getSupportFragmentManager(), "DatePickerDialog");
            }
        });
    }

    @Override
    public void onDateSet(DatePickerDialog view, int Year, int Month, int Day ) {

        String date = "Date: "+Day+"/"+(Month+1)+"/"+Year;
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        SimpleDateFormat simpledateformat = new SimpleDateFormat("EEEE");
        Date date1 = new Date(Year, Month, Day-1);
        String dow = simpledateformat.format(date1);
        etDate.setText(date);

        Query query = db.collection("DoctorSchedules").whereEqualTo("DocId",gv.getSDDocUid()).whereEqualTo(dow,true).whereEqualTo("InActive",true);
        FirestoreRecyclerOptions<DocSchedModel> options = new FirestoreRecyclerOptions.Builder<DocSchedModel>()
                .setQuery(query,DocSchedModel.class)
                .build();

        adapter = new FirestoreRecyclerAdapter<DocSchedModel, DocSchedViewHolder>(options) {
            @NonNull
            @Override
            public DocSchedViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.selectdate_recyclerview,parent,false);
                return new selectDate.DocSchedViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull DocSchedViewHolder holder, int position, @NonNull DocSchedModel model) {
             holder.list_time.setText("Time:"+model.getStartTime()+" - "+model.getEndTime());
                holder.list_maxbook.setText("Max Booking:"+model.getMaximumBooking());
                holder.list_price.setText("Price:"+model.getPrice());
                holder.list_numberbook.setText("No of Books:"+"");
                holder.list_bookbtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
//                        gv.setSDtimestart(model);

                    }
                });
            }
        };

        docschedlist.setHasFixedSize(true);
        docschedlist.setLayoutManager(new LinearLayoutManager(selectDate.this));
        docschedlist.setAdapter(adapter);
        adapter.startListening();



    }


    private class DocSchedViewHolder extends RecyclerView.ViewHolder {
        private TextView list_time;
        private TextView list_price;
        private TextView list_maxbook;
        private TextView list_numberbook;
        private Button list_bookbtn;
        public DocSchedViewHolder(@NonNull View itemView) {
            super(itemView);
            list_bookbtn = itemView.findViewById(R.id.rec_booknow);
            list_time = itemView.findViewById(R.id.rec_time);
            list_price = itemView.findViewById(R.id.rec_price);
            list_maxbook = itemView.findViewById(R.id.rec_maxbooking);
            list_numberbook = itemView.findViewById(R.id.rec_numbooking);
        }
    }
}