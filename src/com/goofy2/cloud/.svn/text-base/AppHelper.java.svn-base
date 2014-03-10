package com.goofy2.cloud;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.goofy2.cloud.utils.*;
import com.goofy2.cloud.R;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import static android.provider.BaseColumns._ID;

public class AppHelper extends CloudHelper {
	// define table and column name for compilation checking.
	public static final String TABLE_NAME = "apps";
	public static final String ID = _ID;
	public static final String PACKAGE = "package";
	public static final String NAME = "name";
	public static final String DETAILS = "details";
	//public static final String IS_CLOUDED = "is_clouded";
	
	
	protected Context mContext;
	
	public AppHelper(Context context) {
		super(context);
		mContext = context;
	}

	public long addApp(App app){
		SQLiteDatabase db = this.getWritableDatabase();
		long ret = addApp(db, app);
		Utils.closeDB(db);
		return ret;
	}
	public long addApp(SQLiteDatabase db, App app){
		ContentValues values = new ContentValues();
		values.put(PACKAGE, app.getPackage());
		values.put(NAME, app.getName());
		values.put(DETAILS, app.getJSON().toString());
		long ret = db.insertOrThrow(TABLE_NAME, null, values);
		return ret;
	}

	public Cursor getApps(SQLiteDatabase db){
		Cursor ret = db.query(TABLE_NAME, null, null, null, null, null, NAME + "," + PACKAGE);
		return ret;
	}

	public JSONArray getApps(){
		JSONArray ret = new JSONArray();
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = getApps(db);
		while (cursor.moveToNext()){
			try {
				JSONObject json = new JSONObject(cursor.getString(cursor.getColumnIndexOrThrow(DETAILS)));
				ret.put(json);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		cursor.close();
		Utils.closeDB(db);
		return ret;
	}

	public int getAppCount(){
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor c = db.query(TABLE_NAME, new String[]{"count(*)"}, null, null, null, null, null);
		c.moveToFirst();
		int ret = c.getInt(0);
		c.close();
		Utils.closeDB(db);
		return ret;
	}

	public void clearAll(){
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(TABLE_NAME, null, null);
		Utils.closeDB(db);
	}

	public App deleteApp(String packageName){
		SQLiteDatabase db = this.getWritableDatabase();
		App app = getApp(db, packageName);
		db.delete(TABLE_NAME, PACKAGE + "='" + packageName + "'", null);
		Utils.closeDB(db);
		return app;
	}

	public void updateOrAddApp(App app){
		SQLiteDatabase db = this.getWritableDatabase();
		updateOrAddApp(db, app);
		Utils.closeDB(db);
	}

	public void updateOrAddApp(SQLiteDatabase db, App app){
		ContentValues values = new ContentValues();
		values.put(PACKAGE, app.getPackage());
		values.put(NAME, app.getName());
		values.put(DETAILS, app.getJSON().toString());
		int ret = db.update(TABLE_NAME, values, PACKAGE + "='" + app.getPackage() + "'", null);
		if(ret == 0) addApp(db, app);
	}

	public App getApp(String packageName){
		SQLiteDatabase db = this.getReadableDatabase();
		App ret = getApp(db, packageName);
		Utils.closeDB(db);
		return ret;
	}

	public App getApp(SQLiteDatabase db, String packageName){
		Cursor cursor = db.query(TABLE_NAME, null, PACKAGE + "='" + packageName + "'", null, null, null, null, "1");
		App ret = null;
		if (cursor.moveToNext()){
			JSONObject json;
			try {
				json = new JSONObject(cursor.getString(cursor.getColumnIndexOrThrow(DETAILS)));
				ret = new App(json);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		cursor.close();
		return ret;
	}

	static String getImageFileName(String relative_url){
		return Const.TMP_FOLDER + "/" + relative_url.replace("/", "_");
	}
	static Boolean saveImageToFile(Context context, String relative_url, int timeout, HashMap<String, Integer> loadingImages){
		Boolean ret = false;
		String fileName = getImageFileName(relative_url);
		File f = new File(fileName);
		if(f.length() == 0){ // not exists or size is 0
			if(loadingImages == null) loadingImages = new HashMap<String, Integer>();
			if(loadingImages.size()<=Const.MULITI_DOWNLOADING){
				Log.v("Bannka", "Bannka FeedHelper saving Image: " + relative_url);
				loadingImages.put(relative_url, 1);
				try {
					//DownloadImage.toFile(Const.HTTP_PREFIX + relative_url , f, timeout);
					DownloadImage.toFile(Utils.getStaticHttpPrefix(context) + relative_url , f, timeout);
					ret = true;
				} catch (Exception e) {
					e.printStackTrace();
				} catch (OutOfMemoryError e){
					Utils.showToast(context, "Out of memory");
					e.printStackTrace();
				}
				loadingImages.remove(relative_url);
				Log.v("Bannka", "Bannka FeedHelper saved Image: " + relative_url);
			}
		}
		return ret;
	}

	static public Bitmap getImageFromFile(Context context, String relative_url){
		if(relative_url == null) return null;
		String pathName = getImageFileName(relative_url);
		Bitmap bm = null;
//        BitmapFactory.Options o2 = new BitmapFactory.Options();
//        o2.inSampleSize = 1;
//		Bitmap bm = BitmapFactory.decodeFile(pathName, o2);
		try{
			bm = BitmapFactory.decodeFile(pathName);
//bms.add(bm);
//for(int i=1; i<=5; i++){
//	bms.add(BitmapFactory.decodeFile(pathName));
//	long mem = Runtime.getRuntime().freeMemory();
//	Log.w("", "memory left "+mem/1024+"K added bitmap "+ bms.size());
//}
		}catch(OutOfMemoryError e){
			Utils.showToast(context, "Out of memory");
			e.printStackTrace();
		}
//		if(bm == null){
//			File f = new File(pathName);
//			if(f.exists()) f.delete();
//		}
		return bm;
	}

}
