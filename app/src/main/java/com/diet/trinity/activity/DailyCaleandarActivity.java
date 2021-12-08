package com.diet.trinity.activity;

import android.Manifest;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.diet.trinity.Adapter.CustomMealAdapter;
import com.diet.trinity.MainApplication;
import com.diet.trinity.MyNotificationPublisher;
import com.diet.trinity.R;
import com.diet.trinity.Utility.BreakfastDatabaseHelper;
import com.diet.trinity.Utility.Common;
import com.diet.trinity.Utility.DatabaseHelper;
import com.diet.trinity.Utility.DinnerDatabaseHelper;
import com.diet.trinity.Utility.Global;
import com.diet.trinity.Utility.LunchDatabaseHelper;
import com.diet.trinity.Utility.MealChooseHelper;
import com.diet.trinity.Utility.MealDatabaseHelper;
import com.diet.trinity.Utility.PersonalDatabaseHelper;
import com.diet.trinity.data.api.REST;
import com.diet.trinity.data.models.Information;
import com.diet.trinity.data.models.User;
import com.diet.trinity.data.models.Wrappers;
import com.diet.trinity.model.DietMode;
import com.diet.trinity.model.Gender;
import com.diet.trinity.model.Goal;
import com.diet.trinity.model.PersonalData;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.ViewPortHandler;
import com.itextpdf.text.Document;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.jjoe64.graphview.DefaultLabelFormatter;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.Viewport;
import com.jjoe64.graphview.helper.DateAsXAxisLabelFormatter;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.pixplicity.easyprefs.library.Prefs;

import org.joda.time.DateTime;
import org.joda.time.Weeks;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;

//--------chart------------//
//-------pie chart---------//

public class DailyCaleandarActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener, View.OnClickListener {
    public static final String NOTIFICATION_CHANNEL_ID = "10001" ;
    private final static String default_notification_channel_id = "default" ;
    private static DailyCaleandarActivity instance;
    TextView _date, _week, _currentWeight, deltaUnits, meat_txt, oily_txt, legume_txt, pasta_txt, breakfast_requirement, lunch_requirement, dinner_requirement;
    LinearLayout _point, _unit;
    ImageView _pointBalerina, _unitBalerina,
            water1, water2, water3, water4, water5, water6, water7, water8,
            fruit1, fruit2, fruit3,
            meat_img, oily_img, legume_img, pasta_img,
            imgBreakfast, imgBreakfastSnack, imgLunch, imgLunchSnack, imgDinner, imgDinnerSnack;

    String [] timings = {"breakfast", "snack_breakfast", "lunch", "snack_lunch", "dinner", "snack_dinner"};
    String [] timings_title = {"Proino", "Dekatiano", "Mesimeriano", "Apogeymatino", "Vradino", "Proypnou"};
    Spinner type_dropdown;
    Date mSelectedDate;
    Date[] weekly_dates;
    int year_num = 0, month_num = 0;
    Calendar mCalendar;
    private SQLiteDatabase db, db1;
    private SQLiteOpenHelper openHelper, openHelper1;

    float y=0;

    private LineGraphSeries<DataPoint> series;

    Float nextUnits = 0f;
    Float nextPoints = 0f;

    //-------pie chart---------//
    PieChart pieChart;
    PieData pieData;
    PieDataSet pieDataSet;
    ArrayList pieEntries;

    //------ timer --------//
    boolean breakfast_start = false;
    boolean lunch_start = false;
    boolean dinner_start = false;
    boolean breakfast_snack_start = false;
    boolean lunch_snack_start = false;
    boolean dinner_snack_start = false;

    //------lunch------//
    int meat_num = 0;
    int oily_num = 0;
    int legumes_num = 0;
    int pasta_num = 0;
    int fruit_num = 0;
    int num = 0;

    //------graph------//
    String dates_s, d1_s, d2_s, d3_s, d4_s, start_date, dirpath;
    GraphView graph;
    float d1_y=0, d2_y=0, d3_y=0, d4_y=0;

    EditText edtWaist, edtNeck, edtThigh;
    Button btnSave;
    TextView txtResult;


    public static DailyCaleandarActivity getInstance(){
        return  instance;
    }

    @Override
    public void onBackPressed() {
        Prefs.clear();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daily_caleandar);
        setSettings();
        getProfile();
        get_user_id();

        mCalendar = Calendar.getInstance();
        Date date = mCalendar.getTime();
        year_num = date.getYear();
        month_num = date.getMonth();

        deltaUnits = findViewById(R.id.deltaUnits);

        meat_txt = findViewById(R.id.meat_amount);
        oily_txt = findViewById(R.id.milk_amount);
        legume_txt = findViewById(R.id.bean_amount);
        pasta_txt = findViewById(R.id.noodle_amount);

        meat_img = findViewById(R.id.meat_warning);
        oily_img = findViewById(R.id.milk_warning);
        legume_img = findViewById(R.id.bean_warning);
        pasta_img = findViewById(R.id.noodle_warning);

        ////Updated for setting
        edtWaist = (EditText)findViewById(R.id.edtWaist);
        edtNeck = (EditText)findViewById(R.id.edtNeck);
        edtThigh = (EditText)findViewById(R.id.edtThigh);
        btnSave = (Button)findViewById(R.id.btnSave);
        btnSave.setOnClickListener(this);
        txtResult = (TextView)findViewById(R.id.txtResult);
        breakfast_requirement = (TextView)findViewById(R.id.txtRequirementBreakfast);
        lunch_requirement = (TextView)findViewById(R.id.txtRequirementLunch);
        dinner_requirement = (TextView)findViewById(R.id.txtRequirementDinner);
        //////

        ////// Dropdown format
        type_dropdown = (Spinner) findViewById(R.id.typeDropdown);

        ActivityCompat.requestPermissions(DailyCaleandarActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, PackageManager.PERMISSION_GRANTED);

        addEventListener();

        //----- timer ----------//
        SetTimer();

        if (Global.meal_num == 1) {
            try {
                Load_breakfast();
            } catch (ParseException e) {
                e.printStackTrace();
            }

            show_breakfast();
        }
        else if (Global.meal_num == 2) {
            try {
                Load_lunch();
            } catch (ParseException e) {
                e.printStackTrace();
            }

            show_lunch();
        }

        else if (Global.meal_num == 3) {
            try {
                Load_dinner();
            } catch (ParseException e) {
                e.printStackTrace();
            }

            show_dinner();
        }

        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        mSelectedDate =  mCalendar.getTime();
        weekly_dates = new Date[1];
        weekly_dates[0] = mSelectedDate;

        scheduleNotification(getNotification( "ΠΡΩΙΝΟ ΓΕΥΜΑ","το πρωινό είναι το πιο σημαντικό γεύμα της ημέρας" ) , 9 );
        scheduleNotification(getNotification( "μεσημεριανό","για μια πιο υγιεινή ζωή μειώστε το αλάτι και τη ζάχαρη στα γεύματά σας" ) , 12 );
        scheduleNotification(getNotification( "ΩΡΑ ΓΙΑ ΝΕΡΟ!","Παρακαλώ, πιείτε νερό!" ) , 13 );
        scheduleNotification(getNotification( "ΔΕΙΠΝΟ","Δύο ώρες πριν από τον ύπνο δεν τρώμε μεγάλα γεύματα!" ) , 20 );

