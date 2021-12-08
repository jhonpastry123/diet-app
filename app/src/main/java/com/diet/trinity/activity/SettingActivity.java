package com.diet.trinity.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.diet.trinity.MainApplication;
import com.diet.trinity.R;
import com.diet.trinity.data.api.REST;
import com.diet.trinity.data.models.Information;
import com.diet.trinity.data.models.Wrappers;
import com.diet.trinity.data.common.Goal;
import com.diet.trinity.data.common.PersonalData;
import com.warkiz.widget.IndicatorSeekBar;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;

public class SettingActivity extends AppCompatActivity {
    private CalendarView mCalendarView;
    TextView txtExpire;
    EditText tallEdit, weightEdit, birthEdit, neckEdit, waistEdit, thighEdit;
    RadioButton manBtn, womanBtn, ex0, ex3,ex5, ex7, exSports;
    RadioGroup settingR, genderR;
    IndicatorSeekBar seekBar;
    LinearLayout linSportContainer, layout1, layout2, layout3;
    ProgressDialog mProgressDialog;
    TextView txt1, txt2, txt3, txtGoal;
    Spinner dropdown1, dropdown2, dropdown3, meal_dropdown, goal_dropdown;

    int sport_type1 = 0, sport_type2 = 0, sport_type3 = 0, sport_time1 = 0, sport_time2 = 0, sport_time3 = 0;
    float coefficient1 = 0, coefficient2 = 0, coefficient3 = 0;

    final Calendar calendar = Calendar.getInstance();

    //------- sport ------//
    ArrayList<String> dropList = new ArrayList<String>();
    Map<String, String> element_data = new HashMap<String, String>();
    String Meal_choose="";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        initView();
        addEventListener();

        String myFormat = "yyyy-MM-dd"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        String current_date = sdf.format(calendar.getTime());
        show_date_setting(current_date);
    }

    private void initView() {
        txt1 = findViewById(R.id.min1);
        txt2 = findViewById(R.id.min2);
        txt3 = findViewById(R.id.min3);
        txtExpire = findViewById(R.id.txtExpire);
        txtGoal = findViewById(R.id.resText);

        meal_dropdown = findViewById(R.id.mealDropdown);
        goal_dropdown = findViewById(R.id.goalDropdown);
        dropdown1 = findViewById(R.id.sport1);
        dropdown2 = findViewById(R.id.sport2);
        dropdown3 = findViewById(R.id.sport3);

        layout1 = findViewById(R.id.layout1);
        layout2 = findViewById(R.id.layout2);
        layout3 = findViewById(R.id.layout3);

        tallEdit = findViewById(R.id.tallEdit);
        weightEdit = findViewById(R.id.weightEdit);
        birthEdit = findViewById(R.id.birthdayEdit);
        neckEdit = findViewById(R.id.neckEdit);
        waistEdit = findViewById(R.id.waistEdit);
        thighEdit = findViewById(R.id.thighEdit);
        manBtn = findViewById(R.id.genderMan);
        womanBtn = findViewById(R.id.genderWoman);
        settingR = findViewById(R.id.settingR);
        genderR = findViewById(R.id.genderradioG);
        ex0 = findViewById(R.id.ex0);
        ex3 = findViewById(R.id.ex3);
        ex5 = findViewById(R.id.ex5);
        ex7 = findViewById(R.id.ex7);
        exSports = findViewById(R.id.exSports);
        linSportContainer = findViewById(R.id.linSportContainer);
        seekBar = findViewById(R.id.seekWeeklyReduce);
    }

    private void addEventListener() {
        final TextView _count = findViewById(R.id.txtSelectedMealCount);

        findViewById(R.id.imgBack).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validate()){
                    String currentString = birthEdit.getText().toString();
                    String[] separated = currentString.split("/");
                    calendar.set(Integer.parseInt(separated[2]), Integer.parseInt(separated[1]), Integer.parseInt(separated[0]));
                    Date mBirthday = calendar.getTime();


                    PersonalData.getInstance().setHeight(Float.parseFloat(tallEdit.getText().toString()));
                    PersonalData.getInstance().setWeight(Float.parseFloat(weightEdit.getText().toString()));
                    PersonalData.getInstance().setBirthday(mBirthday);
                    PersonalData.getInstance().setNeck_perimeter(Float.parseFloat(neckEdit.getText().toString()));
                    PersonalData.getInstance().setWaist_perimeter(Float.parseFloat(waistEdit.getText().toString()));
                    PersonalData.getInstance().setThigh_perimeter(Float.parseFloat(thighEdit.getText().toString()));

                    Intent intent = new Intent(SettingActivity.this, DailyCaleandarActivity.class);
                    startActivity(intent);
                }

            }
        });

        meal_dropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedItemText = (String) parent.getItemAtPosition(position);
                Meal_choose = selectedItemText;
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        goal_dropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                PersonalData.getInstance().setGoal(Goal.values()[position]);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void show_date_setting(String date) {
        mProgressDialog = new ProgressDialog(SettingActivity.this);
        mProgressDialog.setTitle("Loading...");
        mProgressDialog.setIndeterminate(false);
        mProgressDialog.show();

        REST rest = MainApplication.getContainer().get(REST.class);
        rest.getInformation(date)
                .enqueue(new Callback<Wrappers.Single<Information>>() {
                    @Override
                    public void onResponse(Call<Wrappers.Single<Information>> call, retrofit2.Response<Wrappers.Single<Information>> response) {
                        Information information = response.body().data;

                        if (information != null) {
                            Log.e("Date", information.date);
                            mProgressDialog.dismiss();
                        }
                        else {
                            Toast.makeText(SettingActivity.this, getResources().getString(R.string.offline_text), Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Wrappers.Single<Information>> call, Throwable t) {

                    }
                });
    }

    private boolean validate(){
        boolean validate = true;
        if(neckEdit.getText().toString().equals("")){
            neckEdit.setError("Εισαγάγετε τα δεδομένα.");
            validate = false;
        }
        if(waistEdit.getText().toString().equals("")){
            waistEdit.setError("Εισαγάγετε τα δεδομένα.");
            validate = false;
        }
        if(thighEdit.getText().toString().equals("")){
            thighEdit.setError("Εισαγάγετε τα δεδομένα.");
            validate = false;
        }
        return validate;
    }
}
