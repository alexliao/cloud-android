package com.goofy2.cloud;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import com.goofy2.*;
import com.goofy2.cloud.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

public class WithHeaderActivity extends CloudActivity {
	protected TextView txtTitle;
	protected ImageView imgLogo;
	protected View loading;

    @Override
    public void onStart(){
    	super.onStart();
    	prepareUserBar();
    }

    protected void prepareUserBar(){
	    txtTitle = (TextView)this.findViewById(R.id.txtTitle);
		imgLogo = (ImageView) this.findViewById(R.id.imgLogo);
		imgLogo.setOnClickListener(new OnClickListener_imgLogo());
		loading = this.findViewById(R.id.loading);
    }
    
}
