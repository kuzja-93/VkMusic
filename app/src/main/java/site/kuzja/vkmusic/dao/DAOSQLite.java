package site.kuzja.vkmusic.dao;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import site.kuzja.vkmusic.api.objects.UserActor;

/**
 * Created by user on 16.02.17.
 */

public class DAOSQLite implements DAOImpl {
    SQLiteDatabase dataBase;
    public DAOSQLite(Context context) {
        dataBase = new DBHelper(context).getWritableDatabase();
    }

    public UserActor getUserActor() {
        Cursor c = dataBase.query("user", null, null, null, null, null, null);
        if (!c.moveToFirst())
            return null;
        return new UserActor(c.getInt(c.getColumnIndex("user_id")),
                c.getString(c.getColumnIndex("access_token")),
                c.getInt(c.getColumnIndex("expires_in")));
    }

    public void saveUserActor(UserActor actor) {
        ContentValues cv = new ContentValues();
        cv.put("user_id", actor.getUserID());
        cv.put("access_token", actor.getAccessToken());
        cv.put("expires_in", actor.getExpiresIn());
        dataBase.insert("user", null, cv);
    }

    public void clear() {
        dataBase.delete("user", null, null);
    }
}

class DBHelper extends SQLiteOpenHelper {
    public DBHelper(Context context) {
        super(context, "VkMusic", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table user ("
                + "user_id integer primary key ,"
                + "access_token text,"
                + "expires_in integer" + ");");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
