package com.diet.trinity.activity;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CalendarView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.diet.trinity.MainApplication;
import com.diet.trinity.R;
import com.diet.trinity.Utility.Global;
import com.diet.trinity.data.api.REST;
import com.diet.trinity.data.common.PersonalData;
import com.diet.trinity.data.models.Information;
import com.diet.trinity.data.models.Sport;
import com.diet.trinity.data.models.User;
import com.diet.trinity.data.models.Wrappers;
import com.warkiz.widget.IndicatorSeekBar;
import com.warkiz.widget.OnSeekChangeListener;
import com.warkiz.widget.SeekParams;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.view.View.VISIBLE;

public class SettingActivity extends AppCompatActivity {
    private CalendarView mCalendarView;

    TextView txtExpire;
    EditText tallEdit, weightEdit, birthEdit, neckEdit, waistEdit, thighEdit;
    RadioButton manBtn, womanBtn, ex0, ex3, ex5, ex7, exSports;
    RadioGroup settingR, genderR;
    IndicatorSeekBar seekBar;
    LinearLayout linSportContainer, layout1, layout2, layout3;
    ProgressDialog mProgressDialog;
    TextView txt1, txt2, txt3, txtGoal;
    Spinner dropdown1, dropdown2, dropdown3, meal_dropdown, goal_dropdown;

    int sport_type1 = 0, sport_type2 = 0, sport_type3 = 0, sport_time1 = 0, sport_time2 = 0, sport_time3 = 0;
    String sport_name1="", sport_name2="", sport_name3="";

    float mWeeklyReduce = 300;
    int user_id, gender = 0, gym_type = 0, goal=0;

    final Calendar calendar = Calendar.getInstance();

    //------- sport ------//
    Map<Integer, Double> element_data = new HashMap<Integer, Double>();
    Map<String, Integer> element_id_data = new HashMap<String, Integer>();

    ArrayList<String> dropList = new ArrayList<String>();
    String Meal_choose="";
    Date selected_date=calendar.getTime();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        initView();
        addEventListener();

        String current_date = new SimpleDateFormat("yyyy-MM-dd").format(selected_date);
        show_date_setting(current_date);

        set_expired_text();
        set_sport_list();
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

