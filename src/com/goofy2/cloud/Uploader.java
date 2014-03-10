package com.goofy2.cloud;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import com.goofy2.cloud.utils.JSONUtils;
import com.goofy2.cloud.utils.ParamRunnable;
import com.goofy2.cloud.utils.UploadImage;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.widget.RemoteViews;

public class Uploader extends IntentService {
	NotificationManager mNotificationManager;
	
	public Uploader() {
		super("Cloud Uploader");
	}

	public Uploader(String name) {
		super(name);
	}

	@Override
	public void onCreate(){
		Log.d("Cloud", "Cloud Uploader - create service");
		super.onCreate();
		mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
	}

	@Override
	public void onDestroy(){
		Log.d("Cloud", "Cloud Uploader - destroy service");
    	super.onDestroy();
	}

	@Override
	protected void onHandleIntent(Intent intent) {
//		String strApp = intent.getStringExtra(Const.KEY_APP);
//		JSONObject json;
//		try {
//			json = new JSONObject(strApp);
//			App app = new App(json);
//			upload(app);
//		} catch (JSONException e) {
//			e.printStackTrace();
//		}
		String packageName = intent.getDataString();
		App app = new AppHelper(this).getApp(packageName);
		if(app.isInCloud())	Utils.cancelNotify(this, app);
		else upload(app);
		
	}

	protected void upload(final App app){
		//make sure the app is not in cloud
		
		
    	final String actionURL = Const.UPLOAD_HTTP_PREFIX+"/apps/upload?"+Utils.getClientParameters(this);
		Map<String, String> map = JSONUtils.toMap(app.getJSON());
		map.put("format", "json");
		
		//FormFile formfile = new FormFile(fIcon.getName(), UploadImage.getBytesFromFile(fIcon), "icon_file", "image/png");
		//FormFile[] files = new FormFile[] { formfile };
		Map<String, File> files = new HashMap<String, File>();
		File fIcon = new File(app.getIconPath());
		File fApk = new File(app.getApkPath());
		files.put("icon_file", fIcon);
		files.put("apk_file", fApk);
		
		final long totalSize = fIcon.length()+fApk.length();
		final Map<String, String> mapParams = map;
		final Map<String, File> filesParams = files;
		final Notification noti = createNotifyUploading(app);
		//Utils.sendNotify(Uploader.this, noti, app.getPackage().hashCode());
		mNotificationManager.notify(app.getPackage().hashCode(), noti);
		//new Thread() {
			//public void run(){
				try {
					String ret = UploadImage.post_3(actionURL, mapParams, filesParams, 1024*10, new ParamRunnable() {
						public void run(){
							Intent i = new Intent(Const.BROADCAST_UPLOAD_PROGRESS);
							long sizeSent = Long.parseLong((String)param);
							int percent = (int)(sizeSent*100/totalSize-1);
		            		Log.v("Cloud", "Cloud progress: " + percent + " of "+app.getPackage());
		            		if(percent > 0 && percent < 100){
		            			if(percent - getLastPercent() >= 4){ // avoid produce lots of notifications, make sure not more than 100 notifications of them
			            			noti.contentView.setTextViewText(R.id.txtPercent, ""+percent+"%");
				            		noti.contentView.setProgressBar(R.id.progressBar, 100, percent, false);
				        			//Utils.sendNotify(Uploader.this, noti, app.getPackage().hashCode());
				        			mNotificationManager.notify(app.getPackage().hashCode(), noti);
				        			Log.d("Cloud", "Cloud notify percent: "+percent);
				        			setLastPercent(percent);
		            			}
		            		}
		            		
							i.putExtra(Const.KEY_SIZE_SENT, sizeSent);
							i.putExtra(Const.KEY_PERCENT, percent);
							i.putExtra(Const.KEY_PACKAGE, app.getPackage());
							sendBroadcast(i);
						}
						
						int lastPercent = 0;
						private int getLastPercent() {
							return lastPercent;
						}
						private void setLastPercent(int percent) {
							lastPercent = percent;
						}
					});
					JSONObject json = new JSONObject(ret);
					App cloudApp = new App(json);
					AppHelper helper = new AppHelper(Uploader.this);
					app.getJSON().put(App.CLOUD_ID, cloudApp.getCloudId());
					helper.updateOrAddApp(app);
					
					notifyFinished(app, getString(R.string.uploaded));
					Intent i = new Intent(Const.BROADCAST_UPLOAD_PROGRESS);
					i.putExtra(Const.KEY_FINISHED, true);
					i.putExtra(Const.KEY_PACKAGE, app.getPackage());
					i.putExtra(Const.KEY_APP, ret);
					sendBroadcast(i);
					// refresh app list
					i = new Intent(Const.BROADCAST_CACHE_APPS_PROGRESS);
					i.putExtra(Const.KEY_FINISHED, true);
					sendBroadcast(i);
				} catch (final Exception e) {
					e.printStackTrace();
					notifyFinished(app, getString(R.string.err_upload_failed));
					Intent i = new Intent(Const.BROADCAST_UPLOAD_PROGRESS);
					i.putExtra(Const.KEY_FAILED, e.toString()+"\n"+(e.getCause() == null ? "": e.getCause().toString())+"\n"+printStack(e));
					i.putExtra(Const.KEY_PACKAGE, app.getPackage());
					sendBroadcast(i);
				}
			//}
		//}.start();
	}
	private String printStack(Exception e){
		String ret = "";
		for(int i=0; i<Math.min(500,e.getStackTrace().length); i++){
			ret += e.getStackTrace()[i].toString()+"\n";
		}
		return ret;
	}

	
	protected Notification createNotifyUploading(App app){
		int icon = R.drawable.icon;
		String text = getString(R.string.uploading);
		long when = System.currentTimeMillis();
		
		Notification noti = new Notification(icon, text, when);
		//noti.flags |= Notification.FLAG_AUTO_CANCEL;
		noti.flags |= Notification.FLAG_ONGOING_EVENT;
		noti.contentView = new RemoteViews(getPackageName(),R.layout.upload_notification);
		noti.contentView.setTextViewText(R.id.txtTitle, app.getName());
		
		Intent i = new Intent(this, OpenApp.class);
		//String str = app.getJSON().toString();
		//i.putExtra(CloudActivity.APP, str);
		i.setData(Uri.parse(app.getPackage()));
		PendingIntent launchIntent = PendingIntent.getActivity(this, 0, i, 0);
		noti.contentIntent = launchIntent;
		return noti;
	}
	
	protected void notifyFinished(App app, String result){
		int icon = R.drawable.icon;
		String text = result;
		long when = System.currentTimeMillis();
		String expandedText = result;
		String expandedTitle = app.getName();
		
		Notification noti = new Notification(icon, text, when);
		//noti.flags |= Notification.FLAG_AUTO_CANCEL;
		
		Intent i = new Intent(this, OpenApp.class);
		i.setData(Uri.parse(app.getPackage()));
		PendingIntent launchIntent = PendingIntent.getActivity(this, 0, i, 0);
		
		noti.setLatestEventInfo(this, expandedTitle, expandedText, launchIntent);
		
		//Utils.sendNotify(Uploader.this, noti, app.getPackage().hashCode());
		mNotificationManager.notify(app.getPackage().hashCode(), noti);
	}

}
