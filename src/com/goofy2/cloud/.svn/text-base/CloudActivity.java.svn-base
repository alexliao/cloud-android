package com.goofy2.cloud;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.goofy2.cloud.utils.ParamRunnable;
import com.goofy2.cloud.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.app.AlertDialog.Builder;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class CloudActivity extends Activity {
	public static final String APP = "com.goofy2.cloud.CloudActivity.APP";
	public static final String USER = "com.goofy2.cloud.CloudActivity.USER";
	public static final String UPDATE = "com.goofy2.cloud.CloudActivity.UPDATE";
	public static final String FOLLOWED = "com.goofy2.cloud.CloudActivity.FOLLOWED";
	public static final String AUTO_REFRESH = "com.goofy2.cloud.CloudActivity.AUTO_REFRESH";
	public static final String IMAGE_LOADED = "com.goofy2.cloud.IMAGE_LOADED";
	public static final String RESTART = "com.goofy2.cloud.RESTART";
	public JSONObject currentUser;
	protected Button btnSnap;
	protected ImageMessageBroadcastReceiver mImageLoadedReceiver = new ImageMessageBroadcastReceiver();
	protected RestartBroadcastReceiver mRestartReceiver = new RestartBroadcastReceiver();
	protected Menu mMenu;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //requestWindowFeature(Window.FEATURE_NO_TITLE);
        currentUser = Utils.getCurrentUser(this);
		registerReceiver(mImageLoadedReceiver, new IntentFilter(IMAGE_LOADED));
		registerReceiver(mRestartReceiver, new IntentFilter(RESTART));
    }
    
    @Override
    public void onDestroy(){
		unregisterReceiver(mImageLoadedReceiver);
		unregisterReceiver(mRestartReceiver);
    	super.onDestroy();
    }

    @Override
    public void onStart(){
    	super.onStart();
        currentUser = Utils.getCurrentUser(this);
    }

    protected void redirectAnonymous(Uri data){
		if(currentUser == null){
			finish();
//			startActivityForResult(new Intent(this, Signup.class), 0);
			Intent i = new Intent(this, Main.class);
			i.setData(data);
			startActivity(i);
		}
    }

	
	public void setCurrentUser(JSONObject user){
		currentUser = user;
		SharedPreferences pref = this.getSharedPreferences(Const.PREFS, Activity.MODE_PRIVATE);
		SharedPreferences.Editor editor = pref.edit();
		editor.putString("currentUser", user.toString());
		editor.commit();
	}

//	protected void saveSignedIn(String username, JSONObject user){
//		SharedPreferences pref = this.getSharedPreferences(Const.PREFS, Activity.MODE_PRIVATE);
//		SharedPreferences.Editor editor = pref.edit();
//		String usernames = pref.getString("usernames", "");
//		// save and make current user the first order.
//		String[] names = getSignedIns();
//		String ret = username;
//    	for(int i=0; i<names.length; i++){
//    		if(!names[i].equalsIgnoreCase(username)){
//    			ret +=  Const.USERNAME_SPLITOR + names[i];
//    		}
//    	}		
//		//usernames = usernames.replaceAll(username, ""); //it has problem
//		//usernames = username + Const.USERNAME_SPLITOR + usernames;
//		//usernames = usernames.replaceAll(Const.USERNAME_SPLITOR+"+", Const.USERNAME_SPLITOR);
//		//usernames = usernames.replaceAll("^"+Const.USERNAME_SPLITOR, "");
//		//usernames = usernames.replaceAll(Const.USERNAME_SPLITOR+"$", "");
//		editor.putString("usernames", ret);
//		editor.putString(Const.USERNAME_PREFIX+username, user.toString());
//		editor.commit();
//	}
	
	protected void saveSignedIn(String username, String password){
		SharedPreferences pref = this.getSharedPreferences(Const.PREFS, Activity.MODE_PRIVATE);
		SharedPreferences.Editor editor = pref.edit();
		String usernames = pref.getString("usernames", "");
		// save and make current user the first order.
		String[] names = getSignedIns();
		String ret = username;
    	for(int i=0; i<names.length; i++){
    		if(!names[i].equalsIgnoreCase(username)){
    			ret +=  Const.USERNAME_SPLITOR + names[i];
    		}
    	}		
		//usernames = usernames.replaceAll(username, ""); //it has problem
		//usernames = username + Const.USERNAME_SPLITOR + usernames;
		//usernames = usernames.replaceAll(Const.USERNAME_SPLITOR+"+", Const.USERNAME_SPLITOR);
		//usernames = usernames.replaceAll("^"+Const.USERNAME_SPLITOR, "");
		//usernames = usernames.replaceAll(Const.USERNAME_SPLITOR+"$", "");
		editor.putString("usernames", ret);
		if(password != null)
			editor.putString(Const.USERNAME_PREFIX+username, password);
		editor.commit();
	}
	protected String[] getSignedIns(){
		SharedPreferences pref = this.getSharedPreferences(Const.PREFS, Activity.MODE_PRIVATE);
		String usernames = pref.getString("usernames", "");
		if(usernames.equals(""))
			return new String[0]; 
		else
			return usernames.split(Const.USERNAME_SPLITOR);
	}

	protected String getPassword(String username){
		SharedPreferences pref = this.getSharedPreferences(Const.PREFS, Activity.MODE_PRIVATE);
		String password = pref.getString(Const.USERNAME_PREFIX+username, "");
		return password;
	}

