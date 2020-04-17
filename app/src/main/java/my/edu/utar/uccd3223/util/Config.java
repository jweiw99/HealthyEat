package my.edu.utar.uccd3223.util;

public class Config {

    public static final String DATABASE_NAME = "user-db";

    //column names of user table
    public static final String TABLE_USER = "user";
    public static final String COLUMN_USER_ID = "_id";
    public static final String COLUMN_USER_AGE = "age";
    public static final String COLUMN_USER_WEIGHT = "weight";
    public static final String COLUMN_USER_HEIGHT = "height";
    public static final String COLUMN_USER_GENDER = "gender";
    public static final String COLUMN_USER_WEIGHT_GOAL = "weight_goal";
    public static final String COLUMN_USER_ACTIVITY_LEVEL = "activity_level";

    //column names of user calories table
    public static final String TABLE_USER_CALORIES = "user_calories";
    public static final String COLUMN_USER_CALORIES_DATE = "calories_date";
    public static final String COLUMN_USER_MAX_CALORIES = "max_calories";
    public static final String COLUMN_USER_CALORIES_TAKEN = "calories_taken";

    //column names of user food table
    public static final String TABLE_USER_FOOD = "user_food";
    public static final String COLUMN_FOOD_ID = "food_id";
    public static final String COLUMN_USER_FOOD_DATE = "food_date";
    public static final String COLUMN_USER_FOOD_ID = "food_api_id";
    public static final String COLUMN_USER_FOOD_TITLE = "food_title";
    public static final String COLUMN_USER_FOOD_IMAGE = "food_image";
    public static final String COLUMN_USER_FOOD_CALORIES = "food_calories";

}
