package com.diet.trinity.model;

import android.content.Context;

import com.diet.trinity.Utility.Global;
import com.google.gson.Gson;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Date;

public class PersonalData {
    private Goal goal;
    private float initial_weight, weight, height;
    private float waist_perimeter=0, neck_perimeter=0, thigh_perimeter=0;
    private Gender gender;
    private Date birthday, start_date;
    private int age;
    private int gymType;
    private int[] gymDurationPerWeek;
    private float ideal_weight, goal_weight, weekly_reduce;
    private int points, units;
    private DietMode dietMode;
    private int membership;
    private int sport_type1 = 0, sport_type2 = 0, sport_type3 = 0, sport_time1 = 0, sport_time2 = 0, sport_time3 = 0;

    private long trial_time;
    private long purchase_time;

    public float[] gymCoeff = {1.2f, 1.375f, 1.55f, 1.725f, 1.9f};
    private float total_exercise = 0;

    public int Oily_num = 0;
    public int Junk_num = 0;

    private static PersonalData instance = new PersonalData();

    public static PersonalData getInstance()
    {
        return instance;
    }

    public static void setInstance(PersonalData ins){
        instance = ins;
    }

    public boolean writeData(Context context){
        Gson gson = new Gson();
        String json = gson.toJson(this);
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput("personal.db", Context.MODE_APPEND));
            outputStreamWriter.write(json);
            outputStreamWriter.append("\n");
            outputStreamWriter.close();
        }
        catch (IOException e) {
            return false;
        }
        return true;
    }

    public void setTrialTime(long trial_time){this.trial_time = trial_time;}
    public long getTrialTime(){ return trial_time;}

    public void setPurchase_time(long purchase_time){this.purchase_time = purchase_time;}
    public long getPurchase_time(){return purchase_time;}

    public Goal getGoal() {
        return goal;
    }

    public void setGoal(Goal goal) {
        this.goal = goal;
    }

    public float getInitial_weight() {
        return initial_weight;
    }

    public void setInitial_weight(float initial_weight) {
        this.initial_weight = initial_weight;
    }

    public float getWeight() {
        return weight;
    }

    public void setWeight(float weight) {
        this.weight = weight;
    }

    public float getHeight() {
        return height;
    }

    public void setHeight(float height) {
        this.height = height;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    public Date getStart_date() {
        return start_date;
    }

    public void setStart_date(Date start_date) {
        this.start_date = start_date;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public int getGymType() {
        return gymType;
    }

    public void setGymType(int gymType) {
        this.gymType = gymType;
    }

    public void setSportType1(int type) {
        this.sport_type1 = type;
    }
    public void setSportType2(int type) {
        this.sport_type2 = type;
    }
    public void setSportType3(int type) {
        this.sport_type3 = type;
    }
    public void setSportTime1(int min) {
        this.sport_time1 = min;
    }
    public void setSportTime2(int min) {
        this.sport_time2 = min;
    }
    public void setSportTime3(int min) {
        this.sport_time3 = min;
    }

    public int getSportType1() { return sport_type1;}
    public int getSportType2() { return sport_type2;}
    public int getSportType3() { return sport_type3;}
    public int getSportTime1() { return sport_time1;}
    public int getSportTime2() { return sport_time2;}
    public int getSportTime3() { return sport_time3;}

    public int[] getGymDurationPerWeek() {
        return gymDurationPerWeek;
    }

    public void setGymDurationPerWeek(int[] gymDurationPerWeek) {
        this.gymDurationPerWeek = gymDurationPerWeek;
    }

    public float getIdeal_weight() {
        if(gender == Gender.MALE){
            ideal_weight = (int ) (48 + (height - 152) * 1.06);
        }
        else{
            ideal_weight = (int ) (45.2 + (height - 152) * 0.89);
        }

        return ideal_weight;
    }

    public float getGoal_weight() {
        return goal_weight;
    }

    public void setGoal_weight(float goal_weight) {
        this.goal_weight = goal_weight;
    }

    public float getWeekly_reduce() {
        return weekly_reduce;
    }

    public void setWeekly_reduce(float weekly_reduce) {
        this.weekly_reduce = weekly_reduce;
    }

    public void setTotal_exercise(float total_exercise){
        this.total_exercise = total_exercise;
    }

    public int getUnits() {

        float BMR;
        if (getGoal() == Goal.LOSE){
            if(getBMI().value>=29.9 && getBMI().value <=40)
                weight = ideal_weight+(weight - ideal_weight)/4;

            if(gender == Gender.MALE){
                BMR = (int ) (88 + (13.4 * weight) + (4.8 * height) - (5.7 * age));
            }
            else{
                // BMR = (int ) (448 + (9.2 * weight) + (3.1 * height) - (4.3 * age));
                BMR = (float ) ((10 * weight) + (6.25 * height) - (5 * age) - 161);
            }
            float AMR = 0;
            if(gymType == 4)
            {
                AMR = BMR*1.6f + total_exercise/7;
            }
            else {
                AMR = BMR * gymCoeff[gymType];
            }

            AMR = AMR - weekly_reduce;

            units = Math.round(AMR / 100);


            Global.bmr = Math.round(BMR);
        }
        else if (getGoal() == Goal.GAIN){
            if(getBMI().value>=29.9 && getBMI().value <=40)
                weight = ideal_weight+((weight - ideal_weight))/4;

            if(gender == Gender.MALE){
                BMR = (float ) (88 + (13.4 * weight) + (4.8 * height) - (5.7 * age));
            }
            else{
                // BMR = (float ) (448 + (9.2 * weight) + (3.1 * height) - (4.3 * age));
                BMR = (float ) ((10 * weight) + (6.25 * height) - (5 * age) - 161);
            }
            float AMR = 0;
            if(gymType == 4)
            {
                AMR = BMR*1.6f + total_exercise/7;
            }
            else {
                AMR = BMR * gymCoeff[gymType];
            }

            AMR = AMR + weekly_reduce;

            units = Math.round(AMR / 100);

            Global.bmr = Math.round(BMR);
        }
        else if (getGoal() == Goal.STAY){
            if(gender == Gender.MALE){
                BMR = (float ) (5 + (10 * weight) + (6.25 * height) - (5 * age));
            }
            else{
                BMR = (float ) ((10 * weight) + (6.25 * height) - (5 * age) - 161);
            }
            float AMR = 0;
            if(gymType == 4)
            {
                AMR = BMR*1.6f + total_exercise/7;
            }
            else {
                AMR = BMR * gymCoeff[gymType];
            }

            units = Math.round(AMR / 100);

            Global.bmr = Math.round(BMR);
        }

        return units;
    }

    public int getPoints() {
        points = Math.round(units * 1.19394f);
        return points;
    }

    public DietMode getDietMode() {
        return dietMode;
    }

    public void setDietMode(DietMode dietMode) {
        this.dietMode = dietMode;
    }

    public int getMembership() {
        return membership;
    }

    public void setMembership(int membership) {
        this.membership = membership;
    }

    public float getWaist_perimeter() {
        return waist_perimeter;
    }

    public void setWaist_perimeter(float waist_perimeter) {
        this.waist_perimeter = waist_perimeter;
    }

    public float getNeck_perimeter() {
        return neck_perimeter;
    }

    public void setNeck_perimeter(float neck_perimeter) {
        this.neck_perimeter = neck_perimeter;
    }

    public float getThigh_perimeter() {
        return thigh_perimeter;
    }

    public void setThigh_perimeter(float thigh_perimeter) {
        this.thigh_perimeter = thigh_perimeter;
    }

    public float getBFP(){
        float bfp = 0;
        if(gender == Gender.MALE){
            bfp = (float) (495 / (1.0324 - 0.19077 * Math.log10(waist_perimeter - neck_perimeter) + 0.15456 * Math.log10(height)) - 450);
        }
        else{
            bfp = (float) (495 / (1.29579 - 0.35004 * Math.log10(waist_perimeter + thigh_perimeter - neck_perimeter) + 0.22100*(Math.log10(height)) ) - 450);
        }

        return bfp;
    }

    public BMI getBMI(){
        float bmi = 0;
        String BMIState;
        bmi = weight / (height * height / 10000);
        if (bmi < 18.5){
            BMIState = "Ελλιποβαρής";
        }
        else if(bmi < 24.9){
            BMIState = "Φυσιολογικός";
        }
        else if(bmi < 29.9){
            BMIState = "Υπέρβαρος";
        }
        else if(bmi < 35){
            BMIState = "Παχύσαρκος (1ου βαθμού)";
        }
        else if(bmi < 40){
            BMIState = "Παχύσαρκος (2ου βαθμού)";
        }
        else{
            BMIState = "Παχύσαρκος (3ου βαθμού)";
        }
        return new BMI(bmi, BMIState);
    }
}
