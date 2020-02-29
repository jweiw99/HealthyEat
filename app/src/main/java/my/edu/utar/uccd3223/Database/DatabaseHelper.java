package my.edu.utar.uccd3223.Database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import my.edu.utar.uccd3223.util.Config;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static DatabaseHelper databaseHelper;

    // All Static variables
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = Config.DATABASE_NAME;

    // Constructor
    private DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public static synchronized DatabaseHelper getInstance(Context context) {
        if (databaseHelper == null) {
            databaseHelper = new DatabaseHelper(context);
        }
        return databaseHelper;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        // Create tables SQL execution
        String CREATE_USER_TABLE = "CREATE TABLE " + Config.TABLE_USER + "("
                + Config.COLUMN_USER_ID + " INTEGER PRIMARY KEY, "
                + Config.COLUMN_USER_AGE + " INTEGER NOT NULL, "
                + Config.COLUMN_USER_WEIGHT + " REAL NOT NULL, "
                + Config.COLUMN_USER_HEIGHT + " REAL NOT NULL, "
                + Config.COLUMN_USER_GENDER + " INTEGER NOT NULL, "
                + Config.COLUMN_USER_WEIGHT_GOAL + " INTEGER NOT NULL, "
                + Config.COLUMN_USER_ACTIVITY_LEVEL + " INTEGER NOT NULL "
                + ");";

        db.execSQL(CREATE_USER_TABLE);

        // Create tables SQL execution
        String CREATE_USER_CALORIES_TABLE = "CREATE TABLE " + Config.TABLE_USER_CALORIES + "("
                + Config.COLUMN_USER_ID + " INTEGER, "
                + Config.COLUMN_USER_CALORIES_DATE + " INTEGER, "
                + Config.COLUMN_USER_MAX_CALORIES + " INTEGER NOT NULL, "
                + Config.COLUMN_USER_CALORIES_TAKEN + " INTEGER NOT NULL, "
                + "PRIMARY KEY (" + Config.COLUMN_USER_ID + ", " + Config.COLUMN_USER_CALORIES_DATE + ")"
                + ");";

        db.execSQL(CREATE_USER_CALORIES_TABLE);

        // Create tables SQL execution
        String CREATE_USER_FOOD_TABLE = "CREATE TABLE " + Config.TABLE_USER_FOOD + "("
                + Config.COLUMN_FOOD_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + Config.COLUMN_USER_ID + " INTEGER, "
                + Config.COLUMN_USER_FOOD_DATE + " INTEGER, "
                + Config.COLUMN_USER_FOOD_ID + " INTEGER NOT NULL "
                + ");";

        db.execSQL(CREATE_USER_FOOD_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + Config.TABLE_USER);

        // Create tables again
        onCreate(db);
    }
}
