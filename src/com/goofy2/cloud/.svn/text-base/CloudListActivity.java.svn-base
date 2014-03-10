package com.goofy2.cloud;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.apache.http.client.entity.UrlEncodedFormEntity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.goofy2.*;
import com.goofy2.cloud.CloudActivity.OnClickListener_btnSnap;
import com.goofy2.cloud.utils.JSONUtils;
import com.goofy2.cloud.utils.ParamRunnable;
import com.goofy2.cloud.R;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.app.AlertDialog.Builder;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.webkit.WebView;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ResourceCursorAdapter;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.ToggleButton;

public abstract class CloudListActivity extends WithHeaderActivity {
	protected ListView mList;
	protected CloudBaseAdapter mAdapter;
	protected JSONArray mListData = new JSONArray();
	protected String mData;
	protected HashMap<String, Integer> mLoadingImages = new HashMap<String, Integer>();
	private View viewFooter;
	private TextView txtMore;
	private boolean mIsScrolling = false;
	private boolean mIsDirty = false;
	private View mHeader;

    @Override
    public void onCreate(Bundle savedInstanceState) {
		Log.d("Cloud", "Cloud CloudListActivity onCreate");
        super.onCreate(savedInstanceState);
        setContent();

        mList=(ListView)findViewById(R.id.listFeeds);
		//registerForContextMenu(mList); 
		//viewFooter = LayoutInflater.from(this).inflate(R.layout.list_footer, null);
		//mList.addFooterView(viewFooter);
        //loading = (View) viewFooter.findViewById(R.id.loading);
        //txtMore = (TextView) viewFooter.findViewById(R.id.txtMore);
        //txtMore.setOnClickListener(new OnClickListener_txtMore());
        
        mHeader = getHeader();
        if(mHeader != null) mList.addHeaderView(mHeader);
		mAdapter = getAdapter();
		mList.setAdapter(mAdapter);
		//showDialog(0);
		//loadList(getUrl());
		if(getUrl() != null) refresh();
		
		mList.setOnScrollListener(new OnScrollListener(){
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				//Log.d("Cloud", "Cloud CloudListActivity onScrollStateChanged: " + scrollState);
				if(scrollState == OnScrollListener.SCROLL_STATE_IDLE){ 
					mIsScrolling = false;
					if(mIsDirty) updateImage();
					if(view.getCount() > 0 && view.getLastVisiblePosition() > (view.getCount()-2)){
						Log.d("Cloud", "Cloud CloudListActivity try load");
						loadingMore();
					}
				}else{
					mIsScrolling = true;
				}
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				onListScroll(view, firstVisibleItem, visibleItemCount,  totalItemCount);
			}
		});

		mList.setOnItemClickListener(new AdapterView.OnItemClickListener() {  
            @Override  
            public void onItemClick(AdapterView<?> arg0, final View arg1, final int position, long arg3) {
            	if(mHeader!=null && position == 0){ // header
            		onClickHeader();
            	}else if(position > mListData.length()){ // footer
            		loadingMore();
            	}else{
	 				try {
	 					onClickItem(position);
					} catch (Exception e) {
						e.printStackTrace();
					}
            	}
             }  
		});      
		
