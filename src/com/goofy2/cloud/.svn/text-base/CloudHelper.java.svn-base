package com.goofy2.cloud;

import java.io.File;

import org.json.JSONObject;

import com.goofy2.cloud.utils.*;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import static android.provider.BaseColumns._ID;

public class CloudHelper extends SQLiteOpenHelper {
	private static final String DATABASE_NAME = "grapps.db";
	private static final int DATABASE_VERSION = 3;
	
	//protected SQLiteDatabase mDb;
	protected Cursor mCursor = null;

	public CloudHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("create table " + AppHelper.TABLE_NAME + " (" 
				+ AppHelper.ID +" integer primary key autoincrement, " 
				+ AppHelper.NAME +" text, " 
				+ AppHelper.PACKAGE +" text, " 
				//+ AppHelper.IS_CLOUDED + " integer default 0, " 
				+ AppHelper.DETAILS + " text" 
				+" );" 
				+"create index fi1 on " + AppHelper.TABLE_NAME + "(" + AppHelper.NAME + "," + AppHelper.PACKAGE + ");"
				); 
		
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.d("Cloud", "Cloud Upgrading from version " + oldVersion + " to " + newVersion + ", which will destroy all old data.");
		db.execSQL("drop table if exists " + AppHelper.TABLE_NAME + ";");
		onCreate(db);
	}

//	protected void open_db(){
//		open_db(false);
//	}
//
//	protected void open_db(boolean readonly){
//		if(readonly)
//			mDb = this.getReadableDatabase();
//		else
//			mDb = this.getWritableDatabase();
//	}
//	
//	protected void close_db(){
//		if(mDb != null) mDb.close();
//	}
}
