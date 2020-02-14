package com.example.doyoudrink;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.util.Date;

public class DbManager extends SQLiteOpenHelper {
    public String userTable =   "CREATE TABLE user (" +
                                "_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                                "username VARCHAR(20)," +
                                "pass VARCHAR(20)," +
                                "name VARCHAR(30)," +
                                "birth DATE" +
                                ");";
    public String likeDrinks =  "CREATE TABLE likedrinks (" +
                                "userId INTEGER," +
                                "drinkId VARCHAR(8)" +
                                ");";
    public String saveState =   "CREATE TABLE save (" +
                                "userId INTEGER" +
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
            db.execSQL(saveState);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int getSaveUser() {
        SQLiteDatabase db = this.getReadableDatabase();
        try {
            Cursor c = db.rawQuery(" SELECT userId FROM save", null);
            if(c.getCount() > 0) {
                c.moveToFirst();
                int id = c.getInt(0);
                Cursor c2 = db.rawQuery("SELECT username, name, pass FROM user WHERE _id=?", new String[] {String.valueOf(id)});
                c2.moveToFirst();
                AppScreen.user = new User(id, c2.getString(0), c2.getString(1), c2.getString(2));
                return 1;
            }else {
                return -2;
            }

        }catch (Exception ex) {
            ex.printStackTrace();
            return -1;
        }
    }

    public void removeSaved(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            db.delete("save", "userId=?", new String[] {String.valueOf(id)});
        }catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    public int addUser(String username, String pass, String name, String birth) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("username", username);
        cv.put("pass", pass);
        cv.put("name", name);
        cv.put("birth", birth);

        try {
            Cursor c =  db.rawQuery("SELECT * FROM user WHERE username=?", new String[] {username});
            if(c.getCount() > 0) {
                return -2;
            }
            long id = db.insert("user", null, cv);
            return 1;
        }catch(Exception ex){
            ex.printStackTrace();
            return -1;
        }
    }

    public int deleteAcount(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        try {
            int ret;
            String[] args = new String[] {String.valueOf(id)};
            //Cursor c = db.rawQuery(" SELECT COUNT(drinkId) FROM likedrinks WHERE userId=? AND drinkId=?", args);

            db.delete("likedrinks", "userId=?", args);
            db.delete("user", "_id=?", args);
            db.delete("save", "userId=?", new String[] {String.valueOf(id)});
            ret = 1;

            return ret;
        }catch (Exception ex) {
            return -1;
        }
    }

    public int changeUser(int id, String username, String pass, String name) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("username", username);
        cv.put("pass", pass);
        cv.put("name", name);
        try {
            db.update("user", cv, "_id=" + id, null);
            AppScreen.user = new User(id, username, name, pass);
            return 1;
        }catch(Exception ex){
            ex.printStackTrace();
            return -1;
        }
    }

    public int makeLogin(String user, String pass) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        try {
            int ret;
            String[] args = new String[] {user, pass};
            Cursor c = db.rawQuery(" SELECT COUNT(*), _id, name, pass FROM user WHERE username=? AND pass=? GROUP BY _id", args);
            c.moveToFirst();
            int num = 0;
            if(c.getCount() > 0) {
                num = c.getInt(0);
            }
            if(num > 0) {
                ret = c.getInt(1);
                AppScreen.user = new User(ret, user, c.getString(2), c.getString(3));
                cv.put("userId", ret);
                db.insert("save", null, cv);
            }else {
                ret = -2;
            }
            return ret;
        }catch (Exception ex) {
            ex.printStackTrace();
            return -1;
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
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        try {
            db.execSQL(userTable);
            db.execSQL(likeDrinks);
            db.execSQL(saveState);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