		mList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {  
            @Override  
            public boolean onItemLongClick(AdapterView<?> arg0, final View arg1, final int position, long arg3) {
 				try {
 	            	onLongClickItem(position);
				} catch (Exception e) {
					e.printStackTrace();
				}
				return true;
            }
        });
	}

    protected void onListScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		
	}

	abstract protected void setContent();
    
	protected void loadingMore(){
		String lastId = null;
		if(loading.getVisibility() == View.GONE){ 
			try {
				if(mListData.length() > 0)
					lastId = mListData.getJSONObject(mListData.length()-1).getString(getIdName());
				loading.setVisibility(View.VISIBLE);
				txtMore.setVisibility(View.GONE);
				loadList(getUrl(), lastId);
			} catch (JSONException e) {
			}
		}
    }
    
    protected void loadedMore(){
		//viewFooter.setVisibility(View.VISIBLE);
		loading.setVisibility(View.GONE);
		txtMore.setVisibility(View.INVISIBLE);
    }
    
    protected void loadList(final String url, final String lastId){
		Log.d("Cloud", "Cloud CloudListActivity loadList: " + url);
    	AsyncTask<Void, Void, Long> loadTask = new AsyncTask<Void, Void, Long>() {
			private String mErr = null;
			protected Long doInBackground(Void... params) {
				mErr = loadStream(url, lastId);
				return null;
			}
            protected void onPostExecute(Long result) {
		    	removeDialog(0);
		    	if(mErr == null){
		    		mAdapter.notifyDataSetChanged();
					loadedMore();
					//FeedHelper.loadUpdateImages(CloudListActivity.this, mListData, mLoadingImages);
					//loadListImages(mListData, mLoadingImages);
					loadListImagesEx(mListData);
		    	}
		    	else{
		    		//Utils.alert(CloudListActivity.this, mErr);
					loadedMore();
		    		txtMore.setVisibility(View.VISIBLE);
		    	}
            }
        };
        loadTask.execute();
    }
    
	synchronized protected String loadStream(String url, String lastId) {
		Log.d("Cloud", "Cloud CloudListActivity loadStream: " + lastId);
    	if(lastId == null){
    		mLoadingImages.clear();
    		mListData = new JSONArray();
    	}else{
    		url += "&max_id=" + lastId;
    	}
		String err = null;
		String strResult = null;
		try{
			HttpGet httpReq = new HttpGet(url);
			HttpParams httpParameters = new BasicHttpParams();
			HttpConnectionParams.setConnectionTimeout(httpParameters, Const.HTTP_TIMEOUT);
			httpReq.setParams(httpParameters);
			HttpResponse httpResp = new DefaultHttpClient().execute(httpReq);
			strResult = EntityUtils.toString(httpResp.getEntity());
			int code = httpResp.getStatusLine().getStatusCode(); 
			if( code == 200){
				Log.d("Cloud", "Cloud CloudListActivity loadStream ok: " + lastId);
				mData = strResult;
				mListData = JSONUtils.appendArray(mListData, getListArray(strResult));
				mAdapter.setData(mListData);
			}else{
				onHttpError(strResult, code);
			}
		}catch (Exception e){
//	    	Utils.alertTitle(this, getString(R.string.err_no_network_title), e.getMessage());
			err = e.getMessage();
			Log.e("Cloud", "Cloud CloudListActivity loadStream err: " + err);
		}
		return err;
	}
    
    abstract protected String getUrl();
    
    protected String onHttpError(String strResult, int code) throws JSONException{
		JSONObject json = new JSONObject(strResult);
		String err = json.getString("error_message");
		Log.d("Cloud", "Cloud CloudListActivity loadStream err: " + err);
		return err;
    }
    
    @Override
    public void onDestroy(){
		Log.d("Cloud", "Cloud CloudListActivity onDestroy");
    	super.onDestroy();
    }

    @Override 
    public void onStart(){
		Log.d("Cloud", "Cloud CloudListActivity onStart");
    	super.onStart();
    	//redirectAnonymous(null);    	
    }
    
    @Override 
    public void onResume(){
		Log.d("Cloud", "Cloud CloudListActivity onResume");
    	super.onResume();
    }
    @Override 
    public void onPause(){
		Log.d("Cloud", "Cloud CloudListActivity onPause");
    	super.onPause();
    }
    @Override 
    public void onStop(){
		Log.d("Cloud", "Cloud CloudListActivity onStop");
    	super.onStop();
    }
    @Override 
    public void onRestart(){
		Log.d("Cloud", "Cloud CloudListActivity onRestart");
    	super.onRestart();
    }

    public void loadListImagesEx(final JSONArray jsonList){
    	final ExecutorService threadPool = Executors.newFixedThreadPool(Const.MULITI_DOWNLOADING);
        threadPool.execute(new Runnable(){
        	public void run() {
				for(int i=0; i < jsonList.length() ; i++){
					JSONObject item;
					String imageUrl = null;
					try {
						item = jsonList.getJSONObject(i);
						imageUrl = getImageUrl(item);
						ParamRunnable pr = new ParamRunnable(){
				        	public void run() {
								if(AppHelper.saveImageToFile(CloudListActivity.this, (String) param, Const.HTTP_TIMEOUT_LONG, null))
									sendBroadcast(new Intent(CloudActivity.IMAGE_LOADED));
				        	}
						};
						pr.param = imageUrl;
						threadPool.execute(pr);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
        	}
        });  
    }
    
//	//load images synch in order
//    public void loadListImages(final JSONArray jsonList, final HashMap<String, Integer> loadingImages){
//		AsyncTask<Void, Integer, Long> loadTask = new AsyncTask<Void, Integer, Long>() {
//			protected Long doInBackground(Void... params) {
//				long ret = 0;
//				for(int i=0; i < jsonList.length() ; i++){
//					JSONObject item;
//					String imageUrl = null;
//					try {
//						item = jsonList.getJSONObject(i);
//						imageUrl = getImageUrl(item);
//						if(FeedHelper.saveImageToFile(CloudListActivity.this, imageUrl, Const.HTTP_TIMEOUT_LONG, loadingImages))
//							publishProgress(1);
//					} catch (Exception e) {
//						e.printStackTrace();
//					}
//				}
//				return ret;
//			}
//			protected void onProgressUpdate(Integer... progress) {
//            	//if(adapter != null) adapter.notifyDataSetChanged();
//				sendBroadcast(new Intent(CloudActivity.IMAGE_LOADED));
//			}
//		};
//		loadTask.execute();
//	}
    
    protected abstract String getImageUrl(JSONObject item) throws JSONException;

	protected void refresh(){
		showDialog(0);
		refreshWithoutLoading();
	}
	
	protected void refreshWithoutLoading(){
		loadList(getUrl(), null);
		mList.setSelection(0);
	}

	@Override
	protected void onImageLoaded(){
		if(!mIsScrolling) updateImage();
		else mIsDirty = true;
			
	}

	private void updateImage(){
		mAdapter.notifyDataSetChanged();
		mIsDirty = false;
	}
	
	protected String getIdName(){
		return "id";
	}

	abstract protected JSONArray getListArray(String result) throws JSONException;

	abstract protected void onClickHeader();
	
	abstract protected CloudBaseAdapter getAdapter();

    protected abstract void onClickItem(int position) throws JSONException;

    protected abstract void onLongClickItem(int position) throws JSONException;

    protected abstract View getHeader();

}
