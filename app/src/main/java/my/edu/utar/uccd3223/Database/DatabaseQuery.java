package my.edu.utar.uccd3223.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import my.edu.utar.uccd3223.models.Calories;
import my.edu.utar.uccd3223.models.Food;
import my.edu.utar.uccd3223.models.User;
import my.edu.utar.uccd3223.util.Config;

public class DatabaseQuery {
    private Context context;

    public DatabaseQuery(Context context) {
        this.context = context;
    }

    public long insertUser(User user) {

        long id = -1;
        DatabaseHelper databaseHelper = DatabaseHelper.getInstance(context);
        SQLiteDatabase sqLiteDatabase = databaseHelper.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(Config.COLUMN_USER_ID, 1);
        contentValues.put(Config.COLUMN_USER_AGE, user.getAge());
        contentValues.put(Config.COLUMN_USER_WEIGHT, user.getWeight());
        contentValues.put(Config.COLUMN_USER_HEIGHT, user.getHeight());
        contentValues.put(Config.COLUMN_USER_GENDER, user.getGender());
        contentValues.put(Config.COLUMN_USER_WEIGHT_GOAL, user.getWeight_goal());
        contentValues.put(Config.COLUMN_USER_ACTIVITY_LEVEL, user.getActivity_level());

        try {
            id = sqLiteDatabase.insertOrThrow(Config.TABLE_USER, null, contentValues);
        } catch (SQLiteException e) {
            Toast.makeText(context, "Operation failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
        } finally {
            sqLiteDatabase.close();
        }

        return id;
    }

    public User getUser() {

        DatabaseHelper databaseHelper = DatabaseHelper.getInstance(context);
        SQLiteDatabase sqLiteDatabase = databaseHelper.getReadableDatabase();

        Cursor cursor = null;
        try {

            cursor = sqLiteDatabase.query(Config.TABLE_USER, null,
                    Config.COLUMN_USER_ID + " = ? ", new String[]{String.valueOf(1)},
                    null, null, null, null);

            if (cursor != null)
                if (cursor.moveToFirst()) {

                    int age = cursor.getInt(cursor.getColumnIndex(Config.COLUMN_USER_AGE));
                    double weight = cursor.getDouble(cursor.getColumnIndex(Config.COLUMN_USER_WEIGHT));
                    double height = cursor.getDouble(cursor.getColumnIndex(Config.COLUMN_USER_HEIGHT));
                    int gender = cursor.getInt(cursor.getColumnIndex(Config.COLUMN_USER_GENDER));
                    int weight_goal = cursor.getInt(cursor.getColumnIndex(Config.COLUMN_USER_WEIGHT_GOAL));
                    int activity_level = cursor.getInt(cursor.getColumnIndex(Config.COLUMN_USER_ACTIVITY_LEVEL));

                    User userREC = new User(age, weight, height, gender, weight_goal, activity_level);
                    return userREC;
                }

            cursor = sqLiteDatabase.query(Config.TABLE_USER, null, Config.COLUMN_USER_ID + " = ? ", new String[]{String.valueOf(1)}, null, null, null, null);

            if (cursor != null)
                if (cursor.moveToFirst()) {

                    int age = cursor.getInt(cursor.getColumnIndex(Config.COLUMN_USER_AGE));
                    double weight = cursor.getDouble(cursor.getColumnIndex(Config.COLUMN_USER_WEIGHT));
                    double height = cursor.getDouble(cursor.getColumnIndex(Config.COLUMN_USER_HEIGHT));
                    int gender = cursor.getInt(cursor.getColumnIndex(Config.COLUMN_USER_GENDER));
                    int weight_goal = cursor.getInt(cursor.getColumnIndex(Config.COLUMN_USER_WEIGHT_GOAL));
                    int activity_level = cursor.getInt(cursor.getColumnIndex(Config.COLUMN_USER_ACTIVITY_LEVEL));
                    int max_calories = cursor.getInt(cursor.getColumnIndex(Config.COLUMN_USER_MAX_CALORIES));
                    int calories_taken = cursor.getInt(cursor.getColumnIndex(Config.COLUMN_USER_CALORIES_TAKEN));

                    User userList = new User(age, weight, height, gender, weight_goal, activity_level);
                    return userList;
                }
        } catch (Exception e) {
            Toast.makeText(context, "Operation failed", Toast.LENGTH_SHORT).show();
        } finally {
            if (cursor != null)
                cursor.close();
            sqLiteDatabase.close();
        }

        return null;
    }

    public long updateUserInfo(User user) {

        long rowCount = 0;
        DatabaseHelper databaseHelper = DatabaseHelper.getInstance(context);
        SQLiteDatabase sqLiteDatabase = databaseHelper.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(Config.COLUMN_USER_AGE, user.getAge());
        contentValues.put(Config.COLUMN_USER_WEIGHT, user.getWeight());
        contentValues.put(Config.COLUMN_USER_HEIGHT, user.getHeight());
        contentValues.put(Config.COLUMN_USER_GENDER, user.getGender());
        contentValues.put(Config.COLUMN_USER_WEIGHT_GOAL, user.getWeight_goal());
        contentValues.put(Config.COLUMN_USER_ACTIVITY_LEVEL, user.getActivity_level());

        try {
            rowCount = sqLiteDatabase.update(Config.TABLE_USER, contentValues,
                    Config.COLUMN_USER_ID + " = ? ",
                    new String[]{String.valueOf(1)});
        } catch (SQLiteException e) {
            Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
        } finally {
            sqLiteDatabase.close();
        }

        return rowCount;
    }

    public long insertCalories(Calories calories) {

        long id = -1;
        DatabaseHelper databaseHelper = DatabaseHelper.getInstance(context);
        SQLiteDatabase sqLiteDatabase = databaseHelper.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(Config.COLUMN_USER_ID, 1);
        contentValues.put(Config.COLUMN_USER_CALORIES_DATE, calories.getCalories_date());
        contentValues.put(Config.COLUMN_USER_MAX_CALORIES, calories.getMax_calories());
        contentValues.put(Config.COLUMN_USER_CALORIES_TAKEN, calories.getCalories_taken());

        try {
            id = sqLiteDatabase.insertOrThrow(Config.TABLE_USER_CALORIES, null, contentValues);
        } catch (SQLiteException e) {
            Toast.makeText(context, "Operation failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
        } finally {
            sqLiteDatabase.close();
        }

        return id;
    }

    public Calories getTodayCalories() {

        DatabaseHelper databaseHelper = DatabaseHelper.getInstance(context);
        SQLiteDatabase sqLiteDatabase = databaseHelper.getReadableDatabase();

        Cursor cursor = null;
        try {

            cursor = sqLiteDatabase.query(Config.TABLE_USER_CALORIES,
                    new String[]{Config.COLUMN_USER_MAX_CALORIES, Config.COLUMN_USER_CALORIES_TAKEN},
                    Config.COLUMN_USER_ID + " = ? AND " + Config.COLUMN_USER_CALORIES_DATE + " = ? ",
                    new String[]{String.valueOf(1), new SimpleDateFormat("yyyyMMdd").format(new Date().getTime())},
                    null, null, null, null);

            if (cursor != null)
                if (cursor.moveToFirst()) {

                    int max_calories = cursor.getInt(cursor.getColumnIndex(Config.COLUMN_USER_MAX_CALORIES));
                    int calories_taken = cursor.getInt(cursor.getColumnIndex(Config.COLUMN_USER_CALORIES_TAKEN));

                    Calories caloriesRec = new Calories(max_calories, calories_taken);
                    return caloriesRec;
                }
        } catch (Exception e) {
            Toast.makeText(context, "Operation failed", Toast.LENGTH_SHORT).show();
        } finally {
            if (cursor != null)
                cursor.close();
            sqLiteDatabase.close();
        }

        return null;
    }

    public long updateCaloriesInfo(Calories calories) {

        long rowCount = 0;
        DatabaseHelper databaseHelper = DatabaseHelper.getInstance(context);
        SQLiteDatabase sqLiteDatabase = databaseHelper.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(Config.COLUMN_USER_MAX_CALORIES, calories.getMax_calories());
        contentValues.put(Config.COLUMN_USER_CALORIES_TAKEN, calories.getCalories_taken());

        try {
            rowCount = sqLiteDatabase.update(Config.TABLE_USER_CALORIES, contentValues,
                    Config.COLUMN_USER_ID + " = ? AND " + Config.COLUMN_USER_CALORIES_DATE + " = ? ",
                    new String[]{String.valueOf(1), new SimpleDateFormat("yyyyMMdd").format(new Date().getTime())}
            );
        } catch (SQLiteException e) {
            Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
        } finally {
            sqLiteDatabase.close();
        }

        return rowCount;
    }

    public long setCaloriesTaken(int calories) {

        long rowCount = 0;
        DatabaseHelper databaseHelper = DatabaseHelper.getInstance(context);
        SQLiteDatabase sqLiteDatabase = databaseHelper.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(Config.COLUMN_USER_CALORIES_TAKEN, calories);

        try {
            rowCount = sqLiteDatabase.update(Config.TABLE_USER_CALORIES, contentValues,
                    Config.COLUMN_USER_ID + " = ? AND " + Config.COLUMN_USER_CALORIES_DATE + " = ? ",
                    new String[]{String.valueOf(1), new SimpleDateFormat("yyyyMMdd").format(new Date().getTime())});
        } catch (SQLiteException e) {
            Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
        } finally {
            sqLiteDatabase.close();
        }

        return rowCount;
    }


    public long insertFood(Food food) {

        long id = -1;
        DatabaseHelper databaseHelper = DatabaseHelper.getInstance(context);
        SQLiteDatabase sqLiteDatabase = databaseHelper.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(Config.COLUMN_USER_ID, 1);
        contentValues.put(Config.COLUMN_USER_FOOD_DATE, food.getFood_date());
        contentValues.put(Config.COLUMN_USER_FOOD_ID, food.getFood_api_id());

        try {
            id = sqLiteDatabase.insertOrThrow(Config.TABLE_USER_FOOD, null, contentValues);
        } catch (SQLiteException e) {
            Toast.makeText(context, "Operation failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
        } finally {
            sqLiteDatabase.close();
        }

        return id;
    }

    public List<Food> getTodayFood() {

        DatabaseHelper databaseHelper = DatabaseHelper.getInstance(context);
        SQLiteDatabase sqLiteDatabase = databaseHelper.getReadableDatabase();

        Cursor cursor = null;
        try {

            cursor = sqLiteDatabase.query(Config.TABLE_USER_FOOD, null,
                    Config.COLUMN_USER_ID + " = ? AND " + Config.COLUMN_USER_FOOD_DATE + " = ? ",
                    new String[]{String.valueOf(1), new SimpleDateFormat("yyyyMMdd").format(new Date().getTime())},
                    null, null, null, null);

            if (cursor != null)
                if (cursor.moveToFirst()) {
                    List<Food> foodRec = new ArrayList<>();
                    do {
                        int food_id = cursor.getInt(cursor.getColumnIndex(Config.COLUMN_FOOD_ID));
                        int food_date = cursor.getInt(cursor.getColumnIndex(Config.COLUMN_USER_FOOD_DATE));
                        int food_api_id = cursor.getInt(cursor.getColumnIndex(Config.COLUMN_USER_FOOD_ID));

                        foodRec.add(new Food(food_id, food_date, food_api_id));
                    } while (cursor.moveToNext());
                    return foodRec;
                }
        } catch (Exception e) {
            Toast.makeText(context, "Operation failed", Toast.LENGTH_SHORT).show();
        } finally {
            if (cursor != null)
                cursor.close();
            sqLiteDatabase.close();
        }

        return Collections.emptyList();
    }

    public long deleteFoodByid(int food_id) {
        long deletedRowCount = -1;
        DatabaseHelper databaseHelper = DatabaseHelper.getInstance(context);
        SQLiteDatabase sqLiteDatabase = databaseHelper.getWritableDatabase();

        try {
            deletedRowCount = sqLiteDatabase.delete(Config.TABLE_USER_FOOD,
                    Config.COLUMN_FOOD_ID + " = ? ",
                    new String[]{String.valueOf(food_id)});
        } catch (SQLiteException e) {
            Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
        } finally {
            sqLiteDatabase.close();
        }

        return deletedRowCount;
    }
}
