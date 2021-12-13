package com.diet.trinity.activity;

import android.os.Build;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

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
import com.android.billingclient.api.SkuDetailsResponseListener;
import com.diet.trinity.R;

import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.annotation.RequiresApi;

import java.util.ArrayList;
import java.util.List;

public class PaypalLinkActivity extends AppCompatActivity {

    ImageView btnPayNow_bronze,btnPayNow_gold;
    EditText edtAmount;
    String amount = "";
    private BillingClient billingClient;
    private SkuDetails skuDetails;
    private SkuDetails gold, bronze;
    private PurchasesUpdatedListener purchasesUpdatedListener = new PurchasesUpdatedListener() {
        @Override
        public void onPurchasesUpdated(BillingResult billingResult, List<Purchase> purchases) {
            if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK
                    && purchases != null) {
                for (Purchase purchase : purchases) {
                    handlePurchase(purchase);
                }
            } else if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.USER_CANCELED) {
                // Handle an error caused by a user cancelling the purchase flow.
            } else {
                // Handle any other error codes.
            }
        }
    };

    BillingFlowParams billingFlowParams;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paypal_link);
        setUpBillingClient();

        btnPayNow_bronze = findViewById(R.id.BronzePayNow);
        btnPayNow_gold = findViewById(R.id.GoldPayNow);
        edtAmount = findViewById(R.id.edtAmount);

        btnPayNow_bronze.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View view) {
                amount = "3";
                billingFlowParams = BillingFlowParams.newBuilder()
                        .setSkuDetails(bronze)
                        .build();
                int response = billingClient.launchBillingFlow(PaypalLinkActivity.this, billingFlowParams).getResponseCode();
                Log.e("tag", response+"");
            }
        });

        btnPayNow_gold.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View view) {
                amount = "49.99";
                billingFlowParams = BillingFlowParams.newBuilder()
                        .setSkuDetails(gold)
                        .build();
                int response = billingClient.launchBillingFlow(PaypalLinkActivity.this, billingFlowParams).getResponseCode();
                Log.e("tag", response+"");
            }
        });
    }

    private void setUpBillingClient() {
        billingClient = BillingClient.newBuilder(this)
                .setListener(purchasesUpdatedListener)
                .enablePendingPurchases()
                .build();
        startConnection();
    }

    private void startConnection() {
        billingClient.startConnection(new BillingClientStateListener() {
            @Override
            public void onBillingSetupFinished(BillingResult billingResult) {
                if (billingResult.getResponseCode() ==  BillingClient.BillingResponseCode.OK) {
                    Log.v("TAG_INAPP","Setup Billing Done");
                    queryAvaliableProducts();
                }
            }
            @Override
            public void onBillingServiceDisconnected() {
                // Try to restart the connection on the next request to
                // Google Play by calling the startConnection() method.
                Log.e("Billing Client =====>", "Try to restart the connection on the next request to Google Play by calling the startConnection() method");
            }
        });
    }

    private void queryAvaliableProducts() {
        List<String> skuList = new ArrayList<>();
        skuList.add("1vtr1n1t_4");
        skuList.add("2vtr1n1t_4");
        SkuDetailsParams.Builder params = SkuDetailsParams.newBuilder();
        params.setSkusList(skuList).setType(BillingClient.SkuType.INAPP);

        billingClient.querySkuDetailsAsync(params.build(),
                new SkuDetailsResponseListener() {
                    @Override
                    public void onSkuDetailsResponse(BillingResult billingResult,
                                                     List<SkuDetails> skuDetailsList) {
                        // Process the result.
                        if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK && !skuDetailsList.isEmpty()) {

                            for (SkuDetails skuDetails : skuDetailsList) {
                                updateUI(skuDetails);
                            }
                        }
                    }
                });
    }

    private void updateUI(SkuDetails skuDetails) {
        this.skuDetails = skuDetails;
        Log.e("skuDetailsList is not empty", skuDetails.getSku());
        if (skuDetails.getSku().equals("1vtr1n1t_4")) {
            bronze = skuDetails;
        } else if (skuDetails.getSku().equals("2vtr1n1t_4")) {
            gold = skuDetails;
        }
    }

    void handlePurchase(Purchase purchase) {
        ConsumeParams consumeParams =
                ConsumeParams.newBuilder()
                        .setPurchaseToken(purchase.getPurchaseToken())
                        .build();

        ConsumeResponseListener listener = new ConsumeResponseListener() {
            @Override
            public void onConsumeResponse(BillingResult billingResult, String purchaseToken) {
                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                    String paymentDetails = null;
                    startActivity(new Intent(PaypalLinkActivity.this, PaymentDetails.class)
                            .putExtra("PaymentDetails", paymentDetails)
                            .putExtra("Amount", amount));
                    finish();
                }
                else {
                    Log.e("loadPaymentData failed", String.format("Error code: %d", billingResult.getResponseCode() ));
                    Toast.makeText(PaypalLinkActivity.this, "Invalid", Toast.LENGTH_SHORT).show();
                }
            }
        };

        billingClient.consumeAsync(consumeParams, listener);
    }
}