package com.diet.trinity.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.opengl.Visibility;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.diet.trinity.R;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.diet.trinity.Utility.Common;
import com.diet.trinity.model.PersonalData;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {
    EditText usernameE, useremailE, userpassE, userconfirmE;
    String result, useremail, userpass, userconfirm, username;
    ImageButton register_btn;
    ProgressDialog mProgressDialog;
    ImageView   img_back;

    String url, keytoken;
    LinearLayout goLogin;
    RequestQueue queue;
    String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    TextView already_account, passWarning;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        register_btn = findViewById(R.id.RegisterButton);
        useremailE = findViewById(R.id.EmailAddress);
        userpassE = findViewById(R.id.Password);
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

        userpassE.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String user_pass = userpassE.getText().toString();

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


        userpass = userpassE.getText().toString();
        useremail = useremailE.getText().toString();

        if (TextUtils.isEmpty(userpass)) {
            Toast.makeText(RegisterActivity.this, "User password Field is Empty!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(useremail)) {
            Toast.makeText(RegisterActivity.this, "User Email Field is Empty!", Toast.LENGTH_SHORT).show();
            return;
        }
        else{
            if (!(useremail.matches(emailPattern)))
            {
                Toast.makeText(getApplicationContext(),"Invalid email address", Toast.LENGTH_SHORT).show();
            }
            else{
                if (!(TextUtils.isEmpty(userpass)))
                {
                    RegisterUser(userpass, useremail);
                }
            }
        }
    }

    private void RegisterUser(final String userpassT,  final String useremailT) {
        mProgressDialog = new ProgressDialog(RegisterActivity.this);
        mProgressDialog.setTitle("SignUp...");
        mProgressDialog.setIndeterminate(false);
        mProgressDialog.show();

        userpassE.setEnabled(false);
        useremailE.setEnabled(false);

        url = Common.getInstance().getRegisterUrl();
        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        mProgressDialog.dismiss();
                        JSONObject jsonObject = null;
                        try {
                            jsonObject = new JSONObject(response);
                            result = jsonObject.getString("success");
                            if (result.equals("true")){
                                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                                intent.putExtra("activity", "register");
                                startActivity(intent);
                                finish();
                            } else {
                            }
                            already_account.setVisibility(View.INVISIBLE);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        already_account.setVisibility(View.VISIBLE);
                    }
                }){

            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String> params = new HashMap<String, String>();
                int gender = 0;
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                String birthday = dateFormat.format(PersonalData.getInstance().getBirthday());
                String start_date = dateFormat.format(PersonalData.getInstance().getStart_date());
                int membership = PersonalData.getInstance().getMembership();
                if(String.valueOf(PersonalData.getInstance().getGender()).equals("MALE")){
                    gender = 1;
                }else {
                    gender = 2;
                }

                int goal = 0;
                switch (String.valueOf(PersonalData.getInstance().getGoal())) {
                    case "LOSE" :
                        goal = 1;
                        break;
                    case "GAIN" :
                        goal = 2;
                        break;
                    case "STAY" :
                        goal = 3;
                }

                int dietMode = 0;
                switch (String.valueOf(PersonalData.getInstance().getDietMode())) {
                    case "POINT" :
                        dietMode = 1;
                        break;
                    case "UNIT" :
                        dietMode = 2;
                        break;
                }
                params.put("email", useremailT);
                params.put("password", userpassT);
                params.put("c_password", userpassT);

                params.put("type", String.valueOf(membership));
                params.put("goal", String.valueOf(goal));
                params.put("weight", String.valueOf(PersonalData.getInstance().getWeight()));
                params.put("initial_weight", String.valueOf(PersonalData.getInstance().getInitial_weight()));
                params.put("gender", String.valueOf(gender));
                params.put("height", String.valueOf(PersonalData.getInstance().getHeight()));
                params.put("birthday", birthday);
                params.put("age", String.valueOf(PersonalData.getInstance().getAge()));
                params.put("start_date", start_date);
                params.put("gymType", String.valueOf(PersonalData.getInstance().getGymType()));
                params.put("total_exercise", String.valueOf(PersonalData.getInstance().getTotal_exercise()));
                params.put("goal_weight", String.valueOf(PersonalData.getInstance().getGoal_weight()));
                params.put("weekly_reduce", String.valueOf(PersonalData.getInstance().getWeekly_reduce()));
                params.put("dietMode", String.valueOf(dietMode));

                System.out.println("using entrySet and toString");
                for (Map.Entry<String, String> entry : params.entrySet()) {
                    System.out.println(entry);
                }
                System.out.println();

                return params;
            }
        };
        queue = Volley.newRequestQueue(RegisterActivity.this);
        queue.add(postRequest);
    }
}