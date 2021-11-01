package com.diet.trinity.activity;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.diet.trinity.R;
import com.diet.trinity.Utility.PurchaseTimeHelper;
import com.diet.trinity.model.PersonalData;

import java.util.Calendar;

public class PaymentDetails extends AppCompatActivity {
    TextView txtId,txtAmount,txtStatus;

    SQLiteOpenHelper openHelper_purchase;
    SQLiteDatabase db_purchase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_details);

        openHelper_purchase = new PurchaseTimeHelper(PaymentDetails.this);
        db_purchase = openHelper_purchase.getWritableDatabase();

        txtId = findViewById(R.id.txtId);
        txtAmount = findViewById(R.id.txtAmount);
        txtStatus = findViewById(R.id.txtStatus);

        Intent intent = getIntent();

        showDetails(intent.getStringExtra("Amount"));
    }

    private void showDetails(String paymentAmount) {
        if(paymentAmount.equals("3"))
        {
            PersonalData.getInstance().setMembership(1);
            PersonalData.getInstance().setPurchase_time(Calendar.getInstance().getTime().getTime());
            Intent intent = new Intent(this, RegisterActivity.class);
            startActivity(intent);
            finish();
        }
        else if(paymentAmount.equals("49.99"))
        {
            PersonalData.getInstance().setMembership(2);
            PersonalData.getInstance().setPurchase_time(Calendar.getInstance().getTime().getTime());
            Intent intent = new Intent(this, RegisterActivity.class);
            startActivity(intent);
            finish();
        }
    }

    public void insertPurchase(String purchase_time, String purchase_type){
        ContentValues contentValues = new ContentValues();
        contentValues.put(PurchaseTimeHelper.COL_2, purchase_time);
        contentValues.put(PurchaseTimeHelper.COL_3, purchase_type);
        db_purchase.insert(PurchaseTimeHelper.TABLE_NAME,null,contentValues);
    }
}
