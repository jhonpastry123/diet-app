package com.diet.trinity.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import com.diet.trinity.Adapter.CustomMealAdapter;
import com.diet.trinity.MainApplication;
import com.diet.trinity.MyNotificationPublisher;
import com.diet.trinity.R;
import com.diet.trinity.Utility.Global;
import com.diet.trinity.data.api.REST;
import com.diet.trinity.data.common.DietMode;
import com.diet.trinity.data.common.Gender;
import com.diet.trinity.data.common.Goal;
import com.diet.trinity.data.common.PersonalData;
import com.diet.trinity.data.models.GraphValue;
import com.diet.trinity.data.models.Information;
import com.diet.trinity.data.models.Meal;
import com.diet.trinity.data.models.User;
import com.diet.trinity.data.models.Wrappers;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LegendEntry;
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
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

public class DailyCaleandarActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener, View.OnClickListener {
    public static final String NOTIFICATION_CHANNEL_ID = "10001" ;
    private final static String default_notification_channel_id = "default" ;

    Button barcode;
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
    TextView txtTimerBreakfastSnack, txtBreakfastSnackProtein, txtBreakfastSnackCarbon, txtBreakfastSnackFat, txtBreakfastSnackProteinGram, txtBreakfastSnackCarbonGram, txtBreakfastSnackFatGram, txtPointBreakfastSnack;
    LinearLayout linAddBreakfastSnack, linCheckedBreakfastSnack;
    // LunchSnack part
    ImageView imgLunchSnack;
    TextView txtTimerLunchSnack, txtLunchSnackProtein, txtLunchSnackCarbon, txtLunchSnackFat, txtLunchSnackProteinGram, txtLunchSnackCarbonGram, txtLunchSnackFatGram, txtPointLunchSnack;
    LinearLayout linAddLunchSnack, linCheckedLunchSnack;
    // DinnerSnack part
    ImageView imgDinnerSnack;
    TextView txtTimerDinnerSnack, txtDinnerSnackProtein, txtDinnerSnackCarbon, txtDinnerSnackFat, txtDinnerSnackProteinGram, txtDinnerSnackCarbonGram, txtDinnerSnackFatGram, txtPointDinnerSnack;
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
    ImageView imgSubtractWeight, imgAddWeight, weight_save;
    /* Eighth Block Items */
    /* Ninth Block Items */
    TextView water_L;
    ImageView water1, water2, water3, water4, water5, water6, water7, water8, water_save;
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

    Date[] weekly_dates;

    int iWater = 0;

    //-------pie chart---------//
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

    ProgressDialog mProgressDialog;

