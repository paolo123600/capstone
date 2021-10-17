package com.medicall.capstone;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.text.SpannedString;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.ConsumeParams;
import com.android.billingclient.api.ConsumeResponseListener;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.SkuDetailsParams;
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

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class selectDate extends AppCompatActivity implements DatePickerDialog.OnDateSetListener , PurchasesUpdatedListener  {
    private static final String PREF_FILE = "MyPref" ;
    String PRODUCT_ID = "";

    ProgressDialog progressDialog;

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
    Date finaldateee;
    String finalend , finalstart ;
    int finalcount;
    private PreferenceManager preferenceManager;
    BillingClient billingClient;
    ImageView back;
    String finalprice;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selectdate);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        docschedlist = (RecyclerView) findViewById(R.id.recycleviewdocsched);
        etDate = findViewById(R.id.et_date);
        gv = (GlobalVariables) getApplicationContext();

        calendar = Calendar.getInstance();

        preferenceManager = new PreferenceManager(getApplicationContext());
        billingClient = BillingClient.newBuilder(this)
                .enablePendingPurchases().setListener(this).build();
        billingClient.startConnection(new BillingClientStateListener() {
            @Override
            public void onBillingSetupFinished(@NonNull BillingResult billingResult) {
                if(billingResult.getResponseCode()==BillingClient.BillingResponseCode.OK){
                    Purchase.PurchasesResult queryPurchase = billingClient.queryPurchases(BillingClient.SkuType.INAPP);
                    List <Purchase> queryPurchases = queryPurchase.getPurchasesList();
                    if (queryPurchases!=null && queryPurchases.size()>0){
                        handlePurchases(queryPurchases);
                    }
                }
            }


            @Override
            public void onBillingServiceDisconnected() {

            }
        });
        patuid = preferenceManager.getString(Constants.KEY_USER_ID);
        Year = calendar.get(Calendar.YEAR) ;
        Month = calendar.get(Calendar.MONTH);
        Day = calendar.get(Calendar.DAY_OF_MONTH);
        Hour = calendar.get(Calendar.HOUR_OF_DAY);
        Minute = calendar.get(Calendar.MINUTE);

        docid = gv.getSDDocUid();

        back = findViewById(R.id.backspace);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), selectDoc.class);
                startActivity(intent);
            }
        });

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

                        Toast.makeText(selectDate.this, "Datepicker Canceled", Toast.LENGTH_SHORT).show();
                    }
                });

                datePickerDialog.show(getSupportFragmentManager(), "DatePickerDialog");
            }
        });


    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), selectDoc.class);
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
        Date finalDate1 = date2;
        adapter = new FirestoreRecyclerAdapter<DocSchedModel, DocSchedViewHolder>(options) {
            @NonNull
            @Override
            public DocSchedViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.selectdate_recyclerview,parent,false);
                return new selectDate.DocSchedViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull DocSchedViewHolder holder, int position, @NonNull DocSchedModel model) {

                db.collection("Schedules").whereEqualTo("DoctorUId",gv.getSDDocUid()).whereEqualTo("StartTime",model.getStartTime()).whereEqualTo("EndTime", model.getEndTime()).whereEqualTo("Date", finalDate).whereIn("Status", Arrays.asList("Paid","Pending Approval","Approved")).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {

                            int count = task.getResult().size();
                            int maxbook = Integer.parseInt(model.getMaximumBooking());
                            if (maxbook == count){
                                holder.list_numberbook.setText("No of Books: "+"Full");
                                holder.list_bookbtn.setVisibility(View.GONE);
                            }else {
                                holder.list_numberbook.setText("No of Books: "+count);
                                holder.list_bookbtn.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {

                                        AlertDialog.Builder builder = new AlertDialog.Builder(selectDate.this);
                                        builder.setCancelable(true);
                                        builder.setTitle("Booking");
                                        builder.setMessage(Html.fromHtml("Do you want to book this schedule?" + "\n" + "<font color='#D50000'> WARNING: There will be no refund once confirmed</font>"));
                                        builder.setPositiveButton("Confirm",
                                                new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {

                                                        switch (model.getPrice()){
                                                            case "appointment_150":
                                                                PRODUCT_ID = "appointment_150";
                                                                break;

                                                            case "appointment_200":
                                                                PRODUCT_ID = "appointment_200";
                                                                break;

                                                            case "appointment_250":
                                                                PRODUCT_ID = "appointment_250";
                                                                break;

                                                            case "appointment_300":
                                                                PRODUCT_ID = "appointment_300";
                                                                break;
                                                            default: break;

                                                        }

                                                        finalstart= model.getStartTime();
                                                        finalend = model.getEndTime();
                                                        finalcount= count+1;
                                                        finaldateee= finalDate;
                                                        finalprice = model.getPrice();
                                                        if(billingClient.isReady()){
                                                            initiatePurchase();
                                                        }
                                                        else {
                                                            billingClient = BillingClient.newBuilder(selectDate.this)
                                                                    .enablePendingPurchases().setListener(selectDate.this).build();
                                                            billingClient.startConnection(new BillingClientStateListener() {
                                                                @Override
                                                                public void onBillingSetupFinished(@NonNull BillingResult billingResult) {
                                                                    if(billingResult.getResponseCode()==BillingClient.BillingResponseCode.OK){
                                                                        Purchase.PurchasesResult queryPurchase = billingClient.queryPurchases(BillingClient.SkuType.INAPP);
                                                                        List <Purchase> queryPurchases = queryPurchase.getPurchasesList();
                                                                        if (queryPurchases!=null && queryPurchases.size()>0){
                                                                            handlePurchases(queryPurchases);
                                                                        }
                                                                    }
                                                                }

                                                                @Override
                                                                public void onBillingServiceDisconnected() {

                                                                }
                                                            });
                                                        }

                                                    }
                                                });
                                        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
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
                holder.list_time.setText("Time: "+model.getStartTime()+" - "+model.getEndTime());
                holder.list_maxbook.setText("Max Booking: "+model.getMaximumBooking());

                switch (model.getPrice()){
                    case "appointment_150":
                        holder.list_price.setText("Price: ₱150");
                        break;

                    case "appointment_200":
                        holder.list_price.setText("Price: ₱200");
                        break;

                    case "appointment_250":
                        holder.list_price.setText("Price: ₱250");
                        break;

                    case "appointment_300":
                        holder.list_price.setText("Price: ₱300");
                        break;
                    default: break;


                }


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
    private boolean verifyValidSignature(String signedData, String signature) {
        try{
            String base64Key = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAzOFUr4mee/QUtMgNn+9KfBp9PKkoleQBaqLoxcvMJYve58Z+fCxWkypXRAz/DRM28SEbfq38sb/wC9At+3Mn61B0hfBtGnQ1z7FRldON57ewgFP2opt3uy8N7GOPTETBw/kiqvgP6kCkfpdeHUYc6FQJ76WQvL04ygWw23UzbIZsAKgjGZ1sNVST7sw0uFXKFUxV9Xd4BC4hLHjf8nhaISHQoyJH+C2znPNjOeJBov8Zc2f6ecTnd3e33O7/SBtqqtlwn/VrF4wzIy08IrWM127PRzzOrwYKAEV79fRvt979CkrUvnITza33hBk6nxYCqjBwo9eWXrav3AlRnlxHtwIDAQAB";
            return Security.verifyPurchase(base64Key, signedData, signature);
        }
        catch (IOException e){
            return false;
        }
    }  private SharedPreferences getPreferenceObject() {
        return getApplicationContext().getSharedPreferences(PREF_FILE, 0);
    }
    private SharedPreferences.Editor getPreferenceEditObject() {
        SharedPreferences pref = getApplicationContext().getSharedPreferences(PREF_FILE, 0);
        return pref.edit();
    }
    private void savePurchaseCountValueToPref(int value) {
        getPreferenceEditObject().putInt(PRODUCT_ID,value).commit();
    }



    private int getPurchaseCountValueFromPref() {
        return getPreferenceObject().getInt( PRODUCT_ID,0);
    }
    private void initiatePurchase() {
        List <String> skulist = new ArrayList<>();
        skulist.add(PRODUCT_ID);
        SkuDetailsParams.Builder params = SkuDetailsParams.newBuilder();
        params.setSkusList(skulist).setType(BillingClient.SkuType.INAPP);
        billingClient.querySkuDetailsAsync(params.build(),((billingResult, skuDetailsList) -> {
            if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK){
                if (skuDetailsList != null && skuDetailsList.size() >0){
                    BillingFlowParams flowParams = BillingFlowParams.newBuilder()
                            .setSkuDetails(skuDetailsList.get(0))
                            .build();
                    billingClient.launchBillingFlow(selectDate.this,flowParams);
                }
                else {
                    Toast.makeText(this, "Cant find the price", Toast.LENGTH_SHORT).show();
                }
            }
            else {
                Toast.makeText(this, "Error: "+billingResult.getDebugMessage(), Toast.LENGTH_SHORT).show();
            }

        }));
    }
    @Override
    public void onPurchasesUpdated(@NonNull BillingResult billingResult, @Nullable List<Purchase> purchases) {
        if(billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK && purchases !=null){
            progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Validating payment..");
            progressDialog.show();
            handlePurchases(purchases);
        }

        else if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED){
            Purchase.PurchasesResult queryAlreadyPurchasesResult = billingClient.queryPurchases(BillingClient.SkuType.INAPP);
            List <Purchase> alreadyPurchases = queryAlreadyPurchasesResult.getPurchasesList();
            if (alreadyPurchases!=null){
                handlePurchases(alreadyPurchases);
            }
        }
        else if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.USER_CANCELED){
            Toast.makeText(this, "Purchase Canceled", Toast.LENGTH_SHORT).show();
        }


        else {
            Toast.makeText(this, "Error: "+ billingResult.getDebugMessage(), Toast.LENGTH_SHORT).show();
        }
    }
    void handlePurchases(List<Purchase>  purchases){

        for(Purchase purchase:purchases) {
            //if item is purchased
            if (PRODUCT_ID.equals(purchase.getSku()) && purchase.getPurchaseState() == Purchase.PurchaseState.PURCHASED)
            {
                if (!verifyValidSignature(purchase.getOriginalJson(), purchase.getSignature())) {
                    // Invalid purchase
                    // show error to user
                    Toast.makeText(getApplicationContext(), "Error : Invalid Purchase", Toast.LENGTH_SHORT).show();
                    return;
                }
                // else purchase is valid
                else {
                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    Date currentTime = Calendar.getInstance().getTime();
                    Map<String, Object> PatSched = new HashMap<>();
                    PatSched.put("DoctorUId", docid);
                    PatSched.put("StartTime", finalstart);
                    PatSched.put("EndTime", finalend);
                    PatSched.put("Position", finalcount);
                    PatSched.put ("Date", finaldateee);
                    PatSched.put("Price", finalprice);
                    PatSched.put ("Status", "Paid" );
                    PatSched.put ("PatientUId", patuid );
                    PatSched.put ("Dnt",currentTime);
                    PatSched.put("ClinicName",gv.getSDClinic());
                    db.collection("Schedules").document().set(PatSched)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    db.collection("Clinics").whereEqualTo("ClinicName",gv.getSDClinic()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                            if ( task.isSuccessful()){
                                                if (!task.getResult().isEmpty()){
                                                    for (QueryDocumentSnapshot doc : task.getResult()) {
                                                        String docuid =doc.getId();
                                                        Map<String, Object> Pat = new HashMap<>();
                                                        Pat.put("PatUId",patuid);
                                                        db.collection("Clinics").document(docuid).collection("Patients").document(patuid).set(Pat).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                            @Override
                                                            public void onSuccess(Void aVoid) {
                                                                Log.d("TAG", "DocumentSnapshot successfully written!");
                                                                Intent intent = new Intent(selectDate.this , MainActivity.class);
                                                                startActivity(intent);
                                                            }
                                                        });
                                                    }
                                                }
                                            }
                                        }
                                    });

                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.w("TAG", "Error writing document", e);
                                }
                            });
                }
                //if item is purchased and not consumed
                if (!purchase.isAcknowledged()) {
                    ConsumeParams consumeParams = ConsumeParams.newBuilder()
                            .setPurchaseToken(purchase.getPurchaseToken())
                            .build();

                    billingClient.consumeAsync(consumeParams, consumeListener);
                    if(progressDialog.isShowing()){
                        progressDialog.dismiss();
                    }
                    Toast.makeText(getApplicationContext(), "You have successfully booked an appointment", Toast.LENGTH_SHORT).show();
                }
            }
            //if purchase is pending
            else if( PRODUCT_ID.equals(purchase.getSku()) && purchase.getPurchaseState() == Purchase.PurchaseState.PENDING)
            {
                Toast.makeText(getApplicationContext(),
                        "Purchase is Pending. Please complete Transaction", Toast.LENGTH_SHORT).show();
            }
            //if purchase is refunded or unknown
            else if(PRODUCT_ID.equals(purchase.getSku()) && purchase.getPurchaseState() == Purchase.PurchaseState.UNSPECIFIED_STATE)
            {
                Toast.makeText(getApplicationContext(), "Purchase Status Unknown", Toast.LENGTH_SHORT).show();
            }
        }
    }

    ConsumeResponseListener consumeListener = new ConsumeResponseListener() {
        @Override
        public void onConsumeResponse(BillingResult billingResult, String purchaseToken) {
            if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                int consumeCountValue=getPurchaseCountValueFromPref()+1;
                savePurchaseCountValueToPref(consumeCountValue);
            }
        }
    };
}