        //---------pie chart--------//
        pieChartFormat();
        instance = this;
    }

    private void getProfile() {
        REST rest = MainApplication.getContainer().get(REST.class);
        rest.profileShow()
                .enqueue(new Callback<Wrappers.Single<User>>() {

                    @Override
                    public void onResponse(Call<Wrappers.Single<User>> call, retrofit2.Response<Wrappers.Single<User>> response) {
                        User user = response.body().data;
                        Log.e( "user email", user.email + "");
                        PersonalData.getInstance().setMembership(user.type);
                    }

                    @Override
                    public void onFailure(Call<Wrappers.Single<User>> call, Throwable t) {

                    }
                });
    }

    void pieChartFormat() {
        pieChart = findViewById(R.id.pieChart);
        if((Global.morning_protein+Global.lunch_protein+Global.dinner_protein)==0 && (Global.morning_fat+Global.lunch_fat+Global.dinner_fat)==0 && (Global.morning_carbon+Global.lunch_carbon+Global.dinner_carbon)==0)
            pieChart.setVisibility(View.GONE);
        else
            pieChart.setVisibility(View.VISIBLE);

        getEntries();
        pieDataSet = new PieDataSet(pieEntries, "");
        pieData = new PieData(pieDataSet);
        pieChart.setData(pieData);
        pieDataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        pieDataSet.setSliceSpace(2f);
        pieDataSet.setValueTextColor(Color.BLACK);
        pieDataSet.setValueTextSize(15f);
        pieDataSet.setSliceSpace(5f);
        pieChart.setDrawHoleEnabled(false);
        pieChart.setEntryLabelColor(Color.BLACK);
        pieChart.setHoleRadius(0);
        pieDataSet.setXValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);
        IValueFormatter formatter = new IValueFormatter() {
            @Override
            public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
                return Math.round(value) + "";
            }
        };
        pieDataSet.setValueFormatter(formatter);
    }

    void SetTimer(){
        SQLiteDatabase db_meal;
        SQLiteOpenHelper openHelper_meal;
        openHelper_meal = new MealChooseHelper(this);
        db_meal = openHelper_meal.getWritableDatabase();
        final Cursor cursor_meal = db_meal.rawQuery("SELECT *FROM " + MealChooseHelper.TABLE_NAME,  null);
        String meal="";
        if(cursor_meal != null){
            if (cursor_meal.moveToFirst()){
                do{
                    meal = (cursor_meal.getString(cursor_meal.getColumnIndex(MealChooseHelper.COL_2)));
                }while(cursor_meal.moveToNext());
            }
        }

        if(meal.equals("ΠΡΩΙΝΟ"))
            Global.meal_num = 1;
        else if(meal.equals("ΜΕΣΗΜΕΡΙΑΝΟ"))
            Global.meal_num = 2;
        else if(meal.equals("ΒΡΑΔΙΝΟ"))
            Global.meal_num = 3;

        TextView breakfast_main = findViewById(R.id.txtTimerBreakFast);
        TextView lunch_main = findViewById(R.id.txtTimerLunch);
        TextView dinner_main = findViewById(R.id.txtTimerDinner);
        TextView breakfast_snack = findViewById(R.id.txtTimerSnackBreakfast);
        TextView lunch_snack = findViewById(R.id.txtTimerSnackLunch);
        TextView dinner_snack = findViewById(R.id.txtTimerSnackDinner);
        if(PersonalData.getInstance().getGoal() == Goal.LOSE)
        {
            if (breakfast_start == false) {
                breakfast_main.setText("10:00");
            }
            if (lunch_start == false) {
                lunch_main.setText("10:00");
            }
            if (dinner_start == false) {
                dinner_main.setText("10:00");
            }
            if (breakfast_snack_start == false) {
                breakfast_snack.setText("05:00");
            }
            if (lunch_snack_start == false) {
                lunch_snack.setText("05:00");
            }
            if (dinner_snack_start == false) {
                dinner_snack.setText("05:00");
            }

            if(meal.equals("ΠΡΩΙΝΟ") && breakfast_start == false)
                breakfast_main.setText("15:00");
            else if(meal.equals("ΜΕΣΗΜΕΡΙΑΝΟ") && lunch_start == false)
                lunch_main.setText("15:00");
            else if(meal.equals("ΒΡΑΔΙΝΟ") && dinner_start == false)
                dinner_main.setText("15:00");
        }
        else if(PersonalData.getInstance().getGoal() == Goal.GAIN)
        {
            if (breakfast_start == false) {
                breakfast_main.setText("20:00");
            }
            if (lunch_start == false) {
                lunch_main.setText("30:00");
            }
            if (dinner_start == false) {
                dinner_main.setText("20:00");
            }
            if (breakfast_snack_start == false) {
                breakfast_snack.setText("13:00");
            }
            if (lunch_snack_start == false) {
                lunch_snack.setText("13:00");
            }
            if (dinner_snack_start == false) {
                dinner_snack.setText("13:00");
            }
        }
        else
        {
            if (breakfast_start == false) {
                breakfast_main.setText("10:00");
            }
            if (lunch_start == false) {
                lunch_main.setText("10:00");
            }
            if (dinner_start == false) {
                dinner_main.setText("10:00");
            }
            if (breakfast_snack_start == false) {
                breakfast_snack.setText("05:00");
            }
            if (lunch_snack_start == false) {
                lunch_snack.setText("05:00");
            }
            if (dinner_snack_start == false) {
                dinner_snack.setText("05:00");
            }

            if(meal.equals("ΠΡΩΙΝΟ") && breakfast_start == false)
                breakfast_main.setText("15:00");
            else if(meal.equals("ΜΕΣΗΜΕΡΙΑΝΟ") && lunch_start == false)
                lunch_main.setText("15:00");
            else if(meal.equals("ΒΡΑΔΙΝΟ") && dinner_start == false)
                dinner_main.setText("15:00");
        }
    }

    public void get_user_id() {
        openHelper1 = new DatabaseHelper(this);
        db1 = openHelper1.getWritableDatabase();

        final Cursor cursor = db1.rawQuery("SELECT *FROM " + DatabaseHelper.TABLE_NAME, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    Global.user_id = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COL_4));
                    Global.token = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COL_5));
                } while (cursor.moveToNext());
            }
            cursor.close();
        }
    }

    //------pie chart function-------//
    private void getEntries() {
        pieEntries = new ArrayList<>();
        float total = Global.morning_total + Global.lunch_total + Global.dinner_total + Global.snack_morning_total + Global.snack_lunch_total + Global.snack_dinner_total;
        pieEntries.add(new PieEntry((Global.morning_protein+Global.lunch_protein+Global.dinner_protein+Global.snack_morning_protein+Global.snack_lunch_protein+Global.snack_dinner_protein) * 4/ total * 100, "Πρω"));
        pieEntries.add(new PieEntry((Global.morning_fat+Global.lunch_fat+Global.dinner_fat+Global.snack_morning_fat+Global.snack_lunch_fat+Global.snack_dinner_fat) * 9 / total * 100, "Λιπ"));
        pieEntries.add(new PieEntry((Global.morning_carbon+Global.lunch_carbon+Global.dinner_carbon+Global.snack_morning_carbon+Global.snack_lunch_carbon+Global.snack_dinner_carbon) * 4/ total * 100, "Υδατ"));
    }
    //-------end pie chart----------//

    public void initView() throws ParseException {
        _date = findViewById(R.id.txtDate);
        _week = findViewById(R.id.txtWeek);

        TextView _pointValue = findViewById(R.id.txtPoint);
        TextView _unitValue = findViewById(R.id.txtUnit);
        TextView txtGoalWeight = findViewById(R.id.txtGoalWeight);

        txtGoalWeight.setText(PersonalData.getInstance().getGoal_weight()+"");
        _unitValue.setText(String.valueOf(PersonalData.getInstance().getUnits()));
        _pointValue.setText(String.valueOf(PersonalData.getInstance().getPoints()));

        TextView _BFP = findViewById(R.id.txtBFP);
        TextView _IDW = findViewById(R.id.txtIDW);
        TextView _BMI = findViewById(R.id.txtBMI);
        TextView _BMIText = findViewById(R.id.txtBMIText);
        float bfp = PersonalData.getInstance().getBFP();
        float idw = (float) (Math.pow(PersonalData.getInstance().getHeight(), 2) * 18.5 / 10000);
        float idw_max = (float) (Math.pow(PersonalData.getInstance().getHeight(), 2) * 24.9 / 10000);
        float bmi = PersonalData.getInstance().getBMI().getValue();
        String bmiState = PersonalData.getInstance().getBMI().getState();
        if(bfp < 0){
            _BFP.setText("0%");
            _BFP.setTextSize(18);
//            findViewById(R.id.fat1).setVisibility(View.VISIBLE);
            findViewById(R.id.fat).setVisibility(View.GONE);
        }else {
//            findViewById(R.id.fat1).setVisibility(View.GONE);

            findViewById(R.id.fat).setVisibility(View.VISIBLE);
            txtResult.setText(String.format(Locale.US,"%d", Math.round(bfp)) + " %");
            _BFP.setText(String.format(Locale.US,"%d", Math.round(bfp)) + " %");
        }
        _IDW.setText(String.format(Locale.US, "%.1f ~ %.1f kg", idw, idw_max));
        _BMI.setText(String.format(Locale.US, "%.1f", bmi));
        _BMIText.setText(bmiState);

        final TextView appleT = findViewById(R.id.apple_txt);
        final TextView water_L = findViewById(R.id.water_L);
        _currentWeight = findViewById(R.id.txtCurrentWeight);
        _currentWeight.setText(String.format(Locale.US, "%.1f", PersonalData.getInstance().getWeight()));

        TextView _initial = findViewById(R.id.txtInitialWeight);
        TextView _ideal = findViewById(R.id.txtIdealWeight);
        TextView _today = findViewById(R.id.txtTodayWeight);
        TextView _diff = findViewById(R.id.txtWeightDiff);
        _initial.setText(String.format(Locale.US, "%.1f kg", PersonalData.getInstance().getInitial_weight()));
        _ideal.setText(String.format(Locale.US, "%.1f kg", PersonalData.getInstance().getIdeal_weight()));
        _today.setText(String.format(Locale.US, "%.1f kg", PersonalData.getInstance().getWeight()));
        _diff.setText(String.format(Locale.US, "%.1f kg", PersonalData.getInstance().getWeight() - PersonalData.getInstance().getInitial_weight()));

        if(PersonalData.getInstance().getDietMode() == DietMode.POINT){
            _point.performClick();
        }
        else{
            _unit.performClick();
        }

        fruit1 = findViewById(R.id.fruit1);
        fruit2 = findViewById(R.id.fruit2);
        fruit3 = findViewById(R.id.fruit3);

        fruit1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                appleT.setText("1 φ.");
                fruit2.setImageResource(R.drawable.ic_apple_plus);
                fruit3.setImageResource(R.drawable.ic_apple_idle);
                fruit1.setImageResource(R.drawable.ic_apple_eaten);

            }
        });

        fruit2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                appleT.setText("2 φ.");
                fruit2.setImageResource(R.drawable.ic_apple_eaten);
                fruit3.setImageResource(R.drawable.ic_apple_plus);
                fruit1.setImageResource(R.drawable.ic_apple_eaten);
            }
        });

        fruit3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                appleT.setText("3 φ.");
                fruit3.setImageResource(R.drawable.ic_apple_eaten);
                fruit2.setImageResource(R.drawable.ic_apple_eaten);
                fruit1.setImageResource(R.drawable.ic_apple_eaten);
            }
        });

        water1 = findViewById(R.id.water1);
        water2 = findViewById(R.id.water2);
        water3 = findViewById(R.id.water3);
        water4 = findViewById(R.id.water4);
        water5 = findViewById(R.id.water5);
        water6 = findViewById(R.id.water6);
        water7 = findViewById(R.id.water7);
        water8 = findViewById(R.id.water8);

        water1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                water1.setImageResource(R.drawable.ic_water_cup_full);
                water2.setImageResource(R.drawable.ic_water_cup_empty_plus);
                water3.setImageResource(R.drawable.ic_water_cup_empty);
                water4.setImageResource(R.drawable.ic_water_cup_empty);
                water5.setImageResource(R.drawable.ic_water_cup_empty);
                water6.setImageResource(R.drawable.ic_water_cup_empty);
                water7.setImageResource(R.drawable.ic_water_cup_empty);
                water8.setImageResource(R.drawable.ic_water_cup_empty);
                water_L.setText("0.25 L");
            }
        });

        water2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                water1.setImageResource(R.drawable.ic_water_cup_full);
                water2.setImageResource(R.drawable.ic_water_cup_full);
                water3.setImageResource(R.drawable.ic_water_cup_empty_plus);
                water4.setImageResource(R.drawable.ic_water_cup_empty);
                water5.setImageResource(R.drawable.ic_water_cup_empty);
                water6.setImageResource(R.drawable.ic_water_cup_empty);
                water7.setImageResource(R.drawable.ic_water_cup_empty);
                water8.setImageResource(R.drawable.ic_water_cup_empty);
                water_L.setText("0.5 L");
            }
        });

        water3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                water1.setImageResource(R.drawable.ic_water_cup_full);
                water2.setImageResource(R.drawable.ic_water_cup_full);
                water3.setImageResource(R.drawable.ic_water_cup_full);
                water4.setImageResource(R.drawable.ic_water_cup_empty_plus);
                water5.setImageResource(R.drawable.ic_water_cup_empty);
                water6.setImageResource(R.drawable.ic_water_cup_empty);
                water7.setImageResource(R.drawable.ic_water_cup_empty);
                water8.setImageResource(R.drawable.ic_water_cup_empty);
                water_L.setText("0.75 L");
            }
        });

        water4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                water1.setImageResource(R.drawable.ic_water_cup_full);
                water2.setImageResource(R.drawable.ic_water_cup_full);
                water3.setImageResource(R.drawable.ic_water_cup_full);
                water4.setImageResource(R.drawable.ic_water_cup_full);
                water5.setImageResource(R.drawable.ic_water_cup_empty_plus);
                water6.setImageResource(R.drawable.ic_water_cup_empty);
                water7.setImageResource(R.drawable.ic_water_cup_empty);
                water8.setImageResource(R.drawable.ic_water_cup_empty);
                water_L.setText("1.0 L");
            }
        });

        water5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                water1.setImageResource(R.drawable.ic_water_cup_full);
                water2.setImageResource(R.drawable.ic_water_cup_full);
                water3.setImageResource(R.drawable.ic_water_cup_full);
                water4.setImageResource(R.drawable.ic_water_cup_full);
                water5.setImageResource(R.drawable.ic_water_cup_full);
                water6.setImageResource(R.drawable.ic_water_cup_empty_plus);
                water7.setImageResource(R.drawable.ic_water_cup_empty);
                water8.setImageResource(R.drawable.ic_water_cup_empty);
                water_L.setText("1.25 L");
            }
        });

        water6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                water1.setImageResource(R.drawable.ic_water_cup_full);
                water2.setImageResource(R.drawable.ic_water_cup_full);
                water3.setImageResource(R.drawable.ic_water_cup_full);
                water4.setImageResource(R.drawable.ic_water_cup_full);
                water5.setImageResource(R.drawable.ic_water_cup_full);
                water6.setImageResource(R.drawable.ic_water_cup_full);
                water7.setImageResource(R.drawable.ic_water_cup_empty_plus);
                water8.setImageResource(R.drawable.ic_water_cup_empty);
                water_L.setText("1.5 L");
            }
        });

        water7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                water1.setImageResource(R.drawable.ic_water_cup_full);
                water2.setImageResource(R.drawable.ic_water_cup_full);
                water3.setImageResource(R.drawable.ic_water_cup_full);
                water4.setImageResource(R.drawable.ic_water_cup_full);
                water5.setImageResource(R.drawable.ic_water_cup_full);
                water6.setImageResource(R.drawable.ic_water_cup_full);
                water7.setImageResource(R.drawable.ic_water_cup_full);
                water8.setImageResource(R.drawable.ic_water_cup_empty_plus);
                water_L.setText("1.75 L");
            }
        });

        water8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                water1.setImageResource(R.drawable.ic_water_cup_full);
                water2.setImageResource(R.drawable.ic_water_cup_full);
                water3.setImageResource(R.drawable.ic_water_cup_full);
                water4.setImageResource(R.drawable.ic_water_cup_full);
                water5.setImageResource(R.drawable.ic_water_cup_full);
                water6.setImageResource(R.drawable.ic_water_cup_full);
                water7.setImageResource(R.drawable.ic_water_cup_full);
                water8.setImageResource(R.drawable.ic_water_cup_full);
                water_L.setText("2.0 L");
            }
        });

        //-------timer-------------//

        imgBreakfast = findViewById(R.id.imgBreakfast);
        imgBreakfastSnack = findViewById(R.id.imgBreakfastSnack);
        imgLunch = findViewById(R.id.imgLunch);
        imgLunchSnack = findViewById(R.id.imgLunchSnack);
        imgDinner = findViewById(R.id.imgDinner);
        imgDinnerSnack = findViewById(R.id.imgDinnerSnack);

        imgBreakfast.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (breakfast_start == false) {
                    TextView breakfast_main = findViewById(R.id.txtTimerBreakFast);
                    startTimer(breakfast_main, findViewById(R.id.linCheckedBreakfast));
                    breakfast_start = true;
                }
            }
        });

        imgBreakfastSnack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (breakfast_snack_start == false) {
                    TextView breakfast_snack_main = findViewById(R.id.txtTimerSnackBreakfast);
                    startTimer(breakfast_snack_main, findViewById(R.id.linCheckedSnackBreakfast));
                    breakfast_snack_start = true;
                }
            }
        });

        imgLunch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (lunch_start == false) {
                    TextView lunch_main = findViewById(R.id.txtTimerLunch);
                    startTimer(lunch_main, findViewById(R.id.linCheckedLunch));
                    lunch_start = true;
                }
            }
        });

        imgLunchSnack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (lunch_snack_start == false) {
                    TextView lunch_snack_main = findViewById(R.id.txtTimerSnackLunch);
                    startTimer(lunch_snack_main, findViewById(R.id.linCheckedSnackLunch));
                    lunch_snack_start = true;
                }
            }
        });

        imgDinner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dinner_start == false) {
                    TextView dinner_main = findViewById(R.id.txtTimerDinner);
                    startTimer(dinner_main, findViewById(R.id.linCheckedDinner));
                    dinner_start = true;
                }
            }
        });

        imgDinnerSnack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dinner_snack_start == false) {
                    TextView dinner_snack_main = findViewById(R.id.txtTimerSnackDinner);
                    startTimer(dinner_snack_main, findViewById(R.id.linCheckedSnackDinner));
                    dinner_snack_start = true;
                }
            }
        });
        //--------chart------------//
        graph = (GraphView) findViewById(R.id.graph);
        series = new LineGraphSeries<DataPoint>();



        graph.getGridLabelRenderer().setNumHorizontalLabels(3);

        Calendar calendar = Calendar.getInstance();
        Date d1 = calendar.getTime();
        calendar.add(Calendar.DATE, 7);
        Date d2 = calendar.getTime();

        calendar.add(Calendar.DATE, 7);
        Date d3 = calendar.getTime();

        calendar.add(Calendar.DATE, 7);
        Date d4 = calendar.getTime();

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

        d1_s = dateFormat.format(d1);
        d2_s = dateFormat.format(d2);
        d3_s = dateFormat.format(d3);
        d4_s = dateFormat.format(d4);

        graphSeries();

        if(d2_y==0)
        series = new LineGraphSeries<>(new DataPoint[] {
                new DataPoint(d1, d1_y),
        });
        else if(d3_y==0)
        {
            series = new LineGraphSeries<>(new DataPoint[] {
                    new DataPoint(d1, d1_y),
                    new DataPoint(d2, d2_y),
            });
        }
        else if(d4_y==0)
        {
            series = new LineGraphSeries<>(new DataPoint[] {
                    new DataPoint(d1, d1_y),
                    new DataPoint(d2, d2_y),
                    new DataPoint(d3, d3_y),
            });
        }
        else if(d4_y>0)
        {

            series = new LineGraphSeries<>(new DataPoint[] {
                    new DataPoint(d1, d1_y),
                    new DataPoint(d2, d2_y),
                    new DataPoint(d3, d3_y),
                    new DataPoint(d4, d4_y),
            });
        }
            series.setColor(Color.CYAN);
            series.setDrawDataPoints(true);
            series.setDataPointsRadius(10);
            series.setThickness(8);
            if (series != null)
                graph.addSeries(series);


            // styling grid/labels

            graph.getGridLabelRenderer().reloadStyles();

