package com.goofy2.cloud;

import java.util.Date;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.goofy2.cloud.utils.JSONUtils;
import com.goofy2.cloud.R;

public abstract class CloudAppsActivity extends CloudListActivity {
	private ImageButton btnRefresh;

    protected void setContent(){
	    setContentView(R.layout.list);
	
    }
	
	@Override
	protected String getImageUrl(JSONObject item) throws JSONException {
		return item.getString(App.ICON);
	}

	@Override
	protected CloudBaseAdapter getAdapter() {
		return new StreamAdapter(this, mListData, mLoadingImages);
	}

	protected void onClickItem(int position) throws JSONException {
    	JSONObject json = mListData.getJSONObject(position);
    	App app = new App(json);
		openApp(app.getPackage());
	}

	private class OnClickListener_btnRefresh implements Button.OnClickListener {
		@Override
		public void onClick(View v) {
			refresh();
		}
		
	}

	@Override
	protected void onLongClickItem(int position) throws JSONException {
		// TODO Auto-generated method stub
		
	}

}
