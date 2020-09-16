package com.example.minazakaria.thirdaid;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by Mina Zakaria on 2/16/2018.
 */

public class DBHandler extends SQLiteOpenHelper{

    //ALL USED VARIABLES ARE HERE
    private  static final int DATABASE_VERSION = 1;
    private  static final String DATABASE_NAME = "ThirdAidAppDatabase15.db";
    public static final String USER_TABLE = "Users_Table";
    public static final String COLUMN_USER_ID = "User_ID";
    public static final String COLUMN_USER_FIRST_NAME = "User_First_Name";
    public static final String COLUMN_USER_LAST_NAME = "User_Last_Name";
    public static final String COLUMN_USER_TOKEN = "User_Token";
    public static final String COLUMN_USER_DISPLAY_NAME = "User_Display_Name";
    public static final String COLUMN_USER_ROLE = "User_Role";
    public static final String COLUMN_USER_STATUS = "User_Status";
    public static final String COLUMN_USER_NOTIFICATIONS = "User_Notifications";
    public static final String COLUMN_USER_CERTIFIED = "User_Certified";
    public static final String COLUMN_USER_PHONENUMBER = "User_Phone_Number";

    public DBHandler(Context context, String name, SQLiteDatabase.CursorFactory factory, int version){
        super(context, DATABASE_NAME, factory, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String onCreateQuery = "CREATE TABLE " + USER_TABLE + " ( "+
                COLUMN_USER_ID + " VARCHAR, " +
                COLUMN_USER_FIRST_NAME + " VARCHAR, " +
                COLUMN_USER_LAST_NAME + " VARCHAR, " +
                COLUMN_USER_DISPLAY_NAME + " VARCHAR, " +
                COLUMN_USER_ROLE + " VARCHAR, " +
                COLUMN_USER_STATUS + " VARCHAR, " +
                COLUMN_USER_CERTIFIED + " VARCHAR, " +
                COLUMN_USER_NOTIFICATIONS + " VARCHAR, " +
                COLUMN_USER_PHONENUMBER + " VARCHAR, " +
                COLUMN_USER_TOKEN + " VARCHAR)";
        db.execSQL(onCreateQuery);
        Log.d("DB", "CREATED");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS " + USER_TABLE);
        onCreate(db);
    }

    //ALL METHODS ARE HERE
    public void AddUser(User user){
        ContentValues values = new ContentValues();
        values.put(COLUMN_USER_ID, user.getUserID());
        values.put(COLUMN_USER_TOKEN, user.getUserToken());
        values.put(COLUMN_USER_FIRST_NAME, user.getUserFName());
        values.put(COLUMN_USER_LAST_NAME, user.getUserLName());
        values.put(COLUMN_USER_DISPLAY_NAME, user.getUserDName());
        values.put(COLUMN_USER_ROLE, user.getUserRole());
        values.put(COLUMN_USER_STATUS, user.getUserStatus());
        values.put(COLUMN_USER_CERTIFIED, user.getUserIsCertified());
        values.put(COLUMN_USER_PHONENUMBER, user.getUserPhoneNumber());
        values.put(COLUMN_USER_NOTIFICATIONS, user.getUserNotificationStatus());
        SQLiteDatabase db = getReadableDatabase();
        db.insert(USER_TABLE, null, values);
        db.close();
        Log.d("DB", "ADDED");
    }

    public String GetID(){
        SQLiteDatabase db = getReadableDatabase();
        String query = "SELECT * FROM " + USER_TABLE + " WHERE 1";
        Cursor c = db.rawQuery(query, null);
        String output;
        c.moveToFirst();

        output = c.getString(c.getColumnIndex(COLUMN_USER_ID));

        return output;
    }

    public String GetToken(){
        SQLiteDatabase db = getReadableDatabase();
        String query = "SELECT * FROM " + USER_TABLE + " WHERE 1";
        Cursor c = db.rawQuery(query, null);
        String output;
        c.moveToFirst();

        output = c.getString(c.getColumnIndex(COLUMN_USER_TOKEN));

        return output;
    }

    public String GetRole(){
        SQLiteDatabase db = getReadableDatabase();
        String query = "SELECT * FROM " + USER_TABLE + " WHERE 1";
        Cursor c = db.rawQuery(query, null);
        String output;
        c.moveToFirst();

        output = c.getString(c.getColumnIndex(COLUMN_USER_ROLE));

        return output;
    }

    public String GetFullName(){
        SQLiteDatabase db = getReadableDatabase();
        String query = "SELECT * FROM " + USER_TABLE + " WHERE 1";
        Cursor c = db.rawQuery(query, null);
        String fName;
        String lName;
        String output;
        c.moveToFirst();

        fName = c.getString(Integer.parseInt(String.valueOf(c.getColumnIndex(COLUMN_USER_FIRST_NAME)).toLowerCase()));
        lName = c.getString(Integer.parseInt(String.valueOf(c.getColumnIndex(COLUMN_USER_LAST_NAME)).toLowerCase()));

        db.close();

        fName = fName.substring(0, 1).toUpperCase() + fName.substring(1);
        lName = lName.substring(0, 1).toUpperCase() + lName.substring(1);

        output = fName + " " + lName;

        return output;
    }

    public String GetDisplayName(){
        SQLiteDatabase db = getReadableDatabase();
        String query = "SELECT * FROM " + USER_TABLE + " WHERE 1";
        Cursor c = db.rawQuery(query, null);
        String output;
        c.moveToFirst();

        db.close();

        output= c.getString(c.getColumnIndex(COLUMN_USER_DISPLAY_NAME));

        return output;
    }

    public String GetVerifiedStatus(){
        SQLiteDatabase db = getReadableDatabase();
        String query = "SELECT * FROM " + USER_TABLE + " WHERE 1";
        Cursor c = db.rawQuery(query, null);
        String output;
        c.moveToFirst();

        db.close();

        if(c.getString(c.getColumnIndex(COLUMN_USER_STATUS)).equals("t")) {
            return "Yes";
        } else{
            return "No";
        }
    }

    public String GetIsCertified(){
        SQLiteDatabase db = getReadableDatabase();
        String query = "SELECT * FROM " + USER_TABLE + " WHERE 1";
        Cursor c = db.rawQuery(query, null);
        c.moveToFirst();

        db.close();

        if(c.getString(c.getColumnIndex(COLUMN_USER_CERTIFIED)).equals("t")) {
            return "true";
        } else{
            return "false";
        }
    }

    public String GetNotificationsStatus(){
        SQLiteDatabase db = getReadableDatabase();
        String query = "SELECT * FROM " + USER_TABLE + " WHERE 1";
        Cursor c = db.rawQuery(query, null);
        c.moveToFirst();

        db.close();

        if(c.getString(c.getColumnIndex(COLUMN_USER_NOTIFICATIONS)).equals("t")) {
            return "true";
        } else{
            return "false";
        }
    }

    public String GetPhoneNumber(){
        SQLiteDatabase db = getReadableDatabase();
        String output;
        String query = "SELECT * FROM " + USER_TABLE + " WHERE 1";
        Cursor c = db.rawQuery(query, null);
        c.moveToFirst();
        db.close();
        output = c.getString(c.getColumnIndex(COLUMN_USER_PHONENUMBER));
        return output;

    }

    public void ClearTable() {
        String query = "DELETE FROM " + USER_TABLE;
        SQLiteDatabase db = getReadableDatabase();
        db.execSQL(query);
    }

    public void setNotificationStatus(String value){
        String sql = "UPDATE "+ USER_TABLE +" SET " + COLUMN_USER_NOTIFICATIONS+ " = '"+value+"';";
        SQLiteDatabase db = getReadableDatabase();
        db.execSQL(sql);
        db.close();
    }

    public void setCertifiedStatus(String value){
        String sql = "UPDATE "+ USER_TABLE +" SET " + COLUMN_USER_CERTIFIED+ " = '"+value+"';";
        SQLiteDatabase db = getReadableDatabase();
        db.execSQL(sql);
        db.close();
    }

    public long Count(){
        SQLiteDatabase db = getReadableDatabase();
        long count = DatabaseUtils.queryNumEntries(db, USER_TABLE);
        return count;
    }
}




















