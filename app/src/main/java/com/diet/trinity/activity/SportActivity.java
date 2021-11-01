package com.diet.trinity.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.Spinner;
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
import com.diet.trinity.Utility.Global;
import com.diet.trinity.model.PersonalData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import static android.view.View.GONE;
import static android.view.View.INVISIBLE;
import static android.view.View.OnClickListener;
import static android.view.View.VISIBLE;

public class SportActivity extends AppCompatActivity {
    RadioGroup _gym;
    int mGym = -1;
    ArrayList<String> sport_names = new ArrayList<>();
    RequestQueue queue;
    String sport1_name, sport1_min, sport1_efficient, sport2_name, sport2_min, sport2_efficient, sport3_name, sport3_min, sport3_efficient, sport1_type, sport2_type, sport3_type;
    EditText txt1, txt2, txt3;
    LinearLayout layout2, layout3;
    Map<String, String> element_data = new HashMap<String, String>();
    Map<String, String> element_id_data = new HashMap<String, String>();

    ArrayList<String> dropList = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sport);

        load_data();

        txt1 = findViewById(R.id.min1);
        txt2 = findViewById(R.id.min2);
        txt3 = findViewById(R.id.min3);

        layout2 = findViewById(R.id.layout2);
        layout3 = findViewById(R.id.layout3);

        layout2.setVisibility(INVISIBLE);
        layout3.setVisibility(INVISIBLE);

        addEventListener();
    }

    private void load_data(){
        dropList.clear();
        dropList.add("");
        String foodUrl = Common.getInstance().getSportUrl();
        StringRequest postRequest = new StringRequest(Request.Method.GET, foodUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        JSONArray jsonObject = null;
                        try {
                            jsonObject = new JSONArray(response);
                            for(int i=0; i<jsonObject.length();i++)
                            {
                                dropList.add(jsonObject.getJSONObject(i).getString("name"));
                                element_data.put(jsonObject.getJSONObject(i).getString("name"), jsonObject.getJSONObject(i).getString("coefficient"));
                                element_id_data.put(jsonObject.getJSONObject(i).getString("name"), jsonObject.getJSONObject(i).getString("id"));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        }){
        };
        queue = Volley.newRequestQueue(SportActivity.this);
        queue.add(postRequest);

        DropDown("Select Sports", "1");
    }

    private void addEventListener(){
        findViewById(R.id.imgNext).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mGym != -1) {
                    PersonalData.getInstance().setGymType(mGym);

                    sport1_min = txt1.getText().toString();
                    sport2_min = txt2.getText().toString();
                    sport3_min = txt3.getText().toString();

                    if(sport1_min.length()==0)
                        sport1_min = "0";
                    if(sport2_min.length()==0)
                        sport2_min = "0";
                    if(sport3_min.length()==0)
                        sport3_min = "0";

                    if(sport1_name!=null && sport1_name.length()>0)
                    {
                        sport1_efficient = element_data.get(sport1_name);
                    }
                    else {
                        sport1_efficient = "0";
                        sport1_type = "-1";
                    }

                    if(sport2_name!=null && sport2_name.length()>0)
                    {
                        sport2_efficient = element_data.get(sport2_name);
                    }
                    else {
                        sport2_efficient = "0";
                        sport2_type = "-1";
                    }

                    if(sport3_name!=null && sport3_name.length()>0)
                    {
                        sport3_efficient = element_data.get(sport3_name);
                    }
                    else {
                        sport3_efficient = "0";
                        sport3_type = "-1";
                    }

                    float weight = PersonalData.getInstance().getWeight();
                    float total = weight * (Float.parseFloat(sport1_efficient) * Float.parseFloat(sport1_min) + Float.parseFloat(sport2_efficient) * Float.parseFloat(sport2_min) + Float.parseFloat(sport3_efficient) * Float.parseFloat(sport3_min));
                    PersonalData.getInstance().setTotal_exercise(total);

                    sendSettings();

                    Intent intent = new Intent(getBaseContext(), IdealWeightActivity.class);
                    startActivity(intent);
                }
                else {
                    Toast.makeText(SportActivity.this, "Please select Gym Type", Toast.LENGTH_SHORT).show();
                }
            }
        });

        findViewById(R.id.imgBack).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                finish();
            }
        });

        _gym = findViewById(R.id.radGym);
        final LinearLayout _sportContainer = findViewById(R.id.linSportContainer);
        _gym.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                View radioButton = group.findViewById(checkedId);
                int index = group.indexOfChild(radioButton);
                mGym = index % 5 ;

                if(mGym == 4){
                    _sportContainer.setVisibility(VISIBLE);
                }
                else{
                    _sportContainer.setVisibility(GONE);
                }
            }
        });

        txt1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(sport1_name.length()>0  && !(txt1.getText().toString().equals("0")) && txt1.getText().toString().length()>0)
                {
                    layout2.setVisibility(VISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        txt2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(sport2_name.length()>0 && !(txt2.getText().toString().equals("0")) && txt2.getText().toString().length()>0)
                {
                    layout3.setVisibility(VISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void DropDown(String title, String id) {
        Spinner dropdown1 = findViewById(R.id.sport1);
        Spinner dropdown2 = findViewById(R.id.sport2);
        Spinner dropdown3 = findViewById(R.id.sport3);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.spinner_dropdown_item, dropList);
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        dropdown1.setAdapter(adapter);
        dropdown2.setAdapter(adapter);
        dropdown3.setAdapter(adapter);

        dropdown1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedItemText = (String) parent.getItemAtPosition(position);
                sport1_name = selectedItemText;
                sport1_type = position +"";
                if(sport1_name.length()>0  && !(txt1.getText().toString().equals("0")) && txt1.getText().toString().length()>0)
                {
                    layout2.setVisibility(VISIBLE);
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        dropdown2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedItemText = (String) parent.getItemAtPosition(position);
                sport2_name = selectedItemText;
                sport2_type = position+"";
                if(sport2_name.length()>0  && !(txt2.getText().toString().equals("0")) && txt2.getText().toString().length()>0)
                {
                    layout3.setVisibility(VISIBLE);
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        dropdown3.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedItemText = (String) parent.getItemAtPosition(position);
                sport3_name = selectedItemText;
                sport3_type = position+"";
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void sendSettings(){
        String settingUrl = Common.getInstance().getSettingUrl();
        final String access_token = Global.token;

        sport1_min = txt1.getText().toString();
        sport2_min = txt2.getText().toString();
        sport3_min = txt3.getText().toString();

        StringRequest postRequest = new StringRequest(Request.Method.POST, settingUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        JSONObject jsonObject = null;
                        try {
                            jsonObject = new JSONObject(response);

                            String result = jsonObject.getString("success");

                            if (result.equals("true")){
                            }
                            else {
                                Toast.makeText(SportActivity.this, getResources().getString(R.string.retry_info_login), Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(SportActivity.this, getResources().getString(R.string.offline_text), Toast.LENGTH_LONG).show();
            }
        }){

            @Override
            public Map<String, String> getHeaders()  {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer "+access_token);
                return headers;
            }

            @Override
            protected Map<String, String> getParams()
            {

                Map<String, String> params = new HashMap<String, String>();
                params.put("user_id", Global.user_id);
                params.put("date", getCurrentDate());
                params.put("exercise_rate", mGym+"");
                params.put("height", PersonalData.getInstance().getHeight()+"");
                params.put("weight", PersonalData.getInstance().getWeight()+"");
                params.put("neck", PersonalData.getInstance().getNeck_perimeter()+"");
                params.put("waist", PersonalData.getInstance().getWaist_perimeter()+"");
                params.put("thigh", PersonalData.getInstance().getThigh_perimeter()+"");
                params.put("weekly_goal", PersonalData.getInstance().getWeekly_reduce()+"");
                params.put("sport1_type", sport1_type);
                params.put("sport1_time", sport1_min);
                params.put("sport2_type", sport2_type);
                params.put("sport2_time", sport2_min);
                params.put("sport3_type", sport3_type);
                params.put("sport3_time", sport3_min);

                return params;
            }
        };
        queue = Volley.newRequestQueue(SportActivity.this);
        queue.add(postRequest);
    }

    public String getCurrentDate() {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        String formattedDate = df.format(c.getTime());
        return formattedDate;
    }
}