package db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by rain on 2016/10/14.
 */

public class WebcheDBHelper extends SQLiteOpenHelper {

      public WebcheDBHelper(Context context,int version){
          super(context, "webCache.db", null, version);
      }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table if not exists Cache (id INTEGER primary key autoincrement,newsId INTEGER unique,json text)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
