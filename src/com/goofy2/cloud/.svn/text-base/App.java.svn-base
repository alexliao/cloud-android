package com.goofy2.cloud;
import java.io.File;
import java.io.FileOutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.json.JSONException;
import org.json.JSONObject;

import com.goofy2.cloud.utils.Base64;

import android.R.integer;
import android.content.ContentValues;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;


public class App {
	public static final String PACKAGE = "package";
	public static final String NAME = "name";
	public static final String PATH = "path";
	public static final String VERSION_CODE = "version_code";
	public static final String VERSION_NAME = "version_name";
	public static final String ICON = "icon";
	public static final String SIGNATURE = "signature";
	public static final String CLOUD_ID = "id";
//	public static final String CLOUD_APK = "apk";
	public static final String IS_CLOUDED = "is_clouded";

	private JSONObject mJson = new JSONObject();
	
	public boolean isInCloud(){
		return !mJson.optString(CLOUD_ID, "null").equals("null");
	}
	
	public String getName(){
		return mJson.optString(NAME);
	}
	public String getPackage(){
		return mJson.optString(PACKAGE);
	}
	public int getVersionCode(){
		return mJson.optInt(VERSION_CODE, -1);
	}
	public String getVersionName(){
		return mJson.optString(VERSION_NAME);
	}
	public String getApkPath(){
		return mJson.optString(PATH);
	}
	public String getIconPath(){
		return Const.TMP_FOLDER+"/"+getIconName();
	}
	public String getIconName(){
		return mJson.optString(ICON);
	}
	public String getSignature(){
		return mJson.optString(SIGNATURE);
	}
	public String getCloudId(){
		return mJson.optString(CLOUD_ID, null);
	}
//	public String getCloudApk(){
//		return mJson.optString(CLOUD_APK);
//	}
	
	public long getSize(){
		File f = new File(getApkPath());
		return f.length();
	}

	public App(){
	}
	public App(JSONObject data){
		mJson = data;
	}
	public App(PackageManager pm, PackageInfo info){
		setBy(pm, info);
	}

	public JSONObject getJSON(){
		return mJson;
	}
	
	public void setBy(PackageManager pm, PackageInfo info){
		try {
			mJson.put(PACKAGE, info.packageName);
			mJson.put(VERSION_CODE, info.versionCode);
			mJson.put(VERSION_NAME, info.versionName);
			mJson.put(PATH, info.applicationInfo.sourceDir);
			mJson.put(NAME, info.applicationInfo.loadLabel(pm));
			mJson.put(SIGNATURE, getShortSignature(info));
			Log.d("Cloud", "Cloud "+getName()+" - sign: "+getSignature());
			Drawable bd = info.applicationInfo.loadIcon(pm);
			if(bd != null){
				Bitmap bm = ((BitmapDrawable)bd).getBitmap();
				File f = new File(Const.TMP_FOLDER+"/"+getIconFileName(info));
		        FileOutputStream out = new FileOutputStream(f);   
		        bm.compress(Bitmap.CompressFormat.PNG, 100, out);
		        mJson.put(ICON, f.getName());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private String getIconFileName(PackageInfo info){
		return info.packageName + ".png";
	}

	//generate short string by first signature or null if no signature or no MD5 algorithm in device.
	private String getShortSignature(PackageInfo info){
		Signature[] signs = info.signatures;
//		Log.d("Cloud", "Cloud "+getName()+" - sign: "+signs[0].toCharsString());
//		Log.d("Cloud", "Cloud sign hash: "+signs[0].toCharsString().hashCode());
		String ret = null;
		if(signs.length > 0){
			byte[] data = signs[0].toByteArray();
			MessageDigest md5;
			try {
				md5 = MessageDigest.getInstance("MD5");
				md5.update(data);
				ret = new String(Base64.encode(md5.digest()));
			} catch (NoSuchAlgorithmException e) {
				e.printStackTrace();
			}  
		}
		return ret;
	}
	
}
