package com.goofy2.cloud;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.goofy2.cloud.Utils;
import com.goofy2.cloud.CloudActivity.ImageMessageBroadcastReceiver;
import com.goofy2.cloud.CloudActivity.OnClickListener_btnSnap;
import com.goofy2.cloud.Main.OnClickListener_btnStart;
import com.goofy2.cloud.utils.FormFile;
import com.goofy2.cloud.utils.JSONUtils;
import com.goofy2.cloud.utils.ParamRunnable;
import com.goofy2.cloud.utils.UploadImage;
import com.goofy2.cloud.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.AlertDialog.Builder;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

public class OpenApp extends WithHeaderActivity {
	private String mId;
	private App mApp;
	private App mCloudApp;
	private View viewUnknown;
	private View viewUpload;
	private View viewUploading;
	private View viewShare;
	protected ListView listComments;
	private Button btnRetry;
	private Button btnUpload;
	private Button btnBack;
	private Button btnTell;
	private ProgressBar progressBar;
	private TextView txtSizeSent;
	
	protected ProgressBroadcastReceiver mProgressReceiver = new ProgressBroadcastReceiver();
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	Log.d("Cloud", "Cloud OpenApp onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.app);
		
        viewUnknown = this.findViewById(R.id.viewUnknown);
        viewUpload = this.findViewById(R.id.viewUpload);
        viewUploading = this.findViewById(R.id.viewUploading);
        viewShare = this.findViewById(R.id.viewShare);
        btnRetry = (Button) this.findViewById(R.id.btnRetry);
        btnRetry.setOnClickListener(new OnClickListener_btnRetry());
        btnUpload = (Button) this.findViewById(R.id.btnUpload);
        btnUpload.setOnClickListener(new OnClickListener_btnUpload());
        btnBack = (Button) this.findViewById(R.id.btnBack);
        btnBack.setOnClickListener(new OnClickListener_btnBack());
        btnTell = (Button) this.findViewById(R.id.btnTell);
        btnTell.setOnClickListener(new OnClickListener_btnTell());
        progressBar = (ProgressBar) this.findViewById(R.id.progressBar);
        txtSizeSent = (TextView) this.findViewById(R.id.txtSizeSent);

		Intent i = getIntent();
        //String strUpdate = i.getStringExtra(APP);
		String packageName = i.getDataString();
        try {
			//mApp = new App(new JSONObject(strUpdate));
			mApp = new AppHelper(this).getApp(packageName); 
	        bind(mApp);
			registerReceiver(mProgressReceiver, new IntentFilter(Const.BROADCAST_UPLOAD_PROGRESS));
	        checkStatus(mApp);
		} catch (Exception e) {
			Log.d("Cloud", "Cloud OpenApp onCreate err: "+e.getMessage());
			e.printStackTrace();
		}
		

    }

    @Override
    public void onDestroy(){
    	Log.d("Cloud", "Cloud OpenApp onDestroy");
    	try{
    		unregisterReceiver(mProgressReceiver);
    	}catch (Exception e){
    		e.printStackTrace();
    	}
    	super.onDestroy();
    }
    
    @Override 
    public void onStart(){
    	Log.d("Cloud", "Cloud OpenApp onStart");
    	Utils.cancelNotify(this, mApp);
    	super.onStart();
    }

    @Override 
    public void onStop(){
    	Log.d("Cloud", "Cloud OpenApp onStop");
    	super.onStop();
    }

