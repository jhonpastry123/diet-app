package com.diet.trinity.activity;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import com.diet.trinity.MainApplication;
import com.diet.trinity.MyNotificationPublisher;
import com.diet.trinity.R;
import com.diet.trinity.Utility.Global;
import com.diet.trinity.data.api.REST;
import com.diet.trinity.data.common.DietMode;
import com.diet.trinity.data.common.Gender;
import com.diet.trinity.data.common.Goal;
import com.diet.trinity.data.common.PersonalData;
import com.diet.trinity.data.models.Information;
import com.diet.trinity.data.models.User;
import com.diet.trinity.data.models.Wrappers;
import com.github.mikephil.charting.charts.PieChart;
import com.itextpdf.text.Document;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.jjoe64.graphview.GraphView;
import com.pixplicity.easyprefs.library.Prefs;

import org.joda.time.DateTime;
import org.joda.time.Weeks;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DailyCaleandarActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener, View.OnClickListener {
    public static final String NOTIFICATION_CHANNEL_ID = "10001" ;
    private final static String default_notification_channel_id = "default" ;

    /* First Block Items */
    TextView txtDate, txtWeek;
    ImageView imgCalendar;
    /* First Block Items */

    /* Second Block Items */
    TextView deltaUnits, deltaWeight, loseAlert, txtPoint, txtUnit;
    ImageView imgBalerinaPoint, imgBalerinaUnit;
    LinearLayout linPoint, linUnit;
    /* Second Block Items */

    /* Third Block Items */
    PieChart pieChart;
    Spinner typeDropdown;
    ImageButton imgPDF;
    // breakfast part
    ImageView imgBreakfast;
    TextView txtTimerBreakFast, txtBreakfastProtein, txtBreakfastCarbon, txtBreakfastFat, txtBreakfastProteinGram, txtBreakfastCarbonGram, txtBreakfastFatGram, txtPointBreakfast, txtRequirementBreakfast;
    LinearLayout linAddBreakfast, linCheckedBreakfast;
    // lunch part
    ImageView imgLunch;
    TextView txtTimerLunch, txtLunchProtein, txtLunchCarbon, txtLunchFat, txtLunchProteinGram, txtLunchCarbonGram, txtLunchFatGram, txtPointLunch, txtRequirementLunch;
    LinearLayout linAddLunch, linCheckedLunch;
    // Dinner part
    ImageView imgDinner;
    TextView txtTimerDinner, txtDinnerProtein, txtDinnerCarbon, txtDinnerFat, txtDinnerProteinGram, txtDinnerCarbonGram, txtDinnerFatGram, txtPointDinner, txtRequirementDinner;
    LinearLayout linAddDinner, linCheckedDinner;
    // BreakfastSnack part
    ImageView imgBreakfastSnack;
    TextView txtTimerBreakfastSnack, txtBreakfastSnackProtein, txtBreakfastSnackCarbon, txtBreakfastSnackFat, txtBreakfastSnackProteinGram, txtBreakfastSnackCarbonGram, txtBreakfastSnackFatGram, txtPointBreakfastSnack, txtRequirementBreakfastSnack;
    LinearLayout linAddBreakfastSnack, linCheckedBreakfastSnack;
    // LunchSnack part
    ImageView imgLunchSnack;
    TextView txtTimerLunchSnack, txtLunchSnackProtein, txtLunchSnackCarbon, txtLunchSnackFat, txtLunchSnackProteinGram, txtLunchSnackCarbonGram, txtLunchSnackFatGram, txtPointLunchSnack, txtRequirementLunchSnack;
    LinearLayout linAddLunchSnack, linCheckedLunchSnack;
    // DinnerSnack part
    ImageView imgDinnerSnack;
    TextView txtTimerDinnerSnack, txtDinnerSnackProtein, txtDinnerSnackCarbon, txtDinnerSnackFat, txtDinnerSnackProteinGram, txtDinnerSnackCarbonGram, txtDinnerSnackFatGram, txtPointDinnerSnack, txtRequirementDinnerSnack;
    LinearLayout linAddDinnerSnack, linCheckedDinnerSnack;
    /* Third Block Items */

    /* Fourth Block Items */
    EditText edtWaist, edtNeck, edtThigh;
    TextView txtResult;
    Button btnSave;
    /* Fourth Block Items */

    /* Fifth Block Items */
    LinearLayout fat;
    TextView txtBFP;
    /* Fifth Block Items */
    /* Sixth Block Items */
    TextView txtIDW;
    /* Sixth Block Items */
    /* Seventh Block Items */
    TextView txtBMI, txtBMIText;
    /* Seventh Block Items */
    /* Eighth Block Items */
    TextView txtGoalWeight, txtCurrentWeight;
    ImageView imgSubtractWeight, imgAddWeight;
    /* Eighth Block Items */
    /* Ninth Block Items */
    TextView water_L;
    ImageView water1, water2, water3, water4, water5, water6, water7, water8;
    /* Ninth Block Items */
    /* Tenth Block Items */
    TextView meat_amount, milk_amount, bean_amount, noodle_amount;
    ImageView meat_warning, milk_warning, bean_warning, noodle_warning;
    /* Tenth Block Items */
    /* Eleventh Block Items */
    TextView apple_txt;
    ImageView fruit1, fruit2, fruit3;
    /* Eleventh Block Items */
    /* Twelfth Block Items */
    LinearLayout oilImage,oilImageFailure,foodImage, foodImageFailure;
    /* Twelfth Block Items */
    /* Thirteenth Block Items */
    GraphView graph;
    TextView txtInitialWeight, txtIdealWeight, txtTodayWeight, txtWeightDiff;
    /* Thirteenth Block Items */

    /* Main Menu Block Items */
    ImageView imgMain, imgRecipe, imgSetting, imgHelp;
    /* Main Menu Block Items */

    // Variables
    Calendar mCalendar = Calendar.getInstance();
    Date mSelectedDate = mCalendar.getTime();

    String [] timings = {"breakfast", "snack_breakfast", "lunch", "snack_lunch", "dinner", "snack_dinner"};
    String [] timings_title = {"Proino", "Dekatiano", "Mesimeriano", "Apogeymatino", "Vradino", "Proypnou"};

    Float nextUnits = 0f;
    Float nextPoints = 0f;

    Date[] weekly_dates;

    //------ timer --------//
    boolean breakfast_start = false;
    boolean lunch_start = false;
    boolean dinner_start = false;
    boolean breakfast_snack_start = false;
    boolean lunch_snack_start = false;
    boolean dinner_snack_start = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daily_caleandar);
        initView();
        checkAvailable();
        addEventListener();

        scheduleNotification(getNotification( "ΠΡΩΙΝΟ ΓΕΥΜΑ","το πρωινό είναι το πιο σημαντικό γεύμα της ημέρας" ) , 9 );
        scheduleNotification(getNotification( "μεσημεριανό","για μια πιο υγιεινή ζωή μειώστε το αλάτι και τη ζάχαρη στα γεύματά σας" ) , 12 );
        scheduleNotification(getNotification( "ΩΡΑ ΓΙΑ ΝΕΡΟ!","Παρακαλώ, πιείτε νερό!" ) , 13 );
        scheduleNotification(getNotification( "ΔΕΙΠΝΟ","Δύο ώρες πριν από τον ύπνο δεν τρώμε μεγάλα γεύματα!" ) , 20 );
    }
    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.imgCalendar:
                showDatePickerDlg();
                break;
            case R.id.linPoint:
                linUnit.setAlpha(0.5f);
                imgBalerinaUnit.setVisibility(View.INVISIBLE);

                PersonalData.getInstance().setDietMode(DietMode.POINT);
                linPoint.setAlpha(1.0f);
                imgBalerinaPoint.setVisibility(View.VISIBLE);
                deltaUnits.setText(String.format(Locale.US, "%.1f", nextPoints));
                break;
            case R.id.linUnit:
                linPoint.setAlpha(0.5f);
                imgBalerinaPoint.setVisibility(View.INVISIBLE);

                PersonalData.getInstance().setDietMode(DietMode.UNIT);
                linUnit.setAlpha(1.0f);
                imgBalerinaUnit.setVisibility(View.VISIBLE);
                deltaUnits.setText(String.format(Locale.US, "%.1f", nextUnits));
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
            case R.id.imgBreakfast:
                startTimer(txtTimerBreakFast, linCheckedBreakfast);
                breakfast_start = true;
                break;
            case R.id.linAddBreakfast:
                Global.timing_id = 1;
                searchFood();
                break;
            case R.id.linCheckedBreakfast:
                Global.timing_id = 1;
                showMeal_List();
                break;
            case R.id.imgLunch:
                startTimer(txtTimerLunch, linCheckedLunch);
                lunch_start = true;
                break;
            case R.id.linAddLunch:
                Global.timing_id = 2;
                searchFood();
                break;
            case R.id.linCheckedLunch:
                Global.timing_id = 2;
                showMeal_List();
                break;
            case R.id.imgDinner:
                startTimer(txtTimerDinner, linCheckedDinner);
                dinner_start = true;
                break;
            case R.id.linAddDinner:
                Global.timing_id = 3;
                searchFood();
                break;
            case R.id.linCheckedDinner:
                Global.timing_id = 3;
                showMeal_List();
                break;
            case R.id.imgBreakfastSnack:
                startTimer(txtTimerBreakfastSnack, linCheckedBreakfastSnack);
                breakfast_snack_start = true;
                break;
            case R.id.linAddBreakfastSnack:
                Global.timing_id = 4;
                searchFood();
                break;
            case R.id.linCheckedBreakfastSnack:
                Global.timing_id = 4;
                showMeal_List();
                break;
            case R.id.imgLunchSnack:
                startTimer(txtTimerLunchSnack, linCheckedLunchSnack);
                lunch_snack_start = true;
                break;
            case R.id.linAddLunchSnack:
                Global.timing_id = 5;
                searchFood();
                break;
            case R.id.linCheckedLunchSnack:
                Global.timing_id = 5;
                showMeal_List();
                break;
            case R.id.imgDinnerSnack:
                startTimer(txtTimerDinnerSnack, linCheckedDinnerSnack);
                dinner_snack_start = true;
                break;
            case R.id.linAddDinnerSnack:
                Global.timing_id = 6;
                searchFood();
                break;
            case R.id.linCheckedDinnerSnack:
                Global.timing_id = 6;
                showMeal_List();
                break;
            case R.id.btnSave:
                saveSetting();
                break;
            case R.id.imgSubtractWeight:
                txtCurrentWeight.setText(String.format(Locale.US, "%.1f", Float.parseFloat(txtCurrentWeight.getText().toString()) - 0.1));
                break;
            case R.id.imgAddWeight:
                txtCurrentWeight.setText(String.format(Locale.US, "%.1f", Float.parseFloat(txtCurrentWeight.getText().toString()) + 0.1));
                break;
            case R.id.water1:
                water1.setImageResource(R.drawable.ic_water_cup_full);
                water2.setImageResource(R.drawable.ic_water_cup_empty_plus);
                water3.setImageResource(R.drawable.ic_water_cup_empty);
                water4.setImageResource(R.drawable.ic_water_cup_empty);
                water5.setImageResource(R.drawable.ic_water_cup_empty);
                water6.setImageResource(R.drawable.ic_water_cup_empty);
                water7.setImageResource(R.drawable.ic_water_cup_empty);
                water8.setImageResource(R.drawable.ic_water_cup_empty);
                water_L.setText("0.25 L");
                break;
            case R.id.water2:
                water1.setImageResource(R.drawable.ic_water_cup_full);
                water2.setImageResource(R.drawable.ic_water_cup_full);
                water3.setImageResource(R.drawable.ic_water_cup_empty_plus);
                water4.setImageResource(R.drawable.ic_water_cup_empty);
                water5.setImageResource(R.drawable.ic_water_cup_empty);
                water6.setImageResource(R.drawable.ic_water_cup_empty);
                water7.setImageResource(R.drawable.ic_water_cup_empty);
                water8.setImageResource(R.drawable.ic_water_cup_empty);
                water_L.setText("0.5 L");
                break;
            case R.id.water3:
                water1.setImageResource(R.drawable.ic_water_cup_full);
                water2.setImageResource(R.drawable.ic_water_cup_full);
                water3.setImageResource(R.drawable.ic_water_cup_full);
                water4.setImageResource(R.drawable.ic_water_cup_empty_plus);
                water5.setImageResource(R.drawable.ic_water_cup_empty);
                water6.setImageResource(R.drawable.ic_water_cup_empty);
                water7.setImageResource(R.drawable.ic_water_cup_empty);
                water8.setImageResource(R.drawable.ic_water_cup_empty);
                water_L.setText("0.75 L");
                break;
            case R.id.water4:
                water1.setImageResource(R.drawable.ic_water_cup_full);
                water2.setImageResource(R.drawable.ic_water_cup_full);
                water3.setImageResource(R.drawable.ic_water_cup_full);
                water4.setImageResource(R.drawable.ic_water_cup_full);
                water5.setImageResource(R.drawable.ic_water_cup_empty_plus);
                water6.setImageResource(R.drawable.ic_water_cup_empty);
                water7.setImageResource(R.drawable.ic_water_cup_empty);
                water8.setImageResource(R.drawable.ic_water_cup_empty);
                water_L.setText("1.0 L");
                break;
            case R.id.water5:
                water1.setImageResource(R.drawable.ic_water_cup_full);
                water2.setImageResource(R.drawable.ic_water_cup_full);
                water3.setImageResource(R.drawable.ic_water_cup_full);
                water4.setImageResource(R.drawable.ic_water_cup_full);
                water5.setImageResource(R.drawable.ic_water_cup_full);
                water6.setImageResource(R.drawable.ic_water_cup_empty_plus);
                water7.setImageResource(R.drawable.ic_water_cup_empty);
                water8.setImageResource(R.drawable.ic_water_cup_empty);
                water_L.setText("1.25 L");
                break;
            case R.id.water6:
                water1.setImageResource(R.drawable.ic_water_cup_full);
                water2.setImageResource(R.drawable.ic_water_cup_full);
                water3.setImageResource(R.drawable.ic_water_cup_full);
                water4.setImageResource(R.drawable.ic_water_cup_full);
                water5.setImageResource(R.drawable.ic_water_cup_full);
                water6.setImageResource(R.drawable.ic_water_cup_full);
                water7.setImageResource(R.drawable.ic_water_cup_empty_plus);
                water8.setImageResource(R.drawable.ic_water_cup_empty);
                water_L.setText("1.5 L");
                break;
            case R.id.water7:
                water1.setImageResource(R.drawable.ic_water_cup_full);
                water2.setImageResource(R.drawable.ic_water_cup_full);
                water3.setImageResource(R.drawable.ic_water_cup_full);
                water4.setImageResource(R.drawable.ic_water_cup_full);
                water5.setImageResource(R.drawable.ic_water_cup_full);
                water6.setImageResource(R.drawable.ic_water_cup_full);
                water7.setImageResource(R.drawable.ic_water_cup_full);
                water8.setImageResource(R.drawable.ic_water_cup_empty_plus);
                water_L.setText("1.75 L");
                break;
            case R.id.water8:
                water1.setImageResource(R.drawable.ic_water_cup_full);
                water2.setImageResource(R.drawable.ic_water_cup_full);
                water3.setImageResource(R.drawable.ic_water_cup_full);
                water4.setImageResource(R.drawable.ic_water_cup_full);
                water5.setImageResource(R.drawable.ic_water_cup_full);
                water6.setImageResource(R.drawable.ic_water_cup_full);
                water7.setImageResource(R.drawable.ic_water_cup_full);
                water8.setImageResource(R.drawable.ic_water_cup_full);
                water_L.setText("2.0 L");
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
            default:
                break;

        }
    }
    private void addEventListener() {
        imgCalendar.setOnClickListener(this);

        linPoint.setOnClickListener(this);
        linUnit.setOnClickListener(this);

        imgPDF.setOnClickListener(this);

        imgBreakfast.setOnClickListener(this);
        linAddBreakfast.setOnClickListener(this);
        linCheckedBreakfast.setOnClickListener(this);

        imgLunch.setOnClickListener(this);
        linAddLunch.setOnClickListener(this);
        linCheckedLunch.setOnClickListener(this);

        imgDinner.setOnClickListener(this);
        linAddDinner.setOnClickListener(this);
        linCheckedDinner.setOnClickListener(this);

        imgBreakfastSnack.setOnClickListener(this);
        linAddBreakfastSnack.setOnClickListener(this);
        linCheckedBreakfastSnack.setOnClickListener(this);

        imgLunchSnack.setOnClickListener(this);
        linAddLunchSnack.setOnClickListener(this);
        linCheckedLunchSnack.setOnClickListener(this);

        imgDinnerSnack.setOnClickListener(this);
        linAddDinnerSnack.setOnClickListener(this);
        linCheckedDinnerSnack.setOnClickListener(this);

        btnSave.setOnClickListener(this);

        imgSubtractWeight.setOnClickListener(this);
        imgAddWeight.setOnClickListener(this);

        water1.setOnClickListener(this);
        water2.setOnClickListener(this);
        water3.setOnClickListener(this);
        water4.setOnClickListener(this);
        water5.setOnClickListener(this);
        water6.setOnClickListener(this);
        water7.setOnClickListener(this);
        water8.setOnClickListener(this);

        imgMain.setOnClickListener(this);
        imgRecipe.setOnClickListener(this);
        imgSetting.setOnClickListener(this);
        imgHelp.setOnClickListener(this);
    }
    private void setSettings(Date date) {
        String paramDate = new SimpleDateFormat("yyyy-MM-dd").format(date);
        REST rest = MainApplication.getContainer().get(REST.class);
        rest.getInformation(paramDate)
                .enqueue(new Callback<Wrappers.Single<Information>>() {
                    @Override
                    public void onResponse(Call<Wrappers.Single<Information>> call, retrofit2.Response<Wrappers.Single<Information>> response) {
                        if (response.body() != null) {

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

                                setupView(date);
                            }
                            else {
                                Toast.makeText(DailyCaleandarActivity.this, getResources().getString(R.string.offline_text), Toast.LENGTH_LONG).show();
                            }
                        } else {
                            Toast.makeText(DailyCaleandarActivity.this, getResources().getString(R.string.offline_text), Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Wrappers.Single<Information>> call, Throwable t) {

                    }
                });
    }
    private void setupView(Date date) {
        txtDate.setText(getDateString(date));
        txtWeek.setText(getWeekNumber(date));
    }


    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        mCalendar.set(year, month, dayOfMonth);
        mSelectedDate =  mCalendar.getTime();
        setSettings(mSelectedDate);
    }
    public void initView() {
        txtDate = findViewById(R.id.txtDate);
        txtWeek = findViewById(R.id.txtWeek);
        imgCalendar = findViewById(R.id.imgCalendar);

        deltaUnits = findViewById(R.id.deltaUnits);
        deltaWeight = findViewById(R.id.deltaWeight);
        loseAlert = findViewById(R.id.loseAlert);
        txtPoint = findViewById(R.id.txtPoint);
        txtUnit = findViewById(R.id.txtUnit);
        imgBalerinaPoint = findViewById(R.id.imgBalerinaPoint);
        imgBalerinaUnit = findViewById(R.id.imgBalerinaUnit);
        linPoint = findViewById(R.id.linPoint);
        linUnit = findViewById(R.id.linUnit);

        pieChart = findViewById(R.id.pieChart);
        typeDropdown = findViewById(R.id.typeDropdown);
        imgPDF = findViewById(R.id.imgPDF);

        imgBreakfast = findViewById(R.id.imgBreakfast);
        txtTimerBreakFast = findViewById(R.id.txtTimerBreakFast);
        txtBreakfastProtein = findViewById(R.id.txtBreakfastProtein);
        txtBreakfastCarbon = findViewById(R.id.txtBreakfastCarbon);
        txtBreakfastFat = findViewById(R.id.txtBreakfastFat);
        txtBreakfastProteinGram = findViewById(R.id.txtBreakfastProteinGram);
        txtBreakfastCarbonGram = findViewById(R.id.txtBreakfastCarbonGram);
        txtBreakfastFatGram = findViewById(R.id.txtBreakfastFatGram);
        txtPointBreakfast = findViewById(R.id.txtPointBreakfast);
        txtRequirementBreakfast = findViewById(R.id.txtRequirementBreakfast);
        linAddBreakfast = findViewById(R.id.linAddBreakfast);
        linCheckedBreakfast = findViewById(R.id.linCheckedBreakfast);

        imgLunch = findViewById(R.id.imgLunch);
        txtTimerLunch = findViewById(R.id.txtTimerLunch);
        txtLunchProtein = findViewById(R.id.txtLunchProtein);
        txtLunchCarbon = findViewById(R.id.txtLunchCarbon);
        txtLunchFat = findViewById(R.id.txtLunchFat);
        txtLunchProteinGram = findViewById(R.id.txtLunchProteinGram);
        txtLunchCarbonGram = findViewById(R.id.txtLunchCarbonGram);
        txtLunchFatGram = findViewById(R.id.txtLunchFatGram);
        txtPointLunch = findViewById(R.id.txtPointLunch);
        txtRequirementLunch = findViewById(R.id.txtRequirementLunch);
        linAddLunch = findViewById(R.id.linAddLunch);
        linCheckedLunch = findViewById(R.id.linCheckedLunch);

        imgDinner = findViewById(R.id.imgDinner);
        txtTimerDinner = findViewById(R.id.txtTimerDinner);
        txtDinnerProtein = findViewById(R.id.txtDinnerProtein);
        txtDinnerCarbon = findViewById(R.id.txtDinnerCarbon);
        txtDinnerFat = findViewById(R.id.txtDinnerFat);
        txtDinnerProteinGram = findViewById(R.id.txtDinnerProteinGram);
        txtDinnerCarbonGram = findViewById(R.id.txtDinnerCarbonGram);
        txtDinnerFatGram = findViewById(R.id.txtDinnerFatGram);
        txtPointDinner = findViewById(R.id.txtPointDinner);
        txtRequirementDinner = findViewById(R.id.txtRequirementDinner);
        linAddDinner = findViewById(R.id.linAddDinner);
        linCheckedDinner = findViewById(R.id.linCheckedDinner);

        imgBreakfastSnack = findViewById(R.id.imgBreakfastSnack);
        txtTimerBreakfastSnack = findViewById(R.id.txtTimerBreakfastSnack);
        txtBreakfastSnackProtein = findViewById(R.id.txtBreakfastSnackProtein);
        txtBreakfastSnackCarbon = findViewById(R.id.txtBreakfastSnackCarbon);
        txtBreakfastSnackFat = findViewById(R.id.txtBreakfastSnackFat);
        txtBreakfastSnackProteinGram = findViewById(R.id.txtBreakfastSnackProteinGram);
        txtBreakfastSnackCarbonGram = findViewById(R.id.txtBreakfastSnackCarbonGram);
        txtBreakfastSnackFatGram = findViewById(R.id.txtBreakfastSnackFatGram);
        txtPointBreakfastSnack = findViewById(R.id.txtPointBreakfastSnack);
        linAddBreakfastSnack = findViewById(R.id.linAddBreakfastSnack);
        linCheckedBreakfastSnack = findViewById(R.id.linCheckedBreakfastSnack);

        imgLunchSnack = findViewById(R.id.imgLunchSnack);
        txtTimerLunchSnack = findViewById(R.id.txtTimerLunchSnack);
        txtLunchSnackProtein = findViewById(R.id.txtLunchSnackProtein);
        txtLunchSnackCarbon = findViewById(R.id.txtLunchSnackCarbon);
        txtLunchSnackFat = findViewById(R.id.txtLunchSnackFat);
        txtLunchSnackProteinGram = findViewById(R.id.txtLunchSnackProteinGram);
        txtLunchSnackCarbonGram = findViewById(R.id.txtLunchSnackCarbonGram);
        txtLunchSnackFatGram = findViewById(R.id.txtLunchSnackFatGram);
        txtPointLunchSnack = findViewById(R.id.txtPointLunchSnack);
        linAddLunchSnack = findViewById(R.id.linAddLunchSnack);
        linCheckedLunchSnack = findViewById(R.id.linCheckedLunchSnack);

        imgDinnerSnack = findViewById(R.id.imgDinnerSnack);
        txtTimerDinnerSnack = findViewById(R.id.txtTimerDinnerSnack);
        txtDinnerSnackProtein = findViewById(R.id.txtDinnerSnackProtein);
        txtDinnerSnackCarbon = findViewById(R.id.txtDinnerSnackCarbon);
        txtDinnerSnackFat = findViewById(R.id.txtDinnerSnackFat);
        txtDinnerSnackProteinGram = findViewById(R.id.txtDinnerSnackProteinGram);
        txtDinnerSnackCarbonGram = findViewById(R.id.txtDinnerSnackCarbonGram);
        txtDinnerSnackFatGram = findViewById(R.id.txtDinnerSnackFatGram);
        txtPointDinnerSnack = findViewById(R.id.txtPointDinnerSnack);
        linAddDinnerSnack = findViewById(R.id.linAddDinnerSnack);
        linCheckedDinnerSnack = findViewById(R.id.linCheckedDinnerSnack);

        edtWaist = findViewById(R.id.edtWaist);
        edtNeck = findViewById(R.id.edtNeck);
        edtThigh = findViewById(R.id.edtThigh);
        txtResult = findViewById(R.id.txtResult);
        btnSave = findViewById(R.id.btnSave);

        fat = findViewById(R.id.fat);
        txtBFP = findViewById(R.id.txtBFP);

        txtIDW = findViewById(R.id.txtIDW);

        txtBMI = findViewById(R.id.txtBMI);
        txtBMIText = findViewById(R.id.txtBMIText);

        txtGoalWeight = findViewById(R.id.txtGoalWeight);
        txtCurrentWeight = findViewById(R.id.txtCurrentWeight);
        imgSubtractWeight = findViewById(R.id.imgSubtractWeight);
        imgAddWeight = findViewById(R.id.imgAddWeight);

        water_L = findViewById(R.id.water_L);
        water1 = findViewById(R.id.water1);
        water2 = findViewById(R.id.water2);
        water3 = findViewById(R.id.water3);
        water4 = findViewById(R.id.water4);
        water5 = findViewById(R.id.water5);
        water6 = findViewById(R.id.water6);
        water7 = findViewById(R.id.water7);
        water8 = findViewById(R.id.water8);

        meat_amount = findViewById(R.id.meat_amount);
        milk_amount = findViewById(R.id.milk_amount);
        bean_amount = findViewById(R.id.bean_amount);
        noodle_amount = findViewById(R.id.noodle_amount);
        meat_warning = findViewById(R.id.meat_warning);
        milk_warning = findViewById(R.id.milk_warning);
        bean_warning = findViewById(R.id.bean_warning);
        noodle_warning = findViewById(R.id.noodle_warning);

        apple_txt = findViewById(R.id.apple_txt);
        fruit1 = findViewById(R.id.fruit1);
        fruit2 = findViewById(R.id.fruit2);
        fruit3 = findViewById(R.id.fruit3);

        oilImage = findViewById(R.id.oilImage);
        oilImageFailure = findViewById(R.id.oilImageFailure);
        foodImage = findViewById(R.id.foodImage);
        foodImageFailure = findViewById(R.id.foodImageFailure);

        graph = findViewById(R.id.graph);
        txtInitialWeight = findViewById(R.id.txtInitialWeight);
        txtIdealWeight = findViewById(R.id.txtIdealWeight);
        txtTodayWeight = findViewById(R.id.txtTodayWeight);
        txtWeightDiff = findViewById(R.id.txtWeightDiff);

        imgMain = findViewById(R.id.imgMain);
        imgRecipe = findViewById(R.id.imgRecipe);
        imgSetting = findViewById(R.id.imgSetting);
        imgHelp = findViewById(R.id.imgHelp);
    }
    private void checkAvailable() {
        REST rest = MainApplication.getContainer().get(REST.class);
        rest.checkAvailable()
                .enqueue(new Callback<Boolean>() {
                    @Override
                    public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                        Boolean flag = response.body();
                        if (flag) {
                            getProfile();
                        } else {
                            showWarning();
                        }
                    }

                    @Override
                    public void onFailure(Call<Boolean> call, Throwable t) {

                    }
                });
    }
    private void getProfile() {
        REST rest = MainApplication.getContainer().get(REST.class);
        rest.profileShow()
                .enqueue(new Callback<Wrappers.Single<User>>() {
                    @Override
                    public void onResponse(Call<Wrappers.Single<User>> call, retrofit2.Response<Wrappers.Single<User>> response) {
                        User user = response.body().data;
                        Global.user_id = user.id;
                        PersonalData.getInstance().setMembership(user.type);
                        try {
                            PersonalData.getInstance().setStart_date(new SimpleDateFormat("yyyy-MM-dd").parse(user.created_at));
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        Date date = Calendar.getInstance().getTime();
                        setSettings(date);
                    }

                    @Override
                    public void onFailure(Call<Wrappers.Single<User>> call, Throwable t) {

                    }
                });
    }
    private void showWarning() {
        new AlertDialog.Builder(DailyCaleandarActivity.this)
                .setTitle("Warning!")
                .setMessage("Your usage time over full. please make payment for continue using!")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {
                        Intent intent = new Intent(DailyCaleandarActivity.this, TrialNotifyActivity.class);
                        startActivity(intent);
                        finish();
                    }})
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Prefs.clear();
                        Intent intent = new Intent(DailyCaleandarActivity.this, LoginActivity.class);
                        intent.putExtra("activity", "goal");
                        startActivity(intent);
                        finish();
                    }
                }).show();
    }
    private String getWeekNumber(Date date) {
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
    private String getDateString(Date date) {
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
    private Notification getNotification(String title, String content) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder( this, default_notification_channel_id ) ;
        builder.setContentTitle(title) ;
        builder.setContentText(content) ;
        builder.setSmallIcon(R.drawable.ic_logo ) ;
        builder.setAutoCancel( true ) ;
        builder.setChannelId( NOTIFICATION_CHANNEL_ID ) ;
        return builder.build() ;
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
    private void showDatePickerDlg() {
        DatePickerDialog datePickerdlg = new DatePickerDialog(this, this,
                mCalendar.get(Calendar.YEAR),
                mCalendar.get(Calendar.MONTH),
                mCalendar.get(Calendar.DAY_OF_MONTH));
        datePickerdlg.show();
    }
    public void fruit_format() {
        fruit1.setImageResource(R.drawable.ic_apple_plus);
        fruit2.setImageResource(R.drawable.ic_apple_idle);
        fruit3.setImageResource(R.drawable.ic_apple_idle);
        apple_txt.setText("0 φ.");
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
        water_L.setText("0 L");
    }
    private void showMeal_List(){
        Intent intent = new Intent(getBaseContext(), MealListActivity.class);
        intent.putExtra("date", new SimpleDateFormat("yyyy-MM-dd").format(mSelectedDate));
        startActivity(intent);
    }
    private void searchFood(){
        Intent intent = new Intent(getBaseContext(), SearchFoodActivity.class);
        startActivity(intent);
    }
    @SuppressLint("SetTextI18n")
    private void saveSetting() {
        final String strWaist = edtWaist.getText().toString();
        final String strNeck = edtNeck.getText().toString();
        final String strThigh = edtThigh.getText().toString();

        if(strWaist.equals("")){
            Toast.makeText(this, "Input Waist", Toast.LENGTH_SHORT).show();
            return;
        }else if(strNeck.equals("")){
            Toast.makeText(this, "Input Neck", Toast.LENGTH_SHORT).show();
            return;
        }else if(strThigh.equals("")){
            Toast.makeText(this, "Input Thigh", Toast.LENGTH_SHORT).show();
            return;
        }
        PersonalData.getInstance().setWaist_perimeter(Float.parseFloat(strWaist));
        PersonalData.getInstance().setThigh_perimeter(Float.parseFloat(strThigh));
        PersonalData.getInstance().setNeck_perimeter(Float.parseFloat(strNeck));

        float bfp = PersonalData.getInstance().getBFP();
        if(bfp < 0){
            txtBFP.setText("0%");
            txtBFP.setTextSize(18);
            findViewById(R.id.fat).setVisibility(View.GONE);
        }else {
            findViewById(R.id.fat).setVisibility(View.VISIBLE);
            txtResult.setText(String.format(Locale.US,"%d", Math.round(bfp)) + " %");
            txtBFP.setText(String.format(Locale.US,"%d", Math.round(bfp)) + " %");
        }
    }
    void SetTimer(){
        String meal="";

        if(meal.equals("ΠΡΩΙΝΟ"))
            Global.meal_num = 1;
        else if(meal.equals("ΜΕΣΗΜΕΡΙΑΝΟ"))
            Global.meal_num = 2;
        else if(meal.equals("ΒΡΑΔΙΝΟ"))
            Global.meal_num = 3;

        if(PersonalData.getInstance().getGoal() == Goal.LOSE)
        {
            if (breakfast_start == false) {
                txtTimerBreakFast.setText("10:00");
            }
            if (lunch_start == false) {
                txtTimerLunch.setText("10:00");
            }
            if (dinner_start == false) {
                txtTimerDinner.setText("10:00");
            }
            if (breakfast_snack_start == false) {
                txtTimerBreakfastSnack.setText("05:00");
            }
            if (lunch_snack_start == false) {
                txtTimerLunchSnack.setText("05:00");
            }
            if (dinner_snack_start == false) {
                txtTimerDinnerSnack.setText("05:00");
            }

            if(meal.equals("ΠΡΩΙΝΟ") && breakfast_start == false)
                txtTimerBreakFast.setText("15:00");
            else if(meal.equals("ΜΕΣΗΜΕΡΙΑΝΟ") && lunch_start == false)
                txtTimerLunch.setText("15:00");
            else if(meal.equals("ΒΡΑΔΙΝΟ") && dinner_start == false)
                txtTimerDinner.setText("15:00");
        }
        else if(PersonalData.getInstance().getGoal() == Goal.GAIN)
        {
            if (breakfast_start == false) {
                txtTimerBreakFast.setText("20:00");
            }
            if (lunch_start == false) {
                txtTimerLunch.setText("30:00");
            }
            if (dinner_start == false) {
                txtTimerDinner.setText("20:00");
            }
            if (breakfast_snack_start == false) {
                txtTimerBreakfastSnack.setText("13:00");
            }
            if (lunch_snack_start == false) {
                txtTimerLunchSnack.setText("13:00");
            }
            if (dinner_snack_start == false) {
                txtTimerDinnerSnack.setText("13:00");
            }
        }
        else
        {
            if (breakfast_start == false) {
                txtTimerBreakFast.setText("10:00");
            }
            if (lunch_start == false) {
                txtTimerLunch.setText("10:00");
            }
            if (dinner_start == false) {
                txtTimerDinner.setText("10:00");
            }
            if (breakfast_snack_start == false) {
                txtTimerBreakfastSnack.setText("05:00");
            }
            if (lunch_snack_start == false) {
                txtTimerLunchSnack.setText("05:00");
            }
            if (dinner_snack_start == false) {
                txtTimerDinnerSnack.setText("05:00");
            }

            if(meal.equals("ΠΡΩΙΝΟ") && breakfast_start == false)
                txtTimerBreakFast.setText("15:00");
            else if(meal.equals("ΜΕΣΗΜΕΡΙΑΝΟ") && lunch_start == false)
                txtTimerLunch.setText("15:00");
            else if(meal.equals("ΒΡΑΔΙΝΟ") && dinner_start == false)
                txtTimerDinner.setText("15:00");
        }
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
    public void layoutToImage(Date week_date) { }
    public void imageToPDF() throws FileNotFoundException {
        String dirpath = "";
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
}