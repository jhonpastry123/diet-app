package com.diet.trinity.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.diet.trinity.R;
import com.diet.trinity.data.common.Goal;
import com.diet.trinity.data.common.PersonalData;
import com.warkiz.widget.IndicatorSeekBar;
import com.warkiz.widget.OnSeekChangeListener;
import com.warkiz.widget.SeekParams;

import java.util.Locale;

public class IdealWeightActivity extends AppCompatActivity {

    EditText _weight;
    TextView _warning;
    TextView _weeklyGoalText;
    ImageView _warningImage;
    IndicatorSeekBar _weeklyReduce;
    TextView weekly_support;
    int mWeeklyReduce = 300;
    float mGoalWeight;
    int mIdealWeight;
    int mIdealMaxWeight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ideal_weight);

        _weight = findViewById(R.id.editWeight);
        _warning = findViewById(R.id.txtWarning);
        _warningImage = findViewById(R.id.imgWarning);
        _weeklyReduce = findViewById(R.id.seekWeeklyReduce);
        _weeklyGoalText = findViewById(R.id.weekly_goal_txt);
        weekly_support = findViewById(R.id.weekly_support);

        addEventListener();
        initView();
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    private void initView(){
        PersonalData.getInstance().getIdeal_weight();
        float monthly_support_reduce = (float) (PersonalData.getInstance().getInitial_weight() * 0.04);
        float weekly_support_reduce = monthly_support_reduce / 4;
        String month_support = String.format(Locale.US, "%.1f", monthly_support_reduce);
        String week_support = String.format(Locale.US, "%.1f", weekly_support_reduce);
        mIdealWeight = (int) Math.round(Math.pow(PersonalData.getInstance().getHeight(), 2) * 18.5 / 10000);
        mIdealMaxWeight = (int) Math.round(Math.pow(PersonalData.getInstance().getHeight(), 2) * 24.9 / 10000);
        TextView _idealWeight = findViewById(R.id.txtIdealWeight);
        _idealWeight.setText("Το ιδανικό σας βάρος κυμαίνεται από " + mIdealWeight + " έως " + mIdealMaxWeight + "kg");

        if(PersonalData.getInstance().getGoal() == Goal.LOSE){
            _weeklyGoalText.setText("Εβδομαδιαίος Στόχος Απώλειας Βάρους");
            weekly_support.setVisibility(View.VISIBLE);
            weekly_support.setText("Η ιδανική μηνιαία απώλεια βάρους για εσάς είναι: " + month_support + " Kg\n" +
                    "Καλό θα ήταν ο εβδομαδιαίος στόχος σας να μην ξεπερνάει τα " + week_support + " Kg");
        }
        else if (PersonalData.getInstance().getGoal() == Goal.GAIN) {
            _weeklyGoalText.setText("Εβδομαδιαίος Στόχος Αύξησης Βάρους");
            weekly_support.setVisibility(View.GONE);
        }
        else if (PersonalData.getInstance().getGoal() == Goal.STAY) {
            _weeklyGoalText.setText("");
            _weeklyReduce.setVisibility(View.GONE);
            weekly_support.setVisibility(View.GONE);
        }
    }

    private void addEventListener(){
        _weight.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                try{
                    mGoalWeight = Float.parseFloat(s.toString());
                }
                catch (Exception e){
                    mGoalWeight = 0.0f;
                }

                float percentage = mGoalWeight / mIdealWeight;
                if(percentage < 0.95){ // || percentage > 1.05){
                    _warning.setVisibility(View.VISIBLE);
                    _warningImage.setVisibility(View.VISIBLE);
                }
                else{
                    _warning.setVisibility(View.GONE);
                    _warningImage.setVisibility(View.INVISIBLE);
                }
            }
        });

        _weeklyReduce.setOnSeekChangeListener(new OnSeekChangeListener() {
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
                ImageView _warningImageWeekly = findViewById(R.id.imgWarningWeekly);
                TextView _warningTextWeekly = findViewById(R.id.txtWarningWeekly);

                if(mWeeklyReduce / 1000f > PersonalData.getInstance().getWeight() * 0.02f){
                    _warningImageWeekly.setVisibility(View.VISIBLE);
                    _warningTextWeekly.setVisibility(View.VISIBLE);
                }
                else{
                    _warningImageWeekly.setVisibility(View.GONE);
                    _warningTextWeekly.setVisibility(View.GONE);
                }
                //Toast.makeText(getBaseContext(), String.valueOf(mWeeklyReduce), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onStartTrackingTouch(IndicatorSeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(IndicatorSeekBar seekBar) {

            }
        });

        findViewById(R.id.imgNext).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validate()){
                    PersonalData.getInstance().setGoal_weight(mGoalWeight);
                    PersonalData.getInstance().setWeekly_reduce(mWeeklyReduce);

                    Intent intent = new Intent(getBaseContext(), WelcomeActivity.class);
                    startActivity(intent);
                }
            }
        });

        findViewById(R.id.imgBack).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                finish();
            }
        });
    }

    private boolean validate(){
        boolean validate = true;

        try{
            mGoalWeight = Float.parseFloat(_weight.getText().toString());
        }
        catch (Exception e){
            mGoalWeight = 0.0f;
        }

        if(mGoalWeight <= 1.0f){
            _weight.setError("εισάγετε Βάρος");
            validate = false;
        }
        else{
            _weight.setError(null);
        }
        return validate;
    }
}