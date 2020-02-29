package my.edu.utar.uccd3223.models;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Food {
    private int food_id;
    private int food_date;
    private int food_api_id;

    public Food(int food_id, int food_date, int food_api_id) {
        this.food_id = food_id;
        this.food_date = food_date;
        this.food_api_id = food_api_id;
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
}
