package com.goofy2.cloud;

import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.goofy2.cloud.OpenApp.ProgressBroadcastReceiver;
import com.goofy2.cloud.utils.JSONUtils;
import com.goofy2.cloud.R;

public class MyApps extends CloudAppsActivity {
	//protected JSONArray mMyApps = new JSONArray();
	protected CacheProgressBroadcastReceiver mCacheProgressReceiver = new CacheProgressBroadcastReceiver();
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
		registerReceiver(mCacheProgressReceiver, new IntentFilter(Const.BROADCAST_CACHE_APPS_PROGRESS));
    }

    @Override
    public void onDestroy(){
		unregisterReceiver(mCacheProgressReceiver);
    	super.onDestroy();
    }

	@Override
	protected void loadingMore(){
		// disable auto loading
	}	
	@Override
    protected void loadedMore(){
		txtTitle.setText(String.format(getString(R.string.app_count), mListData.length()));
    }

	@Override
	synchronized protected String loadStream(String url, String lastId) {
		Log.d("Cloud", "Cloud MyApps loadStream: " + lastId);
    	if(lastId == null){
    		mLoadingImages.clear();
    		mListData = new JSONArray();
    	}else{
    		//url += "&max_id=" + lastId;
    	}
		String err = null;
		try{
			AppHelper helper = new AppHelper(MyApps.this);
			mListData = JSONUtils.appendArray(mListData, helper.getApps());
			mAdapter.setData(mListData);
		}catch (Exception e){
//	    	Utils.alertTitle(this, getString(R.string.err_no_network_title), e.getMessage());
			err = e.getMessage();
			Log.e("Cloud", "Cloud CloudListActivity loadStream err: " + err);
		}
		return err;
	}

	@Override
	protected String getUrl() {
		return "";
	}

	@Override
	protected JSONArray getListArray(String result) throws JSONException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void onClickHeader() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected View getHeader() {
		// TODO Auto-generated method stub
		return null;
	}

	protected void refreshDb(){
		//showDialog(0);
//		final Handler handler = new Handler();
		new Thread() {
			public void run(){
				Utils.cacheMyApps(MyApps.this);
//				handler.post(new Runnable(){
//					public void run(){
//						refreshWithoutLoading();
//					}
//				});
			}
		}.start();
	}
	
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        mMenu = menu;
        // Inflate the currently selected menu XML resource.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.my_apps, menu);
        //setNoticeMenu();        		
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	if(super.onOptionsItemSelected(item)) return true;
    	switch (item.getItemId()) {
            case R.id.refresh:
            	refreshDb();
                return true;
            default:
                // Don't toast text when a submenu is clicked
                if (!item.hasSubMenu()) {
                    Toast.makeText(this, item.getTitle(), Toast.LENGTH_SHORT).show();
                    return true;
                }
                break;
        }
        
        return false;
    }
	
    protected class CacheProgressBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction().equals(Const.BROADCAST_CACHE_APPS_PROGRESS)){
            	int count = intent.getIntExtra(Const.KEY_COUNT, 0);
        		if(count > 0) txtTitle.setText(String.format(getString(R.string.app_count), count));
        		boolean finished = intent.getBooleanExtra(Const.KEY_FINISHED, false);
        		boolean refresh = intent.getBooleanExtra(Const.KEY_REFRESH, false);
        		
        		if(finished){
        			refreshWithoutLoading();
        			loading.setVisibility(View.GONE);
        		}else{
        			loading.setVisibility(View.VISIBLE);
        			if(refresh)
        				refreshWithoutLoading();
        		}
            }
        }
    }
}
