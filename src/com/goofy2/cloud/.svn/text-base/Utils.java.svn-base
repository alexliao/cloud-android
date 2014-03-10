package com.goofy2.cloud;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.goofy2.cloud.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

public class Utils {

	public final static void alertTitle(Context context, String title, String msg){
		AlertDialog.Builder ad = new AlertDialog.Builder(context);
		ad.setTitle(title);
		ad.setMessage(msg);
		ad.show();
	}

	public final static void alert(Context context, String msg){
		AlertDialog.Builder ad = new AlertDialog.Builder(context);
		//ad.setTitle("");
		ad.setMessage(msg);
		ad.show();
	}

	public final static void showToast(Context context, CharSequence text) {
		Toast.makeText(context, text, Toast.LENGTH_LONG).show();
	}

	/** 
	     * 检测网络是否连接（注：需要在配置文件即AndroidManifest.xml加入权限） 
	     *  
	     * @param context 
	     * @return true : 网络连接成功 
	     * @return false : 网络连接失败 
	* */  
	public static boolean isNetworkAvailable(Context context) {  
	    // 获取手机所有连接管理对象（包括对wi-fi,net等连接的管理）  
	   ConnectivityManager connectivity = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);  
	   if (connectivity != null) {  
	       // 获取网络连接管理的对象  
		   NetworkInfo info = connectivity.getActiveNetworkInfo();  
		   if (info != null) {  
		       // 判断当前网络是否已经连接
//	           if (info.getState() == NetworkInfo.State.CONNECTED) {  
//	               return true;  
//	           }  
			   return info.isAvailable();
	       }  
		}
	   return false;  
	}
	
	public static void setCurrentUser(Context context, JSONObject user){
		SharedPreferences pref = context.getSharedPreferences(Const.PREFS, Activity.MODE_PRIVATE);
		SharedPreferences.Editor editor = pref.edit();
		editor.putString("currentUser", user.toString());
		editor.commit();
	}

	public static JSONObject getCurrentUser(Context context){
		JSONObject user = null;
		SharedPreferences pref = context.getSharedPreferences(Const.PREFS, Activity.MODE_PRIVATE);
		String str = pref.getString("currentUser", null);
		if(str != null){
			try {
				user = new JSONObject(str);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return user;
	}

	public static String getCurrentUserId(Context context){
		String ret = null;
		try {
			JSONObject user = getCurrentUser(context);
			if(user != null) ret = user.getString("id");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return ret;
	}
	public static String getCurrentUserUsername(Context context){
		String ret = null;
		try {
			ret = getCurrentUser(context).getString("username");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return ret;
	}

	public static String getCurrentUserKey(Context context){
		String ret = null;
		try {
			ret = getCurrentUser(context).getString("key");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return ret;
	}
	
	public static Notification getDefaultNotification(String text){
		int icon = R.drawable.icon;
		long when = System.currentTimeMillis();
		Notification notification = new Notification(icon, text, when);
		notification.flags |= Notification.FLAG_AUTO_CANCEL;
		//notification.defaults = Notification.DEFAULT_SOUND;
		return notification;
	}

	public static String getUserPrefString(Context context, String key){
		String user_id = getCurrentUserId(context);
		SharedPreferences pref = context.getSharedPreferences(Const.PREFS+user_id, Activity.MODE_PRIVATE);
		return pref.getString(key, null);
	}

	protected static void setUserPrefString(Context context, String key, String value){
		String user_id = getCurrentUserId(context);
		SharedPreferences pref = context.getSharedPreferences(Const.PREFS+user_id, Activity.MODE_PRIVATE);
		SharedPreferences.Editor editor = pref.edit();
		editor.putString(key, value);
		editor.commit();
	}
	
	public static String getPrefString(Context context, String key, String defValue){
		SharedPreferences pref = context.getSharedPreferences(Const.PREFS, Activity.MODE_PRIVATE);
		return pref.getString(key, defValue);
	}

	protected static void setPrefString(Context context, String key, String value){
		SharedPreferences pref = context.getSharedPreferences(Const.PREFS, Activity.MODE_PRIVATE);
		SharedPreferences.Editor editor = pref.edit();
		editor.putString(key, value);
		editor.commit();
	}

	public static String getStaticHttpPrefix(Context context){
		return getPrefString(context, "static_http_prefix", Const.HTTP_PREFIX);
	}
	
	public static String formatTime(Date time){
        DateFormat formatter;
        Date now = new Date();
        if(time.getDate() == now.getDate())
        	//formatter = new SimpleDateFormat(todayFormat);
        	formatter = DateFormat.getTimeInstance(DateFormat.SHORT);
        else
        	//formatter = new SimpleDateFormat();
        	formatter = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT);
        
		return formatter.format(time);
	}
	
	public static void clearImageCache(int cacheSize){
		try{
			File dir = new File(Const.TMP_FOLDER);
			File[] files = dir.listFiles();
			//ArrayList<File> arrFiles = new ArrayList<File>();
			Arrays.sort(files, new CompareCacheFile());
			int delNum = files.length - cacheSize; 
			Log.v("Cloud", "Cloud clearImageCache will clear " + delNum +" files");
			for(int i=0; i < delNum; i++){ 
				Log.v("Cloud", "Cloud clearImageCache: " + files[i].getName());
				files[i].delete();
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public static class CompareCacheFile implements Comparator<File> {
        @Override
        public int compare(File file1, File file2) {
            return (int) (file1.lastModified()-file2.lastModified());
        }
    }
	
	static public void closeDB(SQLiteDatabase db){
		try{
			db.close();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	static public String getError(String strResult){
		String ret = null;
		JSONObject json;
		try {
			json = new JSONObject(strResult);
			ret = json.getString("error_code")+": "+json.getString("error_message");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return ret;
	}

	static public String getLoginParameters(Context context){
		String ret = "";
		ret += "user_id=" + Utils.getCurrentUserId(context) + "&user_key=" + Utils.getCurrentUserKey(context);;
		return ret;	
	}


	static public String getUserIdInCloud(Context context) {
		if(Utils.getCurrentUser(context) == null){
			cacheAppsStatus(context, new AppHelper(context).getApps());
		}
		String ret = Utils.getCurrentUserId(context);
		return ret;
	}

	static public JSONArray cacheMyApps(Context context) {
		JSONArray ret = new JSONArray();
		PackageManager pm = context.getPackageManager();
		List<PackageInfo> pckInfos = pm.getInstalledPackages(PackageManager.GET_SIGNATURES);
		AppHelper helper = new AppHelper(context);
		helper.clearAll();
		SQLiteDatabase db = helper.getWritableDatabase();
		int count = 0;
		for(int i=0; i<pckInfos.size(); i++){
			PackageInfo info = pckInfos.get(i);
        	if(!info.applicationInfo.sourceDir.matches("/system.*")){
				App app = new App(pm, info);
				ret.put(app.getJSON());
				helper.addApp(db, app);
				count++;
				Intent intent = new Intent(Const.BROADCAST_CACHE_APPS_PROGRESS);
				intent.putExtra(Const.KEY_COUNT, count);
				context.sendBroadcast(intent);
			}
		}
		Utils.closeDB(db);
		Intent intent = new Intent(Const.BROADCAST_CACHE_APPS_PROGRESS);
		intent.putExtra(Const.KEY_REFRESH, true);
		context.sendBroadcast(intent);

		cacheAppsStatus(context, ret);
		intent = new Intent(Const.BROADCAST_CACHE_APPS_PROGRESS);
		intent.putExtra(Const.KEY_FINISHED, true);
		context.sendBroadcast(intent);
		return ret;
	}
	
    public static void cacheAppsStatus(Context context, JSONArray apps){
		try {
			Log.v("Cloud", "Cloud Utils.cacheAppsStatus start...");
			String strResult = null;
			String url = Const.HTTP_PREFIX + "/apps/status_list2?" + Utils.getClientParameters(context);
			HttpPost httpReq = new HttpPost(url);
			List <NameValuePair> params = new ArrayList <NameValuePair>();
			params.add(new BasicNameValuePair("format", "json"));
			params.add(new BasicNameValuePair("apps", apps.toString()));
			httpReq.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
			HttpResponse httpResp = new DefaultHttpClient().execute(httpReq);
			strResult = EntityUtils.toString(httpResp.getEntity());
			if(httpResp.getStatusLine().getStatusCode() == 200){
				JSONObject hash = new JSONObject(strResult);
				apps = hash.optJSONArray("apps");
				AppHelper helper =  new AppHelper(context);
				SQLiteDatabase db = helper.getWritableDatabase();
				for(int i=0; i<apps.length(); i++){
					JSONObject json = apps.getJSONObject(i);
					App cloud_app = new App(json);
					App local_app = helper.getApp(db, cloud_app.getPackage());
					if(cloud_app.getVersionCode()>=local_app.getVersionCode())
						local_app.getJSON().put(App.CLOUD_ID, cloud_app.getCloudId());
					else
						local_app.getJSON().put(App.CLOUD_ID, null);
					helper.updateOrAddApp(db, local_app);
				}
				Utils.closeDB(db);

				JSONObject user = hash.optJSONObject("user");
				Utils.setCurrentUser(context, user);
				
				Log.v("Cloud", "Cloud Utils.cacheAppsStatus done");
			}else{
				JSONObject json = new JSONObject(strResult); 
				Log.e("Cloud", "Cloud Utils.cacheAppsStatus error: " + json.optString("error_message","error"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
    }

    static public JSONObject getAppStatus(Context context, App app) throws Exception{
		String strResult = null;
		JSONObject ret = null;
		String url = Const.HTTP_PREFIX + "/apps/status?format=json&package=" + URLEncoder.encode(app.getPackage()) + "&signature=" + URLEncoder.encode(app.getSignature()) + "&name=" + URLEncoder.encode(app.getName()) + "&" + Utils.getClientParameters(context);
		HttpGet httpReq = new HttpGet(url);
//		HttpParams httpParameters = new BasicHttpParams();
//		HttpConnectionParams.setConnectionTimeout(httpParameters, Const.HTTP_TIMEOUT);
//		httpReq.setParams(httpParameters);
		//Log.d("Cloud", "Cloud Update getUpdate: " + url);
		HttpResponse httpResp = new DefaultHttpClient().execute(httpReq);
		strResult = EntityUtils.toString(httpResp.getEntity());
		JSONObject json = new JSONObject(strResult);
		if(httpResp.getStatusLine().getStatusCode() == 200){
			ret = json;
		}else{
			throw new Exception(json.optString("error_message","error"));
		}
		return ret;
    }

	static public String getClientParameters(Context context){
		String ret = "";
    	PackageInfo pi;
		try {
			pi = context.getPackageManager().getPackageInfo(context.getPackageName(), PackageManager.GET_META_DATA);
			Locale lc = Locale.getDefault();
			TelephonyManager tm = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);  
			String imei = tm.getDeviceId();  
			ret = "client_version=" + pi.versionCode + "&lang=" + lc.getLanguage() + "&country=" + lc.getCountry() + "&imei=" + imei;
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ret;	
	}

	static public boolean checkVersion(Context context){
    	boolean isObsolete = false;
		try {
	    	String actionURL = Const.HTTP_PREFIX + "/account/app_version?format=json&"+Utils.getClientParameters(context);
Log.d("","Cloud checkVersion: " + actionURL);	    	
			HttpGet httpReq = new HttpGet(actionURL);
			Log.d("Cloud", "Cloud checking version ... ");
			final HttpResponse httpResp = new DefaultHttpClient().execute(httpReq);
			if(httpResp.getStatusLine().getStatusCode() == 200){
				try {
					String result = EntityUtils.toString(httpResp.getEntity());
					Utils.setPrefString(context, "version_changes", result);
					Utils.setPrefString(context, "check_version_time", ""+System.currentTimeMillis());
					JSONArray changes = new JSONArray(result);
					if(changes.length() > 0) isObsolete = true;
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return isObsolete;
    }

	public static void reportRemove(Context context, App app) {
		try {
	    	String actionURL = Const.HTTP_PREFIX + "/apps/remove?format=json&package="+URLEncoder.encode(app.getPackage())+"&signature="+URLEncoder.encode(app.getSignature())+"&"+Utils.getClientParameters(context);
Log.d("Cloud","Cloud reportRemove: " + actionURL);	    	
			HttpPost httpReq = new HttpPost(actionURL);
			final HttpResponse httpResp = new DefaultHttpClient().execute(httpReq);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void sendNotify(Context context, Notification noti, int notificationId){
		NotificationManager nm = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
		nm.notify(notificationId, noti);
	}

	public static void cancelNotify(Context context, App app){
		NotificationManager nm = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
		nm.cancel(app.getPackage().hashCode());
    }

//	public static String size4Human(long size){
//		long k = size/1024;
//		long m = 
//	}
}
