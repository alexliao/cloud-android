package com.goofy2.cloud;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.util.Log;

public class AppRefresher extends BroadcastReceiver {

    @Override
    public void onReceive(final Context context, Intent intent) {
       	//Log.d("", "Cloud AppRefresher working...");  
       	//Utils.cacheMyApps(context);
       	//Log.d("", "Cloud AppRefresher done.");
       	String action = intent.getAction();
       	String packageName = intent.getData().getSchemeSpecificPart();
       	Log.d("", "Cloud AppRefresher receive: " + action + " " + packageName);  
       	PackageManager pm = context.getPackageManager();
		try {
			AppHelper helper = new AppHelper(context);
	        if(action.equals(Intent.ACTION_PACKAGE_ADDED)){
				PackageInfo info = pm.getPackageInfo(packageName, PackageManager.GET_SIGNATURES);
				App app = new App(pm, info);
				setStatus(context, app);
				helper.addApp(app);
		       	Log.v("", "Cloud AppRefresher add: " + packageName);  
	        }else if(action.equals(Intent.ACTION_PACKAGE_REMOVED)){
	        	App app = helper.deleteApp(packageName);
	        	Utils.reportRemove(context, app);
		       	Log.v("", "Cloud AppRefresher delete: " + packageName);  
	        }else if(action.equals(Intent.ACTION_PACKAGE_REPLACED)){
				PackageInfo info = pm.getPackageInfo(packageName, PackageManager.GET_SIGNATURES);
				App app = new App(pm, info);
				setStatus(context, app);
	        	helper.updateOrAddApp(app);
		       	Log.v("", "Cloud AppRefresher update: " + packageName);  
	        }
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
        
    }

    protected void setStatus(Context context, App app){
		JSONObject json = null;
		try {
			json = Utils.getAppStatus(context, app);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		if(json != null){
			App cloud_app = new App(json);
			if(cloud_app.getVersionCode() >= app.getVersionCode())
				try {
					app.getJSON().put(App.CLOUD_ID, cloud_app.getCloudId());
				} catch (JSONException e) {
					e.printStackTrace();
				}
		}
    }
}