//            graph.getGridLabelRenderer().setLabelsSpace(1);
            // customize a little bit viewport
            Viewport viewport = graph.getViewport();
            viewport.setYAxisBoundsManual(true);

            viewport.setXAxisBoundsManual(true);
            viewport.scrollToEnd();
            viewport.setMinY(PersonalData.getInstance().getIdeal_weight() - 20);
            viewport.setMaxY(PersonalData.getInstance().getIdeal_weight() + 20);

            viewport.setScrollable(true);
            viewport.setScalable(true);

        graph.addSeries(series);


        graph.getGridLabelRenderer().setLabelFormatter(new DateAsXAxisLabelFormatter(this));
        graph.getGridLabelRenderer().setLabelFormatter(new DefaultLabelFormatter(){
            @Override
            public String formatLabel(double value, boolean isValueX) {
                if (isValueX) {
                    double minX = isValueX ? mViewport.getMaxX(false) : mViewport.getMaxY(false);
                    double maxX = isValueX ? mViewport.getMinX(false) : mViewport.getMinY(false);
                    double diff = maxX - minX;

                    Date date = new Date((long) value);
                    String label = null;

                        label = DateFormat.format("MM/dd", date).toString();

                    return label;
                }
                return super.formatLabel(value, isValueX);
            }
        });
        graph.getGridLabelRenderer().setNumHorizontalLabels(4); // only 4 because of the space
        graph.getViewport().setMinX(d1.getTime());
        graph.getViewport().setMaxX(d4.getTime());
        graph.getViewport().setXAxisBoundsManual(true);
        graph.getGridLabelRenderer().setHumanRounding(false);

    }
    private void scheduleNotification (Notification notification , int time) {
        Intent notificationIntent = new Intent( this, MyNotificationPublisher. class ) ;
        notificationIntent.putExtra(MyNotificationPublisher. NOTIFICATION_ID , 1 ) ;
        notificationIntent.putExtra(MyNotificationPublisher. NOTIFICATION , notification) ;
        PendingIntent pendingIntent = PendingIntent. getBroadcast ( this, time , notificationIntent , PendingIntent. FLAG_UPDATE_CURRENT ) ;
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context. ALARM_SERVICE ) ;
        assert alarmManager != null;
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, time);
        calendar.set(Calendar.MINUTE, 00);
        if (System.currentTimeMillis() > calendar.getTimeInMillis()) {
            calendar.add(Calendar.DATE, 1);
        }

        alarmManager.setRepeating(AlarmManager. RTC , calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY , pendingIntent); ;
    }
    private Notification getNotification (String title, String content) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder( this, default_notification_channel_id ) ;
        builder.setContentTitle(title) ;
        builder.setContentText(content) ;
        builder.setSmallIcon(R.drawable.ic_logo ) ;
        builder.setAutoCancel( true ) ;
        builder.setChannelId( NOTIFICATION_CHANNEL_ID ) ;
        return builder.build() ;
    }

    private void addEventListener() {
        _point = findViewById(R.id.linPoint);
        _unit = findViewById(R.id.linUnit);
        _pointBalerina = findViewById(R.id.imgBalerinaPoint);
        _unitBalerina = findViewById(R.id.imgBalerinaUnit);
        if(PersonalData.getInstance().getMembership()!=1) {
            _point.setOnClickListener(this);
            _unit.setOnClickListener(this);
        }
        findViewById(R.id.imgCalendar).setOnClickListener(this);

        findViewById(R.id.linCheckedBreakfast).setOnClickListener(this);
        findViewById(R.id.linCheckedLunch).setOnClickListener(this);
        findViewById(R.id.linCheckedDinner).setOnClickListener(this);
        findViewById(R.id.linCheckedSnackBreakfast).setOnClickListener(this);
        findViewById(R.id.linCheckedSnackLunch).setOnClickListener(this);
        findViewById(R.id.linCheckedSnackDinner).setOnClickListener(this);

        findViewById(R.id.linAddBreakfast).setOnClickListener(this);
        findViewById(R.id.linAddLunch).setOnClickListener(this);
        findViewById(R.id.linAddDinner).setOnClickListener(this);
        findViewById(R.id.linAddSnackBreakfast).setOnClickListener(this);
        findViewById(R.id.linAddSnackLunch).setOnClickListener(this);
        findViewById(R.id.linAddSnackDinner).setOnClickListener(this);

        if (PersonalData.getInstance().getMembership() != 1) {
            findViewById(R.id.imgSubtractWeight).setOnClickListener(this);
            findViewById(R.id.imgAddWeight).setOnClickListener(this);
        }
        findViewById(R.id.imgMain).setOnClickListener(this);
        findViewById(R.id.imgRecipe).setOnClickListener(this);
        findViewById(R.id.imgSetting).setOnClickListener(this);
        findViewById(R.id.imgHelp).setOnClickListener(this);
        findViewById(R.id.imgPDF).setOnClickListener(this);

        type_dropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    weekly_dates = new Date[1];
                    weekly_dates[0] = mSelectedDate;
                }
                else {
                    weekly_dates = new Date[7];
                    Calendar cal = Calendar.getInstance();
                    cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
                    weekly_dates[0] = cal.getTime();
                    for (int i = 0; i <6; i++) {
                        cal.add(Calendar.DATE, 1);
                        weekly_dates[i+1] = cal.getTime();
                    }
                    cal.setTime(mSelectedDate);
                    cal.getFirstDayOfWeek();
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.linPoint:
                _unit.setAlpha(0.5f);
                _unitBalerina.setVisibility(View.INVISIBLE);

                PersonalData.getInstance().setDietMode(DietMode.POINT);
                initMeal_format();
                try {
                    initMeal();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                _point.setAlpha(1.0f);
                _pointBalerina.setVisibility(View.VISIBLE);
                deltaUnits.setText(String.format(Locale.US, "%.1f", nextPoints));
                break;
            case R.id.linUnit:
                _point.setAlpha(0.5f);
                _pointBalerina.setVisibility(View.INVISIBLE);

                PersonalData.getInstance().setDietMode(DietMode.UNIT);
                initMeal_format();
                try {
                    initMeal();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                _unit.setAlpha(1.0f);
                _unitBalerina.setVisibility(View.VISIBLE);
                deltaUnits.setText(String.format(Locale.US, "%.1f", nextUnits));
                break;
            case R.id.imgCalendar:
                showDatePickerDlg();
                break;
            case R.id.linCheckedBreakfast:
            {
                showMeal_List();
                Global.timing = "breakfast";
            }
                break;
            case R.id.linCheckedLunch:
            {
                showMeal_List();
                Global.timing = "lunch";
            }
            break;
            case R.id.linCheckedDinner:
            {
                showMeal_List();
                Global.timing = "dinner";
            }
            break;
            case R.id.linCheckedSnackBreakfast:
            {
                showMeal_List();
                Global.timing = "snack_breakfast";
            }
            break;
            case R.id.linCheckedSnackLunch:
            {
                showMeal_List();
                Global.timing = "snack_lunch";
            }
            break;
            case R.id.linCheckedSnackDinner:
            {
                showMeal_List();
                Global.timing = "snack_dinner";
            }
            break;
            case R.id.linAddBreakfast: {
                searchFood();
                Global.timing = "breakfast";
            }
                break;
            case R.id.linAddLunch: {
                searchFood();
                Global.timing = "lunch";
            }
                break;
            case R.id.linAddDinner:
            {
                searchFood();
                Global.timing = "dinner";
            }
                break;
            case R.id.linAddSnackBreakfast:
            {
                searchFood();
                Global.timing = "snack_breakfast";
            }
                break;
            case R.id.linAddSnackLunch:
            {
                searchFood();
                Global.timing = "snack_lunch";
            }
                break;
            case R.id.linAddSnackDinner:
            {
                searchFood();
                Global.timing = "snack_dinner";
            }
                break;
            case R.id.imgSubtractWeight:
                float weight = Float.parseFloat(_currentWeight.getText().toString());
                weight -= 0.1;
                _currentWeight.setText(String.format(Locale.US, "%.1f", weight));
                break;
            case R.id.imgAddWeight:
                weight = Float.parseFloat(_currentWeight.getText().toString());
                weight += 0.1;
                _currentWeight.setText(String.format("%.1f", weight));
                break;
            case R.id.imgMain:
                break;
            case R.id.imgRecipe:
                Intent intent = new Intent(getBaseContext(), RecipesActivity.class);
                startActivity(intent);
                break;
            case R.id.imgSetting:
                intent = new Intent(getBaseContext(), SettingActivity.class);
                startActivity(intent);
                break;
            case R.id.imgHelp:
                intent = new Intent(getBaseContext(), PolicyActivity.class);
                startActivity(intent);
                break;
            case R.id.imgPDF:
                for(int i=0; i<weekly_dates.length;i++) {
                    layoutToImage(weekly_dates[i]);
                }
                try {
                    imageToPDF();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                for(int i=0; i<weekly_dates.length;i++) {
                    deleteImage(weekly_dates[i]);
                }
                break;
            case R.id.btnSave:
                saveSetting();
                break;
            default:
                break;
        }
    }

    private void saveSetting() {
        String strDate = "";
        if(mSelectedDate != null){
            strDate = getDateString(mSelectedDate);
        }else{
            strDate = getCurrentDate();
        }

        final String strExcercise = String.valueOf(PersonalData.getInstance().getGymType());
        final String strHeight = String.valueOf(PersonalData.getInstance().getHeight());
        final String strWeight = String.valueOf(PersonalData.getInstance().getWeight());
        final String strWaist = edtWaist.getText().toString();
        final String strNeck = edtNeck.getText().toString();
        final String strThigh = edtThigh.getText().toString();
        final String strWeekly = String.valueOf(Math.round(PersonalData.getInstance().getWeekly_reduce()));

        if(strWaist.equals("")){
            Toast.makeText(this, "Input Waist", Toast.LENGTH_SHORT).show();
            return;
        }else if(strNeck.equals("")){
            Toast.makeText(this, "Input Neck", Toast.LENGTH_SHORT).show();
            return;
        }else if(strThigh.equals("")){
            Toast.makeText(this, "Input Thigh", Toast.LENGTH_SHORT).show();
            return;
        }else if(Global.user_id.equals("")){
            return;
        }
        PersonalData.getInstance().setWaist_perimeter(Float.parseFloat(strWaist));
        PersonalData.getInstance().setThigh_perimeter(Float.parseFloat(strThigh));
        PersonalData.getInstance().setNeck_perimeter(Float.parseFloat(strNeck));

        TextView _BFP = findViewById(R.id.txtBFP);
        float bfp = PersonalData.getInstance().getBFP();
        if(bfp < 0){
            _BFP.setText("0%");
            _BFP.setTextSize(18);
//            findViewById(R.id.fat1).setVisibility(View.VISIBLE);
            findViewById(R.id.fat).setVisibility(View.GONE);
        }else {
//            findViewById(R.id.fat1).setVisibility(View.GONE);

            findViewById(R.id.fat).setVisibility(View.VISIBLE);
            txtResult.setText(String.format(Locale.US,"%d", Math.round(bfp)) + " %");
            _BFP.setText(String.format(Locale.US,"%d", Math.round(bfp)) + " %");
        }

        RequestQueue queue;
        String settingUrl = Common.getInstance().getSettingUrl();
        final String access_token = Global.token;

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
                                Toast.makeText(DailyCaleandarActivity.this, getResources().getString(R.string.retry_info_login), Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(DailyCaleandarActivity.this, getResources().getString(R.string.offline_text), Toast.LENGTH_LONG).show();
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
                params.put("exercise_rate", strExcercise);
                params.put("height", strHeight);
                params.put("weight", strWeight);
                params.put("neck", strNeck);
                params.put("waist", strWaist);
                params.put("thigh", strThigh);
                params.put("weekly_goal", strWeekly);
                params.put("sport1_type", "0");
                params.put("sport1_time", "0");
                params.put("sport2_type", "0");
                params.put("sport2_time", "0");
                params.put("sport3_type", "0");
                params.put("sport3_time", "0");

                return params;
            }
        };
        queue = Volley.newRequestQueue(DailyCaleandarActivity.this);
        queue.add(postRequest);
    }


    private void showDatePickerDlg(){
        DatePickerDialog datePickerdlg = new DatePickerDialog(this, this,
                mCalendar.get(Calendar.YEAR),
                mCalendar.get(Calendar.MONTH),
                mCalendar.get(Calendar.DAY_OF_MONTH));
        datePickerdlg.show();
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        mCalendar.set(year, month, dayOfMonth);
        mSelectedDate =  mCalendar.getTime();
        _date.setText(getDateString(mSelectedDate));
        _week.setText(getWeekNumber(mSelectedDate));

        try {
            initView();
            initMeal();
            pieChartFormat();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private String getDateString(Date date){
        SimpleDateFormat formatter = new SimpleDateFormat("EEE, MMM dd");
        String dateString = formatter.format(date);

        DateTime today = new DateTime();
        DateTime tomorrow = today.plusDays(1);
        DateTime yesterday = today.minusDays(1);
        DateTime selected = new DateTime(date);

        if((selected.dayOfMonth().get() == today.dayOfMonth().get()) && (selected.monthOfYear().get() == today.monthOfYear().get()) && (selected.year().get() == today.year().get()))
            return "Σήμερα";
        if(selected.dayOfMonth().get() == yesterday.dayOfMonth().get() && selected.monthOfYear().get() == yesterday.monthOfYear().get() && selected.year().get() == yesterday.year().get())
            return "Εχθές";
        if(selected.dayOfMonth().get() == tomorrow.dayOfMonth().get() && selected.monthOfYear().get() == tomorrow.monthOfYear().get() && selected.year().get() == tomorrow.year().get())
            return "Αύριο";

        return dateString;
    }

    private String getWeekNumber(Date date){
        DateTime start = new DateTime(PersonalData.getInstance().getStart_date());
        DateTime end = new DateTime(date).plusDays(1);
        int weeknum = Weeks.weeksBetween(start, end).getWeeks();
        if(start.isAfter(end)){
            return "";
        }
        else{
            return "Εβδομάδα " + String.valueOf(weeknum + 1);
        }
    }

    private void showMeal_List(){
        Intent intent = new Intent(getBaseContext(), MealListActivity.class);
        intent.putExtra("yValue", y);
        startActivity(intent);
    }

    private void searchFood(){
        Intent intent = new Intent(getBaseContext(), SearchFoodActivity.class);
        intent.putExtra("yValue", y);
        startActivity(intent);
    }

    public String getCurrentDate() {
        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        String formattedDate = df.format(mCalendar.getTime());
        return formattedDate;
    }

    public String getDayString(Date date) {
        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        String formattedDate = df.format(date);
        return formattedDate;
    }

    public String getCurrentChartDate() {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("MM/dd");
        String formattedDate = df.format(c.getTime());
        return formattedDate;
    }

    public String getStringDate(Date dates) {
        SimpleDateFormat df = new SimpleDateFormat("MM/dd");
        String formattedDate = "";
        try {
            formattedDate = df.format(dates);
        }
        catch (Exception e)
        {

        }
        return formattedDate;
    }

    public void initMeal() throws ParseException {
        openHelper = new MealDatabaseHelper(this);
        db = openHelper.getWritableDatabase();
        final Cursor cursor = db.rawQuery("SELECT *FROM " + MealDatabaseHelper.TABLE_NAME,  null);
        initMeal_format();

        boolean bCarb = false;
        boolean bProtein = false;
        boolean bVitC = false;
        boolean lCarb = false;
        boolean lProtein = false;
        boolean lVitC = false;
        boolean dCarb = false;
        boolean dProtein = false;
        boolean dVitC = false;

        fruit_num = 0;
        if(cursor != null){
            if (cursor.moveToFirst()){
                do{
                    String date = cursor.getString(cursor.getColumnIndex("date"));

                    if(date.equals(getCurrentDate())) {

                        String foodname = cursor.getString(cursor.getColumnIndex("foodname"));
                        String points_m = cursor.getString(cursor.getColumnIndex("points"));
                        String units_m = cursor.getString(cursor.getColumnIndex("units"));
                        String gram_m = cursor.getString(cursor.getColumnIndex("gram"));
                        String timing = cursor.getString(cursor.getColumnIndex("timing"));

                        float carbon = Float.parseFloat(cursor.getString(cursor.getColumnIndex("carbon")));
                        float protein = Float.parseFloat(cursor.getString(cursor.getColumnIndex("protein")));
                        float fat = Float.parseFloat(cursor.getString(cursor.getColumnIndex("fat")));
                        float points = Float.parseFloat(points_m);
                        float units = Float.parseFloat(units_m);
                        float gram = carbon+protein+fat;

                        switch (timing){
                            case "breakfast" :
                            {
                                findViewById(R.id.linCheckedBreakfast).setVisibility(View.VISIBLE);
                                findViewById(R.id.txtRequirementBreakfast).setVisibility(View.VISIBLE);
                                findViewById(R.id.linAddBreakfast).setVisibility(View.GONE);

                                Global.morning_carbon += carbon;
                                Global.morning_protein += protein;
                                Global.morning_fat += fat;
                                Global.morning_points += points;
                                Global.morning_units += units;
                                Global.morning_total += carbon * 4 + protein * 4 + fat * 9;

                                TextView point_txt = findViewById(R.id.txtPointBreakfast);
                                if (PersonalData.getInstance().getDietMode() == DietMode.POINT) {
                                    point_txt.setText(String.format(Locale.US, "%.1f", Global.morning_points)+" points");
                                }
                                else {
                                    point_txt.setText(String.format(Locale.US, "%.1f", Global.morning_units)+" units");
                                }

                                point_txt = findViewById(R.id.txtBreakfastProtein);
                                point_txt.setText("Πρω : "+String.format("%d", Math.round(Global.morning_protein * 4/Global.morning_total*100))+"%");

                                point_txt = findViewById(R.id.txtBreakfastCarbon);
                                point_txt.setText("Υδατ : "+String.format("%d", Math.round(Global.morning_carbon * 4/Global.morning_total*100))+"%");

                                point_txt = findViewById(R.id.txtBreakfastFat);
                                point_txt.setText("Λιπ : "+String.format("%d", Math.round(Global.morning_fat * 9/Global.morning_total*100))+"%");

                                point_txt = findViewById(R.id.txtBreakfastProteinGram);
                                point_txt.setText("Πρω : "+String.format("%.1f", (Global.morning_protein))+"g");

                                point_txt = findViewById(R.id.txtBreakfastCarbonGram);
                                point_txt.setText("Υδατ : "+String.format("%.1f", (Global.morning_carbon))+"g");

                                point_txt = findViewById(R.id.txtBreakfastFatGram);
                                point_txt.setText("Λιπ : "+String.format("%.1f", (Global.morning_fat))+"g");

                                String categoryid = cursor.getString(cursor.getColumnIndex(MealDatabaseHelper.COL_8));
                                for(int i=0; i<categoryid.split(",").length;i++){
                                    if(categoryid.split(",")[i].equals("52"))
                                        bProtein = true;
                                    if(categoryid.split(",")[i].equals("53"))
                                        bCarb = true;
                                    if(categoryid.split(",")[i].equals("54"))
                                        bVitC = true;
                                    if(categoryid.split(",")[i].equals("10"))
                                        fruit_num++;
                                }

                                if (bCarb && bProtein && bVitC) {
                                    breakfast_requirement.setText("Μπράβο!  Έχετε πετύχει τον ιδανικό συνδυασμό Υδατ/Πρωτ/VitC στο γεύμα σας.");
                                }
                                else {
                                    breakfast_requirement.setText("Ο συνδυασμός τροφίμων δεν είναι καλός.  Συνδυάστε Υδατ/Πρωτ/VitC στο γεύμα σας.");
                                }
                            }
                            break;
                            case "lunch" :
                            {
                                findViewById(R.id.linCheckedLunch).setVisibility(View.VISIBLE);
                                findViewById(R.id.txtRequirementLunch).setVisibility(View.VISIBLE);
                                findViewById(R.id.linAddLunch).setVisibility(View.GONE);

                                Global.lunch_carbon += carbon;
                                Global.lunch_protein += protein;
                                Global.lunch_fat += fat;
                                Global.lunch_points += points;
                                Global.lunch_units += units;
                                Global.lunch_total += carbon * 4 + protein * 4 + fat * 9;

                                TextView point_txt = findViewById(R.id.txtPointLunch);
                                if (PersonalData.getInstance().getDietMode() == DietMode.POINT) {
                                    point_txt.setText(String.format(Locale.US, "%.1f", Global.lunch_points)+" points");
                                }
                                else {
                                    point_txt.setText(String.format(Locale.US, "%.1f", Global.lunch_units)+" units");
                                }

                                point_txt = findViewById(R.id.txtLunchProtein);
                                point_txt.setText("Πρω : "+String.format("%d", Math.round(Global.lunch_protein * 4/Global.lunch_total*100))+"%");

                                point_txt = findViewById(R.id.txtLunchCarbon);
                                point_txt.setText("Υδατ : "+String.format("%d", Math.round(Global.lunch_carbon * 4/Global.lunch_total*100))+"%");

                                point_txt = findViewById(R.id.txtLunchFat);
                                point_txt.setText("Λιπ : "+String.format("%d", Math.round(Global.lunch_fat * 9/Global.lunch_total*100))+"%");

                                point_txt = findViewById(R.id.txtLunchProteinGram);
                                point_txt.setText("Πρω : "+String.format("%.1f", Global.lunch_protein)+"g");

                                point_txt = findViewById(R.id.txtLunchCarbonGram);
                                point_txt.setText("Υδατ : "+String.format("%.1f", Global.lunch_carbon)+"g");

                                point_txt = findViewById(R.id.txtLunchFatGram);
                                point_txt.setText("Λιπ : "+String.format("%.1f", Global.lunch_fat)+"g");
                                String categoryid = cursor.getString(cursor.getColumnIndex(MealDatabaseHelper.COL_8));
                                for(int i=0; i<categoryid.split(",").length;i++){
                                    if(categoryid.split(",")[i].equals("52"))
                                        lProtein = true;
                                    if(categoryid.split(",")[i].equals("53"))
                                        lCarb = true;
                                    if(categoryid.split(",")[i].equals("54"))
                                        lVitC = true;
                                    if(categoryid.split(",")[i].equals("10"))
                                        fruit_num++;
                                }

                                if (lCarb && lProtein && lVitC) {
                                    lunch_requirement.setText("Μπράβο!  Έχετε πετύχει τον ιδανικό συνδυασμό Υδατ/Πρωτ/VitC στο γεύμα σας.");
                                }
                                else {
                                    lunch_requirement.setText("Ο συνδυασμός τροφίμων δεν είναι καλός.  Συνδυάστε Υδατ/Πρωτ/VitC στο γεύμα σας.");
                                }
                            }
                            break;
                            case "dinner" :
                            {
                                findViewById(R.id.linCheckedDinner).setVisibility(View.VISIBLE);
                                findViewById(R.id.txtRequirementDinner).setVisibility(View.VISIBLE);
                                findViewById(R.id.linAddDinner).setVisibility(View.GONE);

                                Global.dinner_carbon += carbon;
                                Global.dinner_protein += protein;
                                Global.dinner_fat += fat;
                                Global.dinner_points += points;
                                Global.dinner_units += units;
                                Global.dinner_total += carbon * 4 + protein * 4 + fat * 9;

                                TextView point_txt = findViewById(R.id.txtPointDinner);
                                if (PersonalData.getInstance().getDietMode() == DietMode.POINT) {
                                    point_txt.setText(String.format(Locale.US, "%.1f", Global.dinner_points)+" points");
                                }
                                else {
                                    point_txt.setText(String.format(Locale.US, "%.1f", Global.dinner_units)+" units");
                                }

                                point_txt = findViewById(R.id.txtDinnerProtein);
                                point_txt.setText("Πρω : "+String.format("%d", Math.round(Global.dinner_protein * 4/Global.dinner_total*100))+"%");

                                point_txt = findViewById(R.id.txtDinnerCarbon);
                                point_txt.setText("Υδατ : "+String.format("%d", Math.round(Global.dinner_carbon * 4/Global.dinner_total*100))+"%");

                                point_txt = findViewById(R.id.txtDinnerFat);
                                point_txt.setText("Λιπ : "+String.format("%d", Math.round(Global.dinner_fat * 9/Global.dinner_total*100))+"%");

                                point_txt = findViewById(R.id.txtDinnerProteinGram);
                                point_txt.setText("Πρω : "+String.format("%.1f", Global.dinner_protein)+"g");

                                point_txt = findViewById(R.id.txtDinnerCarbonGram);
                                point_txt.setText("Υδατ : "+String.format("%.1f", Global.dinner_carbon)+"g");

                                point_txt = findViewById(R.id.txtDinnerFatGram);
                                point_txt.setText("Λιπ : "+String.format("%.1f", Global.dinner_fat)+"g");
                                String categoryid = cursor.getString(cursor.getColumnIndex(MealDatabaseHelper.COL_8));
                                for(int i=0; i<categoryid.split(",").length;i++){
                                    if(categoryid.split(",")[i].equals("52"))
                                        dProtein = true;
                                    if(categoryid.split(",")[i].equals("53"))
                                        dCarb = true;
                                    if(categoryid.split(",")[i].equals("54"))
                                        dVitC = true;
                                    if(categoryid.split(",")[i].equals("10"))
                                        fruit_num++;
                                }

                                if (dCarb && dProtein && dVitC) {
                                    dinner_requirement.setText("Μπράβο!  Έχετε πετύχει τον ιδανικό συνδυασμό Υδατ/Πρωτ/VitC στο γεύμα σας.");
                                }
                                else {
                                    dinner_requirement.setText("Ο συνδυασμός τροφίμων δεν είναι καλός.  Συνδυάστε Υδατ/Πρωτ/VitC στο γεύμα σας.");
                                }
                            }
                            break;
                            case "snack_breakfast" :
                            {
                                findViewById(R.id.linCheckedSnackBreakfast).setVisibility(View.VISIBLE);
                                findViewById(R.id.txtRequirementDinner).setVisibility(View.VISIBLE);
                                findViewById(R.id.linAddSnackBreakfast).setVisibility(View.GONE);

                                Global.snack_morning_carbon += carbon;
                                Global.snack_morning_protein += protein;
                                Global.snack_morning_fat += fat;
                                Global.snack_morning_points += points;
                                Global.snack_morning_units += units;
                                Global.snack_morning_total += carbon * 4 + protein * 4 + fat * 9;

                                TextView point_txt = findViewById(R.id.txtPointSnackBreakfast);
                                if (PersonalData.getInstance().getDietMode() == DietMode.POINT) {
                                    point_txt.setText(String.format(Locale.US, "%.1f", Global.snack_morning_points)+" points");
                                }
                                else {
                                    point_txt.setText(String.format(Locale.US, "%.1f", Global.snack_morning_units)+" units");
                                }

                                point_txt = findViewById(R.id.txtSnackBreakfastProtein);
                                point_txt.setText("Πρω : "+String.format("%d", Math.round(Global.snack_morning_protein * 4/Global.snack_morning_total*100))+"%");

                                point_txt = findViewById(R.id.txtSnackBreakfastCarbon);
                                point_txt.setText("Υδατ : "+String.format("%d", Math.round(Global.snack_morning_carbon * 4/Global.snack_morning_total*100))+"%");

                                point_txt = findViewById(R.id.txtSnackBreakfastFat);
                                point_txt.setText("Λιπ : "+String.format("%d", Math.round(Global.snack_morning_fat * 9/Global.snack_morning_total*100))+"%");

                                point_txt = findViewById(R.id.txtSnackBreakfastProteinGram);
                                point_txt.setText("Πρω : "+String.format("%.1f", Global.snack_morning_protein)+"g");

                                point_txt = findViewById(R.id.txtSnackBreakfastCarbonGram);
                                point_txt.setText("Υδατ : "+String.format("%.1f", Global.snack_morning_carbon)+"g");

                                point_txt = findViewById(R.id.txtSnackBreakfastFatGram);
                                point_txt.setText("Λιπ : "+String.format("%.1f", Global.snack_morning_fat)+"g");

                                String categoryid = cursor.getString(cursor.getColumnIndex(MealDatabaseHelper.COL_8));
                                for(int i=0; i<categoryid.split(",").length;i++){
                                    if(categoryid.split(",")[i].equals("10"))
                                        fruit_num++;
                                }
                            }
                            break;
                            case "snack_lunch" :
                            {
                                findViewById(R.id.linCheckedSnackLunch).setVisibility(View.VISIBLE);
                                findViewById(R.id.linAddSnackLunch).setVisibility(View.GONE);

                                Global.snack_lunch_carbon += carbon;
                                Global.snack_lunch_protein += protein;
                                Global.snack_lunch_fat += fat;
                                Global.snack_lunch_points += points;
                                Global.snack_lunch_units += units;
                                Global.snack_lunch_total += carbon * 4 + protein * 4 + fat * 9;

                                TextView point_txt = findViewById(R.id.txtPointSnackLunch);
                                if (PersonalData.getInstance().getDietMode() == DietMode.POINT) {
                                    point_txt.setText(String.format(Locale.US, "%.1f", Global.snack_lunch_points)+" points");
                                }
                                else {
                                    point_txt.setText(String.format(Locale.US, "%.1f", Global.snack_lunch_units)+" units");
                                }

                                point_txt = findViewById(R.id.txtSnackLunchProtein);
                                point_txt.setText("Πρω : "+String.format("%d", Math.round(Global.snack_lunch_protein * 4/Global.snack_lunch_total*100))+"%");

                                point_txt = findViewById(R.id.txtSnackLunchCarbon);
                                point_txt.setText("Υδατ : "+String.format("%d", Math.round(Global.snack_lunch_carbon * 4/Global.snack_lunch_total*100))+"%");

                                point_txt = findViewById(R.id.txtSnackLunchFat);
                                point_txt.setText("Λιπ : "+String.format("%d", Math.round(Global.snack_lunch_fat * 9/Global.snack_lunch_total*100))+"%");

                                point_txt = findViewById(R.id.txtSnackLunchProteinGram);
                                point_txt.setText("Πρω : "+String.format("%.1f", Global.snack_lunch_protein)+"g");

                                point_txt = findViewById(R.id.txtSnackLunchCarbonGram);
                                point_txt.setText("Υδατ : "+String.format("%.1f", Global.snack_lunch_carbon)+"g");

                                point_txt = findViewById(R.id.txtSnackLunchFatGram);
                                point_txt.setText("Λιπ : "+String.format("%.1f", Global.snack_lunch_fat)+"g");

                                String categoryid = cursor.getString(cursor.getColumnIndex(MealDatabaseHelper.COL_8));
                                for(int i=0; i<categoryid.split(",").length;i++){
                                    if(categoryid.split(",")[i].equals("10"))
                                        fruit_num++;
                                }
                            }
                            break;
                            case "snack_dinner" :
                            {
                                findViewById(R.id.linCheckedSnackDinner).setVisibility(View.VISIBLE);
                                findViewById(R.id.linAddSnackDinner).setVisibility(View.GONE);

                                Global.snack_dinner_carbon += carbon;
                                Global.snack_dinner_protein += protein;
                                Global.snack_dinner_fat += fat;
                                Global.snack_dinner_points += points;
                                Global.snack_dinner_units += units;
                                Global.snack_dinner_total += carbon * 4 + protein * 4 + fat * 9;

                                TextView point_txt = findViewById(R.id.txtPointSnackDinner);
                                if (PersonalData.getInstance().getDietMode() == DietMode.POINT) {
                                    point_txt.setText(String.format(Locale.US, "%.1f", Global.snack_dinner_points)+" points");
                                }
                                else {
                                    point_txt.setText(String.format(Locale.US, "%.1f", Global.snack_dinner_units)+" units");
                                }

                                point_txt = findViewById(R.id.txtSnackDinnerProtein);
                                point_txt.setText("Πρω : "+String.format("%d", Math.round(Global.snack_dinner_protein * 4/Global.snack_dinner_total*100))+"%");

                                point_txt = findViewById(R.id.txtSnackDinnerCarbon);
                                point_txt.setText("Υδατ : "+String.format("%d", Math.round(Global.snack_dinner_carbon * 4/Global.snack_dinner_total*100))+"%");

                                point_txt = findViewById(R.id.txtSnackDinnerFat);
                                point_txt.setText("Λιπ : "+String.format("%d", Math.round(Global.snack_dinner_fat * 9/Global.snack_dinner_total*100))+"%");

                                point_txt = findViewById(R.id.txtSnackDinnerProteinGram);
                                point_txt.setText("Πρω : "+String.format("%.1f", Global.snack_dinner_protein)+"g");

                                point_txt = findViewById(R.id.txtSnackDinnerCarbonGram);
                                point_txt.setText("Υδατ : "+String.format("%.1f", Global.snack_dinner_carbon)+"g");

                                point_txt = findViewById(R.id.txtSnackDinnerFatGram);
                                point_txt.setText("Λιπ : "+String.format("%.1f", Global.snack_dinner_fat)+"g");

                                String categoryid = cursor.getString(cursor.getColumnIndex(MealDatabaseHelper.COL_8));
                                for(int i=0; i<categoryid.split(",").length;i++){
                                    if(categoryid.split(",")[i].equals("10"))
                                        fruit_num++;
                                }
                            }
                            break;
                        }
                    }
                }while(cursor.moveToNext());
            }
            cursor.close();

            SQLiteDatabase db_personal;
            SQLiteOpenHelper openHelper_persoanl;

            openHelper_persoanl = new PersonalDatabaseHelper(this);
            db_personal = openHelper_persoanl.getWritableDatabase();

            String start_date = getCurrentDate();
            String weekly_units = PersonalData.getInstance().getUnits()+"";
            String weekly_points = PersonalData.getInstance().getPoints()+"";

            String date = getCurrentDate();

            Date any_date = getDate(getCurrentDate());
            Date st_date = getDate(getCurrentDate());

            SimpleDateFormat ddf = new SimpleDateFormat("dd/MM/yyyy");
            Date date_ = ddf.parse(date);
            Calendar yesterday = Calendar.getInstance();
            yesterday.setTime(date_);
            yesterday.add(Calendar.DATE, -1);
            String yesterday_s = ddf.format(yesterday.getTime());

            float weight_average = PersonalData.getInstance().getWeight();
            Integer weight_n = 0;

            final Cursor cursor1 = db_personal.rawQuery("SELECT *FROM " + PersonalDatabaseHelper.TABLE_NAME ,  null);
            if(cursor1 != null) {
                if (cursor1.moveToFirst()) {
                    do {
                        Integer id = cursor1.getInt(cursor1.getColumnIndex("ID"));

                        if (id == 1)
                            start_date = cursor1.getString(cursor1.getColumnIndex("date"));

                        Integer day_s = Integer.parseInt(cursor1.getString(cursor1.getColumnIndex("days")));

                        if (yesterday_s.equals(cursor1.getString(cursor1.getColumnIndex("date")))) {
                            if (cursor1.getFloat(cursor1.getColumnIndex("next_units")) < 0) {
                                weekly_units = "" + (Float.parseFloat(cursor1.getString(cursor1.getColumnIndex("weekly_units"))) + cursor1.getFloat(cursor1.getColumnIndex("next_units")));
                            }
                            else {
                                weekly_units = "" + (Float.parseFloat(cursor1.getString(cursor1.getColumnIndex("weekly_units"))));
                            }

                            if (cursor1.getInt(cursor1.getColumnIndex("next_points")) < 0) {
                                weekly_points = "" + (Float.parseFloat(cursor1.getString(cursor1.getColumnIndex("weekly_points"))) + cursor1.getInt(cursor1.getColumnIndex("next_points")));
                            }
                            else {
                                weekly_points = "" + (Float.parseFloat(cursor1.getString(cursor1.getColumnIndex("weekly_points"))));
                            }
                        }

                        if (day_s > 7) {
                            Integer n = (day_s / 7);
                            if (day_s > 7 * (n - 1) && day_s <= 7 * n) {
                                weight_n++;
                                weight_average += PersonalData.getInstance().getWeight();
                            }
                        }
                    } while (cursor1.moveToNext());
                }
                cursor1.close();

                if(weight_n>0)
                weight_average = weight_average / weight_n;
                st_date = getDate(start_date);

            }

            Float weight_f = PersonalData.getInstance().getWeight();
            Float height_f = PersonalData.getInstance().getHeight();
            Float waist_f = PersonalData.getInstance().getWaist_perimeter();
            Float neck_f = PersonalData.getInstance().getNeck_perimeter();
            Float thigh_f = PersonalData.getInstance().getThigh_perimeter();

            Float daily_units = 0f ;
            float daily_points = 0 ;

            if (PersonalData.getInstance().getDietMode() == DietMode.POINT) {
                daily_points = Global.morning_points + Global.lunch_points + Global.dinner_points + Global.snack_morning_points + Global.snack_lunch_points + Global.snack_dinner_points;
            }
            else {
                daily_units = Global.morning_units + Global.lunch_units + Global.dinner_units + Global.snack_morning_units + Global.snack_lunch_units + Global.snack_dinner_units;
            }

            String days = Math.round((any_date.getTime() - st_date.getTime())/(1000 * 60 * 60 * 24)) + "";

            if(Integer.parseInt(days)%7==0)
            {
                weekly_units = PersonalData.getInstance().getUnits()+"";
                weekly_points = PersonalData.getInstance().getPoints()+"";
            }

            Float next_units = Float.parseFloat(weekly_units)-daily_units;
            Float next_points = Float.parseFloat(weekly_points)-daily_points;

            insertPersonalData(weight_f+"", height_f+"", waist_f+"", neck_f+"", thigh_f+"", weekly_units, daily_units+"", date, days, next_units+"", Global.user_id, weekly_points, daily_points+"", next_points+"");

            TextView deltaWeight = findViewById(R.id.deltaWeight);

            TextView loseAlert = findViewById(R.id.loseAlert);

            float delta_weight = weight_f - weight_average;

            if(delta_weight<0) {
                deltaWeight.setText(delta_weight + "");
                loseAlert.setVisibility(View.VISIBLE);
            }
            else {
                deltaWeight.setText("--");
                loseAlert.setVisibility(View.INVISIBLE);
            }

            if (PersonalData.getInstance().getDietMode() == DietMode.POINT) {
                deltaUnits.setText(String.format(Locale.US, "%.1f", next_points));
            }
            else {
                deltaUnits.setText(String.format(Locale.US, "%.1f", next_units));
            }

            nextUnits = next_units;
            nextPoints = next_points;

            TextView _pointValue = findViewById(R.id.txtPoint);
            TextView _unitValue = findViewById(R.id.txtUnit);

            _unitValue.setText(String.format(Locale.US, "%.1f", Float.parseFloat(weekly_units)));
            _pointValue.setText(String.format(Locale.US, "%.1f", Float.parseFloat(weekly_points)));
        }

        fruit1 = findViewById(R.id.fruit1);
        fruit2 = findViewById(R.id.fruit2);
        fruit3 = findViewById(R.id.fruit3);
        final TextView appleT = findViewById(R.id.apple_txt);

        if(fruit_num == 1) {
            appleT.setText("1 φ.");
            fruit1.setImageResource(R.drawable.ic_apple_eaten);
            fruit2.setImageResource(R.drawable.ic_apple_plus);
            fruit3.setImageResource(R.drawable.ic_apple_idle);
        }

        else if (fruit_num == 2) {
            appleT.setText("2 φ.");
            fruit1.setImageResource(R.drawable.ic_apple_eaten);
            fruit2.setImageResource(R.drawable.ic_apple_eaten);
            fruit3.setImageResource(R.drawable.ic_apple_plus);
        }

        else if (fruit_num > 2) {
            appleT.setText("3 φ.");
            fruit1.setImageResource(R.drawable.ic_apple_eaten);
            fruit2.setImageResource(R.drawable.ic_apple_eaten);
            fruit3.setImageResource(R.drawable.ic_apple_eaten);
        }
        else if (fruit_num == 0) {
            appleT.setText("0 φ.");
            fruit1.setImageResource(R.drawable.ic_apple_plus);
            fruit2.setImageResource(R.drawable.ic_apple_idle);
            fruit3.setImageResource(R.drawable.ic_apple_idle);
        }
    }

    public void insertPersonalData(String weight, String height, String waist, String neck, String thigh, String weekly_units, String daily_units, String date, String days, String next_units, String user_id,String weekly_points,String daily_points,String next_points)
    {
        SQLiteDatabase db_personal;
        SQLiteOpenHelper openHelper_persoanl;
        openHelper_persoanl = new PersonalDatabaseHelper(this);
        db_personal = openHelper_persoanl.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(PersonalDatabaseHelper.COL_2, weight);
        contentValues.put(PersonalDatabaseHelper.COL_3, height);
        contentValues.put(PersonalDatabaseHelper.COL_4, waist);
        contentValues.put(PersonalDatabaseHelper.COL_5, neck);
        contentValues.put(PersonalDatabaseHelper.COL_6, thigh);
        contentValues.put(PersonalDatabaseHelper.COL_7, weekly_units);
        contentValues.put(PersonalDatabaseHelper.COL_8, daily_units);
        contentValues.put(PersonalDatabaseHelper.COL_9, date);
        contentValues.put(PersonalDatabaseHelper.COL_10, days);
        contentValues.put(PersonalDatabaseHelper.COL_11, next_units);
        contentValues.put(PersonalDatabaseHelper.COL_12, user_id);
        contentValues.put(PersonalDatabaseHelper.COL_13, weekly_points);
        contentValues.put(PersonalDatabaseHelper.COL_14, daily_points);
        contentValues.put(PersonalDatabaseHelper.COL_15, next_points);

        db_personal.insert(PersonalDatabaseHelper.TABLE_NAME, null, contentValues);
    }

    protected void initMeal_format(){
        findViewById(R.id.linCheckedBreakfast).setVisibility(View.GONE);
        findViewById(R.id.txtRequirementBreakfast).setVisibility(View.GONE);
        findViewById(R.id.linAddBreakfast).setVisibility(View.VISIBLE);
        TextView point_txt = findViewById(R.id.txtPointBreakfast);
        point_txt.setText((""));

        point_txt = findViewById(R.id.txtBreakfastProtein);
        point_txt.setText("");

        point_txt = findViewById(R.id.txtBreakfastCarbon);
        point_txt.setText("");

        point_txt = findViewById(R.id.txtBreakfastFat);
        point_txt.setText("");

        point_txt = findViewById(R.id.txtBreakfastProteinGram);
        point_txt.setText("");

        point_txt = findViewById(R.id.txtBreakfastCarbonGram);
        point_txt.setText("");

        point_txt = findViewById(R.id.txtBreakfastFatGram);
        point_txt.setText("");

        findViewById(R.id.linCheckedLunch).setVisibility(View.GONE);
        findViewById(R.id.txtRequirementLunch).setVisibility(View.GONE);
        findViewById(R.id.linAddLunch).setVisibility(View.VISIBLE);
        point_txt = findViewById(R.id.txtPointLunch);
        point_txt.setText((""));

        point_txt = findViewById(R.id.txtLunchProtein);
        point_txt.setText("");

        point_txt = findViewById(R.id.txtLunchCarbon);
        point_txt.setText("");

        point_txt = findViewById(R.id.txtLunchFat);
        point_txt.setText("");

        point_txt = findViewById(R.id.txtLunchProteinGram);
        point_txt.setText("");

        point_txt = findViewById(R.id.txtLunchCarbonGram);
        point_txt.setText("");

        point_txt = findViewById(R.id.txtLunchFatGram);
        point_txt.setText("");

        findViewById(R.id.linCheckedDinner).setVisibility(View.GONE);
        findViewById(R.id.txtRequirementDinner).setVisibility(View.GONE);
        findViewById(R.id.linAddDinner).setVisibility(View.VISIBLE);
        point_txt = findViewById(R.id.txtPointDinner);
        point_txt.setText((""));

        point_txt = findViewById(R.id.txtDinnerProtein);
        point_txt.setText("");

        point_txt = findViewById(R.id.txtDinnerCarbon);
        point_txt.setText("");

        point_txt = findViewById(R.id.txtDinnerFat);
        point_txt.setText("");

        point_txt = findViewById(R.id.txtDinnerProteinGram);
        point_txt.setText("");

        point_txt = findViewById(R.id.txtDinnerCarbonGram);
        point_txt.setText("");

        point_txt = findViewById(R.id.txtDinnerFatGram);
        point_txt.setText("");

        findViewById(R.id.linCheckedSnackBreakfast).setVisibility(View.GONE);
        findViewById(R.id.linAddSnackBreakfast).setVisibility(View.VISIBLE);
        point_txt = findViewById(R.id.txtPointSnackBreakfast);
        point_txt.setText((""));

        point_txt = findViewById(R.id.txtSnackBreakfastProtein);
        point_txt.setText("");

        point_txt = findViewById(R.id.txtSnackBreakfastCarbon);
        point_txt.setText("");

        point_txt = findViewById(R.id.txtSnackBreakfastFat);
        point_txt.setText("");

        point_txt = findViewById(R.id.txtSnackBreakfastProteinGram);
        point_txt.setText("");

        point_txt = findViewById(R.id.txtSnackBreakfastCarbonGram);
        point_txt.setText("");

        point_txt = findViewById(R.id.txtSnackBreakfastFatGram);
        point_txt.setText("");


        findViewById(R.id.linCheckedSnackLunch).setVisibility(View.GONE);
        findViewById(R.id.linAddSnackLunch).setVisibility(View.VISIBLE);
        point_txt = findViewById(R.id.txtPointSnackLunch);
        point_txt.setText((""));

        point_txt = findViewById(R.id.txtSnackLunchProtein);
        point_txt.setText("");

        point_txt = findViewById(R.id.txtSnackLunchCarbon);
        point_txt.setText("");

        point_txt = findViewById(R.id.txtSnackLunchFat);
        point_txt.setText("");

        point_txt = findViewById(R.id.txtSnackLunchProteinGram);
        point_txt.setText("");

        point_txt = findViewById(R.id.txtSnackLunchCarbonGram);
        point_txt.setText("");

        point_txt = findViewById(R.id.txtSnackLunchFatGram);
        point_txt.setText("");

        findViewById(R.id.linCheckedSnackDinner).setVisibility(View.GONE);
        findViewById(R.id.linAddSnackDinner).setVisibility(View.VISIBLE);
        point_txt = findViewById(R.id.txtPointSnackDinner);
        point_txt.setText((""));

        point_txt = findViewById(R.id.txtSnackDinnerProtein);
        point_txt.setText("");

        point_txt = findViewById(R.id.txtSnackDinnerCarbon);
        point_txt.setText("");

        point_txt = findViewById(R.id.txtSnackDinnerFat);
        point_txt.setText("");

        point_txt = findViewById(R.id.txtSnackDinnerProteinGram);
        point_txt.setText("");

        point_txt = findViewById(R.id.txtSnackDinnerCarbonGram);
        point_txt.setText("");

        point_txt = findViewById(R.id.txtSnackDinnerFatGram);
        point_txt.setText("");

        Global.morning_protein = 0;
        Global.morning_carbon = 0;
        Global.morning_fat = 0;
        Global.morning_points = 0;
        Global.morning_units = 0;
        Global.morning_total = 0;

        Global.lunch_points=0;
        Global.lunch_units=0;
        Global.lunch_total=0;
        Global.lunch_fat=0;
        Global.lunch_protein=0;
        Global.lunch_carbon=0;

        Global.dinner_protein = 0;
        Global.dinner_carbon = 0;
        Global.dinner_fat = 0;
        Global.dinner_points = 0;
        Global.dinner_units = 0;
        Global.dinner_total = 0;

        Global.snack_morning_points =0;
        Global.snack_morning_units =0;
        Global.snack_morning_total = 0;
        Global.snack_morning_fat = 0;
        Global.snack_morning_protein = 0;
        Global.snack_morning_carbon = 0;

        Global.snack_lunch_protein =0;
        Global.snack_lunch_carbon =0;
        Global.snack_lunch_fat =0;
        Global.snack_lunch_points =0;
        Global.snack_lunch_units =0;
        Global.snack_lunch_total =0;

        Global.snack_dinner_protein=0;
        Global.snack_dinner_carbon=0;
        Global.snack_dinner_fat=0;
        Global.snack_dinner_points=0;
        Global.snack_dinner_units=0;
        Global.snack_dinner_total =0;
    }

    private void graphSeries() throws ParseException {
        SQLiteDatabase db_personal;
        SQLiteOpenHelper openHelper_persoanl;
        openHelper_persoanl = new PersonalDatabaseHelper(this);
        db_personal = openHelper_persoanl.getWritableDatabase();

        final Cursor cursor1 = db_personal.rawQuery("SELECT *FROM " + PersonalDatabaseHelper.TABLE_NAME ,  null);

        Integer days =-1;
        float weights = 0;

        d1_y=0;
        d2_y=0;
        d3_y=0;
        d4_y=0;
        if(cursor1 != null) {
            if (cursor1.moveToFirst()) {
                do {
                    dates_s = cursor1.getString(cursor1.getColumnIndex(PersonalDatabaseHelper.COL_9));
                    if(dates_s.equals(d1_s))
                    {
                        float weight = Float.parseFloat(cursor1.getString(cursor1.getColumnIndex(PersonalDatabaseHelper.COL_2)));
                        d1_y = weight;
                    }
                    if(dates_s.equals(d2_s))
                    {
                        float weight = Float.parseFloat(cursor1.getString(cursor1.getColumnIndex(PersonalDatabaseHelper.COL_2)));
                        d2_y = weight;
                    }

                    if(dates_s.equals(d3_s))
                    {
                        float weight = Float.parseFloat(cursor1.getString(cursor1.getColumnIndex(PersonalDatabaseHelper.COL_2)));
                        d3_y = weight;
                    }

                    if(dates_s.equals(d4_s))
                    {
                        float weight = Float.parseFloat(cursor1.getString(cursor1.getColumnIndex(PersonalDatabaseHelper.COL_2)));
                        d4_y = weight;
                    }

                } while (cursor1.moveToNext());
            }
            cursor1.close();
        }
    }

    //---------Lunch Meal--------//
    void show_breakfast(){
        SQLiteOpenHelper openHelper_break;
        SQLiteDatabase db_break;


        openHelper_break = new BreakfastDatabaseHelper(this);
        db_break = openHelper_break.getWritableDatabase();

        final Cursor cursor_lunch =  db_break.rawQuery("SELECT *FROM " + BreakfastDatabaseHelper.TABLE_NAME ,  null);

        meat_num = 0;
        oily_num = 0;
        legumes_num = 0;
        pasta_num = 0;
        num = 0;

        if(cursor_lunch != null) {

            if (cursor_lunch.moveToFirst()) {
                do {
                    String[] category_ids = cursor_lunch.getString(cursor_lunch.getColumnIndex(BreakfastDatabaseHelper.COL_3)).split(",");
                    Integer days = cursor_lunch.getInt(cursor_lunch.getColumnIndex(BreakfastDatabaseHelper.COL_5));

                    if(days%7==0)
                    {
                        if(num==0) {
                            meat_num = 0;
                            oily_num = 0;
                            legumes_num = 0;
                            pasta_num = 0;
                        }
                        num++;
                    }
                    else
                    {
                        num = 0;
                    }

                    {
                        for(int j=0; j<category_ids.length; j++){
                            String category_id = category_ids[j];
                            if (category_id.equals("7") || category_id.equals("11") || category_id.equals("13"))
                                meat_num++;
                            else if (category_id.equals("3"))
                                oily_num++;
                            else if (category_id.equals("2"))
                                legumes_num++;
                            else if (category_id.equals("1"))
                                pasta_num++;
                        }
                    }

                } while (cursor_lunch.moveToNext());
            }
            cursor_lunch.close();
        }

        meat_txt.setText(meat_num+"");
        oily_txt.setText(oily_num+"");
        legume_txt.setText(legumes_num+"");
        pasta_txt.setText(pasta_num+"");

        if(meat_num>3)
        {
            meat_img.setVisibility(View.VISIBLE);
        }
        else
        {
            meat_img.setVisibility(View.GONE);
        }

        if(oily_num>2)
        {
            oily_img.setVisibility(View.VISIBLE);
        }
        else
        {
            oily_img.setVisibility(View.GONE);
        }

        if(legumes_num>1)
        {
            legume_img.setVisibility(View.VISIBLE);
        }
        else
        {
            legume_img.setVisibility(View.GONE);
        }

        if(pasta_num>1)
        {
            pasta_img.setVisibility(View.VISIBLE);
        }
        else
        {
            pasta_img.setVisibility(View.GONE);
        }

        if(PersonalData.getInstance().Oily_num>0) {
            findViewById(R.id.oilImage).setVisibility(View.VISIBLE);
            findViewById(R.id.oilImageFailure).setVisibility(View.GONE);
        }
        else {
            findViewById(R.id.oilImage).setVisibility(View.GONE);
            findViewById(R.id.oilImageFailure).setVisibility(View.VISIBLE);
        }

        if(PersonalData.getInstance().Junk_num>0) {
            findViewById(R.id.foodImage).setVisibility(View.GONE);
            findViewById(R.id.foodImageFailure).setVisibility(View.VISIBLE);
        }
        else {
            findViewById(R.id.foodImage).setVisibility(View.VISIBLE);
            findViewById(R.id.foodImageFailure).setVisibility(View.GONE);
        }
    }

    void show_lunch(){
        SQLiteOpenHelper openHelper_lunch;
        SQLiteDatabase db_lunch;


        openHelper_lunch = new LunchDatabaseHelper(this);
        db_lunch = openHelper_lunch.getWritableDatabase();

        final Cursor cursor_lunch =  db_lunch.rawQuery("SELECT *FROM " + LunchDatabaseHelper.TABLE_NAME ,  null);

        meat_num = 0;
        oily_num = 0;
        legumes_num = 0;
        pasta_num = 0;
        num = 0;

        if(cursor_lunch != null) {

            if (cursor_lunch.moveToFirst()) {
                do {
                    String[] category_ids = cursor_lunch.getString(cursor_lunch.getColumnIndex(LunchDatabaseHelper.COL_3)).split(",");
                    Integer days = cursor_lunch.getInt(cursor_lunch.getColumnIndex(LunchDatabaseHelper.COL_5));

                    if(days%7==0)
                    {
                        if(num==0) {
                            meat_num = 0;
                            oily_num = 0;
                            legumes_num = 0;
                            pasta_num = 0;
                        }
                        num++;
                    }
                    else
                    {
                        num = 0;
                    }

                    {
                        for(int j=0; j<category_ids.length; j++){
                            String category_id = category_ids[j];
                            if (category_id.equals("7") || category_id.equals("11") || category_id.equals("13"))
                                meat_num++;
                            else if (category_id.equals("3"))
                                oily_num++;
                            else if (category_id.equals("2"))
                                legumes_num++;
                            else if (category_id.equals("1"))
                                pasta_num++;
                        }
                    }

                } while (cursor_lunch.moveToNext());
            }
            cursor_lunch.close();
        }

        meat_txt.setText(meat_num+"");
        oily_txt.setText(oily_num+"");
        legume_txt.setText(legumes_num+"");
        pasta_txt.setText(pasta_num+"");

        if(meat_num>3)
        {
            meat_img.setVisibility(View.VISIBLE);
        }
        else
        {
            meat_img.setVisibility(View.GONE);
        }

        if(oily_num>2)
        {
            oily_img.setVisibility(View.VISIBLE);
        }
        else
        {
            oily_img.setVisibility(View.GONE);
        }

        if(legumes_num>1)
        {
            legume_img.setVisibility(View.VISIBLE);
        }
        else
        {
            legume_img.setVisibility(View.GONE);
        }

        if(pasta_num>1)
        {
            pasta_img.setVisibility(View.VISIBLE);
        }
        else
        {
            pasta_img.setVisibility(View.GONE);
        }

        if(PersonalData.getInstance().Oily_num>0) {
            findViewById(R.id.oilImage).setVisibility(View.VISIBLE);
            findViewById(R.id.oilImageFailure).setVisibility(View.GONE);
        }
        else {
            findViewById(R.id.oilImage).setVisibility(View.GONE);
            findViewById(R.id.oilImageFailure).setVisibility(View.VISIBLE);
        }

        if(PersonalData.getInstance().Junk_num>0) {
            findViewById(R.id.foodImage).setVisibility(View.GONE);
            findViewById(R.id.foodImageFailure).setVisibility(View.VISIBLE);
        }
        else {
            findViewById(R.id.foodImage).setVisibility(View.VISIBLE);
            findViewById(R.id.foodImageFailure).setVisibility(View.GONE);
        }
    }

    void show_dinner(){
        SQLiteOpenHelper openHelper_dinner;
        SQLiteDatabase db_dinner;


        openHelper_dinner = new DinnerDatabaseHelper(this);
        db_dinner = openHelper_dinner.getWritableDatabase();

        final Cursor cursor_lunch =  db_dinner.rawQuery("SELECT *FROM " + DinnerDatabaseHelper.TABLE_NAME ,  null);

        meat_num = 0;
        oily_num = 0;
        legumes_num = 0;
        pasta_num = 0;
        num = 0;

        if(cursor_lunch != null) {

            if (cursor_lunch.moveToFirst()) {
                do {
                    String[] category_ids = cursor_lunch.getString(cursor_lunch.getColumnIndex(DinnerDatabaseHelper.COL_3)).split(",");
                    Integer days = cursor_lunch.getInt(cursor_lunch.getColumnIndex(DinnerDatabaseHelper.COL_5));

                    if(days%7==0)
                    {
                        if(num==0) {
                            meat_num = 0;
                            oily_num = 0;
                            legumes_num = 0;
                            pasta_num = 0;
                        }
                        num++;
                    }
                    else
                    {
                        num = 0;
                    }

                    {
                        for(int j=0; j<category_ids.length; j++){
                            String category_id = category_ids[j];
                            if (category_id.equals("7") || category_id.equals("11") || category_id.equals("13"))
                                meat_num++;
                            else if (category_id.equals("3"))
                                oily_num++;
                            else if (category_id.equals("2"))
                                legumes_num++;
                            else if (category_id.equals("1"))
                                pasta_num++;
                        }
                    }

                } while (cursor_lunch.moveToNext());
            }
            cursor_lunch.close();
        }

        meat_txt.setText(meat_num+"");
        oily_txt.setText(oily_num+"");
        legume_txt.setText(legumes_num+"");
        pasta_txt.setText(pasta_num+"");

        if(meat_num>3)
        {
            meat_img.setVisibility(View.VISIBLE);
        }
        else
        {
            meat_img.setVisibility(View.GONE);
        }

        if(oily_num>2)
        {
            oily_img.setVisibility(View.VISIBLE);
        }
        else
        {
            oily_img.setVisibility(View.GONE);
        }

        if(legumes_num>1)
        {
            legume_img.setVisibility(View.VISIBLE);
        }
        else
        {
            legume_img.setVisibility(View.GONE);
        }

        if(pasta_num>1)
        {
            pasta_img.setVisibility(View.VISIBLE);
        }
        else
        {
            pasta_img.setVisibility(View.GONE);
        }

        if(PersonalData.getInstance().Oily_num>0) {
            findViewById(R.id.oilImage).setVisibility(View.VISIBLE);
            findViewById(R.id.oilImageFailure).setVisibility(View.GONE);
        }
        else {
            findViewById(R.id.oilImage).setVisibility(View.GONE);
            findViewById(R.id.oilImageFailure).setVisibility(View.VISIBLE);
        }

        if(PersonalData.getInstance().Junk_num>0) {
            findViewById(R.id.foodImage).setVisibility(View.GONE);
            findViewById(R.id.foodImageFailure).setVisibility(View.VISIBLE);
        }
        else {
            findViewById(R.id.foodImage).setVisibility(View.VISIBLE);
            findViewById(R.id.foodImageFailure).setVisibility(View.GONE);
        }
    }

    public void Load_breakfast() throws ParseException {
        SQLiteDatabase db_meal, db_break;
        SQLiteOpenHelper openHelper_meal, openHelper_break;

        openHelper_meal = new MealDatabaseHelper(this);
        db_meal = openHelper_meal.getWritableDatabase();

        openHelper_break = new BreakfastDatabaseHelper(this);
        db_break = openHelper_break.getWritableDatabase();

        db_break.execSQL("delete from "+BreakfastDatabaseHelper.TABLE_NAME);
        PersonalData.getInstance().Oily_num = 0;
        PersonalData.getInstance().Junk_num = 0;

        final Cursor cursor_meal = db_meal.rawQuery("SELECT *FROM " + MealDatabaseHelper.TABLE_NAME ,  null);

        int num = 0;
        fruit_num = 0;
        start_date = "";
        if(cursor_meal != null) {
            if (cursor_meal.moveToFirst()) {
                do {

                    if(num==0)
                        start_date = cursor_meal.getString(cursor_meal.getColumnIndex(MealDatabaseHelper.COL_10));
                    String timing = cursor_meal.getString(cursor_meal.getColumnIndex(MealDatabaseHelper.COL_9));

                    if(timing.equals("breakfast")) {
                        String foodname = cursor_meal.getString(cursor_meal.getColumnIndex(MealDatabaseHelper.COL_2));
                        String categoryid = cursor_meal.getString(cursor_meal.getColumnIndex(MealDatabaseHelper.COL_8));
                        for(int i=0; i<categoryid.split(",").length;i++){
                            if(categoryid.split(",")[i].equals("59"))
                                PersonalData.getInstance().Oily_num++;
                            if(categoryid.split(",")[i].equals("4"))
                                PersonalData.getInstance().Junk_num++;
                        }

                        String date = cursor_meal.getString(cursor_meal.getColumnIndex(MealDatabaseHelper.COL_10));

                        Date end_day = getDate(date);
                        Date start_day = getDate(start_date);

                        long day_s = (end_day.getTime() - start_day.getTime());
                        int day_num =Math.round(day_s/(1000 * 60 * 60 * 24));

                        insertBreakfast(foodname, categoryid, date, day_num);
                    }
                    num++;
                } while (cursor_meal.moveToNext());
            }
            cursor_meal.close();
        }
    }

    public void Load_lunch() throws ParseException {
        SQLiteDatabase db_meal, db_lunch;
        SQLiteOpenHelper openHelper_meal, openHelper_lunch;

        openHelper_meal = new MealDatabaseHelper(this);
        db_meal = openHelper_meal.getWritableDatabase();

        openHelper_lunch = new LunchDatabaseHelper(this);
        db_lunch = openHelper_lunch.getWritableDatabase();

        db_lunch.execSQL("delete from "+LunchDatabaseHelper.TABLE_NAME);
        PersonalData.getInstance().Oily_num = 0;
        PersonalData.getInstance().Junk_num = 0;

        final Cursor cursor_meal = db_meal.rawQuery("SELECT *FROM " + MealDatabaseHelper.TABLE_NAME ,  null);

        int num = 0;
        fruit_num = 0;
        start_date = "";
        if(cursor_meal != null) {
            if (cursor_meal.moveToFirst()) {
                do {

                    if(num==0)
                        start_date = cursor_meal.getString(cursor_meal.getColumnIndex(MealDatabaseHelper.COL_10));
                    String timing = cursor_meal.getString(cursor_meal.getColumnIndex(MealDatabaseHelper.COL_9));

                    if(timing.equals("lunch")) {
                        String foodname = cursor_meal.getString(cursor_meal.getColumnIndex(MealDatabaseHelper.COL_2));
                        String categoryid = cursor_meal.getString(cursor_meal.getColumnIndex(MealDatabaseHelper.COL_8));
                        for(int i=0; i<categoryid.split(",").length;i++){
                            if(categoryid.split(",")[i].equals("59"))
                                PersonalData.getInstance().Oily_num++;
                            if(categoryid.split(",")[i].equals("4"))
                                PersonalData.getInstance().Junk_num++;
                        }
                        String date = cursor_meal.getString(cursor_meal.getColumnIndex(MealDatabaseHelper.COL_10));

                        Date end_day = getDate(date);
                        Date start_day = getDate(start_date);

                        long day_s = (end_day.getTime() - start_day.getTime());
                        int day_num =Math.round(day_s/(1000 * 60 * 60 * 24));

                        insertLunch(foodname, categoryid, date, day_num);
                    }
                    num++;
                } while (cursor_meal.moveToNext());
            }
            cursor_meal.close();
        }
    }

    public void Load_dinner() throws ParseException {
        SQLiteDatabase db_meal, db_dinner;
        SQLiteOpenHelper openHelper_meal, openHelper_dinner;

        openHelper_meal = new MealDatabaseHelper(this);
        db_meal = openHelper_meal.getWritableDatabase();

        openHelper_dinner = new DinnerDatabaseHelper(this);
        db_dinner = openHelper_dinner.getWritableDatabase();

        db_dinner.execSQL("delete from "+DinnerDatabaseHelper.TABLE_NAME);
        PersonalData.getInstance().Oily_num = 0;
        PersonalData.getInstance().Junk_num = 0;

        final Cursor cursor_meal = db_meal.rawQuery("SELECT *FROM " + MealDatabaseHelper.TABLE_NAME ,  null);

        int num = 0;
        fruit_num = 0;
        start_date = "";
        if(cursor_meal != null) {
            if (cursor_meal.moveToFirst()) {
                do {

                    if(num==0)
                        start_date = cursor_meal.getString(cursor_meal.getColumnIndex(MealDatabaseHelper.COL_10));
                    String timing = cursor_meal.getString(cursor_meal.getColumnIndex(MealDatabaseHelper.COL_9));

                    if(timing.equals("dinner")) {
                        String foodname = cursor_meal.getString(cursor_meal.getColumnIndex(MealDatabaseHelper.COL_2));
                        String categoryid = cursor_meal.getString(cursor_meal.getColumnIndex(MealDatabaseHelper.COL_8));
                        for(int i=0; i<categoryid.split(",").length;i++){
                            if(categoryid.split(",")[i].equals("59"))
                                PersonalData.getInstance().Oily_num++;
                            if(categoryid.split(",")[i].equals("4"))
                                PersonalData.getInstance().Junk_num++;
                        }
                        String date = cursor_meal.getString(cursor_meal.getColumnIndex(MealDatabaseHelper.COL_10));

                        Date end_day = getDate(date);
                        Date start_day = getDate(start_date);

                        long day_s = (end_day.getTime() - start_day.getTime());
                        int day_num =Math.round(day_s/(1000 * 60 * 60 * 24));

                        insertDinner(foodname, categoryid, date, day_num);
                    }
                    num++;
                } while (cursor_meal.moveToNext());
            }
            cursor_meal.close();
        }
    }

    void insertBreakfast(String foodname, String categoryid, String date, Integer days){
        SQLiteDatabase db_breakfast;
        SQLiteOpenHelper openHelper_breakfast;

        openHelper_breakfast = new BreakfastDatabaseHelper(this);
        db_breakfast = openHelper_breakfast.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(BreakfastDatabaseHelper.COL_2, foodname);
        contentValues.put(BreakfastDatabaseHelper.COL_3, categoryid);
        contentValues.put(BreakfastDatabaseHelper.COL_4, date);
        contentValues.put(BreakfastDatabaseHelper.COL_5, days);

        db_breakfast.insert(BreakfastDatabaseHelper.TABLE_NAME, null, contentValues);
    }

    void insertLunch(String foodname, String categoryid, String date, Integer days){
        SQLiteDatabase db_lunch;
        SQLiteOpenHelper openHelper_lunch;

        openHelper_lunch = new LunchDatabaseHelper(this);
        db_lunch = openHelper_lunch.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(LunchDatabaseHelper.COL_2, foodname);
        contentValues.put(LunchDatabaseHelper.COL_3, categoryid);
        contentValues.put(LunchDatabaseHelper.COL_4, date);
        contentValues.put(LunchDatabaseHelper.COL_5, days);

        db_lunch.insert(LunchDatabaseHelper.TABLE_NAME, null, contentValues);
    }

    void insertDinner(String foodname, String categoryid, String date, Integer days){
        SQLiteDatabase db_dinner;
        SQLiteOpenHelper openHelper_dinner;

        openHelper_dinner = new DinnerDatabaseHelper(this);
        db_dinner = openHelper_dinner.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(DinnerDatabaseHelper.COL_2, foodname);
        contentValues.put(DinnerDatabaseHelper.COL_3, categoryid);
        contentValues.put(DinnerDatabaseHelper.COL_4, date);
        contentValues.put(DinnerDatabaseHelper.COL_5, days);

        db_dinner.insert(DinnerDatabaseHelper.TABLE_NAME, null, contentValues);
    }

    Date getDate(String date) throws ParseException {
        SimpleDateFormat dateFormat= new SimpleDateFormat("dd/MM");
            Date any_date = dateFormat.parse(date);
            return any_date;
    }

    public void startTimer(final TextView v, final View checkView) {
        String Minutes = v.getText().subSequence(0, 2).toString();
        String Seconds = v.getText().subSequence(3, 5).toString();
        long time = Integer.parseInt(Minutes) * 1000 * 60 + Integer.parseInt(Seconds) * 1000 - 1000;

        CountDownTimer timer = new CountDownTimer(time, 1000) {
            public void onTick(long millisUntilFinished) {
                if (checkView.getVisibility() == View.VISIBLE)
                    setTimerToView(v);
                else {
                    SetTimer();
                }
            }

            public void onFinish() {

            }

        };

        timer.start();
    }

    public  void setTimerToView(TextView txtView) {
        String Minutes = txtView.getText().subSequence(0, 2).toString();
        String Seconds = txtView.getText().subSequence(3, 5).toString();
        long time = Integer.parseInt(Minutes) * 1000 * 60 + Integer.parseInt(Seconds) * 1000 - 1000;
        long remainMinutes = time / 1000  / 60;
        long remainSeconds = time / 1000 % 60;

        if (remainMinutes >= 0 && remainSeconds >= 0) {
            String result = String.format("%02d:%02d", remainMinutes, remainSeconds);
            txtView.setText(result);
        }
        else {
            txtView.setText("00:00");
        }
    }

    public void fruit_format() {
        fruit1.setImageResource(R.drawable.ic_apple_plus);
        fruit2.setImageResource(R.drawable.ic_apple_idle);
        fruit3.setImageResource(R.drawable.ic_apple_idle);
        TextView appleT = findViewById(R.id.apple_txt);
        appleT.setText("0 φ.");
    }

    public void water_format() {
        water1.setImageResource(R.drawable.ic_water_cup_empty_plus);
        water2.setImageResource(R.drawable.ic_water_cup_empty);
        water3.setImageResource(R.drawable.ic_water_cup_empty);
        water4.setImageResource(R.drawable.ic_water_cup_empty);
        water5.setImageResource(R.drawable.ic_water_cup_empty);
        water6.setImageResource(R.drawable.ic_water_cup_empty);
        water7.setImageResource(R.drawable.ic_water_cup_empty);
        water8.setImageResource(R.drawable.ic_water_cup_empty);
        TextView water_L = findViewById(R.id.water_L);
        water_L.setText("0 L");
    }

    public void layoutToImage(Date week_date) {
        String title = new SimpleDateFormat("yyyy-MM-dd").format(week_date);
        LinearLayout layout = findViewById(R.id.hiddenList);
        openHelper = new MealDatabaseHelper(this);
        db = openHelper.getWritableDatabase();

        for(int i = 0; i < timings.length; i++) {
            layout.removeAllViews();
            final Cursor cursor = db.rawQuery("SELECT *FROM " + MealDatabaseHelper.TABLE_NAME,  null);

            if(cursor != null){
                if (cursor.moveToFirst()){
                    do{
                        String date  = cursor.getString(cursor.getColumnIndex("date"));
                        String timing = cursor.getString(cursor.getColumnIndex("timing"));
                        if(date.equals(getDayString(week_date)) && timing.equals(timings[i])) {
                            View convertView = LayoutInflater.from(this).inflate(R.layout.meal_list_item, layout, false);
                            CustomMealAdapter.FoodHolder holder = new CustomMealAdapter.FoodHolder();
                            holder.title = (TextView) convertView.findViewById(R.id.listitem_title);
                            holder.gram_txt = (TextView) convertView.findViewById(R.id.gram_txt);
                            holder.point_txt = (TextView) convertView.findViewById(R.id.point_txt);
                            holder.id_btn = (ImageView) convertView.findViewById(R.id.imgAddMeal);
                            convertView.setTag(holder);
                            convertView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));

                            holder.title.setText(cursor.getString(cursor.getColumnIndex("foodname")));
                            holder.title.setWidth(700);
                            if (cursor.getFloat(cursor.getColumnIndex("gram")) == 0f) {
                                holder.gram_txt.setText("");
                            } else {
                                holder.gram_txt.setText("("+cursor.getString(cursor.getColumnIndex("gram")) + "γρ.)");
                            }

                            holder.point_txt.setText(cursor.getString(cursor.getColumnIndex("date")));
                            holder.id_btn.setId(cursor.getInt(cursor.getColumnIndex("ID")));
                            layout.addView(convertView);
                        }
                    }while(cursor.moveToNext());
                }
                cursor.close();
            }

            if (layout.getChildCount() > 0) {

                layout.setDrawingCacheEnabled(true);
                layout.buildDrawingCache(true);

                layout.measure(layout.getMeasuredWidth(), layout.getMeasuredHeight());
                layout.layout(0, 0, layout.getMeasuredWidth(), layout.getMeasuredHeight());
                layout.buildDrawingCache();
                Bitmap bitmap = Bitmap.createBitmap(layout.getMeasuredWidth(), layout.getMeasuredHeight(), Bitmap.Config.ARGB_8888);

                Canvas canvas = new Canvas(bitmap);
                layout.draw(canvas);
                Intent share = new Intent(Intent.ACTION_SEND);
                share.setType("image/png");
                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, bytes);

                File f = new File(android.os.Environment.getExternalStorageDirectory() + File.separator + timings[i] + "_" +title + "_image.png");
                try {
                    f.createNewFile();
                    FileOutputStream fo = new FileOutputStream(f);
                    fo.write(bytes.toByteArray());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }
    public void imageToPDF() throws FileNotFoundException {
        if (weekly_dates.length == 1) {
            String title = new SimpleDateFormat("yyyy-MM-dd").format(weekly_dates[0]);
            try {
                Document document = new Document();
                dirpath = android.os.Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString();
                PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(dirpath + "/daily_"+title+".pdf")); //  Change pdf's name.
                document.open();
                SimpleDateFormat localDateFormat = new SimpleDateFormat("EEE, MMM d, yy");
                String time = localDateFormat.format(weekly_dates[0]);
                //create a paragraph
                Paragraph paragraph = new Paragraph(time);
                float[] columnWidths = {90f};
                //create PDF table with the given widths
                PdfPTable table = new PdfPTable(columnWidths);

                for (int i = 0; i < timings.length; i++) {
                    String path = android.os.Environment.getExternalStorageDirectory() + File.separator + timings[i] + "_" +title + "_image.png";
                    File fImage = new File(path);
                    if (fImage.exists()) {
                        PdfPCell cell = new PdfPCell();
                        PdfPCell cell_title = new PdfPCell(new Phrase(timings_title[i]));
                        Image image = Image.getInstance(android.os.Environment.getExternalStorageDirectory() + File.separator + timings[i] + "_" + title + "_image.png");
                        cell.addElement(image);
                        table.addCell(cell_title);
                        table.addCell(cell);
                    }
                }

                paragraph.add(table);
                document.add(paragraph);
                document.close();
                Toast.makeText(this, "Η αποθήκευση ολοκληρώθηκε!", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
            }
        }
        else if (weekly_dates.length > 1) {
            String week_title = new SimpleDateFormat("yyyy-MM-dd").format(weekly_dates[0]) + "~" + new SimpleDateFormat("yyyy-MM-dd").format(weekly_dates[6]);
            try {
                Document document = new Document();
                dirpath = android.os.Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString();

                File fdelete = new File(dirpath + "/"+week_title+".pdf");
                if (fdelete.exists()) {
                    if (fdelete.delete()) {
                    } else {
                    }
                }
                PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(dirpath + "/"+week_title+".pdf")); //  Change pdf's name.
                document.open();

                for(int p = 0; p < weekly_dates.length; p++){
                    SimpleDateFormat localDateFormat = new SimpleDateFormat("EEE, MMM d, yy");
                    String title = new SimpleDateFormat("yyyy-MM-dd").format(weekly_dates[p]);
                    String time = localDateFormat.format(weekly_dates[p]);
                    //create a paragraph
                    Paragraph paragraph = new Paragraph(time);
                    float[] columnWidths = {90f};
                    //create PDF table with the given widths
                    PdfPTable table = new PdfPTable(columnWidths);

                    for (int i = 0; i < timings.length; i++) {
                        String path = android.os.Environment.getExternalStorageDirectory() + File.separator + timings[i] + "_" +title + "_image.png";

                        File fImage = new File(path);
                        if (fImage.exists()) {
                            PdfPCell cell = new PdfPCell();
                            PdfPCell cell_title = new PdfPCell(new Phrase(timings_title[i]));
                            Image image = Image.getInstance(android.os.Environment.getExternalStorageDirectory() + File.separator + timings[i] + "_" + title + "_image.png");
                            cell.addElement(image);
                            table.addCell(cell_title);
                            table.addCell(cell);
                        }
                    }

                    paragraph.add(table);
                    document.add(paragraph);
                }

                document.close();
                Toast.makeText(this, "Η αποθήκευση ολοκληρώθηκε!", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
            }
        }
    }
    public void deleteImage(Date week_date) {
        String title = new SimpleDateFormat("yyyy-MM-dd").format(week_date);
        for(int i = 0; i < timings.length; i++) {
            String path = android.os.Environment.getExternalStorageDirectory() + File.separator + timings[i] + "_" +title + "_image.png";
            File fdelete = new File(path);
            if (fdelete.exists()) {
                if (fdelete.delete()) {
                } else {
                }
            }
        }
    }

    private void setSettings() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDateTime now = LocalDateTime.now();
        String date = dtf.format(now);
        REST rest = MainApplication.getContainer().get(REST.class);
        rest.getInformation(date)
                .enqueue(new Callback<Wrappers.Single<Information>>() {
                    @Override
                    public void onResponse(Call<Wrappers.Single<Information>> call, retrofit2.Response<Wrappers.Single<Information>> response) {
                        Information information = response.body().data;

                        if (information != null) {
                            PersonalData.getInstance().setGoal(Goal.values()[information.goal]);
                            PersonalData.getInstance().setInitial_weight(information.initial_weight);
                            PersonalData.getInstance().setWeight(information.weight);
                            PersonalData.getInstance().setGender(Gender.values()[information.gender]);
                            PersonalData.getInstance().setHeight(information.height);
                            try {
                                PersonalData.getInstance().setBirthday(new SimpleDateFormat("yyyy-MM-dd").parse(information.birthday));
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            PersonalData.getInstance().setGymType(information.gym_type);
                            PersonalData.getInstance().setSportType1(information.sport_type1);
                            PersonalData.getInstance().setSportType2(information.sport_type2);
                            PersonalData.getInstance().setSportType3(information.sport_type3);
                            PersonalData.getInstance().setSportTime1(information.sport_time1);
                            PersonalData.getInstance().setSportTime2(information.sport_time2);
                            PersonalData.getInstance().setSportTime3(information.sport_time3);
                            PersonalData.getInstance().setGoal_weight(information.goal_weight);
                            PersonalData.getInstance().setWeekly_reduce(information.weekly_goal);
                            PersonalData.getInstance().setDietMode(DietMode.values()[information.diet_mode]);


                            try {
                                initView();
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            try {
                                initMeal();
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                        }
                        else {
                            Toast.makeText(DailyCaleandarActivity.this, getResources().getString(R.string.offline_text), Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Wrappers.Single<Information>> call, Throwable t) {

                    }
                });
    }
}