    //------ graph --------//
    LineGraphSeries<DataPoint> series;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daily_caleandar);
        ActivityCompat.requestPermissions(DailyCaleandarActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, PackageManager.PERMISSION_GRANTED);
        initView();
        checkAvailable();
        addEventListener();
        weekly_dates = new Date[1];
        weekly_dates[0] = mSelectedDate;

        scheduleNotification(getNotification( "ΠΡΩΙΝΟ ΓΕΥΜΑ","το πρωινό είναι το πιο σημαντικό γεύμα της ημέρας" ) , 9 );
        scheduleNotification(getNotification( "μεσημεριανό","για μια πιο υγιεινή ζωή μειώστε το αλάτι και τη ζάχαρη στα γεύματά σας" ) , 12 );
        scheduleNotification(getNotification( "ΩΡΑ ΓΙΑ ΝΕΡΟ!","Παρακαλώ, πιείτε νερό!" ) , 13 );
        scheduleNotification(getNotification( "ΔΕΙΠΝΟ","Δύο ώρες πριν από τον ύπνο δεν τρώμε μεγάλα γεύματα!" ) , 20 );
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        IntentResult intentResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        // if the intentResult is null then
        // toast a message as "cancelled"
        if (intentResult != null) {
            if (intentResult.getContents() == null) {
                Toast.makeText(getBaseContext(), "Cancelled", Toast.LENGTH_SHORT).show();
            } else {
                // if the intentResult is not null we'll set
                // the content and format of scan message
                Log.e("Contents", intentResult.getContents());
                Toast.makeText(getBaseContext(), "Barcode : "+intentResult.getContents(), Toast.LENGTH_SHORT).show();
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.barcode:
                IntentIntegrator intentIntegrator = new IntentIntegrator(this);
                intentIntegrator.setPrompt("Scan a barcode or QR Code");
                intentIntegrator.setOrientationLocked(true);
                intentIntegrator.initiateScan();
                break;
            case R.id.imgCalendar:
                showDatePickerDlg();
                break;
            case R.id.linPoint:
                linUnit.setAlpha(0.5f);
                imgBalerinaUnit.setVisibility(View.INVISIBLE);

                PersonalData.getInstance().setDietMode(DietMode.POINT);
                linPoint.setAlpha(1.0f);
                imgBalerinaPoint.setVisibility(View.VISIBLE);
                save_dietmode();
                initMeal(mSelectedDate);
                break;
            case R.id.linUnit:
                linPoint.setAlpha(0.5f);
                imgBalerinaPoint.setVisibility(View.INVISIBLE);

                PersonalData.getInstance().setDietMode(DietMode.UNIT);
                linUnit.setAlpha(1.0f);
                imgBalerinaUnit.setVisibility(View.VISIBLE);
                save_dietmode();
                initMeal(mSelectedDate);
                break;
            case R.id.imgPDF:
                mProgressDialog = new ProgressDialog(DailyCaleandarActivity.this);
                mProgressDialog.setTitle("Loading...");
                mProgressDialog.setIndeterminate(false);
                mProgressDialog.setCancelable(false);
                mProgressDialog.show();

                for(int i=0; i<weekly_dates.length;i++) {
                    layoutToImage(weekly_dates[i]);
                }
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        imageToPDF();
                        for(int i=0; i<weekly_dates.length;i++) {
                            deleteImage(weekly_dates[i]);
                        }
                        mProgressDialog.dismiss();
                    }
                }, 20000);

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
                calculate();
                break;
            case R.id.imgSubtractWeight:
                txtCurrentWeight.setText(String.format(Locale.US, "%.1f", Float.parseFloat(txtCurrentWeight.getText().toString()) - 0.1));
                break;
            case R.id.imgAddWeight:
                txtCurrentWeight.setText(String.format(Locale.US, "%.1f", Float.parseFloat(txtCurrentWeight.getText().toString()) + 0.1));
                break;
            case R.id.weight_save:
                save_weight();
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
                iWater = 1;
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
                iWater = 2;
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
                iWater = 3;
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
                iWater = 4;
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
                iWater = 5;
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
                iWater = 6;
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
                iWater = 7;
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
                iWater = 8;
                break;
            case R.id.water_save:
                save_water();
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
    private void save_dietmode() {
        int dietmode = PersonalData.getInstance().getDietMode().ordinal();

        REST rest = MainApplication.getContainer().get(REST.class);
        rest.SaveDietMode(dietmode)
                .enqueue(new Callback<Boolean>() {
                    @Override
                    public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                        Boolean result = response.body();
                    }

                    @Override
                    public void onFailure(Call<Boolean> call, Throwable t) {

                    }
                });

    }
    private void save_weight() {
        float current_weight = Float.parseFloat(txtCurrentWeight.getText().toString());
        REST rest = MainApplication.getContainer().get(REST.class);
        rest.SaveWeight(current_weight)
                .enqueue(new Callback<Boolean>() {
                    @Override
                    public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                        Boolean result = response.body();
                        if (result) {
                            Toast.makeText(DailyCaleandarActivity.this, "Το βάρος σας ενημέρωθηκε!", Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Boolean> call, Throwable t) {

                    }
                });

    }
    private void save_water() {
        REST rest = MainApplication.getContainer().get(REST.class);
        rest.SaveWater(iWater)
                .enqueue(new Callback<Boolean>() {
                    @Override
                    public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                        Boolean result = response.body();
                        if (result) {
                            Toast.makeText(DailyCaleandarActivity.this, "Το νερό που ήπιατε ενημερώθηκε!", Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Boolean> call, Throwable t) {

                    }
                });
    }
    private void addEventListener() {
        barcode.setOnClickListener(this);
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
        weight_save.setOnClickListener(this);

        water1.setOnClickListener(this);
        water2.setOnClickListener(this);
        water3.setOnClickListener(this);
        water4.setOnClickListener(this);
        water5.setOnClickListener(this);
        water6.setOnClickListener(this);
        water7.setOnClickListener(this);
        water8.setOnClickListener(this);
        water_save.setOnClickListener(this);

        imgMain.setOnClickListener(this);
        imgRecipe.setOnClickListener(this);
        imgSetting.setOnClickListener(this);
        imgHelp.setOnClickListener(this);

        typeDropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
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
    private void setSettings(Date date) {
        mProgressDialog = new ProgressDialog(DailyCaleandarActivity.this);
        mProgressDialog.setTitle("Loading...");
        mProgressDialog.setIndeterminate(false);
        mProgressDialog.show();

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
                                iWater = information.water;

                                setupView(date);
                            }
                            else {
                                Toast.makeText(DailyCaleandarActivity.this, getResources().getString(R.string.offline_text), Toast.LENGTH_LONG).show();
                                mProgressDialog.dismiss();
                            }
                        } else {
                            Toast.makeText(DailyCaleandarActivity.this, getResources().getString(R.string.offline_text), Toast.LENGTH_LONG).show();
                            mProgressDialog.dismiss();
                        }
                    }

                    @Override
                    public void onFailure(Call<Wrappers.Single<Information>> call, Throwable t) {
                        mProgressDialog.dismiss();
                    }
                });
    }
    private void setupView(Date date) {
        txtDate.setText(getDateString(date));
        txtWeek.setText(getWeekNumber(date));
        float bfp = PersonalData.getInstance().getBFP();
        float idw = (float) (Math.pow(PersonalData.getInstance().getHeight(), 2) * 18.5 / 10000);
        float idw_max = (float) (Math.pow(PersonalData.getInstance().getHeight(), 2) * 24.9 / 10000);
        float bmi = PersonalData.getInstance().getBMI().getValue();
        String bmiState = PersonalData.getInstance().getBMI().getState();
        if(bfp < 0){
            txtBFP.setText("0%");
            txtBFP.setTextSize(18);
            fat.setVisibility(View.GONE);
        }else {
            fat.setVisibility(View.VISIBLE);
            txtResult.setText(String.format(Locale.US,"%d", Math.round(bfp)) + " %");
            txtBFP.setText(String.format(Locale.US,"%d", Math.round(bfp)) + " %");
        }
        txtIDW.setText(String.format(Locale.US, "%.1f ~ %.1f kg", idw, idw_max));
        txtBMI.setText(String.format(Locale.US, "%.1f", bmi));
        txtBMIText.setText(bmiState);
        txtCurrentWeight.setText(String.format(Locale.US, "%.1f", PersonalData.getInstance().getWeight()));

        txtInitialWeight.setText(String.format(Locale.US, "%.1f kg", PersonalData.getInstance().getInitial_weight()));
        double mIdealWeight = Math.pow(PersonalData.getInstance().getHeight(), 2) * 18.5 / 10000;
        double mIdealMaxWeight =  Math.pow(PersonalData.getInstance().getHeight(), 2) * 24.9 / 10000;
        txtIdealWeight.setText(String.format(Locale.US, "%.1f kg - %.1f kg", mIdealWeight, mIdealMaxWeight));
        txtTodayWeight.setText(String.format(Locale.US, "%.1f kg", PersonalData.getInstance().getWeight()));
        txtWeightDiff.setText(String.format(Locale.US, "%.1f kg", PersonalData.getInstance().getWeight() - PersonalData.getInstance().getInitial_weight()));
        if(PersonalData.getInstance().getDietMode() == DietMode.POINT){
            linPoint.performClick();
        }
        else{
            linUnit.performClick();
        }
        SetTimer();
        initGraph();
        initMeal(date);
    }
    private void initWater() {
        switch (iWater) {
            case 0:
                water1.setImageResource(R.drawable.ic_water_cup_empty_plus);
                water2.setImageResource(R.drawable.ic_water_cup_empty);
                water3.setImageResource(R.drawable.ic_water_cup_empty);
                water4.setImageResource(R.drawable.ic_water_cup_empty);
                water5.setImageResource(R.drawable.ic_water_cup_empty);
                water6.setImageResource(R.drawable.ic_water_cup_empty);
                water7.setImageResource(R.drawable.ic_water_cup_empty);
                water8.setImageResource(R.drawable.ic_water_cup_empty);
                water_L.setText("0 L");
                break;
            case 1:
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
            case 2:
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
            case 3:
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
            case 4:
                water1.setImageResource(R.drawable.ic_water_cup_full);
                water2.setImageResource(R.drawable.ic_water_cup_full);
                water3.setImageResource(R.drawable.ic_water_cup_full);
                water4.setImageResource(R.drawable.ic_water_cup_full);
                water5.setImageResource(R.drawable.ic_water_cup_empty_plus);
                water6.setImageResource(R.drawable.ic_water_cup_empty);
                water7.setImageResource(R.drawable.ic_water_cup_empty);
                water8.setImageResource(R.drawable.ic_water_cup_empty);
                water_L.setText("1 L");
                break;
            case 5:
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
            case 6:
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
            case 7:
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
            case 8:
                water1.setImageResource(R.drawable.ic_water_cup_full);
                water2.setImageResource(R.drawable.ic_water_cup_full);
                water3.setImageResource(R.drawable.ic_water_cup_full);
                water4.setImageResource(R.drawable.ic_water_cup_full);
                water5.setImageResource(R.drawable.ic_water_cup_full);
                water6.setImageResource(R.drawable.ic_water_cup_full);
                water7.setImageResource(R.drawable.ic_water_cup_full);
                water8.setImageResource(R.drawable.ic_water_cup_full);
                water_L.setText("2 L");
                break;
        }
    }
    private void initMealFormat() {
        linAddBreakfast.setVisibility(View.VISIBLE);
        linAddLunch.setVisibility(View.VISIBLE);
        linAddDinner.setVisibility(View.VISIBLE);
        linAddBreakfastSnack.setVisibility(View.VISIBLE);
        linAddLunchSnack.setVisibility(View.VISIBLE);
        linAddDinnerSnack.setVisibility(View.VISIBLE);

        linCheckedBreakfast.setVisibility(View.GONE);
        linCheckedLunch.setVisibility(View.GONE);
        linCheckedDinner.setVisibility(View.GONE);
        linCheckedBreakfastSnack.setVisibility(View.GONE);
        linCheckedLunchSnack.setVisibility(View.GONE);
        linCheckedDinnerSnack.setVisibility(View.GONE);

        txtRequirementBreakfast.setVisibility(View.GONE);
        txtRequirementLunch.setVisibility(View.GONE);
        txtRequirementDinner.setVisibility(View.GONE);

        txtBreakfastCarbon.setText("");
        txtBreakfastProtein.setText("");
        txtBreakfastFat.setText("");
        txtBreakfastCarbonGram.setText("");
        txtBreakfastProteinGram.setText("");
        txtBreakfastFatGram.setText("");
        txtPointBreakfast.setText("");

        txtLunchCarbon.setText("");
        txtLunchProtein.setText("");
        txtLunchFat.setText("");
        txtLunchCarbonGram.setText("");
        txtLunchProteinGram.setText("");
        txtLunchFatGram.setText("");
        txtPointLunch.setText("");

        txtDinnerCarbon.setText("");
        txtDinnerProtein.setText("");
        txtDinnerFat.setText("");
        txtDinnerCarbonGram.setText("");
        txtDinnerProteinGram.setText("");
        txtDinnerFatGram.setText("");
        txtPointDinner.setText("");

        txtBreakfastSnackCarbon.setText("");
        txtBreakfastSnackProtein.setText("");
        txtBreakfastSnackFat.setText("");
        txtBreakfastSnackCarbonGram.setText("");
        txtBreakfastSnackProteinGram.setText("");
        txtBreakfastSnackFatGram.setText("");
        txtPointBreakfastSnack.setText("");

        txtBreakfastSnackCarbon.setText("");
        txtBreakfastSnackProtein.setText("");
        txtBreakfastSnackFat.setText("");
        txtBreakfastSnackCarbonGram.setText("");
        txtBreakfastSnackProteinGram.setText("");
        txtBreakfastSnackFatGram.setText("");
        txtPointBreakfastSnack.setText("");

        txtLunchSnackCarbon.setText("");
        txtLunchSnackProtein.setText("");
        txtLunchSnackFat.setText("");
        txtLunchSnackCarbonGram.setText("");
        txtLunchSnackProteinGram.setText("");
        txtLunchSnackFatGram.setText("");
        txtPointLunchSnack.setText("");

        txtDinnerSnackCarbon.setText("");
        txtDinnerSnackProtein.setText("");
        txtDinnerSnackFat.setText("");
        txtDinnerSnackCarbonGram.setText("");
        txtDinnerSnackProteinGram.setText("");
        txtDinnerSnackFatGram.setText("");
        txtPointDinnerSnack.setText("");

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

        Global.pasta_num = 0;
        Global.legumes_num = 0;
        Global.oily_num = 0;
        Global.junk_img_num = 0;
        Global.fruit_num = 0;
        Global.meat_num = 0;
        Global.oily_img_num = 0;

        Global.bCarbon = false;
        Global.bProtein = false;
        Global.bFat = false;

        Global.lCarbon = false;
        Global.lProtein = false;
        Global.lFat = false;

        Global.dCarbon = false;
        Global.dProtein = false;
        Global.dFat = false;

    }
    private void initMeal(Date date) {
        String paramDate = new SimpleDateFormat("yyyy-MM-dd").format(date);
        REST rest = MainApplication.getContainer().get(REST.class);
        rest.MealsByCategory(paramDate, 0)
                            .enqueue(new Callback<Wrappers.Collection<Meal>>() {
                    @SuppressLint({"SetTextI18n", "DefaultLocale"})
                    @Override
                    public void onResponse(Call<Wrappers.Collection<Meal>> call, Response<Wrappers.Collection<Meal>> response) {
                        List<Meal> meals = response.body().data;
                        initMealFormat();
                        mealBoxFormat(meals);
                        pieChartFormat();
                        warning_format();
                        fruit_format();
                        unit_point_format();
                        initWater();
                        mProgressDialog.dismiss();
                    }

                    @Override
                    public void onFailure(Call<Wrappers.Collection<Meal>> call, Throwable t) {
                        mProgressDialog.dismiss();
                    }
                });
    }
    private void unit_point_format() {
        float daily_units = 0;
        float daily_points = 0 ;
        float weekly_units = 0;
        float weekly_points = 0;
        float next_units = 0;
        float next_points = 0;
        float weight = 0;
        float weight_average = 0;

        weekly_points = PersonalData.getInstance().getPoints();
        weekly_units = PersonalData.getInstance().getUnits();
        daily_points = Global.morning_points + Global.lunch_points + Global.dinner_points + Global.snack_morning_points + Global.snack_lunch_points + Global.snack_dinner_points;
        daily_units = Global.morning_units + Global.lunch_units + Global.dinner_units + Global.snack_morning_units + Global.snack_lunch_units + Global.snack_dinner_units;
        next_points = weekly_points - daily_points;
        next_units = weekly_units - daily_units;

        txtPoint.setText(String.format(Locale.US, "%.1f", weekly_points));
        txtUnit.setText(String.format(Locale.US, "%.1f", weekly_units));
        if (PersonalData.getInstance().getDietMode() == DietMode.POINT) {
            deltaUnits.setText(String.format(Locale.US, "%.1f", next_points));
        } else {
            deltaUnits.setText(String.format(Locale.US, "%.1f", next_units));
        }

        float delta_weight = weight - weight_average;

        if(delta_weight<0) {
            deltaWeight.setText(delta_weight + "");
            loseAlert.setVisibility(View.VISIBLE);
        }
        else {
            deltaWeight.setText("--");
            loseAlert.setVisibility(View.INVISIBLE);
        }
    }
    private void warning_format() {
        meat_amount.setText(Global.meat_num+"");
        milk_amount.setText(Global.oily_num+"");
        bean_amount.setText(Global.legumes_num+"");
        noodle_amount.setText(Global.pasta_num+"");

        if(Global.meat_num>3)
        {
            meat_warning.setVisibility(View.VISIBLE);
        }
        else
        {
            meat_warning.setVisibility(View.GONE);
        }

        if(Global.oily_num>2)
        {
            milk_warning.setVisibility(View.VISIBLE);
        }
        else
        {
            milk_warning.setVisibility(View.GONE);
        }

        if(Global.legumes_num>1)
        {
            bean_warning.setVisibility(View.VISIBLE);
        }
        else
        {
            bean_warning.setVisibility(View.GONE);
        }

        if(Global.pasta_num>1)
        {
            noodle_warning.setVisibility(View.VISIBLE);
        }
        else
        {
            noodle_warning.setVisibility(View.GONE);
        }

        if(Global.oily_img_num > 0) {
            oilImage.setVisibility(View.VISIBLE);
            oilImageFailure.setVisibility(View.GONE);
        }
        else {
            oilImage.setVisibility(View.GONE);
            oilImageFailure.setVisibility(View.VISIBLE);
        }

        if(Global.junk_img_num > 0) {
            foodImage.setVisibility(View.GONE);
            foodImageFailure.setVisibility(View.VISIBLE);
        }
        else {
            foodImage.setVisibility(View.VISIBLE);
            foodImageFailure.setVisibility(View.GONE);
        }
    }
    public void fruit_format() {
        switch (Global.fruit_num) {
            case 0:
                fruit1.setImageResource(R.drawable.ic_apple_idle);
                fruit2.setImageResource(R.drawable.ic_apple_idle);
                fruit3.setImageResource(R.drawable.ic_apple_idle);
                apple_txt.setText("0 φ.");
                break;
            case 1:
                fruit1.setImageResource(R.drawable.ic_apple_eaten);
                fruit2.setImageResource(R.drawable.ic_apple_idle);
                fruit3.setImageResource(R.drawable.ic_apple_idle);
                apple_txt.setText("1 φ.");
                break;
            case 2:
                fruit1.setImageResource(R.drawable.ic_apple_eaten);
                fruit2.setImageResource(R.drawable.ic_apple_eaten);
                fruit3.setImageResource(R.drawable.ic_apple_idle);
                apple_txt.setText("2 φ.");
                break;
            default:
                fruit1.setImageResource(R.drawable.ic_apple_eaten);
                fruit2.setImageResource(R.drawable.ic_apple_eaten);
                fruit3.setImageResource(R.drawable.ic_apple_eaten);
                apple_txt.setText(Global.fruit_num + " φ.");
                break;
        }
    }
    private void mealBoxFormat(List<Meal> meals) {
        Boolean bf = false, lu = false, di = false, bfs = false, lus = false, dis = false;
        for (Meal meal: meals) {
            switch (meal.timing_id) {
                case 1:
                    Global.morning_carbon += meal.carbon;
                    Global.morning_protein += meal.protein;
                    Global.morning_fat += meal.fat;
                    Global.morning_points += meal.points;
                    Global.morning_units += meal.units;
                    Global.morning_total += meal.carbon * 4 + meal.protein * 4 + meal.fat * 9;

                    if (meal.flag_carbon == 1) Global.bCarbon = true;
                    if (meal.flag_protein == 1) Global.bProtein = true;
                    if (meal.flag_fat == 1) Global.bFat = true;

                    linCheckedBreakfast.setVisibility(View.VISIBLE);
                    txtRequirementBreakfast.setVisibility(View.VISIBLE);
                    linAddBreakfast.setVisibility(View.GONE);
                    bf = true;
                    break;
                case 2:
                    Global.lunch_carbon += meal.carbon;
                    Global.lunch_protein += meal.protein;
                    Global.lunch_fat += meal.fat;
                    Global.lunch_points += meal.points;
                    Global.lunch_units += meal.units;
                    Global.lunch_total += meal.carbon * 4 + meal.protein * 4 + meal.fat * 9;

                    if (meal.flag_carbon == 1) Global.lCarbon = true;
                    if (meal.flag_protein == 1) Global.lProtein = true;
                    if (meal.flag_fat == 1) Global.lFat = true;

                    linCheckedLunch.setVisibility(View.VISIBLE);
                    txtRequirementLunch.setVisibility(View.VISIBLE);
                    linAddLunch.setVisibility(View.GONE);
                    lu = true;
                    break;
                case 3:
                    Global.dinner_carbon += meal.carbon;
                    Global.dinner_protein += meal.protein;
                    Global.dinner_fat += meal.fat;
                    Global.dinner_points += meal.points;
                    Global.dinner_units += meal.units;
                    Global.dinner_total += meal.carbon * 4 + meal.protein * 4 + meal.fat * 9;

                    if (meal.flag_carbon == 1) Global.dCarbon = true;
                    if (meal.flag_protein == 1) Global.dProtein = true;
                    if (meal.flag_fat == 1) Global.dFat = true;

                    linCheckedDinner.setVisibility(View.VISIBLE);
                    txtRequirementDinner.setVisibility(View.VISIBLE);
                    linAddDinner.setVisibility(View.GONE);
                    di = true;
                    break;
                case 4:
                    Global.snack_morning_carbon += meal.carbon;
                    Global.snack_morning_protein += meal.protein;
                    Global.snack_morning_fat += meal.fat;
                    Global.snack_morning_points += meal.points;
                    Global.snack_morning_units += meal.units;
                    Global.snack_morning_total += meal.carbon * 4 + meal.protein * 4 + meal.fat * 9;
                    linCheckedBreakfastSnack.setVisibility(View.VISIBLE);
                    linAddBreakfastSnack.setVisibility(View.GONE);
                    bfs = true;
                    break;
                case 5:
                    Global.snack_lunch_carbon += meal.carbon;
                    Global.snack_lunch_protein += meal.protein;
                    Global.snack_lunch_fat += meal.fat;
                    Global.snack_lunch_points += meal.points;
                    Global.snack_lunch_units += meal.units;
                    Global.snack_lunch_total += meal.carbon * 4 + meal.protein * 4 + meal.fat * 9;
                    linCheckedLunchSnack.setVisibility(View.VISIBLE);
                    linAddLunchSnack.setVisibility(View.GONE);
                    lus = true;
                    break;
                case 6:
                    Global.snack_dinner_carbon += meal.carbon;
                    Global.snack_dinner_protein += meal.protein;
                    Global.snack_dinner_fat += meal.fat;
                    Global.snack_dinner_points += meal.points;
                    Global.snack_dinner_units += meal.units;
                    Global.snack_dinner_total += meal.carbon * 4 + meal.protein * 4 + meal.fat * 9;
                    linCheckedDinnerSnack.setVisibility(View.VISIBLE);
                    linAddDinnerSnack.setVisibility(View.GONE);
                    dis = true;
                    break;
            }
            if (meal.timing_id == Global.meal_num+1) {
                Global.pasta_num += meal.pasta_num;
                Global.legumes_num += meal.legumes_num;
                Global.oily_num += meal.oily_num;
                Global.meat_num += meal.meat_num;
                Global.junk_img_num += meal.junk_img_num;
            }

            Global.oily_img_num += meal.oily_img_num;
            Global.fruit_num += meal.fruit_num;
        }
        if (bf) {
            if (PersonalData.getInstance().getDietMode() == DietMode.POINT) {
                txtPointBreakfast.setText(String.format(Locale.US, "%.1f", Global.morning_points) + " points");
            } else {
                txtPointBreakfast.setText(String.format(Locale.US, "%.1f", Global.morning_units)+" units");
            }

            txtBreakfastCarbon.setText("Πρω : "+String.format("%d", Math.round(Global.morning_carbon * 4/Global.morning_total*100))+"%");
            txtBreakfastProtein.setText("Υδατ : "+String.format("%d", Math.round(Global.morning_protein * 4/Global.morning_total*100))+"%");
            txtBreakfastFat.setText("Λιπ : "+String.format("%d", Math.round(Global.morning_fat * 4/Global.morning_total*100))+"%");
            txtBreakfastCarbonGram.setText("Πρω : "+String.format("%d", Math.round(Global.morning_carbon))+" g");
            txtBreakfastProteinGram.setText("Υδατ : "+String.format("%d", Math.round(Global.morning_protein))+" g");
            txtBreakfastFatGram.setText("Λιπ : "+String.format("%d", Math.round(Global.morning_fat))+" g");
            if (Global.bCarbon && Global.bProtein && Global.bFat) {
                txtRequirementBreakfast.setText("Μπράβο!  Έχετε πετύχει τον ιδανικό συνδυασμό Υδατ/Πρωτ/VitC στο γεύμα σας.");
            }
            else {
                txtRequirementBreakfast.setText("Ο συνδυασμός τροφίμων δεν είναι καλός.  Συνδυάστε Υδατ/Πρωτ/VitC στο γεύμα σας.");
            }
        }
        if (lu) {
            if (PersonalData.getInstance().getDietMode() == DietMode.POINT) {
                txtPointLunch.setText(String.format(Locale.US, "%.1f", Global.lunch_points) + " points");
            } else {
                txtPointLunch.setText(String.format(Locale.US, "%.1f", Global.lunch_units)+" units");
            }

            txtLunchCarbon.setText("Πρω : "+String.format("%d", Math.round(Global.lunch_carbon * 4/Global.lunch_total*100))+"%");
            txtLunchProtein.setText("Υδατ : "+String.format("%d", Math.round(Global.lunch_protein * 4/Global.lunch_total*100))+"%");
            txtLunchFat.setText("Λιπ : "+String.format("%d", Math.round(Global.lunch_fat * 4/Global.lunch_total*100))+"%");
            txtLunchCarbonGram.setText("Πρω : "+String.format("%d", Math.round(Global.lunch_carbon))+" g");
            txtLunchProteinGram.setText("Υδατ : "+String.format("%d", Math.round(Global.lunch_protein))+" g");
            txtLunchFatGram.setText("Λιπ : "+String.format("%d", Math.round(Global.lunch_fat))+" g");
            if (Global.lCarbon && Global.lProtein && Global.lFat) {
                txtRequirementLunch.setText("Μπράβο!  Έχετε πετύχει τον ιδανικό συνδυασμό Υδατ/Πρωτ/VitC στο γεύμα σας.");
            }
            else {
                txtRequirementLunch.setText("Ο συνδυασμός τροφίμων δεν είναι καλός.  Συνδυάστε Υδατ/Πρωτ/VitC στο γεύμα σας.");
            }
        }
        if (di) {
            if (PersonalData.getInstance().getDietMode() == DietMode.POINT) {
                txtPointDinner.setText(String.format(Locale.US, "%.1f", Global.dinner_points) + " points");
            } else {
                txtPointDinner.setText(String.format(Locale.US, "%.1f", Global.dinner_units)+" units");
            }

            txtDinnerCarbon.setText("Πρω : "+String.format("%d", Math.round(Global.dinner_carbon * 4/Global.dinner_total*100))+"%");
            txtDinnerProtein.setText("Υδατ : "+String.format("%d", Math.round(Global.dinner_protein * 4/Global.dinner_total*100))+"%");
            txtDinnerFat.setText("Λιπ : "+String.format("%d", Math.round(Global.dinner_fat * 4/Global.dinner_total*100))+"%");
            txtDinnerCarbonGram.setText("Πρω : "+String.format("%d", Math.round(Global.dinner_carbon))+" g");
            txtDinnerProteinGram.setText("Υδατ : "+String.format("%d", Math.round(Global.dinner_protein))+" g");
            txtDinnerFatGram.setText("Λιπ : "+String.format("%d", Math.round(Global.dinner_fat))+" g");
            if (Global.dCarbon && Global.dProtein && Global.dFat) {
                txtRequirementDinner.setText("Μπράβο!  Έχετε πετύχει τον ιδανικό συνδυασμό Υδατ/Πρωτ/VitC στο γεύμα σας.");
            }
            else {
                txtRequirementDinner.setText("Ο συνδυασμός τροφίμων δεν είναι καλός.  Συνδυάστε Υδατ/Πρωτ/VitC στο γεύμα σας.");
            }
        }
        if (bfs) {
            if (PersonalData.getInstance().getDietMode() == DietMode.POINT) {
                txtPointBreakfastSnack.setText(String.format(Locale.US, "%.1f", Global.snack_morning_points) + " points");
            } else {
                txtPointBreakfastSnack.setText(String.format(Locale.US, "%.1f", Global.snack_morning_units)+" units");
            }

            txtBreakfastSnackCarbon.setText("Πρω : "+String.format("%d", Math.round(Global.snack_morning_carbon * 4/Global.snack_morning_total*100))+"%");
            txtBreakfastSnackProtein.setText("Υδατ : "+String.format("%d", Math.round(Global.snack_morning_protein * 4/Global.snack_morning_total*100))+"%");
            txtBreakfastSnackFat.setText("Λιπ : "+String.format("%d", Math.round(Global.snack_morning_fat * 4/Global.snack_morning_total*100))+"%");
            txtBreakfastSnackCarbonGram.setText("Πρω : "+String.format("%d", Math.round(Global.snack_morning_carbon))+" g");
            txtBreakfastSnackProteinGram.setText("Υδατ : "+String.format("%d", Math.round(Global.snack_morning_protein))+" g");
            txtBreakfastSnackFatGram.setText("Λιπ : "+String.format("%d", Math.round(Global.snack_morning_fat))+" g");
        }
        if (lus) {
            if (PersonalData.getInstance().getDietMode() == DietMode.POINT) {
                txtPointLunchSnack.setText(String.format(Locale.US, "%.1f", Global.snack_lunch_points) + " points");
            } else {
                txtPointLunchSnack.setText(String.format(Locale.US, "%.1f", Global.snack_lunch_units)+" units");
            }

            txtLunchSnackCarbon.setText("Πρω : "+String.format("%d", Math.round(Global.snack_lunch_carbon * 4/Global.snack_lunch_total*100))+"%");
            txtLunchSnackProtein.setText("Υδατ : "+String.format("%d", Math.round(Global.snack_lunch_protein * 4/Global.snack_lunch_total*100))+"%");
            txtLunchSnackFat.setText("Λιπ : "+String.format("%d", Math.round(Global.snack_lunch_fat * 4/Global.snack_lunch_total*100))+"%");
            txtLunchSnackCarbonGram.setText("Πρω : "+String.format("%d", Math.round(Global.snack_lunch_carbon))+" g");
            txtLunchSnackProteinGram.setText("Υδατ : "+String.format("%d", Math.round(Global.snack_lunch_protein))+" g");
            txtLunchSnackFatGram.setText("Λιπ : "+String.format("%d", Math.round(Global.snack_lunch_fat))+" g");
        }
        if (dis) {
            if (PersonalData.getInstance().getDietMode() == DietMode.POINT) {
                txtPointDinnerSnack.setText(String.format(Locale.US, "%.1f", Global.snack_dinner_points) + " points");
            } else {
                txtPointDinnerSnack.setText(String.format(Locale.US, "%.1f", Global.snack_dinner_units)+" units");
            }

            txtDinnerSnackCarbon.setText("Πρω : "+String.format("%d", Math.round(Global.snack_dinner_carbon * 4/Global.snack_dinner_total*100))+"%");
            txtDinnerSnackProtein.setText("Υδατ : "+String.format("%d", Math.round(Global.snack_dinner_protein * 4/Global.snack_dinner_total*100))+"%");
            txtDinnerSnackFat.setText("Λιπ : "+String.format("%d", Math.round(Global.snack_dinner_fat * 4/Global.snack_dinner_total*100))+"%");
            txtDinnerSnackCarbonGram.setText("Πρω : "+String.format("%d", Math.round(Global.snack_dinner_carbon))+" g");
            txtDinnerSnackProteinGram.setText("Υδατ : "+String.format("%d", Math.round(Global.snack_dinner_protein))+" g");
            txtDinnerSnackFatGram.setText("Λιπ : "+String.format("%d", Math.round(Global.snack_dinner_fat))+" g");
        }
    }
    private void initGraph() {
        REST rest = MainApplication.getContainer().get(REST.class);
        rest.GetGraphValues(new SimpleDateFormat("yyyy-MM-dd").format(mSelectedDate))
                .enqueue(new Callback<Wrappers.Collection<GraphValue>>() {
                    @Override
                    public void onResponse(Call<Wrappers.Collection<GraphValue>> call, Response<Wrappers.Collection<GraphValue>> response) {
                        List<GraphValue> values = response.body().data;
                        graph.removeAllSeries();
                        ArrayList<DataPoint> points = new ArrayList<>();
                        float min=1000, max=0;
                        long minX = Calendar.getInstance().getTimeInMillis(), maxX = 0;
                        for (GraphValue value : values){
                            if (value.weight > max) {
                                max = value.weight;
                            }
                            if (value.weight < min) {
                                min = value.weight;
                            }
                            try {
                                Date date = new SimpleDateFormat("yyyy-MM-dd").parse(value.date);
                                points.add(new DataPoint(date, value.weight));

                                if (date.getTime() > maxX) {
                                    maxX = date.getTime();
                                }
                                if (date.getTime() < minX) {
                                    minX = date.getTime();
                                }
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                        }

                        series = new LineGraphSeries<>(points.toArray(new DataPoint[points.size()]));
                        series.setColor(Color.CYAN);
                        series.setDrawDataPoints(true);
                        series.setDataPointsRadius(10);
                        series.setThickness(8);
                        Viewport viewport = graph.getViewport();
                        viewport.setYAxisBoundsManual(true);
                        viewport.setXAxisBoundsManual(true);
                        viewport.scrollToEnd();
                        viewport.setMinX(minX);
                        viewport.setMaxX(maxX);
                        viewport.setMinY(min - 10);
                        viewport.setMaxY(max + 10);

                        viewport.setScrollable(true);
                        viewport.setScalable(true);

                        graph.addSeries(series);
                        graph.getGridLabelRenderer().reloadStyles();
                        graph.getGridLabelRenderer().setLabelFormatter(new DateAsXAxisLabelFormatter(DailyCaleandarActivity.this));
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
                        graph.getGridLabelRenderer().setNumHorizontalLabels(4);
                    }

                    @Override
                    public void onFailure(Call<Wrappers.Collection<GraphValue>> call, Throwable t) {
                        t.printStackTrace();
                    }
                });
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        mCalendar.set(year, month, dayOfMonth);
        mSelectedDate =  mCalendar.getTime();
        setSettings(mSelectedDate);
    }
    public void initView() {
        barcode = findViewById(R.id.barcode);
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
        weight_save = findViewById(R.id.weight_save);

        water_L = findViewById(R.id.water_L);
        water1 = findViewById(R.id.water1);
        water2 = findViewById(R.id.water2);
        water3 = findViewById(R.id.water3);
        water4 = findViewById(R.id.water4);
        water5 = findViewById(R.id.water5);
        water6 = findViewById(R.id.water6);
        water7 = findViewById(R.id.water7);
        water8 = findViewById(R.id.water8);
        water_save = findViewById(R.id.water_save);

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
                .setTitle("Προσοχή!")
                .setMessage("Ο χρόνος δωρεάν λειτουργίας έχει λήξει.  Ξεκλειδώστε τις λειτουργίες μας κάνωντας αγορά το πακέτο που σας αρμόζει.")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setCancelable(false)
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

    private void getEntries() {
        pieEntries = new ArrayList<>();
        float total = Global.morning_total + Global.lunch_total + Global.dinner_total + Global.snack_morning_total + Global.snack_lunch_total + Global.snack_dinner_total;
        pieEntries.add(new PieEntry((Global.morning_protein+Global.lunch_protein+Global.dinner_protein+Global.snack_morning_protein+Global.snack_lunch_protein+Global.snack_dinner_protein) * 4/ total * 100, "Πρω"));
        pieEntries.add(new PieEntry((Global.morning_fat+Global.lunch_fat+Global.dinner_fat+Global.snack_morning_fat+Global.snack_lunch_fat+Global.snack_dinner_fat) * 9 / total * 100, "Λιπ"));
        pieEntries.add(new PieEntry((Global.morning_carbon+Global.lunch_carbon+Global.dinner_carbon+Global.snack_morning_carbon+Global.snack_lunch_carbon+Global.snack_dinner_carbon) * 4/ total * 100, "Υδατ"));
    }
    private void pieChartFormat() {
        if((Global.morning_protein+Global.lunch_protein+Global.dinner_protein)==0 && (Global.morning_fat+Global.lunch_fat+Global.dinner_fat)==0 && (Global.morning_carbon+Global.lunch_carbon+Global.dinner_carbon)==0)
            pieChart.setVisibility(View.GONE);
        else
            pieChart.setVisibility(View.VISIBLE);

        getEntries();
        Legend legend = pieChart.getLegend();
        legend.setEnabled(true);
        List<LegendEntry> entries = new ArrayList<>();

        String[] label = new String[] {"Πρω", "Λιπ", "Υδατ"};
        for (int i = 0; i < pieEntries.size(); i++) {
            LegendEntry entry = new LegendEntry();
            entry.formColor = ColorTemplate.COLORFUL_COLORS[i];
            entry.label = label[i];
            entries.add(entry);
        }
        legend.setCustom(entries);
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
        pieChart.getDescription().setEnabled(false);

        pieDataSet.setXValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);
        IValueFormatter formatter = new IValueFormatter() {
            @Override
            public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
                return Math.round(value) + "";
            }
        };
        pieDataSet.setValueFormatter(formatter);
        pieChart.invalidate();
    }
    private void showMeal_List(){
        Intent intent = new Intent(getBaseContext(), MealListActivity.class);
        intent.putExtra("date", new SimpleDateFormat("yyyy-MM-dd").format(mSelectedDate));
        startActivity(intent);
    }
    private void searchFood(){
        Intent intent = new Intent(getBaseContext(), SearchFoodActivity.class);
        intent.putExtra("date", new SimpleDateFormat("yyyy-MM-dd").format(mSelectedDate));
        startActivity(intent);
    }
    @SuppressLint("SetTextI18n")
    private void calculate() {
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
            txtBFP.setText("0 %");
            txtBFP.setTextSize(18);
            txtResult.setText("0 %");
            findViewById(R.id.fat).setVisibility(View.GONE);
        }else {
            findViewById(R.id.fat).setVisibility(View.VISIBLE);
            txtBFP.setTextSize(18);
            txtResult.setText(String.format(Locale.US,"%d", Math.round(bfp)) + " %");
            txtBFP.setText(String.format(Locale.US,"%d", Math.round(bfp)) + " %");
        }
    }
    void SetTimer(){
        if(PersonalData.getInstance().getGoal() == Goal.GAIN)
        {
            if (!breakfast_start) {
                txtTimerBreakFast.setText("20:00");
            }
            if (!lunch_start) {
                txtTimerLunch.setText("20:00");
            }
            if (!dinner_start) {
                txtTimerDinner.setText("20:00");
            }
            if (!breakfast_snack_start) {
                txtTimerBreakfastSnack.setText("12:00");
            }
            if (!lunch_snack_start) {
                txtTimerLunchSnack.setText("12:00");
            }
            if (!dinner_snack_start) {
                txtTimerDinnerSnack.setText("12:00");
            }

            if(Global.meal_num == 0 && !breakfast_start)
                txtTimerBreakFast.setText("30:00");
            else if(Global.meal_num == 1 && !lunch_start)
                txtTimerLunch.setText("30:00");
            else if(Global.meal_num == 2 && !dinner_start)
                txtTimerDinner.setText("30:00");
        }
        else {
            if (!breakfast_start) {
                txtTimerBreakFast.setText("10:00");
            }
            if (!lunch_start) {
                txtTimerLunch.setText("10:00");
            }
            if (!dinner_start) {
                txtTimerDinner.setText("10:00");
            }
            if (!breakfast_snack_start) {
                txtTimerBreakfastSnack.setText("05:00");
            }
            if (!lunch_snack_start) {
                txtTimerLunchSnack.setText("05:00");
            }
            if (!dinner_snack_start) {
                txtTimerDinnerSnack.setText("05:00");
            }

            if(Global.meal_num == 0 && !breakfast_start)
                txtTimerBreakFast.setText("15:00");
            else if(Global.meal_num == 1 && !lunch_start)
                txtTimerLunch.setText("15:00");
            else if(Global.meal_num == 2 && !dinner_start)
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
    public void layoutToImage(Date week_date) {
        LinearLayout layout = findViewById(R.id.hiddenList);
        String title = new SimpleDateFormat("yyyy-MM-dd").format(week_date);
        REST rest = MainApplication.getContainer().get(REST.class);
        rest.MealsByCategory(title, 0)
                .enqueue(new Callback<Wrappers.Collection<Meal>>() {
                    @Override
                    public void onResponse(Call<Wrappers.Collection<Meal>> call, Response<Wrappers.Collection<Meal>> response) {
                        List<Meal> meals = response.body().data;

                        for (int j=0; j< timings.length; j++) {
                            layout.removeAllViews();
                            for (int i=0;i<meals.size();i++) {
                                Meal meal = meals.get(i);
                                if (meal.timing_id == j+1) {
                                    View convertView = LayoutInflater.from(DailyCaleandarActivity.this).inflate(R.layout.meal_list_item, layout, false);
                                    CustomMealAdapter.FoodHolder holder = new CustomMealAdapter.FoodHolder();
                                    holder.title = convertView.findViewById(R.id.listitem_title);
                                    holder.gram_txt = convertView.findViewById(R.id.gram_txt);
                                    holder.point_txt = convertView.findViewById(R.id.point_txt);
                                    holder.id_btn = convertView.findViewById(R.id.imgAddMeal);
                                    convertView.setTag(holder);
                                    convertView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));

                                    holder.title.setText(meal.food_name);
                                    holder.title.setWidth(700);
                                    if (meal.amount == 0f) {
                                        holder.gram_txt.setText("");
                                    } else {
                                        holder.gram_txt.setText("("+ meal.amount + "γρ.)");
                                    }

                                    holder.point_txt.setText(new SimpleDateFormat("yyyy-MM-dd").format(week_date));
                                    holder.id_btn.setId(meal.id);
                                    layout.addView(convertView);
                                }
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

                                String baseDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString();
                                File f = new File(baseDir + File.separator + timings[j] + "_" +title + "_image.png");
                                try {
                                    f.createNewFile();
                                    FileOutputStream fo = new FileOutputStream(f);
                                    fo.write(bytes.toByteArray());
                                    fo.flush();
                                    fo.close();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<Wrappers.Collection<Meal>> call, Throwable t) {

                    }
                });
    }
    public void imageToPDF() {
        String baseDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString();
        if (weekly_dates.length == 1) {
            String title = new SimpleDateFormat("yyyy-MM-dd").format(weekly_dates[0]);
            try {
                Document document = new Document();
                PdfWriter.getInstance(document, new FileOutputStream(baseDir + "/daily_"+title+".pdf")); //  Change pdf's name.
                document.open();
                SimpleDateFormat localDateFormat = new SimpleDateFormat("EEE, MMM d, yy");
                String time = localDateFormat.format(weekly_dates[0]);
                //create a paragraph
                Paragraph paragraph = new Paragraph(time);
                float[] columnWidths = {90f};
                //create PDF table with the given widths
                PdfPTable table = new PdfPTable(columnWidths);

                for (int i = 0; i < timings.length; i++) {
                    String path = baseDir + File.separator + timings[i] + "_" +title + "_image.png";
                    File fImage = new File(path);
                    if (fImage.exists()) {
                        PdfPCell cell = new PdfPCell();
                        PdfPCell cell_title = new PdfPCell(new Phrase(timings_title[i]));
                        Image image = Image.getInstance(baseDir + File.separator + timings[i] + "_" + title + "_image.png");
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

                File fdelete = new File(baseDir + "/"+week_title+".pdf");
                if (fdelete.exists()) {
                    if (fdelete.delete()) {
                    } else {
                    }
                }
                PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(baseDir + "/"+week_title+".pdf")); //  Change pdf's name.
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
                        String path = baseDir + File.separator + timings[i] + "_" +title + "_image.png";

                        File fImage = new File(path);
                        if (fImage.exists()) {
                            PdfPCell cell = new PdfPCell();
                            PdfPCell cell_title = new PdfPCell(new Phrase(timings_title[i]));
                            Image image = Image.getInstance(baseDir + File.separator + timings[i] + "_" + title + "_image.png");
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

        for(int i=0; i<weekly_dates.length;i++) {
            deleteImage(weekly_dates[i]);
        }

    }

    public void deleteImage(Date week_date) {
        String baseDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString();
        String title = new SimpleDateFormat("yyyy-MM-dd").format(week_date);
        for (String timing : timings) {
            String path = baseDir + File.separator + timing + "_" + title + "_image.png";
            File fdelete = new File(path);
            if (fdelete.exists()) {
                Log.e("delete:", fdelete.delete() + "");
            }
        }
    }
}