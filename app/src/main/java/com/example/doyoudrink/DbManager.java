package com.example.doyoudrink;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DbManager extends SQLiteOpenHelper {
    public String userTable =   "CREATE TABLE user (" +
                                "_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                                "username VARCHAR(20)," +
                                "pass VARCHAR(20)" +
                                ");";
    public String likeDrinks =  "CREATE TABLE likedrinks (" +
                                "userId INTEGER," +
                                "drinkId VARCHAR(8)" +
                                ");";
    public Context c;

    public DbManager(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        this.c = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            db.execSQL(userTable);
            db.execSQL(likeDrinks);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String addUser(String username, String pass) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("username", username);
        cv.put("pass", pass);
        try {
            long id = db.insert("user", null, cv);
            return String.valueOf(id);
        }catch(Exception ex){
            return ex.toString();
        }
    }

    public int likeDrink(int userID, String drinkId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        try {
            int ret;
            String[] args = new String[] {String.valueOf(userID), drinkId};
            Cursor c = db.rawQuery(" SELECT COUNT(drinkId) FROM likedrinks WHERE userId=? AND drinkId=?", args);
            c.moveToFirst();
            int num = c.getInt(0);
            if(num > 0) {
                db.delete("likedrinks", "userId=? AND drinkId=?", args);
                ret = 0;
            }else {
                cv.put("userId", userID);
                cv.put("drinkId", drinkId);
                db.insert("likedrinks", null, cv);
                ret = 1;
            }
            return ret;
        }catch (Exception ex) {
            return -1;
        }
    }

    public boolean doLikeDrink(int userID, String drinkId) {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            String[] args = new String[] {String.valueOf(userID), drinkId};
            Cursor c = db.rawQuery(" SELECT COUNT(drinkId) FROM likedrinks WHERE userId=? AND drinkId=?", args);
            c.moveToFirst();
            int num = c.getInt(0);
            if(num > 0) {
                return true;
            }else {
                return false;
            }
        }catch (Exception ex) {
            return false;
        }
    }

    public Cursor getLikeDrinks(int userID) {
        SQLiteDatabase db = this.getReadableDatabase();
        try {
            String[] args = new String[] {String.valueOf(userID)};
            Cursor c = db.rawQuery(" SELECT drinkId FROM likedrinks WHERE userId=? ", args);
            return c;
        }catch (Exception ex) {
            return null;
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