//	protected JSONObject getUser(String username){
//		SharedPreferences pref = this.getSharedPreferences(Const.PREFS, Activity.MODE_PRIVATE);
//		String str = pref.getString(Const.USERNAME_PREFIX+username, "");
//		JSONObject user = null;
//		try {
//			user = new JSONObject(str);
//		} catch (JSONException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} 
//		return user;
//	}

	protected void clearSignedIns(){
		SharedPreferences pref = this.getSharedPreferences(Const.PREFS, Activity.MODE_PRIVATE);
		SharedPreferences.Editor editor = pref.edit();
		try {
			editor.putString("usernames", currentUser == null ? "" : currentUser.getString("username"));
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		editor.commit();
		this.startActivity(getIntent());
	}

	public String getAppUrl(String appId, String displayName){
		return Const.HTTP_PREFIX + "/apps/" + appId;
	}

	protected String getLoginParameters(){
		return Utils.getLoginParameters(this);	
	}
	
	protected String getClientParameters(){
		return Utils.getClientParameters(this);
	}

	public String getSiteUrl(){
    	return Const.HTTP_PREFIX + "/?" + getLoginParameters() + " &" + System.currentTimeMillis();
    }
	
	public void startSite(){
		startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(getSiteUrl())));
	}

	protected Uri getPicUri(Intent it){
    	if(it == null) it = new Intent();
        Uri picUri = null;
        picUri = it.getData();
        if(picUri == null){
        	Bundle bd = it.getExtras();
        	if(bd != null){
        		Object o = bd.get(Intent.EXTRA_STREAM);
        		picUri = (Uri)o;
        	}
        }
        return picUri;
	}
	public boolean createTempDirectory() {
	    File tempdir = new File(Const.TMP_FOLDER);
	    if (!tempdir.exists()) {
	        if (!tempdir.mkdirs()) {
	        	Utils.showToast(this, getString(R.string.err_no_SD));
	            //Log.d("Cloud", "Cannot create directory: " + Const.TMP_FOLDER);
	            return false;
	        }
	    }
	    return true;
	}

	
	/**
	   * 检测网络是否存在
	   */
	public boolean HttpTest()
	{ 
		boolean ret = true;
	    if( !Utils.isNetworkAvailable(this) ){
	    	ret = false;
	      AlertDialog.Builder builders = new AlertDialog.Builder(this);
	      builders.setTitle(getString(R.string.err_no_network_title));
	      builders.setMessage(getString(R.string.err_no_network_message));
	      //LayoutInflater _inflater = LayoutInflater.from(mActivity);
	      //View convertView = _inflater.inflate(R.layout.error,null);
	      //builders.setView(convertView);
	      builders.setPositiveButton(getString(R.string.settings),  new DialogInterface.OnClickListener(){
		      public void onClick(DialogInterface dialog, int which)
		      {
		    	  startActivity(new Intent(Settings.ACTION_WIRELESS_SETTINGS)); 		      
		      }       
	      });
	      builders.setNegativeButton(getString(R.string.cancel), null);
	      builders.show();
	    }
	    return ret;
	}	
	public boolean HttpPrompt()
	{ 
		boolean ret = true;
	    if( !Utils.isNetworkAvailable(this) ){
	    	ret = false;
	    	Utils.showToast(this, getString(R.string.err_no_network_message));
	    }
	    return ret;
	}	
	
	class OnClickListener_imgLogo implements ImageView.OnClickListener {
    	
		@Override
		public void onClick(View v) {
//    		startSite();         
//	    	if(currentUser != null){
//	    		SharedPreferences pref = getSharedPreferences(Const.PREFS, Activity.MODE_PRIVATE);
//	    		SharedPreferences.Editor editor = pref.edit();
//	    		// save and make current user the first order.
//	    		editor.putBoolean("tappedLogo", true);
//	    		editor.commit();
//	    	}
			startActivity(new Intent(CloudActivity.this, Main.class));
		}
    }

    protected class OnClickListener_btnSnap implements Button.OnClickListener {
		@Override
		public void onClick(View v) {
//			startActivity(new Intent(CloudActivity.this, Shooter.class));
		}
		
	}

    @Override
    protected Dialog onCreateDialog(int id) {
        ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage(getString(R.string.loading));
        dialog.setIndeterminate(true);
        //dialog.setCancelable(false);
        //dialog.setCanceledOnTouchOutside(true);
        return dialog;
    }

    public void openMe(){
    	JSONObject user = Utils.getCurrentUser(this);
		Intent i = new Intent(this, MyApps.class);
		try {
			i.setData(Uri.parse(user.getString("id")));
			i.putExtra(USER, user.toString());
			startActivity(i);
		} catch (JSONException e) {
			e.printStackTrace();
		}
    }

    
