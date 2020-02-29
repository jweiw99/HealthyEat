package my.edu.utar.uccd3223.models;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Calories {
    private int calories_date;
    private int max_calories;
    private int calories_taken;

    public Calories(){
        this.calories_date = Integer.parseInt(new SimpleDateFormat("yyyyMMdd").format(new Date().getTime()));
        this.max_calories = 0;
        this.calories_taken = 0;
    }

    public Calories(int max_calories,int calories_taken){
        this.calories_date = Integer.parseInt(new SimpleDateFormat("yyyyMMdd").format(new Date().getTime()));
        this.max_calories = max_calories;
        this.calories_taken = calories_taken;
    }

    public int getCalories_date() {
        return calories_date;
    }

    public void setCalories_date(int calories_date) {
        this.calories_date = calories_date;
    }

    public int getMax_calories() {
        return max_calories;
    }

    public void setMax_calories(int max_calories) {
        this.max_calories = max_calories;
    }

    public int getCalories_taken() {
        return calories_taken;
    }

    public void setCalories_taken(int calories_taken) {
        this.calories_taken = calories_taken;
    }
}