        mCalendarView = findViewById(R.id.calendarView);
        mCalendarView.setFocusedMonthDateColor(Color.MAGENTA);
    }
    private void addEventListener() {
        findViewById(R.id.imgBack).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validate()){
                    Date current_date = Calendar.getInstance().getTime();
                    if (new SimpleDateFormat("dd/MM/yyyy").format(selected_date).equals(new SimpleDateFormat("dd/MM/yyyy").format(current_date))) {
                        new AlertDialog.Builder(SettingActivity.this)
                                .setTitle("Title")
                                .setMessage("Do you really want to whatever?")
                                .setIcon(android.R.drawable.ic_dialog_alert)
                                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                                    public void onClick(DialogInterface dialog, int whichButton) {
                                        if (validate()) {
                                            save_setting("back");
                                        }
                                    }})
                                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Intent intent = new Intent(SettingActivity.this, DailyCaleandarActivity.class);
                                        startActivity(intent);
                                        finish();
                                    }
                                }).show();
                    }
                    else {
                        Intent intent = new Intent(SettingActivity.this, DailyCaleandarActivity.class);
                        startActivity(intent);
                        finish();
                    }
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
                goal = position;
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        seekBar.setOnSeekChangeListener(new OnSeekChangeListener() {
            @Override
            public void onSeeking(SeekParams seekParams) {
                switch (seekParams.thumbPosition){
                    case 0:
                        mWeeklyReduce = 300;
                        break;
                    case 1:
                        mWeeklyReduce = 700;
                        break;
                    case 2:
                        mWeeklyReduce = 1100;
                        break;
                    case 3:
                        mWeeklyReduce = 1500;
                        break;
                }
            }

            @Override
            public void onStartTrackingTouch(IndicatorSeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(IndicatorSeekBar seekBar) {

            }
        });
        settingR.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(exSports.isChecked()){
                    linSportContainer.setVisibility(View.VISIBLE);
                    gym_type = 4;
                }
                else{
                    if (ex0.isChecked()) {
                        gym_type = 0;
                    } else if (ex3.isChecked()) {
                        gym_type = 1;
                    } else if (ex5.isChecked()) {
                        gym_type = 2;
                    } else if (ex7.isChecked()) {
                        gym_type = 3;
                    }
                    linSportContainer.setVisibility(View.GONE);
                }
            }
        });
        genderR.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(manBtn.isChecked()) {
                    gender = 0;
                } else if (womanBtn.isChecked()) {
                    gender = 1;
                }
            }
        });
        mCalendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(CalendarView CalendarView, int year, int month, int dayOfMonth) {
                Date current_date = Calendar.getInstance().getTime();
                String date = year + "-" + (month+1) + "-"+ dayOfMonth ;
                if (new SimpleDateFormat("dd/MM/yyyy").format(selected_date).equals(new SimpleDateFormat("dd/MM/yyyy").format(current_date))) {
                    new AlertDialog.Builder(SettingActivity.this)
                            .setTitle("Title")
                            .setMessage("Do you really want to whatever?")
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                                public void onClick(DialogInterface dialog, int whichButton) {
                                    if (validate()) {

                                        save_setting(date);
                                    }
                                }})
                            .setNegativeButton(android.R.string.no, null).show();
                } else {
                    show_date_setting(date);
                }
            }
        });
        DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, monthOfYear);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel();
            }

        };
        birthEdit.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                new DatePickerDialog(SettingActivity.this, date, calendar
                        .get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
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
        txt1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(sport_name1.length()>0  && Integer.parseInt(s.toString()) > 0)
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
                if(sport_name2.length()>0 && Integer.parseInt(s.toString()) > 0)
                {
                    layout3.setVisibility(VISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

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
                            tallEdit.setText((int) information.height + "");
                            weightEdit.setText((int) information.weight + "");

                            try {
                                Date birthday = new SimpleDateFormat("yyyy-MM-dd").parse(information.birthday);
                                calendar.set(Calendar.YEAR, birthday.getYear() + 1900);
                                calendar.set(Calendar.MONTH, birthday.getMonth());
                                calendar.set(Calendar.DAY_OF_MONTH, birthday.getDate());
                                birthEdit.setText(new SimpleDateFormat("dd/MM/yyyy").format(birthday));
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }

                            neckEdit.setText((int) information.neck + "");
                            waistEdit.setText((int) information.waist + "");
                            thighEdit.setText((int) information.thigh + "");
                            seekBar.setProgress(information.weekly_goal);
                            mWeeklyReduce = information.weekly_goal;
                            switch (information.gym_type) {
                                case 0:
                                    ex0.setChecked(true);
                                    break;
                                case 1:
                                    ex3.setChecked(true);
                                    break;
                                case 2:
                                    ex5.setChecked(true);
                                    break;
                                case 3:
                                    ex7.setChecked(true);
                                    break;
                                case 4:
                                    exSports.setChecked(true);
                                    linSportContainer.setVisibility(VISIBLE);
                                    dropdown1.setSelection(information.sport_type1);
                                    Log.e("time", information.sport_time1+"");
                                    txt1.setText(information.sport_time1+"");
                                    if (information.sport_time2 != 0) {
                                        layout2.setVisibility(VISIBLE);
                                        dropdown2.setSelection(information.sport_type2);
                                        txt2.setText(information.sport_time2+"");
                                    } else {
                                        layout2.setVisibility(View.GONE);
                                    }
                                    if (information.sport_time3 != 0) {
                                        layout3.setVisibility(VISIBLE);
                                        dropdown3.setSelection(information.sport_type3);
                                        txt3.setText(information.sport_time3+"");
                                    } else {
                                        layout3.setVisibility(View.GONE);
                                    }

                                    sport_type1 = information.sport_type1;
                                    sport_type2 = information.sport_type2;
                                    sport_type3 = information.sport_type3;
                                    sport_time1 = information.sport_time1;
                                    sport_time2 = information.sport_time2;
                                    sport_time3 = information.sport_time3;
                                    break;
                                default:
                                    break;
                            }
                            gym_type = information.gym_type;
                            if(information.gender == 0){
                                manBtn.setChecked(true);
                            }else {
                                womanBtn.setChecked(true);
                            }
                            gender = information.gender;
                            goal_dropdown.setSelection(information.goal);
                            meal_dropdown.setSelection(Global.meal_num);
                            try {
                                selected_date = new SimpleDateFormat("yyyy-MM-dd").parse(date);
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }

                            mProgressDialog.dismiss();
                        }
                        else {
                            Toast.makeText(SettingActivity.this, "This date does not exist. You were not registered at that time.", Toast.LENGTH_LONG).show();
                            mProgressDialog.dismiss();
                        }
                    }

                    @Override
                    public void onFailure(Call<Wrappers.Single<Information>> call, Throwable t) {

                    }
                });
    }

    private void save_setting(String action) {
        int iDietMode=0;
        float height = Float.parseFloat(tallEdit.getText().toString());
        float weight = Float.parseFloat(weightEdit.getText().toString());
        String birthday = new SimpleDateFormat("yyyy-MM-dd").format(calendar.getTime());
        float initial_weight = PersonalData.getInstance().getInitial_weight();

        float neck = Float.parseFloat(neckEdit.getText().toString());
        float waist = Float.parseFloat(waistEdit.getText().toString());
        float thigh = Float.parseFloat(thighEdit.getText().toString());

        float goal_weight = PersonalData.getInstance().getGoal_weight();
        float weekly_goal = mWeeklyReduce;

        if (gym_type == 4) {
            sport_time1 = Integer.parseInt(txt1.getText().toString());
            sport_time2 = Integer.parseInt(txt2.getText().toString());
            sport_time3 = Integer.parseInt(txt3.getText().toString());
        } else {
            sport_type1 = 0;
            sport_type2 = 0;
            sport_type3 = 0;
            sport_time1 = 0;
            sport_time2 = 0;
            sport_time3 = 0;
        }


        iDietMode = PersonalData.getInstance().getDietMode().ordinal();

        REST rest = MainApplication.getContainer().get(REST.class);
        rest.InformationStore(user_id, goal, initial_weight, weight, gender, height, birthday, gym_type, sport_type1, sport_type2, sport_type3, sport_time1, sport_time2, sport_time3, goal_weight, weekly_goal, iDietMode, neck, waist, thigh)
                .enqueue(new Callback<Boolean>() {
                    @Override
                    public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                        Boolean result = response.body();
                        Log.e("result", result + "");
                        if (result == true) {
                            if (action == "back") {
                                Intent intent = new Intent(SettingActivity.this, DailyCaleandarActivity.class);
                                startActivity(intent);
                                finish();
                            } else {
                                show_date_setting(action);
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<Boolean> call, Throwable t) {

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
    private void updateLabel() {
        String myFormat = "dd/MM/yyyy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        birthEdit.setText(sdf.format(calendar.getTime()));
    }
    private void set_expired_text() {
        REST rest = MainApplication.getContainer().get(REST.class);
        rest.profileShow()
                .enqueue(new Callback<Wrappers.Single<User>>() {

                    @Override
                    public void onResponse(Call<Wrappers.Single<User>> call, retrofit2.Response<Wrappers.Single<User>> response) {
                        User user = response.body().data;
                        Date date = null;
                        user_id = user.id;
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd").withZone(ZoneId.of("Europe/Paris"));;
                        try {
                            date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(user.purchase_time);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        if (user.type == 0) {
                            Instant instant = date.toInstant();
                            Instant nextDay = instant.plus(1, ChronoUnit.DAYS);
                            txtExpire.setText(formatter.format(nextDay));
                        } else {
                            Instant instant = date.toInstant();
                            Instant nextMonth = instant.plus(1, ChronoUnit.MONTHS);
                            txtExpire.setText(formatter.format(nextMonth));
                        }
                    }

                    @Override
                    public void onFailure(Call<Wrappers.Single<User>> call, Throwable t) {

                    }
                });
    }
    private void set_sport_list() {
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
    private void DropDown(String title, String id) {
        Spinner dropdown1 = findViewById(R.id.sport1);
        Spinner dropdown2 = findViewById(R.id.sport2);
        Spinner dropdown3 = findViewById(R.id.sport3);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.spinner_dropdown_item, dropList);
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        dropdown1.setAdapter(adapter);
        dropdown2.setAdapter(adapter);
        dropdown3.setAdapter(adapter);
    }
}