//    protected void openApp(JSONObject app){
//		Intent i = new Intent(this, OpenApp.class);
//		i.putExtra(APP, app.toString());
//		startActivity(i);
//	}

    protected void openApp(String packageName){
		Intent i = new Intent(this, OpenApp.class);
		i.setData(Uri.parse(packageName));
		startActivity(i);
	}

    protected void share(String name, String cloudId){
		try {
			String content = name + " " + getAppUrl(cloudId, name);
	        Intent intent = new Intent(Intent.ACTION_SEND);
	        intent.setType("text/plain");
	        intent.putExtra(Intent.EXTRA_TEXT, content);
	        intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.share_via_email_title));
	    	Intent i = Intent.createChooser(intent, getString(R.string.tell_friends_via));
	    	startActivity(i);
		} catch (Exception e) {
			e.printStackTrace();
		}
    }

    protected class ImageMessageBroadcastReceiver extends BroadcastReceiver {
    	//private Feeds mUI;

    	//public ImageMessageBroadcastReceiver(Feeds ui){
    	//	mUI = ui;
    	//}
        @Override
        public void onReceive(Context context, Intent intent) {
    		Log.d("Cloud", "Cloud ImageMessageBroadcastReceiver onReceive: " + intent.toString());
            if(intent.getAction().equals(IMAGE_LOADED)){
            	onImageLoaded();
            }
        }
    }
	
    protected class RestartBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
    		Log.d("Cloud", "Cloud RestartBroadcastReceiver onReceive: " + intent.toString());
            if(intent.getAction().equals(RESTART)){
            	finish();
            	startActivity(CloudActivity.this.getIntent());
            }
        }
    }

    // override it in subclass
	protected void onImageLoaded(){}
	
	public void confirm(String title, DialogInterface.OnClickListener listener)
	{ 
	      AlertDialog.Builder builders = new AlertDialog.Builder(this);
	      builders.setTitle(title);
	      builders.setMessage(getString(R.string.confirm_prompt));
	      builders.setPositiveButton(getString(R.string.ok),  listener);
	      builders.setNegativeButton(getString(R.string.cancel), null);
	      builders.show();
    }
	
//	public void setNoticeMenu(){
//        if(Checker.isNoticeOn(this)){
//        	mMenu.findItem(R.id.notice_on).setVisible(false);
//       		mMenu.findItem(R.id.notice_off).setVisible(true);
//        }else{
//        	mMenu.findItem(R.id.notice_on).setVisible(true);
//       		mMenu.findItem(R.id.notice_off).setVisible(false);
//        }
//	}
	

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        mMenu = menu;
        // Inflate the currently selected menu XML resource.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.common, menu);
        //setNoticeMenu();        		
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	String prompt;
        switch (item.getItemId()) {
//	        case R.id.settings:
//				startActivity(new Intent(CloudActivity.this, CloudSettings.class));
//	            return true;
            case R.id.about:
    			startActivity(new Intent(CloudActivity.this, About.class));
                return true;
            default:
                return false;
        }
    }


    protected void notifyNewVersion(){
    	try{
			String s = Utils.getPrefString(CloudActivity.this, "version_changes", null);
			JSONArray changes = new JSONArray(s);
			Log.d("Cloud", "Cloud CloudActitivy get " + changes.length() + " new version");
			if(changes.length() > 0){
				int newVersion = changes.getJSONObject(0).getInt("code");
				String versionName = ""+(newVersion/1000.0);
				String text = String.format(getString(R.string.not_up2date), versionName);
				NotificationManager nm = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
				Notification notification = Utils.getDefaultNotification(text);
				Intent i = new Intent(this, About.class);
				PendingIntent launchIntent = PendingIntent.getActivity(this, 0, i, 0);
				notification.setLatestEventInfo(this, getString(R.string.cloud_update), text, launchIntent);
				
				nm.notify(100, notification);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
    
}
