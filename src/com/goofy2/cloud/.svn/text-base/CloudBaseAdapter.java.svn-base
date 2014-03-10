package com.goofy2.cloud;

import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public abstract class CloudBaseAdapter extends BaseAdapter {
	protected Context mContext;
	protected LayoutInflater mInflater;
	protected JSONArray mListData;
	protected HashMap<String, Integer> mLoadingImages;

	public CloudBaseAdapter(Context context, JSONArray list, HashMap<String, Integer> loadingImages){
		this.mContext = context;
        mInflater = LayoutInflater.from(context);
		this.mListData = list;
		mLoadingImages = loadingImages;
	}

	@Override
	public int getCount() {
		return mListData.length();
	}

	@Override
	public Object getItem(int arg0) {
		Object ret = null;
		try {
			ret = mListData.get(arg0);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return ret;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView (int position, View convertView, ViewGroup parent){
		View v;
		if(convertView == null){
			v = newView(mContext, parent);
		}else v = convertView;
		try {
			bindView(v, mContext, mListData.getJSONObject(position));
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return v;
	}

	protected abstract void bindView(View v, Context context, JSONObject jsonObject);

	protected abstract View newView(Context context, ViewGroup parent);

	public void setData(JSONArray data) {
		mListData = data;
	}
}
