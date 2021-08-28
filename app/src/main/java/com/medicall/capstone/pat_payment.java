package com.medicall.capstone;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.ConsumeParams;
import com.android.billingclient.api.ConsumeResponseListener;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.SkuDetails;
import com.android.billingclient.api.SkuDetailsParams;
import com.android.billingclient.api.SkuDetailsResponseListener;

import java.util.Arrays;
import java.util.List;

import com.medicall.capstone.R;

import com.medicall.capstone.adapters.MyProductAdapter;
import com.medicall.capstone.Utils.BilingClientSetup;

public class pat_payment extends AppCompatActivity implements PurchasesUpdatedListener {

    BillingClient billingClient;
    ConsumeResponseListener listener;

    Button loadProduct;
    RecyclerView recyclerView;
    TextView txtPremium;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pat_payment);
        setupBillingClient ();
        init();
    }

    private void init() {
        txtPremium = (TextView) findViewById(R.id.txt_premium);
        loadProduct = (Button) findViewById(R.id.btn_load_product);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_product);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(new DividerItemDecoration(this,layoutManager.getOrientation()));

        //Event
        loadProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (billingClient.isReady())
                {
                    SkuDetailsParams params = SkuDetailsParams.newBuilder()
                            .setSkusList(Arrays.asList("money_capstone","capstone_payment10"))
                            .setType(BillingClient.SkuType.INAPP)
                            .build();
                    billingClient.querySkuDetailsAsync(params, new SkuDetailsResponseListener() {
                        @Override
                        public void onSkuDetailsResponse(@NonNull BillingResult billingResult,
                                                         @Nullable List<SkuDetails> list) {
                            if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK)
                                loadProductToRecyclerView(list);
                            else
                                Toast.makeText(pat_payment.this, "Error code: "+billingResult.getResponseCode(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }

    private void loadProductToRecyclerView(List<SkuDetails> list) {
        MyProductAdapter adapter = new MyProductAdapter(this, list, billingClient);
        recyclerView.setAdapter(adapter);
    }

    private void setupBillingClient() {
        listener = new ConsumeResponseListener() {
            @Override
            public void onConsumeResponse(@NonNull  BillingResult billingResult, @NonNull  String s) {
                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK)
                    Toast.makeText(pat_payment.this, "Consume OK!", Toast.LENGTH_SHORT).show();

            }
        };
        billingClient = BilingClientSetup.getInstance(this, this);
        billingClient.startConnection(new BillingClientStateListener() {
            @Override
            public void onBillingSetupFinished(@NonNull  BillingResult billingResult) {
                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK){
                    
                    Toast.makeText(pat_payment.this, "Success to connect billing", Toast.LENGTH_SHORT).show();
                    //Query
                    List<Purchase> purchases = billingClient.queryPurchases(BillingClient.SkuType.INAPP)
                            .getPurchasesList();
                    handleItemAlreadyPurchase(purchases);
                } else {
                    Toast.makeText(pat_payment.this, "Error code: "+billingResult.getResponseCode(), Toast.LENGTH_SHORT).show();

                }

            }

            @Override
            public void onBillingServiceDisconnected() {
                Toast.makeText(pat_payment.this, "You are disconnected from Billing Service", Toast.LENGTH_SHORT).show();

            }
        });

    }

    private void handleItemAlreadyPurchase(List<Purchase> purchases) {

        StringBuilder purchasedItem = new StringBuilder(txtPremium.getText()); // empty
        for (Purchase purchase : purchases)
        {
            if (purchase.getSku().equals("money_capstone")) // Consume item
            {
                ConsumeParams consumeParams = ConsumeParams.newBuilder()
                        .setPurchaseToken(purchase.getPurchaseToken())
                        .build();
                billingClient.consumeAsync(consumeParams,listener);
            }
            purchasedItem.append("\n"+purchase.getSku())
                    .append("\n");
        }
        txtPremium.setText(purchasedItem.toString());
        txtPremium.setVisibility(View.VISIBLE);

    }

    @Override
    public void onPurchasesUpdated(@NonNull  BillingResult billingResult, @Nullable List<Purchase> list) {

    }
}