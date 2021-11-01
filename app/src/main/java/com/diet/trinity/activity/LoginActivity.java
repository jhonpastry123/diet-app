package com.diet.trinity.activity;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.diet.trinity.R;
import com.diet.trinity.Utility.Common;
import com.diet.trinity.Utility.DatabaseHelper;
import com.diet.trinity.Utility.Global;
import com.diet.trinity.model.DietMode;
import com.diet.trinity.model.Gender;
import com.diet.trinity.model.Goal;
import com.diet.trinity.model.PersonalData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {
    private SQLiteDatabase db, db1, db2, db3;
    private SQLiteOpenHelper openHelper, openHelper1, openHelper2, openHelper3;
    EditText email, pass;
    ImageButton loginBtn;
    String Email, Pass, result, user_id, token, useremail, userpass;
    RequestQueue queue;
    ImageView   img_back;
    LinearLayout goRegister;
    JSONArray fooditems = new JSONArray(), category_item = new JSONArray();
    String foodname, carbon_m, protein_m, fat_m, gram_m, category_id;
    TextView already_account;
    String Recipe_name, description, category, image;
    JSONArray foods;

    //----- loading dialog -----//
    ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        openHelper = new DatabaseHelper(this);
        db = openHelper.getWritableDatabase();

        email = findViewById(R.id.EmailAddress);
        pass = findViewById(R.id.Password);
        already_account = findViewById(R.id.alreadyaccount);
        loginBtn = findViewById(R.id.RegisterButton);
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mProgressDialog = new ProgressDialog(LoginActivity.this);
                mProgressDialog.setTitle("Sign-In...");
                mProgressDialog.setIndeterminate(false);
                mProgressDialog.show();

                Email = email.getText().toString().trim();
                Pass = pass.getText().toString().trim();
                if (Email.isEmpty() || Pass.isEmpty()) {
                    Toast.makeText(LoginActivity.this, getResources().getString(R.string.entry_info_login), Toast.LENGTH_SHORT).show();
                    return;
                }else {
                    ApiLogin();
                }
            }
        });

        goRegister = findViewById(R.id.goRegister);
        goRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });

        final String sTrial = getIntent().getStringExtra("activity");

        img_back = findViewById(R.id.imgBack);
        img_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent;
                switch (sTrial) {
                    case "trial" :
                        intent = new Intent(LoginActivity.this, TrialNotifyActivity.class);
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
    }
    private void locallogin(){
        if(Email.equals(useremail) && Pass.equals(userpass)){
            Intent intent = new Intent(LoginActivity.this, GoalActivity.class);
            startActivity(intent);
        }else {
            Toast.makeText(LoginActivity.this, "Wrong information.", Toast.LENGTH_SHORT).show();
        }
    }

    private void ApiLogin() {
        String url = Common.getInstance().getLoginUrl();
        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        mProgressDialog.dismiss();
                        JSONObject jsonObject = null;
                        try {
                            jsonObject = new JSONObject(response);
                            result = jsonObject.getString("success");
                            user_id = jsonObject.getString("user_id");
                            token = jsonObject.getString("accessToken");
                            JSONObject user = jsonObject.getJSONObject("user");

                            if (result.equals("true")){
                                insertData(Email, Pass, user_id, token);
                                Global.user_id = user_id;
                                Global.token = token;

                                Gender gender;
                                if(user.getInt("gender") == 1){
                                    gender = Gender.MALE;
                                }else {
                                    gender = Gender.FEMALE;
                                }

                                Goal goal = null;
                                switch (user.getInt("goal")) {
                                    case 1 :
                                        goal = Goal.LOSE;
                                        break;
                                    case 2 :
                                        goal = Goal.GAIN;
                                        break;
                                    case 3 :
                                        goal = Goal.STAY;
                                        break;
                                    default:
                                        break;
                                }

                                DietMode dietMode = null;
                                switch (user.getInt("dietMode")) {
                                    case 1 :
                                        dietMode = DietMode.POINT;
                                        break;
                                    case 2 :
                                        dietMode = DietMode.UNIT;
                                        break;
                                    default:
                                        break;
                                }

                                PersonalData.getInstance().setMembership(user.getInt("type"));
                                PersonalData.getInstance().setGoal(goal);
                                PersonalData.getInstance().setWeight((float) user.getDouble("weight"));
                                PersonalData.getInstance().setInitial_weight((float) user.getDouble("initial_weight"));
                                PersonalData.getInstance().setGender(gender);
                                PersonalData.getInstance().setHeight((float) user.getDouble("height"));
                                PersonalData.getInstance().setAge(user.getInt("age"));
                                try {
                                    PersonalData.getInstance().setBirthday(new SimpleDateFormat("yyy-MM-dd").parse(user.getString("birthday")));
                                    PersonalData.getInstance().setStart_date(new SimpleDateFormat("yyy-MM-dd").parse(user.getString("start_date")));
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                                PersonalData.getInstance().setGymType(user.getInt("gymType"));
                                PersonalData.getInstance().setTotal_exercise((float) user.getDouble("total_exercise"));
                                PersonalData.getInstance().setGoal_weight((float) user.getDouble("goal_weight"));
                                PersonalData.getInstance().setWeekly_reduce((float) user.getDouble("weekly_reduce"));
                                PersonalData.getInstance().setDietMode(dietMode);

                                Intent intent = new Intent(LoginActivity.this, DailyCaleandarActivity.class);
                                startActivity(intent);
                            } else {
                                Toast.makeText(LoginActivity.this, getResources().getString(R.string.retry_info_login), Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },

                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        locallogin();
                        Toast.makeText(LoginActivity.this, getResources().getString(R.string.offline_text), Toast.LENGTH_LONG).show();
                    }
                }){
            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String> params = new HashMap<String, String>();
                params.put("email", Email);
                params.put("password", Pass);
                params.put("c_password", Pass);

                return params;
            }
        };
        queue = Volley.newRequestQueue(LoginActivity.this);
        queue.add(postRequest);
    }

    public void insertData(String femail,String fpassword, String fuser_id, String ftoken){
        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseHelper.COL_2,femail);
        contentValues.put(DatabaseHelper.COL_3,fpassword);
        contentValues.put(DatabaseHelper.COL_4, fuser_id);
        contentValues.put(DatabaseHelper.COL_5, ftoken);

        db.insert(DatabaseHelper.TABLE_NAME,null,contentValues);
    }
}