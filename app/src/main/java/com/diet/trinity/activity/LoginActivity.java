package com.diet.trinity.activity;

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

import androidx.appcompat.app.AppCompatActivity;

import com.diet.trinity.MainApplication;
import com.diet.trinity.R;
import com.diet.trinity.Utility.Constants;
import com.diet.trinity.Utility.Global;
import com.diet.trinity.data.api.REST;
import com.diet.trinity.data.models.Token;
import com.pixplicity.easyprefs.library.Prefs;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {
    EditText etEmail, etPass;
    ImageButton loginBtn;
    String stEmail, stPass, token;
    ImageView   img_back;
    TextView txForgot;
    LinearLayout goRegister;

    //----- loading dialog -----//
    ProgressDialog mProgressDialog;
    String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initView();
        addEventListener();
    }

    private void initView() {
        etEmail = findViewById(R.id.EmailAddress);
        etPass = findViewById(R.id.Password);
        loginBtn = findViewById(R.id.RegisterButton);
        goRegister = findViewById(R.id.goRegister);
        img_back = findViewById(R.id.imgBack);
        txForgot = findViewById(R.id.forgot);
    }

    private void addEventListener() {
        final String sTrial = getIntent().getStringExtra("activity");
        switch (sTrial) {
            case "goal" :
                if (goRegister.getVisibility() == View.VISIBLE)
                    goRegister.setVisibility(View.GONE);
                break;
            case "welcome":
            case "register":
            default:
                if (goRegister.getVisibility() == View.GONE)
                    goRegister.setVisibility(View.VISIBLE);
                break;
        }
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stEmail = etEmail.getText().toString().trim();
                stPass = etPass.getText().toString().trim();
                if (stEmail.isEmpty() || stPass.isEmpty()) {
                    Toast.makeText(LoginActivity.this, getResources().getString(R.string.entry_info_login), Toast.LENGTH_SHORT).show();
                    return;
                } else if (!(stEmail.matches(emailPattern))) {
                    Toast.makeText(getApplicationContext(), "Invalid email address", Toast.LENGTH_SHORT).show();
                } else {
                    ApiLogin();
                }
            }
        });
        goRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });
        img_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent;
                switch (sTrial) {
                    case "welcome" :
                        intent = new Intent(LoginActivity.this, WelcomeActivity.class);
                        break;
                    case "goal" :
                        intent = new Intent(LoginActivity.this, GoalActivity.class);
                        break;
                    case "register":
                        intent = new Intent(LoginActivity.this, RegisterActivity.class);
                        break;
                    default:
                        intent = new Intent(LoginActivity.this, GoalActivity.class);
                }

                startActivity(intent);
                finish();
            }
        });
        txForgot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
                startActivity(intent);
            }
        });
    }

    private void ApiLogin() {
        mProgressDialog = new ProgressDialog(LoginActivity.this);
        mProgressDialog.setTitle("Sign-In...");
        mProgressDialog.setIndeterminate(false);
        mProgressDialog.show();

        REST rest = MainApplication.getContainer().get(REST.class);
        rest.login(stEmail, stPass)
            .enqueue(new Callback<Token>() {
                @Override
                public void onResponse(Call<Token> call, Response<Token> response) {
                    mProgressDialog.dismiss();

                    Token result = response.body();
                    if (result != null) {
                        if (result.success == true) {
                            token = result.token;
                            Prefs.putString(Constants.PREF_SERVER_TOKEN, token);

                            Intent intent = new Intent(getBaseContext(), DailyCaleandarActivity.class);
                            startActivity(intent);
                            finish();
                        }
                        else {
                            Toast.makeText(LoginActivity.this, getResources().getString(R.string.offline_text), Toast.LENGTH_LONG).show();
                        }
                    }
                    else {
                        Toast.makeText(LoginActivity.this, getResources().getString(R.string.offline_text), Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(Call<Token> call, Throwable t) {
                    Toast.makeText(LoginActivity.this, getResources().getString(R.string.offline_text), Toast.LENGTH_LONG).show();
                    mProgressDialog.dismiss();
                }
            });
    }


}