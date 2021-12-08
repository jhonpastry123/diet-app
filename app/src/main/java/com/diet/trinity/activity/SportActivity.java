package com.diet.trinity.activity;

import android.content.Intent;
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

import com.diet.trinity.MainApplication;
import com.diet.trinity.R;
import com.diet.trinity.data.api.REST;
import com.diet.trinity.data.models.Sport;
import com.diet.trinity.data.models.Wrappers;
import com.diet.trinity.data.common.PersonalData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;

import static android.view.View.GONE;
import static android.view.View.INVISIBLE;
import static android.view.View.OnClickListener;
import static android.view.View.VISIBLE;

public class SportActivity extends AppCompatActivity {
    RadioGroup _gym;
    int mGym = -1;
    String sport_name1, sport_name2, sport_name3;
    int sport_type1 = 0, sport_type2 = 0, sport_type3 = 0, sport_time1 = 0, sport_time2 = 0, sport_time3 = 0;
    float coefficient1 = 0, coefficient2 = 0, coefficient3 = 0;
    EditText txt1, txt2, txt3;
    LinearLayout layout2, layout3;
    Map<Integer, Double> element_data = new HashMap<Integer, Double>();
    Map<String, Integer> element_id_data = new HashMap<String, Integer>();

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
        REST rest = MainApplication.getContainer().get(REST.class);
        rest.SportsIndex()
                .enqueue(new Callback<Wrappers.Collection<Sport>>() {
                    @Override
                    public void onResponse(Call<Wrappers.Collection<Sport>> call, retrofit2.Response<Wrappers.Collection<Sport>> response) {
                        List<Sport> sports = response.body().data;
                        for (int i = 0; i < sports.size(); i ++) {
                            Sport sport = sports.get(i);
                            dropList.add(sport.name);
                            element_data.put(sport.id, sport.coefficient);
                            element_id_data.put(sport.name, sport.id);
                        }
                    }

                    @Override
                    public void onFailure(Call<Wrappers.Collection<Sport>> call, Throwable t) {

                    }
                });

        DropDown("Select Sports", "1");
    }

    private void addEventListener(){
        findViewById(R.id.imgNext).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mGym != -1) {
                    PersonalData.getInstance().setGymType(mGym);

                    if (mGym == 4) {
                        try {
                            sport_time1 = Integer.parseInt(txt1.getText().toString());
                            sport_time2 = Integer.parseInt(txt2.getText().toString());
                            sport_time3 = Integer.parseInt(txt3.getText().toString());
                            coefficient1 = element_data.get(sport_type1).floatValue();
                            coefficient2 = element_data.get(sport_type2).floatValue();
                            coefficient3 = element_data.get(sport_type3).floatValue();

                            float weight = PersonalData.getInstance().getWeight();
                            float total = weight * (coefficient1 * sport_time1 + coefficient2 * sport_time2 + coefficient3 * sport_time3);
                            PersonalData.getInstance().setTotal_exercise(total);

                        } catch (Exception e) {};
                    }

                    PersonalData.getInstance().setSportType1(sport_type1);
                    PersonalData.getInstance().setSportType2(sport_type2);
                    PersonalData.getInstance().setSportType3(sport_type3);
                    PersonalData.getInstance().setSportTime1(sport_time1);
                    PersonalData.getInstance().setSportTime2(sport_time2);
                    PersonalData.getInstance().setSportTime3(sport_time3);

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
                if(sport_name1.length()>0  && !(txt1.getText().toString().equals("0")) && txt1.getText().toString().length()>0)
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
                if(sport_name2.length()>0 && !(txt2.getText().toString().equals("0")) && txt2.getText().toString().length()>0)
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
                sport_name1 = selectedItemText;
                sport_type1 = position;
                if(sport_name1.length()>0  && !(txt1.getText().toString().equals("0")) && txt1.getText().toString().length()>0)
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
                sport_name2 = selectedItemText;
                sport_type2 = position;
                if(sport_name2.length()>0  && !(txt2.getText().toString().equals("0")) && txt2.getText().toString().length()>0)
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
                sport_name3 = selectedItemText;
                sport_type3 = position;
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }
}