    protected void updateStatus(String cloudId){
		try {
			mApp.getJSON().put(App.CLOUD_ID, cloudId);
			Intent intent = new Intent(Const.BROADCAST_CACHE_APPS_PROGRESS);
			intent.putExtra(Const.KEY_FINISHED, true);
			sendBroadcast(intent);
		} catch (JSONException e) {
			e.printStackTrace();
		}
    }
    private void checkStatus(final App app){
    	AsyncTask<Void, Void, Long> loadTask = new AsyncTask<Void, Void, Long>() {
			private String mErr = null;
			private JSONObject mRet = null;
			protected void onPreExecute() {
				showDialog(0);
			}
			protected Long doInBackground(Void... params) {
				try {
					mRet = Utils.getAppStatus(OpenApp.this, app);
				} catch (Exception e) {
					mErr = e.getMessage();
				}
				return null;
			}
            protected void onPostExecute(Long result) {
		    	removeDialog(0);
		    	if(mRet != null){
					mCloudApp = new App(mRet);
					if(mCloudApp.getVersionCode() >= mApp.getVersionCode()){
						AppHelper helper = new AppHelper(OpenApp.this);
						try {
							mApp.getJSON().put(App.CLOUD_ID, mCloudApp.getCloudId());
							helper.updateOrAddApp(mApp);
							updateStatus(mCloudApp.getCloudId());
							setStep(3);
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}else if(progressBar.getProgress() == 0) setStep(1);
					else setStep(2);
		    	}else setStep(0);
		    	if(mErr != null) Utils.alert(OpenApp.this, mErr);
            }
        };
        
        if(mApp.isInCloud()){
        	setStep(3);
        }else if(progressBar.getProgress()>0){
        	setStep(2);
        }else{
        	if(HttpTest()){
        		loadTask.execute();
        	}else setStep(0);
        }
    }

	protected void setStep(int step) {
//		int step = 1;
//		if(mCloudApp != null && mCloudApp.isInCloud()){
//			if(mCloudApp.getVersionCode() >= mApp.getVersionCode()){
//				step = 3;
//			}
//		}
//		if(progressBar.getProgress()>0) step=2;
//		if(progressBar.getProgress()==100) step=3;
//		
		if(step == 0){
			viewUnknown.setVisibility(View.VISIBLE);
			viewUpload.setVisibility(View.GONE);
			viewUploading.setVisibility(View.GONE);
			viewShare.setVisibility(View.GONE);
		}else if(step == 1){
			btnUpload.setEnabled(true);
			viewUnknown.setVisibility(View.GONE);
			viewUpload.setVisibility(View.VISIBLE);
			viewUploading.setVisibility(View.GONE);
			viewShare.setVisibility(View.GONE);
		}else if (step == 2){
			viewUnknown.setVisibility(View.GONE);
			viewUpload.setVisibility(View.GONE);
			viewUploading.setVisibility(View.VISIBLE);
			viewShare.setVisibility(View.GONE);
		}else if (step == 3){
			mApp = new AppHelper(this).getApp(mApp.getPackage()); // refresh from database for cloud id;
	    	Utils.cancelNotify(this, mApp);
			viewUnknown.setVisibility(View.GONE);
			viewUpload.setVisibility(View.GONE);
			viewUploading.setVisibility(View.GONE);
			viewShare.setVisibility(View.VISIBLE);
		}
		removeDialog(0);
	}


    
    
    private void bind(App app) throws JSONException{
		TextView tv;
		tv = (TextView)findViewById(R.id.txtName);
		tv.setText(app.getName());

		tv = (TextView)findViewById(R.id.txtVersion);
		tv.setText(String.format(getString(R.string.app_version), app.getVersionName()));

		tv = (TextView)findViewById(R.id.txtSize);
		tv.setText(String.format(getString(R.string.app_size), app.getSize()/1048576.0));

		ImageView iv = (ImageView)findViewById(R.id.icon);
		Bitmap bm = null;
		String url = app.getIconName();
		bm = AppHelper.getImageFromFile(this, url); // file store 
		iv.setImageBitmap(bm);
		
		tv = (TextView)findViewById(R.id.txtInCloud);
		tv.setText(String.format(getString(R.string.in_cloud), app.getName()));
//		btnUpload.setVisibility(View.VISIBLE);
    }

	protected void notifyQueued(App app){
		int icon = R.drawable.icon;
		String text = getString(R.string.uploading);
		long when = System.currentTimeMillis();
		String expandedText = getString(R.string.uploading_queued);
		String expandedTitle = app.getName();
		
		Notification noti = new Notification(icon, text, when);
		noti.flags |= Notification.FLAG_ONGOING_EVENT;
		
		Intent i = new Intent(this, OpenApp.class);
		i.setData(Uri.parse(app.getPackage()));
		PendingIntent launchIntent = PendingIntent.getActivity(this, 0, i, 0);
		
		noti.setLatestEventInfo(this, expandedTitle, expandedText, launchIntent);
		Utils.sendNotify(this, noti, app.getPackage().hashCode());
	}
    
    protected class OnClickListener_btnUpload implements Button.OnClickListener {
		@Override
		public void onClick(View v) {
			btnUpload.setEnabled(false);
			setStep(2);
			Intent i = new Intent(OpenApp.this, Uploader.class);
			//i.putExtra(Const.KEY_APP, mApp.getJSON().toString());
			i.setData(Uri.parse(mApp.getPackage()));
			startService(i);
			
		}
		
	}

    protected class OnClickListener_btnTell implements Button.OnClickListener {
		@Override
		public void onClick(View v) {
			if(mApp.isInCloud())
				share(mApp.getName(), mApp.getCloudId());
		}
		
	}

    protected class OnClickListener_btnBack implements Button.OnClickListener {
		@Override
		public void onClick(View v) {
			notifyQueued(mApp);
			Utils.showToast(OpenApp.this, getString(R.string.uploading_queued_prompt));
			finish();
		}
		
	}

    protected class OnClickListener_btnRetry implements Button.OnClickListener {
		@Override
		public void onClick(View v) {
			checkStatus(mApp);
		}
		
	}

    protected class ProgressBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
    		Log.d("Cloud", "Cloud ProgressBroadcastReceiver onReceive: " + intent.toString());
            if(intent.getAction().equals(Const.BROADCAST_UPLOAD_PROGRESS)){
            	String strPackage = intent.getStringExtra(Const.KEY_PACKAGE);
            	if(strPackage.equals(mApp.getPackage())){
            		int percent = intent.getIntExtra(Const.KEY_PERCENT, 0);
            		long sizeSent = intent.getLongExtra(Const.KEY_SIZE_SENT, 0);
            		if(percent > 0 ){
                		progressBar.setIndeterminate(false);
                		progressBar.setProgress(percent);
	            		txtSizeSent.setText(String.format(getString(R.string.size_sent), percent, sizeSent/1024));
            		}
            		if(progressBar.getProgress()>=100) progressBar.setIndeterminate(true);
            		//boolean failed = intent.getBooleanExtra(Const.KEY_FAILED, false);
            		String errMsg = intent.getStringExtra(Const.KEY_FAILED);
//            		if(failed){
//        				Utils.alert(OpenApp.this, getString(R.string.err_upload_failed));
            		if(errMsg != null){
            			Utils.alertTitle(OpenApp.this, getString(R.string.err_upload_failed), errMsg);
                		progressBar.setProgress(0);
                		setStep(1);
            		}else{
	            		boolean finished = intent.getBooleanExtra(Const.KEY_FINISHED, false);
	            		if(finished){
	            			String strApp = intent.getStringExtra(Const.KEY_APP);
	            			JSONObject json;
	            			try {
	            				json = new JSONObject(strApp);
	            				mCloudApp = new App(json);
								updateStatus(mCloudApp.getCloudId());
		            			setStep(3);
	            			} catch (JSONException e) {
	            				e.printStackTrace();
	            			}
	            		}
	            		else setStep(2);
            		}
            	}
            	
            }
        }
    }
}
