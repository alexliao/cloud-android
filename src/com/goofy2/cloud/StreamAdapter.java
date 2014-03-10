package com.goofy2.cloud;

import java.util.Date;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.goofy2.cloud.R;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class StreamAdapter extends CloudBaseAdapter {
	 

	public StreamAdapter(Context context, JSONArray stream,	HashMap<String, Integer> loadingImages) {
		super(context, stream, loadingImages);
	}

	public void bindView(View view, Context context, JSONObject jsonApp) {
		try {
			//String id = update.getString("id");
			//FeedHelper.bindUpdate(mContext, view, update, this, mLoadingImages, false);
			App app = new App(jsonApp);
			
			TextView tv;
			tv = (TextView)view.findViewById(R.id.txtName);
			tv.setText(app.getName());
			
			ImageView iv = (ImageView)view.findViewById(R.id.icon);
			Bitmap bm = null;
			String url = app.getIconName();
			bm = AppHelper.getImageFromFile(context, url); // file store 
			iv.setImageBitmap(bm);
			
//			String time = Utils.formatTimeDistance(context, new Date((long) (dTime*1000)));
//			tv = (TextView)v.findViewById(R.id.txtTime);
//			tv.setText(time);

			iv = (ImageView)view.findViewById(R.id.cloud);
			if(app.isInCloud()) iv.setVisibility(View.VISIBLE);
			else iv.setVisibility(View.GONE);
			
		} catch (Exception e) {
			Log.d("Cloud", "Cloud StreamAdapter - bindView err: " + e.getMessage());
		}
	}

	public View newView(Context context, ViewGroup parent) {
		View ret = null;
		int resId = R.layout.app_row;
		ret = mInflater.inflate(resId, parent, false);
		return ret;
	}


}
