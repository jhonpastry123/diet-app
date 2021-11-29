package com.diet.trinity.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
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
import com.diet.trinity.model.PersonalData;
import com.pixplicity.easyprefs.library.Prefs;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {
    EditText etEmail, etPass;
    String stEmail, stPass;
    ImageButton register_btn;
    ProgressDialog mProgressDialog;
    ImageView   img_back;

    LinearLayout goLogin;
    String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    TextView already_account, passWarning;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        register_btn = findViewById(R.id.RegisterButton);
        etEmail = findViewById(R.id.EmailAddress);
        etPass = findViewById(R.id.Password);
        already_account = findViewById(R.id.alreadyaccount);
        register_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signupProcessing();
            }

        });
        passWarning = findViewById(R.id.passWarning);

        goLogin = findViewById(R.id.goLogin);
        goLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        img_back = findViewById(R.id.imgBack);
        img_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        etEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (already_account.getVisibility() == View.VISIBLE) {
                    already_account.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        etPass.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String user_pass = etPass.getText().toString();

                if(user_pass.length()>=6)
                {
                    passWarning.setVisibility(View.INVISIBLE);
                }
                else
                {
                    passWarning.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void signupProcessing() {
        stEmail = etEmail.getText().toString();
        stPass = etPass.getText().toString();

        if (TextUtils.isEmpty(stEmail)) {
            Toast.makeText(RegisterActivity.this, "User Email Field is Empty!", Toast.LENGTH_SHORT).show();
            return;
        } else if (TextUtils.isEmpty(stPass)) {
            Toast.makeText(RegisterActivity.this, "User password Field is Empty!", Toast.LENGTH_SHORT).show();
            return;
        }
        else if (!(stEmail.matches(emailPattern))) {
            Toast.makeText(getApplicationContext(), "Invalid email address", Toast.LENGTH_SHORT).show();
        }
        else{
            int type = 0;
            RegisterUser(stEmail, stPass, type);
        }
    }

    private void RegisterUser(final String email,  final String pass, final int type) {
        mProgressDialog = new ProgressDialog(RegisterActivity.this);
        mProgressDialog.setTitle("SignUp...");
        mProgressDialog.setIndeterminate(false);
        mProgressDialog.show();

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();
        String date = dtf.format(now);

        REST rest = MainApplication.getContainer().get(REST.class);
        rest.register(stEmail, stPass, type, date)
                .enqueue(new Callback<Token>() {
                    @Override
                    public void onResponse(Call<Token> call, Response<Token> response) {
                        mProgressDialog.dismiss();

                        Token result = response.body();
                        if (result != null) {
                            if (result.success == true) {
                                String token = result.token;
                                Prefs.putString(Constants.PREF_SERVER_TOKEN, token);
                                Global.token = token;
                                int user_id = result.id;
                                saveUserSetting(user_id);
                            }
                            else {
                                already_account.setVisibility(View.VISIBLE);
                            }
                        }
                        else {
                            Toast.makeText(RegisterActivity.this, getResources().getString(R.string.offline_text), Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Token> call, Throwable t) {
                        Toast.makeText(RegisterActivity.this, getResources().getString(R.string.offline_text), Toast.LENGTH_LONG).show();
                        mProgressDialog.dismiss();
                    }
                });
    }

    private void saveUserSetting(int user_id) {
        int iGoal=0, iGender=0, iDietMode=0;
        iGoal = PersonalData.getInstance().getGoal().ordinal();

        float initial_weight = PersonalData.getInstance().getInitial_weight();
        float weight = PersonalData.getInstance().getWeight();

        iGender = PersonalData.getInstance().getGender().ordinal();
        float height = PersonalData.getInstance().getHeight();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String birthday = dateFormat.format(PersonalData.getInstance().getBirthday());
        int gym_type = PersonalData.getInstance().getGymType();
        int sport_type1 = PersonalData.getInstance().getSportType1();
        int sport_type2 = PersonalData.getInstance().getSportType2();
        int sport_type3 = PersonalData.getInstance().getSportType3();
        int sport_time1 = PersonalData.getInstance().getSportTime1();
        int sport_time2 = PersonalData.getInstance().getSportTime2();
        int sport_time3 = PersonalData.getInstance().getSportTime3();

        float goal_weight = PersonalData.getInstance().getGoal_weight();
        float weekly_goal = PersonalData.getInstance().getWeekly_reduce();

        iDietMode = PersonalData.getInstance().getDietMode().ordinal();

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();
        String date = dtf.format(now);

        Log.e("weight", weight + "");

        REST rest = MainApplication.getContainer().get(REST.class);
        rest.InformationStore(user_id, iGoal, initial_weight, weight, iGender, height, birthday, gym_type, sport_type1, sport_type2, sport_type3, sport_time1, sport_time2, sport_time3, goal_weight, weekly_goal, iDietMode, date)
                .enqueue(new Callback<Boolean>() {
                    @Override
                    public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                        Boolean result = response.body();
                        Log.e("result", result + "");
                        if (result == true) {
                            Intent intent = new Intent(RegisterActivity.this, DailyCaleandarActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    }

                    @Override
                    public void onFailure(Call<Boolean> call, Throwable t) {

                    }
                });
    }

}