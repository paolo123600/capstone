package com.medicall.capstone;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.BaseBundle;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.ConsumeParams;
import com.android.billingclient.api.ConsumeResponseListener;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.SkuDetails;
import com.android.billingclient.api.SkuDetailsParams;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Testpayment extends AppCompatActivity implements PurchasesUpdatedListener {
Button confirm;
BillingClient billingClient;

    public static final String PREF_FILE= "MyPref";
    public static final String PRODUCT_ID= "appointment_90";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_testpayment);

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

        confirm = (Button) findViewById(R.id.confrimbtn);
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(billingClient.isReady()){
                    initiatePurchase();
                }
                else {
                    billingClient = BillingClient.newBuilder(Testpayment.this)
                            .enablePendingPurchases().setListener(Testpayment.this).build();
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
                    //if item is purchased and not consumed
                    if (!purchase.isAcknowledged()) {
                        ConsumeParams consumeParams = ConsumeParams.newBuilder()
                                .setPurchaseToken(purchase.getPurchaseToken())
                                .build();

                        billingClient.consumeAsync(consumeParams, consumeListener);
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
                    Toast.makeText(getApplicationContext(), "Item Consumed", Toast.LENGTH_SHORT).show();
                }
            }
        };



    private SharedPreferences getPreferenceObject() {
        return getApplicationContext().getSharedPreferences(PREF_FILE, 0);
    }
    private SharedPreferences.Editor getPreferenceEditObject() {
        SharedPreferences pref = getApplicationContext().getSharedPreferences(PREF_FILE, 0);
        return pref.edit();
    }
    private void savePurchaseCountValueToPref(int value) {
        getPreferenceEditObject().putInt("appointment_90",value).commit();
    }



    private int getPurchaseCountValueFromPref() {
        return getPreferenceObject().getInt( "appointment_90",0);
    }

    private boolean verifyValidSignature(String signedData, String signature) {
        try{
            String base64Key = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAzOFUr4mee/QUtMgNn+9KfBp9PKkoleQBaqLoxcvMJYve58Z+fCxWkypXRAz/DRM28SEbfq38sb/wC9At+3Mn61B0hfBtGnQ1z7FRldON57ewgFP2opt3uy8N7GOPTETBw/kiqvgP6kCkfpdeHUYc6FQJ76WQvL04ygWw23UzbIZsAKgjGZ1sNVST7sw0uFXKFUxV9Xd4BC4hLHjf8nhaISHQoyJH+C2znPNjOeJBov8Zc2f6ecTnd3e33O7/SBtqqtlwn/VrF4wzIy08IrWM127PRzzOrwYKAEV79fRvt979CkrUvnITza33hBk6nxYCqjBwo9eWXrav3AlRnlxHtwIDAQAB";
            return Security.verifyPurchase(base64Key, signedData, signature);
        }
        catch (IOException e){
            return false;
        }
    }

    private void initiatePurchase() {
        List <String> skulist = new ArrayList<>();
        skulist.add("appointment_90");
        SkuDetailsParams.Builder params = SkuDetailsParams.newBuilder();
        params.setSkusList(skulist).setType(BillingClient.SkuType.INAPP);
        billingClient.querySkuDetailsAsync(params.build(),((billingResult, skuDetailsList) -> {
            if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK){
                if (skuDetailsList != null && skuDetailsList.size() >0){
                    BillingFlowParams flowParams = BillingFlowParams.newBuilder()
                            .setSkuDetails(skuDetailsList.get(0))
                            .build();
                    billingClient.launchBillingFlow(Testpayment.this,flowParams);
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
}