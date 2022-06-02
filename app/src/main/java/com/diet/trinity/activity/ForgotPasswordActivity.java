package com.diet.trinity.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.diet.trinity.MainApplication;
import com.diet.trinity.R;
import com.diet.trinity.Utility.Constants;
import com.diet.trinity.data.api.REST;
import com.diet.trinity.data.models.Token;
import com.pixplicity.easyprefs.library.Prefs;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ForgotPasswordActivity extends AppCompatActivity {

    String stEmail;
    TextView txWarning;
    EditText etEmail;
    ImageView img_back;
    ImageButton resetBtn;
    String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        initView();
        addEventListener();
    }

    private void initView() {
        txWarning = findViewById(R.id.txtWarning);
        etEmail = findViewById(R.id.email);
        img_back = findViewById(R.id.imgBack);
        resetBtn = findViewById(R.id.resetPassword);
    }

    private void addEventListener() {
        img_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        resetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                stEmail = etEmail.getText().toString();

                if (stEmail.isEmpty()) {
                    Toast.makeText(ForgotPasswordActivity.this, getResources().getString(R.string.entry_info_login), Toast.LENGTH_SHORT).show();
                    return;
                } else if (!(stEmail.matches(emailPattern))) {
                    Toast.makeText(getApplicationContext(), "Invalid email address", Toast.LENGTH_SHORT).show();
                } else {
                    resetPassword();
                }
            }
        });
    }

    private void resetPassword() {
        ProgressDialog mProgressDialog = new ProgressDialog(ForgotPasswordActivity.this);
        mProgressDialog.setTitle("Reset Password Email Sending ...");
        mProgressDialog.setIndeterminate(false);
        mProgressDialog.show();

        REST rest = MainApplication.getContainer().get(REST.class);
        rest.resetPassword(stEmail)
                .enqueue(new Callback<Token>() {
                    @Override
                    public void onResponse(Call<Token> call, Response<Token> response) {
                        mProgressDialog.dismiss();

                        Token result = response.body();
                        if (result != null) {
                            if (result.success == true) {
                                Toast.makeText(ForgotPasswordActivity.this, "Reset Password Email sent to your email. Check your email.", Toast.LENGTH_LONG).show();
                            }
                            else {
                                Toast.makeText(ForgotPasswordActivity.this, getResources().getString(R.string.offline_text), Toast.LENGTH_LONG).show();
                            }
                        }
                        else {
                            Toast.makeText(ForgotPasswordActivity.this, getResources().getString(R.string.offline_text), Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Token> call, Throwable t) {
                        Toast.makeText(ForgotPasswordActivity.this, getResources().getString(R.string.offline_text), Toast.LENGTH_LONG).show();
                        mProgressDialog.dismiss();
                    }
                });
    }
}