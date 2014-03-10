package com.goofy2.cloud;


import java.io.File;
import java.util.Date;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import com.goofy2.*;
import com.goofy2.cloud.utils.DownloadImage;
import com.goofy2.cloud.R;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import android.preference.PreferenceManager;
import android.provider.MediaStore;

public class Main extends CloudActivity {

	private Button btnStart;
	//private ImageView imgLogoBig;
//	private View headNavBar;
	//private View slogon;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        createTempDirectory();        
        
        //header = (View) this.findViewById(R.id.header);
        //slogon = (View) this.findViewById(R.id.slogon);
        btnStart = (Button) this.findViewById(R.id.btnStart);
        btnStart.setOnClickListener(new OnClickListener_btnStart());
		//imgLogoBig = (ImageView) this.findViewById(R.id.imgLogoBig);
		//imgLogoBig.setOnClickListener(new OnClickListener_imgLogo());
		new Thread() {
			public void run(){
				Utils.clearImageCache(Const.IMAGE_CACHE_SIZE);
				
		    	long lastCheckTime = Long.parseLong(Utils.getPrefString(Main.this, "check_version_time", "0"));
		    	if(System.currentTimeMillis() - lastCheckTime > 3600*8*1000){
		    		if(Utils.checkVersion(Main.this))	notifyNewVersion();	
		    	}
		    	//detectStaticHost();
			}
		}.start();
		new Thread() {
			public void run(){
				if(new AppHelper(Main.this).getAppCount()==0) Utils.cacheMyApps(Main.this);
				else Utils.getUserIdInCloud(Main.this);		    	
			}
		}.start();

        View btnSwably = this.findViewById(R.id.btnSwably);
        btnSwably.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.swably_url))).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
			}
		});
        
    
    }
    
    private void detectStaticHost(){
    	long tb, te, t1 = 0, t2 = 0, r=0;
    	File f = new File(Const.TMP_FOLDER + "/logo.png");
    	
    	for(int i=1; i<=3; i++){
	    	tb = System.currentTimeMillis();
	    	try {
				DownloadImage.toFile(Const.HTTP_PREFIX + "/logo.png" , f, Const.HTTP_TIMEOUT_LONG);
			} catch (Exception e) {
				e.printStackTrace();
			}
			te = System.currentTimeMillis();
			t1 = te - tb;
			Log.i("Cloud", "Cloud Main detectStaticHost " + Const.HTTP_PREFIX + " " + t1);
			
	    	tb = System.currentTimeMillis();
	    	try {
				DownloadImage.toFile(Const.STATIC_HTTP_PREFIX_CN + "/logo.png" , f, Const.HTTP_TIMEOUT_LONG);
			} catch (Exception e) {
				e.printStackTrace();
			}
			te = System.currentTimeMillis();
			t2 = te - tb;
			Log.i("Cloud", "Cloud Main detectStaticHost " + Const.STATIC_HTTP_PREFIX_CN + " " + t2);
			
			if(t2 < t1) r += 1;
			else if(t2 > t1) r -= 1;
    	}
    	
		if(r > 0)
			Utils.setPrefString(this, "static_http_prefix", Const.STATIC_HTTP_PREFIX_CN);
		else
			Utils.setPrefString(this, "static_http_prefix", Const.HTTP_PREFIX);
		
		Log.i("Cloud", "Cloud Main detectStaticHost r=" + r + " select: " + Utils.getStaticHttpPrefix(this));
		
    }

//    @Override
//    protected void prepareUserBar(){
//    	super.prepareUserBar();
//    	if(currentUser == null && getSignedIns().length == 0){
//        	header.setVisibility(View.GONE);
//        }else{
//        	header.setVisibility(View.VISIBLE);
//            imgLogo.setVisibility(View.GONE);
//        }
//        if(currentUser == null){
//        	navigationBar.setVisibility(View.GONE);
//		    navBar.setVisibility(View.GONE);
//        	btnSnap.setText(getString(R.string.start));
//        }
//        else{
//        	navigationBar.setVisibility(View.VISIBLE);
//		    navBar.setVisibility(View.VISIBLE);
//        	btnSnap.setText(getString(R.string.snap));
//        }
//    }
    
    @Override
    protected void onNewIntent(Intent intent){
    	super.onNewIntent(intent);
    	autoSignin(intent);
    	//setHeader();
    }

    @Override
    public void onStart(){
    	autoSignin(getIntent());
    	super.onStart();
    	//setHeader();
    }

    private void autoSignin(Intent intent){
    	Uri data = intent.getData();
    	if(data != null){
	    	String scheme = data.getScheme(); // "http" or "grapps"
	    	String host = data.getHost(); // "grapps.com"
	    	List<String> params = data.getPathSegments();
	    	String action = params.get(0); // "snap"
	    	String parameters = data.getQuery();
	    	String id = data.getQueryParameter("id");
	    	String username = data.getQueryParameter("username");
	    	String key = data.getQueryParameter("key");
	    	if(id != null && username != null && key != null){
	    		try {
	    			JSONObject user = new JSONObject();
					user.put("id", id);
		    		user.put("username", username);
		    		user.put("name", username);
		    		user.put("key", key);
		    		setCurrentUser(user);
					saveSignedIn(username, null);
				} catch (JSONException e) {
					Log.e("Cloud", "Cloud Main onStart:" + e.getMessage());
				}
	    	}
	    	intent.setData(null);
    	}
    }
    
//    private void setHeader(){
//    	if(currentUser == null && getSignedIns().length == 0){
//        	//slogon.setVisibility(View.VISIBLE);
//        	header.setVisibility(View.GONE);
//        }else{
//        	//slogon.setVisibility(View.GONE);
//        	header.setVisibility(View.VISIBLE);
//            imgLogo.setVisibility(View.GONE);
//        }
//        if(currentUser == null){
//		    navBar.setVisibility(View.GONE);
//        	btnSnap.setVisibility(View.GONE);
//        	btnStart.setVisibility(View.VISIBLE);
//        }
//        else{
//		    navBar.setVisibility(View.VISIBLE);
//        	btnSnap.setVisibility(View.VISIBLE);
//        	btnStart.setVisibility(View.GONE);
//        }
//    }
    
    @Override
    public void onResume(){
    	super.onResume();
    }
    
    
//    private class OnClickListener_btnHome implements Button.OnClickListener {
//		@Override
//		public void onClick(View v) {
//			//startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(Const.API_PREFIX + "public?" + getLoginParameters() + " &" + System.currentTimeMillis())));
//			startActivity(new Intent(Main.this, PublicStream.class));
//		}
//		
//	}

    protected class OnClickListener_btnStart implements Button.OnClickListener {
		@Override
		public void onClick(View v) {
			//startActivity(new Intent(Main.this, Start.class));
			startActivity(new Intent(Main.this, MyApps.class));
		}
		
	}
   

}