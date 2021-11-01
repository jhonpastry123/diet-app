package com.diet.trinity.activity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.diet.trinity.R;
import com.diet.trinity.Utility.Common;
import com.diet.trinity.Utility.Global;
import com.diet.trinity.Utility.MealChooseHelper;
import com.diet.trinity.Utility.PurchaseTimeHelper;
import com.diet.trinity.Utility.SportItemDatabaseHelper;
import com.diet.trinity.Utility.SportsDatabaseHelper;
import com.diet.trinity.model.Goal;
import com.diet.trinity.model.PersonalData;
import com.itextpdf.text.Document;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfWriter;
import com.warkiz.widget.IndicatorSeekBar;
import com.warkiz.widget.OnSeekChangeListener;
import com.warkiz.widget.SeekParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class SettingActivity extends AppCompatActivity {
    EditText tallEdit, weightEdit, birthEdit, neckEdit, waistEdit, thighEdit;
    RadioButton manBtn, womanBtn, ex0, ex3,ex5, ex7, exSports;
    RadioGroup settingR, genderR;
    IndicatorSeekBar seekBar;
    LinearLayout linSportContainer;
    int gymNum;
    Calendar calendar;
    Date mBirthday;
    int mWeeklyReduce = 300;
    RequestQueue queue;
    String user_id, sport1_name, sport1_min, sport1_efficient, sport2_name, sport2_min, sport2_efficient, sport3_name, sport3_min, sport3_efficient, sport1_type, sport2_type, sport3_type, dirpath;
    float min=0, coefficient=0;

    //------calendar---------//
    private CalendarView mCalendarView;
    private AlertDialog.Builder alertDialogBuilder;
    private AlertDialog alertDialog;

    //-------sports----------//
    private SQLiteDatabase db, db_purchase;
    private SQLiteOpenHelper openHelper;
    LinearLayout layout2, layout3, layout1;

    //------pdf---------//
    LinearLayout relativeLayout;

    JSONArray settings;

    //----- loading dialog -----//
    ProgressDialog mProgressDialog;

    //------- sport ------//
    TextView txt1, txt2, txt3;
    Spinner dropdown1, dropdown2, dropdown3, meal_dropdown, goal_dropdown;
    ArrayList<String> dropList = new ArrayList<String>();
    Map<String, String> element_data = new HashMap<String, String>();
    String Meal_choose="";

    //-------- expire date --------//
    TextView txtExpire;

    //--------- gain & lose text-----------//
    TextView txtGoal;
    SQLiteOpenHelper openHelper_sportitem, openHelper_purchase;
    SQLiteDatabase db_sportitem;
    long purchase_time;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        openHelper = new SportsDatabaseHelper(this);
        db = openHelper.getWritableDatabase();
        openHelper_purchase = new PurchaseTimeHelper(this);
        db_purchase = openHelper_purchase.getWritableDatabase();
        final Cursor cursor = db.rawQuery("SELECT *FROM " + SportsDatabaseHelper.TABLE_NAME, null);
        final Cursor cursor_purchase = db_purchase.rawQuery("SELECT *FROM " + PurchaseTimeHelper.TABLE_NAME,  null);

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_setting);

        init();

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

        layout2.setVisibility(View.GONE);
        layout3.setVisibility(View.GONE);

        openHelper_sportitem = new SportItemDatabaseHelper(this);
        db_sportitem = openHelper_sportitem.getWritableDatabase();

        relativeLayout = findViewById(R.id.contentLayout);

        ActivityCompat.requestPermissions(SettingActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, PackageManager.PERMISSION_GRANTED);
        if(Global.meal_num>0)
            meal_dropdown.setSelection(Global.meal_num-1);
        if (PersonalData.getInstance().getGoal() == Goal.LOSE) {
            goal_dropdown.setSelection(0);
        }
        else if (PersonalData.getInstance().getGoal() == Goal.GAIN) {
            goal_dropdown.setSelection(1);
        }
        else if (PersonalData.getInstance().getGoal() == Goal.STAY) {
            goal_dropdown.setSelection(2);
        }
        addEventListener();
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        String bitdate = dateFormat.format(PersonalData.getInstance().getBirthday());

        if(String.valueOf(PersonalData.getInstance().getGender()).equals("MALE")){
            manBtn.setChecked(true);
        }else {
            womanBtn.setChecked(true);
        }

        if (PersonalData.getInstance().getGoal() == Goal.GAIN) {
            txtGoal.setText("Εβδομαδιαίος Στόχος Αύξησης Βάρους");
        }

        seekBar.setOnSeekChangeListener(new OnSeekChangeListener() {
            @Override
            public void onSeeking(SeekParams seekParams) {
                switch (seekParams.thumbPosition){
                    case 0:
                        mWeeklyReduce = 300;
                        break;
                    case 1:
                        mWeeklyReduce = 600;
                        break;
                    case 2:
                        mWeeklyReduce = 1000;
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

        genderR.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

            }
        });

        mWeeklyReduce = Math.round(PersonalData.getInstance().getWeekly_reduce());


        settingR.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                View radioButton = group.findViewById(checkedId);
                int index = group.indexOfChild(radioButton);
                gymNum = index % 5 ;
                if(gymNum == 4){
                    linSportContainer.setVisibility(View.VISIBLE);
                }
                else{
                    linSportContainer.setVisibility(View.GONE);
                }
            }
        });

        if(!exSports.isChecked()){
            findViewById(R.id.linSportContainer).setVisibility(GONE);
        }
        else
        {
            if(PersonalData.getInstance().getMembership() == 0) {
                if (cursor != null) {
                    if (cursor.moveToFirst()) {
                        do {
                            user_id = cursor.getString(cursor.getColumnIndex(SportsDatabaseHelper.COL_2));

                            if (user_id.equals(Global.user_id)) {
                                sport1_type = cursor.getString(cursor.getColumnIndex(SportsDatabaseHelper.COL_3));
                                sport1_min = cursor.getString(cursor.getColumnIndex(SportsDatabaseHelper.COL_4));
                                sport1_efficient = cursor.getString(cursor.getColumnIndex(SportsDatabaseHelper.COL_5));

                                sport2_type = cursor.getString(cursor.getColumnIndex(SportsDatabaseHelper.COL_6));
                                sport2_min = cursor.getString(cursor.getColumnIndex(SportsDatabaseHelper.COL_7));
                                sport2_efficient = cursor.getString(cursor.getColumnIndex(SportsDatabaseHelper.COL_8));

                                sport3_type = cursor.getString(cursor.getColumnIndex(SportsDatabaseHelper.COL_9));
                                sport3_min = cursor.getString(cursor.getColumnIndex(SportsDatabaseHelper.COL_10));
                                sport3_efficient = cursor.getString(cursor.getColumnIndex(SportsDatabaseHelper.COL_11));
                            }

                        } while (cursor.moveToNext());
                    }
                    cursor.close();
                }
                if(sport1_type != null && Integer.parseInt(sport1_type)>-1) {
                    dropdown1.setSelection(Integer.parseInt(sport1_type));
                    txt1.setText(sport1_min);
                }
                if(sport2_type != null && Integer.parseInt(sport2_type)>-1) {
                    layout2.setVisibility(VISIBLE);
                    dropdown2.setSelection(Integer.parseInt(sport2_type));
                    txt2.setText(sport2_min);
                    if(Integer.parseInt(sport2_min)==0)
                        txt2.setText("");
                }
                if(sport3_type!=null && Integer.parseInt(sport3_type)>-1)
                {
                    layout3.setVisibility(VISIBLE);
                    dropdown3.setSelection(Integer.parseInt(sport3_type));
                    txt3.setText(sport3_min);
                    if(Integer.parseInt(sport3_min)==0)
                        txt3.setText("");
                }
            }
        }

        if(PersonalData.getInstance().getMembership()!=0) {
            mProgressDialog = new ProgressDialog(SettingActivity.this);
            mProgressDialog.setTitle("Loading...");
            mProgressDialog.setIndeterminate(false);
            mProgressDialog.show();
            gymNum = PersonalData.getInstance().getGymType();
            show_date_settings(getCurrentDate());
        }
        else
        {
            tallEdit.setText( String.valueOf((PersonalData.getInstance().getHeight())));
            weightEdit.setText(String.valueOf(PersonalData.getInstance().getWeight()));
            birthEdit.setText(String.valueOf(bitdate));
            neckEdit.setText(String.valueOf(PersonalData.getInstance().getNeck_perimeter()));
            waistEdit.setText(String.valueOf(PersonalData.getInstance().getWaist_perimeter()));
            thighEdit.setText(String.valueOf(PersonalData.getInstance().getThigh_perimeter()));

            seekBar.setProgress(PersonalData.getInstance().getWeekly_reduce());

            gymNum = PersonalData.getInstance().getGymType();
            switch (gymNum){
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
                    break;
                default:
                    break;
            }
        }

        //--------calendar----------//
        alertDialogBuilder = new AlertDialog.Builder(this);
        mCalendarView = (CalendarView) findViewById(R.id.calendarView);
        mCalendarView.setFocusedMonthDateColor(Color.MAGENTA);
        mCalendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(CalendarView CalendarView, int year, int month, int dayOfMonth) {
                mProgressDialog = new ProgressDialog(SettingActivity.this);
                mProgressDialog.setTitle("Loading...");
                mProgressDialog.setIndeterminate(false);
                mProgressDialog.show();

                String date = year + "-" + (month+1) + "-"+ dayOfMonth ;
                if(month<9)
                    date = year + "-" + "0"+(month+1) + "-"+ dayOfMonth ;
                show_date_settings(date);
            }
        });

        DropDown("Select Sports", "1");

        String date = dateFormat.format(System.currentTimeMillis() + 1000 * 60 * 60 * 24);
        txtExpire.setText(date);

        purchase_time = 0;
        if(cursor_purchase != null){
            if (cursor_purchase.moveToFirst()){
                purchase_time = Long.parseLong(cursor_purchase.getString(cursor_purchase.getColumnIndex(PurchaseTimeHelper.COL_2)));
                long time = purchase_time / (1000*60*60*24) + 30;
                date = dateFormat.format(time * 1000 * 60 * 60 * 24);
                txtExpire.setText(date);
            }
        }
    }

    private void addEventListener() {
        final TextView _count = findViewById(R.id.txtSelectedMealCount);

        findViewById(R.id.imgBack).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validate()){
                    calendar = Calendar.getInstance();
                    String currentString = birthEdit.getText().toString();
                    String[] separated = currentString.split("/");
                    calendar.set(Integer.parseInt(separated[2]), Integer.parseInt(separated[1]), Integer.parseInt(separated[0]));
                    mBirthday = calendar.getTime();


                    PersonalData.getInstance().setHeight(Float.parseFloat(tallEdit.getText().toString()));
                    PersonalData.getInstance().setWeight(Float.parseFloat(weightEdit.getText().toString()));
                    PersonalData.getInstance().setBirthday(mBirthday);
                    PersonalData.getInstance().setNeck_perimeter(Float.parseFloat(neckEdit.getText().toString()));
                    PersonalData.getInstance().setWaist_perimeter(Float.parseFloat(waistEdit.getText().toString()));
                    PersonalData.getInstance().setThigh_perimeter(Float.parseFloat(thighEdit.getText().toString()));
                    PersonalData.getInstance().setGymType(gymNum);
                    PersonalData.getInstance().setWeekly_reduce(mWeeklyReduce);

                    sport1_efficient = get_efficient_sport(sport1_type);
                    sport1_min = txt1.getText().toString();
                    if(sport1_min==null || sport1_min.length()==0)
                        sport1_min = "0";
                    if(sport1_type==null || sport1_type.length()==0)
                        sport1_type = "-1";

                    sport2_efficient = get_efficient_sport(sport2_type);
                    sport2_min = txt2.getText().toString();
                    if(sport2_min==null || sport2_min.length()==0)
                        sport2_min = "0";
                    if(sport2_type==null || sport2_type.length()==0)
                        sport2_type = "-1";

                    sport3_efficient = get_efficient_sport(sport3_type);
                    sport3_min = txt3.getText().toString();
                    if(sport3_min==null || sport3_min.length()==0)
                        sport3_min = "0";
                    if(sport3_type==null || sport3_type.length()==0)
                        sport3_type = "-1";


                    float weight = PersonalData.getInstance().getWeight();

                    float total = weight * (Float.parseFloat(sport1_efficient) * Float.parseFloat(sport1_min) + Float.parseFloat(sport2_efficient) * Float.parseFloat(sport2_min) + Float.parseFloat(sport3_efficient) + Float.parseFloat(sport3_min));

                    PersonalData.getInstance().setTotal_exercise(total);
                if(PersonalData.getInstance().getMembership()!=0)
                    sendSettings();
                else
                    sendSettings1();

                    insertMealChoose(Meal_choose);

                    Intent intent = new Intent(SettingActivity.this, DailyCaleandarActivity.class);
                    startActivity(intent);
                }

            }
        });

