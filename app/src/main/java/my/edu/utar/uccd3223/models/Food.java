package my.edu.utar.uccd3223.models;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Food {
    private int food_id;
    private int food_date;
    private int food_api_id;
    private String food_title;
    private String food_image;
    private int food_calories;

    public Food(int food_id, int food_date, int food_api_id, String food_title, String food_image, int food_calories) {
        this.food_id = food_id;
        this.food_date = food_date;
        this.food_api_id = food_api_id;
        this.food_title = food_title;
        this.food_image = food_image;
        this.food_calories = food_calories;
    }

    public int getFood_id() {
        return food_id;
    }

    public void setFood_id(int food_id) {
        this.food_id = food_id;
    }

    public int getFood_date() {
        return food_date;
    }

    public void setFood_date(int food_date) {
        this.food_date = food_date;
    }

    public int getFood_api_id() {
        return food_api_id;
    }

    public void setFood_api_id(int food_api_id) {
        this.food_api_id = food_api_id;
    }

    public String getFood_title() {
        return food_title;
    }

    public void setFood_title(String food_title) {
        this.food_title = food_title;
    }

    public String getFood_image() {
        return food_image;
    }

    public void setFood_image(String food_image) {
        this.food_image = food_image;
    }

    public int getFood_calories() {
        return food_calories;
    }

    public void setFood_calories(int food_calories) {
        this.food_calories = food_calories;
    }
}