//        findViewById(R.id.imgWarning).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                String fileStamp = new SimpleDateFormat("yyyymmdd_HHmmss").format(new Date());
//                    layoutToImage("setting"+fileStamp);
//                try {
//                    imageToPDF("setting"+fileStamp);
//                } catch (FileNotFoundException e) {
//                    e.printStackTrace();
//                }
//            }
//        });

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
                if (position == 0) {
                    PersonalData.getInstance().setGoal(Goal.LOSE);
                }
                else if (position == 1) {
                    PersonalData.getInstance().setGoal(Goal.GAIN);
                }
                else if (position == 2) {
                    PersonalData.getInstance().setGoal(Goal.STAY);
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    public void layoutToImage(String title) {
        relativeLayout.setDrawingCacheEnabled(true);
        relativeLayout.buildDrawingCache(true);

        Bitmap bm = relativeLayout.getDrawingCache();
        Intent share = new Intent(Intent.ACTION_SEND);
        share.setType("image/jpeg");
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.PNG, 100, bytes);

        File f = new File(android.os.Environment.getExternalStorageDirectory() + File.separator + title + "image.png");
        try {
            f.createNewFile();
            FileOutputStream fo = new FileOutputStream(f);
            fo.write(bytes.toByteArray());
        } catch (IOException e) {
            e.printStackTrace();
        }
//        Bitmap viewBitmap = Bitmap.createBitmap(relativeLayout.getWidth(), relativeLayout.getHeight(), Bitmap.Config.RGB_565);
//
//        Canvas viewCanvas = new Canvas(viewBitmap);
//
//        Drawable backgroundDrawable = relativeLayout.getBackground();
//
//        if(backgroundDrawable != null)
//        {
//            backgroundDrawable.draw(viewCanvas);
//        }
//        else
//        {
//            viewCanvas.drawColor(Color.WHITE);
//            relativeLayout.draw(viewCanvas);
//        }
//
//        OutputStream outputStream = null;
//
//        try{
//            File imgFile = new File(Environment.getExternalStorageDirectory() + File.separator + title + "image.png");
//
//            outputStream = new FileOutputStream(imgFile);
//            viewBitmap.compress(Bitmap.CompressFormat.PNG, 40, outputStream);
//            outputStream.close();
//        }
//        catch (Exception e){
//            e.printStackTrace();
//        }
    }
    
    public void imageToPDF(String title) throws FileNotFoundException {
        try {
            Document document = new Document();
            dirpath = android.os.Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString();
            PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(dirpath + "/"+title+".pdf")); //  Change pdf's name.
            document.open();
            PdfContentByte content = writer.getDirectContent();
            Image image = Image.getInstance(Environment.getExternalStorageDirectory() + File.separator + title + "image.png");
            image.scaleAbsolute(PageSize.A4);
            image.setAbsolutePosition(0, 0);

            float width = PageSize.A4.getWidth();
            float heightRatio = image.getHeight() * width / image.getWidth();
            int nPages = Math.round (heightRatio / PageSize.A4.getHeight());
            float difference = heightRatio % PageSize.A4.getHeight();

            while (nPages >= 0) {
                document.setMargins(10, 10, 10, 10);
                document.newPage();
                content.addImage(image, width, 0, 0, heightRatio, 0, -((--nPages * PageSize.A4.getHeight()) + difference));
            }
            document.close();
            Toast.makeText(this, "Η αποθήκευση ολοκληρώθηκε!", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
        }
    }

    private Bitmap getBitmapFromView(View view, int height, int width) {
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Drawable bgDrawable = view.getBackground();
        if (bgDrawable != null)
            bgDrawable.draw(canvas);
        else
            canvas.drawColor(Color.WHITE);
        view.draw(canvas);
        return bitmap;
    }

    private void init() {
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

    private void sendSettings(){
        String settingUrl = Common.getInstance().getSettingUrl();
        final String access_token = Global.token;
        if(txt1.getText().toString().length()>0)
            sport1_min = txt1.getText().toString();
        else
            sport1_min = "0";

        if(txt2.getText().toString().length()>0)
            sport2_min = txt2.getText().toString();
        else
            sport2_min = "0";

        if(txt3.getText().toString().length()>0)
            sport3_min = txt3.getText().toString();
        else
            sport3_min = "0";

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
                                Toast.makeText(SettingActivity.this, getResources().getString(R.string.retry_info_login), Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(SettingActivity.this, getResources().getString(R.string.offline_text), Toast.LENGTH_LONG).show();
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
                params.put("exercise_rate", gymNum+"");
                params.put("height", tallEdit.getText().toString());
                params.put("weight", weightEdit.getText().toString());
                params.put("neck", neckEdit.getText().toString());
                params.put("waist", waistEdit.getText().toString());
                params.put("thigh", thighEdit.getText().toString());
                params.put("weekly_goal", mWeeklyReduce+"");
                params.put("sport1_type", sport1_type);
                params.put("sport1_time", sport1_min);
                params.put("sport2_type", sport2_type);
                params.put("sport2_time", sport2_min);
                params.put("sport3_type", sport3_type);
                params.put("sport3_time", sport3_min);

                return params;
            }
        };
        queue = Volley.newRequestQueue(SettingActivity.this);
        queue.add(postRequest);
    }

    private void sendSettings1(){
        insertSportData("",sport1_type, sport1_efficient,sport1_min,sport2_type, sport2_efficient,sport2_min,sport3_type, sport3_efficient,sport3_min);
    }

    public String getCurrentDate() {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        String formattedDate = df.format(c.getTime());
        return formattedDate;
    }

    private void show_date_settings(final String date_){
        String settingUrl = Common.getInstance().getSettingUrl()+"?user_id="+Global.user_id+"&date="+date_;

        final String access_token = Global.token;
        StringRequest postRequest = new StringRequest(Request.Method.GET, settingUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        mProgressDialog.dismiss();

                        JSONObject jsonObject = null;
                        try {
                            jsonObject = new JSONObject(response);
                            settings = jsonObject.optJSONArray("settings");
                            if(settings.length()==0)
                            {
                                tallEdit.setText(PersonalData.getInstance().getHeight()+"");
                                weightEdit.setText(PersonalData.getInstance().getWeight()+"");
                                neckEdit.setText(PersonalData.getInstance().getNeck_perimeter()+"");
                                waistEdit.setText(PersonalData.getInstance().getWaist_perimeter()+"");
                                thighEdit.setText(PersonalData.getInstance().getThigh_perimeter()+"");
                                linSportContainer.setVisibility(View.GONE);
                                ex0.setChecked(false);
                                ex3.setChecked(false);
                                ex5.setChecked(false);
                                ex7.setChecked(false);
                                exSports.setChecked(false);
                                seekBar.setProgress(300);
                                linSportContainer.setVisibility(GONE);
                            }
                            else
                            {
                            int i = settings.length()-1;
                                {
                                     {
                                        tallEdit.setText(settings.getJSONObject(i).getString("height"));
                                        weightEdit.setText(settings.getJSONObject(i).getString("weight"));
                                        neckEdit.setText(settings.getJSONObject(i).getString("neck"));
                                        waistEdit.setText(settings.getJSONObject(i).getString("waist"));
                                        thighEdit.setText(settings.getJSONObject(i).getString("thigh"));

                                        sport1_type = settings.getJSONObject(i).getString("sport1_type");
                                        sport2_type = settings.getJSONObject(i).getString("sport2_type");
                                        sport3_type = settings.getJSONObject(i).getString("sport3_type");

                                        sport1_min = settings.getJSONObject(i).getString("sport1_time");
                                        sport2_min = settings.getJSONObject(i).getString("sport2_time");
                                        sport3_min = settings.getJSONObject(i).getString("sport3_time");

                                        int gNum = Integer.parseInt(settings.getJSONObject(i).getString("exercise_rate"));
                                        switch (gNum) {
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
                                                break;
                                            default:
                                                break;
                                        }

                                        seekBar.setProgress(Integer.parseInt(settings.getJSONObject(i).getString("weekly_goal")));
                                        if(gNum == 4) {
                                            linSportContainer.setVisibility(VISIBLE);

                                                dropdown1.setSelection(Integer.parseInt(sport1_type));
                                                txt1.setText(settings.getJSONObject(i).getString("sport1_time"));

                                            if (!(settings.getJSONObject(i).getString("sport2_time").equals("0"))) {
                                                layout2.setVisibility(View.VISIBLE);
                                                dropdown2.setSelection(Integer.parseInt(sport2_type));
                                                txt2.setText(settings.getJSONObject(i).getString("sport2_time"));
                                            } else {
                                                layout2.setVisibility(GONE);
                                            }
                                            if (!(settings.getJSONObject(i).getString("sport3_time").equals("0"))) {
                                                layout3.setVisibility(View.VISIBLE);
                                                dropdown3.setSelection(Integer.parseInt(sport3_type));
                                                txt3.setText(settings.getJSONObject(i).getString("sport3_time"));
                                            } else {
                                                layout3.setVisibility(GONE);
                                            }
                                        }
                                    }
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(SettingActivity.this, getResources().getString(R.string.offline_text), Toast.LENGTH_LONG).show();
            }

        }){
            @Override
            public Map<String, String> getHeaders()  {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer "+access_token);
                return headers;
            }
        };
        queue = Volley.newRequestQueue(SettingActivity.this);
        queue.add(postRequest);
    }

    private void DropDown(String title, String id) {
        final String dropid = id;

        dropList.add("");
        final Cursor cursor_sportitem = db_sportitem.rawQuery("SELECT *FROM " + SportItemDatabaseHelper.TABLE_NAME,  null);

        if(cursor_sportitem != null){
            if (cursor_sportitem.moveToFirst()){
                do{
                    dropList.add(cursor_sportitem.getString(cursor_sportitem.getColumnIndex(SportItemDatabaseHelper.COL_2)));
                    element_data.put(cursor_sportitem.getString(cursor_sportitem.getColumnIndex(SportItemDatabaseHelper.COL_4)), cursor_sportitem.getString(cursor_sportitem.getColumnIndex(SportItemDatabaseHelper.COL_3)));
                }while(cursor_sportitem.moveToNext());
            }
            cursor_sportitem.close();
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.spinner_dropdown_item, dropList);
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        dropdown1.setAdapter(adapter);
        dropdown2.setAdapter(adapter);
        dropdown3.setAdapter(adapter);

        if(sport1_type!=null && Integer.parseInt(sport1_type)>-1)
            dropdown1.setSelection(Integer.parseInt(sport1_type));

        dropdown1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedItemText = (String) parent.getItemAtPosition(position);
                sport1_type = position + "";
                sport1_name = selectedItemText;
                sport1_efficient = get_efficient_sport(sport1_type);
                if (!(txt1.getText().toString().equals("0")) && txt1.getText().toString().length() > 0) {
                    layout2.setVisibility(VISIBLE);
                    if (Integer.parseInt(sport2_type) > -1) {
                        dropdown2.setSelection(Integer.parseInt(sport2_type));
                        txt2.setText(sport2_min);
                    }
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
                sport2_type = position + "";
                sport2_name = selectedItemText;
                sport2_efficient = get_efficient_sport(sport2_type);
                if(!(txt2.getText().toString().equals("0")) && txt2.getText().toString().length()>0)
                {
                    layout3.setVisibility(VISIBLE);
                    if (Integer.parseInt(sport3_type) > -1) {
                        dropdown3.setSelection(Integer.parseInt(sport3_type));
                        txt3.setText(sport3_min);
                    }
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
                sport3_type = position + "";
                sport3_name = selectedItemText;
                sport3_efficient = get_efficient_sport(sport3_type);
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
                if(Integer.parseInt(sport1_type)>-1  && !(txt1.getText().toString().equals("0")) && txt1.getText().toString().length()>0)
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
                if(Integer.parseInt(sport2_type)>-1 &&  !(txt2.getText().toString().equals("0")) && txt2.getText().toString().length()>0)
                {
                    layout3.setVisibility(VISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    String get_efficient_sport(String sport_type){
        if(element_data.get(sport_type)!=null)
        return element_data.get(sport_type);
        else
            return "0";
    }

    void insertMealChoose(String Meal_choose){
        SQLiteOpenHelper openHelper_meal;
        SQLiteDatabase db_meal;

        openHelper_meal = new MealChooseHelper(this);
        db_meal = openHelper_meal.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(MealChooseHelper.COL_2, Meal_choose);
        db_meal.insert(MealChooseHelper.TABLE_NAME,null,contentValues);
    }

    private void insertSportData(String id, String sport1_type, String sport1_efficient, String sport1_min, String sport2_type, String sport2_efficient, String sport2_min, String sport3_type, String sport3_efficient, String sport3_min){
        ContentValues contentValues = new ContentValues();

        contentValues.put(SportsDatabaseHelper.COL_2, "");

        contentValues.put(SportsDatabaseHelper.COL_3, sport1_type);
        contentValues.put(SportsDatabaseHelper.COL_4, sport1_min);
        contentValues.put(SportsDatabaseHelper.COL_5, sport1_efficient);

        contentValues.put(SportsDatabaseHelper.COL_6, sport2_type);
        contentValues.put(SportsDatabaseHelper.COL_7, sport2_min);
        contentValues.put(SportsDatabaseHelper.COL_8, sport2_efficient);

        contentValues.put(SportsDatabaseHelper.COL_9, sport3_type);
        contentValues.put(SportsDatabaseHelper.COL_10, sport3_min);
        contentValues.put(SportsDatabaseHelper.COL_11, sport3_efficient);

        db.insert(SportsDatabaseHelper.TABLE_NAME,null,contentValues);
    }
}
